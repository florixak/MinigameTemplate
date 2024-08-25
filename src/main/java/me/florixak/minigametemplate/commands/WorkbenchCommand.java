package me.florixak.minigametemplate.commands;

import me.florixak.minigametemplate.config.Messages;
import me.florixak.minigametemplate.game.Permissions;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class WorkbenchCommand implements CommandExecutor {

	@Override
	public boolean onCommand(@NotNull final CommandSender sender, @NotNull final Command command, @NotNull final String label, @NotNull final String[] args) {

		if (!(sender instanceof Player)) {
			sender.sendMessage(Messages.ONLY_PLAYER.toString());
			return true;
		}

		final Player player = (Player) sender;

		if (!player.hasPermission(Permissions.WORKBENCH.getPerm())) {
			player.sendMessage(Messages.NO_PERM.toString());
			return true;
		}
		player.openWorkbench(player.getLocation(), true);
		return true;
	}
}
