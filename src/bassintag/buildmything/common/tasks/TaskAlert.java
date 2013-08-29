package bassintag.buildmything.common.tasks;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import bassintag.buildmything.common.ChatUtil;

public class TaskAlert extends BukkitRunnable{
	
	private final String MESSAGE;
	private final List<Player> SENDTO;
	
	public TaskAlert(String message, List<Player> players){
		this.MESSAGE = message;
		this.SENDTO = players;
	}

	@Override
	public void run() {
		for(Player p : this.SENDTO){
			ChatUtil.send(p, MESSAGE);
		}
	}

}
