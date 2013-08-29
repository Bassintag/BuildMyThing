package bassintag.buildmything.common;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class LocationUtil {
	
	public static String LocationToString(Location l){
		return String.valueOf(l.getWorld().getName() + ":" + l.getBlockX()) + ":" + String.valueOf(l.getBlockY()) + ":" + String.valueOf(l.getBlockZ());
	}
	
	public static Location StringToLoc(String s){
		Location l = null;
		try{
			World world = Bukkit.getWorld(s.split(":")[0]);
			Double x = Double.parseDouble(s.split(":")[1]);
			Double y = Double.parseDouble(s.split(":")[2]);
			Double z = Double.parseDouble(s.split(":")[3]);
			
			l = new Location(world, x, y, z);
		} catch(Exception ex){
			ex.printStackTrace();
		}
		
		return l;
	}

}
