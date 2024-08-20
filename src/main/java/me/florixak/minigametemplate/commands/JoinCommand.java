package me.florixak.minigametemplate.commands;

import eu.decentsoftware.holograms.api.utils.PAPI;
import me.florixak.minigametemplate.config.Messages;
import me.florixak.minigametemplate.game.arena.Arena;
import me.florixak.minigametemplate.game.arena.ArenaManager;
import me.florixak.minigametemplate.game.player.GamePlayer;
import me.florixak.minigametemplate.managers.GameManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class JoinCommand implements CommandExecutor {

	private final GameManager gameManager;
	private final ArenaManager arenaManager;

	public JoinCommand(final GameManager gameManager) {
		this.gameManager = gameManager;
		this.arenaManager = gameManager.getArenaManager();
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
			player.sendMessage("Usage: /join <arena>");
			return true;
		} else if (args[0].equalsIgnoreCase("join")) {
			if (args.length == 2) {
				if (!this.arenaManager.existsArena(args[1])) {
					player.sendMessage("Arena " + args[1] + " does not exist!");
					return true;
				}
				final Arena arena = this.arenaManager.getArena(args[1]);

				if (this.arenaManager.isPlayerInArena(gamePlayer)) {
					final Arena currentArena = this.arenaManager.getPlayerArena(gamePlayer);
					currentArena.leave(gamePlayer);
					currentArena.broadcast(PAPI.setPlaceholders(player, Messages.ARENA_LEAVE.toString().replace("%player%", player.getName())));
				}
				arena.join(gamePlayer);
				arena.broadcast(PAPI.setPlaceholders(player, Messages.ARENA_JOIN.toString().replace("%player%", player.getName())));
				return true;
			} else {
				player.sendMessage("Usage: /join <arena>");
				return true;
			}
		}
		return true;
	}
}
