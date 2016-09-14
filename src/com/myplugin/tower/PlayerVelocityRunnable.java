package com.myplugin.tower;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Queue;
import java.util.TreeMap;
import java.util.concurrent.ArrayBlockingQueue;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class PlayerVelocityRunnable implements Runnable {
	
	public static final long PERIOD = 20L; //max 20(= 1 second)
	public static final int MEMORY = 10 * (int)(20 / PERIOD); //no. of seconds 
	
	//private static TreeMap<Player, Vector> velocities;
	private static TreeMap<Player, ArrayBlockingQueue<Location>> locations;
	
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
		/*
		velocities = new TreeMap<Player, Vector>(comp);
		for(Player p : Bukkit.getOnlinePlayers()) 
			velocities.put(p, new Vector(0, 0, 0));
		*/
		locations = new TreeMap<Player, ArrayBlockingQueue<Location>>(comp);
		for(Player p : Bukkit.getOnlinePlayers()) {
			ArrayBlockingQueue<Location> temp = new ArrayBlockingQueue<Location>(MEMORY);
			for(int i = 0; i < MEMORY; i++)
				temp.offer(p.getLocation().clone());
			locations.put(p, temp);
		}
	}
	
	public void addPlayer(Player p) {
		//velocities.put(p, new Vector(0, 0, 0));
		ArrayBlockingQueue<Location> temp = new ArrayBlockingQueue<Location>(MEMORY);
		for(int i = 0; i < MEMORY; i++)
			temp.offer(p.getLocation().clone());
		locations.put(p, temp);
	}
	
	public void removePlayer(Player p) {
		//velocities.remove(p);
		locations.remove(p);
	}
	
	public static Vector getVelocity(Player p, int delta) {
		if(--delta > MEMORY || delta < 0)
			return null;
		Location[] ls = (Location[])locations.get(p).toArray();
		Location l1 = ls[MEMORY - 1 - delta], l2 = ls[MEMORY - 1];
		return l2.toVector().subtract(l1.toVector()).multiply(1. / (delta * PERIOD));
		//return velocities.get(p);
	}
	
	@Override
	public void run() {
		for(Player p : locations.keySet()) {
			ArrayBlockingQueue<Location> temp = locations.get(p);
			temp.poll(); 
			try {
				temp.put(p.getLocation().clone());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			locations.put(p, temp);
			//Bukkit.broadcastMessage(velocities.get(p) + "");
		}
		
	}

}
