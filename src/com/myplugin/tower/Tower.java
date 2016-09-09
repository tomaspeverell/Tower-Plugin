package com.myplugin.tower;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class Tower {
	private int x, y, z;
	private int chunk_x, chunk_z;
	private String team;
	private String name;
	
	private static int RANGE = 30;
	private static final World w = Bukkit.getWorlds().get(0);
	
	private ArrayList<Player> playersList;
	
	
	public Tower(int x, int y, int z, int chunk_x, int chunk_z, String team, String name) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.chunk_x = chunk_x;
		this.chunk_z = chunk_z;
		this.team = team;
		this.name = name;
		
		playersList = new ArrayList<Player>();
	}
	
	@SuppressWarnings("deprecation")
	public void updatePlayersList(Player p) {
		Location l = p.getLocation();
		if(!isWithinRange(l.getBlockX(), l.getBlockZ()) || Main.getTeam(p).equals(team)) {
			if(playersList.contains(p))
				playersList.remove(p);
			return;
		}
		if(playersList.contains(p))
			return;
		playersList.add(p);
	}
	
	public boolean isWithinRange(int x, int z) {
		if(Math.abs(this.x - x) <= RANGE && Math.abs(this.z - z) <= RANGE)
			return true;
		return false;
	}
	
	public boolean isTowerChunk(int chunk_x, int chunk_z) {
		if(this.chunk_x == chunk_x && this.chunk_z == chunk_z)
			return true;
		return false;
	}
	
	public void tick() {
		for(Player p : playersList) {
			Location l = p.getLocation();
			Vector v = new Vector(l.getX() - x, l.getY() - y + 1, l.getZ() - z);
			w.spawnArrow(new Location(w, x, y, z), v, 5.0f, 0f);
		}
	}
	
	public String getTeam() {
		return team;
	}
	
	public String getName() {
		return name;
	}
	
	public int getX() {
		return x;
	}
	
	public int getZ() {
		return z;
	}
}

