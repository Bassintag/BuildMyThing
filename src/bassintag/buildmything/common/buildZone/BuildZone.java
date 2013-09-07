package bassintag.buildmything.common.buildZone;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
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
import bassintag.buildmything.common.tasks.TaskStart;

public class BuildZone implements Listener {
	
	private BuildMyThing instance;
	
	private CuboidZone buildzone;
	private Location spectateTP;
	
	private Map<Player, Integer> score = new HashMap<Player, Integer>();
	private Map<Player, Boolean> ready = new HashMap<Player, Boolean>();
	private Map<Player, Integer> hasBeenBuilder = new HashMap<Player, Integer>();
	private Map<Player, ItemStack[]> inventories = new HashMap<Player, ItemStack[]>();
	private Map<Player, GameMode> gamemode = new HashMap<Player, GameMode>();
	private Map<Player, Integer> foodLevel = new HashMap<Player, Integer>();
	
	private List<Player> hasFound = new ArrayList<Player>();
	private Player builder;
	
	private int players;
	private int maxplayers = 12;
	private int buildPerPlayer = 2;
	private String name;
	
	private String word;
	
	private boolean started;
	
	private boolean wordHasBeenFound = false;
	private int playerFound = 0;
	
	private List<BukkitRunnable> tasks = new ArrayList<BukkitRunnable>();
	
	private boolean acceptWords = true;
	
	private List<Block> signs = new ArrayList<Block>();
	
	ScoreboardManager manager = Bukkit.getScoreboardManager();
	Scoreboard board = manager.getNewScoreboard();
	Objective objective;

	private boolean usesCustomWords;
	
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
			if(this.started == false){
				if(this.players == this.maxplayers){
					this.sendMessage("Someone left the game, game won't start until everyone is ready");
					this.cancelTasks();
				}
			}
			player.setFoodLevel(this.foodLevel.get(player));
			this.foodLevel.remove(player);
			this.score.remove(player);
			this.ready.remove(player);
			this.hasBeenBuilder.remove(player);
			if(this.hasFound(player)){
				this.playerFound--;
			}
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
			this.players--;
			player.teleport(LocationUtil.StringToLoc(player.getMetadata("oldLoc").get(0).asString()));
			player.removeMetadata("oldLoc", instance);
			player.removeMetadata("inbmt", instance);
			ChatUtil.send(player, instance.translator.get("player-left"));
			this.updateSigns();
			if(this.players > 1){
				this.sendMessage(instance.translator.get("room-player-left").replace("$player", player.getName()));
			} else if(this.isStarted()){
				this.cancelTasks();
				this.sendMessage("Sorry not enough people left, stopping the game...");
				this.stop();
			}
		}
	}
	
	public void join(Player player){
		if(!isStarted()){
			if(!this.score.containsKey(player)){
				player.setMetadata("oldLoc", new FixedMetadataValue(instance, LocationUtil.LocationToString(player.getLocation())));
				player.teleport(this.spectateTP);
				if(this.players < this.maxplayers){
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
					this.foodLevel.put(player, player.getFoodLevel());
					player.setFoodLevel(20);
					this.gamemode.put(player, player.getGameMode());
					player.getInventory().clear();
					player.setGameMode(GameMode.SURVIVAL);
					player.setMetadata("inbmt", new FixedMetadataValue(instance, this.getName()));
					player.setScoreboard(board);
					this.score.put(player, 0);
					this.ready.put(player, false);
					this.players += 1;
					if(this.players > 0){
						for(Player p : score.keySet()){
							ChatUtil.send(p, instance.translator.get("join").replace("$player", player.getName()).replace("$currentplayers", String.valueOf(this.players)).replace("$maxplayers", String.valueOf(this.maxplayers)));
						}
					}
					
					if(this.players == this.maxplayers){
						this.sendMessage(instance.translator.get("room-starting"));
						TaskStart start = new TaskStart(this);
						start.runTaskLater(instance, 100);
						this.tasks.add(start);
					}
					
					this.updateSigns();
				} else {
					ChatUtil.send(player, instance.translator.get("room-full"));
				}
			}
		} else {
			ChatUtil.send(player, instance.translator.get("room-started"));
		}
			
	}
	
	private String getNewWord(){
		if(this.usesCustomWords){
			return this.getRandomWordFromConfig();
		} else {
			return instance.getRandomWord();
		}
	}
	
	private String getRandomWordFromConfig(){
		@SuppressWarnings("unchecked")
		List<String> words = (List<String>)(this.instance.getConfig().getList("rooms" + this.name + ".custom-word-list"));
		if(words.size() > 0){
			int i = words.size();
			Random r = new Random();
			return words.get(r.nextInt(i));
		} else {
			return "null";
		}
	}
	
	public void start(){
		if(!started){
			this.cancelTasks();
			this.started = true;
			this.word = null;
			if(this.players < 3){
				this.buildPerPlayer = 3;
			}
			this.hasBeenBuilder.clear();
			for(Player p : this.score.keySet()){
				this.hasBeenBuilder.put(p, 0);
			}
			this.startRound();
			this.updateSigns();
		}
	}
	
	public void startRound(){
		if(this.word != null){
			this.sendMessage(instance.translator.get("word-reveal").replace("$word", word));
			
			this.sendMessage(instance.translator.get("score"));
			for(Player p : score.keySet()){
				this.sendMessage(instance.translator.get("score-player").replace("$score", String.valueOf(score.get(p))).replace("$player", p.getName()));
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
		
		TaskAlert alert1 = new TaskAlert(instance.translator.get("60sec"), this.getPlayers());
		TaskAlert alert2 = new TaskAlert(instance.translator.get("30sec"), this.getPlayers());
		TaskAlert alert3 = new TaskAlert(instance.translator.get("10sec"), this.getPlayers());
		TaskNextRound endRound = new TaskNextRound(this);
		endRound.runTaskLater(instance, 1900);
		this.tasks.add(endRound);
		TaskAlert endRoundMsg = new TaskAlert(instance.translator.get("time-out"), this.getPlayers());
		endRoundMsg.runTaskLater(instance, 1800);
		alert1.runTaskLater(instance, 600);
		alert2.runTaskLater(instance, 1200);
		alert3.runTaskLater(instance, 1600);
		this.tasks.add(alert1);
		this.tasks.add(alert2);
		this.tasks.add(alert3);
		this.tasks.add(endRoundMsg);
	}
	
	public void removePlayerFromAlerts(Player p){
		for(BukkitRunnable task : this.tasks){
			if(task instanceof TaskAlert){
				TaskAlert taskAlert = (TaskAlert)task;
				taskAlert.removePlayer(p);
			}
		}
	}
	
	public List<Player> getPlayers(){
		List<Player> result = new ArrayList<Player>();
		for(Player p : this.score.keySet()){
			result.add(p);
		}
		
		return result;
	}
	
	public void stop(){
		
		this.started = false;
		
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
		this.sendMessage(instance.translator.get("game-over"));
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
		
		if(this.score.containsKey(winner)){
			int i = score.get(winner);
			this.sendMessage(instance.translator.get("winner").replace("$score", String.valueOf(i)).replace("$player", winner.getName()));
			if(instance.getConfig().getBoolean("broadcast-on-game-over")){
				ChatUtil.broadcast(instance.translator.get("broadcast-name").replace("$player", winner.getName()).replace("$room", this.getName()));
			}
		}
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
				p.getInventory().addItem(new ItemStack(Material.STAINED_CLAY, 64, i));
			}
		}
		this.sendMessage(instance.translator.get("builder").replace("$player", p.getName()));
		this.sendMessage(instance.translator.get("time"));
		ChatUtil.send(p, instance.translator.get("word").replace("$word", word));
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
		file.set("rooms" + this.getName() + ".pos1", LocationUtil.LocationToString(this.buildzone.getCorner1().getLocation()));
		file.set("rooms" + this.getName() + ".pos2", LocationUtil.LocationToString(this.buildzone.getCorner2().getLocation()));
		file.set("rooms" + this.getName() + ".spawn", LocationUtil.LocationToString(this.spectateTP));
		file.set("rooms" + this.getName() + ".maxplayers", this.maxplayers);
		file.addDefault("rooms" + this.getName() + ".custom-words", false);
		List<String> signData = new ArrayList<String>();
		for(Block s : this.signs){
			if (s.getType() == Material.WALL_SIGN){
				String loc = LocationUtil.LocationToString(s.getLocation());
				String display;
				if(s.hasMetadata("display")){
					display = ";" + s.getMetadata("display").get(0).asString();
				} else {
					display = ";none";
				}
				String result = loc + display;
				signData.add(result);
			}
		}
		file.set("rooms" + this.getName() + ".signs", signData);
	}
	
	public void remove(FileConfiguration file){
		this.stop();
		file.set(this.getName(), null);
		this.instance.saveConfig();
	}
	
	public static BuildZone load(FileConfiguration file, String name, BuildMyThing instance){
		Location corner1 = LocationUtil.StringToLoc(file.getString("rooms" + name + ".pos1"));
		Location corner2 = LocationUtil.StringToLoc(file.getString("rooms" + name + ".pos2"));
		Location spawn = LocationUtil.StringToLoc(file.getString("rooms" + name + ".spawn"));
		BuildZone b = new BuildZone(new CuboidZone(corner1.getBlock(), corner2.getBlock()), spawn, name, instance);
		b.setMaxPlayers(file.getInt("rooms" + name + ".maxplayers"));
		if(file.getBoolean("rooms" + name + ".custom-words")){
			file.addDefault("rooms" + name + ".custom-word-list", BuildMyThing.DEFAULT_WORDS);
			b.setUsesCustomWords(true);
		}
		if(file.getList("rooms" + name + ".signs") != null){
			@SuppressWarnings("unchecked")
			List<String> signLoc = (List<String>) file.getList("rooms" + name + ".signs");
			for(String s : signLoc){
				String loc = s.split(";")[0];
				String display = s.split(";")[1];
				Location l = LocationUtil.StringToLoc(loc);
				Block block = l.getBlock();
				if(block.getState() instanceof Sign){
					if (block.getType() == Material.WALL_SIGN){
						b.registerSign(block, display);
					}
				}
			}
		}
		return b;
	}
	
	public void setUsesCustomWords(Boolean b){
		this.usesCustomWords = true;
	}
	
	public void setMaxPlayers(int i){
		this.maxplayers = i;
	}
	
	private boolean isEveryoneReady(){
		if(this.ready.size() > 0){
			for(Player p : ready.keySet()){
				if(ready.get(p) == true){
					continue;
				} else {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	public void setReady(Player player) {
		if(!this.isStarted()){
			if(this.ready.containsKey(player)){
				if(this.ready.get(player) == true){
					this.sendMessage(instance.translator.get("not-ready").replace("$player", player.getName()));
					this.ready.put(player, false);
				} else {
					this.ready.put(player, true);
					this.sendMessage(instance.translator.get("ready").replace("$player", player.getName()));
					if(this.players > 1){
						if(this.isEveryoneReady()){
							this.sendMessage(instance.translator.get("everyone-ready"));
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
			
			for(Player p : this.score.keySet()){
				p.getWorld().playSound(p.getLocation(), Sound.ANVIL_LAND, 1, 1);
			}
			
			if(!wordHasBeenFound){
				this.sendMessage(instance.translator.get("player-find-word-3points").replace("$player", player.getName()));
				this.sendMessage(instance.translator.get("builder-get-points").replace("$player", builder.getName()));
				this.increaseScore(player, 3);
				this.increaseScore(builder, 2);
				this.wordHasBeenFound = true;
			} else {
				this.sendMessage(instance.translator.get("player-find-word-1point").replace("$player", player.getName()));
				this.increaseScore(player, 1);
			}
			
			instance.spawnRandomFirework(player.getLocation());
			this.playerFound++;
		}
		
		if(this.playerFound == this.players - 1){
			this.sendMessage(instance.translator.get("everyone-found"));
			this.sendMessage(instance.translator.get("next-round"));
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
		return this.maxplayers;
	}

	public boolean hasFound(Player player) {
		return this.hasFound.contains(player);
	}
	
	public void updateSigns(){
		for(Block b : this.signs){
			if(b.getState() instanceof Sign){
				Sign s = (Sign)b.getState();
				s.setLine(0, ChatColor.WHITE + "[" + ChatColor.GREEN + "Join" + ChatColor.WHITE + "]");
				s.setLine(1, ChatColor.YELLOW + this.getName());
				s.setLine(2, ChatColor.GRAY + String.valueOf(players) + ChatColor.WHITE + "/" + ChatColor.GRAY + maxplayers);
				s.setLine(3, this.started ? ChatColor.RED + "STARTED" : ChatColor.GREEN + "OPEN");
				s.update();
				
				if(b.hasMetadata("display")){
					String display = b.getMetadata("display").get(0).asString();
					org.bukkit.material.Sign sign = (org.bukkit.material.Sign) b.getState().getData();
					Block attachedBlock = b.getRelative(sign.getAttachedFace());
					if(display.equals("wool")){
						for(int i = 0; i < this.maxplayers; i++){
							if(this.isStarted()){
								Location loc = attachedBlock.getLocation().clone().add(0, i + 1, 0);
								loc.getBlock().setType(Material.WOOL);
								loc.getBlock().setData((byte)14);
							} else {
								Location loc = attachedBlock.getLocation().clone().add(0, i + 1, 0);
								loc.getBlock().setType(Material.WOOL);
								loc.getBlock().setData(i < this.players ? (byte)1 : (byte)5);
							}
						}
					} else if(display.equals("lamp")){
							if(this.isStarted()){
								Location loc = attachedBlock.getLocation().clone().add(0, + 1, 0);
								attachedBlock.setType(Material.REDSTONE_BLOCK);
								loc.getBlock().setType(Material.REDSTONE_LAMP_ON);
							} else {
								Location loc = attachedBlock.getLocation().clone().add(0, + 1, 0);
								attachedBlock.setType(Material.EMERALD_BLOCK);
								loc.getBlock().setType(Material.REDSTONE_LAMP_OFF);
							}
					} else if(display.equals("kany")){
						//OMG A SECRET !
						if(this.isStarted()){
							Location loc = attachedBlock.getLocation().clone().add(0, + 1, 0);
							attachedBlock.setType(Material.GRASS);
							loc.getBlock().setType(Material.RED_ROSE);
						} else {
							Location loc = attachedBlock.getLocation().clone().add(0, + 1, 0);
							attachedBlock.setType(Material.GRASS);
							loc.getBlock().setType(Material.YELLOW_FLOWER);
						}
					}
				}
			}
		}
	}
	
	public void registerSign(Block block){
		this.registerSign(block, "none");
	}

	public void registerSign(Block block, String display) {
		if(block.getState() instanceof Sign) {
			this.signs.add(block);
			block.setMetadata("bmtjoinsign", new FixedMetadataValue(instance, this.getName()));
			if(display.equalsIgnoreCase("wool")){
				block.setMetadata("display", new FixedMetadataValue(instance, "wool"));
			} else if(display.equalsIgnoreCase("lamp")){
				block.setMetadata("display", new FixedMetadataValue(instance, "lamp"));
			} else if(display.equalsIgnoreCase("kany")){
				//OMG A SECRET !
				block.setMetadata("display", new FixedMetadataValue(instance, "kany"));
			}
			this.updateSigns();
		}
	}
	
	public void removeSign(Block block){
		if(block.getState() instanceof Sign) {
			this.signs.remove(block);
			if(block.hasMetadata("bmtjoinsign")){
				block.removeMetadata("bmtjoinsign", instance);
			}
		}
	}

	public void abondon(Player player) {
		if(this.builder.equals(player)){
			this.sendMessage(instance.translator.get("builder-abondon"));
			if(this.instance.getConfig().getBoolean("penalty-on-abandon")){
				this.sendMessage(instance.translator.get("player-penalty").replace("$player", player.getName()).replace("$score", "1"));
				this.decreaseScore(player, 1);
			}
			this.sendMessage(instance.translator.get("next-round"));
			this.cancelTasks();
			TaskNextRound nextRound = new TaskNextRound(this);
			nextRound.runTaskLater(instance, 100);
		}
	}

	private void decreaseScore(Player player, int i) {
		this.score.put(player, this.score.get(player) - i);
		if(this.score.get(player) < 0){
			this.score.put(player, 0);
		}
	}
}
