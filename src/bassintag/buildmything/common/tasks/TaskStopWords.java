package bassintag.buildmything.common.tasks;

import org.bukkit.scheduler.BukkitRunnable;

import bassintag.buildmything.common.buildZone.BuildZone;

public class TaskStopWords extends BukkitRunnable{
	
	private BuildZone buildzone;

	@Override
	public void run() {
		buildzone.setNotAcceptWords();
	}

}
