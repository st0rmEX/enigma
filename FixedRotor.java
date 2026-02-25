package enigma;

import static enigma.EnigmaException.*;

class FixedRotor extends Rotor {
    FixedRotor(String name, Permutation perm) {
        super(name, perm);
    }
}