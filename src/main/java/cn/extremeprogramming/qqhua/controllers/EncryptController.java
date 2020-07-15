package cn.extremeprogramming.qqhua.controllers;

import cn.extremeprogramming.qqhua.models.EncryptedPicture;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
public class EncryptController {

    public static final String ATTR_NAME_FOR_BASE64 = "imageAsBase64";

    @PostMapping("/encrypt")
    public String post(
            @RequestParam("message") String message,
            @RequestParam("picture") MultipartFile picture,
            Model model) throws IOException {
        EncryptedPicture encryptedPicture = new EncryptedPicture(picture.getBytes(), message);
        model.addAttribute(ATTR_NAME_FOR_BASE64, encryptedPicture.toBase64());
        return "encrypted.html";
    }
}
