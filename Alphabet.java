package enigma;
import java.util.ArrayList;
import java.util.HashMap;
import static enigma.EnigmaException.*;

class Alphabet {

    Alphabet(String chars) {

        illegalchars.add('(');
        illegalchars.add(')');
        illegalchars.add('*');

        for (int i = 0; i < chars.length(); i++) {
            char currentChar = chars.charAt(i);

            if (alphabetlistbw.containsKey(currentChar)) {
                throw error("Duplicate Character");
            } else if (illegalchars.contains(currentChar)) {
                throw error("Illegal Character in Alphabet");
            }
            alphabetlist.put(i, currentChar);
            alphabetlistbw.put(currentChar, i);
        }
    }

    Alphabet() {
        this("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
    }

    int size() {
        return alphabetlist.size();
    }

    boolean contains(char ch) {
        if (illegalchars.contains(ch)) {
            throw error("Illegal Character in Alphabet");
        }
        return alphabetlist.containsValue(ch);
    }

    char toChar(int index) {
        if (index < 0 || index >= size()) {
            throw error("Invalid Index in Alphabet");
        }
        return alphabetlist.get(index);
    }

    int toInt(char ch) {
        if (!alphabetlistbw.containsKey(ch)) {
            throw error("Invalid Character in Alphabet");
        }
        return alphabetlistbw.get(ch);
    }

    private HashMap<Integer, Character> alphabetlist =
            new HashMap<Integer, Character>();

    private HashMap<Character, Integer> alphabetlistbw =
            new HashMap<Character, Integer>();

    private ArrayList<Character> illegalchars =
            new ArrayList<Character>();
}
