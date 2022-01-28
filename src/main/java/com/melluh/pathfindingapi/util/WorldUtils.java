package com.melluh.pathfindingapi.util;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;

public class WorldUtils {

	private WorldUtils() {}
	
	public static Location normaliseLocation(Location loc) {
		Location result = loc.clone();
		result.setY(Math.floor(loc.getY()));
		return result;
	}
	
	public static boolean canStandIn(Material material) {
		// TODO: expand check
		return !material.isSolid() && !isLiquid(material);
	}
	
	public static boolean canStandOn(Material material) {
		// TODO: expand check
		if(Tag.FENCES.isTagged(material) || Tag.FENCE_GATES.isTagged(material))
			return false;
		
		return material.isSolid();
	}
	
	public static boolean isLiquid(Material material) {
		return material == Material.WATER || material == Material.LAVA;
	}
	
}
