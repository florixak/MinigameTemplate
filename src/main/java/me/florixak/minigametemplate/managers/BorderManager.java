package me.florixak.minigametemplate.managers;

import me.florixak.uhcrevamp.game.GameValues;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.WorldBorder;

public class BorderManager {

	private WorldBorder wb;

	public BorderManager() {
	}

	public void setBorder() {
		this.wb = Bukkit.getWorld(GameValues.WORLD_NAME).getWorldBorder();
		this.wb.setCenter(0, 0);
		this.wb.setSize(GameValues.BORDER.INIT_SIZE);
		this.wb.setDamageAmount(GameValues.BORDER.BORDER_DAMAGE);
		this.wb.setWarningDistance(10);
	}

	public void removeBorder() {
		this.wb.setSize(0);
	}

	public void setSize(final double size) {
		this.wb.setSize(size);
	}

	public void setSize(final double size, final int countdown) {
		this.wb.setSize(size, countdown);
	}

	public double getSize() {
		return this.wb.getSize();
	}

	public double getMaxSize() {
		return GameValues.BORDER.INIT_SIZE;
	}

//	public double getSpeed() {
//		return GameValues.BORDER.BORDER_SPEED;
//	}

	public boolean exists() {
		return this.wb != null && this.wb.getSize() == getMaxSize();
	}

	public boolean isInBorder(final Location loc) {
		if (loc.getX() > getSize() && loc.getZ() > getSize()) return true;
		return false;
	}

	public void checkBorder() {
		if (!exists()) setBorder();
	}

	public void startShrinking(final double size, final int countdown) {
		setSize(size, countdown);
	}

	public void stopShrinking() {
		setSize(getSize());
	}
}