/*******************************************************************************
 * Copyright 2018, 2020 Jorel Ali (Skepter) - MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
package dev.jorel.commandapi.executors;

import dev.jorel.commandapi.abstractions.AbstractCommandSender;

import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;

/**
 * The interface for normal command executors
 * @param <T> the commandsender
 */
public interface IExecutorNormal<K, T extends AbstractCommandSender<K>> extends IExecutorTyped<K> {
	
	/**
	 * Executes the command executor with the provided command sender and the provided arguments.
	 * @param sender the command sender for this command
	 * @param args the arguments provided to this command
	 * @return 1 if the command succeeds, 0 if the command fails
	 * @throws WrapperCommandSyntaxException if an error occurs during the execution of this command
	 */
	@Override
	default int executeWith(AbstractCommandSender<K> sender, Object[] args) throws WrapperCommandSyntaxException {
		this.run(sender.getSource(), args);
		return 1;
	}
	
	/**
	 * Executes the command.
	 * @param sender the command sender for this command
	 * @param args the arguments provided to this command
	 * @throws WrapperCommandSyntaxException if an error occurs during the execution of this command
	 */
	void run(K sender, Object[] args) throws WrapperCommandSyntaxException;

}
