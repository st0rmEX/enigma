package enigma;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;


import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;


import static enigma.EnigmaException.*;

/** Enigma simulator.
 *  @author Troy Burad
 */
public final class Main {

    /** Process a sequence of encryptions and decryptions, as
     *  specified by ARGS, where 1 <= ARGS.length <= 3.
     *  ARGS[0] is the name of a configuration file.
     *  ARGS[1] is optional; when present, it names an input file
     *  containing messages.  Otherwise, input comes from the standard
     *  input.  ARGS[2] is optional; when present, it names an output
     *  file for processed messages.  Otherwise, output goes to the
     *  standard output. Exits normally if there are no errors in the input;
     *  otherwise with code 1. */
    public static void main(String... args) {
        try {
            boolean verbose = false;
            List<String> positional = new ArrayList<>();

            for (String arg : args) {
                if (arg.equals("--verbose")) {
                    verbose = true;
                } else if (arg.startsWith("--")) {
                    throw error("Usage: java enigma.Main [--verbose] "
                            + "[INPUT [OUTPUT]]");
                } else {
                    positional.add(arg);
                }
            }

            if (positional.isEmpty() || positional.size() > 3) {
                throw error("Usage: java enigma.Main [--verbose] "
                        + "[INPUT [OUTPUT]]");
            }

            _verbose = verbose;
            new Main(positional).process();
            return;
        } catch (EnigmaException excp) {
            System.err.printf("Error: %s%n", excp.getMessage());
        }
        System.exit(1);
    }

    /** Open the necessary files for non-option arguments ARGS (see comment
     *  on main). */
    Main(List<String> args) {
        _config = getInput(args.get(0));

        if (args.size() > 1) {
            _input = getInput(args.get(1));
        } else {
            _input = new Scanner(System.in);
        }

        if (args.size() > 2) {
            _output = getOutput(args.get(2));
        } else {
            _output = System.out;
        }
    }

    /** Return a Scanner reading from the file named NAME. */
    private Scanner getInput(String name) {
        try {
            return new Scanner(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Return a PrintStream writing to the file named NAME. */
    private PrintStream getOutput(String name) {
        try {
            return new PrintStream(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Configure an Enigma machine from the contents of configuration
     *  file _config and apply it to the messages in _input, sending the
     *  results to _output. */
    private void process() {
        Machine mac = readConfig();
        Scanner newline;
        String currline, result;
        boolean hasinput = false;
        while (_input.hasNextLine()) {
            newline = new Scanner(_input.nextLine());
            if (newline.hasNext("\\*")) {
                setUp(mac, newline);
                hasinput = true;
            } else if (hasinput) {
                currline = "";
                while (newline.hasNext("[^*]*")) {
                    currline +=  newline.next();
                }
                result = mac.convert(currline);
                printMessageLine(result);

            } else {
                throw error("must need asterix");
            }

        }
    }

    /** Return an Enigma machine configured from the contents of configuration
     *  file _config. */
    private Machine readConfig() {
        try {
            String alphabetstr = _config.next();
            _alphabet = new Alphabet(alphabetstr);

            int rotors, pawls;
            if (_config.hasNextInt()) {
                rotors = _config.nextInt();
            } else {
                throw error("Invalid Rotor Input");
            }

            if (_config.hasNextInt()) {
                pawls = _config.nextInt();
            } else {
                throw error("Invalid Pawl Input");
            }

            ArrayList<Rotor> allRotors = new ArrayList<Rotor>();
            ArrayList<String> names = new ArrayList<String>();

            while (_config.hasNext()) {
                Rotor current = readRotor();
                if (!names.contains(current.name())) {
                    names.add(current.name());
                    allRotors.add(current);
                } else {
                    throw error("Duplicate Router Name");
                }
            }
            return new Machine(_alphabet, rotors, pawls, allRotors);
        } catch (NoSuchElementException excp) {
            throw error("configuration file truncated");
        }
    }

    /** Return a rotor, reading its description from _config. */
    private Rotor readRotor() {
        try {
            String name, type, notches = "";
            if (_config.hasNext()) {
                name = _config.next();
            } else {
                throw error("Invalid rotor input");
            }

            if (_config.hasNext()) {
                type = _config.next();
            } else {
                throw error("Bad rotor type");
            }

            if (type.charAt(0) == 'M') {
                for (int i = 1; i < type.length(); i++) {
                    notches += type.charAt(i);
                }
            } else if (type.charAt(0) == 'N') {
                if (type.length() > 1) {
                    throw error("Non moving rotors can't have notches");
                }

            } else if (type.charAt(0) == 'R') {
                if (type.length() > 1) {
                    throw error("Reflectors can't have notches");
                }
            }
            String perm = "";

            while (_config.hasNext("\\(.*\\)")) {
                perm += _config.next();
            }
            Permutation permm = new Permutation(perm, _alphabet);

            if (type.charAt(0) == 'M') {
                return new MovingRotor(name, permm, notches);
            } else if (type.charAt(0) == 'R') {
                return new Reflector(name, permm);
            } else {
                return new FixedRotor(name, permm);
            }


        } catch (NoSuchElementException excp) {
            throw error("bad rotor description");
        }
    }

    /** Set M according to the specification given on SETTINGS,
     *  which must have the format specified in the assignment TEMP is
     *  thing it reads from.  */
    private void setUp(Machine M, Scanner temp) {

        temp.next();
        String [] names = new String[M.numRotors()];
        for (int i = 0; i < M.numRotors(); i++) {
            names[i] = temp.next();
        }

        for (int i = 0; i < names.length - 1; i++) {
            for (int j = i + 1; j < names.length; j++) {
                if (names[i].equals(names[j])) {
                    throw error("repeated rotor");
                }
            }
        }

        M.insertRotors(names);

        String settings = temp.next();


        String perm = "";

        while (temp.hasNext("\\(.*\\)")) {
            perm += temp.next();
        }
        Permutation permm = new Permutation(perm, _alphabet);

        M.setPlugboard(permm);
        M.setRotors(settings);
    }

    /** Return true iff verbose option specified. */
    static boolean verbose() {
        return _verbose;
    }

    /** Print MSG in groups of five (except that the last group may
     *  have fewer letters). */
    private void printMessageLine(String msg) {
        String output = "";
        for (int i = 0; i < msg.length(); i++) {
            output += msg.charAt(i);
            if (output.length() == 5 || i == msg.length() - 1) {
                _output.print(output);
                _output.print(" ");
                output = "";
            }
        }
        System.out.println();
    }

    /** Alphabet used in this machine. */
    private Alphabet _alphabet;

    /** Source of input messages. */
    private Scanner _input;

    /** Source of machine configuration. */
    private Scanner _config;

    /** File for encoded/decoded messages. */
    private PrintStream _output;

    /** name of da rotors. */
    private ArrayList<String> _rotorNames;

    /** True if --verbose specified. */
    private static boolean _verbose;
}
