package ch.epfl.alpano;

public interface Preconditions {
    
    static void checkArgument(boolean b) {
        checkArgument(b, "");
    }
    
    static void checkArgument(boolean b, String message) {
        if(!b)
            throw new IllegalArgumentException(message);
    }
    
}
