package bassintag.buildmything.common.cuboid;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

public class CuboidZone {
	
	private Block corner1;
	private Block corner2;
	private World world;

	public CuboidZone(Block corner1, Block corner2){
		if(corner1.getWorld().equals(corner2.getWorld())){
			this.corner1 = corner1;
			this.corner2 = corner2;
			this.world = corner1.getWorld();
		} else {
			throw new IllegalArgumentException("All cuboid blocks aren't in the same world !");
		}
	}
	
	public void set(Material material){
		for(Block b : this.toArray()){
			b.setType(material);
		}
	}
	
	public boolean contains(Block b){
		return this.toArray().contains(b);
	}
	
	public List<Block> toArray(){
		List<Block> result = new ArrayList<Block>();
		
		int minX = Math.min(corner1.getX(), corner2.getX());
		int minY = Math.min(corner1.getY(), corner2.getY());
		int minZ = Math.min(corner1.getZ(), corner2.getZ());
		int maxX = Math.max(corner1.getX(), corner2.getX());
		int maxY = Math.max(corner1.getY(), corner2.getY());
		int maxZ = Math.max(corner1.getZ(), corner2.getZ());
		
		for(int x = minX; x <= maxX; x++){
			for(int y = minY; y <= maxY; y++){
				for(int z = minZ; z <= maxZ; z++){
					result.add(world.getBlockAt(new Location(world, x, y, z)));
				}
			}
		}
		
		return result;
	}
	
	@Override
	public String toString(){
		Location l = this.corner1.getLocation();
		String s = String.valueOf(world.getName() + ":" + l.getBlockX()) + ":" + String.valueOf(l.getBlockY()) + ":" + String.valueOf(l.getBlockZ());
		Location l1 = this.corner2.getLocation();
		String s1 = String.valueOf(world.getName() + ":" + l1.getBlockX()) + ":" + String.valueOf(l1.getBlockY()) + ":" + String.valueOf(l1.getBlockZ());
		String result = s + ";" + s1;
		return result;
	}

	public Block getCorner1() {
		return this.corner1;
	}

	public Block getCorner2() {
		return this.corner2;
	}

	public Location getBottomCenter() {
		int minY = Math.min(corner1.getY(), corner2.getY());
		int minX = Math.min(corner1.getX(), corner2.getX());
		int minZ = Math.min(corner1.getZ(), corner2.getZ());
		int maxX = Math.max(corner1.getX(), corner2.getX());
		int maxZ = Math.max(corner1.getZ(), corner2.getZ());
		
		return new Location(world, minX + (maxX - minX) / 2, minY, minZ + (maxZ - minZ) / 2);
	}

	public void clear() {
		for(Block b : this.toArray()){
			b.setType(Material.AIR);
		}
	}
}
