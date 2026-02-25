package enigma;

import java.util.HashMap;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Iterator;

import static enigma.EnigmaException.*;

class Machine {

    Machine(Alphabet alpha, int numRotors, int pawls,
            Collection<Rotor> allRotors) {

        if (numRotors < 1) {
            throw error("Not Enough Rotors");
        } else if (pawls < 0 || pawls >= numRotors) {
            throw error("Invalid Pawl Count");
        }
        _alphabet = alpha;
        _numRotors = numRotors;
        _numPawls = pawls;

        Iterator<Rotor> rotorList = allRotors.iterator();
        Rotor temp;

        while (rotorList.hasNext()) {
            temp = rotorList.next();
            _allRotors.put(temp.name(), temp);
        }

    }

    int numRotors() {
        return _numRotors;
    }

    int numPawls() {
        return _numPawls;
    }

    Rotor getRotor(int k) {
        if (k >= _numRotors || k < 0) {
            throw error("Invalid Rotor Index");
        }
        return _rotorBoard.get(k);
    }

    Alphabet alphabet() {
        return _alphabet;
    }

    void insertRotors(String[] rotors) {
        String tempname;
        Rotor temprotor;
        _rotorBoard.clear();

        for (int i = 0; i < rotors.length; i++) {
            tempname = rotors[i];
            temprotor = _allRotors.get(tempname);
            if (temprotor == null) {
                throw error("Rotor Name Doesn't Exist");
            }
            _rotorBoard.add(temprotor);
        }
        if (_numRotors > 0 && !_rotorBoard.get(0).isReflector()) {
            throw error("First rotor has to be a reflector");
        }

        for (int i = 1; i < rotors.length; i++) {
            if (_rotorBoard.get(i).isReflector()) {
                throw error("Only first rotor can be reflector");
            }
        }
    }

    void setRotors(String setting) {
        if (setting.length() != _numRotors - 1) {
            throw error("Invalid/Wrong Settings");
        }
        for (int i = 1; i <= setting.length(); i++) {
            _rotorBoard.get(i).set(setting.charAt(i - 1));
        }

    }

    Permutation plugboard() {
        return _plugBoard;
    }

    void setPlugboard(Permutation plugboard) {
        _plugBoard = plugboard;
    }

    int convert(int c) {
        advanceRotors();
        if (Main.verbose()) {
            System.err.printf("[");
            for (int r = 1; r < numRotors(); r += 1) {
                System.err.printf("%c",
                        alphabet().toChar(getRotor(r).setting()));
            }
            System.err.printf("] %c -> ", alphabet().toChar(c));
        }
        c = plugboard().permute(c);
        if (Main.verbose()) {
            System.err.printf("%c -> ", alphabet().toChar(c));
        }
        c = applyRotors(c);
        c = plugboard().permute(c);
        if (Main.verbose()) {
            System.err.printf("%c%n", alphabet().toChar(c));
        }
        return c;
    }

    private void advanceRotors() {
        boolean [] rotate = new boolean[_numRotors];
        boolean [] haspawl = new boolean[_numRotors];

        for (int i = 0; i < _numRotors; i++) {
            rotate[i] = false;
        }

        for (int i = 1; i <= _numRotors; i++) {
            if (i <= numPawls()) {
                haspawl[_numRotors - i] = true;
            } else {
                haspawl[_numRotors - i] = false;
            }
        }

        int lastmotor = _numRotors - 1;

        if (_numPawls > 0) {
            rotate[lastmotor] = true;
        }

        Rotor current;
        for (int i = lastmotor; i > 1; i--) {
            current = _rotorBoard.get(i);
            if (haspawl[i] && haspawl[i - 1] && current.atNotch()) {
                rotate[i] = true;
                rotate[i - 1] = true;
            }
        }

        for (int i = 1; i < _numRotors; i++) {
            if (rotate[i]) {
                getRotor(i).advance();
            }
        }
    }

    private int applyRotors(int c) {
        int current = c;
        Rotor currrotor;
        for (int i = _numRotors - 1; i >= 0; i--) {
            currrotor = getRotor(i);
            current = currrotor.convertForward(current);
        }
        for (int i = 1; i < _numRotors; i++) {
            currrotor = getRotor(i);
            current = currrotor.convertBackward(current);
        }
        return current;
    }

    String convert(String msg) {
        String result = "";
        int current;
        for (int i = 0; i < msg.length(); i++) {
            current = alphabet().toInt(msg.charAt(i));
            result += alphabet().toChar(convert(current));
        }
        return result;
    }

    private final Alphabet _alphabet;

    private HashMap<String, Rotor> _allRotors = new HashMap<String, Rotor>();

    private Permutation _plugBoard;

    private ArrayList<Rotor> _rotorBoard = new ArrayList<Rotor>();

    private int _numPawls;

    private int _numRotors;
}
