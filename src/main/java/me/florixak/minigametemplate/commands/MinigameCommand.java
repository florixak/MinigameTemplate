package me.florixak.minigametemplate.commands;

import eu.decentsoftware.holograms.api.utils.PAPI;
import me.florixak.minigametemplate.config.Messages;
import me.florixak.minigametemplate.game.arena.Arena;
import me.florixak.minigametemplate.game.player.GamePlayer;
import me.florixak.minigametemplate.game.teams.GameTeam;
import me.florixak.minigametemplate.gui.menu.InGameArenasMenu;
import me.florixak.minigametemplate.gui.menu.WaitingArenasMenu;
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
		}

		if (args[0].equalsIgnoreCase("leave")) {
			if (!this.gameManager.getArenaManager().isPlayerInArena(gamePlayer)) {
				player.sendMessage(PAPI.setPlaceholders(player, Messages.CANT_USE_NOW.toString()));
				return true;
			}
			this.gameManager.getArenaManager().getPlayerArena(gamePlayer).leave(gamePlayer);
			return true;
		} else if (args[0].equalsIgnoreCase("arenas")) {
			new WaitingArenasMenu(this.gameManager.getMenuManager().getMenuUtils(gamePlayer)).open();
		} else if (args[0].equalsIgnoreCase("ingame")) {
			new InGameArenasMenu(this.gameManager.getMenuManager().getMenuUtils(gamePlayer)).open();
		} else if (args[0].equalsIgnoreCase("create")) {
			if (args.length != 4) {
				player.sendMessage(PAPI.setPlaceholders(player, Messages.UHC_PLAYER_HELP.toString()));
				return true;
			}
			this.gameManager.getArenaManager().createArena(args[1], args[2], player.getLocation(), Integer.parseInt(args[3]));
		} else if (args[0].equalsIgnoreCase("team")) {
			if (args.length < 2) {
				player.sendMessage(PAPI.setPlaceholders(player, Messages.UHC_PLAYER_HELP.toString()));
				return true;
			}
			final Arena arena = this.gameManager.getArenaManager().getArena(args[1]);

			if (args[1].equalsIgnoreCase("create")) {
				if (args.length != 4) {
					player.sendMessage(PAPI.setPlaceholders(player, Messages.UHC_PLAYER_HELP.toString()));
					return true;
				}
				arena.addTeam(new GameTeam(args[2], Integer.parseInt(args[3]), player.getLocation()));
			} else if (args[1].equalsIgnoreCase("delete")) {

			} else {
				player.sendMessage(PAPI.setPlaceholders(player, Messages.UHC_PLAYER_HELP.toString()));
			}

		} else if (args[0].equalsIgnoreCase("start")) {
			if (args.length != 2) {
				player.sendMessage(PAPI.setPlaceholders(player, Messages.UHC_PLAYER_HELP.toString()));
				return true;
			}

		} else if (args[0].equalsIgnoreCase("stop")) {
			if (args.length != 2) {
				player.sendMessage(PAPI.setPlaceholders(player, Messages.UHC_PLAYER_HELP.toString()));
				return true;
			}

		} else if (args[0].equalsIgnoreCase("join")) {
			if (args.length != 2) {
				player.sendMessage(PAPI.setPlaceholders(player, Messages.UHC_PLAYER_HELP.toString()));
				return true;
			}

		} else if (args[0].equalsIgnoreCase("spectate")) {
			if (args.length != 2) {
				player.sendMessage(PAPI.setPlaceholders(player, Messages.UHC_PLAYER_HELP.toString()));
				return true;
			}

		} else if (args[0].equalsIgnoreCase("setLobby")) {

		} else if (args[0].equalsIgnoreCase("setSpectate")) {

		} else if (args[0].equalsIgnoreCase("setSpawn")) {
			if (args.length != 2) {
				player.sendMessage(PAPI.setPlaceholders(player, Messages.UHC_PLAYER_HELP.toString()));
				return true;
			}

		} else if (args[0].equalsIgnoreCase("setCenter")) {
			if (args.length != 2) {
				player.sendMessage(PAPI.setPlaceholders(player, Messages.UHC_PLAYER_HELP.toString()));
				return true;
			}


		} else {
			player.sendMessage(PAPI.setPlaceholders(player, Messages.UHC_PLAYER_HELP.toString()));
		}

		return true;
	}
}
