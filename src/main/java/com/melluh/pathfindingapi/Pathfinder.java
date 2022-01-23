package com.melluh.pathfindingapi;

import java.util.Optional;

import org.bukkit.Location;

import com.melluh.pathfindingapi.util.BlockPosition;

public class Pathfinder {
	
	private int maxNodeVisits;
	private boolean moveDiagonally;
	
	private Pathfinder(int maxNodeVisits, boolean moveDiagonally) {
		this.maxNodeVisits = maxNodeVisits;
		this.moveDiagonally = moveDiagonally;
	}
	
	public Optional<Path> calculatePath(Location origin, Location goal) {
		if(origin.getWorld() != goal.getWorld())
			throw new IllegalStateException("Cannot pathfind across worlds");
		
		AStar alghoritm = new AStar(this, new BlockPosition(origin), new BlockPosition(goal));
		alghoritm.findPath();
		return Optional.ofNullable(alghoritm.getPath());
	}
	
	protected int getMaxNodeVisits() {
		return maxNodeVisits;
	}
	
	protected boolean canMoveDiagonally() {
		return moveDiagonally;
	}
	
	public static class Builder {
		
		private int maxNodeVisits = 500;
		private boolean moveDiagonally = true;
		
		public Builder setMaxNodeVisits(int maxNodeVisits) {
			this.maxNodeVisits = maxNodeVisits;
			return this;
		}
		
		public Builder setMoveDiagonally(boolean moveDiagonally) {
			this.moveDiagonally = moveDiagonally;
			return this;
		}
		
		public Pathfinder build() {
			return new Pathfinder(maxNodeVisits, moveDiagonally);
		}
		
	}
	
}
