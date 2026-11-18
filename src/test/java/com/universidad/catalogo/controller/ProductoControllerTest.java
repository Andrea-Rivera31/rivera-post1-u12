package com.universidad.catalogo.controller;

import com.universidad.catalogo.model.Producto;
import com.universidad.catalogo.service.ProductoService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Prueba de la CAPA WEB: solo controladores, convertidores y MockMvc; el
// servicio se reemplaza por un mock. No abre un puerto real ni toca la base.
@WebMvcTest(ProductoController.class)
class ProductoControllerTest {

    @Autowired
    MockMvc mockMvc;

    // @MockitoBean reemplaza a @MockBean, que se eliminó en Spring Boot 4
    @MockitoBean
    ProductoService service;

    @Test
    void get_productoExiste_retorna200() throws Exception {
        Producto p = new Producto();
        p.setId(1L);
        p.setNombre("Laptop");
        p.setPrecio(1500.0);
        when(service.buscarPorId(1L)).thenReturn(p);

        mockMvc.perform(get("/api/productos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Laptop"));
    }

    @Test
    void get_noExiste_retorna404() throws Exception {
        when(service.buscarPorId(99L)).thenThrow(new EntityNotFoundException("no encontrado"));

        mockMvc.perform(get("/api/productos/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void post_conDatosValidos_retorna201() throws Exception {
        Producto guardado = new Producto();
        guardado.setId(1L);
        guardado.setNombre("Mouse");
        guardado.setPrecio(50.0);
        when(service.crear(any())).thenReturn(guardado);

        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre":"Mouse","precio":50.0,"categoria":"ELECTRONICA"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void post_conPrecioInvalido_retorna400() throws Exception {
        when(service.crear(any()))
                .thenThrow(new IllegalArgumentException("El precio debe ser mayor a cero"));

        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre":"Auricular","precio":-5.0}
                                """))
                .andExpect(status().isBadRequest());
    }

    // Test extra: cubre el endpoint de listado por categoría
    @Test
    void get_listarPorCategoria_retorna200() throws Exception {
        Producto p = new Producto();
        p.setNombre("Laptop");
        p.setCategoria("ELECTRONICA");
        when(service.listarPorCategoria("ELECTRONICA")).thenReturn(List.of(p));

        mockMvc.perform(get("/api/productos").param("categoria", "ELECTRONICA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Laptop"));
    }
}