package enigma;

class EnigmaException extends RuntimeException {

    EnigmaException(String msg) {
        super(msg);
    }

    static EnigmaException error(String msgFormat, Object... arguments) {
        return new EnigmaException(String.format(msgFormat, arguments));
    }

}
