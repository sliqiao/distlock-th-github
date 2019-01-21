package com.github.distsystem.lock.exception;
public class DistLockException extends RuntimeException {
	public DistLockException() {
	}

	public DistLockException(String message) {
		super(message);
	}

	public DistLockException(Throwable cause) {
		super(cause);
	}

	public DistLockException(String message, Throwable cause) {
		super(message, cause);
	}
}