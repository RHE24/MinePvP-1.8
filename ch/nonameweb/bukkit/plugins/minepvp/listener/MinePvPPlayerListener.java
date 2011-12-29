package ch.nonameweb.bukkit.plugins.minepvp.listener;

import net.sacredlabyrinth.phaed.simpleclans.Helper;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import ch.nonameweb.bukkit.plugins.minepvp.Clan;
import ch.nonameweb.bukkit.plugins.minepvp.MinePvP;
import ch.nonameweb.bukkit.plugins.minepvp.manager.ClanManager;

public class MinePvPPlayerListener extends PlayerListener{

	private MinePvP plugin;
	private ClanManager clanManager;
	
	/**
	 * 
	 */
	public MinePvPPlayerListener() {
		plugin = MinePvP.getInstance();
		clanManager = plugin.getClanManager();
	}
	
	/**
	 * 
	 */
	public void onPlayerCommandPreprocess( PlayerCommandPreprocessEvent event ) {
		
		if ( event.isCancelled() ) {
			return;
		}
		
		Player player = event.getPlayer();
		
		if ( player == null ) {
			return;
		}
		
		String[] split = event.getMessage().substring(1).split(" ");
		
		if ( split.length == 0 ) {
			return;
		}
		
		String command = split[0];
		
		if ( command.equalsIgnoreCase("minepvp") ) {
			
			plugin.log(" Command From : " + event.getPlayer().getName() + " : " + event.getMessage().toString() );
			
			plugin.getCommandManager().processMinePvP(player, Helper.removeFirst(split));
		}
	}
	
	/**
	 * 
	 */
	public void onPlayerMove ( PlayerMoveEvent event ) {
		
		if ( event.isCancelled() ) {
			return;
		}
		
		Player player = event.getPlayer();
		
		if ( player == null ) {
			return;
		}
		
		Clan playerClan = clanManager.getClanByPlayer(player);
		Clan clanFrom = clanManager.getClanByLocation( event.getFrom() );
		Clan clanTo = clanManager.getClanByLocation( event.getTo() );
		
		if ( clanFrom == null ) {
			
			if ( clanTo != null ) {
				
				// Ist mindestens Spieler Online vom Clan
				if ( clanManager.canClanAttackTheClan(playerClan, clanTo) || clanTo.getName().equalsIgnoreCase( playerClan.getName() ) ) {
					// AlarmSystem
					
					if ( playerClan != null ) {
						if ( clanTo.getName().equalsIgnoreCase( playerClan.getName() ) != true ) {
							
							if ( clanTo.getAlertsystem() == 1 ) {
								
								clanManager.sendClanMessage(clanTo, ChatColor.RED + "Ein feindlicher Spieler betritt euer Gebiet!");
								
							} else if ( clanTo.getAlertsystem() == 2 ) {
								
								clanManager.sendClanMessage(clanTo, ChatColor.RED + "Ein feindlicher Spieler vom Clan " + playerClan.getName() + " betritt euer Gebiet!");
								
							} else if ( clanTo.getAlertsystem() == 3 ) {
								
								clanManager.sendClanMessage(clanTo, ChatColor.RED + "Ein feindlicher Spieler " + player.getName() + " vom Clan " + playerClan.getName() + " betritt euer Gebiet!");
								
							}
							
						}
					} else {
						
						if ( clanTo.getAlertsystem() == 1 ) {
							
							clanManager.sendClanMessage(clanTo, ChatColor.RED + "Ein feindlicher Spieler betritt euer Gebiet!");
							
						} else if ( clanTo.getAlertsystem() == 2 ) {
							
							clanManager.sendClanMessage(clanTo, ChatColor.RED + "Ein feindlicher Spieler ohne Clan betritt euer Gebiet!");
							
						} else if ( clanTo.getAlertsystem() == 3 ) {
							
							clanManager.sendClanMessage(clanTo, ChatColor.RED + "Ein feindlicher Spieler ohne Clan " + playerClan.getName() + " betritt euer Gebiet!");
							
						}
						
					}
					
					player.sendMessage(ChatColor.GOLD + "Du betrittst das gebiet von " + clanTo.getName() + "." );
				} else {
					player.teleport( event.getFrom() );
					player.sendMessage(ChatColor.GOLD + "Um dieses K�nigreich anzugreiffen /minepvp attack " + clanTo.getName() + "");
				}
					
			}
			
		} else {
			
			if ( clanTo == null ) {
				player.sendMessage(ChatColor.GOLD + "Du verl�sst das gebiet von " + clanFrom.getName() + ".");
			} else {
				
				// Wenn in Wasser von einem feindlichen Clan kriegt er schaden // moat / wassergraben
				if ( player.getLocation().getBlock().isLiquid() ) {
					
					// Wenn der Spieler sich in einem Feindlichen Clangebiet bewegt
					if ( clanFrom.getName().equalsIgnoreCase( playerClan.getName() ) != true && clanTo.getName().equalsIgnoreCase( playerClan.getName() ) != true  ) {
						
						// Wenn der Clan einen Wassergraben hat
						if ( clanFrom.getMoat() == true ) {
							clanManager.playerMoatDamage(player);
						}
						
					}
					
				}
				
				
			}
			
		}
		
	}
	
	/**
	 * 
	 */
	public void onPlayerQuit ( PlayerQuitEvent event ) {
		
		Player player = event.getPlayer();
		
		if ( player == null ) {
			return;
		}
		
		
		
		
		
		// Wenn jemand noch eine Flagge hat wird diese Resettet
		plugin.getClanManager().resetFlag(player);
		
	}
	
	public void onPlayerKick( PlayerKickEvent event) {
		
		Player player = event.getPlayer();
		
		if ( player == null ) {
			return;
		}
		
		// Wenn jemand noch eine Flagge hat wird diese Resettet
		plugin.getClanManager().resetFlag(player);
		
		
	}
	
	/**
	 * 
	 */
	public void onPlayerTeleport ( PlayerTeleportEvent event ) {
		
		Player player = event.getPlayer();
		
		if ( player == null ) {
			return;
		}
		
		if ( event.getFrom().getWorld().getName().equalsIgnoreCase("world") ) {
			
			if ( event.getTo().getWorld().getName().equalsIgnoreCase("world") ) {
				if ( event.getFrom().distance( event.getTo() ) > 5 ) {
				
					// Wenn jemand noch eine Flagge hat wird diese Resettet	
					plugin.getClanManager().resetFlag(player);
					
				}
			}
			
		}		
		
	}
	
	public void onPlayerJoin ( PlayerJoinEvent event ) {
		
		Player player = event.getPlayer();
		
		Clan playerClan = clanManager.getClanByPlayer(player);
		Clan clanLand = clanManager.getClanByLocation( player.getLocation() );
		
		if ( clanLand != null ) {
			
			if ( playerClan != null ) {
				if ( playerClan.getName().equalsIgnoreCase( clanLand.getName() ) ) {
					
				} else {
					
					// Wenn der Clan einen Spawn hat dahin sonst zum World Spawn
					if ( playerClan.getClanSpawn() ) {
						if ( playerClan.getClanSpawnX() != null ) {
							player.teleport( playerClan.getClanSpawnLocation() );
						} else {
							player.teleport( playerClan.getBaseLocation() );
						}
						
					} else {
						player.teleport( new Location(plugin.getServer().getWorld("world"), plugin.getServer().getWorld("world").getSpawnLocation().getX(), plugin.getServer().getWorld("world").getSpawnLocation().getY() + 4, plugin.getServer().getWorld("world").getSpawnLocation().getZ()) );
					}
					
					player.sendMessage(ChatColor.GOLD + "Du warst im Gebiet eines Feindlichen Clans wo keine Spieler Online sind.");
				}
			} else {
				
				player.teleport( new Location(plugin.getServer().getWorld("world"), plugin.getServer().getWorld("world").getSpawnLocation().getX(), plugin.getServer().getWorld("world").getSpawnLocation().getY() + 4, plugin.getServer().getWorld("world").getSpawnLocation().getZ()) );
				
				player.sendMessage(ChatColor.GOLD + "Du warst im Gebiet eines Feindlichen Clans wo keine Spieler Online sind.");
				
			}
			
			
			
		}
		
	}
	
	public void onPlayerBucketEmpty (PlayerBucketEmptyEvent event) {
		
		
		Player player = event.getPlayer();
		Material bucket = event.getBucket();
		
		Clan clanLand = clanManager.getClanByLocation( event.getBlockClicked().getLocation() );
		
		// Ist es in einen gebit von einem Clan?
		if ( clanLand != null ) {
		
			// Hat der Spieler einen Clan?
			if ( clanManager.hasPlayerAClan(player) ) {
				
				// Ist er im gleichen Clan wie das Land?
				if ( clanLand.getName().equalsIgnoreCase( clanManager.getClanNameByPlayer(player) ) ) {
					
					
					
				} else {
					
					// Wasser / Lava unterbinden
					if ( bucket.getId() == 326 || bucket.getId() == 327 ) {
						event.setCancelled(true);
					}					
					
					clanManager.playerDamage(player);
				}
				
			} else {
				
				// Wasser / Lava unterbinden
				if ( bucket.getId() == 326 || bucket.getId() == 327 ) {
					event.setCancelled(true);
				}	
				
				clanManager.playerDamage(player);			
			}
			
		} else {
			
		}
		
		
		
	}
	
	public void onPlayerInteract(PlayerInteractEvent event) {
		
		Player player = event.getPlayer();
		Block block = event.getClickedBlock();
		
		try {
			
			Clan clanLand = clanManager.getClanByLocation( event.getClickedBlock().getLocation() );
			
			// Ist es in einen gebit von einem Clan?
			if ( clanLand != null ) {
			
				// Hat der Spieler einen Clan?
				if ( clanManager.hasPlayerAClan(player) ) {
					
					// Ist er im gleichen Clan wie das Land?
					if ( clanLand.getName().equalsIgnoreCase( clanManager.getClanNameByPlayer(player) ) ) {
						
					} else {
						
						// Chests and Furnances
						if ( block.getTypeId() == 54 || block.getTypeId() == 61 || block.getTypeId() == 62 || block.getTypeId() == 26 || block.getTypeId() == 23 || block.getTypeId() == 116 || block.getTypeId() == 117 || block.getTypeId() == 58) {
							clanManager.playerDamage(player);
							event.setCancelled(true);
						}					
						
					}
					
				} else {
					
					// Chests and Furnances
					if ( block.getTypeId() == 54 || block.getTypeId() == 61 || block.getTypeId() == 62 || block.getTypeId() == 26 || block.getTypeId() == 23 || block.getTypeId() == 116 || block.getTypeId() == 117 || block.getTypeId() == 58) {
						clanManager.playerDamage(player);
						event.setCancelled(true);
					}	
					
				}
				
			} else {
				
			}
			
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		
		
	}
	
	
}
