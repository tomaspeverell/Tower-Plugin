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
	private static final float SPEED = 5.0f;
	private static final float SPREAD = 0f;
	private static final double ODDS = 1./3;
	private static final double FIRE_ODDS = 1./30000000;
	
	private static final float ARROW_G = 0.05f;
	private static final float ARROW_DRAG = 0.01f;
	private static final float ARROW_T_VEL = 5.0f;
	
	private static final float ARROW_TIME = 20.0f; //2.5 seconds of flight 
	
	private static final int TWILIGHT = 13000;
	private static final int DAWN = 22000;
	
	
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
	//	Bukkit.broadcastMessage("TICK");
		//Bukkit.broadcastMessage(name + " ticked.");
		if(w.getBlockAt(x, y, z).getType() != TOWER_BLOCK_TYPE)
			w.getBlockAt(x, y, z).setType(TOWER_BLOCK_TYPE);
		
		for(Player p : playersList) {
			if(shouldShoot()) {
				Location playerLocation = p.getEyeLocation();
				Vector playerVelocity = PlayerVelocityRunnable.getVelocity(p);
				Location arrowOrigin = new Location(w, x, y + 2, z); //two above the tower block
				/*
				Vector arrowVelocity = (new Vector(playerLocation.getX() - arrowOrigin.getX(),
												  playerLocation.getY() - arrowOrigin.getY(),
												  playerLocation.getZ() - arrowOrigin.getZ()))
												  .normalize()
												  .multiply(SPEED);
				*/
				Vector arrowVelocity = new Vector(
						vox(playerLocation, arrowOrigin, playerVelocity), 
						voy(playerLocation, arrowOrigin, playerVelocity), 
						voz(playerLocation, arrowOrigin, playerVelocity));
				
				Arrow a; 
				if(p.getPotionEffect(PotionEffectType.INVISIBILITY) != null 
					&& p.getPotionEffect(PotionEffectType.GLOWING) == null)
					a = w.spawnArrow(arrowOrigin, arrowVelocity, (float)arrowVelocity.length(), SPREAD, SpectralArrow.class);
				else {
					a = w.spawnArrow(arrowOrigin, arrowVelocity, (float)arrowVelocity.length(), SPREAD);
					if(shouldShootFlame())
						a.setFireTicks(10000000);
				}
				a.setCustomName("the " + name + " archer");
			//	Bukkit.broadcastMessage("TICK");
			}
		}
	}
	
	private float vox(Location player, Location origin, Vector playerVelocity) {
		/*
		return (float)((player.getBlockX() 
			  + playerVelocity.getX() * ARROW_TIME 
			  + (1. / ARROW_DRAG) * Math.exp(-1 * ARROW_DRAG) 
			  - origin.getX())
			  / ARROW_TIME);
		*/
		//return (float) (ARROW_DRAG * Math.exp(ARROW_DRAG * ARROW_TIME) * (origin.getX() - player.getX()));
		return (float) (
				(player.getX() - origin.getX()  + playerVelocity.getX() * ARROW_TIME)
				/ (1.0 - Math.exp(-1.0 * ARROW_G * ARROW_TIME / ARROW_T_VEL))
				* (ARROW_G / ARROW_T_VEL)
				);
	}
	
	private float voy(Location player, Location origin, Vector playerVelocity) {
		/*
		return (float)((player.getBlockY() 
				  + playerVelocity.getY() * ARROW_TIME 
				  - origin.getY()
				  - ARROW_G * (1. / ARROW_DRAG) * ARROW_TIME)
				  * ARROW_DRAG
				  * Math.exp(ARROW_DRAG * ARROW_TIME)
				  + ARROW_G / ARROW_DRAG);
		*/
		/*return (float)(((
				player.getY()
				- origin.getY())
				+ (ARROW_G * Math.pow(ARROW_TIME, 2.0) / 2.0))
				/ ARROW_TIME);	
				 
		*/
		return (float)(
				(player.getY() - origin.getY() + playerVelocity.getY() * ARROW_TIME + ARROW_T_VEL * ARROW_TIME)
				/ (1.0 - Math.exp(-1.0 * ARROW_G * ARROW_TIME / ARROW_T_VEL))
				* (ARROW_G / ARROW_T_VEL)
				- ARROW_T_VEL
				);
	}
	
	private float voz(Location player, Location origin, Vector playerVelocity) {
		/*
		return (float)((player.getBlockZ() 
				  + playerVelocity.getZ() * ARROW_TIME 
				  + (1. / ARROW_DRAG) * Math.exp(-1 * ARROW_DRAG) 
				  - origin.getZ())
				  / ARROW_TIME);
		 */
		//return (float) (-1.0 * ARROW_DRAG * Math.exp(ARROW_DRAG * ARROW_TIME) * (origin.getZ() - player.getZ()));
		return (float) (
				(player.getZ() - origin.getZ() + playerVelocity.getZ() * ARROW_TIME)
				/ (1.0 - Math.exp(-1.0 * ARROW_G * ARROW_TIME / ARROW_T_VEL))
				* (ARROW_G / ARROW_T_VEL)
				);
	}
	
	private boolean shouldShoot() {
		//return true;
		return Math.random() * (1 / ODDS)  <= 1.0;
	}
	
	private boolean shouldShootFlame() {
		return Math.random() * (1 / FIRE_ODDS)  <= 1.0;
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

