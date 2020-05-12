package deaddrop.core;

import java.nio.ByteBuffer;

import static deaddrop.utilities.data_management.get_array;
import static deaddrop.utilities.data_management.get_sub_array;
import static deaddrop.utilities.low_level.extract_bit;
import static deaddrop.utilities.low_level.get_bit;

public class header {
    static byte signature = (byte) 0xA8;        // Define default signature as 101010XX
    static byte signature_mask = (byte) 0xFC;   // Define default signature mask

    public static void decode_header(Image img, Technique tech) {
        byte raw = tech.recover_data(img, 1)[0];     // Get first byte (i.e. encoder type)
        if ((raw & signature_mask) != signature) return;       // Check if signature is present

        // Check if correct technique is being used
        byte saved_technique = (byte) extract_bit(raw, 0, 1, 0);
        if ((saved_technique == 0 && (tech instanceof BPCS)) || (saved_technique == 1 && (tech instanceof LSB)))
            throw new IllegalCallerException("Encoder technique not equal to encoded format.");

        img.was_used = true;
        img.encode_tech = saved_technique;

        // Determine encoder used and decode header
        byte mode = (byte) get_bit(raw, 0);
        if (mode == 0) decode_basic(img, tech);
        if (mode == 1) decode_advanced(img, tech);
    }

    // Generate header for encoding mode 1, basic
    public static byte[] generate_basic(Image img, Technique tech) {
        byte[] header = new byte[5];                                    // Allocate header

        header[0] = signature;                                          // Add signature and encoding mode (0)
        if (tech instanceof BPCS) header[0] = (byte) (header[0] | 2);   // If technique 1, mark in header
        System.arraycopy(get_array(img.data_size), 0, header, 1, 4);    // Add data length

        return header;
    }

    // Decode header when using encoding mode 1, basic
    public static void decode_basic(Image img, Technique tech) {
        img.encode_mode = 0;

        // Get data size
        byte[] tmp = tech.recover_data(img, 4, 1);
        img.data_size = ByteBuffer.wrap(tmp).getInt();
    }

    // Generate header for encoding mode 1, advanced
    public static byte[] generate_advanced(Image img, Technique tech) {
        byte[] header = new byte[8];                                    // Allocate header
        header[0] = (byte) (signature | 1);                             // Add signature and encoding mode
        if (tech instanceof BPCS) header[0] = (byte) (header[0] | 2);   // If technique 1, encode in header

        // Add information (data length, image index, encoding id)
        System.arraycopy(get_array(img.data_size), 0, header, 1, 4);
        header[5] = img.image_index;
        System.arraycopy(get_array(img.encoding_id), 0, header, 6, 2);

        return header;
    }

    // Decode header when using encoding mode 2, advanced
    public static void decode_advanced(Image img, Technique tech) {
        img.encode_mode = 1;

        byte[] raw_header = tech.recover_data(img, 7, 1);

        // Recover information (data length, image index, encoding id)
        img.data_size = ByteBuffer.wrap(get_sub_array(raw_header, 0, 4)).getInt();
        img.image_index = raw_header[4];
        img.encoding_id = ByteBuffer.wrap(get_sub_array(raw_header, 5, 2)).getShort();
    }
}
