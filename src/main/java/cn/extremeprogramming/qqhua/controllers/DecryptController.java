package cn.extremeprogramming.qqhua.controllers;

import cn.extremeprogramming.qqhua.models.EncryptedPicture;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
public class DecryptController {
    @PostMapping("/decrypt")
    public String post(@RequestParam("picture") MultipartFile picture, Model model) throws IOException {
        EncryptedPicture encryptedPicture = new EncryptedPicture(picture.getBytes());
        model.addAttribute("message", encryptedPicture.getMessage());
        return "decrypted.html";
    }
}
