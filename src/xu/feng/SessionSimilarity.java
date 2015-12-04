package xu.feng;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SessionSimilarity {

	public static void main(String[] args) throws IOException {
		File csv = new File("session_url.csv");
		FileReader reader = new FileReader(csv);
		BufferedReader br = new BufferedReader(reader);
		String line;
		
		Map<String, List<String>> sessionMap = new HashMap<String, List<String>>();
        Set<String> urlSet = new HashSet<String>();

		while ((line = br.readLine()) != null) {
			System.out.println(line);
			String[] strArr = line.split(",");
			String sessionId = strArr[0], url = strArr[1];
            // add url to set
            urlSet.add(url);

			if (sessionMap.containsKey(sessionId)) {
				sessionMap.get(sessionId).add(url);
			} else {
				List<String> newSession = new ArrayList<String>();
				if (newSession.add(url)) {
					sessionMap.put(sessionId, newSession);
				} else {
					throw new RuntimeException("failed to add url '" + url + "' to session '" + sessionId + "'");
				}
			}
		}

        br.close();

		System.out.println(sessionMap.toString());
		
		double sim1 = calSessionSim1(sessionMap.get("s1"), sessionMap.get("s2"));
		
        System.out.println("Sim1 = " + sim1);

        for (String url : sessionMap.get("s1")) {
            System.out.println(url + " degree: " + getUrlDegree(url));
        }

        double sim2 = calSessionSim2(sessionMap.get("s1"), sessionMap.get("s2"));

        System.out.println("Sim2 = " + sim2);

        System.out.println("url sim: " + calUrlSim("/", "/info/prospective/"));
        System.out.println("url sim: " + calUrlSim("/info/prospective/", "/"));
        System.out.println("url sim: " + calUrlSim("/", "/"));
        System.out.println("url sim: "
                + calUrlSim("/education/computer-science/programinfo.html", "/education/computer-science/programapply.htm"));

        System.out.println("final sim: " + Math.max(sim1, sim2));
	}
	
    static double calSessionSim1(List<String> s1, List<String> s2) {
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
	
    static double calSessionSim2(List<String> s1, List<String> s2) {
        double sim2 = 0;
        double numerator = 0;
        // double denominator = 1;
        double urlSim = 0;
        for (String s1url : s1) {
            for (String s2url : s2) {
                if (s1url.equals(s2url)) {
                    numerator++;
                } else {
                    urlSim = calUrlSim(s1url, s2url);
                    numerator += urlSim;
                }
            }
        }

        sim2 = numerator / (s1.size() * s2.size());

        return sim2;
	}

    static int getUrlDegree(String url) {
        int degree = 0;
        int pos = -1;
        while ((pos = url.indexOf("/")) != -1) {
            url = url.substring(pos + 1);
            if (pos == 0 || url.length() > 0) {
                degree++;
            } else {
                break;
            }
        }

        return degree;
    }

    static double calUrlSim(String url1, String url2) {
        if (!url1.startsWith("/") || !url2.startsWith("/")) {
            throw new RuntimeException("url does not start with /: " + url1 + " or " + url2);
        }
        int sameDegree = 0;
        int degree = Math.min(getUrlDegree(url1), getUrlDegree(url2));

        String[] tokenArr1 = url1.split("/");
        String[] tokenArr2 = url2.split("/");

        for (int i = 1; i <= degree; i++) {
            String token1, token2;
            if (tokenArr1.length == 0) {
                token1 = "";
            } else {
                token1 = tokenArr1[i];
            }

            if (tokenArr2.length == 0) {
                token2 = "";
            } else {
                token2 = tokenArr2[i];
            }
            
            if (token1.equals(token2)) {
                sameDegree++;
            }
        }

        return sameDegree / (Math.sqrt(getUrlDegree(url1)) * Math.sqrt(getUrlDegree(url2)));
    }
}
