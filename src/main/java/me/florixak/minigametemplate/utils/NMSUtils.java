package me.florixak.minigametemplate.utils;

import lombok.Getter;
import org.bukkit.Bukkit;

import java.lang.reflect.Method;

public class NMSUtils {

	@Getter
	private static String nmsVer;
	private static boolean oldMethods;

	public static void sendPacket(final Object playerConnection, final Class<?> packetClass, final Object packet) throws Exception {
		final Method sendPacket = playerConnection.getClass().getMethod("sendPacket", packetClass);
		sendPacket.invoke(playerConnection, packet);
	}

	public static Class<?> getNMSClass(final String name) throws ClassNotFoundException {
		return Class.forName("net.minecraft.server.v1_8_R3." + name);
	}

	public static void checkNMSVersion() {
		nmsVer = Bukkit.getServer().getClass().getPackage().getName();
		nmsVer = nmsVer.substring(nmsVer.lastIndexOf(".") + 1);

		if (nmsVer.contains("v1_8") || nmsVer.startsWith("v1_7_")) {
			oldMethods = true;
		}
	}

	public static boolean useOldMethods() {
		return oldMethods;
	}

}
