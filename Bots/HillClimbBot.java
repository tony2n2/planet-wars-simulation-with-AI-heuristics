package Bots;

public class HillClimbBot {
	static final int NEUTRAL = 0;
	static final int ME = 1;
	static final int ENEMY = 2;	

  	public static void DoTurn(PlanetWars pw) {
  		long startTime = System.nanoTime();
  		Planet source = null;
		Planet dest = null;
		double bestScore = Double.MIN_VALUE;
		
		for(Planet p : pw.MyPlanets()){								// Look at all planets I can attack
			for(Planet q : pw.NotMyPlanets()){
				SimulatedPlanetWars simpw = new SimulatedPlanetWars(pw);	// Simulate the attack
				simpw.IssueOrder(p, q);
				simpw.simulateGrowth();
				double score = heuristic(simpw);					// Calculate a score on how effective it was
				if(score > bestScore){								// Replace current best with new best if better
					bestScore = score;
					source = p;
					dest = q;
				}				
			}
		}
		
		if (source != null && dest != null) {
			pw.IssueOrder(source, dest);
		}
		long finishTime = System.nanoTime();
		pw.log("HillClimbBot: (Planets: " + pw.MyPlanets().size() + ";Time taken: " + (finishTime - startTime)/1000000 + "ms)");
	}
	
	public static double heuristic(SimulatedPlanetWars pw){
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
