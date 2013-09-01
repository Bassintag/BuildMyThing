package bassintag.buildmything.common.tasks;

import org.bukkit.scheduler.BukkitRunnable;

import bassintag.buildmything.common.buildZone.BuildZone;

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
