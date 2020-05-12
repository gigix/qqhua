package deaddrop;

import deaddrop.core.*;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static deaddrop.core.header.decode_header;
import static deaddrop.utilities.input.get_input;
import static deaddrop.utilities.input.load_file;
import static deaddrop.utilities.output.write_file;
import static deaddrop.utilities.strings.get_extension;

public class Handler {
    Potential selected;
    HashMap<Short, Potential> potentials;
    Technique naive = new LSB(), bpcs = new BPCS();
    String source_directory, target_filename, target_directory;
    boolean will_encode, is_basic, is_naive;
    private String plaintext_key;
    static String manual = "Expected parameters as:\n" +
            "-E/D B/A mode of operations (i.e. encrypt or decode, for encode specify Basic/Advanced)\n" +
            "-T L/B technique (i.e. LSB or BPCS encoding technique)\n" +
            "-s source directory (i.e. location of image files)\n" +
            "-d data file (i.e. path for the data/recovered file)\n" +
            "-t target directory (if encoding, directory to save images to)\n" +
            "-k encryption key, optional";

    public static void main(String[] args) {
        Handler handler = new Handler();
        handler.get_params(args);

        if (!handler.will_encode) {
            handler.select_potential();
            handler.decode_selection();
        }
    }

    // TODO modify params to stop the plaintext key from being supplied in the params (since it would appear in the bash history)
    void get_params(String[] args) {
        for (int index = 0; index < args.length - 1; index++) {
            if (args[index].charAt(0) != '-') continue;
            if (args[index].equals("-s"))
                source_directory = args[index + 1];
            else if (args[index].equals("-d"))
                target_filename = args[index + 1];
            else if (args[index].equals("-E") || args[index].equals("-D")) {        // Assumes decode by default
                will_encode = args[index].equals("-E");
                if (args[index + 1].charAt(0) != '-')
                    is_basic = args[index + 1].equals("B");
            }
            else if (args[index].equals("-t"))
                target_directory = args[index + 1];
            else if (will_encode && args[index].equals("-T")) {
                if (args[index + 1].charAt(0) != '-')
                    is_naive = args[index + 1].equals("L");
            }
            else if (args[index].equals("-k"))
                plaintext_key = args[index + 1];
        }

        if (source_directory == null)
            throw new IllegalArgumentException("Source directory not provided.\n" + manual);
        if (!(new File(source_directory).exists()))
            throw new IllegalArgumentException("Source directory doesn't exist.\n" + manual);
        if (target_filename == null)
            throw new IllegalArgumentException("Data filename must be provided.\n" + manual);
        if (will_encode && !(new File(target_filename)).exists())
            throw new IllegalArgumentException("Data file does not exist.\n" + manual);
        if (will_encode && target_directory == null)
            throw new IllegalArgumentException("Target directory not provided.\n" + manual);
        if (will_encode && !(new File(target_directory).exists()))
            throw new IllegalArgumentException("Target directory doesn't exist.\n" + manual);

        get_potentials();
    }

    void get_potentials() {
        Path dir_path = Paths.get(source_directory);
        File[] files = get_candidates(dir_path);

        if (!will_encode)
            potentials = check_candidates(files);
        else {
            System.out.println("Available images:");
            for (int index = 0; index < files.length; index++)
                System.out.printf("%d %s\n", index, files[index].getName());
            encode_selection(files);
        }
    }

    void encode_selection(File[] files) {
        String[] filenames = new String[files.length];
        for (int index = 0; index < files.length; index++)
            filenames[index] = files[index].toString();

        String tech = is_basic? "naive" : "bpcs";
        Encoder encoder = is_basic? new Basic(filenames, tech) : new Advanced(filenames, tech);
        if (plaintext_key != null)
            encoder.set_encryption_key(plaintext_key);

        byte[] file_data = load_file(target_filename);
        encoder.encode_data(file_data);
        encoder.save_images(target_directory);
    }

    void decode_selection() {
        if (selected == null)
            throw new IllegalCallerException("Can't decode data without a valid selection");
        Image[] selected_images = selected.image_set.toArray(new Image[0]);
        Technique tech = selected_images[0].encode_tech == 0? naive : bpcs;

        Encoder encoder;
        if (selected.is_advanced) encoder = new Advanced(selected_images, tech, selected.encoding_id);
        else encoder = new Basic(selected_images, tech);
        if (plaintext_key != null)
            encoder.set_encryption_key(plaintext_key);

        byte[] recovered = encoder.decode_data();
        write_file(target_filename, recovered);
    }

    public void select_potential() {
        System.out.println(this);
        System.out.println("Enter the entry index or encoding ID");
        short index = get_input();              // Get user index selection
        if (potentials.containsKey(index))      // If basic get encoding
            selected = potentials.get(index);
        else                                    // If advanced get encoding
            selected = potentials.values().stream()
                    .filter(potential -> potential.index == index).findFirst()
                    .orElse(null);

        // If index is invalid, try again
        if (selected == null) {
            System.out.println("Invalid index, try again.");
            select_potential();
            return;
        }
        System.out.print("Selected ");
        System.out.println(selected);
    }

    public HashMap<Short, Potential> check_candidates(File[] candidates) {
        HashMap<Short, Potential> potentials = new HashMap<>();
        short index = 0;

        for (File filename : candidates) {
            Image img = new Image(filename.toString(), naive);      // Try naive
            if (!img.was_used) decode_header(img, bpcs);            // Try bpcs
            if (!img.was_used) continue;                            // Unused image

            if (img.encode_mode == 0)           // If basic
                potentials.put(index, new Potential(index++, false, img));
            else if (img.encode_mode == 1) {    // If advanced
                short key = img.encoding_id;
                if (potentials.containsKey(key))
                    potentials.get(key).add(img);
                else
                    potentials.put(key, new Potential(index++, true, img, key));
            }
        }

        return potentials;
    }

    static HashSet<String> source_filetypes = new HashSet<>(Arrays.asList("png", "jpg", "bmp"));
    static HashSet<String> recovery_filetypes = new HashSet<>(Arrays.asList("png", "bmp"));

    final static FilenameFilter decode_filter = ((dir, name) -> recovery_filetypes.contains(get_extension(name)));
    final static FilenameFilter encode_filter = ((dir, name) -> source_filetypes.contains(get_extension(name)));

    // TODO modify to allow user to choose the images when encoding or sort images to use them in order of decreasing capacity
    // TODO add check to ensure user wants to overwrite previously used images (where applicable)
    public File[] get_candidates(Path directory) {
        File dir_file = directory.toFile();

        return dir_file.listFiles(will_encode? encode_filter : decode_filter);
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        output.append("Potential candidates:\n");

        // Before printing, sort potentials by index
        ArrayList<Potential> sorted_potentials = new ArrayList<>(potentials.values());
        sorted_potentials.sort(Comparator.comparingInt(Potential::get_index));

        for (Potential potential : sorted_potentials) {
            output.append(potential);
            output.append('\n');
        }

        return output.toString();
    }
}
