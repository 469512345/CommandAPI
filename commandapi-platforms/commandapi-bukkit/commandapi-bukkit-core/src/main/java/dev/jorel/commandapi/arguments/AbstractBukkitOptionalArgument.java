package dev.jorel.commandapi.arguments;

import dev.jorel.commandapi.exceptions.DefaultValueForOptionalArgumentException;
import dev.jorel.commandapi.exceptions.OptionalOptionalArgumentException;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AbstractBukkitOptionalArgument<T, B, Impl extends AbstractBukkitOptionalArgument<T, B, Impl>> extends Argument<B> implements AbstractOptionalArgument<B, Impl, Argument<?>, CommandSender> {

	protected final Argument<T> base;
	private final List<AbstractBukkitOptionalArgument<?, ?, ?>> combinedArguments = new ArrayList<>();

	public AbstractBukkitOptionalArgument(final Argument<T> base) {
		super(base.getNodeName(), base.getRawType());
		this.base = base;
	}

	@Override
	public CommandAPIArgumentType getArgumentType() {
		return base.getArgumentType();
	}

	public List<Argument<?>> getCombinedArguments() {
		return List.copyOf(combinedArguments);
	}

	public boolean hasCombinedArguments() {
		return !combinedArguments.isEmpty();
	}

	@Override
	public Impl combineWith(final AbstractBukkitOptionalArgument<?, ?, ?>... combinedArguments) {
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
		final DefaultValueFunction<B, CommandSender> defaultValue
	) {
		throw new DefaultValueForOptionalArgumentException();
	}

}
