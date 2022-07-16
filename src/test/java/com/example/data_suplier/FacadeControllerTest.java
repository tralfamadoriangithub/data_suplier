package com.example.data_suplier;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
public class FacadeControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    public void getAll() throws Exception {
        getOkByUrl("/");
    }

    @Test
    public void getAllIds() throws Exception {
        getOkByUrl("/ids");
    }

    @Test
    public void getById() throws Exception {

        String url = "/info/2";
        String expected = "{\"id\":\"2\"}";

        getOkByUrl(url).andExpect(content().string(equalTo(expected)));
    }

    @Test
    public void getByUnexistingId() throws Exception {

        String url = "/info/999";
        String expected = "";

        getOkByUrl(url).andExpect(content().string(equalTo(expected)));
    }

    private ResultActions getOkByUrl(String url) throws Exception {
        return getByUrl(url).andExpect(status().isOk());
    }

    private ResultActions getByUrl(String url) throws Exception {
        return mvc.perform(MockMvcRequestBuilders.get(url).accept(MediaType.APPLICATION_JSON));
    }
}
