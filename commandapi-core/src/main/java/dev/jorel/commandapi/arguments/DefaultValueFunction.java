package dev.jorel.commandapi.arguments;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;

/**
 * Function signature of the default value generator
 *
 * @param <CommandSender> the command's sender
 * @param <T> type of default argument
 */
public interface DefaultValueFunction<T, CommandSender> {

	T getDefaultValue(CommandSender sender, CommandArguments previousArgs) throws CommandSyntaxException,
																				  WrapperCommandSyntaxException;

}
