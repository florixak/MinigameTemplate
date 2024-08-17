package me.florixak.minigametemplate.hooks;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

public class PlaceholderExp extends PlaceholderExpansion {

	@Override
	public String getIdentifier() {
		return "minigametemplate";
	}

	@Override
	public String getAuthor() {
		return "FloriXak";
	}

	@Override
	public String getVersion() {
		return "1.0";
	}

	@Override
	public boolean persist() {
		return true; // This is required or else PlaceholderAPI will unregister the Expansion on reload
	}

	@Override
	public boolean canRegister() {
		return true;
	}

	@Override
	public String onPlaceholderRequest(final Player p, final String params) {
		final String placeholder = params.toLowerCase();

		if (p != null) {
			if (placeholder.equals("player")) {
				return p.getName();
			}
		}
		return null;
	}
}
