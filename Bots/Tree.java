package Bots;
import java.util.ArrayList;

public class Tree {
	
	private SimulatedPlanetWars_tony content;
	public double score = Double.MIN_VALUE;
	public Planet source = null;
	public Planet dest = null;
	private Tree parent = null;
	private ArrayList<Tree> children;

	public Tree(SimulatedPlanetWars_tony content) {
		this.content = content;
	}

	public Tree addChild(SimulatedPlanetWars_tony content) {
		Tree child = new Tree(content);
		if (children == null) {
			children = new ArrayList<Tree>();
		}
		children.add(child);
		return child;
	}
	
	public SimulatedPlanetWars_tony getContent() {
		return content;
	}

	public ArrayList<Tree> getChildren() {
		return children;
	}
	
	public Tree getParent() {
		return parent;
	}
	
	public Tree getMaxChild() {
		if (children == null)
			return null;
		
		Tree maxChild = children.get(0);
		for (Tree child : children) {
			if (child.score > maxChild.score)
				maxChild = child;
		}
		return maxChild;
	}
	
	public Tree getMinChild() {
		if (children == null)
			return null;
		
		Tree minChild = children.get(0);
		for (Tree child : children) {
			if (child.score < minChild.score)
				minChild = child;
		}
		return minChild;
	}
}
