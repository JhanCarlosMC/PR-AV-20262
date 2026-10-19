package co.edu.uniquindio.sga;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

// Los casos de uso todavía son clases Java simples (Guía 06): el contexto completo
// no los encuentra como beans hasta la Guía 09, donde se registran en Spring.
/** Prueba de arranque del proyecto Spring Boot (Guía 03) */
@Disabled("Se habilita en la Guía 09, cuando los casos de uso sean beans de Spring")
@SpringBootTest
class SgaApplicationTests {

	@Test
	void contextLoads() {
	}

}
