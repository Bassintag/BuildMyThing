package bassintag.buildmything.common.tasks;

import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;

import bassintag.buildmything.common.buildZone.BuildZone;

public class TaskRegisterSign extends BukkitRunnable{
	
	private Block b;
	private BuildZone bz;
	private String display;
	
	public TaskRegisterSign(Block b, BuildZone bz, String display){
		this.b = b;
		this.bz = bz;
		this.display = display;
	}

	@Override
	public void run() {
		bz.registerSign(b, display);
	}
}
