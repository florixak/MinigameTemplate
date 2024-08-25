package me.florixak.minigametemplate.utils.text;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextUtils {

	private static final int CENTER_PX = 154;
	private static final Pattern HEX_PATTERN = Pattern.compile("#<([A-Fa-f0-9]){6}>");
	public static boolean HEX_USE = false;

	/**
	 * Colors a message using ChatColor codes.
	 * If HEX_USE is true, it will also replace hex color codes.
	 *
	 * @param message The message to color.
	 * @return The colored message.
	 */
	public static String color(String message) {
		if (HEX_USE) {
			Matcher matcher = HEX_PATTERN.matcher(message);

			while (matcher.find()) {
				String hexString = matcher.group();

				hexString = "#" + hexString.substring(2, hexString.length() - 1);

				final ChatColor hex = ChatColor.valueOf(hexString);
				final String before = message.substring(0, matcher.start());
				final String after = message.substring(matcher.end());

				message = before + hex + after;
				matcher = HEX_PATTERN.matcher(message);
			}
		}

		return ChatColor.translateAlternateColorCodes('&', message);
	}

	/**
	 * Colors a list of messages using ChatColor codes.
	 *
	 * @param messages The list of messages to color.
	 * @return The list of colored messages.
	 */
	public static List<String> color(final List<String> messages) {
		final List<String> colored = new ArrayList<>();
		for (final String message : messages) {
			colored.add(color(message));
		}
		return colored;
	}

	/**
	 * Removes color codes from a message.
	 *
	 * @param message The message to remove color from.
	 * @return The message without color codes.
	 */
	public static String removeColor(final String message) {
		return ChatColor.stripColor(message);
	}

	/**
	 * Converts a string to normal text with the first letter capitalized.
	 *
	 * @param text The text to convert.
	 * @return The converted text.
	 */
	public static String toNormalText(final String text) {
		return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
	}

	/**
	 * Converts a string with underscores to normal camel case text.
	 *
	 * @param text The text to convert.
	 * @return The converted text.
	 */
	public static String toNormalCamelText(final String text) {
		final String[] words = text.split("_");
		final StringBuilder sb = new StringBuilder();
		for (final String word : words) {
			sb.append(word.substring(0, 1).toUpperCase());
			sb.append(word.substring(1).toLowerCase());
			sb.append(" ");
		}
		final String newText = sb.toString().trim();
		return newText;
	}

	/**
	 * Removes special characters from a string.
	 *
	 * @param text The text to process.
	 * @return The text without special characters.
	 */
	public static String removeSpecialCharacters(final String text) {
		return text.replaceAll("[^a-zA-Z0-9]", "");
	}

	/**
	 * Converts a string to camel case text.
	 *
	 * @param text The text to convert.
	 * @return The converted text.
	 */
	public static String toCamelCaseText(final String text) {
		final String[] words = text.split(" ");
		final StringBuilder sb = new StringBuilder();
		for (final String word : words) {
			sb.append(word.substring(0, 1).toUpperCase());
			sb.append(" ");
		}
		final String newText = sb.toString().trim();
		return newText;
	}

	/**
	 * Removes spaces from a string.
	 *
	 * @param text The text to process.
	 * @return The text without spaces.
	 */
	public static String removeSpaces(final String text) {
		return text.replaceAll(" ", "");
	}

	/**
	 * Centers a message for display in chat.
	 *
	 * @param message The message to center.
	 * @return The centered message.
	 */
	public static String getCenteredMessage(String message) {
		if (message == null || message.equals("")) return "";

		message = ChatColor.translateAlternateColorCodes('&', message);
		message = message.replace("<center>", "").replace("</center>", "");

		int messagePxSize = 0;
		boolean previousCode = false;
		boolean isBold = false;

		for (final char c : message.toCharArray()) {
			if (c == '�') {
				previousCode = true;

			} else if (previousCode) {
				previousCode = false;
				if (c == 'l' || c == 'L') {
					isBold = true;
				} else isBold = false;

			} else {
				final DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);
				messagePxSize += isBold ? dFI.getBoldLength() : dFI.getLength();
				messagePxSize++;
			}
		}

		final int halvedMessageSize = messagePxSize / 2;
		final int toCompensate = CENTER_PX - halvedMessageSize;
		final int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;
		int compensated = 0;
		final StringBuilder sb = new StringBuilder();
		while (compensated < toCompensate) {
			sb.append(" ");
			compensated += spaceLength;
		}

		return sb.toString() + message;
	}

	/**
	 * Joins an array of strings into a single string starting from a given index.
	 *
	 * @param index The starting index.
	 * @param args  The array of strings to join.
	 * @return The joined string.
	 */
	public static String joinString(final int index, final String[] args) {
		final StringBuilder builder = new StringBuilder();
		for (int i = index; i < args.length; i++) {
			builder.append(args[i]).append(" ");
		}
		return builder.toString();
	}

	/**
	 * Gets a Color object from a string representation.
	 *
	 * @param s The string representation of the color.
	 * @return The Color object, or null if the color is not recognized.
	 */
	public static Color getColor(final String s) {
		switch (s.toUpperCase()) {
			case "AQUA":
				return Color.AQUA;
			case "BLACK":
				return Color.BLACK;
			case "BLUE":
				return Color.BLUE;
			case "FUCHSIA":
				return Color.FUCHSIA;
			case "GRAY":
				return Color.GRAY;
			case "GREEN":
				return Color.GREEN;
			case "LIME":
				return Color.LIME;
			case "MAROON":
				return Color.MAROON;
			case "NAVY":
				return Color.NAVY;
			case "OLIVE":
				return Color.OLIVE;
			case "ORANGE":
				return Color.ORANGE;
			case "PURPLE":
				return Color.PURPLE;
			case "RED":
				return Color.RED;
			case "SILVER":
				return Color.SILVER;
			case "TEAL":
				return Color.TEAL;
			case "WHITE":
				return Color.WHITE;
			case "YELLOW":
				return Color.YELLOW;
			default:
				return null;
		}
	}

	/**
	 * Formats a number to a string with zero decimal places.
	 *
	 * @param number The number to format.
	 * @return The formatted string.
	 */
	public static String formatToZeroDecimal(final double number) {
		return String.format("%.0f", number);
	}

	/**
	 * Formats a number to a string with one decimal place.
	 *
	 * @param number The number to format.
	 * @return The formatted string.
	 */
	public static String formatToOneDecimal(final double number) {
		return String.format("%.1f", number);
	}

	/**
	 * Formats a number to a string with two decimal places.
	 *
	 * @param number The number to format.
	 * @return The formatted string.
	 */
	public static String formatToTwoDecimals(final double number) {
		return String.format("%.2f", number);
	}
}