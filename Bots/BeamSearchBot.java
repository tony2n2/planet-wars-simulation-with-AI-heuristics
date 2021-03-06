package Bots;

import java.util.ArrayList;
import java.util.List;

public class BeamSearchBot {
	private final static int MAX_BREADTH = 128;
	private final static int MAX_DEPTH = 24;

	public static void DoTurn(PlanetWars pw) {
		long startTime = System.nanoTime();
		SimulatedPlanetWars p = new SimulatedPlanetWars(pw);
		PWNode root = new PWNode(p);
		ArrayList<PWNode> list = new ArrayList<PWNode>();
		list.add(root);
		buildTree(list,0);
		scoreTree(root,0);

		Planet source = root.source;
		Planet dest = root.destination;

		if (source != null && dest != null) {    		  		
			pw.IssueOrder(source, dest);
		}
		long finishTime = System.nanoTime();
		pw.log("BeamSearchBot: (Planets: " + pw.MyPlanets().size() + ";Time taken: " + (finishTime - startTime)/1000000 + "ms)");
	}

	private static void buildTree(ArrayList<PWNode> list, int depth) {
		if(depth >= MAX_DEPTH) return;
		ArrayList<PWNode> children = new ArrayList<PWNode>();		//One level of the tree
		for(PWNode n : list){
			n.children = getChildren(n.p);
			children.addAll(n.children);
		}
		children = pickBest(children ,depth);						//Get the best nodes of the level
		buildTree(children, depth + 1);								//And continue building the tree from those
	}

	private static ArrayList<PWNode> getChildren(SimulatedPlanetWars p) {	//Get all the possible next states for a Simulated Planet Wars
		ArrayList<PWNode> children = new ArrayList<PWNode>();

		for(Planet source : p.MyPlanets()){									//For each of my planets
			for(Planet destination : p.Planets()){							//try to attack every other planet
				if (destination != source){									//that isn't the same planet
					SimulatedPlanetWars p2 = new SimulatedPlanetWars(p);	//Create simulation
					p2.IssueOrder(source, destination);						//Attack
					p2.simulateGrowth();									
					PWNode temp = new PWNode(p2);							
					temp.source = source;
					temp.destination = destination;
					children.add(temp);
				}
			}			
		}		
		return children;
	}

	private static ArrayList<PWNode> pickBest(ArrayList<PWNode> children, int depth) {	//Return the best n children from the list by sorting the array and taking the first n nodes
		if(children.size() <= MAX_BREADTH) return children;
		
		scoreNodes(children);		
		children = mergeSort(children);
		//if(depth%2 == 0){
			ArrayList<PWNode> bestChildren = new ArrayList<PWNode>();
			for(int i = 0; i < MAX_BREADTH; i++){
				bestChildren.add(children.get(i));
			}
			return bestChildren;
		/*}else{
			ArrayList<PWNode> worstChildren = new ArrayList<PWNode>();
			for(int i = children.size() - 1; i > children.size() - MAX_BREADTH - 1; i--){
				worstChildren.add(children.get(i));
			}
			return worstChildren;
		}*/
		
	}


	private static void scoreNodes(ArrayList<PWNode> nodes) {
		for(PWNode n:nodes){
			n.score = heuristic(n.p);
		}		
	}

	static ArrayList<PWNode> mergeSort(ArrayList<PWNode> list) {
		if (list.size() > 1) {
			int left = list.size()/2;

			ArrayList<PWNode> leftList = new ArrayList<PWNode> (list.subList(0, left));
			ArrayList<PWNode> rightList = new ArrayList<PWNode> (list.subList(left, list.size()));

			leftList = mergeSort(leftList);
			rightList = mergeSort(rightList);

			return merge(leftList,rightList);
		}else{
			return list;
		}
	}

	static ArrayList<PWNode> merge(ArrayList<PWNode> leftList, List<PWNode> rightList) {	// Removing the last element to hopefully make the code faster compared to removing the first element
		ArrayList<PWNode> list = new ArrayList<PWNode>();
		while(leftList.size()>0 && rightList.size()>0){
			if(leftList.get(leftList.size() - 1).score > rightList.get(rightList.size() - 1).score){
				list.add(leftList.remove(leftList.size() - 1));
			}else{
				list.add(rightList.remove(rightList.size() - 1));
			}
		}
		while(leftList.size() > 0){
			list.add(leftList.remove(leftList.size() - 1));
		}
		while(rightList.size() > 0){
			list.add(rightList.remove(rightList.size() - 1));
		}
		return list;
	}

	public static double heuristic(SimulatedPlanetWars pw){
		if(pw.EnemyPlanets().size() == 0) return Double.MAX_VALUE;
		if(pw.MyPlanets().size() == 0) return Double.MIN_VALUE;
		double myShips = 0;
		double myGrowthRate = 0;
		double enemyShips = 0;
		double enemyGrowthRate = 0;

		for(Planet p : pw.Planets()){
			switch(p.Owner()){
			case 1:
				myShips += p.NumShips();
				myGrowthRate += p.GrowthRate();
				break;
			case 2:
				enemyShips += p.NumShips();
				enemyGrowthRate += p.GrowthRate();
				break;
			default:
				break;
			}
		}
		return (myShips * myGrowthRate) / ((enemyShips * enemyGrowthRate)+1);
		//return (myGrowthRate) / (enemyGrowthRate+1);	
		//return (myShips) / (enemyShips +1);
	}

	public static void scoreTree(PWNode root, int depth){
		double maxScore = Double.MIN_VALUE;
		if(root.children != null && root.children.size() > 0){
			for(PWNode child : root.children){
				scoreTree(child, depth + 1);
				if(child.score > maxScore){
					maxScore = child.score;
					if(depth == 0){
						root.source = child.source;
						root.destination = child.destination;
					}
				}
			}
			root.score = maxScore;
		}else{
			root.score = heuristic(root.p);
		}
	}

	public static void main(String[] args) {
		String line = "";
		String message = "";
		int c;
		try {
			while ((c = System.in.read()) >= 0) {
				switch (c) {
				case '\n':
					if (line.equals("go")) {
						PlanetWars pw = new PlanetWars(message);
						DoTurn(pw);
						pw.FinishTurn();
						message = "";
					} else {
						message += line + "\n";
					}
					line = "";
					break;
				default:
					line += (char) c;
					break;
				}
			}
		} catch (Exception e) {
			// Uhh..
		}
	}
}
