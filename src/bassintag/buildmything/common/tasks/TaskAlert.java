package bassintag.buildmything.common.tasks;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import bassintag.buildmything.common.ChatUtil;

public class TaskAlert extends BukkitRunnable{
	
	private final String MESSAGE;
	private List<Player> sendTo;
	
	public TaskAlert(String message, List<Player> players){
		this.MESSAGE = message;
		this.sendTo = players;
	}

	@Override
	public void run() {
		for(Player p : this.sendTo){
			ChatUtil.send(p, MESSAGE);
		}
	}

	public void removePlayer(Player p) {
		if(this.sendTo.contains(p)){
			sendTo.remove(p);
		}
		
	}

}
