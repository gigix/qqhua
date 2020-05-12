package deaddrop.utilities;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;


public class input {
    private static Scanner input_scanner;

    // Import file as byte stream
    public static byte[] load_file(Path file_path) {
        FileInputStream data_stream;
        byte[] byte_array;

        try {
            File data_file = new File(file_path.toString());    // Initialize file
            if (!data_file.exists())                            // Check if file exists
                throw new FileNotFoundException("Given path does not exist.");

            byte_array = new byte[(int) data_file.length()];    // Initialize byte array
            data_stream = new FileInputStream(data_file);       // Initialize data stream
            data_stream.read(byte_array);                       // Load file into byte array

        } catch (IOException e) {                               // Catch IO errors
            e.printStackTrace();
            return null;
        }

        return byte_array;
    }

    // Load file
    public static byte[] load_file(String filename) {
        Path file_path = Paths.get(filename);
        return load_file(file_path);
    }

    // Load image
    public static BufferedImage load_image(String filename) {
        BufferedImage image;
        try {
            image = ImageIO.read(new File(filename));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return image;
    }

    // Load image
    public static BufferedImage load_image(Path file_path) {
        return load_image(file_path.toString());
    }

    // Get user input (numeric short)
    public static short get_input() {
        if (input_scanner == null)
            input_scanner = new Scanner(System.in);
        return input_scanner.nextShort();
    }
}
