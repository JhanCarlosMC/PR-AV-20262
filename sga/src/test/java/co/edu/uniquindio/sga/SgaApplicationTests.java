package co.edu.uniquindio.sga;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/** Prueba de arranque del proyecto Spring Boot (Guía 03), habilitada en la Guía 09 */
@SpringBootTest(properties = "sga.jwt.secret=clave-exclusiva-de-pruebas-de-al-menos-32-caracteres")
class SgaApplicationTests {

	@Test
	void contextLoads() {
	}

}
