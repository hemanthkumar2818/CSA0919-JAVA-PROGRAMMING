import java.io.IOException;

class ExamException extends Exception {
    ExamException(String message) { super(message); }
    ExamException(String message, Throwable cause) { super(message, cause); }
}
class SubmissionStorageException extends ExamException {
    SubmissionStorageException(String message, Throwable cause) { super(message, cause); }
}
class ExamWindowClosedException extends ExamException {
    ExamWindowClosedException(String message) { super(message); }
}
class StudentNotRegisteredException extends ExamException {
    StudentNotRegisteredException(String message) { super(message); }
}
class Storage {
    void save(String fileName, long sizeKb) throws SubmissionStorageException {
        try {
            if (sizeKb > 500) throw new IOException("Disk quota exceeded");
            System.out.println("Saved " + fileName);
        } catch (IOException e) {
            // TODO 1 completed: preserve the original IOException as the cause.
            throw new SubmissionStorageException("Could not store " + fileName, e);
        }
    }
}
class ExamService {
    private final Storage storage = new Storage();
    void submit(String studentId, boolean registered, boolean examOpen,
                String fileName, long sizeKb) throws ExamException {
        if (!examOpen) throw new ExamWindowClosedException("Exam window has closed");
        if (!registered) throw new StudentNotRegisteredException("Student is not registered");
        storage.save(fileName, sizeKb);
    }
}
public class StorageDemo {
    public static void main(String[] args) {
        ExamService service = new ExamService();
        String[][] cases = {
            {"valid", "true", "true", "answer01.pdf", "120"},
            {"oversize", "true", "true", "answer02.pdf", "800"},
            {"closed", "true", "false", "answer03.pdf", "100"},
            {"unregistered", "false", "true", "answer04.pdf", "100"}
        };
        for (String[] c : cases) {
            try {
                service.submit(c[0], Boolean.parseBoolean(c[1]), Boolean.parseBoolean(c[2]),
                               c[3], Long.parseLong(c[4]));
            } catch (ExamException e) {
                System.out.println("Error: " + e.getMessage());
                if (e.getCause() != null) System.out.println("Cause: " + e.getCause().getMessage());
            }
        }
    }
}
