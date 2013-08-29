package bassintag.buildmything.common.buildZone;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import bassintag.buildmything.common.BuildMyThing;
import bassintag.buildmything.common.ChatUtil;
import bassintag.buildmything.common.LocationUtil;
import bassintag.buildmything.common.cuboid.CuboidZone;
import bassintag.buildmything.common.tasks.TaskAlert;
import bassintag.buildmything.common.tasks.TaskNextRound;

public class BuildZone implements Listener {
	
	private BuildMyThing instance;
	
	private CuboidZone buildzone;
	private Location spectateTP;
	
	private Map<Player, Integer> score = new HashMap<Player, Integer>();
	private Map<Player, Boolean> ready = new HashMap<Player, Boolean>();
	private Map<Player, Integer> hasBeenBuilder = new HashMap<Player, Integer>();
	private Map<Player, ItemStack[]> inventories = new HashMap<Player, ItemStack[]>();
	private Map<Player, GameMode> gamemode = new HashMap<Player, GameMode>();
	
	private List<Player> hasFound = new ArrayList<Player>();
	private Player builder;
	
	private int players;
	private final int MAXPLAYERS = 4;
	private int buildPerPlayer = 2;
	private String name;
	
	private String word;
	
	private boolean started;
	
	private boolean wordHasBeenFound = false;
	private int playerFound = 0;
	
	private List<BukkitRunnable> tasks = new ArrayList<BukkitRunnable>();
	
	private boolean acceptWords = true;
	
	ScoreboardManager manager = Bukkit.getScoreboardManager();
	Scoreboard board = manager.getNewScoreboard();
	Objective objective;
	
	public BuildZone(CuboidZone build, Location loc, String name, BuildMyThing instance){
		this.buildzone = build;
		this.spectateTP = loc;
		this.name = name;
		this.instance = instance;
		objective = board.registerNewObjective(this.name + "_points", "dummy");
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		objective.setDisplayName(ChatColor.GOLD + "Score");
	}
	
	public void cancelTasks(){
		for(BukkitRunnable r : this.tasks){
			r.cancel();
		}
	}
	
	public void leave(Player player){
		if(this.score.containsKey(player)){
			this.started = false;
			this.score.remove(player);
			this.ready.remove(player);
			this.hasBeenBuilder.remove(player);
			this.hasFound.remove(player);
			if(this.inventories.get(player) != null){
				player.getInventory().setContents(inventories.get(player));
			}
			this.inventories.remove(player);
			player.setAllowFlight(false);
			player.setGameMode(this.gamemode.get(player));
			this.gamemode.remove(player);
			this.board.resetScores(player);
			player.setScoreboard(manager.getMainScoreboard());
			this.players -= 1;
			player.teleport(LocationUtil.StringToLoc(player.getMetadata("oldLoc").get(0).asString()));
			player.removeMetadata("oldLoc", instance);
			player.removeMetadata("inbmt", instance);
			ChatUtil.send(player, "You left the game");
			if(this.players > 1){
				this.sendMessage(player.getName() + " left the game");
			} else {
				this.cancelTasks();
				this.sendMessage("Sorry not enough people left, stopping the game...");
				this.stop();
			}
		}
	}
	
	public void join(Player player){
		if(!isStarted()){
			if(!this.score.containsKey(player)){
				if(this.players < this.MAXPLAYERS){
					ItemStack[] inventory = player.getInventory().getContents();
					ItemStack[] saveInventory = new ItemStack[inventory.length];
					for(int i = 0; i < inventory.length; i++)
					{
					    if(inventory[i] != null)
					    {
					        saveInventory[i] = inventory[i].clone();
					    }
					}
					this.inventories.put(player, saveInventory);
					this.gamemode.put(player, player.getGameMode());
					player.getInventory().clear();
					player.setGameMode(GameMode.SURVIVAL);
					player.setMetadata("oldLoc", new FixedMetadataValue(instance, LocationUtil.LocationToString(player.getLocation())));
					player.teleport(this.spectateTP);
					player.setMetadata("inbmt", new FixedMetadataValue(instance, this.getName()));
					player.setScoreboard(board);
					this.score.put(player, 0);
					this.ready.put(player, false);
					this.players += 1;
					if(this.players > 0){
						for(Player p : score.keySet()){
							ChatUtil.send(p, player.getName() + " join the game" + "   (" + this.players + "/" + this.MAXPLAYERS + " players)");
						}
					}
				} else {
					ChatUtil.send(player, "Room full!");
				}
			}
		} else {
			ChatUtil.send(player, "Room already ingame!");
		}
			
	}
	
	private String getNewWord(){
		return instance.getRandomWord();
	}
	
	public void start(){
		this.started = true;
		this.sendMessage("Everyone is ready, starting the game!");
		this.word = null;
		if(this.players < 3){
			this.buildPerPlayer = 3;
		}
		this.hasBeenBuilder.clear();
		for(Player p : this.score.keySet()){
			this.hasBeenBuilder.put(p, 0);
		}
		this.startRound();
	}
	
	public void startRound(){
		if(this.word != null){
			this.sendMessage(ChatColor.GREEN + "The word was: " + ChatColor.BOLD + word);
			
			this.sendMessage(ChatColor.GREEN + "Score:");
			for(Player p : score.keySet()){
				this.sendMessage(ChatColor.GREEN + p.getName() + ChatColor.WHITE + " [" + score.get(p) + "]");
			}
		}
		
		this.cancelTasks();
		
		this.wordHasBeenFound = false;
		this.playerFound = 0;
		
		this.hasFound.clear();
		this.acceptWords = true;
		this.word = this.getNewWord();
		this.buildzone.clear();
		this.getNextBuilder();
		
		TaskAlert alert1 = new TaskAlert("60 seconds left!", this.getPlayers());
		TaskAlert alert2 = new TaskAlert("30 seconds left!", this.getPlayers());
		TaskAlert alert3 = new TaskAlert("10 seconds left!", this.getPlayers());
		TaskNextRound endRound = new TaskNextRound(this);
		endRound.runTaskLater(instance, 1900);
		this.tasks.add(endRound);
		TaskAlert endRoundMsg = new TaskAlert(ChatColor.RED + "Time out! Starting next round in 5sec!", this.getPlayers());
		endRoundMsg.runTaskLater(instance, 1800);
		alert1.runTaskLater(instance, 600);
		alert2.runTaskLater(instance, 1200);
		alert3.runTaskLater(instance, 1600);
		this.tasks.add(alert1);
		this.tasks.add(alert2);
		this.tasks.add(alert3);
		this.tasks.add(endRoundMsg);
	}
	
	public List<Player> getPlayers(){
		List<Player> result = new ArrayList<Player>();
		for(Player p : this.score.keySet()){
			result.add(p);
		}
		
		return result;
	}
	
	public void stop(){
		List<Player> toKick = new ArrayList<Player>();
		for(Player p : this.score.keySet()){
			toKick.add(p);
		}
		
		for(Player p : toKick){
			this.leave(p);
		}
	}
	
	private void getNextBuilder(){
		if(this.getBuilder() != null){
			if(this.instance.getConfig().getBoolean("allow-creative")){
				this.builder.setGameMode(GameMode.SURVIVAL);
			} else {
				this.builder.setAllowFlight(false);
			}
			this.builder.teleport(this.spectateTP);
			this.builder.getInventory().clear();
			this.builder = null;
		}
		for(int i = 0; i < this.buildPerPlayer; i++){
			for(Player p : this.hasBeenBuilder.keySet()){
				if(this.hasBeenBuilder.get(p) > i){
					continue;
				} else {
					this.setBuilder(p);
					return;
				}
			}
		}
		this.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "GAME OVER");
		Player winner = null;
		for(Player p : this.score.keySet()){
			if(winner != null){
				if(this.score.get(p) > this.score.get(winner)){
					winner = p;
				}
			} else {
				winner = p;
			}
		}
		int i = score.get(winner);
		this.sendMessage(ChatColor.GREEN + "WINNER: " + ChatColor.BOLD + winner.getName() + ChatColor.RESET + " [" + i + "]");
		this.stop();
	}
	
	private void setBuilder(Player p){
		this.builder = p;
		this.hasBeenBuilder.put(p, this.hasBeenBuilder.get(p) + 1);
		p.teleport(this.buildzone.getBottomCenter());
		if(this.instance.getConfig().getBoolean("allow-creative")){
			p.setGameMode(GameMode.CREATIVE);
		} else {
			p.setAllowFlight(true);
			for(short i = 0; i < 16; i++){
				p.getInventory().addItem(new ItemStack(Material.STAINED_CLAY, 2, i));
			}
		}
		this.sendMessage(p.getName() + " is building this time!");
		this.sendMessage("You have" + ChatColor.BOLD +  " 90sec" + ChatColor.RESET + " to guess the word!");
		ChatUtil.send(p,ChatColor.RED + "The word to guess is: " + ChatColor.BOLD + this.word);
	}
	
	public void sendMessage(String message){
		for(Player p : score.keySet()){
			ChatUtil.send(p, message);
		}
	}
	
	public boolean isStarted(){
		return this.started;
	}

	public String getName() {
		return name;
	}
	
	public void save(FileConfiguration file){
		file.set(this.getName() + ".pos1", LocationUtil.LocationToString(this.buildzone.getCorner1().getLocation()));
		file.set(this.getName() + ".pos2", LocationUtil.LocationToString(this.buildzone.getCorner2().getLocation()));
		file.set(this.getName() + ".spawn", LocationUtil.LocationToString(this.spectateTP));
	}
	
	public void remove(FileConfiguration file){
		this.stop();
		file.set(this.getName(), null);
		this.instance.saveConfig();
	}
	
	public static BuildZone load(FileConfiguration file, String name, BuildMyThing instance){
		Location corner1 = LocationUtil.StringToLoc(file.getString(name + ".pos1"));
		Location corner2 = LocationUtil.StringToLoc(file.getString(name + ".pos2"));
		Location spawn = LocationUtil.StringToLoc(file.getString(name + ".spawn"));
		
		return new BuildZone(new CuboidZone(corner1.getBlock(), corner2.getBlock()), spawn, name, instance);
	}
	
	private boolean isEveryoneReady(){
		for(Player p : ready.keySet()){
			if(ready.get(p) == true){
				continue;
			} else {
				return false;
			}
		}
		return true;
	}

	public void setReady(Player player) {
		if(!this.isStarted()){
			if(this.ready.containsKey(player)){
				if(this.ready.get(player) == true){
					this.sendMessage(player.getName() + " is no longer ready");
					this.ready.put(player, false);
				} else {
					this.ready.put(player, true);
					this.sendMessage(player.getName() + " is ready");
					if(this.players > 1){
						if(this.isEveryoneReady()){
							this.start();
						}
					}
				}
			}
		}
	}

	public Player getBuilder() {
		return builder;
	}

	public CuboidZone getBuildZone() {
		return this.buildzone;
	}

	public String getWord() {
		return word;
	}
	
	public void increaseScore(Player p, int value){
		if(this.score.containsKey(p)){
			this.score.put(p, this.score.get(p) + value);
			Score scoreBoard = objective.getScore(p);
			scoreBoard.setScore(this.score.get(p));
		}
	}

	public void wordFoundBy(Player player) {
		if(this.acceptWords && !this.hasFound.contains(player)){
			this.hasFound.add(player);
			if(!wordHasBeenFound){
				this.sendMessage(ChatColor.BOLD + player.getName() + ChatColor.RESET + ChatColor.GREEN + " has found the word! " + ChatColor.RESET + "[+3]");
				this.sendMessage(ChatColor.BOLD + builder.getName() + ChatColor.RESET + ChatColor.GREEN + " also earn 2 points!");
				this.increaseScore(player, 3);
				this.increaseScore(builder, 2);
				this.wordHasBeenFound = true;
			} else {
				this.sendMessage(ChatColor.BOLD + player.getName() + ChatColor.RESET + ChatColor.GREEN + " has found the word! " + ChatColor.RESET + "[+1]");
				this.increaseScore(player, 1);
			}
			
			instance.spawnRandomFirework(player.getLocation());
			this.playerFound++;
		}
		
		if(this.playerFound >= this.players - 1){
			this.sendMessage("Everyone found the word, great!");
			this.sendMessage("Next round starting in 5sec!");
			this.cancelTasks();
			TaskNextRound endRound = new TaskNextRound(this);
			endRound.runTaskLater(instance, 100);
			this.tasks.add(endRound);
		}
	}

	public void setNotAcceptWords() {
		this.acceptWords = false;
		
	}

	public int getMaxPlayers() {
		return this.MAXPLAYERS;
	}
}
