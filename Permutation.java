package enigma;

import java.util.HashMap;

import static enigma.EnigmaException.*;

class Permutation {

    Permutation(String cycles, Alphabet alphabet) {
        _alphabet = alphabet;
        cycles = cycles.trim();
        int currindex = 0, firstindex = 0;
        char current;
        boolean parentheses = false;
        while (currindex < cycles.length()) {
            current = cycles.charAt(currindex);
            if (current == '(') {
                if (parentheses) {
                    throw error("Unmatched Parentheses");
                }
                firstindex = currindex + 1;
                parentheses = true;
            } else if (cycles.charAt(currindex) == ')') {
                if (!parentheses) {
                    throw error("Unmatched Parentheses");
                }
                parentheses = false;
                addCycle(cycles.substring(firstindex, currindex));
            }
            currindex += 1;
        }
    }

    private void addCycle(String cycle) {
        char front, back;
        for (int i = 0; i < cycle.length() - 1; i++) {
            front = cycle.charAt(i);
            back = cycle.charAt(i + 1);

            if (forwards.containsKey(front) || forwards.containsValue(back)) {
                throw error("Duplicate Value Mapped");
            }

            forwards.put(front, back);
            backwards.put(back, front);
        }
        forwards.put(cycle.charAt(cycle.length() - 1), cycle.charAt(0));
        backwards.put(cycle.charAt(0), cycle.charAt(cycle.length() - 1));
    }

    final int wrap(int p) {
        int r = p % size();
        if (r < 0) {
            r += size();
        }
        return r;
    }

    int size() {
        return _alphabet.size();
    }

    int permute(int p) {
        char contact = _alphabet.toChar(wrap(p));
        char contact2 = forwards.getOrDefault(contact, contact);
        int c2index = _alphabet.toInt(contact2);
        return c2index;
    }

    int invert(int c) {
        char contact = _alphabet.toChar(wrap(c));
        char contact2 = backwards.getOrDefault(contact, contact);
        int c2index = _alphabet.toInt(contact2);
        return c2index;
    }

    char permute(char p) {
        int c1index = _alphabet.toInt(p);
        int c2index =  permute(c1index);
        char contact2 = _alphabet.toChar(c2index);
        return contact2;
    }

    char invert(char c) {
        int c1index = _alphabet.toInt(c);
        int c2index =  invert(c1index);
        char contact2 = _alphabet.toChar(c2index);
        return contact2;
    }

    Alphabet alphabet() {
        return _alphabet;
    }

    boolean derangement() {
        return forwards.size() == _alphabet.size();
    }

    private Alphabet _alphabet;

    private HashMap<Character, Character> forwards
            = new HashMap<Character, Character>();

    private HashMap<Character, Character> backwards
            = new HashMap<Character, Character>();;
}
