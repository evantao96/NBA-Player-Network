import java.util.Scanner;

public class Main {
	public static void main(String[] args) {
		AnswerGetter url = new AnswerGetter();
		url.getTeams();
		url.printPlayerInfo();
		// url.getRosters("/teams/GSW/");
		// url.getPlayerInfo("/players/d/duranke01.html");
		// url.printPlayerInfo();
		// url.printNeighbors("/players/d/duranke01.html");
	}
}
