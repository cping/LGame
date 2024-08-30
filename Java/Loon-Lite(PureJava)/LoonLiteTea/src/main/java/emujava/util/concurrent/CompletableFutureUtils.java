package emujava.util.concurrent;

public class CompletableFutureUtils {
    /**
     * Ensures that an object reference passed as a parameter to the calling method is not null.
     */
    public static <T> T checkNotNull(T reference) {
        try {
            checkCriticalNotNull(reference);
        }
        catch(Exception e) {
            throw new AssertionError(e);
        }
        return reference;
    }

    public static <T> T checkCriticalNotNull(T reference) {
        if(reference == null) {
            throw new NullPointerException();
        }
        return reference;
    }
}