package deaddrop.utilities;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class output {
    // Print hex output of byte array to console
    public static void print_hex(byte[] array, int start, int end) {
        int offset;

        for(int index = start; index < end && index < array.length; index++) {
            byte target = array[index];
            System.out.printf("%02X ", target);

            offset = start - index - 1;
            if (offset % 16 == 0)
                System.out.print('\n');
            else if (offset % 4 == 0)
                System.out.print("  ");
        }
        System.out.print('\n');
    }

    // Print hex output of byte array to console
    public static void print_hex(byte[] array) {
        print_hex(array, 0, array.length);
    }

    // Write byte array to file
    public static void write_file(Path file_path, byte[] data) {
        FileOutputStream data_file;
        try {
            data_file = new FileOutputStream(file_path.toString());
            data_file.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Write byte array to file
    public static void write_file(String file_name, byte[] data) {
        Path file_path = Paths.get(file_name);
        write_file(file_path, data);
    }

    // Save image to file
    public static void save_image(BufferedImage image, String filename, String file_type) {
        try {
            ImageIO.write(image, file_type, new File(filename));
            System.out.println(filename + " saved.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Print (assumed) character array to console
    public static void read_out(byte[] data) {
        if (data.length > 250)
            throw new IllegalArgumentException("Provided data is too long.");

        for (byte target : data)
            System.out.print((char) target);
        System.out.print('\n');
    }
}
