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
package dev.jorel.commandapi;

import com.mojang.brigadier.Message;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.chat.BaseComponent;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Represents a suggestion for an argument with a hover tooltip text for that
 * suggestion. This class is to be used with normal suggestion overrides, via
 * the <code>Argument.overrideSuggestionsT</code> method.
 */
public class StringTooltip implements IStringTooltip {

	private final String suggestion;
	private final Message tooltip;



	/**
	 * Constructs a StringTooltip with a suggestion and a string tooltip
	 *
	 * @param suggestion the suggestion to provide to the user
	 * @param tooltip    the string tooltip to show to the user when they hover over the
	 *                   suggestion
	 * @return a StringTooltip representing this suggestion and tooltip
	 */
	public static StringTooltip of(String suggestion, String tooltip) {
		return of(suggestion, Tooltip.toMessage(tooltip));
	}

	/**
	 * Constructs a StringTooltip with a suggestion and a formatted tooltip
	 *
	 * @param suggestion the suggestion to provide to the user
	 * @param tooltip    the formatted tooltip to show to the user when they hover over the
	 *                   suggestion
	 * @return a StringTooltip representing this suggestion and tooltip
	 */
	public static StringTooltip of(String suggestion, Message tooltip) {
		return tooltip == null ? none(suggestion) : new StringTooltip(suggestion, tooltip);
	}

	/**
	 * Constructs a StringTooltip with a suggestion and a formatted bungee text component tooltip
	 *
	 * @param suggestion the suggestion to provide to the user
	 * @param tooltip    the formatted tooltip to show to the user when they hover over the
	 *                   suggestion
	 * @return a StringTooltip representing this suggestion and tooltip
	 */
	public static StringTooltip of(String suggestion, BaseComponent... tooltip) {
		return of(suggestion, Tooltip.toMessage(tooltip));
	}

	/**
	 * Constructs a StringTooltip with a suggestion and a formatted adventure text component tooltip
	 *
	 * @param suggestion the suggestion to provide to the user
	 * @param tooltip    the formatted tooltip to show to the user when they hover over the
	 *                   suggestion
	 * @return a StringTooltip representing this suggestion and tooltip
	 */
	public static StringTooltip of(String suggestion, Component tooltip) {
		return of(suggestion, Tooltip.toMessage(tooltip));
	}

	/**
	 * Constructs a StringTooltip with a suggestion and no tooltip
	 * 
	 * @param suggestion the suggestion to provide to the user
	 * @return a StringTooltip representing this suggestion
	 */
	public static StringTooltip none(String suggestion) {
		return new StringTooltip(suggestion, null);
	}
	
	/**
	 * Constructs an array of {@link StringTooltip} objects from an array of suggestions, and no tooltips
	 *
	 * @param suggestions array of suggestions to provide to the user
	 * @return an array of {@link StringTooltip} objects from the suggestions, with no tooltips
	 */
	public static StringTooltip[] none(String... suggestions) {
		return generate(String::toString, (s, t) -> StringTooltip.none(s), suggestions);
	}

	/**
	 * Constructs an array of {@link StringTooltip} objects from an array of suggestions,
	 * and a function which generates a string tooltip for each suggestion
	 *
	 * @param tooltipGenerator function which returns a string tooltip for the suggestion
	 * @param suggestions array of suggestions to provide to the user
	 * @return an array of {@link StringTooltip} objects from the provided suggestions, with the generated string tooltips
	 */
	public static StringTooltip[] generateStrings(Function<String, String> tooltipGenerator, String... suggestions) {
		return generate(tooltipGenerator, StringTooltip::of, suggestions);
	}

	/**
	 * Constructs an array of {@link StringTooltip} objects from an array of suggestions,
	 * and a function which generates a formatted tooltip for each suggestion
	 *
	 * @param tooltipGenerator function which returns a formatted tooltip for the suggestion
	 * @param suggestions array of suggestions to provide to the user
	 * @return an array of {@link StringTooltip} objects from the provided suggestions, with the generated formatted tooltips
	 */
	public static StringTooltip[] generateMessages(Function<String, Message> tooltipGenerator, String... suggestions) {
		return generate(tooltipGenerator, StringTooltip::of, suggestions);
	}

	/**
	 * Constructs an array of {@link StringTooltip} objects from an array of suggestions,
	 * and a function which generates a formatted tooltip for each suggestion
	 *
	 * @param tooltipGenerator function which returns a formatted tooltip for the suggestion,
	 * as an array of bungee text components
	 * @param suggestions array of suggestions to provide to the user
	 * @return an array of {@link StringTooltip} objects from the provided suggestions,
	 * with the generated formatted tooltips
	 */
	public static StringTooltip[] generateBungeeComponents(Function<String, BaseComponent[]> tooltipGenerator, String... suggestions) {
		return generate(tooltipGenerator, StringTooltip::of, suggestions);
	}

	/**
	 * Constructs an array of {@link StringTooltip} objects from an array of suggestions,
	 * and a function which generates a formatted tooltip for each suggestion
	 *
	 * @param tooltipGenerator function which returns a formatted tooltip for the suggestion,
	 * as an array of adventure text components
	 * @param suggestions array of suggestions to provide to the user
	 * @return an array of {@link StringTooltip} objects from the provided suggestions,
	 * with the generated formatted tooltips
	 */
	public static StringTooltip[] generateAdventureComponents(Function<String, Component> tooltipGenerator, String... suggestions) {
		return generate(tooltipGenerator, StringTooltip::of, suggestions);
	}

	/**
	 * Internal base method for the other generation types
	 *
	 * @param <T> the type of the tooltip
	 * @param tooltipGenerator tooltip generation function
	 * @param tooltipWrapper function which wraps suggestion and tooltip into a {@link StringTooltip} object
	 * @param suggestions array of suggestions to provide to the user
	 * @return an array of {@link StringTooltip} objects from the provided suggestion, wrapped using the above functions
	 */
	private static <T> StringTooltip[] generate(Function<String, T> tooltipGenerator, BiFunction<String, T, StringTooltip> tooltipWrapper, String... suggestions) {
		StringTooltip[] tooltips = new StringTooltip[suggestions.length];
		for(int i = 0; i < suggestions.length; i++) {
			String suggestion = suggestions[i];
			tooltips[i] = tooltipWrapper.apply(suggestion, tooltipGenerator.apply(suggestion));
		}
		return tooltips;
	}

	private StringTooltip(String suggestion, Message tooltip) {
		this.suggestion = suggestion;
		this.tooltip = tooltip;
	}
	
	/**
	 * Returns the current suggestion that this class holds
	 * @return the current suggestion that this class holds
	 */
	public String getSuggestion() {
		return this.suggestion;
	}
	
	/**
	 * Returns the current tooltip text that this class holds
	 * @return the current tooltip text that this class holds
	 */
	public Message getTooltip() {
		return this.tooltip;
	}
	
}
