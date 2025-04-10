package com.ivan.test.springboot.app.springboot_test.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ivan.test.springboot.app.springboot_test.models.Cuenta;
import com.ivan.test.springboot.app.springboot_test.models.TransferenciaDto;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.hasSize;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CuentaControllerWebTestClientTest {

    @Autowired
    private WebTestClient client;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Order(1)
    @Test
    void testTransferir() throws JsonProcessingException {

        TransferenciaDto dto = new TransferenciaDto();
        dto.setCuentaOrigen(1L);
        dto.setCuentaDestino(2L);
        dto.setMonto(new BigDecimal(100));
        dto.setBancoID(1L);

        Map<String, Object> response = new HashMap<>();
        response.put("date", LocalDate.now().toString());
        response.put("status", "OK");
        response.put("mensaje", "Transferencia realizada con éxito!");
        response.put("transaccion", dto);

        //when

        client.post().uri("/api/cuentas/transferir")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .consumeWith(respuesta -> {
                    try {
                        String jsonString = new String(Objects.requireNonNull(respuesta.getResponseBody()));
                        JsonNode json = objectMapper.readTree(jsonString);
                        assertEquals("Transferencia realizada con éxito!", json.get("mensaje").asText());
                        assertEquals(dto.getCuentaOrigen(), json.get("transaccion").get("cuentaOrigen").asLong());
                        assertEquals(dto.getCuentaDestino(), json.get("transaccion").get("cuentaDestino").asLong());
                        assertEquals(dto.getMonto().intValue(), json.get("transaccion").get("monto").asInt());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .jsonPath("$.mensaje").isEqualTo("Transferencia realizada con éxito!")
                .jsonPath("$.transaccion.cuentaOrigen").isEqualTo(dto.getCuentaOrigen())
                .jsonPath("$.transaccion.cuentaDestino").isEqualTo(dto.getCuentaDestino())
                .jsonPath("$.transaccion.monto").isEqualTo(100)
                .json(objectMapper.writeValueAsString(response));

    }

    @Order(2)
    @Test
    void testDetalle() {
        client.get().uri("/api/cuentas/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.persona").isEqualTo("Andrés")
                .jsonPath("$.saldo").isEqualTo(900);
    }

    @Order(3)
    @Test
    void testDetalle2() {
        client.get().uri("/api/cuentas/2")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Cuenta.class)
                .consumeWith(respuesta -> {
                    Cuenta cuenta = respuesta.getResponseBody();
                    assert cuenta != null;
                    assertEquals("John", cuenta.getPersona());
                    assertEquals(2100, cuenta.getSaldo().intValue());
                });
    }

    @Test
    @Order(4)
    void testListar() {
        client.get().uri("/api/cuentas").exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$[0].persona").isEqualTo("Andrés")
                .jsonPath("$[0].id").isEqualTo(1)
                .jsonPath("$[0].saldo").isEqualTo(900)
                .jsonPath("$[1].persona").isEqualTo("John")
                .jsonPath("$[1].id").isEqualTo(2)
                .jsonPath("$[1].saldo").isEqualTo(2100)
                .jsonPath("$").isArray()
                .jsonPath("$").value(hasSize(2));
    }

    @Test
    @Order(5)
    void testListar2() {
        client.get().uri("/api/cuentas").exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Cuenta.class)
                .consumeWith(response -> {
                    List<Cuenta> cuentas = response.getResponseBody();
                    assertNotNull(cuentas);
                    assertEquals(2, cuentas.size());
                    assertEquals(1L, cuentas.getFirst().getId());
                    assertEquals("Andrés", cuentas.get(0).getPersona());
                    assertEquals(900, cuentas.get(0).getSaldo().intValue());
                    assertEquals(2L, cuentas.get(1).getId());
                    assertEquals("John", cuentas.get(1).getPersona());
                    assertEquals("2100.00", cuentas.get(1).getSaldo().toPlainString());
                })
                .hasSize(2)
                .value(hasSize(2));
    }

    @Test
    @Order(6)
    void testGuardar() {
        // given
        Cuenta cuenta = new Cuenta(null, "Pepe", new BigDecimal("3000"));

        // when
        client.post().uri("/api/cuentas")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(cuenta)
                .exchange()
                // then
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").isEqualTo(3)
                .jsonPath("$.persona").isEqualTo("Pepe")
                .jsonPath("$.persona").value(is("Pepe"))
                .jsonPath("$.saldo").isEqualTo(3000);
    }

    @Test
    @Order(7)
    void testGuardar2() {
        // given
        Cuenta cuenta = new Cuenta(null, "Pepa", new BigDecimal("3500"));

        // when
        client.post().uri("/api/cuentas")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(cuenta)
                .exchange()
                // then
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Cuenta.class)
                .consumeWith(response -> {
                    Cuenta c = response.getResponseBody();
                    assertNotNull(c);
                    assertEquals(4L, c.getId());
                    assertEquals("Pepa", c.getPersona());
                    assertEquals("3500", c.getSaldo().toPlainString());
                });
    }

    @Test
    @Order(8)
    void testEliminar() {
        client.get().uri("/api/cuentas").exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Cuenta.class)
                .hasSize(4);

        client.delete().uri("/api/cuentas/3")
                .exchange()
                .expectStatus().isNoContent()
                .expectBody().isEmpty();

        client.get().uri("/api/cuentas").exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Cuenta.class)
                .hasSize(3);

        client.get().uri("/api/cuentas/3").exchange()
//                .expectStatus().is5xxServerError();
                .expectStatus().isNotFound()
                .expectBody().isEmpty();
    }
}