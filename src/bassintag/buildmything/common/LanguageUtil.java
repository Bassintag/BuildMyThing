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
		if(config.contains(truePath.trim())){
			return config.getString(truePath.trim());
		}	else {
			return "null";
		}
	}
	
	public void setLanguage(String language){
		FileConfiguration config = this.instance.getConfig();
		if(language.toLowerCase().startsWith("en")){
			config.set("language.p1-set", "Point 1 set to your actual feet position");
			config.set("language.p2-set", "Point 2 set to your actual feet position");
			config.set("language.spawn-set", "Room spawn location set to your actual feet position");
			config.set("language.room-created", "Room created!");
			config.set("language.room-cannot-create", "Make sure you selected the 2 points of the build zone and the room spawn location");
			config.set("language.room-precize", "You must precize a room name");
			config.set("language.room-doesnt-exist", "This room doesn't exist");
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
			config.set("language.everyone-found", "Everyone found the word, great!");
			config.set("language.next-round", "Next round starting in 5sec");
			config.set("language.builder", "$player is building this time!");
			config.set("language.time", "You have" + ChatColor.BOLD +  " 90sec" + ChatColor.RESET + " to guess the word!");
			config.set("language.word", ChatColor.RED + "The word to guess is: " + ChatColor.BOLD + "$word");
			config.set("language.ready", "$player is ready!");
			config.set("language.not-ready", "$player is no longer ready!");
			config.set("language.everyone-ready", "Everyone is ready, starting the game!");
			config.set("language.game-over", ChatColor.BOLD + "GAME OVER");
			config.set("language.winner", ChatColor.GREEN + "WINNER: " + ChatColor.BOLD + "$player" + ChatColor.RESET + " [$score]");
			config.set("language.60sec", "60 seconds left!");
			config.set("language.30sec", "30 seconds left!");
			config.set("language.10sec", "10 seconds left!");
			config.set("language.time-out", ChatColor.RED + "Time out! Starting next round in 5sec!");
			config.set("language.word-reveal", ChatColor.GREEN + "The word was: " + ChatColor.BOLD + "$word");
			config.set("language.score", ChatColor.GREEN + "Score:");
			config.set("language.score-player", ChatColor.GREEN +"$player" + ChatColor.WHITE + " [$score]");
			config.set("language.invite", "$player wants to play Build My Thing, use \"" + ChatColor.YELLOW + "/bmt playwith $player" + ChatColor.RESET + "\" to play with him");
			config.set("language.join", "$player join the game ($currentplayers / $maxplayers)");
			config.set("language.room-starting", "Room is full, starting game in 5sec");
			config.set("language.room-started", "Room already started!");
			config.set("language.room-full", "Room full!");
			config.set("language.player-left", "You left the game");
			config.set("language.room-player-left", "$player left the game");
			config.set("language.room-deleted", "Room removed!");
			config.set("language.already-reported", "You already reported this player!");
			config.set("language.room-updated", "Room updated!");
			config.set("language.builder-abondon", "The builder abondon!");
			config.set("language.player-penalty", "$player recieved a penalty! " + ChatColor.RED + "[-$penalty]");
			config.set("language.reload", "Reloading config, all started games will stop!");
			config.set("language.broadcast-win", "$player just won a Build My Thing game in $room!");
		} else if(language.toLowerCase().startsWith("fr")){
			config.set("language.p1-set", "Le point 1 se trouve maintenant à votre position");
			config.set("language.p2-set", "Le point 2 se trouve maintenant à votre position");
			config.set("language.spawn-set", "Le point de spawn se trouve maintenant à votre position");
			config.set("language.room-created", "La salle a été crée!");
			config.set("language.room-cannot-create", "Assurez vous d'avoir choisi les point 1 et 2 ainsi qu'un point de spawn");
			config.set("language.room-precize", "Vous devez choisir une salle");
			config.set("language.room-doesnt-exist", "Cette salle n'existe pas");
			config.set("language.player-not-ingame", "Vous n'ètes pas dans une salle");
			config.set("language.player-already-ingame", "Vous ètes déjà dans une salle");
			config.set("language.player-not-online", "Ce joueur n'est pas en ligne");
			config.set("language.player-not-playing", "Ce joueur ne joue pas à Build My Thing");
			config.set("language.wrong-command", "Sous-commande inconnue, tapez\"" + ChatColor.YELLOW + "/bmt help" + ChatColor.RESET + "\"pour avoir une liste des commandes");
			config.set("language.no-command", "Aucune sous-commande précisée, tapez\"" + ChatColor.YELLOW + "/bmt help" + ChatColor.RESET + "\"pour avoir une liste des commandes");
			config.set("language.room-list", "Liste des salles:");
			config.set("language.no-command-while-ingame", "Les commandes sont désactivées en jeu");
			config.set("language.no-chat-while-builder", "Vous ne pouvez pas parler en tant que constructeur");
			config.set("language.word-already-found", "Vous avez déjà trouvé le mot");
			config.set("language.player-find-word-3points", ChatColor.BOLD + "$player" + ChatColor.RESET + ChatColor.GREEN + " a trouvé le mot! " + ChatColor.RESET + "[+3]");
			config.set("language.player-find-word-1point", ChatColor.BOLD + "$player" + ChatColor.RESET + ChatColor.GREEN + " a trouvé le mot! " + ChatColor.RESET + "[+1]");
			config.set("language.builder-get-points", ChatColor.BOLD + "$player" + ChatColor.RESET + ChatColor.GREEN + " gagne aussi 2 points!");
			config.set("language.everyone-found", "Tout le monde a trouvé le mot!");
			config.set("language.next-round", "le prochain round commence dans 5sec");
			config.set("language.builder", "$player construit cette fois!");
			config.set("language.time", "Vous avez" + ChatColor.BOLD +  " 90sec" + ChatColor.RESET + " pour deviner le mot!");
			config.set("language.word", ChatColor.RED + "Le mot à trouver est: " + ChatColor.BOLD + "$word");
			config.set("language.ready", "$player est prêt!");
			config.set("language.not-ready", "$player n'est plus prêt!");
			config.set("language.everyone-ready", "Tout le mond est prêt, le jeu peut commencer");
			config.set("language.game-over", ChatColor.BOLD + "GAME OVER");
			config.set("language.winner", ChatColor.GREEN + "GAGNANT: " + ChatColor.BOLD + "$player" + ChatColor.RESET + " [$score]");
			config.set("language.60sec", "Il reste 60sec!");
			config.set("language.30sec", "Il reste 30sec!");
			config.set("language.10sec", "Il reste 10sec!");
			config.set("language.time-out", ChatColor.RED + "Temps écoulé, le prochain round débutera dans 5sec!");
			config.set("language.word-reveal", ChatColor.GREEN + "Le mot était: " + ChatColor.BOLD + "$word");
			config.set("language.score", ChatColor.GREEN + "Score:");
			config.set("language.score-player", ChatColor.GREEN +"$player" + ChatColor.WHITE + " [$score]");
			config.set("language.invite", "$player voudrait jouer à Build My Thing, tapez \"" + ChatColor.YELLOW + "/bmt playwith $player" + ChatColor.RESET + "\" pour participer!");
			config.set("language.join", "$player a rejoint la partie ($currentplayers / $maxplayers)");
			config.set("language.room-starting", "La salle est pleine, la partie va commencer dans 5sec");
			config.set("language.room-started", "La partie a déjà commencée!");
			config.set("language.room-full", "La salle est pleine!");
			config.set("language.player-left", "Vous avez quitté la partie");
			config.set("language.room-player-left", "$player a quitté la partie");
			config.set("language.room-deleted", "Salle supprimée!");
			config.set("language.already-reported", "Vous avez déjà signalé ce joueur!");
			config.set("language.room-updated", "Salle mise à jour!");
			config.set("language.builder-abondon", "Le constructeur abandonne!");
			config.set("language.reload", "Rechargement de la configuration, toutes les salles vont s'arrêter!");
			config.set("language.broadcast-win", "$player vient de gagner une partie de Build My Thing dans $room!");
		}
	}

}
