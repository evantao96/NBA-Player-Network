import java.util.Scanner;

public class Main {
	public static void main(String[] args) {
		AnswerGetter url = new AnswerGetter();
//			url.getTeams();
		url.getRosters("/teams/GSW/");
		url.getPlayerInfo("/players/c/curryst01.html");
		url.printPlayerInfo();
		url.printNeighbors("/players/c/curryst01.html");
	}
}
