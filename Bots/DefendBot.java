package Bots;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;


public class DefendBot {

	public static boolean DoTurn(PlanetWars pw) {		
		List<Planet> myPlanets = pw.MyPlanets();
		
		Planet myStrongest = myPlanets.get(0);
		Planet myWeakest = myPlanets.get(0);
		for (Planet planet : myPlanets) {
			if (planet.NumShips() > myStrongest.NumShips()) {
				myStrongest = planet;
			}
			if (planet.NumShips() < myWeakest.NumShips()) {
				myWeakest = planet;
			}
		}
		if (myStrongest.NumShips() > myWeakest.NumShips() * 2) {
			pw.IssueOrder(myStrongest, myWeakest);
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
			System.exit(1); //just stop now. we've got a problem
		}
	}
}
