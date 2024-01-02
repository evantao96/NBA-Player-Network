import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.io.FileWriter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class AnswerGetter {

	static String URL = "http://www.basketball-reference.com";

	/*
	 * map each player's URL to a list of all other players' URLs they have
	 * played with
	 */
	Map<String, List<String>> edges = new HashMap<String, List<String>>();

	/*
	 * map each player's URL to their name and other attributes
	 */
	Map<String, String[]> players = new HashMap<String, String[]>();

	ArrayList<Integer> years; 
	// instead of hard-coding multiple URLs, we retrieve URL's based on the
	// parameter given to this method

	public AnswerGetter(ArrayList<Integer> y) {
		years = y;
	}
	/*
	 * Retrieves the URL for each team, and passes it to getRosters
	 */
	public void getTeams() {
		System.out.println("Getting teams...");
		try {
			Document doc = Jsoup.connect(URL + "/teams/").get();
			Element active = doc.getElementById("all_teams_active");
			Elements teams = active.select("a");
			for (Element e : teams) {
				String team = e.attr("href");
				if (team.contains("ATL"))
					getSeasons(team);	
			} 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * Retrieves the URL for each season, and passes it to getPlayers
	 */
	private void getSeasons(String team) {
		try {
			TimeUnit.SECONDS.sleep(5);
		} catch (InterruptedException e2) {
			e2.printStackTrace();
		}
		System.out.println("Getting " + team +  " season...");
		try {
			Document doc = Jsoup.connect(URL + team).get();
			Element table = doc.getElementById("content");
			Elements year = table.select("a");
			for (Element e : year) {
				String seasonLink = e.attr("href");
				String seasonText = e.text(); 

				if (seasonText.length() == 7) {
					/*
					 * the following if statement can be adjusted to get
						 * whichever years are desired
						 */
					for(int y: years) {
						if (seasonLink.contains(Integer.toString(y))) {
							getPlayers(seasonLink);
						}
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * Retrieves the URL for each player, and passes it to addEdges (to create
	 * the graph) and getPlayerInfo to get the attributes of each player
	 */
	private void getPlayers(String season) {
		try {
			TimeUnit.SECONDS.sleep(5);
		} catch (InterruptedException e2) {
			e2.printStackTrace();
		}
		System.out.println("Getting " + season + " players...");
		try {
			List<String> roster = new LinkedList<String>();
			Document doc = Jsoup.connect(URL + season).get();
			Element table = doc.getElementById("roster");
			Elements row = table.select("td").select("a");
			for (Element e : row) {
				String playerLink = e.attr("href");
				if (!playerLink.contains("college")) {
					roster.add(playerLink); 
				}
			}
			for (String s: roster) {
				addEdges(s, roster);
				getPlayerInfo(s);
			}
		} catch (IOException e) {
		// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	

	/*
	 * Creates the graph; the input represents a team's roster for one year so
	 * we add edges between every pair of players
	 */
	private void addEdges(String player, List<String> roster) {
		System.out.println("Adding " + player + " edges...");
		if (!edges.containsKey(player)) {
			edges.put(player, new LinkedList<String>());
		}
		for (String other: roster) {
			if (!edges.get(player).contains(other) && !player.equals(other)) {
				edges.get(player).add(other);
			}
		}	
	}

	/*
	 * Retrieves the attributes for each player, filling in the players map
	 */
	private void getPlayerInfo(String player) {
		try {
			TimeUnit.SECONDS.sleep(5);
		} catch (InterruptedException e2) {
			e2.printStackTrace();
		}
		if (!players.containsKey(player)) {
			players.put(player, new String[8]);
		}
		System.out.println("Getting " + player + " information...");
		try {
			Document doc = Jsoup.connect(URL + player).get();
			getPlayerName(player, doc); 
			getPlayerPositionAndHand(player, doc); 
			getPlayerHeight(player, doc); 
			getPlayerWeight(player, doc); 
			getPlayerExperience(player, doc); 
			getPlayerNumTeammates(player);
			getPlayerNumTeams(player, doc);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void getPlayerName(String player, Document doc) {
		Elements table = doc.getAllElements();
		Elements rows = table.select("h1");
		for (Element e : rows) {
			String name = e.text();
			players.get(player)[0] = name;
		} 
	}

	private void getPlayerPositionAndHand(String player, Document doc) {
		Elements table = doc.getAllElements();
		Elements rows = table.select("p");
		for (Element e : rows) {
			String s = e.text();
			if (s.contains("Position")) { 
				int posBeg = s.indexOf(":");
				int posEnd = s.indexOf("▪");
				// add position attribute
				players.get(player)[1] = s.substring(posBeg + 2, posEnd - 1);
				int hndBeg = s.indexOf(":", posEnd);
				// add left or right handed attribute
				players.get(player)[2] = s.substring(hndBeg + 2);
			}
		}
	}

	private void getPlayerHeight(String player, Document doc) {
		Elements table = doc.getAllElements();
		Elements rows = table.select("p");
		for (Element e : rows) {
			String s = e.text();
			if (s.contains("cm")) {
				int htBeg = s.indexOf("("); 
				int htEnd = s.indexOf(")");
				// add height attribute
				players.get(player)[3] = s.substring(htBeg + 1, htEnd);
			}
		}
	}

	private void getPlayerWeight(String player, Document doc) {
		Elements table = doc.getAllElements();
		Elements rows = table.select("p");
		for (Element e : rows) {
			String s = e.text();
			if (s.contains("lb")) {
				int wtBeg = s.indexOf("(");
				int wtEnd = s.indexOf(")"); 
				// add weight attribute
				players.get(player)[4] = s.substring(wtBeg + 1, wtEnd); 
			}
		}
	}

	private void getPlayerExperience(String player, Document doc) {
		Elements table = doc.getAllElements();
		Elements rows = table.select("p");
		for (Element e : rows) {
			String s = e.text();
			if (s.contains("Experience") || s.contains("Career Length")) {
				int eBeg = s.indexOf(":");
				int eEnd = s.indexOf("years");
				// add experience/career length attribute
				players.get(player)[5] = s.substring(eBeg + 2, eEnd - 1);
			}
		}
	}

	private void getPlayerNumTeammates(String player) {
		// add number of teammates 
		String numTeammates = Integer.toString(edges.get(player).size());
		players.get(player)[6] = numTeammates;
	}
	
	private void getPlayerNumTeams(String player, Document doc) {
		Elements table = doc.getElementsByClass("jersey");
		/*
		 *  this will get the number of different shirts they have had,
		 *  so it isn't exactly the number of teams they have played for
		 */
		players.get(player)[7] = Integer.toString(table.size());
	}

	public void printPlayerInfo() {
		for (Map.Entry<String, String[]> e : players.entrySet()) {
			System.out.println("\nName: " + e.getValue()[0] 
							+ "\nURL: " + e.getKey()
							+ "\nPosition: " + e.getValue()[1] 
							+ "\nShoots: " + e.getValue()[2] 
							+ "\nHeight: " + e.getValue()[3]
							+ "\nWeight: " + e.getValue()[4] 
							+ "\nExperience: " + e.getValue()[5] 
							+ "\nNumber of Teammates: " + e.getValue()[6] 
							+ "\nNumber of Teams: " + e.getValue()[7] + "\n");
		}
	}

	public void printNeighbors(String player) {
		System.out.println("\nThe following players have played with " + player + ":");
		for (String s: edges.get(player)) {
			System.out.println(s);
		}
	}
}
