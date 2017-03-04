package ch.epfl.alpano;


/**
 * Permet de tester une précondition spécifique.
 *  
 * @author Robin Mamie (257234)
 * @author Maxence Jouve (269716)
 */
public interface Preconditions {
    
    
    /**
     * Appelle la méthode plus spécifique en lui envoyant un message vide.
     * 
     * @param b
     *          précondition spécifique
     *          
     * @throws IllegalArgumentException
     *            lorsque le booléen b est faux
     */
    static void checkArgument(boolean b) {
        checkArgument(b, "");
    }
    
    
    /**
     * Lance une IllegalArgumentException avec un certain message lorsque le
     * booléen est false.
     * 
     * @param b
     *          précondition spécifique
     * @param message
     *          message d'erreur à afficher
     *          
     * @throws IllegalArgumentException
     *          lorsque le booléen b est faux
     */
    static void checkArgument(boolean b, String message) {
        if(!b)
            throw new IllegalArgumentException(message);
    }
    
}
