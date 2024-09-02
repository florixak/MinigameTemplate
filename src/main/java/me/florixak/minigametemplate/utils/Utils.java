package me.florixak.minigametemplate.utils;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import me.florixak.minigametemplate.config.Messages;
import me.florixak.minigametemplate.utils.text.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;

public class Utils {

	public static void broadcast(final String msg) {
		if (msg == null || msg.isEmpty()) return;
		Bukkit.broadcastMessage(TextUtils.color(msg));
	}

	public static void sendHotbarMessage(final Player player, final String message) {
		try {
			final Object chatComponentText = getNMSClass("ChatComponentText").getConstructor(String.class).newInstance(message);
			final Object packetPlayOutChat = getNMSClass("PacketPlayOutChat")
					.getConstructor(getNMSClass("IChatBaseComponent"), byte.class)
					.newInstance(chatComponentText, (byte) 2);
			sendPacket(player, packetPlayOutChat);
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	private static void sendPacket(final Player player, final Object packet) {
		try {
			final Object handle = player.getClass().getMethod("getHandle").invoke(player);
			final Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
			playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, packet);
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	private static Class<?> getNMSClass(final String name) {
		try {
			return Class.forName("net.minecraft.server." + getServerVersion() + "." + name);
		} catch (final ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	private static String getServerVersion() {
		return org.bukkit.Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
	}

	public static void skullTeleport(final Player p, final ItemStack item) {
		if (item.getType() != Material.AIR && item.getType() != null) {
			final SkullMeta meta = (SkullMeta) item.getItemMeta();
			if (meta.getDisplayName() != null) {
				if (Bukkit.getPlayer(meta.getDisplayName()) != null) {
					final Player player = Bukkit.getPlayer(meta.getDisplayName());
					if (player == null) {
						p.closeInventory();
						p.sendMessage(Messages.OFFLINE_PLAYER.toString());
						return;
					}
					p.teleport(player);
				}
			}
		}
	}

	public static Hologram createHologram(final String name, final List<String> text, final Location loc, final boolean save) {
		return DHAPI.createHologram(name, loc, save, text);
	}
}