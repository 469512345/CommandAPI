package dev.jorel.commandapi.arguments;

import com.mojang.brigadier.arguments.ArgumentType;
import com.velocitypowered.api.command.CommandSource;
import dev.jorel.commandapi.VelocityExecutable;

/**
 * The core abstract class for Command API arguments
 *
 * @param <T> The type of the underlying object that this argument casts to
 */
public abstract class Argument<T> extends AbstractArgument<T, Argument<T>, Argument<?>, CommandSource> implements VelocityExecutable<Argument<T>> {
	/**
	 * Constructs an argument with a given NMS/brigadier type.
	 *
	 * @param nodeName the name to assign to this argument node
	 * @param rawType  the NMS or brigadier type to be used for this argument
	 */
	protected Argument(String nodeName, ArgumentType<?> rawType) {
		super(nodeName, rawType);
	}

	@Override
	public Argument<T> instance() {
		return this;
	}

	@Override
	public OptionalArgument<T> asOptional() {
		return new OptionalArgument<>(this);
	}

	@Override
	public DefaultOptionalArgument<T> withDefaultValue(final T defaultValue) {
		return null;
	}

	@Override
	public DefaultOptionalArgument<T> withDefaultValue(
		final DefaultValueFunction<T, CommandSource> defaultValue
	) {
		return null;
	}

}
