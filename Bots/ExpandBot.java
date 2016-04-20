package Bots;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;


public class ExpandBot {

	public static boolean DoTurn(PlanetWars pw) {
		if (pw.NeutralPlanets().size() == 0)
			return false;
		
		List<Planet> neutralPlanets = pw.NeutralPlanets();
		
		Planet weakestNeutral = neutralPlanets.get(0);
		for (Planet planet : neutralPlanets) {
			if (planet.NumShips() < weakestNeutral.NumShips()) {
				weakestNeutral = planet;
			} else if (planet.NumShips() == weakestNeutral.NumShips()) {
				if (planet.GrowthRate() > weakestNeutral.GrowthRate()) {
					weakestNeutral = planet;
				}
			}
		}
		
		List<Planet> myPlanets = pw.MyPlanets();
		
		Planet myStrongest = myPlanets.get(0);
		for (Planet planet : myPlanets) {
			if (planet.NumShips() > myStrongest.NumShips()) {
				myStrongest = planet;
			} else if (planet.NumShips() == myStrongest.NumShips()) {
				if (planet.GrowthRate() > myStrongest.GrowthRate()) {
					myStrongest = planet;
				}
			}
		}
		
		pw.IssueOrder(myStrongest, weakestNeutral);
		return true;
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
