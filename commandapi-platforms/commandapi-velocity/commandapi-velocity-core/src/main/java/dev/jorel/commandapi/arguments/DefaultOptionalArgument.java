package dev.jorel.commandapi.arguments;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.velocitypowered.api.command.CommandSource;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;

public class DefaultOptionalArgument<T> extends AbstractVelocityOptionalArgument<T, T, DefaultOptionalArgument<T>> {

	private final DefaultValueFunction<T, CommandSource> defaultValue;

	public DefaultOptionalArgument(final Argument<T> base, DefaultValueFunction<T, CommandSource> defaultValue) {
		super(base);

		this.defaultValue = defaultValue;
	}

	@Override
	public Class<T> getPrimitiveType() {
		return base.getPrimitiveType();
	}

	@Override
	public <Source> T parseArgument(
		final CommandContext<Source> cmdCtx, final String key, final CommandArguments previousArgs
	) throws CommandSyntaxException {
		return base.parseArgument(cmdCtx, key, previousArgs);
	}

	@Override
	public T getDefaultValue(final CommandSource sender, final CommandArguments previousArgs) throws
																							  CommandSyntaxException,
																							  WrapperCommandSyntaxException {
		return defaultValue.getDefaultValue(sender, previousArgs);
	}

}
