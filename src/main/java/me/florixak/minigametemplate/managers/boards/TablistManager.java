package me.florixak.minigametemplate.managers.boards;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import eu.decentsoftware.holograms.api.utils.PAPI;
import me.florixak.minigametemplate.MinigameTemplate;
import me.florixak.minigametemplate.game.GameValues;
import me.florixak.minigametemplate.game.arena.Arena;
import me.florixak.minigametemplate.game.player.GamePlayer;
import me.florixak.minigametemplate.managers.GameManager;
import me.florixak.minigametemplate.utils.text.TextUtils;
import org.bukkit.entity.Player;

public class TablistManager {

	private final GameManager gameManager;

	public TablistManager(final GameManager gameManager) {
		this.gameManager = gameManager;
	}

	public void setPlayerList() {
		if (!GameValues.TABLIST.ENABLED) return;

		for (final GamePlayer gamePlayer : this.gameManager.getPlayerManager().getPlayersInLobby()) {
			final Player player = gamePlayer.getPlayer();
			final String header = GameValues.TABLIST.HEADER;
			final String footer = GameValues.TABLIST.FOOTER;
			final String tablist = GameValues.TABLIST.SOLO_MODE;

			setHeaderAndFooter(player, header, footer);
			setPlayerListName(player, tablist);
		}

		for (final GamePlayer gamePlayer : this.gameManager.getPlayerManager().getPlayersInArenas()) {
			final Arena arena = this.gameManager.getArenaManager().getPlayerArena(gamePlayer);
			for (final GamePlayer player : arena.getPlayers()) {
				final String header = GameValues.TABLIST.HEADER;
				final String footer = GameValues.TABLIST.FOOTER;
				final String tablist = GameValues.TABLIST.TEAM_MODE;

				setHeaderAndFooter(player.getPlayer(), header, footer);
				setPlayerListName(player.getPlayer(), tablist);
			}
		}
	}

	private void setHeaderAndFooter(final Player player, final String header, final String footer) {
		if (!GameValues.ADDONS.CAN_USE_PROTOCOLLIB) return;

		final ProtocolManager pm = MinigameTemplate.getInstance().getProtocolLibHook().getProtocolManager();
		final PacketContainer pc = pm.createPacket(PacketType.Play.Server.PLAYER_LIST_HEADER_FOOTER);

		pc.getChatComponents()
				.write(0, WrappedChatComponent.fromText(TextUtils.color(PAPI.setPlaceholders(player, header))))
				.write(1, WrappedChatComponent.fromText(TextUtils.color(PAPI.setPlaceholders(player, footer))));

		try {
			pm.sendServerPacket(player, pc);
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	private void setPlayerListName(final Player player, final String tablist) {
		player.setPlayerListName(TextUtils.color(PAPI.setPlaceholders(player, tablist)));
	}


}