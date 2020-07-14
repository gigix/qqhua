package cn.extremeprogramming.qqhua.models;

import deaddrop.Basic;
import org.junit.jupiter.api.Test;

import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

import static java.nio.file.Files.readAllBytes;
import static java.nio.file.Paths.get;
import static java.util.Base64.getMimeDecoder;
import static java.util.Objects.requireNonNull;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class EncryptedPictureTest {

    public static final String MESSAGE = "这是我要编码的信息";

    @Test
    public void should_encrypt_message_into_given_file() throws IOException {
        byte[] picture = requireNonNull(getClass().getClassLoader().getResourceAsStream("banner.png")).readAllBytes();
        EncryptedPicture encryptedPicture = new EncryptedPicture(picture, MESSAGE);
        String base64 = encryptedPicture.toBase64();

        byte[] encryptedPictureContent = getMimeDecoder().decode(base64);
        String encryptedPicturePath = "build/tmp/encrypted-banner.png";
        FileOutputStream fileOutputStream = new FileOutputStream(encryptedPicturePath);
        fileOutputStream.write(encryptedPictureContent);
        fileOutputStream.close();

        Basic decoder = new Basic(new String[]{encryptedPicturePath});
        String encryptedData = new String(decoder.decode_data());
        assertThat(encryptedData, is(MESSAGE));
    }

    @Test
    public void should_create_encrypted_file_with_base64() throws IOException {
        String base64 = new String(readAllBytes(get("src/test/resources/encrypted_base64.txt")));
        EncryptedPicture encryptedPicture = new EncryptedPicture(base64);
        assertThat(encryptedPicture.getMessage(), is(MESSAGE));
    }

    @Test
    public void should_demonstrate_usage_in_both_directions() throws IOException {
        byte[] picture = requireNonNull(getClass().getClassLoader().getResourceAsStream("banner.png")).readAllBytes();
        EncryptedPicture encrypted = new EncryptedPicture(picture, MESSAGE);
        String base64 = encrypted.toBase64();

        EncryptedPicture toBeDecrypted = new EncryptedPicture(base64);
        assertThat(toBeDecrypted.getMessage(), is(MESSAGE));
    }
}