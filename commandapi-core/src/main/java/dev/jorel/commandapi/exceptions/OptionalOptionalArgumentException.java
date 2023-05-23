package dev.jorel.commandapi.exceptions;

public class OptionalOptionalArgumentException extends RuntimeException {

	@Override
	public String getMessage() {
		return "Cannot call asOptional() on an argument which is already optional";
	}

}
