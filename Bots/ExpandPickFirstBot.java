package Bots;

public class ExpandPickFirstBot {

	public static void DoTurn(PlanetWars pw) {
		Planet source = null;
		Planet dest = null;
		
		if(pw.MyPlanets().size() == 1 & pw.EnemyPlanets().size() == 1){
			source = pw.MyPlanets().get(0);
			dest = pw.NeutralPlanets().get(0);
			for(Planet p: pw.NeutralPlanets()){
				if(p.GrowthRate()>dest.GrowthRate()){
					dest = p;
				}
			}
		}else
		
		if(pw.MyPlanets().size() > 0 & pw.EnemyPlanets().size() > 0){
			source = pw.MyPlanets().get(0);
			for(Planet p : pw.MyPlanets()){
				if(p.NumShips() > source.NumShips()){
					source = p;
				}
			}
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
