package me.florixak.minigametemplate.utils;

import java.util.ArrayList;

public class MathUtils {

	// Arrays for Roman numeral conversion
	private static final String[] UNITS = {"", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX"};
	private static final String[] TENS = {"", "X", "XX", "XXX", "XL", "L", "LX", "LXX", "LXXX", "XC"};
	private static final String[] HUNDREDS = {"", "C", "CC", "CCC", "CD", "D", "DC", "DCC", "DCCC", "CM"};
	private static final String[] THOUSANDS = {"", "M", "MM", "MMM"};

	/**
	 * Converts an integer to a Roman numeral.
	 *
	 * @param number the integer to convert (must be between 1 and 3999)
	 * @return the Roman numeral representation of the number, or "Invalid Roman Number Value" if out of range
	 */
	public static String toRoman(final int number) {
		if (number < 1 || number > 3999) {
			return "Invalid Roman Number Value";
		}
		return THOUSANDS[number / 1000] + HUNDREDS[(number % 1000) / 100] + TENS[(number % 100) / 10] + UNITS[number % 10];
	}

	/**
	 * Calculates the percentage of part out of total as an integer.
	 *
	 * @param total the total value
	 * @param part  the part value
	 * @return the percentage of part out of total
	 */
	public static int getPercentageInt(final int total, final int part) {
		return part * 100 / total;
	}

	/**
	 * Calculates the percentage of part out of total as an integer.
	 *
	 * @param total the total value
	 * @param part  the part value
	 * @return the percentage of part out of total
	 */
	public static int getPercentageDouble(final double total, final double part) {
		return (int) (part * 100 / total);
	}

	/**
	 * Calculates the percentage of part out of total with specified decimal places.
	 *
	 * @param total         the total value
	 * @param part          the part value
	 * @param decimalPlaces the number of decimal places
	 * @return the percentage of part out of total rounded to the specified decimal places
	 */
	public static double getPercentageDouble(final double total, final double part, final int decimalPlaces) {
		return Math.round(part * 100 / total * Math.pow(10, decimalPlaces)) / Math.pow(10, decimalPlaces);
	}

	/**
	 * Floors a double value.
	 *
	 * @param x the value to floor
	 * @return the largest integer less than or equal to the value
	 */
	public static int floor(final double x) {
		final int i = (int) x;
		return x < i ? i - 1 : i;
	}

	/**
	 * Ceils a double value.
	 *
	 * @param x the value to ceil
	 * @return the smallest integer greater than or equal to the value
	 */
	public static int ceil(final double x) {
		final int i = (int) x;
		return x > i ? i + 1 : i;
	}

	/**
	 * Performs a raytrace between two points.
	 *
	 * @param x0 the starting x-coordinate
	 * @param y0 the starting y-coordinate
	 * @param x1 the ending x-coordinate
	 * @param y1 the ending y-coordinate
	 * @return a list of points representing the path of the raytrace
	 */
	public static ArrayList<int[]> raytrace(final int x0, final int y0, final int x1, final int y1) {
		int dx = Math.abs(x1 - x0);
		int dy = Math.abs(y1 - y0);
		int x = x0;
		int y = y0;
		int n = 1 + dx + dy;
		final int x_inc = (x1 > x0) ? 1 : -1;
		final int y_inc = (y1 > y0) ? 1 : -1;
		int error = dx - dy;
		dx *= 2;
		dy *= 2;

		final ArrayList<int[]> path = new ArrayList<>((int) (Math.sqrt(dist2D(x0, y0, x1, y1)) * 2));

		for (; n > 0; --n) {
			path.add(new int[]{x, y});
			if (error > 0) {
				x += x_inc;
				error -= dy;
			} else {
				y += y_inc;
				error += dx;
			}
		}

		return path;
	}

	/**
	 * Calculates the 3D distance between two points.
	 *
	 * @param x1 the x-coordinate of the first point
	 * @param y1 the y-coordinate of the first point
	 * @param z1 the z-coordinate of the first point
	 * @param x2 the x-coordinate of the second point
	 * @param y2 the y-coordinate of the second point
	 * @param z2 the z-coordinate of the second point
	 * @return the 3D distance between the two points
	 */
	public static double dist3D(final double x1, final double y1, final double z1, final double x2, final double y2, final double z2) {
		final double x = java.lang.Math.abs(x1 - x2);
		final double y = java.lang.Math.abs(y1 - y2);
		final double z = java.lang.Math.abs(z1 - z2);
		return (x * x + y * y + z * z);
	}

	/**
	 * Calculates the 2D distance between two points.
	 *
	 * @param x  the x-coordinate of the first point
	 * @param z  the z-coordinate of the first point
	 * @param x2 the x-coordinate of the second point
	 * @param z2 the z-coordinate of the second point
	 * @return the 2D distance between the two points
	 */
	public static double dist2D(final double x, final double z, final double x2, final double z2) {
		final double x3 = java.lang.Math.abs(x - x2);
		final double z3 = java.lang.Math.abs(z - z2);
		return (x3 * x3 + z3 * z3);
	}

	/**
	 * Converts a short value to a byte array.
	 *
	 * @param s the short value to convert
	 * @return the byte array representation of the short value
	 */
	public static byte[] shortToBytes(final short s) {
		return new byte[]{(byte) s, (byte) (s >> 8)};
	}

	/**
	 * Converts two bytes to a short value.
	 *
	 * @param one the first byte
	 * @param two the second byte
	 * @return the short value represented by the two bytes
	 */
	public static short bytesToShort(final byte one, final byte two) {
		return (short) ((one & 0xFF) | ((two & 0xFF) << 8));
	}

	/**
	 * Converts four bytes to an int value.
	 *
	 * @param one   the first byte
	 * @param two   the second byte
	 * @param three the third byte
	 * @param four  the fourth byte
	 * @return the int value represented by the four bytes
	 */
	public static short bytesToInt(final byte one, final byte two, final byte three, final byte four) {
		return (short) ((one & 0xFF) | ((two & 0xFF) << 8) | ((three & 0xFF) << 16) | ((four & 0xFF) << 24));
	}

	/**
	 * Converts an int value to a byte array.
	 *
	 * @param s the int value to convert
	 * @return the byte array representation of the int value
	 */
	public static byte[] intToBytes(final int s) {
		return new byte[]{(byte) s, (byte) (s >> 8), (byte) (s >> 16), (byte) (s >> 24)};
	}

	/**
	 * Converts a long value to an int array.
	 *
	 * @param l the long value to convert
	 * @return the int array representation of the long value
	 */
	public static int[] longToInt(final long l) {
		return new int[]{(int) l, (int) (l >> 32)};
	}

	/**
	 * Converts two int values to a long value.
	 *
	 * @param one the first int value
	 * @param two the second int value
	 * @return the long value represented by the two int values
	 */
	public static long intToLong(final int one, final int two) {
		return ((one & 0xFFFFFFFFL) | ((long) two << 32));
	}

	/**
	 * Converts a short value to chunk coordinates.
	 *
	 * @param jabba the short value to convert
	 * @return an array containing the chunk coordinates
	 */
	public static int[] shortToChunkXYZ(final short jabba) {
		return new int[]{jabba & 0x000F, (jabba & 0xFF00) >> 8, (jabba & 0x00F0) >> 4};
	}

	/**
	 * Converts chunk coordinates to a short value.
	 *
	 * @param wx the x-coordinate
	 * @param wy the y-coordinate
	 * @param wz the z-coordinate
	 * @return the short value representing the chunk coordinates
	 */
	public static short chunkXYZToShort(final int wx, final int wy, final int wz) {
		final int x = (wx % 16 + 16) % 16;
		final int y = wy % 256;
		final int z = (wz % 16 + 16) % 16;
		return (short) (0 | ((x & 0xF) + ((z & 0xF) << 4) + ((y & 0xFF) << 8)));
	}

	/**
	 * Combines RGB values into a single color value.
	 *
	 * @param r the red component (0-255)
	 * @param g the green component (0-255)
	 * @param b the blue component (0-255)
	 * @return the combined color value
	 */
	public static int getColor(final int r, final int g, final int b) {
		return (r << 16) | (g << 8) | b;
	}

	/**
	 * Performs a unidirectional linear interpolation.
	 *
	 * @param x   the interpolation factor
	 * @param x1  the start value
	 * @param x2  the end value
	 * @param q00 the value at the start
	 * @param q01 the value at the end
	 * @return the interpolated value
	 */
	public static float uniLerp(final float x, final float x1, final float x2, final float q00, final float q01) {
		return ((x2 - x) / (x2 - x1)) * q00 + ((x - x1) / (x2 - x1)) * q01;
	}

	/**
	 * Performs a bidirectional linear interpolation.
	 *
	 * @param x   the x-coordinate
	 * @param y   the y-coordinate
	 * @param x1  the start x-coordinate
	 * @param x2  the end x-coordinate
	 * @param y1  the start y-coordinate
	 * @param y2  the end y-coordinate
	 * @param q11 the value at (x1, y1)
	 * @param q12 the value at (x1, y2)
	 * @param q21 the value at (x2, y1)
	 * @param q22 the value at (x2, y2)
	 * @return the interpolated value
	 */
	public static float biLerp(final float x, final float y, final float x1, final float x2, final float y1, final float y2, final float q11, final float q12, final float q21, final float q22) {
		final float r1 = uniLerp(x, x1, x2, q11, q21);
		final float r2 = uniLerp(x, x1, x2, q12, q22);

		return uniLerp(y, y1, y2, r1, r2);
	}

	/**
	 * Performs a trilinear interpolation.
	 *
	 * @param x   the x-coordinate
	 * @param y   the y-coordinate
	 * @param z   the z-coordinate
	 * @param xs  the start x-coordinate
	 * @param xe  the end x-coordinate
	 * @param ys  the start y-coordinate
	 * @param ye  the end y-coordinate
	 * @param zs  the start z-coordinate
	 * @param ze  the end z-coordinate
	 * @param val the values at the corners of the cube
	 * @return the interpolated value
	 */
	public static float triLerp(final float x, final float y, final float z, final float xs, final float xe, final float ys, final float ye, final float zs, final float ze, final float[] val) {
		final float x00 = uniLerp(x, xs, xe, val[0], val[4]);
		final float x10 = uniLerp(x, xs, xe, val[2], val[6]);
		final float x01 = uniLerp(x, xs, xe, val[1], val[5]);
		final float x11 = uniLerp(x, xs, xe, val[3], val[7]);
		final float r0 = uniLerp(y, ys, ye, x00, x01);
		final float r1 = uniLerp(y, ys, ye, x10, x11);

		return uniLerp(z, zs, ze, r0, r1);
	}

	/**
	 * Performs a basic linear interpolation.
	 *
	 * @param amount the interpolation factor
	 * @param a      the start value
	 * @param b      the end value
	 * @return the interpolated value
	 */
	public static double lerp(final double amount, final double a, final double b) {
		return a + amount * (b - a);
	}
}