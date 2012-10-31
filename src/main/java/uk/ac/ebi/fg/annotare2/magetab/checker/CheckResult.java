package uk.ac.ebi.fg.annotare2.magetab.checker;

/**
 * @author Olga Melnichuk
 */
public class CheckResult {

    private final CheckResultType type;

    private final CheckModality modality;

    private final String message;

    private final String title;

    private final Exception exception;

    private CheckResult(String title, CheckModality modality, CheckResultType type, String message, Exception exeption) {
        this.title = title;
        this.type = type;
        this.modality = modality;
        this.message = message;
        this.exception = exeption;
    }

    public static CheckResult checkSucceeded(String checkTitle) {
        return new CheckResult(checkTitle, null, CheckResultType.CHECK_SUCCESS, null, null);
    }

    public static CheckResult checkFailed(String checkTitle, CheckModality checkModality, String message) {
        return new CheckResult(checkTitle, checkModality, CheckResultType.CHECK_FAILURE, message, null);
    }

    public static CheckResult checkBroken(String checkTitle, CheckModality checkModality, Exception e) {
        return new CheckResult(checkTitle, checkModality, CheckResultType.RUN_ERROR, "an exception were thrown during the check run", e);
    }

    @Override
    public String toString() {
        return "CheckResult{" +
                "type=" + type +
                ", modality=" + modality +
                ", message='" + message + '\'' +
                ", title='" + title + '\'' +
                ", exception=" + exception +
                '}';
    }
}
