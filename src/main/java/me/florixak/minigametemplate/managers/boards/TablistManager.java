package me.florixak.minigametemplate.managers.boards;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import me.florixak.minigametemplate.MinigameTemplate;
import me.florixak.minigametemplate.game.GameValues;
import me.florixak.minigametemplate.game.arena.Arena;
import me.florixak.minigametemplate.game.player.GamePlayer;
import me.florixak.minigametemplate.managers.GameManager;
import me.florixak.minigametemplate.utils.PAPIUtils;
import me.florixak.minigametemplate.utils.text.TextUtils;
import org.bukkit.entity.Player;

import java.util.List;

public class TablistManager {

	private final GameManager gameManager;

	private int order = 0;

	public TablistManager(final GameManager gameManager) {
		this.gameManager = gameManager;
	}

	public void setPlayerList(final GamePlayer gamePlayer) {
		final Player player = gamePlayer.getPlayer();
		if (gamePlayer.isLobby()) {
			if (GameValues.TABLIST.LOBBY_ENABLED) setLobbyTablist(player);
		} else if (GameValues.TABLIST.INGAME_ENABLED) {
			setInGameTablist(player);
		} else {
			if (GameValues.TABLIST.LOBBY_ENABLED) setLobbyTablist(player);
		}
	}

	private void setTablist(final Player player, final List<String> header, final String footer, final String tablist) {
		setHeaderAndFooter(player, header, footer);
		setPlayerListName(player, tablist);
	}

	private void setLobbyTablist(final Player player) {
		final List<String> header = GameValues.TABLIST.LOBBY_HEADER;
		final String footer = GameValues.TABLIST.LOBBY_FOOTER;
		final String tablist = GameValues.TABLIST.LOBBY_PLAYER_LIST;
		setTablist(player, header, footer, tablist);
	}

	private void setInGameTablist(final Player player) {
		final List<String> header = GameValues.TABLIST.INGAME_HEADER;
		final String footer = GameValues.TABLIST.INGAME_FOOTER;
		final String tablist = GameValues.TABLIST.INGAME_PLAYER_LIST;
		setTablist(player, header, footer, tablist);
	}

	private void setHeaderAndFooter(final Player player, final List<String> header, final String footer) {
		if (!GameValues.ADDONS.CAN_USE_PROTOCOLLIB) return;

		final ProtocolManager pm = MinigameTemplate.getInstance().getProtocolLibHook().getProtocolManager();
		final PacketContainer pc = pm.createPacket(PacketType.Play.Server.PLAYER_LIST_HEADER_FOOTER);
		final Arena arena = this.gameManager.getArenaManager().getPlayerArena(player);

		if (this.order >= header.size()) this.order = 0;
		pc.getChatComponents()
				.write(0, WrappedChatComponent.fromText(TextUtils.color(PAPIUtils.setPlaceholders(player, arena, header.get(this.order)))))
				.write(1, WrappedChatComponent.fromText(TextUtils.color(PAPIUtils.setPlaceholders(player, arena, footer))));

		try {
			pm.sendServerPacket(player, pc);
			this.order++;
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	private void setPlayerListName(final Player player, final String tablist) {
		final Arena arena = this.gameManager.getArenaManager().getPlayerArena(player);
		player.setPlayerListName(TextUtils.color(PAPIUtils.setPlaceholders(player, arena, tablist)));
	}


}