package com.myplugin.tower;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class TowersListRunnable implements Runnable {
	public static final long PERIOD = 2L;

	private  ArrayList<Tower> activeTowers;
	private  ArrayList<Tower> inactiveTowers;
	
	private static TowersListRunnable me;  
	
	public static TowersListRunnable getInstance() {
		if(me == null)
			me = new TowersListRunnable();
		
		return me;
	}

	private TowersListRunnable() {
		activeTowers = new ArrayList<Tower>();
		inactiveTowers = new ArrayList<Tower>();
	}
	
	@Override
	public void run() {
		for(Tower t : activeTowers)
			t.tick();
	}

	public boolean addNewTower(int x, int y, int z, int chunk_x, int chunk_z, String team, String name) {
		if(getTower(team, name) != null) return false;
		activeTowers.add(new Tower(x, y, z, chunk_x, chunk_z, team, name));
		Bukkit.getWorlds().get(0).getBlockAt(x, y, z).setType(Tower.TOWER_BLOCK_TYPE);
		return true;
	}
	
	public boolean removeTower(String team, String name) {
		Tower t = getTower(team, name);
		return removeTower(t);
	}
	
	public boolean removeTower(Tower t) {
		if(t == null) return false;
		
		activeTowers.remove(t);
		inactiveTowers.remove(t);
		Bukkit.getWorlds().get(0).getBlockAt(t.getX(), t.getY(), t.getZ()).setType(Material.AIR);
		
		return true;
	}
	
	public void removeIfTower(int x, int y, int z) {
		removeTower(getTower(x, y, z));
	}
	
	public void loadTower(int chunk_x, int chunk_z) {
		for(Tower t : inactiveTowers) 
			if(t.isTowerChunk(chunk_x, chunk_z)) {
				activeTowers.add(t);
				//Bukkit.broadcastMessage("Tower " + t.getName() + " is active");
				inactiveTowers.remove(t);
				break;
			}
	}
	
	public void unloadTower(int chunk_x, int chunk_z) {
		for(Tower t : activeTowers) 
			if(t.isTowerChunk(chunk_x, chunk_z)) {
				inactiveTowers.add(t);
				//Bukkit.broadcastMessage("Tower " + t.getName() + " is inactive");
				activeTowers.remove(t);
				break;
			}		
	}
	
	public void updatePlayerList(Player p) {
		for(Tower t : activeTowers)
			t.updatePlayersList(p);
	}
	
	public ArrayList<ArrayList<String>> getTowersListForTeam(String team) {
		ArrayList<ArrayList<String>> allTowersInfo = new ArrayList<ArrayList<String>>();
		for(Tower t : activeTowers)
			if(team.equals(t.getTeam()))
				allTowersInfo.add(t.getAllAttr());
		for(Tower t : inactiveTowers)
			if(team.equals(t.getTeam()))
				allTowersInfo.add(t.getAllAttr());
		return allTowersInfo;
	}
	
	public void readFile(String path) {
		File f = new File(path);
		if(!f.exists())
			return;
		List<String> x, y, z, chunk_x, chunk_z, team, name;
		YamlConfiguration yaml = YamlConfiguration.loadConfiguration(f);
		x = yaml.getStringList("x");
		y = yaml.getStringList("y");
		z = yaml.getStringList("z");
		chunk_x = yaml.getStringList("chunk_x");
		chunk_z = yaml.getStringList("chunk_z");
		team = yaml.getStringList("team");
		name = yaml.getStringList("name");
		
		for(int i = 0; i < x.size(); i++)
			activeTowers.add(new Tower(
					Integer.parseInt(x.get(i)), 
					Integer.parseInt(y.get(i)), 
					Integer.parseInt(z.get(i)), 
					Integer.parseInt(chunk_x.get(i)), 
					Integer.parseInt(chunk_z.get(i)), 
					team.get(i), name.get(i)
					)
			);	
		
		for(Tower t : activeTowers) 
			Bukkit.getWorlds().get(0).loadChunk(t.getChunkX(), t.getChunkZ());
		
	}
	
	public void writeFile(String path) {
		YamlConfiguration yaml = new YamlConfiguration();
		ArrayList<String> x = new ArrayList<String>(), 
						  y = new ArrayList<String>(), 
						  z = new ArrayList<String>(), 
						  chunk_x = new ArrayList<String>(), 
						  chunk_z = new ArrayList<String>(), 
						  team = new ArrayList<String>(), 
						  name = new ArrayList<String>();
		ArrayList<ArrayList<String>> allTowerInfo = new ArrayList<ArrayList<String>>();
		
		for(Tower t : activeTowers) 
			allTowerInfo.add(t.getAllAttr());
		for(Tower t : inactiveTowers) 
			allTowerInfo.add(t.getAllAttr());
		
		for(ArrayList<String> al_s : allTowerInfo) {
			x.add(al_s.get(0));
			y.add(al_s.get(1));
			z.add(al_s.get(2));
			chunk_x.add(al_s.get(3));
			chunk_z.add(al_s.get(4));
			team.add(al_s.get(5));
			name.add(al_s.get(6));
		}
		
		yaml.createSection("x");
		yaml.set("x", x);
		yaml.createSection("y");
		yaml.set("y", y);
		yaml.createSection("z");
		yaml.set("z", z);
		yaml.createSection("chunk_x");
		yaml.set("chunk_x", chunk_x);
		yaml.createSection("chunk_z");
		yaml.set("chunk_z", chunk_z);
		yaml.createSection("team");
		yaml.set("team", team);
		yaml.createSection("name");
		yaml.set("name", name);
		
		try {
			yaml.save(path);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private Tower getTower(String team, String name) {
		for(Tower t : activeTowers)
			if(t.getName().equals(name) && t.getTeam().equals(team))
				return t;
		for(Tower t : inactiveTowers) 
			if(t.getName().equals(name) && t.getTeam().equals(team)) 
				return t;
		return null;
	}
	
	private Tower getTower(int x, int y, int z) {
		for(Tower t : activeTowers)
			if(t.getX() == x && t.getY() == y && t.getZ() == z)
				return t;
		for(Tower t : inactiveTowers) 
			if(t.getX() == x && t.getY() == y && t.getZ() == z)
				return t;
		return null;
	}
	
}
