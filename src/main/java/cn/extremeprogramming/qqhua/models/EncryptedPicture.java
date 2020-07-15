package cn.extremeprogramming.qqhua.models;

import deaddrop.Basic;
import org.joda.time.DateTime;

import java.io.FileOutputStream;
import java.io.IOException;

import static java.lang.String.format;
import static java.util.Base64.getMimeDecoder;
import static java.util.Base64.getMimeEncoder;

public class EncryptedPicture {
    private final byte[] picture;
    private final String message;

    public EncryptedPicture(byte[] picture, String message) {
        this.picture = picture;
        this.message = message;
    }

    public EncryptedPicture(String encryptedBase64) throws IOException {
        picture = getMimeDecoder().decode(encryptedBase64);
        message = decode();
    }

    public EncryptedPicture(byte[] encryptedPictureContent) throws IOException {
        picture = encryptedPictureContent;
        message = decode();
    }

    public String toBase64() throws IOException {
        String originalFilePath = writePictureToTempFile();
        byte[] encodedPictureContent = Basic.encode(originalFilePath, message);
        return getMimeEncoder().encodeToString(encodedPictureContent);
    }

    public String getMessage() {
        return message;
    }

    private String decode() throws IOException {
        String originalFilePath = writePictureToTempFile();
        Basic decoder = new Basic(new String[]{originalFilePath});
        return new String(decoder.decode_data());
    }

    private String writePictureToTempFile() throws IOException {
        String originalFilePath = format("/tmp/%s.jpg", new DateTime());
        new FileOutputStream(originalFilePath).write(picture);
        return originalFilePath;
    }
}
