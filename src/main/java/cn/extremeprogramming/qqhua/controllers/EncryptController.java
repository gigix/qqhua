package cn.extremeprogramming.qqhua.controllers;

import cn.extremeprogramming.qqhua.models.EncryptedPicture;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
public class EncryptController {
    @PostMapping("/encrypt")
    public String post(
            @RequestParam("message") String message,
            @RequestParam("picture") MultipartFile picture,
            RedirectAttributes attributes) throws IOException {
        EncryptedPicture encryptedPicture = new EncryptedPicture(picture.getBytes(), message);
        attributes.addFlashAttribute("imageAsBase64", encryptedPicture.toBase64());
        return "redirect:/encrypted";
    }

    @GetMapping("/encrypted")
    public String get(@ModelAttribute("imageAsBase64") String imageAsBase64, Model model) {
        model.addAttribute("imageAsBase64", imageAsBase64);
        return "encrypted.html";
    }
}
