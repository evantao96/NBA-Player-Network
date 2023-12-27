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
			Document doc = Jsoup.connect(URL + "/teams/").ignoreHttpErrors(true).timeout(0).get();
			Element active = doc.getElementById("all_teams_active");
			Elements table = active.getElementsByClass("full_table");
			Elements rows = table.select("tr");
			Elements teams = rows.select("a");
			for (Element e : teams) {
				String team = e.attr("href");
				getRosters(team);
			} 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * Retrieves the URL for the year of each team, and passes it to getPlayers
	 */
	public void getRosters(String team) {
		System.out.println("Getting " + team +  " roster");
		try {
			Document doc = Jsoup.connect(URL + team).ignoreHttpErrors(true).timeout(0).get();
			Elements table = doc.getElementsByClass("active");
			Elements year = table.select("a");
			for (Element e : year) {
				String s = e.attr("href");
				/*
				 * the first two strings for each team are short and don't
				 * correspond to the years we want
				 */
				if (s.length() > 11) {
					/*
					 * the following if statement can be adjusted to get
					 * whichever years are desired
					 */
					if (s.contains("2013") || s.contains("2012")) {
						getPlayers(s);
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
	public void getPlayers(String s) {
		System.out.println("Getting players...");
		List<String> players = new LinkedList<String>();
		try {
			Document doc = Jsoup.connect(URL + s).ignoreHttpErrors(true).timeout(0).get();
			Elements table = doc.getElementsByClass("table_container");
			for (Element e : table) {
				if (e.text().contains("College")) {
					Elements row = e.select("td");
					Elements year = row.select("a");
					for (Element yr : year) {
						String links = yr.attr("href");
						if (!links.contains("college")) {
							players.add(links);
							getPlayerInfo(links);
						}
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		addEdges(players);
	}

	/*
	 * Creates the graph; the input represents a team's roster for one year so
	 * we add edges between every pair of players
	 */
	public void addEdges(List<String> players) {
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
	public void getPlayerInfo(String player) {
		System.out.println("Getting player information...");
		getPlayerName(player);
		getPlayerNumTeams(player);
		if (players.get(player)[1] == null) {
			try {
				Document doc = Jsoup.connect(URL + player).ignoreHttpErrors(true).timeout(0).get();
				Elements table = doc.getAllElements();
				Elements rows = table.select("p");
				for (Element e : rows) {
					String s = e.text();
					if (s.contains("Position")) { // get the right
															// paragraph
						int posBeg = s.indexOf(":");
						int posEnd = s.indexOf("▪");
						// add position attribute
						players.get(player)[1] = s.substring(posBeg + 2,
								posEnd - 1);
						int hndBeg = s.indexOf(":", posEnd);
						// add left or right handed attribute
						players.get(player)[2] = s.substring(hndBeg + 2);
					}
					if (s.contains("cm")) { // get the next paragraph 
						int htBeg = s.indexOf("("); 
						int htEnd = s.indexOf(")");
						// add height attribute
						players.get(player)[3] = s.substring(htBeg + 1, htEnd);
					}
					if (s.contains("lb")) {
						int wtBeg = s.indexOf("(");
						int wtEnd = s.indexOf(")"); 
						// add weight attribute
						players.get(player)[4] = s.substring(wtBeg + 1, wtEnd); 
					}
					if (s.contains("Experience")) {
						int eBeg = s.indexOf("Experience");
						int eEnd = s.indexOf("year", eBeg);
						/*
						 * add experience attribute (in years) this
						 * information is not available for some, so
						 * experience will remain 'null'
						 */
						players.get(player)[5] = s.substring(eBeg + 12,
								eEnd);
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void getPlayerName(String player) {
		try {
			Document doc = Jsoup.connect(URL + player).ignoreHttpErrors(true).timeout(0).get();
			Elements table = doc.getAllElements();
			Elements rows = table.select("h1");
			for (Element e : rows) {
				String name = e.text();
				if (!players.containsKey(player)) {
					players.put(player, new String[8]);
					players.get(player)[0] = name;
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void getPlayerNumTeams(String player) {
		try {
			Document doc = Jsoup.connect(URL + player).ignoreHttpErrors(true).timeout(0).get();
			Elements table = doc.getElementsByAttributeValueContaining("class", "uni_holder bbr");
			/*
			 *  this will get the number of different shirts they have had,
			 *  so it isn't exactly the number of teams they have played for
			 */
			players.get(player)[7] = Integer.toString(table.size());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void printPlayerInfo() {
		getNumTeammates();
		System.out.println("\n");
		for (Map.Entry<String, String[]> e : players.entrySet()) {
			System.out.println(e.getValue()[0] + "\nURL: " + e.getKey()
					+ "\nPosition: " + e.getValue()[1] + "\nShoots: "
					+ e.getValue()[2] + "\nHeight: " + e.getValue()[3]
					+ "\nWeight: " + e.getValue()[4] + "\nExperience: "
					+ e.getValue()[5] + "\nDistinct Teammates: "
					+ e.getValue()[6] + "\nDistinct Jerseys: "
					+ e.getValue()[7] + "\n");
		}
	}

	public void getNumTeammates() {
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
