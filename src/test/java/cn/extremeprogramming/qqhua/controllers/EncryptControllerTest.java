package cn.extremeprogramming.qqhua.controllers;

import cn.extremeprogramming.qqhua.models.EncryptedPicture;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.io.InputStream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EncryptController.class)
public class EncryptControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void should_handle_post_and_redirect() throws Exception {
        InputStream resource = getClass().getClassLoader().getResourceAsStream("banner.png");
        MockMultipartFile picture = new MockMultipartFile("picture", resource);
        mockMvc.perform(multipart("/encrypt")
                .file(picture)
                .param("message", "Hello!"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/encrypted"));
    }

    @Test
    public void should_encrypt_message_into_picture_as_base64() throws Exception {
        InputStream resource = getClass().getClassLoader().getResourceAsStream("banner.png");
        MockMultipartFile picture = new MockMultipartFile("picture", resource);
        String message = "Hello!";

        EncryptedPicture encryptedPicture = new EncryptedPicture(picture.getBytes(), message);
        String expectedBase64 = encryptedPicture.toBase64();

        mockMvc.perform(multipart("/encrypt")
                .file(picture)
                .param("message", message))
                .andExpect(flash().attributeExists("imageAsBase64"))
                .andExpect(flash().attribute("imageAsBase64", expectedBase64));
    }

    @Test
    public void should_render_template_to_show_encrypted_picture() throws Exception {
        String expectedBase64Attr = "this_should_be_base_64";
        mockMvc.perform(get("/encrypted")
                .flashAttr("imageAsBase64", expectedBase64Attr))
                .andExpect(status().isOk())
                .andExpect(view().name("encrypted.html"))
                .andExpect(model().attribute("imageAsBase64", expectedBase64Attr));
    }
}
