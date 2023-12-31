import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

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

	// instead of hard-coding multiple URLs, we retrieve URL's based on the
	// parameter given to this method

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
		System.out.println("Getting " + team +  " season");
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
					if (seasonLink.contains("2013")) {
						getPlayers(seasonLink);
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
		List<String> playerList = new LinkedList<String>();
		try {
			Document doc = Jsoup.connect(URL + season).get();
			Element table = doc.getElementById("roster");
			Elements row = table.select("td").select("a");
			for (Element e : row) {
				String links = e.attr("href");
				if (!links.contains("college")) {
					// playerList.add(links);
					getPlayerInfo(links);
				}
			}
		} catch (IOException e) {
		// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	
		// addEdges(playerList);

	/*
	 * Creates the graph; the input represents a team's roster for one year so
	 * we add edges between every pair of players
	 */
	private void addEdges(List<String> players) {
		System.out.println("Adding edges...");
		for (int i = 0; i < players.size(); i++) {
			if (!edges.containsKey(players.get(i))) {
				edges.put(players.get(i), new LinkedList<String>());
			}
			for (int j = 0; j < players.size(); j++) {
				if (!edges.get(players.get(i)).contains(players.get(j))) {
					edges.get(players.get(i)).add(players.get(j));
					// note that this will add a self-edge
				}
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
			System.out.println("Getting " + player + " information...");
			players.put(player, new String[8]);
			try {
				Document doc = Jsoup.connect(URL + player).get();
				getPlayerName(player, doc); 
				getPlayerPositionAndHand(player, doc); 
				getPlayerHeight(player, doc); 
				getPlayerWeight(player, doc); 
				getPlayerExperience(player, doc); 
				getPlayerNumTeams(player, doc);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
	
	private void getPlayerNumTeams(String player, Document doc) {
		Elements table = doc.getElementsByClass("jersey");
		/*
		 *  this will get the number of different shirts they have had,
		 *  so it isn't exactly the number of teams they have played for
		 */
		players.get(player)[7] = Integer.toString(table.size());
	}

	public void printPlayerInfo() {
		//getNumTeammates();
		for (Map.Entry<String, String[]> e : players.entrySet()) {
			System.out.println("\nName: " + e.getValue()[0] 
							+ "\nURL: " + e.getKey()
							+ "\nPosition: " + e.getValue()[1] 
							+ "\nShoots: " + e.getValue()[2] 
							+ "\nHeight: " + e.getValue()[3]
							+ "\nWeight: " + e.getValue()[4] 
							+ "\nExperience: " + e.getValue()[5] 
							+ "\nDistinct Teammates: " + e.getValue()[6] 
							+ "\nDistinct Jerseys: " + e.getValue()[7] + "\n");
		}
	}

	private void getNumTeammates() {
		for (Map.Entry<String, List<String>> e : edges.entrySet()) {
			String player = e.getKey();
			String numTeammates = Integer.toString(e.getValue().size());
			players.get(player)[6] = numTeammates;
		}
	}

	public void printNeighbors(String player) {
		System.out.println("\nThe following players have played with "
				+ players.get(player)[0] + ":");
		for (int i = 0; i < edges.get(player).size(); i++) {
			System.out.println(players.get(edges.get(player).get(i))[0]);
		}
	}
}
