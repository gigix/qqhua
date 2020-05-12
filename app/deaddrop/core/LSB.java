package deaddrop.core;

import java.awt.image.Raster;
import java.awt.image.WritableRaster;

import static deaddrop.utilities.low_level.extract_bit;
import static deaddrop.utilities.low_level.insert_bit;

public class LSB extends Technique {
    // Analyze image in preparation for future operations
    public void analyze_image(Image img) {
        Raster raster = img.image.getRaster();
        img.data_capacity = raster.getWidth() * raster.getHeight() * img.num_channels / 8;
    }

    // Embed data into image
    public int embed_data(Image img, byte[] data, int offset) {
        return embed_data(img, data, offset, 0, -1);
    }

    // TODO add line to enable LSB to perform encryption
    // Embed data into image
    public int embed_data(Image img, byte[] data, int byte_offset, int bit_plane, int target_channel) {
        WritableRaster image_raster = img.image.getRaster();

        int num_channels = image_raster.getNumBands();
        int byte_index = 0, bit_index = 0, source = data[0];
        int height = image_raster.getHeight(), width = image_raster.getWidth();
        int channel_start = (target_channel == -1)? 0 : target_channel,
                channel_end = (target_channel == -1)? num_channels : target_channel + 1,
                channel_width = channel_end - channel_start;


        int[] target_image = new int[num_channels];
        boolean initial_load = true;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (initial_load) {
                    x = (byte_offset * 8 / channel_width) / height;
                    y = (byte_offset * 8 / channel_width) % height;
                }

                image_raster.getPixel(x, y, target_image);          // Get pixel value
                for (int channel=channel_start;channel<channel_end;channel++) {
                    if (initial_load) {
                        initial_load = false;
                        channel = (target_channel != -1)? target_channel : (byte_offset * 8) % channel_width;
                    }

                    target_image[channel] = insert_bit(source, target_image[channel], bit_index, bit_plane);

                    bit_index++;
                    if (bit_index > 7) {
                        bit_index = 0;                              // Reset bit index

                        byte_index++;                               // Increment byte index
                        if (byte_index >= data.length) {            // If no more data, stop
                            image_raster.setPixel(x, y, target_image);      // Set pixel value before stopping
                            return data.length;
                        }
                        source = data[byte_index];                  // Get next byte to embed
                    }
                }
                image_raster.setPixel(x, y, target_image);          // Set pixel value
            }
        }
        return data.length;
    }

    // Recover data from image
    public byte[] recover_data(Image img, int data_size, int offset) {
        byte[] data = new byte[data_size];
        return recover_data(img, data, offset);
    }

    // Recover data from image
    public byte[] recover_data(Image img, byte[] data, int offset) {
        return recover_data(img, data, offset, 0, -1);
    }

    // Recover data from image
    public byte[] recover_data(Image img, byte[] data, int byte_offset, int bit_plane, int target_channel) {
        WritableRaster image_raster = img.image.getRaster();            // Get core.image raster

        int num_channels = image_raster.getNumBands();              // Get number of channels in core.image
        int height = image_raster.getHeight(), width = image_raster.getWidth();   // Get height and width of core.image
        int byte_index = 0, bit_index = 0, current_byte = 0;        // Allocate indexes

        int channel_start = (target_channel == -1)? 0 : target_channel,
                channel_end = (target_channel == -1)? num_channels : target_channel + 1,
                channel_width = channel_end - channel_start;

        int[] target_pixel = new int[num_channels];                 // Allocate byte array to extract core.image data
        boolean initial_load = true;                                // Set initial loop to true

        for (int x = 0; x < width; x++) {         // For each row
            for (int y = 0; y < height; y++) {    // For each column
                if (initial_load) {         // Set offset before starting
                    x = (byte_offset * 8 / channel_width) / height;
                    y = (byte_offset * 8 / channel_width) % height;
                }

                image_raster.getPixel(x, y, target_pixel);  // Get pixel value
                for (int channel = channel_start; channel < channel_end; channel++) {
                    if (initial_load) {
                        initial_load = false;
                        channel = (target_channel != -1)? target_channel : (byte_offset * 8) % channel_width;
                    }
                    current_byte = extract_bit(target_pixel[channel], current_byte, bit_plane, bit_index);

                    bit_index++;                                    // Increment bit index
                    if (bit_index > 7) {                            // If end of byte
                        bit_index = 0;                              // Reset bit index

                        data[byte_index] = (byte) current_byte;     // Add current byte to extracted data array
                        current_byte = 0;                           // Reset current byte value

                        byte_index++;                               // Increment byte index
                        if (byte_index >= data.length)              // If no more data, stop
                            return data;
                    }
                }
            }
        }
        return data;
    }
}
