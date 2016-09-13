package com.myplugin.tower;

import java.util.Comparator;
import java.util.TreeMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class PlayerVelocityRunnable implements Runnable {
	
	public static final long PERIOD = 20L; 
	
	private static TreeMap<Player, Vector> velocities;
	private static TreeMap<Player, Location> locations;
	
	private static PlayerVelocityRunnable me;  
	
	public static PlayerVelocityRunnable getInstance() {
		if(me == null)
			me = new PlayerVelocityRunnable();
		
		return me;
	}

	private PlayerVelocityRunnable() {
		Comparator<Player> comp = new Comparator<Player>() {
			public int compare(Player o1, Player o2) {
				return o1.getName().compareTo(o2.getName());
			}
		};
		velocities = new TreeMap<Player, Vector>(comp);
		for(Player p : Bukkit.getOnlinePlayers()) 
			velocities.put(p, new Vector(0, 0, 0));
		locations = new TreeMap<Player, Location>(comp);
		for(Player p : Bukkit.getOnlinePlayers()) 
			locations.put(p, p.getLocation());
	}
	
	public void addPlayer(Player p) {
		velocities.put(p, new Vector(0, 0, 0));
	}
	
	public void removePlayer(Player p) {
		velocities.remove(p);
	}
	
	public static Vector getVelocity(Player p) {
		return velocities.get(p);
	}
	
	@Override
	public void run() {
		for(Player p : locations.keySet()) {
			Location old = locations.get(p);
			Location current = p.getLocation();
			velocities.put(p, new Vector(current.getX() - old.getX(),
										 current.getY() - old.getY(),
										 current.getZ() - old.getZ())
										.multiply(1. / PERIOD));
			locations.put(p, current);
			//Bukkit.broadcastMessage(velocities.get(p) + "");
		}
		
	}

}
