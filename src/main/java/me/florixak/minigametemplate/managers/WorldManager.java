package me.florixak.minigametemplate.managers;

import me.florixak.minigametemplate.game.GameValues;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;

import java.io.File;
import java.io.IOException;

public class WorldManager {

	private void setOldGameRules(final World world) {
		world.setGameRuleValue("doDaylightCycle", "false");
		world.setGameRuleValue("announceAdvancements", "false");
		world.setGameRuleValue("doWeatherCycle", "false");
		world.setGameRuleValue("spawnRadius", "0");
	}

	private void removeWorld() {
		try {
			final File world = new File(Bukkit.getWorldContainer(), GameValues.WORLD_NAME);
			FileUtils.deleteDirectory(world);
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	public void createLobbyWorld(final String lobbyName) {
		final WorldCreator c = new WorldCreator(lobbyName);
		c.type(WorldType.FLAT);
		final World world = c.createWorld();
	}

	public boolean doesWorldExist(final String worldName) {
		final File worldFolder = new File(Bukkit.getWorldContainer(), worldName);
		return worldFolder.exists() && worldFolder.isDirectory();
	}

	public void loadWorldIfExists(final String worldName) {
		if (doesWorldExist(worldName) && Bukkit.getWorld(worldName) == null) {
			final WorldCreator creator = new WorldCreator(worldName);
			Bukkit.createWorld(creator);
		} else {
			createLobbyWorld(worldName);
		}
	}

	public boolean worldExists(final String worldName) {
		final World world = Bukkit.getWorld(worldName);
		return world != null;
	}
}