package deaddrop.utilities;

public class low_level {
    public static int base_bit = ~1;

    // Generates mask to isolate the bit at the given index
    public static int get_mask(int index) {
        return 1 << index;
    }

    // Isolates the bit at a given index in the source int
    // TODO Add range check on index
    public static int get_bit(int source, int index) {
        return source & get_mask(index);
    }

    // Inserts bit from given index of source into LSB of the target
    public static int insert_bit(int source, int target, int index) {
        return insert_bit(source, target, index, 0);
    }

    // Inserts bit from given index of source into specified index in target
    public static int insert_bit(int source, int target, int source_index, int target_index) {
        int source_bit = get_bit(source, source_index), offset = target_index - source_index;
        source_bit = (offset < 0)? source_bit >> -offset : source_bit << offset;

        return (target & ~get_mask(target_index)) | source_bit;
    }

    // Extracts the bit from the LSB of the source and inserts into the given index of the target
    public static int extract_bit(int source, int target, int index) {
        return extract_bit(source, target, 0, index);
    }

    // Extracts the bit from the given index of the source and inserts into the specified index of the target
    public static int extract_bit(int source, int target, int source_index, int target_index) {
        int offset = target_index - source_index, source_bit = get_bit(source, source_index);
        source_bit = (offset < 0)? source_bit >> -offset : source_bit << offset;

        return source_bit | target;
    }
}
