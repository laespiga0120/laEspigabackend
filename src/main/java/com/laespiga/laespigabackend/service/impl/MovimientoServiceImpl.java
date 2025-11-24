package com.laespiga.laespigabackend.service.impl;

import com.laespiga.laespigabackend.dto.*;
import com.laespiga.laespigabackend.entity.*;
import com.laespiga.laespigabackend.exception.ResourceNotFoundException;
import com.laespiga.laespigabackend.repository.*;
import com.laespiga.laespigabackend.service.MovimientoService;
import com.laespiga.laespigabackend.specification.ProductoSpecification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MovimientoServiceImpl implements MovimientoService {

    @Autowired private ProductoRepository productoRepository;
    @Autowired private LoteRepository loteRepository;
    @Autowired private MovimientoInventarioRepository movimientoRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private ProveedorRepository proveedorRepository;

    // -------------------------------------------------------------------------
    // --- MÉTODOS DE BÚSQUEDA Y MOVIMIENTOS (Entradas y Salidas) ---
    // -------------------------------------------------------------------------

    @Override
    public List<ProductoBusquedaDto> buscarProductosPorNombre(String nombre) {
        List<Producto> productos;

        // 1. Si el nombre está vacío, buscar todos. Si no, filtrar.
        if (nombre == null || nombre.trim().isEmpty()) {
            productos = productoRepository.findAll();
        } else {
            productos = productoRepository.findByNombreProductoContainingIgnoreCase(nombre);
        }

        // 2. Mapear y calcular el stock REAL desde los lotes
        return productos.stream()
                .map(p -> {
                    // Calcular el stock real sumando los lotes disponibles
                    List<Lote> lotes = loteRepository.findLotesDisponiblesParaSalida(p.getIdProducto());
                    int stockReal = lotes.stream().mapToInt(Lote::getCantidad).sum();

                    // 3. Devolver el DTO con el stock real
                    return new ProductoBusquedaDto(
                            p.getIdProducto(),
                            p.getNombreProducto(),
                            p.getDescripcionProducto(),
                            stockReal // <-- Se usa el stock real
                    );
                })
                .collect(Collectors.toList());
    }


    @Override
    @Transactional
    public void registrarSalida(RegistroSalidaDto salidaDto, String username) { // 🔹 CAMBIO
        // Buscamos el usuario real por su username
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + username));

        MovimientoInventario movimiento = new MovimientoInventario();
        movimiento.setTipoMovimiento("SALIDA");
        movimiento.setUsuario(usuario); // Asignamos el usuario autenticado

        String motivoFinal = "otro".equalsIgnoreCase(salidaDto.getMotivo()) ?
                salidaDto.getObservacion() : salidaDto.getMotivo();
        movimiento.setMotivo(motivoFinal);

        MovimientoInventario movimientoGuardado = movimientoRepository.save(movimiento);
        List<DetalleMovimiento> detallesAGuardar = new ArrayList<>();

        for (DetalleSalidaDto detalleDto : salidaDto.getDetalles()) {
            Producto producto = productoRepository.findById(detalleDto.getIdProducto())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + detalleDto.getIdProducto()));

            List<Lote> lotes = loteRepository.findLotesDisponiblesParaSalida(producto.getIdProducto());
            int stockDisponibleEnLotes = lotes.stream().mapToInt(Lote::getCantidad).sum();

            if (stockDisponibleEnLotes < detalleDto.getCantidad()) {
                throw new IllegalStateException("Stock insuficiente en lotes para el producto: " + producto.getNombreProducto() + ". Disponible: " + stockDisponibleEnLotes);
            }

            int cantidadARetirar = detalleDto.getCantidad();
            for (Lote lote : lotes) {
                if (cantidadARetirar <= 0) break;
                int cantidadDelLote = lote.getCantidad();
                int cantidadDescontada = Math.min(cantidadARetirar, cantidadDelLote);
                lote.setCantidad(cantidadDelLote - cantidadDescontada);
                cantidadARetirar -= cantidadDescontada;
                loteRepository.save(lote);
            }

            DetalleMovimiento detalleMovimiento = new DetalleMovimiento();
            detalleMovimiento.setMovimientoInventario(movimientoGuardado);
            detalleMovimiento.setProducto(producto);
            detalleMovimiento.setCantidad(detalleDto.getCantidad());
            detalleMovimiento.setPrecioUnitario(producto.getPrecio_compra() != null ? producto.getPrecio_compra() : 0.0);
            detallesAGuardar.add(detalleMovimiento);
        }
        movimientoGuardado.setDetalles(detallesAGuardar);
    }

    @Override
    public List<MovimientoHistorialDto> obtenerHistorialDeSalidas() {
        return movimientoRepository.findByTipoMovimientoOrderByFechaMovimientoDesc("SALIDA").stream()
                .map(this::convertirAMovimientoHistorialDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductoPorProveedorDto> obtenerProductosPorProveedor(Integer idProveedor) {
        return productoRepository.findByProveedorIdProveedor(idProveedor).stream()
                .map(p -> new ProductoPorProveedorDto(p.getIdProducto(), p.getNombreProducto(), p.getPerecible()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void registrarEntrada(RegistroEntradaDto entradaDto, String username) { // 🔹 CAMBIO
        // Buscamos el usuario real por su username
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + username));

        Proveedor proveedor = proveedorRepository.findById(entradaDto.getIdProveedor())
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado con ID: " + entradaDto.getIdProveedor()));

        MovimientoInventario movimiento = new MovimientoInventario();
        movimiento.setTipoMovimiento("ENTRADA");
        movimiento.setUsuario(usuario); // Asignamos el usuario autenticado
        movimiento.setProveedor(proveedor);
        movimiento.setFechaMovimiento(entradaDto.getFechaEntrada() != null ? entradaDto.getFechaEntrada() : LocalDateTime.now());

        MovimientoInventario movimientoGuardado = movimientoRepository.save(movimiento);

        List<DetalleMovimiento> detallesAGuardar = new ArrayList<>();
        List<Lote> lotesAGuardar = new ArrayList<>();

        for (DetalleEntradaDto detalleDto : entradaDto.getDetalles()) {
            if (detalleDto.getCantidad() == null || detalleDto.getCantidad() <= 0) {
                throw new IllegalArgumentException("La cantidad debe ser un entero positivo.");
            }

            Producto producto = productoRepository.findById(detalleDto.getIdProducto())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + detalleDto.getIdProducto()));

            if (Boolean.TRUE.equals(producto.getPerecible()) && detalleDto.getFechaVencimiento() == null) {
                throw new IllegalArgumentException("El producto '" + producto.getNombreProducto() + "' es perecible y requiere fecha de vencimiento.");
            }

            // 1. CREACIÓN Y GUARDADO DEL LOTE
            Lote nuevoLote = new Lote();
            nuevoLote.setProducto(producto);
            nuevoLote.setCantidad(detalleDto.getCantidad());
            nuevoLote.setFechaVencimiento(detalleDto.getFechaVencimiento());
            nuevoLote.setMovimientoInventario(movimientoGuardado);
            nuevoLote.setCodigoLote("LOTE-" + producto.getIdProducto() + "-" + System.currentTimeMillis());

            Lote loteGuardado = loteRepository.save(nuevoLote);
            lotesAGuardar.add(loteGuardado);

            // 2. CREACIÓN DEL DETALLE DEL MOVIMIENTO
            DetalleMovimiento detalleMovimiento = new DetalleMovimiento();
            detalleMovimiento.setMovimientoInventario(movimientoGuardado);
            detalleMovimiento.setProducto(producto);
            detalleMovimiento.setCantidad(detalleDto.getCantidad());

            detalleMovimiento.setPrecioUnitario(detalleDto.getPrecioUnitario() != null ? detalleDto.getPrecioUnitario() : 0.0);
            detalleMovimiento.setObservacionDetalle(detalleDto.getObservacionDetalle());

            detalleMovimiento.setIdLote(loteGuardado.getIdLote());
            detalleMovimiento.setFechaVencimiento(loteGuardado.getFechaVencimiento());

            detallesAGuardar.add(detalleMovimiento);
        }

        movimientoGuardado.setDetalles(detallesAGuardar);
        movimientoGuardado.setLotes(lotesAGuardar);

        // 3. Actualización de stock y PRECIO
        for (DetalleEntradaDto detalleDto : entradaDto.getDetalles()) {
            Producto productoAfectado = productoRepository.findById(detalleDto.getIdProducto())
                    .orElseThrow(() -> new ResourceNotFoundException("Error interno: Producto no encontrado con ID: " + detalleDto.getIdProducto()));

            // Actualizar Stock
            int stockActual = productoAfectado.getStock() != null ? productoAfectado.getStock() : 0;
            productoAfectado.setStock(stockActual + detalleDto.getCantidad());

            // Actualizar Precio si aplica
            if (detalleDto.getPrecioUnitario() != null && detalleDto.getPrecioUnitario() > 0) {
                productoAfectado.setPrecio_compra(detalleDto.getPrecioUnitario());
            }

            productoRepository.save(productoAfectado);
        }
    }

    @Override
    public List<MovimientoHistorialDto> obtenerHistorialDeEntradas() {
        return movimientoRepository.findByTipoMovimientoOrderByFechaMovimientoDesc("ENTRADA").stream()
                .map(this::convertirAMovimientoHistorialDto)
                .collect(Collectors.toList());
    }

    // -------------------------------------------------------------------------
    // --- LÓGICA DEL REPORTE DE STOCK ACTUAL ---
    // -------------------------------------------------------------------------

    @Override
    public List<ReporteDto> generarReporteStockActual(
            Integer categoriaId,
            String marca,
            Boolean stockBajoMinimo,
            String sortBy,
            String sortDir
    ) {
        // 1. Inicializar la especificación base (no nula)
        Specification<Producto> spec = Specification.where(null);

        // 2. Aplicar filtros condicionales
        if (categoriaId != null) {
            spec = spec.and(ProductoSpecification.hasCategoria(categoriaId));
        }
        if (marca != null && !marca.trim().isEmpty()) {
            spec = spec.and(ProductoSpecification.hasMarca(marca));
        }
        if (Boolean.TRUE.equals(stockBajoMinimo)) {
            spec = spec.and(ProductoSpecification.isStockBajoMinimo());
        }

        // 3. Configurar la ordenación dinámica
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir.toUpperCase()), sortBy);

        // 4. Ejecutar la consulta con filtro y ordenación
        List<Producto> productos = productoRepository.findAll(spec, sort);

        // 5. Mapear los resultados al DTO (CON VERIFICACIÓN DE NULLS)
        return productos.stream()
                .map(p -> {
                    // VERIFICACIÓN SEGURA DE CATEGORIA
                    String nombreCategoria = p.getCategoria() != null ? p.getCategoria().getNombreCategoria() : "Sin Categoría";

                    // VERIFICACIÓN SEGURA DE UBICACION y REPISA
                    String ubicacionStr = "N/A";
                    if (p.getUbicacion() != null) {
                        Ubicacion ubicacion = p.getUbicacion();
                        String codigoRepisa = "S/R";

                        if (ubicacion.getRepisa() != null && ubicacion.getRepisa().getCodigo() != null) {
                            codigoRepisa = ubicacion.getRepisa().getCodigo();
                        }

                        ubicacionStr = String.format("Repisa: %s, Fila: %d, Columna: %d",
                                codigoRepisa,
                                ubicacion.getFila(),
                                ubicacion.getColumna());
                    }

                    return new ReporteDto(
                            p.getIdProducto(),
                            p.getNombreProducto(),
                            nombreCategoria,
                            p.getMarca(),
                            ubicacionStr,
                            p.getStock(),
                            p.getStockMinimo()
                    );
                })
                .collect(Collectors.toList());
    }

    // -------------------------------------------------------------------------
    // --- METODO AUXILIAR PARA DTO DE HISTORIAL (MODIFICADO) ---
    // -------------------------------------------------------------------------

    private MovimientoHistorialDto convertirAMovimientoHistorialDto(MovimientoInventario mov) {
        MovimientoHistorialDto dto = new MovimientoHistorialDto();
        dto.setIdMovimiento(mov.getIdMovimiento());

        if ("ENTRADA".equalsIgnoreCase(mov.getTipoMovimiento()) && mov.getProveedor() != null) {
            dto.setMotivo("Proveedor: " + mov.getProveedor().getNombreProveedor());
        } else {
            dto.setMotivo(mov.getMotivo());
        }

        dto.setFechaMovimiento(mov.getFechaMovimiento());

        Usuario usuario = mov.getUsuario();
        dto.setNombreUsuario(usuario.getNombre() + " " + usuario.getApellido());

        // --- INICIO DE MODIFICACIÓN ---
        // Ahora pasamos el precio unitario al DTO de detalle
        List<DetalleHistorialDto> detalles = mov.getDetalles().stream()
                .map(d -> new DetalleHistorialDto(
                        d.getProducto().getNombreProducto(),
                        d.getCantidad(),
                        d.getPrecioUnitario() // <-- Pasamos el precio
                ))
                .collect(Collectors.toList());
        dto.setDetalles(detalles);

        // Calculamos y asignamos el total general
        Double totalGeneral = detalles.stream()
                .mapToDouble(DetalleHistorialDto::getSubtotal)
                .sum();
        dto.setTotalGeneral(totalGeneral);
        // --- FIN DE MODIFICACIÓN ---

        return dto;
    }

}