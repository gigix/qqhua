package controllers;

import deaddrop.Basic;
import deaddrop.core.Image;
import org.joda.time.DateTime;
import play.data.FormFactory;
import play.libs.Files;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import static deaddrop.Basic.decode;
import static deaddrop.Basic.encode;
import static java.lang.String.format;

public class EncryptController extends Controller {
    @Inject
    FormFactory formFactory;

    public Result index() {
        return ok(views.html.encrypt.render());
    }

    public Result upload(Http.Request request) throws IOException {
        String message = formFactory.form().bindFromRequest(request).get("message");

        Http.MultipartFormData<Files.TemporaryFile> body = request.body().asMultipartFormData();
        Http.MultipartFormData.FilePart<Files.TemporaryFile> picture = body.getFile("picture");
        if (picture != null) {
            Files.TemporaryFile file = picture.getRef();
            String originalFilePath = format("/tmp/%s.jpg", new DateTime());
            file.copyTo(Paths.get(originalFilePath), true);

            byte[] encryptedImageData = encode(originalFilePath, message);
            System.out.println(encryptedImageData.length);
            return ok(encryptedImageData).as("image/png");
        } else {
            return badRequest().flashing("error", "Missing file");
        }
    }
}
