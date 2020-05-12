package controllers;

import deaddrop.Basic;
import org.joda.time.DateTime;
import play.data.FormFactory;
import play.libs.Files;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

import javax.inject.Inject;
import java.nio.file.Paths;

import static java.lang.String.format;

public class DecryptController extends Controller {
    public Result upload(Http.Request request) {
        Http.MultipartFormData<Files.TemporaryFile> body = request.body().asMultipartFormData();
        Http.MultipartFormData.FilePart<Files.TemporaryFile> picture = body.getFile("picture");
        if (picture != null) {
            Files.TemporaryFile file = picture.getRef();
            String originalFilePath = format("/tmp/%s.jpg", new DateTime());
            file.copyTo(Paths.get(originalFilePath), true);

            Basic decoder = new Basic(new String[]{originalFilePath});
            String encryptedData = new String(decoder.decode_data());
            return ok(encryptedData);
        } else {
            return badRequest().flashing("error", "Missing file");
        }
    }
}
