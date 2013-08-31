package bassintag.buildmything.common;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

public class LanguageUtil {
	
	private final BuildMyThing instance;
	
	
	public LanguageUtil(BuildMyThing plugin){
		instance = plugin;
	}
	
	public String get(String path){
		FileConfiguration config = this.instance.getConfig();
		String truePath = "language." + path;
		System.out.println(truePath);
		System.out.println(config.getString("language.p1-set"));
		System.out.println(truePath.trim() == "language.p1-set");
		if(config.contains(truePath.trim())){
			return config.getString(truePath.trim());
		}	else {
			return "null";
		}
	}
	
	public void setLanguage(String language){
		FileConfiguration config = this.instance.getConfig();
		if(language.toLowerCase() == "en"){
			config.set("language.p1-set", "Point 1 set to your actual feet position");
			config.set("language.p2-set", "Point 2 set to your actual feet position");
			config.set("language.spawn-set", "Room spawn location set to your actual feet position");
			config.set("language.room-created", "Room created!");
			config.set("language.room-cannot-create", "Make sure you selected the 2 points of the build zone and the room spawn location");
			config.set("language.room-precize", "You must precize a room name");
			config.set("language.room-doesnt-exist", "This room doesn't exist");
			config.set("language.room-precize", "You must precize a room name");
			config.set("language.player-not-ingame", "You aren't in a game room");
			config.set("language.player-already-ingame", "You are already in a game room");
			config.set("language.player-not-online", "This player isn't online");
			config.set("language.player-not-playing", "This player isn't playing Build My Thing");
			config.set("language.wrong-command", "Unknown sub-command, use\"" + ChatColor.YELLOW + "/bmt help" + ChatColor.RESET + "\"to get a list of commands");
			config.set("language.no-command", "No sub-command, use\"" + ChatColor.YELLOW + "/bmt help" + ChatColor.RESET + "\"to get a list of commands");
			config.set("language.room-list", "Room List:");
			config.set("language.no-command-while-ingame", "Commands are disabled while in-game");
			config.set("language.no-chat-while-builder", "You can't chat while being the builder");
			config.set("language.word-already-found", "You already found the word");
			config.set("language.player-find-word-3points", ChatColor.BOLD + "$player" + ChatColor.RESET + ChatColor.GREEN + " has found the word! " + ChatColor.RESET + "[+3]");
			config.set("language.player-find-word-1point", ChatColor.BOLD + "$player" + ChatColor.RESET + ChatColor.GREEN + " has found the word! " + ChatColor.RESET + "[+1]");
			config.set("language.builder-get-points", ChatColor.BOLD + "$player" + ChatColor.RESET + ChatColor.GREEN + " also earn 2 points!");
		}
	}

}
