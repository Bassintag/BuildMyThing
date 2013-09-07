package bassintag.buildmything.common.buildZone;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import bassintag.buildmything.common.BuildMyThing;
import bassintag.buildmything.common.ChatUtil;
import bassintag.buildmything.common.update.UpdateChecker;

public class BuildZoneListener implements Listener{
	
	private BuildMyThing instance;
	
	public BuildZoneListener(BuildMyThing instance){
		this.instance = instance;
	}
	
	/*
	 * 	###################
	 * 	# EVENTS HANDLERS #
	 * 	###################
	 */
	
	
	@EventHandler
	public void onPlayerLogOut(PlayerQuitEvent event){
		if(event.getPlayer().hasMetadata("inbmt")){
			instance.getRoomByName(event.getPlayer().getMetadata("inbmt").get(0).asString()).leave(event.getPlayer());
		}
	}
	
	@EventHandler
	public void onPlayerPlaceBlock(BlockPlaceEvent event){
		if(event.getPlayer().hasMetadata("inbmt")){
			if(instance.getRoomByName(event.getPlayer().getMetadata("inbmt").get(0).asString()) != null){
				if(instance.getRoomByName(event.getPlayer().getMetadata("inbmt").get(0).asString()).getBuilder().getName() == event.getPlayer().getName()){
					if(instance.getRoomByName(event.getPlayer().getMetadata("inbmt").get(0).asString()).getBuildZone().contains(event.getBlock())){
						event.getPlayer().getInventory().addItem(new ItemStack(event.getBlockPlaced().getType(), 1, event.getBlockPlaced().getData()));
						event.getBlockPlaced().setType(event.getBlockPlaced().getType());
					}
				}
			}
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event){
		if(event.getAction() == Action.LEFT_CLICK_BLOCK){
			if(event.getPlayer().hasMetadata("inbmt")){
				if(instance.getRoomByName(event.getPlayer().getMetadata("inbmt").get(0).asString()) != null){
					if(instance.getRoomByName(event.getPlayer().getMetadata("inbmt").get(0).asString()).getBuilder() != null){
						if(instance.getRoomByName(event.getPlayer().getMetadata("inbmt").get(0).asString()).getBuilder().getName() == event.getPlayer().getName()){
							if(instance.getRoomByName(event.getPlayer().getMetadata("inbmt").get(0).asString()).getBuildZone().contains(event.getClickedBlock())){
								event.getClickedBlock().setType(Material.AIR);
								return;
							}
						}
					}
				}
			}
		} else if(event.getPlayer().hasMetadata("inbmt")) {
			if(event.getPlayer().getItemInHand().getTypeId() >= 256){
				event.setCancelled(true);
			}
		}
	}
	
    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event){
		if(event.getPlayer().hasMetadata("inbmt")){
			if(!event.getMessage().startsWith("/bmt")){
				ChatUtil.send(event.getPlayer(), instance.translator.get("no-command-while-ingame"));
				event.setCancelled(true);
			}
	    }
    }
	
	@EventHandler
	public void onPlayerBreakBlock(BlockBreakEvent event){
		if(event.getPlayer().hasMetadata("inbmt")){
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event){
		if(event.getPlayer().hasMetadata("inbmt")){
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlayerHit(EntityDamageEvent event){
		if(event.getEntity() instanceof Player){
			Player p = (Player) event.getEntity();
			if(p.hasMetadata("inbmt")){
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onPlayerHungerChange(FoodLevelChangeEvent event){
		if(event.getEntity().hasMetadata("inbmt")){
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event){
		if(event.getPlayer().hasPermission("bmt.admin")){
			if(instance.getConfig().getBoolean("update-checker")){
				if(UpdateChecker.isOutdated(instance)){
					String version = UpdateChecker.getLastVersion("http://dev.bukkit.org/bukkit-plugins/build-my-thing/files.rss");
					ChatUtil.send(event.getPlayer(),ChatColor.RED +  "New version available: " + ChatColor.RESET + version + ChatColor.RED + "\n Get it here:");
					event.getPlayer().sendMessage("  http://dev.bukkit.org/bukkit-plugins/build-my-thing/files");
					event.getPlayer().sendMessage(ChatColor.GRAY + "Update checker can be disabled in the config.yml");
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent event){
		if(event.getPlayer().hasMetadata("inbmt")){
			if(instance.getRoomByName(event.getPlayer().getMetadata("inbmt").get(0).asString()) != null){
				if(instance.getRoomByName(event.getPlayer().getMetadata("inbmt").get(0).asString()).isStarted()){
					if(instance.getRoomByName(event.getPlayer().getMetadata("inbmt").get(0).asString()).getBuilder().getName() == event.getPlayer().getName()){
						ChatUtil.send(event.getPlayer(), instance.translator.get("no-chat-while-builder"));
						event.setCancelled(true);
					} else {
						event.setCancelled(true);
						String word = instance.getRoomByName(event.getPlayer().getMetadata("inbmt").get(0).asString()).getWord();
						if(instance.getRoomByName(event.getPlayer().getMetadata("inbmt").get(0).asString()).hasFound(event.getPlayer())){
							ChatUtil.send(event.getPlayer(), instance.translator.get("word-already-found"));
						} else if(event.getMessage().toLowerCase().contains(word)){
							instance.getRoomByName(event.getPlayer().getMetadata("inbmt").get(0).asString()).wordFoundBy(event.getPlayer());
						} else {
							instance.getRoomByName(event.getPlayer().getMetadata("inbmt").get(0).asString()).sendMessage(ChatColor.BOLD + event.getPlayer().getName() + ": "+ ChatColor.RESET + event.getMessage().toLowerCase());
						}
					}
				}
			}
		}
	}
	

}
