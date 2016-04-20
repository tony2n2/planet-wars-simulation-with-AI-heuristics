package Bots;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

public class SingleBot {

	public static boolean DoTurn(PlanetWars pw) {		
		List<Planet> myPlanets = pw.MyPlanets();
		List<Planet> notMyPlanet = pw.NotMyPlanets();
		
		Planet myStrongest = myPlanets.get(0);
		Planet dest = null;
		
		for (Planet planet : myPlanets) {
			if (planet.NumShips() > myStrongest.NumShips()) {
				myStrongest = planet;
			}
		}
		for (Planet planet : notMyPlanet) {
			if (myStrongest.NumShips()/2 >= planet.NumShips()) {
				if ((dest == null) || (dest.Owner() == 0 && planet.Owner() > 1) || 
						(dest.Owner() == planet.Owner() && dest.NumShips() > planet.NumShips()) ||
						(dest.Owner() == planet.Owner() && dest.NumShips() == planet.NumShips() && dest.GrowthRate() < planet.GrowthRate())) {
					dest = planet;
				}
			}
		}
		
		if (dest != null) {
			pw.IssueOrder(myStrongest, dest);
			return true;
		}
		return false;
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
