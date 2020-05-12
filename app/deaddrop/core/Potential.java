package deaddrop.core;

import java.util.ArrayList;

public class Potential {
    public int index, data_stored, data_capacity;
    public short encoding_id;
    public boolean is_advanced;
    public ArrayList<Image> image_set;

    public Potential(int _index, boolean _is_advanced, Image img, short _encoding_id) {
        index = _index;
        is_advanced = _is_advanced;
        encoding_id = _encoding_id;
        image_set = new ArrayList<>();

        add(img);
    }

    public Potential(int _index, boolean _is_advanced, Image img) {
        this(_index, _is_advanced, img, (short) -1);
    }

    public void add(Image img) {
        if (!is_advanced && image_set.size() > 0)
            throw new IllegalArgumentException("Cannot add more than one image to basic potential encoding");
        image_set.add(img);
    }

    public void get_usage() {
        data_stored = data_capacity = 0;
        for (Image image : image_set) {
            data_stored += image.data_size;
            data_capacity += image.data_capacity;
        }
    }

    public int get_index() {
        return index;
    }

    @Override
    public int hashCode() {
        System.out.printf("Computing hash for %d on %s\n", encoding_id, image_set.get(0).filename);
        if (is_advanced)
            return encoding_id;
        return image_set.get(0).hashCode();
    }

    @Override
    public String toString() {
        get_usage();

        StringBuilder listing = new StringBuilder();
        listing.append(index);
        listing.append(": ");
        listing.append(is_advanced? "advanced " : "basic ");
        listing.append(image_set.get(0).encode_tech == 0? "lsb " : "bpcs ");

        listing.append("Using ");
        listing.append(data_stored / 1024);
        listing.append("K of ");
        listing.append(data_capacity / 1024);
        listing.append("K ");

        if (is_advanced)
            listing.append(encoding_id);

        for (Image img : image_set) {
            listing.append(' ');
            listing.append(img.filename.getFileName());
        }
        return listing.toString();
    }
}
