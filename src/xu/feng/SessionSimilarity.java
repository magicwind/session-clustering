package xu.feng;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SessionSimilarity {

	public static void main(String[] args) throws IOException {
		File csv = new File("session_url.csv");
		FileReader reader = new FileReader(csv);
		BufferedReader br = new BufferedReader(reader);
		String line;
		
		Map<String, ArrayList<String>> sessionMap = new HashMap<String, ArrayList<String>>();
		while ((line = br.readLine()) != null) {
			System.out.println(line);
			String[] strArr = line.split(",");
			String sessionId = strArr[0], url = strArr[1];
			if (sessionMap.containsKey(sessionId)) {
				sessionMap.get(sessionId).add(url);
			} else {
				ArrayList<String> newSession = new ArrayList<String>();
				if (newSession.add(url)) {
					sessionMap.put(sessionId, newSession);
				} else {
					throw new RuntimeException("failed to add url '" + url + "' to session '" + sessionId + "'");
				}
			}
		}

		System.out.println(sessionMap.toString());
		
		System.out.println("Sim1 = " + calSessionSim1(sessionMap.get("s1"), sessionMap.get("s2")));
	}
	
	static double calSessionSim1(ArrayList<String> s1, ArrayList<String> s2) {
		double sim1 = 0;
		if (s1.size() == 0 || s2.size() == 0) {
			return sim1;
		}
		int sameUrlCount = 0;
		for (String url1 : s1) {
			for (String url2 : s2) {
				if (url1.equals(url2)) {
					sameUrlCount++;
				}
			}
		}
		
		sim1 = sameUrlCount / (Math.sqrt(s1.size()) * Math.sqrt(s2.size()));
		
		return sim1;
	}
	
	static double calSessionSim2(ArrayList<String> s1, ArrayList<String> s2) {
		return 0;
	}
}
