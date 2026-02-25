package enigma;

import static enigma.EnigmaException.*;

class Reflector extends FixedRotor {

    Reflector(String name, Permutation perm) {
        super(name, perm);
    }

    @Override
    void set(int posn) {
        if (posn != 0) {
            throw error("reflector has only one position");
        }
    }

    @Override
    public boolean isReflector() {
        return true;
    }

}
