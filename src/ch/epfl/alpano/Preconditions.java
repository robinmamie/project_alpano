package ch.epfl.alpano;


/**
 * Permet de tester une précondition spécifique.
 *  
 * @author Robin Mamie (257234)
 * @author Maxence Jouve (269716)
 */
public interface Preconditions {
    
    
    /**
     * Lance une IllegalArgumentException sans message spécifique
     * lorsque l'argument est false.
     * 
     * @param b
     *          précondition spécifique
     */
    static void checkArgument(boolean b) {
        checkArgument(b, "");
    }
    
    
    /**
     * Lance une IllegalArgumentException avec un certain message
     * lorsque l'argument est false.
     * 
     * @param b
     *          précondition spécifique
     * @param message
     *          message d'erreur à afficher
     */
    static void checkArgument(boolean b, String message) {
        if(!b)
            throw new IllegalArgumentException(message);
    }
    
}
