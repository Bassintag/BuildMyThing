package bassintag.buildmything.common;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ChatUtil {
	
	private final static String PREFIX = ChatColor.AQUA + "[" + ChatColor.YELLOW + "Build " + ChatColor.WHITE + "My " + ChatColor.YELLOW  + "Thing" + ChatColor.AQUA + "] " + ChatColor.WHITE;
	
	public static void send(Player p, String message){
		p.sendMessage(PREFIX + message);
	}

	public static void broadcast(String message){
		Bukkit.broadcastMessage(PREFIX + message);
	}
}
