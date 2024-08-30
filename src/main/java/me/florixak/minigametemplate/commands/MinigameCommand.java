package me.florixak.minigametemplate.commands;

import eu.decentsoftware.holograms.api.utils.PAPI;
import me.florixak.minigametemplate.config.Messages;
import me.florixak.minigametemplate.game.player.GamePlayer;
import me.florixak.minigametemplate.gui.menu.lobby.AvailableArenasMenu;
import me.florixak.minigametemplate.gui.menu.lobby.InGameArenasMenu;
import me.florixak.minigametemplate.managers.GameManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class MinigameCommand implements CommandExecutor {

	private final GameManager gameManager;

	public MinigameCommand(final GameManager gameManager) {
		this.gameManager = gameManager;
	}

	@Override
	public boolean onCommand(@NotNull final CommandSender sender, @NotNull final Command command, @NotNull final String label, @NotNull final String[] args) {

		if (!(sender instanceof Player)) {
			sender.sendMessage(PAPI.setPlaceholders(null, Messages.ONLY_PLAYER.toString()));
			return true;
		}

		final Player player = (Player) sender;
		final GamePlayer gamePlayer = this.gameManager.getPlayerManager().getGamePlayer(player.getUniqueId());

		if (args.length == 0) {
			player.sendMessage(PAPI.setPlaceholders(player, Messages.UHC_PLAYER_HELP.toString()));
			return true;
		} else if (args[0].equalsIgnoreCase("arenas")) {
			new AvailableArenasMenu(this.gameManager.getMenuManager().getMenuUtils(gamePlayer)).open();

		} else if (args[0].equalsIgnoreCase("ingame")) {
			new InGameArenasMenu(this.gameManager.getMenuManager().getMenuUtils(gamePlayer)).open();
		} else {
			player.sendMessage(PAPI.setPlaceholders(player, Messages.UHC_PLAYER_HELP.toString()));
		}

		return true;
	}

}
