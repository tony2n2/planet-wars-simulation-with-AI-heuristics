package Bots;

public class MyAdaptiveBot {
	public static void DoTurn(PlanetWars pw) {
		if(pw.MyPlanets().size() < pw.Planets().size()/2){
			MMBeamSearchBot.DoTurn(pw);
		}else{
			MinMaxBot.DoTurn(pw);
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
