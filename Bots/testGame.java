package Bots;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class testGame {
	PlanetWars pw;

	public static void main(String[] args) {
		new testGame().start();
	}
	
	testGame(){
		
	}

	private void start() {
		//System.out.println("Loading map...");
		String s = "";
		Scanner in = null;
		try {
			in = new Scanner(new File("map1.txt"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		while(in.hasNext()){
			s += in.nextLine();
			s += "\n";
		}
		pw = new PlanetWars(s);
		System.out.println(pw.toString());
		MinMaxBot.DoTurn(pw);
		System.out.println(pw.toString());
	}

}
