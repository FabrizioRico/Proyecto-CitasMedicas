package pe.edu.cibertec.demo_citas_medicas.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import org.springframework.security.test.context.support.WithMockUser;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc

class CitamedicaControllerTest {

    @Autowired
    private MockMvc mockMvc;



@Test
void getAllCitamedicas()throws Exception  {

}



    @Test
    void saveCitamedica() throws Exception {

    }

    @Test
    void updateCitamedica() throws Exception {


    }

    @Test
    void marcarComoAtendida() throws Exception{

    }
}