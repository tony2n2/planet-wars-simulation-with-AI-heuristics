package Bots;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;

public class HillClimbingBot {

	void start() {
		String line = "";
		String message = "";
		int c;
		try {
			while ((c = System.in.read()) >= 0) {
				switch (c) {
				case '\n':
					if (line.equals("go")) {
						PlanetWars pw = new PlanetWars(message);
						doTurn(pw);
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

	void doTurn(PlanetWars pw) {
		double score = calculateHeuristicValue(pw);
		Planet source = null;
		Planet dest = null;

		for (Planet myPlanet : pw.MyPlanets()) {
			if (myPlanet.NumShips() <= 1)
				continue;	
			for (Planet planet : pw.Planets()) {
				SimulatedPlanetWars simpw = new SimulatedPlanetWars(pw);
				simpw.simulateAttack(myPlanet, planet);
				simpw.simulateGrowth();

				double scoreMax = calculateHeuristicValue(simpw);
				
				if (scoreMax > score) {					
					score = scoreMax;
					source = myPlanet;
					dest = planet;
				}
			}
		}

		// Attack!
		if (source != null && dest != null) {
			pw.IssueOrder(source, dest);
		}
	}

	double calculateHeuristicValue(PlanetWars pw) {
		int numMyShips = 1;
		int numEnemyShips = 1;
		for (Planet myPlanet : pw.MyPlanets()) {
			numMyShips += (myPlanet.NumShips() * myPlanet.GrowthRate());
		}
		for (Planet enemyPlanet : pw.EnemyPlanets()) {
			numEnemyShips += (enemyPlanet.NumShips() * enemyPlanet.GrowthRate());
		}
		return (numMyShips) / (numEnemyShips);
	}
	
	double calculateHeuristicValue(SimulatedPlanetWars pw){
		int numMyShips = 1;
		int numEnemyShips = 1;
		for (Planet myPlanet : pw.MyPlanets()) {
			numMyShips += (myPlanet.NumShips() * myPlanet.GrowthRate());
		}
		for (Planet enemyPlanet : pw.EnemyPlanets()) {
			numEnemyShips += (enemyPlanet.NumShips() * enemyPlanet.GrowthRate());
		}
		return (numMyShips) / (numEnemyShips);
	}
	
	public static void main(String[] args) {
		new HillClimbingBot().start();
	}
}

