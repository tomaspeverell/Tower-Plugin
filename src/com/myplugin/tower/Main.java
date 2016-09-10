package com.myplugin.tower;

import java.util.ArrayList;
import java.util.Set;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Team;

public class Main extends JavaPlugin {
	
	private static final String gamerule = "activeTowers";
	private static final String path = "plugins/Tower/list.yaml";
	
	TowersListRunnable towers;
	private int runnableId;
	
	@Override
	public void onEnable() {
		Bukkit.broadcastMessage("Hi y'all");
		World w = Bukkit.getWorlds().get(0);
		if(w.isGameRule(gamerule) && w.getGameRuleValue(gamerule).equals("false"))
			Bukkit.getServer().getPluginManager().disablePlugin(this);
		towers = TowersListRunnable.getInstance();
		towers.readFile(path);
		Bukkit.getServer()
			  .getPluginManager()
			  .registerEvents(new EventListener(towers), this);
		runnableId = Bukkit.getServer()
						   .getScheduler()
						   .scheduleSyncRepeatingTask(this, towers, 0L, TowersListRunnable.PERIOD);
		super.onEnable();
	}
	
	@Override
	public void onDisable() {
		Bukkit.getServer().getScheduler().cancelTask(runnableId);
		towers.writeFile(path);
		Bukkit.broadcastMessage("Bye y'all");
		super.onDisable();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if(sender instanceof Player) {
			if(!sender.isOp()) {
				sender.sendMessage(ChatColor.RED + "You have to be an Op to use this command");
				return true;
			}
			if(command.getLabel().equals("addtower")) {
				if(!isOverworld(((Player) sender).getWorld())) {
					sender.sendMessage("You can only use this command in the Overworld");
					return true;
				}
				if(args.length == 1) {
					Location l = ((Player) sender).getLocation();
					String team = getTeam((Player) sender);
					if(team == null) {
						sender.sendMessage("You are not part of a team");
						return true;
					}
					if(towers.addNewTower(l.getBlockX(), l.getBlockY(), l.getBlockZ(), 
										  l.getChunk().getX(), l.getChunk().getZ(), 
										  team, args[0])) { 
						sender.sendMessage("Tower added");
						return true;
					}
					else { 
						sender.sendMessage("Tower already exists");
						return true;
					}
				}
			}
			else if(command.getLabel().equals("removetower")) {
				if(args.length == 1) {
					Location l = ((Player) sender).getLocation();
					String team = getTeam((Player) sender);
					if(team == null) {
						sender.sendMessage("You are not part of a team");
						return true;
					}
					if(towers.removeTower(team, args[0])) { 
						sender.sendMessage("Tower removed");
						return true;
					}
					else { 
						sender.sendMessage("Tower doesn't exist");
						return true;
					}
				}
			}
			else if(command.getLabel().equals("towerlist")) {
				String team = getTeam((Player) sender);
				if(team == null) {
					sender.sendMessage("You are not part of a team");
					return true;
				}
				if(args.length == 0) {
					ArrayList<ArrayList<String>> allTowersInfo = towers.getTowersListForTeam(team);
					String message = new String();
					if(allTowersInfo.isEmpty())
						message = message.concat("There are no towers in your team faction");
					else {
						message = new String("Current towers: \n");
						for(ArrayList<String> al_s : allTowersInfo)
							message = message.concat(
									" - " 
									+ al_s.get(6) 
									+ " at x: " + al_s.get(0)
									+ ", y: " + al_s.get(1)
									+ ", z: " + al_s.get(2) 
									+ "\n");
					}
					sender.sendMessage(message);
					return true;
				}
			}
		}
		return false;
	}
	
	public static String getTeam(Player p) {
		Set<Team> teams = Bukkit.getScoreboardManager().getMainScoreboard().getTeams();
		for(Team t : teams) {
			if(t.hasPlayer(p))
				return t.getName();
		}
		return null;
	}
	
	public static boolean isOverworld(World w) {
		if(w.getName().endsWith("_nether") 
			|| w.getName().endsWith("_the_end"))
			return false;
		return true;
	}
	
}
