package bassintag.buildmything.common.signs;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import bassintag.buildmything.common.BuildMyThing;
import bassintag.buildmything.common.ChatUtil;

public class SignListener implements Listener{
	
	private final BuildMyThing plugin;
	
	public SignListener(BuildMyThing plugin){
		this.plugin = plugin;
	}

	@EventHandler
	public void onBlockPlaced(SignChangeEvent Event) {
		if (Event.getLine(0).startsWith("[bmt]")) {
			if (Event.getLine(1).length() > 0) {
				if (Event.getPlayer().hasPermission("bmt.admin")) {
					String line2 = Event.getLine(1);
					if (plugin.getRoomByName(line2) != null) {
						Event.setLine(0, "-*-");
						Event.setLine(1, ChatColor.WHITE + "[" + ChatColor.GREEN + "BMT"+ ChatColor.WHITE + "]");
						Event.setLine(2, line2);
						Event.setLine(3, "-*-");

						ChatUtil.send(Event.getPlayer(), "Sign created!");
					} else {
						ChatUtil.send(Event.getPlayer(), "Unknown room: " + line2);
					}
				}
			}
		}
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent Event) {
		if(Event.getClickedBlock() != null){
			Player player = Event.getPlayer();
			Block block = Event.getClickedBlock();
			if (block.getType() == Material.WALL_SIGN || block.getType() == Material.SIGN_POST) {
				Sign sign = (Sign) block.getState();
				String line1 = sign.getLine(1);
				if (line1.equals(ChatColor.WHITE + "[" + ChatColor.GREEN + "BMT"
						+ ChatColor.WHITE + "]")) {
					String line2 = sign.getLine(2);
					if (plugin.getRoomByName(line2) != null) {
						plugin.getRoomByName(line2).join(player);
					}
				}
			}
		}

	}
}
