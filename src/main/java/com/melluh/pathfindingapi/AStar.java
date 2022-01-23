package com.melluh.pathfindingapi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.melluh.pathfindingapi.util.BlockPosition;

public class AStar {

	private Pathfinder pathfinder;
	
	private PathNode startNode;
	private BlockPosition goal;
	
	private Set<PathNode> openNodes = new HashSet<>();
	private Set<PathNode> closedNodes = new HashSet<>();
	private int visitedNodes;
	
	private Path resultPath;
	
	protected AStar(Pathfinder pathfinder, BlockPosition start, BlockPosition goal) {
		this.pathfinder = pathfinder;
		this.startNode = new PathNode(start);
		this.goal = goal;
	}
	
	public void findPath() {
		startNode.setParent(null, 0);
		this.addOpenNode(startNode);
		
		while(!openNodes.isEmpty()) {
			if(visitedNodes >= pathfinder.getMaxNodeVisits()) {
				// TODO: better error handling
				System.out.println("Failed to find path: exceeded max node visits");
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
		
		// TODO: better error handling
		System.out.println("Failed to find path: ran out of open nodes");
	}
	
	public boolean pathFound() {
		return resultPath != null;
	}
	
	public Path getPath() {
		return resultPath;
	}
	
	private void visitNode(PathNode node) {
		visitedNodes++;
		
		// TODO: y-movement
		for(int dX = -1; dX <= 1; dX++) {
			for(int dZ = -1; dZ <= 1; dZ++) {
				if(dX == 0 && dZ == 0)
					continue;
				
				if(!pathfinder.canMoveDiagonally() && dX * dZ != 0)
					continue;
				
				BlockPosition neighbourPos = node.getPosition().getRelative(dX, 0, dZ);
				PathNode neighbour = this.getNode(neighbourPos);
				
				double tentativeGCost = node.getGCost() + node.getPosition().distanceTo(neighbourPos);
				if(tentativeGCost < neighbour.getGCost()) { // check if this path is better than the previous one
					neighbour.setParent(node, tentativeGCost);
					this.addOpenNode(neighbour);
				}
			}
		}
	}
	
	private void addOpenNode(PathNode node) {
		double distanceToEnd = node.getPosition().distanceTo(goal);
		node.setHCost(distanceToEnd);
		openNodes.add(node);
	}
	
	// TODO: fix this garbage
	private PathNode getNode(BlockPosition pos) {
		for(PathNode node : openNodes) {
			if(node.getPosition().equals(pos))
				return node;
		}
		
		for(PathNode node : closedNodes) {
			if(node.getPosition().equals(pos))
				return node;
		}
		
		return new PathNode(pos);
	}
	
	private PathNode getCurrentNode() {
		Optional<PathNode> opt = openNodes.stream()
			.sorted(Comparator.comparing(PathNode::getFCost))
			.findFirst();
		return opt.isPresent() ? opt.get() : null;
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
