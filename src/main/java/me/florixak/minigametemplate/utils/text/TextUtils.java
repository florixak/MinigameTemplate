package me.florixak.minigametemplate.utils.text;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Color;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextUtils {

	private static final int CENTER_PX = 154;
	private static final Pattern HEX_PATTERN = Pattern.compile("#<([A-Fa-f0-9]){6}>");
	public static boolean HEX_USE = false;

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

	public static String removeColor(String message) {
		return ChatColor.stripColor(message);
	}

	public static String toNormalText(String text) {
		return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
	}

	public static String toNormalCamelText(String text) {
		String[] words = text.split("_");
		StringBuilder sb = new StringBuilder();
		for (String word : words) {
			sb.append(word.substring(0, 1).toUpperCase());
			sb.append(word.substring(1).toLowerCase());
			sb.append(" ");
		}
		String newText = sb.toString().trim();
		return newText;
	}

	public static String removeSpecialCharacters(String text) {
		return text.replaceAll("[^a-zA-Z0-9]", "");
	}

	public static String toCamelCaseText(String text) {
		String[] words = text.split(" ");
		StringBuilder sb = new StringBuilder();
		for (String word : words) {
			sb.append(word.substring(0, 1).toUpperCase());
			sb.append(" ");
		}
		String newText = sb.toString().trim();
		return newText;

	}

	public static String removeSpaces(String text) {
		return text.replaceAll(" ", "");
	}

	public static String getCenteredMessage(String message) {
		if (message == null || message.equals("")) return "";

		message = ChatColor.translateAlternateColorCodes('&', message);
		message = message.replace("<center>", "").replace("</center>", "");

		int messagePxSize = 0;
		boolean previousCode = false;
		boolean isBold = false;

		for (char c : message.toCharArray()) {
			if (c == '�') {
				previousCode = true;

			} else if (previousCode) {
				previousCode = false;
				if (c == 'l' || c == 'L') {
					isBold = true;
				} else isBold = false;

			} else {
				DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);
				messagePxSize += isBold ? dFI.getBoldLength() : dFI.getLength();
				messagePxSize++;
			}
		}

		int halvedMessageSize = messagePxSize / 2;
		int toCompensate = CENTER_PX - halvedMessageSize;
		int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;
		int compensated = 0;
		StringBuilder sb = new StringBuilder();
		while (compensated < toCompensate) {
			sb.append(" ");
			compensated += spaceLength;
		}

		return sb.toString() + message;

	}

	public static String joinString(int index, String[] args) {
		StringBuilder builder = new StringBuilder();
		for (int i = index; i < args.length; i++) {
			builder.append(args[i]).append(" ");
		}
		return builder.toString();
	}

	public static Color getColor(String s) {
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

	public static String formatToZeroDecimal(double number) {
		return String.format("%.0f", number);
	}

	public static String formatToOneDecimal(double number) {
		return String.format("%.1f", number);
	}

	public static String formatToTwoDecimals(double number) {
		return String.format("%.2f", number);
	}
}