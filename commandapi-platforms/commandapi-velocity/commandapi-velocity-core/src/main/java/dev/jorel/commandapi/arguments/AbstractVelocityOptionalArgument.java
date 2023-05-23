package dev.jorel.commandapi.arguments;

import com.velocitypowered.api.command.CommandSource;
import dev.jorel.commandapi.exceptions.DefaultValueForOptionalArgumentException;
import dev.jorel.commandapi.exceptions.OptionalOptionalArgumentException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AbstractVelocityOptionalArgument<T, B, Impl extends AbstractVelocityOptionalArgument<T, B, Impl>> extends Argument<B> implements AbstractOptionalArgument<B, Impl, Argument<?>, CommandSource> {

	protected final Argument<T> base;
	private final List<AbstractVelocityOptionalArgument<?, ?,? >> combinedArguments = new ArrayList<>();

	public AbstractVelocityOptionalArgument(final Argument<T> base) {
		super(base.getNodeName(), base.getRawType());
		this.base = base;
	}

	@Override
	public CommandAPIArgumentType getArgumentType() {
		return base.getArgumentType();
	}

	/**
	 * Returns a list of arguments linked to this argument.
	 *
	 * @return A list of arguments linked to this argument
	 */
	public List<Argument<?>> getCombinedArguments() {
		return List.copyOf(combinedArguments);
	}

	/**
	 * Returns true if this argument has linked arguments.
	 *
	 * @return true if this argument has linked arguments
	 */
	public boolean hasCombinedArguments() {
		return !combinedArguments.isEmpty();
	}

	/**
	 * Adds combined arguments to this argument. Combined arguments are used to have required arguments after optional
	 * arguments by ignoring they exist until they are added to the arguments array for registration.
	 * <p>
	 * This method also causes permissions and requirements from this argument to be copied over to the arguments you
	 * want to combine this argument with. Their permissions and requirements will be ignored.
	 *
	 * @param combinedArguments The arguments to combine to this argument
	 *
	 * @return this current argument
	 */
	@Override
	public final Impl combineWith(AbstractVelocityOptionalArgument<?, ?, ?>... combinedArguments) {
		Collections.addAll(this.combinedArguments, combinedArguments);
		return (Impl) this;
	}

	@Override
	public OptionalArgument<B> asOptional() {
		throw new OptionalOptionalArgumentException();
	}

	@Override
	public DefaultOptionalArgument<B> withDefaultValue(
		final B defaultValue
	) {
		throw new DefaultValueForOptionalArgumentException();
	}

	@Override
	public DefaultOptionalArgument<B> withDefaultValue(
		final DefaultValueFunction<B, CommandSource> defaultValue
	) {
		throw new DefaultValueForOptionalArgumentException();
	}

}
