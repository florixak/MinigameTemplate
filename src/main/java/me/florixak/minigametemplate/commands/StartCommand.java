package me.florixak.minigametemplate.commands;

import me.florixak.minigametemplate.config.Messages;
import me.florixak.minigametemplate.game.Permissions;
import me.florixak.minigametemplate.game.arena.Arena;
import me.florixak.minigametemplate.game.player.GamePlayer;
import me.florixak.minigametemplate.managers.GameManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class StartCommand implements CommandExecutor {

	private final GameManager gameManager;

	public StartCommand(final GameManager gameManager) {
		this.gameManager = gameManager;
	}

	@Override
	public boolean onCommand(@NotNull final CommandSender sender, @NotNull final Command command, @NotNull final String label, @NotNull final String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(Messages.ONLY_PLAYER.toString());
			return true;
		}

		final Player player = (Player) sender;
		if (!player.hasPermission(Permissions.FORCE_START.getPerm())) {
			player.sendMessage(Messages.NO_PERM.toString());
			return true;
		}
		final GamePlayer gamePlayer = this.gameManager.getPlayerManager().getGamePlayer(player);

		if (!gamePlayer.isInArena()) {
			player.sendMessage(Messages.CANT_USE_NOW.toString());
			return true;
		}

		final Arena arena = gamePlayer.getArena();


		return true;
	}
}
