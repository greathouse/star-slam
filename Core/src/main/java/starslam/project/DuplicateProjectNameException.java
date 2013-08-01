package starslam.project;

public class DuplicateProjectNameException extends RuntimeException {

	public DuplicateProjectNameException() {
		super();
	}

	public DuplicateProjectNameException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public DuplicateProjectNameException(String message, Throwable cause) {
		super(message, cause);
	}

	public DuplicateProjectNameException(String message) {
		super(message);
	}

	public DuplicateProjectNameException(Throwable cause) {
		super(cause);
	}

}
