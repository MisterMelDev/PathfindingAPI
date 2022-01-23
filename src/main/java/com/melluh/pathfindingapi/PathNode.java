package com.melluh.pathfindingapi;

import java.util.Objects;

import com.melluh.pathfindingapi.util.BlockPosition;

public class PathNode {

	private double gCost = Double.MAX_VALUE;
	private double hCost = Double.MAX_VALUE;
	
	private final BlockPosition position;
	private PathNode parent;
	
	protected PathNode(BlockPosition position) {
		this.position = Objects.requireNonNull(position, "position is missing");
	}
	
	protected void setParent(PathNode parent, double gCost) {
		this.parent = parent;
		this.gCost = gCost;
	}
	
	protected void setHCost(double hCost) {
		this.hCost = hCost;
	}
	
	// G cost: distance from starting node
	public double getGCost() {
		return gCost;
	}
	
	// H cost: distance from end node
	public double getHCost() {
		return hCost;
	}
	
	// F cost: total cost (G cost + H cost)
	public double getFCost() {
		return gCost + hCost;
	}
	
	public BlockPosition getPosition() {
		return position;
	}
	
	public PathNode getParent() {
		return parent;
	}
	
}
