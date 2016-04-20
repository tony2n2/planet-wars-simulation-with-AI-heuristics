package Bots;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;

public class MinMaxBot_tony {

	public static final int lookAheadNRound = 2;

	public static void DoTurn(PlanetWars origPw) {
		long startTime = System.nanoTime();
		SimulatedPlanetWars_tony currState = new SimulatedPlanetWars_tony(origPw);
		Tree tree = new Tree(currState);
		simulateNRound(tree);
		computeMiniMax(tree);
		Tree maxChild = tree.getMaxChild();
		System.err.println(maxChild.source);
		origPw.IssueOrder(maxChild.source, maxChild.dest);
		long finishTime = System.nanoTime();
		origPw.log("MinMaxBot_tony: (Planets: " + origPw.MyPlanets().size() + ";Time taken: " + (finishTime - startTime)/1000000 + "ms)");
	}
	
	public static void simulateNRound(Tree tree) {
		ArrayList<Tree> workingSet = new ArrayList<Tree>();
		workingSet.add(tree);

		for (int i = 0; i < lookAheadNRound; i++) {
			ArrayList<Tree> newStates = new ArrayList<Tree>();
			for (int k = 0; k < workingSet.size(); k++) {
				simulateRound(workingSet.get(k), newStates);
			}
			workingSet = newStates;
		}
	}
	
	private static void simulateRound(Tree tree, ArrayList<Tree> newStates) {
		SimulatedPlanetWars_tony pw = (SimulatedPlanetWars_tony) tree.getContent();
		for (Planet myPlanet : pw.MyPlanets()) {
			if (myPlanet.NumShips() <= 1)
				continue;

			for (Planet notMyPlanet : pw.NotMyPlanets()) {
				// a empty node to group the nodes together
				Tree groupNode = tree.addChild(null);
				groupNode.source = myPlanet;
				groupNode.dest = notMyPlanet;
				for (Planet enemyPlanet : pw.EnemyPlanets()) {
					for (Planet enemyTargetPlanet : pw.MyPlanets()) {
						SimulatedPlanetWars_tony simpw = new SimulatedPlanetWars_tony(pw);
						simpw.simulateAttack(myPlanet, notMyPlanet);
						
						simpw.simulateAttack(2, enemyPlanet, enemyTargetPlanet);
						simpw.simulateGrowth();
						Tree tempNode = groupNode.addChild(simpw);
						tempNode.source = myPlanet;
						tempNode.dest = notMyPlanet;
						newStates.add(tempNode);
					}
					for (Planet enemyTargetPlanet : pw.NeutralPlanets()) {
						SimulatedPlanetWars_tony simpw = new SimulatedPlanetWars_tony(pw);
						simpw.simulateAttack(myPlanet, notMyPlanet);
						
						simpw.simulateAttack(2, enemyPlanet, enemyTargetPlanet);
						simpw.simulateGrowth();
						Tree tempNode = groupNode.addChild(simpw);
						tempNode.source = myPlanet;
						tempNode.dest = notMyPlanet;
						newStates.add(tempNode);
					}
				}
			}
		}
	}
	
	public static double computeHeuristicVal(SimulatedPlanetWars_tony pw) {
		double enemyShips = 1.0;
		double myShips = 1.0;

		for (Planet pl : pw.EnemyPlanets()) {
			enemyShips += pl.NumShips();
			enemyShips += pl.GrowthRate() * 3;
		}

		for (Planet pl : pw.MyPlanets()) {
			myShips += pl.NumShips();
			enemyShips += pl.GrowthRate() * 3;
		}

		return myShips / enemyShips;
	}

	private static void computeMiniMax(Tree tree) {
		if (tree.getChildren() == null) {
			tree.score = computeHeuristicVal(tree.getContent());
			return;
		}
		for (Tree child : tree.getChildren()) {
			computeMiniMax(child);
		}
		if (tree.getContent() == null) {
			// enemy will choose for the minimal heuristic value
			tree.score = tree.getMinChild().score;
		} else {
			// we will choose the maximal heuristic value
			tree.score = tree.getMaxChild().score;
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
			StringWriter writer = new StringWriter();
			e.printStackTrace(new PrintWriter(writer));
			String stackTrace = writer.toString();
			System.err.println(stackTrace);
			System.exit(1); // just stop now. we've got a problem
		}
	}
}
