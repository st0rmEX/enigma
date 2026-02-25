package enigma;
import java.util.ArrayList;

import static enigma.EnigmaException.*;


class Rotor {

    Rotor(String name, Permutation perm) {
        _name = name;
        _permutation = perm;
        _currSetting = 0;
    }

    String name() {
        return _name;
    }

    Alphabet alphabet() {
        return _permutation.alphabet();
    }

    Permutation permutation() {
        return _permutation;
    }

    int size() {
        return _permutation.size();
    }

    boolean rotates() {
        return false;
    }

    boolean reflecting() {
        return false;
    }

    int setting() {
        return _currSetting;
    }

    void set(int posn) {
        _currSetting = posn;
    }

    void set(char cposn) {
        _currSetting = alphabet().toInt(cposn);
    }

    int convertForward(int p) {
        int c1index = (p + _currSetting);
        int c2index = _permutation.permute(c1index);
        int result = _permutation.wrap(c2index - _currSetting);
        if (Main.verbose()) {
            System.err.printf("%c -> ", alphabet().toChar(result));
        }
        return result;
    }

    int convertBackward(int e) {
        int c1index = (e + _currSetting);
        int c2index = _permutation.invert(c1index);
        int result = _permutation.wrap(c2index - _currSetting);
        if (Main.verbose()) {
            System.err.printf("%c -> ", alphabet().toChar(result));
        }
        return result;
    }

    String notches() {
        return "";
    }

    boolean atNotch() {
        for (int i = 0; i < notches().length(); i++) {
            if (notches().charAt(i)
                    == _permutation.alphabet().toChar(_currSetting)) {
                return true;
            }
        }
        return false;
    }

    void addNotch(char a) {
        _notches.add(a);
    }

    ArrayList getNotches() {
        return _notches;
    }

    void advance() {
    }

    @Override
    public String toString() {
        return "Rotor " + _name;
    }

    private final String _name;

    void addCurrSetting(int a) {
        _currSetting += a;
        _currSetting = (_currSetting + alphabet().size()) % alphabet().size();
    }

    public boolean isReflector() {
        return false;
    }

    private Permutation _permutation;

    private int _currSetting;

    private ArrayList<Character> _notches = new ArrayList<Character>();

}
