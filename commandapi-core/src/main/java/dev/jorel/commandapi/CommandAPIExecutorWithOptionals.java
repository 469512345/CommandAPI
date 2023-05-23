package dev.jorel.commandapi;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.jorel.commandapi.arguments.AbstractOptionalArgument;
import dev.jorel.commandapi.commandsenders.AbstractCommandSender;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import dev.jorel.commandapi.executors.ExecutionInfo;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * This class wraps an executor with optional arguments which will take default values in commands
 * @param <CommandSender>
 * @param <WrapperType>
 */
@SuppressWarnings("rawtypes")
public class CommandAPIExecutorWithOptionals<CommandSender, WrapperType extends AbstractCommandSender<? extends CommandSender>> extends CommandAPIExecutor<CommandSender, WrapperType> {

	private final CommandAPIExecutor<CommandSender, WrapperType> originalExecutor;
	private final AbstractOptionalArgument[] optionalArguments;
	private final int optionalArgumentCount;

	public CommandAPIExecutorWithOptionals(CommandAPIExecutor<CommandSender, WrapperType> originalExecutor, AbstractOptionalArgument[] optionalArguments) {
		this.originalExecutor = originalExecutor;
		this.optionalArguments = optionalArguments;
		this.optionalArgumentCount = this.optionalArguments.length;
	}

	@Override
	public int execute(final ExecutionInfo<CommandSender, WrapperType> info) throws CommandSyntaxException {
		CommandArguments original = info.args();

		CommandSender sender = info.sender();

		Object[] originalArray = original.args();

		Map<String, Object> originalMap = original.argsMap();

		int originalCount = originalArray.length;

		Object[] newArray = Arrays.copyOf(originalArray, originalCount + optionalArgumentCount);
		Map<String, Object> newMap = new HashMap<>(originalMap);

		try {
			for(int i = 0; i < optionalArgumentCount; i++) {
				AbstractOptionalArgument argument = optionalArguments[i];
				Object defaultValue = argument.getDefaultValue(sender, original);
				newArray[i + originalCount] = defaultValue;
				newMap.put(argument.getNodeName(), defaultValue);
			}
		} catch(WrapperCommandSyntaxException e) {
			throw e.getException();
		}

		CommandArguments newArgs = new CommandArguments(newArray, newMap, original.getFullInput());

		ExecutionInfo<CommandSender, WrapperType> newInfo = new ExecutionInfo<>() {

			@Override
			public CommandSender sender() {
				return sender;
			}

			@Override
			public WrapperType senderWrapper() {
				return info.senderWrapper();
			}

			@Override
			public CommandArguments args() {
				return newArgs;
			}
		};

		return originalExecutor.execute(newInfo);

	}

}
