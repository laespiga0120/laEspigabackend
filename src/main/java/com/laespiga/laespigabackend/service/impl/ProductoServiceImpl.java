package com.laespiga.laespigabackend.service.impl;

import com.laespiga.laespigabackend.dto.*;
import com.laespiga.laespigabackend.entity.*; // <-- Import completo
import com.laespiga.laespigabackend.exception.*;
import com.laespiga.laespigabackend.repository.CategoriaRepository; // <-- AÑADIDO
import com.laespiga.laespigabackend.repository.LoteRepository;
import com.laespiga.laespigabackend.repository.RepisaRepository;
import com.laespiga.laespigabackend.repository.ProductoRepository;
import com.laespiga.laespigabackend.repository.UbicacionRepository;
import com.laespiga.laespigabackend.repository.UsuarioRepository; // <-- AÑADIDO
import com.laespiga.laespigabackend.service.CategoriaService;
import com.laespiga.laespigabackend.service.ProductoService;
import com.laespiga.laespigabackend.service.UbicacionService;
import com.laespiga.laespigabackend.specification.ProductoSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException; // <-- AÑADIDO
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductoServiceImpl implements ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private UbicacionRepository ubicacionRepository;

    @Autowired
    private LoteRepository loteRepository;

    @Autowired
    private RepisaRepository repisaRepository;

    @Autowired
    private CategoriaService categoriaService;

    @Autowired
    private UbicacionService ubicacionService;

    @Autowired
    private UsuarioRepository usuarioRepository; // <-- AÑADIDO

    @Autowired
    private CategoriaRepository categoriaRepository; // <-- AÑADIDO

    // Formateador para las fechas de LoteDetalleDto y fechaProxima
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");


    @Override
    public List<Producto> listarTodos() {
        return productoRepository.findAll();
    }

    @Override
    public Optional<Producto> obtenerPorId(Integer id) {
        return productoRepository.findById(id);
    }

    @Override
    public Optional<Producto> obtenerPorNombre(String nombre) {
        return productoRepository.findByNombreProducto(nombre);
    }

    @Override
    @Transactional
    public Producto guardar(Producto producto) {
        // Validaciones de ubicación (ya existentes)
        if (producto.getUbicacion() != null && producto.getUbicacion().getIdUbicacion() != null) {
            Ubicacion ubicacion = ubicacionRepository.findById(producto.getUbicacion().getIdUbicacion())
                    .orElseThrow(() -> new ResourceNotFoundException("La ubicación seleccionada no existe."));

            if ("OCUPADA".equals(ubicacion.getEstado())) {
                throw new IllegalStateException("La ubicación seleccionada ya se encuentra ocupada.");
            }

            ubicacion.setEstado("OCUPADA");
            producto.setUbicacion(ubicacion);
        }

        // Guardar producto
        Producto productoGuardado = productoRepository.save(producto);

        // 🔹 Crear lote inicial si hay stock
        if (productoGuardado.getStock() != null && productoGuardado.getStock() > 0) {
            Lote lote = new Lote();
            lote.setProducto(productoGuardado);
            lote.setCodigoLote("L" + productoGuardado.getIdProducto() + "-" + System.currentTimeMillis());
            lote.setCantidad(productoGuardado.getStock());
            lote.setFechaVencimiento(productoGuardado.getFechaVencimiento());
            loteRepository.save(lote);
        }

        return productoGuardado;
    }


    @Override
    public Producto actualizar(Producto producto) {
        // Verifica si el producto existe
        if (!productoRepository.existsById(producto.getIdProducto())) {
            throw new IllegalArgumentException("El producto no existe");
        }
        return productoRepository.save(producto);
    }

    @Override
    public boolean existePorNombre(String nombre) {
        return productoRepository.existsByNombreProducto(nombre);
    }

    // --- NUEVOS MÉTODOS PARA EL PANEL PRINCIPAL (Index.tsx) ---

    @Override
    @Transactional(readOnly = true)
    public List<ProductoInventarioDto> getInventario(String nombre, Integer categoriaId, String repisa, Integer fila, Integer columna, String sortBy, String sortDir) {

        // 1. Crear especificación base
        Specification<Producto> spec = Specification.where(null);

        // 2. Añadir filtros condicionalmente usando las especificaciones
        if (nombre != null && !nombre.trim().isEmpty()) {
            spec = spec.and(ProductoSpecification.hasNombre(nombre));
        }
        if (categoriaId != null) {
            spec = spec.and(ProductoSpecification.hasCategoria(categoriaId));
        }
        if (repisa != null && !repisa.trim().isEmpty()) {
            spec = spec.and(ProductoSpecification.hasRepisa(repisa));
        }
        if (fila != null) {
            spec = spec.and(ProductoSpecification.hasFila(fila));
        }
        if (columna != null) {
            spec = spec.and(ProductoSpecification.hasColumna(columna));
        }

        // 3. Crear Sort (manejo especial para 'ubicacion' y 'categoria')
        Sort sort;
        Sort.Direction direction = Sort.Direction.fromString(sortDir.toUpperCase());

        if ("ubicacion".equalsIgnoreCase(sortBy)) {
            // Ordenamiento complejo por campos de tablas unidas
            sort = Sort.by(
                    new Sort.Order(direction, "ubicacion.repisa.codigo"),
                    new Sort.Order(direction, "ubicacion.fila"),
                    new Sort.Order(direction, "ubicacion.columna")
            );
        } else if ("categoria".equalsIgnoreCase(sortBy)) {
            // Ordenamiento por join de categoria
            sort = Sort.by(new Sort.Order(direction, "categoria.nombreCategoria"));
        } else if ("proveedor".equalsIgnoreCase(sortBy)) { // <-- AÑADIDO
            // Ordenamiento por join de proveedor
            sort = Sort.by(new Sort.Order(direction, "proveedor.nombreProveedor"));
        } else {
            // Ordenamiento estándar (nombre, precio, stock, stockMinimo)
            // Nota: "stock" ordenará por producto.stock, no por el calculado.
            // Si se necesita ordenar por el stock de lotes, la consulta sería mucho más compleja (Criteria API con subquery).
            // Por ahora, ordenamos por el campo 'stock' de la entidad Producto.
            String sortField = sortBy.equals("stock") ? "stock" : "nombreProducto"; // Default a nombreProducto si no coincide
            if (sortBy.equals("precio") || sortBy.equals("stockMinimo")) {
                sortField = sortBy;
            }
            sort = Sort.by(new Sort.Order(direction, sortField));
        }

        // 4. Ejecutar consulta
        List<Producto> productos = productoRepository.findAll(spec, sort);

        // 5. Mapear a DTO
        return productos.stream().map(p -> {

            // 5a. Calcular stock real desde los lotes
            int stockDisp = loteRepository.findLotesDisponiblesParaSalida(p.getIdProducto())
                    .stream()
                    .mapToInt(Lote::getCantidad)
                    .sum();

            // 5b. Formatear ubicación (con chequeos null)
            String ubicacionStr = "N/A";
            if (p.getUbicacion() != null && p.getUbicacion().getRepisa() != null) {
                ubicacionStr = String.format("%s-%d-%d",
                        p.getUbicacion().getRepisa().getCodigo(),
                        p.getUbicacion().getFila(),
                        p.getUbicacion().getColumna());
            }

            // 5c. Obtener nombre de categoría (con chequeo null)
            String categoriaNombre = (p.getCategoria() != null) ? p.getCategoria().getNombreCategoria() : "N/A";

            // 5d. Obtener nombre de proveedor (con chequeo null) // <-- AÑADIDO
            String proveedorNombre = (p.getProveedor() != null) ? p.getProveedor().getNombreProveedor() : "N/A";

            // 5e. Crear DTO
            return new ProductoInventarioDto(
                    p.getIdProducto(),
                    p.getNombreProducto(),
                    categoriaNombre,
                    p.getPrecioCompra(),
                    p.getPrecioVenta(),
                    stockDisp, // ¡Stock real de lotes!
                    p.getStockMinimo(),
                    ubicacionStr,
                    proveedorNombre // <-- AÑADIDO
            );
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ProductoDetalleDto getProductoDetalle(Integer id) {
        // 1. Buscar producto
        Producto p = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + id));

        // 2. Buscar lotes (usamos la query que filtra cantidad > 0 y ordena por fecha)
        List<Lote> lotes = loteRepository.findLotesDisponiblesParaSalida(p.getIdProducto());

        // 3. Calcular stock total de lotes
        int stockDisp = lotes.stream()
                .mapToInt(Lote::getCantidad)
                .sum(); // Ya están filtrados por cantidad > 0

        // 4. Buscar fecha de vencimiento próxima
        String fechaProxima = "N/A"; // Default
        if (Boolean.TRUE.equals(p.getPerecible())) {
            fechaProxima = lotes.stream()
                    .filter(l -> l.getFechaVencimiento() != null) // Solo lotes con fecha
                    .map(Lote::getFechaVencimiento)
                    .findFirst() // Ya están ordenados por la query, tomamos el primero
                    .map(fecha -> fecha.format(DATE_FORMATTER)) // Formato "YYYY-MM-DD"
                    .orElse("N/A"); // Si es perecible pero no hay lotes con fecha
        }

        // 5. Mapear lotes a DTO
        List<LoteDetalleDto> lotesDto = lotes.stream()
                .map(lote -> new LoteDetalleDto(
                        lote.getCodigoLote(),
                        lote.getCantidad(),
                        lote.getFechaVencimiento() != null ? lote.getFechaVencimiento().format(DATE_FORMATTER) : null
                ))
                .collect(Collectors.toList());

        // 6. Formatear ubicación
        String ubicacionStr = "N/A";
        if (p.getUbicacion() != null && p.getUbicacion().getRepisa() != null) {
            ubicacionStr = String.format("%s-%d-%d",
                    p.getUbicacion().getRepisa().getCodigo(),
                    p.getUbicacion().getFila(),
                    p.getUbicacion().getColumna());
        }

        // 7. Obtener Proveedor // <-- AÑADIDO
        String proveedorNombre = (p.getProveedor() != null) ? p.getProveedor().getNombreProveedor() : "N/A";

        // 8. Construir DTO final
        ProductoDetalleDto detalleDto = new ProductoDetalleDto();
        detalleDto.setIdProducto(p.getIdProducto()); // IMPORTANTE
        detalleDto.setNombre(p.getNombreProducto());
        detalleDto.setCategoria(p.getCategoria() != null ? p.getCategoria().getNombreCategoria() : "N/A");
        detalleDto.setIdCategoria(p.getCategoria() != null ? p.getCategoria().getIdCategoria() : null); // AÑADIDO
        detalleDto.setMarca(p.getMarca());
        detalleDto.setDescripcion(p.getDescripcionProducto());
        detalleDto.setPrecioCompra(p.getPrecioCompra());
        detalleDto.setPrecioVenta(p.getPrecioVenta());
        detalleDto.setStockDisponible(stockDisp);
        detalleDto.setStockMinimo(p.getStockMinimo());
        detalleDto.setUbicacion(ubicacionStr);

        // --- NUEVOS CAMPOS DE UBICACIÓN DETALLADA ---
        if (p.getUbicacion() != null) {
            detalleDto.setIdUbicacion(p.getUbicacion().getIdUbicacion());
            detalleDto.setIdRepisa(p.getUbicacion().getRepisa() != null ? p.getUbicacion().getRepisa().getIdRepisa() : null);
            detalleDto.setFila(p.getUbicacion().getFila());
            detalleDto.setColumna(p.getUbicacion().getColumna());
        }

        detalleDto.setPerecible(Boolean.TRUE.equals(p.getPerecible()));
        detalleDto.setFechaVencimientoProxima(fechaProxima);
        detalleDto.setProveedor(proveedorNombre);
        detalleDto.setLotes(lotesDto);

        return detalleDto;
    }

    @Override
    @Transactional(readOnly = true)
    public FiltrosDto getFiltrosInventario() {
        // Llama a los servicios correspondientes para obtener las listas
        List<CategoriaDto> categorias = categoriaService.obtenerTodasConConteo();
        List<RepisaDetalleDto> repisas = ubicacionService.obtenerTodasLasRepisasConDimensiones();

        // Construye y  DTO de filtros
        return new FiltrosDto(categorias, repisas);
    }

    // --- AÑADIDO: MÉTODO DE ACTUALIZACIÓN ---
    @Override
    @Transactional
    public ProductoDetalleDto actualizarProductoParcial(Integer idProducto, ProductoUpdateDto dto, String username) {

        // 1. Verificación de permisos
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        if (!usuario.getIdUsuario().equals(1)) {
            throw new AccessDeniedException("Usuario no autorizado para esta operación.");
        }

        // 2. Buscar Producto
        Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + idProducto));

        Categoria categoria = categoriaRepository.findById(dto.getIdCategoria())
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con id: " + dto.getIdCategoria()));

        // 3. Validar nombre duplicado (si el nombre cambió)
        if (!producto.getNombreProducto().equalsIgnoreCase(dto.getNombreProducto())) {
            if (productoRepository.existsByNombreProducto(dto.getNombreProducto())) {
                throw new IllegalArgumentException("Ya existe otro producto con el nombre: " + dto.getNombreProducto());
            }
        }

        // 4. Actualizar campos básicos
        producto.setNombreProducto(dto.getNombreProducto());
        producto.setDescripcionProducto(dto.getDescripcion());
        producto.setPrecioCompra(dto.getPrecioCompra());
        producto.setPrecioVenta(dto.getPrecioVenta());
        producto.setStockMinimo(dto.getStockMinimo());
        producto.setCategoria(categoria);

        // Actualizar marca
        if (dto.getMarca() != null) {
            producto.setMarca(dto.getMarca());
        }

        // 5. ACTUALIZAR UBICACIÓN CON MANEJO DE UBICACIÓN OCUPADA
        if (dto.getIdRepisa() != null && dto.getFila() != null && dto.getColumna() != null) {
            Repisa repisa = repisaRepository.findById(dto.getIdRepisa())
                    .orElseThrow(() -> new ResourceNotFoundException("Repisa no encontrada con id: " + dto.getIdRepisa()));

            Ubicacion nuevaUbicacion = ubicacionRepository.findByRepisaAndFilaAndColumna(repisa, dto.getFila(), dto.getColumna())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            String.format("Ubicación no encontrada: Repisa %s, Fila %d, Columna %d",
                                    repisa.getCodigo(), dto.getFila(), dto.getColumna())));

            // Verificar si es diferente a la ubicación actual
            boolean esDiferenteUbicacion = producto.getUbicacion() == null ||
                    !producto.getUbicacion().getIdUbicacion().equals(nuevaUbicacion.getIdUbicacion());

            if (esDiferenteUbicacion) {
                // Verificar si la ubicación está ocupada
                if ("OCUPADA".equals(nuevaUbicacion.getEstado())) {

                    // Buscar el producto que ocupa la ubicación
                    Optional<Producto> productoOcupante = productoRepository.findByUbicacion(nuevaUbicacion);

                    // Si no se proporcionó el flag para forzar, lanzar excepción con información
                    if (dto.getForzarCambioUbicacion() == null || !dto.getForzarCambioUbicacion()) {
                        if (productoOcupante.isPresent()) {
                            throw new UbicacionOcupadaException(
                                    "La ubicación está ocupada por otro producto",
                                    productoOcupante.get().getIdProducto(),
                                    productoOcupante.get().getNombreProducto()
                            );
                        } else {
                            throw new IllegalStateException("La ubicación seleccionada ya está ocupada por otro producto.");
                        }
                    }

                    // Si se forzó el cambio, desasignar ubicación del producto anterior
                    if (productoOcupante.isPresent()) {
                        Producto prodAnterior = productoOcupante.get();
                        prodAnterior.setUbicacion(null); // Quitar ubicación del producto anterior
                        productoRepository.save(prodAnterior);
                    }
                }

                // Liberar ubicación anterior del producto actual (si existe)
                if (producto.getUbicacion() != null) {
                    Ubicacion ubicacionAntigua = producto.getUbicacion();
                    ubicacionAntigua.setEstado("LIBRE");
                    ubicacionRepository.save(ubicacionAntigua);
                }

                // Ocupar nueva ubicación
                nuevaUbicacion.setEstado("OCUPADA");
                ubicacionRepository.save(nuevaUbicacion);
                producto.setUbicacion(nuevaUbicacion);
            }
        }

        // 6. Guardar
        productoRepository.save(producto);

        // 7. Devolver los detalles actualizados
        return this.getProductoDetalle(idProducto);
    }
}
