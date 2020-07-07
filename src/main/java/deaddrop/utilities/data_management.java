package deaddrop.utilities;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static deaddrop.core.BPCS.max_plane;

public class data_management {
    // Concatenate byte arrays
    public static byte[] concat_arrays(byte[] a, byte[] b) {
        int new_length = a.length + b.length;
        byte[] combined = new byte[new_length];

        System.arraycopy(a, 0, combined, 0, a.length);
        System.arraycopy(b, 0, combined, a.length, b.length);

        return combined;
    }

    // Split byte arrays
    public static void split_array(byte[] combined, byte[] a, byte[] b) {
        System.arraycopy(combined, 0, a, 0, a.length);
        System.arraycopy(combined, a.length, b, 0, b.length);
    }

    public static void offload_differences(byte[][][] counts, int x, int y, int difference) {
        for (int plane = 0; plane < max_plane; plane++) {
            if (counts[plane][x][y] < Byte.MAX_VALUE)
                counts[plane][x][y] += low_level.get_bit(difference, plane) > 0? 1 : 0;
        }
    }

    public static byte[] get_sub_array(byte[] array, int start, int length) {
        byte[] sub_array = new byte[length];
        System.arraycopy(array, start, sub_array, 0, length);

        return sub_array;
    }

    // Compute MD5 hash of byte array
    public static String compute_md5(byte[] data) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");                  // Initialize MD5 hash
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }

        byte[] messageDigest = md.digest(data);                     // Hash content
        BigInteger no = new BigInteger(1, messageDigest);   // Convert to numeric value
        StringBuilder hashtext = new StringBuilder();               // Initialize string builder
        hashtext.append(no.toString(16));                     // Convert numeric to hex value

        while (hashtext.length() < 32)                              // Add zeros if length is inadequate
            hashtext.insert(0, "0");

        return hashtext.toString();                                 // Convert string builder to string
    }

    public static byte[] get_array(short value) {
        return ByteBuffer.allocate(2).putShort(value).array();
    }

    public static byte[] get_array(int value) {
        return ByteBuffer.allocate(4).putInt(value).array();
    }

    public static byte[] get_array(long value) {
        return ByteBuffer.allocate(8).putLong(value).array();
    }
}
