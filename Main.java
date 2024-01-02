import java.util.Scanner;
import java.util.ArrayList;

public class Main {
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in); 
		String anotherYear = "yes";
		ArrayList<Integer> years = new ArrayList<Integer>();
		while(anotherYear.equals("yes")) { 
			System.out.print("Enter a year to analyze: ");
			int year = sc.nextInt(); 
			years.add(year);
			System.out.print("Another year? (yes or no) ");
			anotherYear = sc.next();
		}
		AnswerGetter url = new AnswerGetter(years);
		url.getTeams();
		url.printPlayerInfo();
	}
}
