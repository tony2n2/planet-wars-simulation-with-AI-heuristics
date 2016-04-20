package Bots;

public class PickFirstBot {

	public static void DoTurn(PlanetWars pw) {
		Planet source = null;
		Planet dest = null;
		
		if(pw.MyPlanets().size() > 0 & pw.EnemyPlanets().size() > 0){
			source = pw.MyPlanets().get(0);
			//dest = pw.NotMyPlanets().get(0);
			dest = pw.EnemyPlanets().get(0);
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

		}
	}
}
