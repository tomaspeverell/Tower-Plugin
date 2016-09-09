package com.myplugin.tower;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TowersListRunnable implements Runnable {
	public static final long PERIOD = 40L; 
	
	private  ArrayList<Tower> activeTowers;
	private  ArrayList<Tower> inactiveTowers;
	
	private static TowersListRunnable me; 
	
	public static TowersListRunnable getInstance() {
		if(me == null)
			me = new TowersListRunnable();
		
		return me;
	}

	private TowersListRunnable() {
		//read towers from yaml
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
		return true;
	}
	
	public boolean removeTower(String team, String name) {
		Tower t = getTower(team, name);
		if(t == null) return false;
		else {
			activeTowers.remove(t);
			inactiveTowers.remove(t);
		}
		return true;
	}
	
	public void loadTower(int chunk_x, int chunk_z) {
		for(Tower t : inactiveTowers) 
			if(t.isTowerChunk(chunk_x, chunk_z)) {
				activeTowers.add(t);
				Bukkit.broadcastMessage("Tower " + t.getName() + " is active");
				inactiveTowers.remove(t);
				break;
			}
	}
	
	public void unloadTower(int chunk_x, int chunk_z) {
		for(Tower t : activeTowers) 
			if(t.isTowerChunk(chunk_x, chunk_z)) {
				inactiveTowers.add(t);
				Bukkit.broadcastMessage("Tower " + t.getName() + " is inactive");
				activeTowers.remove(t);
				break;
			}		
	}
	
	public void updatePlayerList(Player p) {
		for(Tower t : activeTowers)
			t.updatePlayersList(p);
	}
	
	public void listTowersToPlayer(Player p) {
		String message = new String("Current towers: \n");
		for(Tower t : activeTowers) 
			if(t.getTeam().equals(Main.getTeam(p)))
				message = message.concat(" - " + t.getName() + " at x: " + t.getX() + ", z: " + t.getZ() + "\n");
		for(Tower t : inactiveTowers) 
			if(t.getTeam().equals(Main.getTeam(p)))
				message = message.concat(" - " + t.getName() + " at x: " + t.getX() + ", z: " + t.getZ() + "\n");
		p.sendMessage(message);
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
	
}
