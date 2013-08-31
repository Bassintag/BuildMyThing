package bassintag.buildmything.common.buildZone;

import org.bukkit.scheduler.BukkitRunnable;

public class TaskStart extends BukkitRunnable{
	
	BuildZone b;
	
	public TaskStart(BuildZone b){
		this.b = b;
	}

	@Override
	public void run() {
		b.start();
	}

}
