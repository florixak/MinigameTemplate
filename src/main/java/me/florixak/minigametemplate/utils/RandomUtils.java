package me.florixak.minigametemplate.utils;

import me.florixak.minigametemplate.game.player.GamePlayer;
import org.bukkit.block.BlockFace;

import java.util.List;
import java.util.Random;

/**
 * Utility class for generating random values and selecting random elements.
 */
public class RandomUtils {
	private static final Random r = new Random();

	/**
	 * Returns the shared Random instance.
	 *
	 * @return the shared Random instance
	 */
	public static Random getRandom() {
		return r;
	}

	/**
	 * Generates a random integer between the specified min and max values, inclusive.
	 *
	 * @param min the minimum value
	 * @param max the maximum value
	 * @return a random integer between min and max, inclusive
	 */
	public static int randomInteger(final int min, final int max) {
		final int realMin = Math.min(min, max);
		final int realMax = Math.max(min, max);
		final int exclusiveSize = realMax - realMin;
		return r.nextInt(exclusiveSize + 1) + min;
	}

	/**
	 * Generates a random double between the specified min and max values.
	 *
	 * @param min the minimum value
	 * @param max the maximum value
	 * @return a random double between min and max
	 */
	public static double randomDouble(final double min, final double max) {
		final double realMin = Math.min(min, max);
		final double realMax = Math.max(min, max);
		final double exclusiveSize = realMax - realMin;
		return r.nextDouble(exclusiveSize + 1) + min;
	}

	/**
	 * Generates a random float between the specified min and max values.
	 *
	 * @param min the minimum value
	 * @param max the maximum value
	 * @return a random float between min and max
	 */
	public static double randomFloat(final float min, final float max) {
		final float realMin = Math.min(min, max);
		final float realMax = Math.max(min, max);
		final float exclusiveSize = realMax - realMin;
		return r.nextFloat(exclusiveSize + 1) + min;
	}

	/**
	 * Returns a random BlockFace from a predefined array of possible faces.
	 *
	 * @return a random BlockFace
	 */
	public static BlockFace randomAdjacentFace() {
		final BlockFace[] faces = {BlockFace.DOWN, BlockFace.DOWN, BlockFace.DOWN, BlockFace.UP, BlockFace.UP, BlockFace.UP, BlockFace.EAST, BlockFace.WEST, BlockFace.NORTH, BlockFace.SOUTH};
		return faces[randomInteger(0, faces.length - 1)];
	}

	/**
	 * Returns a random boolean value.
	 *
	 * @return a random boolean value
	 */
	public static boolean randomBoolean() {
		return r.nextBoolean();
	}

	/**
	 * Returns true with a specified chance.
	 *
	 * @param chance the probability of returning true
	 * @return true with the specified chance, false otherwise
	 */
	public static boolean chance(final double chance) {
		return r.nextDouble() <= chance;
	}

	/**
	 * Returns a random element from the specified array.
	 *
	 * @param array the array to select an element from
	 * @param <T>   the type of the array elements
	 * @return a random element from the array
	 */
	public static <T> T randomElement(final T[] array) {
		return array[randomInteger(0, array.length - 1)];
	}

	/**
	 * Returns a random element from the specified list.
	 *
	 * @param onlineList the list to select an element from
	 * @return a random element from the list
	 */
	public static GamePlayer randomOnlinePlayer(final List<GamePlayer> onlineList) {
		return onlineList.get(randomInteger(0, onlineList.size() - 1));
	}

	/**
	 * Returns a random element from the specified array.
	 *
	 * @param array the array to select an element from
	 * @param start the start index of the array
	 * @param end   the end index of the array
	 * @param <T>   the type of the array elements
	 * @return a random element from the array
	 */
	public static <T> T randomElement(final T[] array, final int start, final int end) {
		return array[randomInteger(start, end)];
	}
}