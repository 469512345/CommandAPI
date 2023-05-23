package dev.jorel.commandapi.arguments;

import java.util.List;

public interface AbstractOptionalArgument<DefaultValue, Impl extends AbstractOptionalArgument<DefaultValue, Impl, Argument, CommandSender>, Argument extends AbstractArgument<?, ?, Argument, CommandSender>, CommandSender> extends DefaultValueFunction<DefaultValue, CommandSender> {

	/**
	 * Returns the name of this argument's node
	 *
	 * @return the name of this argument's node
	 */
	String getNodeName();

	/**
	 * Returns a list of arguments linked to this argument.
	 *
	 * @return A list of arguments linked to this argument
	 */
	List<Argument> getCombinedArguments();

	/**
	 * Returns true if this argument has linked arguments.
	 *
	 * @return true if this argument has linked arguments
	 */
	boolean hasCombinedArguments();

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
	Impl combineWith(AbstractOptionalArgument<?, ?, Argument, CommandSender>... combinedArguments);

}
