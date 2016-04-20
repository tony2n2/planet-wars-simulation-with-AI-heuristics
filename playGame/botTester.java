package playGame;

/* NOTES:
 * The timing method assumes your bot logs its time spent calculating using pw.log in the format:
 * MinMaxBot: (Planets: 1;Time taken: 17ms) 
 * or
 * <package name>.MinMaxBot: (Planets: 1;Time taken: 17ms) 
 * 
 * Bot names in the String[] BOTS need to include package name, if applicable
 */

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class botTester {
	private final static int MATCH_REPEATS = 3;
	private final static String[] BOTS = {"Bots.MMBeamSearchBot","Bots.HillClimbingBot", "Bots.AdaptiveBot", "Bots.MinMaxBot_tony"};
	private final static String[] MAPS = {"tools/maps/8planets/map1.txt"};

	private void start(){
		playGames();
		scoreBots();
	}

	private void playGames() {
		try {
			System.setErr(new PrintStream(new FileOutputStream(new File("Erroutput.txt"))));
		} catch (Exception e) {
			e.printStackTrace();
		}
		for(String bot1 : BOTS){
			for(String bot2 : BOTS){
				if(!bot1.equals(bot2)){
					for(String map : MAPS){
						for(int i = 0; i < MATCH_REPEATS; i++){
							System.out.println("---- Starting match (map: " + map + ", turn: " + (i+1) + ", " + bot1 + " vs " + bot2 + ") ---- ");
							
							String[] args = {map,"java " + bot1,"java " + bot2 ,"parallel"};
							Engine.main(args);
							
							System.out.println("---- Ending match ---- " + bot1 + " vs " + bot2);
						}
					}
				}
			}
		}
	}
	
	private void scoreBots() {
		int[] wins = new int[BOTS.length];
		int[] turns = new int[BOTS.length];
		int[] time = new int[BOTS.length];
		File input = new File("Erroutput.txt");
		
		Pattern winPattern = Pattern.compile("Player ([1-2]) Wins!");
		Pattern botPattern = Pattern.compile("(Bots.[a-zA-Z]+)");
		
		int turn = 0;
		
		try {
			Scanner in = new Scanner(input);
			while(in.hasNextLine()){
				String line = in.nextLine();				
				if(line.startsWith("Turn")) {
					//System.out.println("Turnline: " + line);
					turn += 1;
				}else if(line.startsWith("---- ") || line.endsWith("Wins!")){
					//System.out.println("Matchline: " + line);
					Matcher winMatcher = winPattern.matcher(line);
					if (winMatcher.find()){						
						Matcher botMatcher = botPattern.matcher(in.nextLine());
						String winner = winMatcher.group(1);
						String loser;
						botMatcher.find();
						String bot1 = botMatcher.group(1);
						botMatcher.find();
						String bot2 = botMatcher.group(1);
						
						if(winner.equals("1")){
							winner = bot1;
							loser = bot2;
						}else{
							winner = bot2;
							loser = bot1;
						}
					    //System.out.println(winner + " lost from " + loser + " in " + turn + " turns!");
					    for(int i = 0; i < BOTS.length; i++){
					    	if(BOTS[i].equals(winner)){
					    		wins[i] += 1;
					    		turns[i] += turn;
					    	}
					    }
					    turn = 0;
					}
				}else{	
					//System.out.println("playerstatus: " + line);
					Pattern timePattern = Pattern.compile("Player \\d: ([a-zA-Z]+): .Planets: \\d+;Time taken: (\\d+)ms.");
					Matcher timeMatcher = timePattern.matcher(line);
					if(timeMatcher.find()){
						for(int i = 0; i < BOTS.length; i++){
							if(BOTS[i].endsWith(timeMatcher.group(1))){
								time[i] += Integer.parseInt(timeMatcher.group(2));
							}
						}
						;
						timeMatcher.group(2);//time taken
					}
				}
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("---- Statistics ---- (% of wins; average nr of turns needed to win; total computation time)");
		for(int i = 0; i < BOTS.length; i++){
			double turnsWins = wins[i]>0 ? (double)turns[i]/wins[i] : -1;
			System.out.printf("%s: (%.2f%%; %.2f; %ds)\n",BOTS[i] ,(double)wins[i]/(BOTS.length * (BOTS.length-1) * MAPS.length * MATCH_REPEATS)*100, turnsWins, time[i]/1000);
		}
	}
	
	public static void main(String[] args) {
		new botTester().start();
	}
}
