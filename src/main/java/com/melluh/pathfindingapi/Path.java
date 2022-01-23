package com.melluh.pathfindingapi;

import java.util.ArrayList;
import java.util.List;

public class Path {

	private List<PathNode> nodes = new ArrayList<>();
	
	protected Path(List<PathNode> nodes) {
		this.nodes = nodes;
	}
	
	public List<PathNode> getNodes() {
		return nodes;
	}
	
}
