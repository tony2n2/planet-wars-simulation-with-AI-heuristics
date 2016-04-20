package Bots;

import java.util.ArrayList;

public class MinMaxBot {
	private final static int MAX_DEPTH = 4;

	public static void DoTurn(PlanetWars pw) {
		long startTime = System.nanoTime();
		SimulatedPlanetWars p = new SimulatedPlanetWars(pw);
    	PWNode tree = toTree(p,0);
    	scoreTree(tree,0);
    	Planet source = tree.source;
    	Planet dest = tree.destination;
    	if (source != null && dest != null) {    		  		
			pw.IssueOrder(source, dest);
		}
    	long finishTime = System.nanoTime();
		pw.log("MinMaxBot: (Planets: " + pw.MyPlanets().size() + ";Time taken: " + (finishTime - startTime)/1000000 + "ms)");
	}

	public static double heuristic(SimulatedPlanetWars pw){
		if(pw.EnemyPlanets().size() == 0) return Double.MAX_VALUE;
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
 	
 	public static PWNode toTree(SimulatedPlanetWars pw, int depth){
 		if(depth >= MAX_DEPTH) return null;		// cutoff depth
		ArrayList<PWNode> children = new ArrayList<PWNode>();
		ArrayList<Planet> sourcePlanets = new ArrayList<Planet>();
		
		if(depth%2 == 0){//My Turn
			sourcePlanets = (ArrayList<Planet>) pw.MyPlanets();
		}else{//Enemies turn
			sourcePlanets = (ArrayList<Planet>) pw.EnemyPlanets();
		}	
		
		for(Planet source : sourcePlanets){
			for(Planet destination : pw.Planets()){
				if (destination != source){
					SimulatedPlanetWars pw2 = new SimulatedPlanetWars(pw);
					pw2.IssueOrder(source, destination);
					pw2.simulateGrowth();
					PWNode temp = toTree(pw2, depth + 1);
					if(temp != null){
						temp.source = source;
						temp.destination = destination;
						children.add(temp);
					}
				}
			}			
		}
 		return new PWNode(pw, children);
 	}
 	
 	public static double scoreTree(PWNode root, int depth){
 		double maxMinScore = 0;
 		if(root.children != null && root.children.size() > 0){
	 		if(depth%2 == 0){//MAX
	 			maxMinScore = Double.MIN_VALUE;
	 		}else{//MIN
	 			maxMinScore = Double.MAX_VALUE;
	 		}
	 		
	 		for(PWNode child : root.children){
	 			double score = scoreTree(child, depth+1);
	 			if(depth%2 == 0){
	 				if(score > maxMinScore){
		 				maxMinScore = score;
		 				if(depth == 0){
		 					root.source = child.source;
		 					root.destination = child.destination;
		 				}
		 			}
	 			}else{
	 				if(score < maxMinScore){
	 					maxMinScore = score;
	 				}	 			
	 			}
	 		}
 		}else{
 			maxMinScore = heuristic(root.p);
 		}
 		return maxMinScore;
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

		}
	}
}
