package dev.jorel.commandapi.arguments;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.command.CommandSender;

public class DefaultOptionalArgument<T> extends AbstractBukkitOptionalArgument<T, T, DefaultOptionalArgument<T>> {

	private final DefaultValueFunction<T, CommandSender> defaultValue;

	public DefaultOptionalArgument(final Argument<T> base, DefaultValueFunction<T, CommandSender> defaultValue) {
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
	public T getDefaultValue(final CommandSender sender, final CommandArguments previousArgs) throws
																							  CommandSyntaxException,
																							  WrapperCommandSyntaxException {
		return defaultValue.getDefaultValue(sender, previousArgs);
	}

}
