package co.edu.uniquindio.sga;

import co.edu.uniquindio.sga.domain.entity.*;
import co.edu.uniquindio.sga.domain.repository.*;
import co.edu.uniquindio.sga.domain.valueobject.*;
import co.edu.uniquindio.sga.infrastructure.persistence.jpa.UsuarioJpaDataRepository;
import co.edu.uniquindio.sga.infrastructure.security.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import tools.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.*;
import java.time.*;
import java.math.BigDecimal;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
    "sga.jwt.secret=clave-exclusiva-de-pruebas-de-al-menos-32-caracteres",
    "spring.datasource.url=jdbc:h2:mem:seguridad;DB_CLOSE_DELAY=-1"
})
class SeguridadIntegrationTest {
    @LocalServerPort int port;
    @Autowired ObjectMapper json;
    @Autowired UsuarioJpaDataRepository usuarios;
    @Autowired JwtTokenProvider tokens;
    @Autowired ApartamentoRepository apartamentos;
    @Autowired co.edu.uniquindio.sga.infrastructure.persistence.inmemory.TemporadaRepositoryInMemory temporadas;
    @Autowired co.edu.uniquindio.sga.infrastructure.persistence.inmemory.PoliticaCancelacionRepositoryInMemory politicas;
    final HttpClient client = HttpClient.newHttpClient();

    HttpResponse<String> call(String method, String path, String body, String token) throws Exception {
        var builder = HttpRequest.newBuilder(URI.create("http://localhost:"+port+path))
            .header("Content-Type", "application/json")
            .method(method, body == null ? HttpRequest.BodyPublishers.noBody() : HttpRequest.BodyPublishers.ofString(body));
        if (token != null) builder.header("Authorization", "Bearer "+token);
        return client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
    }
    String login(String correo, String password) throws Exception {
        var response = call("POST", "/api/auth/login", "{\"correo\":\""+correo+"\",\"contrasena\":\""+password+"\"}", null);
        assertEquals(200, response.statusCode(), response.body());
        assertEquals("Bearer", json.readTree(response.body()).get("tipo").asText());
        return json.readTree(response.body()).get("token").asText();
    }
    @Test void autenticacionRegistroYJwtInvalido() throws Exception {
        assertEquals(5, usuarios.count());
        assertTrue(usuarios.findAll().stream().allMatch(u -> u.getContrasena().startsWith("{bcrypt}")));
        String recepcion = login("recepcion@sga.local", "Recepcion2026");
        assertEquals(401, call("POST", "/api/auth/login", "{\"correo\":\"recepcion@sga.local\",\"contrasena\":\"incorrecta\"}",null).statusCode());
        assertEquals(400, call("POST", "/api/auth/login", "{\"correo\":\"invalido\",\"contrasena\":\"incorrecta\"}",null).statusCode());
        assertEquals(401, call("GET", "/api/reservas?estado=PENDIENTE",null,null).statusCode());
        assertEquals(200, call("GET", "/api/reservas?estado=PENDIENTE",null,recepcion).statusCode());
        assertEquals(401, call("GET", "/api/reservas?estado=PENDIENTE",null,"x"+recepcion).statusCode());
        String vencido = tokens.generateTokenAsString("recepcion@sga.local", "1000000002", List.of("RECEPCIONISTA"), Instant.now().minusSeconds(600), Instant.now().minusSeconds(120));
        assertEquals(401, call("GET", "/api/reservas?estado=PENDIENTE",null,vencido).statusCode());
        String registro = """
            {"nombre":"Nuevo","correo":"nuevo@sga.local","contrasena":"Segura2026","telefono":"3001234567","documento":"99000001","fechaNacimiento":"1990-01-01","rol":"ADMINISTRADOR"}
            """;
        assertEquals(201, call("POST", "/api/auth/registro",registro,null).statusCode());
        assertEquals("HUESPED", usuarios.findByCorreo("nuevo@sga.local").orElseThrow().getRol().name());
        assertEquals(409, call("POST", "/api/auth/registro",registro,null).statusCode());
        assertEquals(400, call("POST", "/api/auth/registro",registro.replace("Segura2026","debil"),null).statusCode());
        var nuevo = usuarios.findByCorreo("nuevo@sga.local").orElseThrow();
        nuevo.setActivo(false); usuarios.saveAndFlush(nuevo);
        assertEquals(401, call("POST", "/api/auth/login","{\"correo\":\"nuevo@sga.local\",\"contrasena\":\"Segura2026\"}",null).statusCode());
    }
    @Test void rolesCanalesYPropietario() throws Exception {
        String h1=login("huesped1@sga.local","Huesped2026"), h2=login("huesped2@sga.local","Huesped2026");
        String recepcion=login("recepcion@sga.local","Recepcion2026"), servicio=login("servicio@sga.local","Servicio2026");
        String admin=login("admin@sga.local","Admin2026");
        assertEquals(403,call("GET","/api/reservas?estado=PENDIENTE",null,h1).statusCode());
        assertEquals(200,call("GET","/api/reservas?estado=PENDIENTE",null,admin).statusCode());
        assertEquals(404,call("GET","/api/reservas/RES-NO-EXISTE",null,recepcion).statusCode());
        assertEquals(403,call("GET","/api/reservas/RES-NO-EXISTE",null,h1).statusCode());
        var hoy=LocalDate.now(); var base=new IdTemporada("SEGURIDAD");
        temporadas.guardar(new Temporada(base,"Base",new Periodo(hoy,hoy.plusYears(1)),true));
        var apt=new Apartamento(new IdentificacionApartamento("APT-SEC"),"Pruebas",2,4);
        apt.definirTarifa(new Tarifa(base,new Dinero(new BigDecimal("200000")))); apartamentos.guardar(apt);
        politicas.guardar(new PoliticaCancelacion(new VersionPolitica(1,hoy),List.of(new TramoRetencion(0,new Porcentaje(new BigDecimal("50"))), new TramoRetencion(72,new Porcentaje(BigDecimal.ZERO))),new Porcentaje(new BigDecimal("100"))));
        String body="""
            {"identificacionApartamento":"APT-SEC","fechaEntrada":"%s","fechaSalida":"%s","canalOrigen":"PORTAL","horaEstimadaLlegada":"16:00","titular":{"documento":"1094000111","nombre":"Laura","fechaNacimiento":"1995-01-30"},"ocupantes":[{"documento":"1094000111","nombre":"Laura","fechaNacimiento":"1995-01-30"}]}
            """.formatted(hoy.plusDays(10),hoy.plusDays(12));
        assertEquals(403,call("POST","/api/reservas",body,h2).statusCode());
        assertEquals(403,call("POST","/api/reservas",body.replace("PORTAL","DIRECTO"),h1).statusCode());
        assertEquals(403,call("POST","/api/reservas",body,recepcion).statusCode());
        assertEquals(403,call("POST","/api/reservas",body,servicio).statusCode());
        var created=call("POST","/api/reservas",body,h1);
        assertEquals(201,created.statusCode(),created.body());
        String path=URI.create(created.headers().firstValue("Location").orElseThrow()).getPath();
        assertEquals(200,call("GET",path,null,h1).statusCode());
        assertEquals(403,call("GET",path,null,h2).statusCode());
        assertEquals(403,call("PUT",path+"/confirmar",null,servicio).statusCode());
        assertEquals(403,call("PUT",path+"/confirmar",null,h1).statusCode());
        assertEquals(200,call("PUT",path+"/confirmar",null,recepcion).statusCode());
        assertEquals(403,call("PUT",path+"/cancelar","{\"motivo\":\"Cambio de planes\"}",h2).statusCode());
        assertEquals(200,call("PUT",path+"/cancelar","{\"motivo\":\"Cambio de planes\"}",h1).statusCode());
    }
}
