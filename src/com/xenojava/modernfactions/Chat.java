package com.xenojava.modernfactions;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Chat {

	public static void messagePlayer(Player p, String message) {

		String prefix = ModernFactions.PREFIX;
		String msg = message;

		p.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + msg));

		return;
	}

}
