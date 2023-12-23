import java.util.Scanner;

public class Main {
	public static void main(String[] args) {
		Scanner reader = new Scanner(System.in);
		AnswerGetter url = new AnswerGetter();
		String repeat = "yes";

		while (repeat.equals("yes")) {
			/* 
			 * some trials I did to check functionality, run getTeams() to get
			 * all information in one run
			 */
			url.getRosters("/teams/GSW/");
//			url.getPlayerInfo("/players/c/curryst01.html");
			url.printPlayerInfo();
//			url.printNeighbors("/players/c/curryst01.html");
			System.out.println("Enter 'yes' to run the program again.");
			repeat = reader.next();
		}
		reader.close();
	}
}
