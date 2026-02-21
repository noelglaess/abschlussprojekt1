package kit.edu.assignmentone;

/**
 * The main entry point for the application.
 *
 * @author uqhkm
 */
public final class Main {

    private static final String UTILITY_CLASS_ERROR = "Utility classes cannot be instantiated";

    private Main() {
        throw new UnsupportedOperationException(UTILITY_CLASS_ERROR);
    }

    /**
     * The main entry point for the program.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // Hier werden wir später die Kommandozeilen-Argumente parsen,
        // das Game-Model erstellen und die UI (CommandHandler) starten!
        System.out.println("Das Fundament steht!");
    }
}