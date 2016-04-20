package Bots;

import java.util.ArrayList;

public class PWNode{
	public SimulatedPlanetWars p;
	public ArrayList<PWNode> children;
	public double score;
	public Planet source;
	public Planet destination;
	
	PWNode(SimulatedPlanetWars p, ArrayList<PWNode> children){
		this.p = p;
		this.children = children;
	}
	
	PWNode(SimulatedPlanetWars p){
		this.p = p;
		this.children = new ArrayList<PWNode>();
	}		
}