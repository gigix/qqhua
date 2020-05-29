package controllers;

import org.joda.time.DateTime;
import play.data.FormFactory;
import play.libs.Files;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Base64;

import static deaddrop.Basic.encode;
import static java.lang.String.format;

public class EncryptController extends Controller {
    @Inject
    FormFactory formFactory;

    public Result upload(Http.Request request) throws IOException {
        String message = formFactory.form().bindFromRequest(request).get("message");

        Http.MultipartFormData<Files.TemporaryFile> body = request.body().asMultipartFormData();
        Http.MultipartFormData.FilePart<Files.TemporaryFile> picture = body.getFile("picture");
        if (picture != null) {
            Files.TemporaryFile file = picture.getRef();
            String originalFilePath = format("/tmp/%s.jpg", new DateTime());
            file.copyTo(Paths.get(originalFilePath), true);

            byte[] encryptedImageData = encode(originalFilePath, message);
            return ok(views.html.encrypt.render(Base64.getMimeEncoder().encodeToString(encryptedImageData)));
        } else {
            return badRequest().flashing("error", "Missing file");
        }
    }
}
