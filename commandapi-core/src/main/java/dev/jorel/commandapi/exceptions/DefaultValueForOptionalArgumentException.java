package dev.jorel.commandapi.exceptions;

public class DefaultValueForOptionalArgumentException extends RuntimeException {

	@Override
	public String getMessage() {
		return "Unable to add a default value to an argument which is already optional, or already has a default value.";
	}

}
