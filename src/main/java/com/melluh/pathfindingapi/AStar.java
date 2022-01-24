package com.melluh.pathfindingapi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;

import com.melluh.pathfindingapi.util.BlockPosition;
import com.melluh.pathfindingapi.util.WorldUtils;

public class AStar {

	private Pathfinder pathfinder;
	
	private World world;
	private PathNode startNode;
	private BlockPosition goal;
	
	private Set<PathNode> openNodes = new HashSet<>();
	private Set<PathNode> closedNodes = new HashSet<>();
	private Map<BlockPosition, PathNode> nodes = new HashMap<>();
	private int visitedNodes;
	
	private Path resultPath;
	
	protected AStar(Pathfinder pathfinder, World world, BlockPosition start, BlockPosition goal) {
		this.pathfinder = pathfinder;
		this.world = world;
		this.startNode = new PathNode(start);
		this.goal = goal;
	}
	
	public void findPath() {
		startNode.setParent(null, 0);
		this.addOpenNode(startNode);
		
		while(!openNodes.isEmpty()) {
			if(visitedNodes >= pathfinder.getMaxNodeVisits()) {
				return;
			}
			
			PathNode currentNode = this.getCurrentNode(); // Node with lowest F cost
			if(currentNode.getPosition().equals(goal)) {
				this.resultPath = new Path(this.getPath(currentNode));
				return;
			}
			
			openNodes.remove(currentNode);
			closedNodes.add(currentNode);
			
			this.visitNode(currentNode);
		}
	}
	
	public boolean pathFound() {
		return resultPath != null;
	}
	
	public Path getPath() {
		return resultPath;
	}
	
	private void visitNode(PathNode node) {
		visitedNodes++;
		
		for(int dX = -1; dX <= 1; dX++) {
			for(int dY = -1; dY <= 1; dY++) {
				for(int dZ = -1; dZ <= 1; dZ++) {
					// Skip if not moving
					if(dX == 0 && dY == 0 && dZ == 0)
						continue;
					
					// Diagonal movement is not allowed when:
					// - It is disabled in the pathfinder
					// - Moving up/down
					if(dX * dZ != 0 && (!pathfinder.canMoveDiagonally() || dY != 0 || !this.canMoveDiagonally(node.getPosition().toLocation(world), dX, dZ)))
						continue;
					
					BlockPosition neighbourPos = node.getPosition().getRelative(dX, dY, dZ);
					if(!this.canStandAt(neighbourPos.toLocation(world)))
						continue;
					
					PathNode neighbour = this.getNode(neighbourPos);
					
					double tentativeGCost = node.getGCost() + node.getPosition().fastDistanceTo(neighbourPos);
					if(tentativeGCost < neighbour.getGCost()) { // check if this path is better than the previous one
						neighbour.setParent(node, tentativeGCost);
						this.addOpenNode(neighbour);
					}
				}
			}
		}
	}
	
	private boolean canStandAt(Location feetLoc) {
		return this.isUnobstructed(feetLoc) &&
				WorldUtils.canStandOn(feetLoc.getBlock().getRelative(BlockFace.DOWN).getType());
	}
	
	private boolean canMoveDiagonally(Location feetLoc, int dX, int dZ) {
		return this.isUnobstructed(feetLoc.clone().add(dX, 0, 0)) &&
				this.isUnobstructed(feetLoc.clone().add(0, 0, dZ));
	}
	
	private boolean isUnobstructed(Location feetLoc) {
		return WorldUtils.canStandIn(feetLoc.getBlock().getType()) &&
				WorldUtils.canStandIn(feetLoc.getBlock().getRelative(BlockFace.UP).getType());
	}
	
	private void addOpenNode(PathNode node) {
		double distanceToEnd = node.getPosition().fastDistanceTo(goal);
		node.setHCost(distanceToEnd);
		openNodes.add(node);
	}
	
	private PathNode getNode(BlockPosition pos) {
		PathNode node = nodes.get(pos);
		if(node != null) {
			return node;
		}
		
		node = new PathNode(pos);
		nodes.put(pos, node);
		return node;
	}
	
	// Linear scan is more efficient than sorting - O(n) vs O(n log n)
	private PathNode getCurrentNode() {
		PathNode lowest = null;
		double lowestVal = Double.MAX_VALUE;
		
		for(PathNode node : openNodes) {
			double val = node.getFCost();
			if(val < lowestVal) {
				lowest = node;
				lowestVal = val;
			}
		}
		
		return lowest;
	}
	
	private List<PathNode> getPath(PathNode end) {
		List<PathNode> path = new ArrayList<>();
		
		PathNode node = end;
		while(node != null) {
			path.add(node);
			node = node.getParent();
		}
		
		Collections.reverse(path);
		return path;
	}
	
}
