package com.melluh.pathfindingapi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Bisected.Half;
import org.bukkit.block.data.type.Stairs;

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
		
		BlockPosition nodePos = node.getPosition();
		for(int dX = -1; dX <= 1; dX++) {
			for(int dY = -1; dY <= 1; dY++) {
				for(int dZ = -1; dZ <= 1; dZ++) {
					// Skip if not moving
					if(dX == 0 && dY == 0 && dZ == 0) {
						continue;
					}
					
					// Diagonal movement is not allowed when:
					// - It is disabled in the pathfinder
					// - Moving up/down
					if(dX * dZ != 0 && (!pathfinder.canMoveDiagonally() || dY != 0 || !this.canMoveDiagonally(nodePos.getX(), nodePos.getY(), nodePos.getZ(), dX, dZ)))
						continue;
					
					if(!this.checkMove(nodePos, dX, dY, dZ))
						continue;
					
					BlockPosition neighbourPos = nodePos.getRelative(dX, dY, dZ);
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
	
	private boolean checkMove(BlockPosition from, int dX, int dY, int dZ) {
		int x = from.getX() + dX;
		int y = from.getY() + dY;
		int z = from.getZ() + dZ;
		
		Block standingOn = world.getBlockAt(x, y - 1, z);
		if(standingOn.getType() == Material.LADDER) {
			return this.isUnobstructed(x, y, z);
		}
		
		if(!WorldUtils.canStandOn(standingOn.getType()) || !this.isUnobstructed(x, y, z))
			return false;
			
		if(Tag.STAIRS.isTagged(standingOn.getType()) && !checkStair(standingOn, dX, dY, dZ))
			return false;
		
		return true;
	}
	
	private boolean checkStair(Block block, int dX, int dY, int dZ) {
		BlockData data = block.getBlockData();
		if(!(data instanceof Stairs))
			return false;
		
		Stairs stairs = (Stairs) data;
		if(stairs.getHalf() == Half.TOP)
			return true;
		
		BlockFace facing = stairs.getFacing();
		return facing.getModX() == dX && facing.getModZ() == dZ;
	}
	
	private boolean canMoveDiagonally(int x, int y, int z, int dX, int dZ) {
		return this.isUnobstructed(x + dX, y, z) &&
				this.isUnobstructed(x, y, z + dZ);
	}
	
	private boolean isUnobstructed(int x, int y, int z) {
		return WorldUtils.canStandIn(world.getBlockAt(x, y, z).getType()) &&
				WorldUtils.canStandIn(world.getBlockAt(x, y + 1, z).getType());
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
