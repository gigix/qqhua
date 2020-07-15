package cn.extremeprogramming.qqhua.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.io.InputStream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DecryptController.class)
public class DecryptControllerTest {
    @Autowired
    private MockMvc mockMvc;

    private MockMultipartFile picture;

    @BeforeEach
    public void setUp() throws IOException {
        InputStream resource = getClass().getClassLoader().getResourceAsStream("encrypted_picture.png");
        picture = new MockMultipartFile("picture", resource);
    }

    @Test
    public void should_handle_post_request_and_render_view() throws Exception {
        mockMvc.perform(multipart("/decrypt").file(picture))
                .andExpect(status().isOk())
                .andExpect(view().name("decrypted.html"));
    }

    @Test
    public void should_decrypt_uploaded_picture_and_retrieve_text() throws Exception {
        mockMvc.perform(multipart("/decrypt").file(picture))
                .andExpect(model().attributeExists("message"))
                .andExpect(model().attribute("message", "这是我要编码的信息"));
    }
}
