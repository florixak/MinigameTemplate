package me.florixak.minigametemplate.commands;

import eu.decentsoftware.holograms.api.utils.PAPI;
import me.florixak.minigametemplate.config.Messages;
import me.florixak.minigametemplate.game.Permissions;
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
			handleTeamArgument(player, args);

		} else if (args[0].equalsIgnoreCase("arena")) {
			handleArenaArgument(player, args);

		} else if (args[0].equalsIgnoreCase("setSpectate")) {

		} else if (args[0].equalsIgnoreCase("setLobby")) {
			if (args.length != 2) {
				player.sendMessage(PAPI.setPlaceholders(player, Messages.UHC_PLAYER_HELP.toString()));
				return true;
			}

		} else {
			player.sendMessage(PAPI.setPlaceholders(player, Messages.UHC_PLAYER_HELP.toString()));
		}

		return true;
	}

	private void handleArenaArgument(final Player player, final String[] args) {
		if (!player.hasPermission(Permissions.SETUP.getPerm())) {
			player.sendMessage(PAPI.setPlaceholders(player, Messages.NO_PERM.toString()));
			return;
		}

		final GamePlayer gamePlayer = this.gameManager.getPlayerManager().getGamePlayer(player.getUniqueId());

		if (args[1].equalsIgnoreCase("create")) {
			if (args.length != 5) {
				player.sendMessage(PAPI.setPlaceholders(player, Messages.UHC_PLAYER_HELP.toString()));
				return;
			}
			try {
				final String arenaId = args[2];
				final String arenaName = args[3];
				final int arenaMin = Integer.parseInt(args[4]);
				this.gameManager.getArenaManager().createArena(arenaId, arenaName, player.getLocation(), arenaMin);
			} catch (final NumberFormatException e) {
				player.sendMessage(PAPI.setPlaceholders(player, Messages.UHC_PLAYER_HELP.toString()));
			}
			return;
		}
		if (args.length != 3) {
			player.sendMessage(PAPI.setPlaceholders(player, Messages.UHC_PLAYER_HELP.toString()));
			return;
		}
		final Arena arena = this.gameManager.getArenaManager().getArena(args[2]);

		if (arena == null) {
			player.sendMessage("Arena not found.");
			return;
		}

		if (args[1].equalsIgnoreCase("delete")) {

			player.sendMessage("Arena " + arena.getName() + " deleted.");
			this.gameManager.getArenaManager().deleteArena(arena);

		} else if (args[1].equalsIgnoreCase("enable")) {

			if (!this.gameManager.getArenaManager().enableArena(player, arena)) return;
			player.sendMessage("Arena " + arena.getName() + " enabled.");

		} else if (args[1].equalsIgnoreCase("disable")) {
			this.gameManager.getArenaManager().disableArena(arena);
			player.sendMessage("Arena " + arena.getName() + " disabled.");

		} else if (args[1].equalsIgnoreCase("start")) {
			this.gameManager.getArenaManager().startArena(arena);
			player.sendMessage("Arena " + arena.getName() + " started.");

		} else if (args[1].equalsIgnoreCase("stop")) {
//			this.gameManager.getArenaManager().end(arena);
			player.sendMessage("Arena " + arena.getName() + " stopped.");

		} else if (args[1].equalsIgnoreCase("join")) {
			arena.join(gamePlayer);

		} else if (args[1].equalsIgnoreCase("spectate")) {
			arena.joinAsSpectator(gamePlayer);

		} else {
			player.sendMessage(PAPI.setPlaceholders(player, Messages.UHC_PLAYER_HELP.toString()));
		}
	}

	private void handleTeamArgument(final Player player, final String[] args) {
		if (args.length < 2) {
			player.sendMessage(PAPI.setPlaceholders(player, Messages.UHC_PLAYER_HELP.toString()));
			return;
		}

		if (args[1].equalsIgnoreCase("create")) {
			if (args.length != 5) {
				player.sendMessage(PAPI.setPlaceholders(player, Messages.UHC_PLAYER_HELP.toString()));
				return;
			}
			try {
				final Arena arena = this.gameManager.getArenaManager().getArena(args[2]);
				final String teamName = args[3];
				final int teamSize = Integer.parseInt(args[4]);
				arena.addTeam(new GameTeam(teamName, teamSize, player.getLocation()));
			} catch (final NumberFormatException e) {
				player.sendMessage(PAPI.setPlaceholders(player, Messages.UHC_PLAYER_HELP.toString()));
			}
		} else if (args[1].equalsIgnoreCase("delete")) {

		} else {
			player.sendMessage(PAPI.setPlaceholders(player, Messages.UHC_PLAYER_HELP.toString()));
		}
	}
}
