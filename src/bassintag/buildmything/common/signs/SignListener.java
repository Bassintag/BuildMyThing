package bassintag.buildmything.common.signs;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import bassintag.buildmything.common.BuildMyThing;
import bassintag.buildmything.common.ChatUtil;
import bassintag.buildmything.common.tasks.TaskRegisterSign;

public class SignListener implements Listener{
	
	private final BuildMyThing plugin;
	
	public SignListener(BuildMyThing plugin){
		this.plugin = plugin;
	}

	@EventHandler
	public void onBlockPlaced(SignChangeEvent Event) {
		if (Event.getLine(0).startsWith("[bmt j]")) {
			if (Event.getLine(1).length() > 0) {
				if (Event.getPlayer().hasPermission("bmt.admin")) {
					String line2 = Event.getLine(1);
					if (Event.getBlock().getType() == Material.WALL_SIGN){
						if (plugin.getRoomByName(line2) != null) {
							if(Event.getLine(2) != null){
								new TaskRegisterSign(Event.getBlock(), plugin.getRoomByName(line2), Event.getLine(2)).runTaskLater(plugin, 20);
							} else {
								new TaskRegisterSign(Event.getBlock(), plugin.getRoomByName(line2), "none").runTaskLater(plugin, 20);
							}
							ChatUtil.send(Event.getPlayer(), "Sign created!");
						} else {
							ChatUtil.send(Event.getPlayer(), "Unknown room: " + line2);
						}
					} else {
						ChatUtil.send(Event.getPlayer(), "Sorry, it must be a wall sign");
					}
				}
			}
		} else if (Event.getLine(0).startsWith("[bmt l]")) {
			if (Event.getPlayer().hasPermission("bmt.admin")) {
				Event.setLine(0, "");
				Event.setLine(1, ChatColor.WHITE + "[" + ChatColor.GREEN + "Leave" + ChatColor.WHITE + "]");
				Event.setLine(2, "");
				Event.setLine(3, "");
			}
		} else if (Event.getLine(0).startsWith("[bmt r]")) {
			if (Event.getPlayer().hasPermission("bmt.admin")) {
				Event.setLine(0, "");
				Event.setLine(1, ChatColor.WHITE + "[" + ChatColor.GREEN + "Ready" + ChatColor.WHITE + "]");
				Event.setLine(2, "");
				Event.setLine(3, "");
			}
		}
	}
	
	@EventHandler
	public void onBlockBroken(BlockBreakEvent Event) {
		Block b = Event.getBlock();
		if(b.getState() instanceof Sign) {
			if(b.hasMetadata("bmtjoinsign")){
				if (Event.getPlayer().hasPermission("bmt.admin")) {
					String name = b.getMetadata("bmtjoinsign").get(0).asString();
					if(plugin.getRoomByName(name) != null){
						plugin.getRoomByName(name).removeSign(b);
						ChatUtil.send(Event.getPlayer(), ChatColor.RED + "Sign removed");
					}
				} else {
					Event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent Event) {
		if(Event.getClickedBlock() != null){
			if(Event.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
				Player player = Event.getPlayer();
				Block block = Event.getClickedBlock();
				if(block.getState() instanceof Sign) {
					Sign sign = (Sign) block.getState();
					if (block.hasMetadata("bmtjoinsign")){
						String room = sign.getMetadata("bmtjoinsign").get(0).asString();
						if(plugin.getRoomByName(room) != null){
							if(player.hasPermission("bmt.default")){
								plugin.getRoomByName(room).join(player);
							}
						}
					} else if(sign.getLine(1).equals(ChatColor.WHITE + "[" + ChatColor.GREEN + "Leave" + ChatColor.WHITE + "]")){
						if(player.hasPermission("bmt.default")){
							if(player.hasMetadata("inbmt")){
								if(plugin.getRoomByName(player.getMetadata("inbmt").get(0).asString()) != null){
									plugin.getRoomByName(player.getMetadata("inbmt").get(0).asString()).leave(player);
								}
							}
						}
					} else if(sign.getLine(1).equals(ChatColor.WHITE + "[" + ChatColor.GREEN + "Ready" + ChatColor.WHITE + "]")){
						if(player.hasPermission("bmt.default")){
							if(player.hasMetadata("inbmt")){
								if(plugin.getRoomByName(player.getMetadata("inbmt").get(0).asString()) != null){
									plugin.getRoomByName(player.getMetadata("inbmt").get(0).asString()).setReady(player);
								}
							}
						}
					}
				}
			}
		}

	}
}
