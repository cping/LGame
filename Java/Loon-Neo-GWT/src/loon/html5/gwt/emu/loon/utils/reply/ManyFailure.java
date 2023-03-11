package loon.utils.reply;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class ManyFailure extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Iterable<Throwable> failures() {
		return _failures;
	}

	public void addFailure(Throwable t) {
		_failures.add(t);
	}

	@Override
	public String getMessage() {
		StringBuilder buf = new StringBuilder();
		for (Throwable failure : _failures) {
			if (buf.length() > 0)
				buf.append(", ");
			buf.append(failure.getClass().getName()).append(": ").append(failure.getMessage());
		}
		return _failures.size() + " failures: " + buf;
	}

	@Override
	public void printStackTrace(PrintStream s) {
		for (Throwable failure : _failures) {
			failure.printStackTrace(s);
		}
	}

	@Override
	public Throwable fillInStackTrace() {
		return this; // no stack trace here
	}

	// this must be non-final so that GWT can serialize it
	protected List<Throwable> _failures = new ArrayList<Throwable>();
}
