package co.edu.uptc.sensores.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import co.edu.uptc.sensores.aplicacion.EventoLecturaPublisher;
import co.edu.uptc.sensores.aplicacion.DeteccionFueraDeRangoService;
import co.edu.uptc.sensores.aplicacion.IngestaLecturaService;
import co.edu.uptc.sensores.aplicacion.LecturaFactory;
import co.edu.uptc.sensores.aplicacion.SensorFactory;
import co.edu.uptc.sensores.aplicacion.ContadorLecturasStore;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(LecturaController.class)
@Import({IngestaLecturaService.class, DeteccionFueraDeRangoService.class, SensorFactory.class, LecturaFactory.class, ContadorLecturasStore.class})
class LecturaControllerTest {
    @Autowired private MockMvc mvc;
    @MockitoBean private EventoLecturaPublisher publisher;

    @Test
    void aceptaLecturaConEnvioYRangos() throws Exception {
        mvc.perform(post("/api/lecturas").contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"sensorId":"a5df56c2-9b6c-4c95-9272-bd7904be0c27","envioId":"fbfdd060-8c44-4ad8-9acb-7e50d91ce9d0","temperatura":9,"humedad":50,"temperaturaMinima":2,"temperaturaMaxima":8,"humedadMinima":30,"humedadMaxima":70}
                        """))
                .andExpect(status().isAccepted());
    }
}
