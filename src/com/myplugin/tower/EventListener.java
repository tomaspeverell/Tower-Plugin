package com.myplugin.tower;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

public class EventListener implements Listener {
	TowersListRunnable towers;
	
	public EventListener(TowersListRunnable towers) {
		this.towers = towers;
	}
	
	@EventHandler
	public void onChunkLoad(ChunkLoadEvent event) {
		if(!Main.isOverworld(event.getWorld()))
			return;
		Chunk c = event.getChunk();
		towers.loadTower(c.getX(), c.getZ());
	}
	
	@EventHandler
	public void onChunkUnload(ChunkUnloadEvent event) {
		if(!Main.isOverworld(event.getWorld()))
			return;
		Chunk c = event.getChunk();
		towers.unloadTower(c.getX(), c.getZ());
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		if(!Main.isOverworld(event.getTo().getWorld()))
			return;
		towers.updatePlayerList(event.getPlayer());	
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Block b = event.getBlock();
		if(!Main.isOverworld(b.getWorld()))
			return;
		if(b.getType() != Tower.TOWER_BLOCK_TYPE)
			return;
		towers.removeIfTower(b.getX(), b.getY(), b.getZ());
	}
}
