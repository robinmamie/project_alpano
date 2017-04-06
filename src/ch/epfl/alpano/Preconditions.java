package ch.epfl.alpano;

/**
 * Interface permettant de lancer facilement IllegalArgumentException avec un
 * éventuel message d'erreur l'accompagnant.
 * 
 * @author Robin Mamie (257234)
 * @author Maxence Jouve (269716)
 */
public interface Preconditions {

    /**
     * Lance une IllegalArgumentException sans message spécifique lorsque
     * l'argument est false.
     * 
     * @param b
     *            Précondition spécifique. Lance une erreur si elle <i>n'est pas
     *            remplie</i>.
     *            
     * @throws IllegalArgumentException
     *             si la condition passée en argument <i>n'est pas remplie</i>.
     */
    static void checkArgument(boolean b) {
        if (!b)
            throw new IllegalArgumentException();
    }

    /**
     * Lance une IllegalArgumentException lorsque la valeur passée en argument
     * est false. L'exception est accompagnée par un texte défini par
     * l'utilisateur.
     * 
     * @param b
     *            Précondition spécifique. Lance une erreur si elle <i>n'est pas
     *            remplie</i>.
     * @param message
     *            Message d'erreur à afficher si la précondition n'est pas
     *            remplie.
     *            
     * @throws IllegalArgumentException
     *             si la condition passée en argument <i>n'est pas remplie</i>.
     */
    static void checkArgument(boolean b, String message) {
        if (!b)
            throw new IllegalArgumentException(message);
    }

}
