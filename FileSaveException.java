import java.io.IOException;

class FileSaveException extends IOException {
  public FileSaveException(String message) {
    super(message);
  }

  public FileSaveException(String message, Throwable cause) {
    super(message, cause);
  }
}