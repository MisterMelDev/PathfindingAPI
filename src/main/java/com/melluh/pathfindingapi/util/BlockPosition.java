package com.melluh.pathfindingapi.util;

import java.util.Objects;

import org.bukkit.Location;
import org.bukkit.World;

public class BlockPosition {
	
	private final int x, y, z;
	
	public BlockPosition(Location loc) {
		this.x = loc.getBlockX();
		this.y = loc.getBlockY();
		this.z = loc.getBlockZ();
	}
	
	public BlockPosition(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Location toLocation(World world) {
		return new Location(Objects.requireNonNull(world, "world is missing"), x, y, z);
	}
	
	// Performance difference is MAJOR for large operations
	// Example: path of 130 nodes - 1476ms with distanceTo(), ~5ms with fastDistanceTo()
	public double fastDistanceTo(BlockPosition pos2) {
		return Math.pow(this.x - pos2.x, 2) + Math.pow(this.y - pos2.y, 2) + Math.pow(this.z - pos2.z, 2);
	}
	
	public double distanceTo(BlockPosition pos2) {
		return Math.sqrt(this.fastDistanceTo(pos2));
	}
	
	public BlockPosition getRelative(int dX, int dY, int dZ) {
		return new BlockPosition(x + dX, y + dY, z + dZ);
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
	
	@Override
	public boolean equals(Object obj) {
		if(obj == this)
			return true;
		
		if(!(obj instanceof BlockPosition))
			return false;
		
		BlockPosition pos2 = (BlockPosition) obj;
		return pos2.x == x && pos2.y == y && pos2.z == z;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(x, y, z);
	}
	
}
