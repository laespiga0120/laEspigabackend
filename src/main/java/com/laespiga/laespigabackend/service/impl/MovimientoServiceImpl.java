package com.laespiga.laespigabackend.service.impl;

import com.laespiga.laespigabackend.dto.MovimientoDto.*;
import com.laespiga.laespigabackend.entity.*;
import com.laespiga.laespigabackend.exception.ResourceNotFoundException;
import com.laespiga.laespigabackend.repository.*;
import com.laespiga.laespigabackend.service.MovimientoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MovimientoServiceImpl implements MovimientoService {

    @Autowired private ProductoRepository productoRepository;
    @Autowired private LoteRepository loteRepository;
    @Autowired private MovimientoInventarioRepository movimientoRepository;
    @Autowired private UsuarioRepository usuarioRepository; // Asumiendo que existe un UsuarioRepository

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

        // Si el motivo es "otro", se usa la observación.
        String motivoFinal = "otro".equalsIgnoreCase(salidaDto.getMotivo()) ?
                salidaDto.getObservacion() : salidaDto.getMotivo();
        movimiento.setMotivo(motivoFinal);

        MovimientoInventario movimientoGuardado = movimientoRepository.save(movimiento);

        List<DetalleMovimiento> detallesAGuardar = new ArrayList<>();

        for (DetalleSalidaDto detalleDto : salidaDto.getDetalles()) {
            Producto producto = productoRepository.findById(detalleDto.getIdProducto())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + detalleDto.getIdProducto()));

            // Lógica para descontar de lotes (FIFO por fecha de vencimiento/registro)
            List<Lote> lotes = loteRepository.findLotesDisponiblesParaSalida(producto.getIdProducto());

            // Validación de stock basada en la suma de lotes disponibles
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

                loteRepository.save(lote); // Actualiza el lote
            }

            // Actualizar stock total del producto
            // producto.setStock(producto.getStock() - detalleDto.getCantidad());
            // productoRepository.save(producto);

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

    private MovimientoHistorialDto convertirAMovimientoHistorialDto(MovimientoInventario mov) {
        MovimientoHistorialDto dto = new MovimientoHistorialDto();
        dto.setIdMovimiento(mov.getIdMovimiento());
        dto.setMotivo(mov.getMotivo());
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

