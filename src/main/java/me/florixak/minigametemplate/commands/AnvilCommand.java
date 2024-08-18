package me.florixak.minigametemplate.commands;


import me.florixak.minigametemplate.MinigameTemplate;
import me.florixak.minigametemplate.config.Messages;
import me.florixak.minigametemplate.game.Permissions;
import me.florixak.minigametemplate.managers.GameManager;
import me.florixak.minigametemplate.versions.VersionUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class AnvilCommand implements CommandExecutor {

	private final GameManager gameManager;

	public AnvilCommand(final GameManager gameManager) {
		this.gameManager = gameManager;
	}

	@Override
	public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
		if (!(sender instanceof Player)) return true;

		final Player p = (Player) sender;

		if (!p.hasPermission(Permissions.ANVIL.getPerm()) && !p.hasPermission(Permissions.VIP.getPerm())) {
			p.sendMessage(Messages.NO_PERM.toString());
			return true;
		}

		if (!this.gameManager.isPlaying() || this.gameManager.isEnding()) {
			p.sendMessage(Messages.CANT_USE_NOW.toString());
			return true;
		}
		try {
			final VersionUtils versionUtils = MinigameTemplate.getInstance().getVersionUtils();
			versionUtils.openAnvil(p);
		} catch (final NoSuchMethodError e) {
			p.sendMessage(Messages.CANT_USE_NOW.toString());
		}

		return true;
	}
}
