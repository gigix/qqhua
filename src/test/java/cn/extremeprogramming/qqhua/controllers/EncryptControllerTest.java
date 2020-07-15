package cn.extremeprogramming.qqhua.controllers;

import cn.extremeprogramming.qqhua.models.EncryptedPicture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.io.InputStream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EncryptController.class)
public class EncryptControllerTest {
    @Autowired
    private MockMvc mockMvc;

    private MockMultipartFile picture;

    @BeforeEach
    public void setUp() throws IOException {
        InputStream resource = getClass().getClassLoader().getResourceAsStream("banner.png");
        picture = new MockMultipartFile("picture", resource);
    }

    @Test
    public void should_handle_post_and_render_view() throws Exception {
        mockMvc.perform(multipart("/encrypt")
                .file(picture)
                .param("message", "Hello!"))
                .andExpect(status().isOk())
                .andExpect(view().name("encrypted.html"));
    }

    @Test
    public void should_encrypt_message_into_picture_as_base64() throws Exception {
        String message = "Hello!";
        EncryptedPicture encryptedPicture = new EncryptedPicture(picture.getBytes(), message);
        String expectedBase64 = encryptedPicture.toBase64();

        mockMvc.perform(multipart("/encrypt")
                .file(picture)
                .param("message", message))
                .andExpect(model().attributeExists("imageAsBase64"))
                .andExpect(model().attribute("imageAsBase64", expectedBase64));
    }
}
