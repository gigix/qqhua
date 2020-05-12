package deaddrop.core;

import deaddrop.utilities.input;
import deaddrop.utilities.output;

import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.nio.file.Paths;

import static deaddrop.core.header.decode_header;
import static deaddrop.utilities.strings.get_extension;
import static deaddrop.utilities.strings.remove_extension;

public class Image {
    // Define class variables
    public Path filename;
    public short encoding_id;
    public BufferedImage image;
    public boolean was_used = false;
    public int data_capacity, data_size, num_channels;
    public byte image_index, encode_mode = -1, encode_tech = -1;

    // Default constructor to load image
    public Image(String _filename, Technique tech) {
        filename = Paths.get(_filename);
        load_image(tech);
    }

    // Allow empty initialization
    public Image() {}

    // Load image from file
    public void load_image(Technique tech) {
        image = input.load_image(this.filename);
        num_channels = image.getRaster().getNumBands();

        decode_header(this, tech);
    }

    // Save image to file
    public void save_image(String filename, String file_type) {
        output.save_image(this.image, filename, file_type);
    }

    // Save image to file
    public void save_image() {
        String raw_filename = remove_extension(this.filename.getFileName().toString());
        save_image("processed/" + raw_filename + ".png");
    }

    // Save image to file
    public void save_image(String filename) {
        String extension = get_extension(filename);
        extension = extension == null ? "png" : extension;

        save_image(filename, extension);
    }

    public int get_index() {
        return image_index;
    }

    @Override
    // Define hash as the one associated with the underlying image
    public int hashCode() {
        return image.hashCode();
    }

    @Override
    public String toString() {
        return image_index + " of set " + encoding_id + " using " +
                data_size / 1024 + "K of " + data_capacity / 1024 + "K";
    }
}
