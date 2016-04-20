package Bots;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;

/* A bot which adapts its behaviour according to the environment characteristics.
 * It changes its strategy, based on the current environment (e.g. number of neutral planets in the map,
 * number of ships, etc.). Knowing which strategy to use has to be collected beforehand.
 * This requires running a number of games of your bots, and evaluate which bot performs best for a certain environment.
 * You should then add this to the data structure (in AdaptivityMap.java). 
 * The DoTurn method can then query this data structure to know what strategy should be used for this turn. 
 * This example provides two environment variables: the number of neutral planets on the map, and the average growth
 * ratio of these neutral planets.
 * 
 * We provide a possible implementation using the hash adaptivityMap, which maps lists of integers (representing 
 * the environment) with names of bots. See AdaptivityMap.java
 * 
 * Interesting questions (you can probably come up with other questions yourself as well):
 * 1. Can you modify or extend the environment variables we use? Maybe other things are interesting other than the number of neutral planets, and the average planet size of these neutral planets.
 * 2. The table in AdaptivityMap.java is filled by us (randomly) with only two simple bots. But how should the table really look like? 
 * This means you should collect data on how all your previous bots (BullyBot, RandomBot, HillclimbingBot, LookaheadBot and/or others) perform in different environments
 * 3. Can you implement your other bot implementations in AdaptiveBot.java? Currently the only strategies are BullyBot ('DoBullyBotTurn') and RandomBot ('DoRandomBotTurn').
 * Implement the bot strategies you used to fill AdaptivityMap.java here as well.
 */

public class AdaptiveBot {
	
	public static final String[] ADAPTIVE_STRATEGIES = {"bully", "single/defend", "single/defend", "single/bully", "single/minmax", "single/minmax", "single/bully", "single/minmax", "minmax"};
	
	/**
	 * The main method for issuing your commands. Here, the best strategy is selected depending on the environment characteristics
	 * @param pw
	 */
	public static void DoTurn(PlanetWars pw) {
		long startTime = System.nanoTime();
		int myShips = 0;
		int enemyShips = 0;
		int myGrowthRate = 0;
		int enemyGrowthRate = 0;
		int chosenStrategyId = 0;
		int diffShips = 0;
		int diffGrowthRate = 0;
		String chosenStrategy;
		
		List<Planet> planets = pw.Planets();
		for (Planet planet : planets) {
			if (planet.Owner() == 1) {
				myShips += planet.NumShips();
				myGrowthRate += planet.GrowthRate();
			} else if (planet.Owner() > 1) {
				enemyShips += planet.NumShips();
				enemyGrowthRate += planet.GrowthRate();
			}
		}
		
		diffShips = myShips - enemyShips;
		diffGrowthRate = myGrowthRate - enemyGrowthRate;
		
		if (Math.abs(diffShips) * 5 < enemyShips) {
			chosenStrategyId = 1;
		} else if (diffShips > 0) {
			chosenStrategyId = 0;
		} else {
			chosenStrategyId = 2;
		}
		
		if (Math.abs(diffGrowthRate) * 5 < enemyGrowthRate) {
			chosenStrategyId += (1 * 3);
		} else if (diffGrowthRate > 0) {
			chosenStrategyId += (0 * 3);
		} else {
			chosenStrategyId += (2 * 3);
		}
		
		chosenStrategy = ADAPTIVE_STRATEGIES[chosenStrategyId];
		
		if (chosenStrategy.equals("bully")) {
			System.err.println("BullyBot is going to play this turn");
			BullyBot.DoTurn(pw);
		} else if (chosenStrategy.equals("single/defend")) {
			if (!SingleBot.DoTurn(pw)) {
				System.err.println("(SingleBot)DefendBot is going to play this turn");
				DefendBot.DoTurn(pw);
			} else {
				System.err.println("SingleBot(DefendBot) is going to play this turn");
			}
		} else if (chosenStrategy.equals("single/bully")) {
			if (!SingleBot.DoTurn(pw)) {
				System.err.println("(SingleBot)BullyBot is going to play this turn");
				BullyBot.DoTurn(pw);
			} else {
				System.err.println("SingleBot(BullyBot) is going to play this turn");
			}
		} else if (chosenStrategy.equals("single/minmax")) {
			if (!SingleBot.DoTurn(pw)) {
				System.err.println("(SingleBot)MinMaxBot is going to play this turn");
				BullyBot.DoTurn(pw);
			} else {
				System.err.println("SingleBot(MinMaxBot) is going to play this turn");
			}
		} else if (chosenStrategy.equals("minmax")) {
			System.err.println("MinMaxBot is going to play this turn");
			BullyBot.DoTurn(pw);
		}
		long finishTime = System.nanoTime();
		pw.log("AdaptiveBot: (Planets: " + pw.MyPlanets().size() + ";Time taken: " + (finishTime - startTime)/1000000 + "ms)");
	}
	
	/**
	 * Implementation of the bullybot strategy (copy pasted from the regular BullyBot.java)
	 * @param pw
	 */
	public static void DoBullyBotTurn(PlanetWars pw) {
		Planet source = null;
		double sourceScore = Double.MIN_VALUE;
		//Select my strongest planet to send ships from
		for (Planet myPlanet : pw.MyPlanets()) {
			if (myPlanet.NumShips() <= 1)
				continue;
			double score = (double) myPlanet.NumShips();
			if (score > sourceScore) {
				sourceScore = score;
				source = myPlanet;
			}
		}
		
		Planet dest = null;
		double destScore = Double.MAX_VALUE;
		//Select weakest destination planet
		for (Planet notMyPlanet : pw.NotMyPlanets()) {
			double score = (double) (notMyPlanet.NumShips());

			if (score < destScore) {
				destScore = score;
				dest = notMyPlanet;
			}
		}
		
		if (source != null && dest != null) {
			pw.IssueOrder(source, dest);
		}
	}
	
	/**
	 * Implementation of the RandomBot strategy (copy pasted from the regular RandomBot.java)
	 * @param pw
	 */
	public static void DoRandomBotTurn(PlanetWars pw) {

		Random random = new Random();
		
		Planet source = null;
		List<Planet> myPlanets = pw.MyPlanets();
		//Randomly select source planet
		if (myPlanets.size() > 0) {
			Integer randomSource = random.nextInt(myPlanets.size());
			source = myPlanets.get(randomSource);
		}
		
		Planet dest = null;
		List<Planet> allPlanets = pw.NotMyPlanets();
		//Randomly select destication planets
		if (allPlanets.size() > 0) {
			Integer randomTarget = random.nextInt(allPlanets.size());
			dest = allPlanets.get(randomTarget);
		}

		if (source != null && dest != null) {
			pw.IssueOrder(source, dest);
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
			System.exit(1); //just stop now. we've got a problem
		}
	}
}
