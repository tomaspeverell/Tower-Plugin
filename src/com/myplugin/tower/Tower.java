package com.myplugin.tower;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.SpectralArrow;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class Tower {
	private int x, y, z;
	private int chunk_x, chunk_z;
	private String team;
	private String name;
	
	private static final int RANGE = 60;
	private static final float SPEED = 8.0f;
	private static final float SPREAD = 2f;
	private static final double ODDS = 1./3;
	private static final double FIRE_ODDS = 1./3;
	public static final int TWILIGHT = 13000;
	public static final int DAWN = 22000;
	
	public static final Material TOWER_BLOCK_TYPE = Material.OBSIDIAN;
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
		//Bukkit.broadcastMessage(name + " ticked.");
		if(w.getBlockAt(x, y, z).getType() != TOWER_BLOCK_TYPE)
			w.getBlockAt(x, y, z).setType(TOWER_BLOCK_TYPE);
		for(Player p : playersList) {
			if(Math.random() * (1 / ODDS)  > 1.0) return;
			Location l = p.getLocation();
			Vector v = new Vector(l.getX() - x, l.getY() - y + 1, l.getZ() - z);
			Arrow a;
			if(p.getPotionEffect(PotionEffectType.INVISIBILITY) != null 
				&& p.getPotionEffect(PotionEffectType.GLOWING) == null)
				a = w.spawnArrow(new Location(w, x + 1, y, z), v, SPEED, SPREAD, SpectralArrow.class);
			else {
				a = w.spawnArrow(new Location(w, x + 1, y, z), v, SPEED, SPREAD);
				if(Math.random() * (1 / FIRE_ODDS)  <= 1.0)
					a.setFireTicks(10000000);
			}
			a.setCustomName("the " + name + " archer");
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
	
	public int getY() {
		return y;
	}
	
	public int getZ() {
		return z;
	}

	public int getChunkX() {
		return chunk_x;
	}

	public int getChunkZ() {
		return chunk_z;
	}
	public ArrayList<String> getAllAttr() {
		ArrayList<String> info = new ArrayList<String>();
		info.add(x + "");
		info.add(y + "");
		info.add(z + "");
		info.add(chunk_x + "");
		info.add(chunk_z + "");
		info.add(team);
		info.add(name);
		return info;
	}
}

