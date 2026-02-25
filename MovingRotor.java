package enigma;
/*import java.util.ArrayList;
import static enigma.EnigmaException.*;*/

class MovingRotor extends Rotor {

    MovingRotor(String name, Permutation perm, String notches) {
        super(name, perm);
        for (int i = 0; i < notches.length(); i++) {
            addNotch(notches.charAt(i));
        }
    }


    @Override
    void advance() {
        addCurrSetting(1);
    }

    @Override
    String notches() {
        String result = "";
        for (int i = 0; i < getNotches().size(); i++) {
            result += getNotches().get(i);
        }
        return result;
    }
}
