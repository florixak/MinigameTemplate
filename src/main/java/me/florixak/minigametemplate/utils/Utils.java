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
		Bukkit.broadcastMessage(TextUtils.color(msg));
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