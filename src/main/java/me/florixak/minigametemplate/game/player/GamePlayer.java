package me.florixak.minigametemplate.game.player;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XPotion;
import lombok.Getter;
import me.florixak.minigametemplate.MinigameTemplate;
import me.florixak.minigametemplate.game.arena.Arena;
import me.florixak.minigametemplate.managers.GameManager;
import me.florixak.minigametemplate.utils.NMSUtils;
import me.florixak.minigametemplate.utils.text.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;

import java.util.Objects;
import java.util.UUID;

@Getter
public class GamePlayer {

	private final GameManager gameManager = GameManager.getInstance();

	private final UUID uuid;
	private final String name;

	public GamePlayer(final UUID uuid, final String name) {
		this.uuid = uuid;
		this.name = name;

	}

	public Player getPlayer() {
		return Bukkit.getPlayer(this.uuid);
	}

	public String getName() {
		if (Bukkit.getPlayer(this.name) == null) {
			return this.getData().getName();
		}
		return this.name;
	}

	public boolean isOnline() {
		final Player player = Bukkit.getPlayer(this.uuid);
		return player != null;
	}

	public boolean isLobby() {
		return !isInArena();
	}

	public boolean isInArena() {
		return this.gameManager.getArenaManager().isPlayerInArena(this);
	}

	public Arena getArena() {
		return this.gameManager.getArenaManager().getPlayerArena(this);
	}

	public PlayerArenaData getArenaData() {
		return getArena().getPlayerArenaData(this);
	}

	public PlayerData getData() {
		return this.gameManager.getPlayerDataManager().getPlayerData(this);
	}

	public PlayerQuestData getQuestData() {
		return this.gameManager.getPlayerQuestDataManager().getQuestData(this);
	}

	public boolean hasPermission(final String permission) {
		return getPlayer().hasPermission(permission);
	}

	public void teleport(final Location loc) {
		if (loc == null) return;
		getPlayer().teleport(loc);
	}

	public PlayerInventory getInventory() {
		return getPlayer().getInventory();
	}

	public void clearInventory() {
		getPlayer().getInventory().clear();

		//clear player armor
		final ItemStack[] emptyArmor = new ItemStack[4];
		for (int i = 0; i < emptyArmor.length; i++) {
			emptyArmor[i] = new ItemStack(Material.AIR);
		}
		getPlayer().getInventory().setArmorContents(emptyArmor);
	}

	public void openInventory(final Inventory inventory) {
		getPlayer().openInventory(inventory);
	}

	public void closeInventory() {
		getPlayer().closeInventory();
	}

	public void giveExp(final int exp) {
		getPlayer().giveExp(exp);
	}

	public void addEffect(final XPotion potion, final int duration, final int level) {
		getPlayer().addPotionEffect(Objects.requireNonNull(potion.buildPotionEffect(duration * 20, level), "Cannot create potion from null."));
	}

	public void clearPotions() {
//		getPlayer().getActivePotionEffects().clear();
		for (final PotionEffect effect : getPlayer().getActivePotionEffects()) {
			getPlayer().removePotionEffect(effect.getType());
		}
	}

	public void kick(final String message) {
		if (message == null || message.isEmpty() || !isOnline()) return;
		getPlayer().kickPlayer(TextUtils.color(message));
	}

	public void setGameMode(final GameMode gameMode) {
		getPlayer().setGameMode(gameMode);
	}

	public void sendMessage(final String message) {
		if (message == null || message.isEmpty() || !isOnline()) return;
		getPlayer().sendMessage(TextUtils.color(message));
	}

	public void sendMessage(final String message, final String... replacements) {
		if (message == null || message.isEmpty() || !isOnline() || replacements.length % 2 != 0) return;

		String messageToSend = TextUtils.color(message);
		for (int i = 0; i < replacements.length; i += 2) {
			messageToSend = messageToSend.replace(replacements[i], replacements[i + 1]);
		}
		sendMessage(messageToSend);
	}

	public void sendTitle(final String title, final String subtitle, final int fadeIn, final int stay, final int fadeOut) {
		if (title == null || title.isEmpty() || !isOnline()) return;
		MinigameTemplate.getInstance().getVersionUtils().sendTitle(getPlayer(), TextUtils.color(title), TextUtils.color(subtitle), fadeIn, stay, fadeOut);
	}

	public ItemStack getPlayerHead(final String playerName) {
		final Material type = XMaterial.matchXMaterial(MinigameTemplate.useOldMethods() ? "SKULL_ITEM" : "PLAYER_HEAD").get().parseMaterial();
		final ItemStack item = new ItemStack(type, 1);

		final SkullMeta meta = (SkullMeta) item.getItemMeta();
		if (playerName != null) meta.setDisplayName(TextUtils.color(playerName));
		if (MinigameTemplate.useOldMethods()) {
			item.setDurability((short) 3);
			meta.setOwner(getPlayer().getName());
		} else meta.setOwningPlayer(getPlayer());

		item.setItemMeta(meta);
		return item;
	}

	public int getPing() {
		try {
			return Integer.parseInt(getPlayer().getClass().getMethod("getPing").invoke(getPlayer()).toString());
		} catch (final Exception e) {
			return 0;
		}
	}

	public void sendHotBarMessage(final String message) {
		if (message == null || message.isEmpty() || !isOnline()) return;
		NMSUtils.sendHotBarMessageViaNMS(getPlayer(), TextUtils.color(message));
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof GamePlayer && ((GamePlayer) obj).getUuid().equals(getUuid());
	}

	@Override
	public String toString() {
		return "GamePlayer(uuid=" + this.uuid
				+ ", name=" + this.name
				+ ")";
	}

	@Override
	public int hashCode() {
		return this.uuid.hashCode();
	}
}