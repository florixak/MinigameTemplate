package me.florixak.minigametemplate.commands;

import eu.decentsoftware.holograms.api.utils.PAPI;
import me.florixak.minigametemplate.config.Messages;
import me.florixak.minigametemplate.game.Permissions;
import me.florixak.minigametemplate.game.arena.Arena;
import me.florixak.minigametemplate.game.player.GamePlayer;
import me.florixak.minigametemplate.game.teams.GameTeam;
import me.florixak.minigametemplate.gui.menu.lobby.AvailableArenasMenu;
import me.florixak.minigametemplate.managers.GameManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class ArenaCommand implements CommandExecutor, TabCompleter {

	private final GameManager gameManager;

	public ArenaCommand(final GameManager gameManager) {
		this.gameManager = gameManager;
	}

	@Override
	public boolean onCommand(@NotNull final CommandSender sender, @NotNull final Command command, @NotNull final String label, @NotNull final String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(PAPI.setPlaceholders(null, Messages.ONLY_PLAYER.toString()));
			return true;
		}
		handleArenaArgument((Player) sender, args);
		return true;
	}

	private void handleArenaArgument(final Player player, final String[] args) {
		if (!player.hasPermission(Permissions.SETUP.getPerm())) {
			player.sendMessage(PAPI.setPlaceholders(player, Messages.NO_PERM.toString()));
			return;
		}

		final GamePlayer gamePlayer = this.gameManager.getPlayerManager().getGamePlayer(player.getUniqueId());

		if (args.length == 0) {
			new AvailableArenasMenu(this.gameManager.getMenuManager().getMenuUtils(gamePlayer)).open();
			return;
		}

		if (args[0].equalsIgnoreCase("create")) {
			if (args.length != 4) {
				player.sendMessage(PAPI.setPlaceholders(player, Messages.UHC_PLAYER_HELP.toString()));
				return;
			}
			try {
				final String arenaId = args[1];
				final String arenaName = args[2];
				final int arenaMin = Integer.parseInt(args[3]);
				this.gameManager.getArenaManager().createArena(arenaId, arenaName, player.getLocation(), arenaMin);
				player.sendMessage("Arena " + arenaName + " created.");
			} catch (final Exception e) {
				player.sendMessage(PAPI.setPlaceholders(player, Messages.UHC_PLAYER_HELP.toString()));
			}
			return;
		}

		final Arena arena = this.gameManager.getArenaManager().getArena(args[0]);
		final String parameter = args[1];

		if (parameter.equalsIgnoreCase("delete")) {
			player.sendMessage("Arena " + arena.getName() + " deleted.");
			this.gameManager.getArenaManager().deleteArena(arena);

		} else if (parameter.equalsIgnoreCase("enable")) {
			if (!this.gameManager.getArenaManager().enableArena(player, arena)) return;
			player.sendMessage("Arena " + arena.getName() + " enabled.");

		} else if (parameter.equalsIgnoreCase("disable")) {
			this.gameManager.getArenaManager().disableArena(arena);
			player.sendMessage("Arena " + arena.getName() + " disabled.");

		} else if (parameter.equalsIgnoreCase("start")) {
			this.gameManager.getArenaManager().startArena(arena);
			player.sendMessage("Arena " + arena.getName() + " started.");

		} else if (parameter.equalsIgnoreCase("stop")) {
//			this.gameManager.getArenaManager().end(arena);
			player.sendMessage("Arena " + arena.getName() + " stopped.");

		} else if (parameter.equalsIgnoreCase("team")) {
			handleTeam(player, arena, args);
			player.sendMessage("Team " + args[2] + " added to arena " + arena.getName());

		} else if (parameter.equalsIgnoreCase("waiting")) {
			handleWaitingLocation(player, arena, args);

		} else if (parameter.equalsIgnoreCase("ending")) {
			handleEndingLocation(player, arena, args);

		} else {
			player.sendMessage(PAPI.setPlaceholders(player, Messages.UHC_PLAYER_HELP.toString()));
		}
	}

	private void handleTeam(final Player player, final Arena arena, final String... args) {
		if (arena.isEnabled()) {
			player.sendMessage("Arena is enabled, disable it first.");
			return;
		}
		final String parameter = args[2];
		if (parameter.equalsIgnoreCase("add")) {
			if (args.length != 5) {
				player.sendMessage(PAPI.setPlaceholders(player, Messages.UHC_PLAYER_HELP.toString()));
				return;
			}
			try {
				final String teamName = args[3];
				final int teamSize = Integer.parseInt(args[4]);
				arena.addTeam(new GameTeam(teamName, teamSize, player.getLocation()));
			} catch (final NumberFormatException e) {
				player.sendMessage(PAPI.setPlaceholders(player, Messages.UHC_PLAYER_HELP.toString()));
			}

		} else if (parameter.equalsIgnoreCase("remove")) {
			if (args.length != 4) {
				player.sendMessage(PAPI.setPlaceholders(player, Messages.UHC_PLAYER_HELP.toString()));
				return;
			}
			final GameTeam team = arena.getTeam(args[3]);
			player.sendMessage("Team " + team.getName() + " removed from arena " + arena.getName());
			arena.getTeams().remove(team);
		} else {
			player.sendMessage(PAPI.setPlaceholders(player, Messages.UHC_PLAYER_HELP.toString()));
		}
	}

	private void handleWaitingLocation(final Player player, final Arena arena, final String... args) {
		if (arena.isEnabled()) {
			player.sendMessage("Arena is enabled, disable it first.");
			return;
		}
		final String parameter2 = args[1];
		if (parameter2.equalsIgnoreCase("set")) {
			arena.setWaitingLocation(player.getLocation());
			player.sendMessage("Lobby set for arena " + arena.getName());
		} else if (parameter2.equalsIgnoreCase("remove")) {
			arena.setWaitingLocation(null);
			player.sendMessage("Lobby removed for arena " + arena.getName());
		} else {
			player.sendMessage(PAPI.setPlaceholders(player, Messages.UHC_PLAYER_HELP.toString()));
		}
	}

	private void handleEndingLocation(final Player player, final Arena arena, final String... args) {
		if (arena.isEnabled()) {
			player.sendMessage("Arena is enabled, disable it first.");
			return;
		} else {

		}
		final String parameter2 = args[1];
		if (parameter2.equalsIgnoreCase("set")) {
			arena.setEndingLocation(player.getLocation());
			player.sendMessage("End location set for arena " + arena.getName());
		} else if (parameter2.equalsIgnoreCase("remove")) {
			arena.setEndingLocation(null);
			player.sendMessage("End location removed for arena " + arena.getName());
		} else {
			player.sendMessage(PAPI.setPlaceholders(player, Messages.UHC_PLAYER_HELP.toString()));
		}
	}

	@Nullable
	@Override
	public List<String> onTabComplete(@NotNull final CommandSender sender, @NotNull final Command command, @NotNull final String label, @NotNull final String[] args) {
		if (args.length == 1) {
			return Arrays.asList("create", "delete", "enable", "disable", "start", "stop", "team", "waiting", "ending");
		}
		if (args.length == 2) {
			if (args[0].equalsIgnoreCase("team")) {
				return Arrays.asList("add", "remove");
			}
			if (args[0].equalsIgnoreCase("waiting") || args[0].equalsIgnoreCase("ending")) {
				return Arrays.asList("set", "remove");
			}
		}
		return null;
	}
}
