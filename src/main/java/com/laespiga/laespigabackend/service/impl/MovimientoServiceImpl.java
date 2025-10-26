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
        return productoRepository.findByNombreProductoContainingIgnoreCase(nombre).stream()
                .map(p -> new ProductoBusquedaDto(p.getIdProducto(), p.getNombreProducto(), p.getDescripcionProducto(), p.getStock()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void registrarSalida(RegistroSalidaDto salidaDto, Integer idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + idUsuario));

        MovimientoInventario movimiento = new MovimientoInventario();
        movimiento.setTipoMovimiento("SALIDA");
        movimiento.setUsuario(usuario);

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

            producto.setStock(producto.getStock() - detalleDto.getCantidad());
            productoRepository.save(producto);

            DetalleMovimiento detalleMovimiento = new DetalleMovimiento();
            detalleMovimiento.setMovimientoInventario(movimientoGuardado);
            detalleMovimiento.setProducto(producto);
            detalleMovimiento.setCantidad(detalleDto.getCantidad());
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
    public void registrarEntrada(RegistroEntradaDto entradaDto, Integer idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + idUsuario));

        Proveedor proveedor = proveedorRepository.findById(entradaDto.getIdProveedor())
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado con ID: " + entradaDto.getIdProveedor()));

        MovimientoInventario movimiento = new MovimientoInventario();
        movimiento.setTipoMovimiento("ENTRADA");
        movimiento.setUsuario(usuario);
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

        // 3. Actualización de stock
        entradaDto.getDetalles().stream()
                .collect(Collectors.groupingBy(DetalleEntradaDto::getIdProducto,
                        Collectors.summingInt(DetalleEntradaDto::getCantidad)))
                .forEach((idProducto, cantidadTotalEntrante) -> {
                    Producto productoAfectado = productoRepository.findById(idProducto)
                            .orElseThrow(() -> new ResourceNotFoundException("Error interno: Producto no encontrado con ID: " + idProducto));

                    int stockActual = productoAfectado.getStock() != null ? productoAfectado.getStock() : 0;
                    productoAfectado.setStock(stockActual + cantidadTotalEntrante);
                    productoRepository.save(productoAfectado);
                });
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
        // 1. Inicializar la especificación a null para encadenar sin warnings.
        Specification<Producto> spec = null;

        // 2. Aplicar filtros condicionales
        if (categoriaId != null) {
            spec = Specification.where(spec).and(ProductoSpecification.hasCategoria(categoriaId));
        }
        if (marca != null && !marca.trim().isEmpty()) {
            spec = Specification.where(spec).and(ProductoSpecification.hasMarca(marca));
        }
        if (Boolean.TRUE.equals(stockBajoMinimo)) {
            spec = Specification.where(spec).and(ProductoSpecification.isStockBajoMinimo());
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
    // --- METODO AUXILIAR PARA DTO DE HISTORIAL ---
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

        List<DetalleHistorialDto> detalles = mov.getDetalles().stream()
                .map(d -> new DetalleHistorialDto(d.getProducto().getNombreProducto(), d.getCantidad()))
                .collect(Collectors.toList());
        dto.setDetalles(detalles);

        return dto;
    }
}