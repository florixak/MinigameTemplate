package me.florixak.minigametemplate.managers.scoreboard;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import eu.decentsoftware.holograms.api.utils.PAPI;
import me.florixak.minigametemplate.MinigameTemplate;
import me.florixak.minigametemplate.game.GameValues;
import me.florixak.minigametemplate.utils.text.TextUtils;
import org.bukkit.entity.Player;

public class TabManager {

	public TabManager() {
	}

	public void setPlayerList(final Player p) {
		if (!GameValues.TABLIST.ENABLED) return;
		if (p == null) return;

		if (!GameValues.ADDONS.CAN_USE_PROTOCOLLIB) return;

		final ProtocolManager pm = MinigameTemplate.getInstance().getProtocolLibHook().getProtocolManager();
		final PacketContainer pc = pm.createPacket(PacketType.Play.Server.PLAYER_LIST_HEADER_FOOTER);

		pc.getChatComponents()
				.write(0, WrappedChatComponent.fromText(TextUtils.color(GameValues.TABLIST.HEADER)))
				.write(1, WrappedChatComponent.fromText(TextUtils.color(GameValues.TABLIST.FOOTER)));

		try {
			pm.sendServerPacket(p, pc);
		} catch (final Exception e) {
			e.printStackTrace();
		}

		final String tablist = !GameValues.TEAM.TEAM_MODE ? GameValues.TABLIST.SOLO_MODE : GameValues.TABLIST.TEAM_MODE;
		p.setPlayerListName(TextUtils.color(PAPI.setPlaceholders(p, tablist)));
	}
}