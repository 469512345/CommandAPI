package dev.jorel.commandapi.arguments;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.command.CommandSender;

import java.util.Optional;

@SuppressWarnings("rawtypes")
public class OptionalArgument<T> extends AbstractBukkitOptionalArgument<T, Optional, OptionalArgument<T>> {

	public OptionalArgument(final Argument<T> base) {
		super(base);
	}

	@Override
	public Class<Optional> getPrimitiveType() {
		return Optional.class;
	}

	@Override
	public <Source> Optional<T> parseArgument(
		final CommandContext<Source> cmdCtx, final String key, final CommandArguments previousArgs
	) throws CommandSyntaxException {
		return Optional.of(base.parseArgument(cmdCtx, key, previousArgs));
	}

	@Override
	public Optional<T> getDefaultValue(final CommandSender sender, final CommandArguments previousArgs) {
		return Optional.empty();
	}

}
