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
package dev.jorel.commandapi.arguments;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import dev.jorel.commandapi.exceptions.BadLiteralException;
import dev.jorel.commandapi.nms.NMS;

/**
 * An argument that represents multiple LiteralArguments
 */
public interface MultiLiteralArgumentBase<ImplementedSender> extends IArgumentBase<String, ImplementedSender> {

	@Override
	public default Class<String> getPrimitiveType() {
		return String.class;
	}

	/**
	 * Returns the literals that are present in this argument
	 * @return the literals that are present in this argument
	 */
	public String[] getLiterals();
	
	@Override
	public default CommandAPIArgumentType getArgumentType() {
		return CommandAPIArgumentType.MULTI_LITERAL;
	}
	
	@Override
	public default <CommandListenerWrapper> String parseArgument(NMS<CommandListenerWrapper, ImplementedSender> nms,
			CommandContext<CommandListenerWrapper> cmdCtx, String key) throws CommandSyntaxException {
		throw new IllegalStateException("Cannot parse MultiLiteralArgument");
	}
	
	public static class MultiLiteralArgumentBaseImpl<ImplementedSender>
			extends ArgumentBase<String, ImplementedSender, MultiLiteralArgumentBaseImpl<ImplementedSender>>
			implements MultiLiteralArgumentBase<ImplementedSender> {
		
		String[] literals;

		public MultiLiteralArgumentBaseImpl(final String... literals) {
			super(null, null);
			if(literals == null) {
				throw new BadLiteralException(true);
			}
			if(literals.length == 0) {
				throw new BadLiteralException(false);
			}
			this.literals = literals;
		}

		@Override
		public String[] getLiterals() {
			return this.literals;
		}
		
	}
}
