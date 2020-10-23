package org.nrg.xnatx.plugins.rapidViewer.exceptions;

public class AlreadyExistsException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public AlreadyExistsException(String msg) {
		super(msg);
	}

	public AlreadyExistsException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public AlreadyExistsException(Throwable cause) {
		super(cause);
	}
}
