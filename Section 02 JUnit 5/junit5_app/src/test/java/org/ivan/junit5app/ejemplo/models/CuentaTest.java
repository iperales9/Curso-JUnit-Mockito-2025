package org.ivan.junit5app.ejemplo.models;

import org.ivan.junit5app.ejemplo.exception.DineroInsuficienteException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

class CuentaTest {

    Cuenta cuenta;

    // Se ejecuta una vez antes de todos los test
    @BeforeEach
    void initMethodTest(TestInfo testInfo, TestReporter testReporter) {
        System.out.println("Iniciando el test...");
        this.cuenta = new Cuenta("Ivan", new BigDecimal("1000.00"));

        System.out.println("Ejecutando " + testInfo.getDisplayName() + " " + testInfo.getTestMethod().orElse(null).getName()
                + " con las etiquetas " + testInfo.getTags() + " y el reporter " + testReporter.getClass().getName());
    }

    // Se ejecuta una vez después de todos los test
    @AfterEach
    void tearTest() {
        System.out.println("Finalizando el test...");
    }

    // Se ejecuta una vez antes de todos los test
    @BeforeAll
    static void initAll() {
        System.out.println("Iniciando todos los test...");
    }

    // Se ejecuta una vez después de todos los test
    @AfterAll
    static void tearAll() {
        System.out.println("Finalizando todos los test...");
    }

    @Tag("cuenta")
    @Nested
    @DisplayName("Test para comprobar el nombre y saldo de la cuenta")
    class CuentaTestNombreSaldo {

        @Test
        @DisplayName("Test para comprobar el nombre de la cuenta")
        void testNameAccount() {
            cuenta.setNombre("Juan");

            assertNotNull(cuenta.getNombre(), () -> "El nombre no puede ser nulo");
            assertEquals("Juan", cuenta.getNombre(), () -> "El nombre de la cuenta no es el esperado");
        }

        @Test
        @DisplayName("Test para comprobar el saldo de la cuenta")
        void testAccountBalance() {
            cuenta.setSaldo(new BigDecimal("2000.00"));

            assertNotNull(cuenta.getSaldo());
            assertEquals(new BigDecimal("2000.00"), cuenta.getSaldo());
            assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        @Test
        @DisplayName("Test para comprobar la referencia de la cuenta")
        void testAccountReference() {
            Cuenta cuenta1 = new Cuenta("Ivan", new BigDecimal("8999.9997"));
            Cuenta cuenta2 = new Cuenta("Ivan", new BigDecimal("8999.9997"));

            assertEquals(cuenta1, cuenta2);
        }

    }

    @Tag("cuenta")
    @Tag("banco")
    @Nested
    class CuentaOperacionesTest {


        @Test
        @DisplayName("Test para comprobar la referencia de la cuenta con el mismo objeto")
        void testDebitoCuenta() {
            cuenta.debito(new BigDecimal("100.00"));

            assertNotNull(cuenta.getSaldo());
            assertEquals(900, cuenta.getSaldo().intValue());
            assertEquals("900.00", cuenta.getSaldo().toString());
        }

        @Test
        @DisplayName("Test para comprobar la referencia de la cuenta con el mismo objeto")
        void testCreditoCuenta() {
            cuenta.credito(new BigDecimal("100.00"));

            assertNotNull(cuenta.getSaldo());
            assertEquals(1100, cuenta.getSaldo().intValue());
            assertEquals("1100.00", cuenta.getSaldo().toString());
        }

        @Test
        @DisplayName("Test para comprobar la transferencia de dinero entre cuentas")
        void testTransferirDinero() {
            Cuenta cuentaOrigen = new Cuenta("Ivan", new BigDecimal("1000.12345"));
            Cuenta cuentaDestino = new Cuenta("Juan", new BigDecimal("500.00"));
            Banco banco = new Banco();
            banco.setNombre("Banco de Prueba");
            banco.transferir(cuentaOrigen, cuentaDestino, new BigDecimal("100.00"));

            assertEquals(900, cuentaOrigen.getSaldo().intValue());
            assertEquals(600, cuentaDestino.getSaldo().intValue());
        }
    }


    @Test
    @Tag("cuenta")
    @Tag("error")
    void testDineroInsuficienteExceptionCuenta() {
        Exception exception = assertThrows(DineroInsuficienteException.class, () -> cuenta.debito(new BigDecimal("2000.00")));
        String actual = exception.getMessage();
        String esperado = "No hay suficiente saldo";
        assertEquals(esperado, actual);
    }


    @Test
    @DisplayName("Test para comprobar la transferencia de dinero entre cuentas con el mismo objeto")
    void testRelacionBancoCuenta() {
        Cuenta cuenta1 = new Cuenta("Ivan", new BigDecimal("1000.12345"));
        Cuenta cuenta2 = new Cuenta("Juan", new BigDecimal("500.00"));

        Banco banco = new Banco();
        banco.setNombre("Banco del Estado");
        banco.addCuenta(cuenta1);
        banco.addCuenta(cuenta2);

        banco.transferir(cuenta1, cuenta2, new BigDecimal("100.00"));

        assertAll(() -> assertEquals(900, cuenta1.getSaldo().intValue(), () -> "El saldo de la cuenta 1 no es el esperado"), () -> assertEquals(600, cuenta2.getSaldo().intValue(), () -> "El saldo de la cuenta 2 no es el esperado"), () -> assertEquals(2, banco.getCuentas().size()), () -> assertEquals("Banco del Estado", cuenta1.getBanco().getNombre()), () -> {
            Optional<Cuenta> cuentaIvan = banco.getCuentas().stream().filter(c -> c.getNombre().equals("Ivan")).findFirst();
            assertTrue(cuentaIvan.isPresent(), "Cuenta with name 'Ivan' should exist");
            assertEquals("Ivan", cuentaIvan.get().getNombre());
        }, () -> assertTrue(banco.getCuentas().stream().anyMatch(c -> c.getNombre().equals("Juan"))));

    }

    @Nested
    class SistemaOperativos {
        @Test
        @EnabledOnOs(OS.WINDOWS)
        void testSoloWindows() {
            System.out.println("Test solo para Windows");
        }

        @Test
        @EnabledOnOs(OS.LINUX)
        void testSoloLinux() {
            System.out.println("Test solo para Linux");
        }

        @Test
        @EnabledOnOs(OS.MAC)
        void testSoloMac() {
            System.out.println("Test solo para Mac");
        }

    }

    @Nested
    class JavaVersionTest {
        @Test
        @EnabledOnJre(JRE.JAVA_15)
        void testSoloJava15() {
            System.out.println("Test solo para Java 15");
        }

        @Test
        @EnabledOnJre(JRE.JAVA_17)
        void testJDK17() {
            System.out.println("Test solo para Java 17");
        }

        @Test
        @EnabledOnJre(JRE.JAVA_21)
        void testSoloJava21() {
            System.out.println("Test solo para Java 21");
        }
    }

    @Nested
    class SistemPropertiesTest {
        @Test
        void imprimirSystemProperties() {
            System.getProperties().forEach((k, v) -> System.out.println(k + ": " + v));
        }

        @Test
        @EnabledIfSystemProperty(named = "os.name", matches = "Windows 10")
        void testJavaVersion() {
        }

        @Test
        @EnabledIfSystemProperty(named = "user.name", matches = "iperales")
        void testUserName() {
            System.out.println("El usuario es ivan");
        }

        @Test
        @EnabledIfSystemProperty(named = "ENV", matches = "env")
        void testDev() {
            System.out.println("El entorno es DEV");
        }


    }

    @Nested
    class VariableAmbienteTest {
        @Test
        void imprimirVariblesAmbiente() {
            System.getenv().forEach((k, v) -> System.out.println(k + ": " + v));
        }

        @Test
        @EnabledIfEnvironmentVariable(named = "JAVA_HOME", matches = ".*jdk-17.0.1*")
        void testJavaHome() {
            System.out.println("La variable de entorno JAVA_HOME está definida");
        }

        @Test
        @EnabledIfEnvironmentVariable(named = "NUMBER_OF_PROCESSORS", matches = "8")
        void testNumeroProcesadores() {
            System.out.println("El número de procesadores es 8");
        }
    }

    @Test
    @DisplayName("Test saldo cuenta dev")
    void testAccountBalanceDev() {
        boolean esDev = "prod".equals(System.getProperty("ENV"));

        // Asumimos que el entorno es DEV si no lo es no se ejecutan los test
        assumeTrue(esDev);
        cuenta.setSaldo(new BigDecimal("2000.00"));
        assertNotNull(cuenta.getSaldo());
        assertEquals(new BigDecimal("2000.00"), cuenta.getSaldo());
        assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    @DisplayName("Test saldo cuenta dev 2")
    void testAccountBalanceDev2() {
        boolean esDev = "dev".equals(System.getProperty("ENV"));

        // Asumimos que el entorno es DEV si no lo es no se ejecutan los test
        cuenta.setSaldo(new BigDecimal("2000.00"));

        assumingThat(esDev, () -> {
            System.out.println("El entorno es DEV");
            assertNotNull(cuenta.getSaldo());
            assertEquals(new BigDecimal("2000.00"), cuenta.getSaldo());
        });

        assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }

    @DisplayName("Probando Debito Cuenta Repetir!")
    @RepeatedTest(value = 5, name = "Repeticion numero {currentRepetition} de {totalRepetitions}")
    void testDebitoCuentaRepetir(RepetitionInfo info) {

        if (info.getCurrentRepetition() == 3) {
            System.out.println("Repeticion numero " + info.getCurrentRepetition() + " de " + info.getTotalRepetitions());
        }

        cuenta.debito(new BigDecimal("100.00"));
        assertNotNull(cuenta.getSaldo());
        assertEquals(900, cuenta.getSaldo().intValue());
        assertEquals("900.00", cuenta.getSaldo().toString());
    }

    @Tag("param")
    @Nested
    class PruebasParametrizadas {
        @ParameterizedTest(name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}")
        @ValueSource(ints = {100, 200, 300, 400, 500, 950})
        void testDebitoCuentaValueSource(int monto) {
            cuenta.debito(new BigDecimal(monto));

            assertNotNull(cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0, "El saldo no puede ser negativo");
        }

        @ParameterizedTest(name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}")
        @CsvSource({"1,100, 2,200, 3,300, 4,400, 5,500,6,950"})
        void testDebitoCuentaCsvSource(String index, String monto) {
            System.out.println(index + " - " + monto);
            cuenta.debito(new BigDecimal(monto));
            assertNotNull(cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0, "El saldo no puede ser negativo");
        }

        @ParameterizedTest(name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}")
        @CsvSource({"200,100,Andres,Andres", "250,200,Pepe,Pepe", "300,300,Maria,Maria", "450,400,Pepa,Pepa", "750,500,Lucas,Lucas", "950,950,Ivan,Ivan"})
        void testDebitoCuentaCsvSource2(String saldo, String monto, String esperado, String actual) {
            System.out.println(saldo + " - " + monto);
            cuenta.setSaldo(new BigDecimal(saldo));
            cuenta.debito(new BigDecimal(monto));
            cuenta.setNombre(actual);

            assertNotNull(cuenta.getNombre());
            assertNotNull(cuenta.getSaldo());
            assertEquals(esperado, actual);
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        @ParameterizedTest(name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}")
        @CsvFileSource(resources = "/data.csv")
        void testDebitoCuentaCsvFileSource(String monto) {
            cuenta.debito(new BigDecimal(monto));
            assertNotNull(cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0, "El saldo no puede ser negativo");
        }

        @ParameterizedTest(name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}")
        @CsvFileSource(resources = "/data2.csv")
        void testDebitoCuentaCsvFileSource2(String saldo, String monto, String esperado, String actual) {
            System.out.println(saldo + " - " + monto);
            cuenta.setSaldo(new BigDecimal(saldo));
            cuenta.debito(new BigDecimal(monto));
            cuenta.setNombre(actual);

            assertNotNull(cuenta.getNombre());
            assertNotNull(cuenta.getSaldo());
            assertEquals(esperado, actual);
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

    }

    @Tag("param")
    @ParameterizedTest(name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}")
    @MethodSource("montoProvider")
    void testDebitoCuentaMethodSource(String monto) {
        cuenta.debito(new BigDecimal(monto));
        assertNotNull(cuenta.getSaldo());
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0, "El saldo no puede ser negativo");
    }

    private static List<String> montoProvider() {
        return List.of("100", "200", "300", "400", "500", "950");
    }

    @Nested
    @Tag("timeout")
    class EjemploTimeOutTest {
        @Test
        @Timeout(2)
        void pruebaTimeout() throws InterruptedException {
            TimeUnit.SECONDS.sleep(1);
        }

        @Test
        @Timeout(value = 1000, unit = TimeUnit.MILLISECONDS)
        void pruebaTimeout2() throws InterruptedException {
            TimeUnit.SECONDS.sleep(1);
        }

        @Test
        void assertTimeoutTest() {
            assertTimeout(Duration.ofSeconds(2), () -> {
                TimeUnit.SECONDS.sleep(1);
            });
        }
    }

}