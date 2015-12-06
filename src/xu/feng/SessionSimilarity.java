package xu.feng;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SessionSimilarity {

	static Logger log = LoggerFactory.getLogger(SessionSimilarity.class);
	
	public static void main(String[] args) throws IOException {
		//PropertyConfigurator.configure("log4j.properties");
		
		File csv = new File("session_url10.csv");
		File output = new File("session.csv");
		if (output.exists()) {
			output.delete();
		}
		File idOutput = new File("session_ids.csv");
		if (idOutput.exists()) {
			idOutput.delete();
		}
		BufferedWriter bw = new BufferedWriter(new FileWriter(output));
		BufferedWriter idbw = new BufferedWriter(new FileWriter(idOutput));
		
		FileReader reader = new FileReader(csv);
		BufferedReader br = new BufferedReader(reader);
		String line;
		
		int countSim1 = 0, countSim2 = 0;
		
		Map<String, List<String>> sessionMap = new HashMap<String, List<String>>();
        Set<String> urlSet = new HashSet<String>();
        
        Map<String, Map<String, Double>> simMatrix = new HashMap<String, Map<String, Double>>();

		while ((line = br.readLine()) != null) {
			//log.info(line);
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

		//log.info(sessionMap.toString());
		/*
		double sim1 = calSessionSim1(sessionMap.get("s1"), sessionMap.get("s2"));
		
        log.info("Sim1 = " + sim1);

        for (String url : sessionMap.get("s1")) {
            log.info(url + " degree: " + getUrlDegree(url));
        }

        double sim2 = calSessionSim2(sessionMap.get("s1"), sessionMap.get("s2"));

        log.info("Sim2 = " + sim2);
		 */
        log.info("url sim: " + calUrlSim("/", "/info/prospective/"));
        log.info("url sim: " + calUrlSim("/info/prospective/", "/"));
        log.info("url sim: " + calUrlSim("/", "/"));
        log.info("url sim: "
                + calUrlSim("/education/computer-science/programinfo.html", "/education/computer-science/programapply.htm"));

        //log.info("final sim: " + Math.max(sim1, sim2));
        
        // calculate simMatrix
        try {
        	int count = 0;
        	
        	for (int i = 1; i <= sessionMap.keySet().size(); i++) {
        		log.info("session " + i + ": " + sessionMap.keySet().toArray()[i-1]);
        		idbw.write(sessionMap.keySet().toArray()[i-1].toString());
        		idbw.newLine();
        	}
        	idbw.flush();
        	idbw.close();
        	
	        for (String session_id1 : sessionMap.keySet()) {
	        	
	        	Map<String, Double> simMap = simMatrix.get(session_id1);
	        	if (simMap == null) {
	        		simMap = new HashMap<String, Double>();
	        		simMatrix.put(session_id1, simMap);
	        	}
	        	count++;
	        	
	        	//if (count == 21) {
	        	//	break;
	        	//}
	        	
	        	log.info(count + ":cal sim between " + session_id1 + " and others");
	        	int count2 = 0;
				for (String session_id2 : sessionMap.keySet()) {
					if (!session_id1.equals(session_id2)) {
						
						double tsim1 = calSessionSim1(sessionMap.get(session_id1), sessionMap.get(session_id2));
						double tsim2 = calSessionSim2(sessionMap.get(session_id1), sessionMap.get(session_id2));
						//log.info("sim " + session_id2 + ": " + tsim1 + "," + tsim2);
						double tsim = Math.max(tsim1, tsim2);
						if (tsim1 >= tsim2) {
							countSim1++;
						} else {
							countSim2++;
						}
						simMap.put(session_id2, tsim);
					} else {
						simMap.put(session_id2, 1.0);
					}
					count2++;
					//if (count2 == 20) {
					//	break;
					//}
				}
			}

	        log.info("-----result-----");
	        for (String session_id : simMatrix.keySet()) {
	        	Map<String, Double> simMap = simMatrix.get(session_id);
	        	String resultLine = "";
	        	for (String t_session_id : simMap.keySet()) {
	        		if (!resultLine.equals("")) {
	        			bw.write(",");
	        		}
	        		Double dSim = simMap.get(t_session_id);
	        		resultLine += String.format("%1$,.2f ", 1 - dSim);
	        		bw.write(String.format("%1$,.4f", 1 - dSim));
	        	}
	        	if (simMap.size() > 0) {
        		    bw.newLine();
    	        	log.info(resultLine);
	        	}
	        }
	        
	        log.info("use sim1 count: " + countSim1 + ", use sim2 count:" + countSim2);
	        
	        bw.flush();
	        bw.close();
        } catch (Exception e) {
        	log.info(e.toString());
        }
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
                    //log.info("url sim " + s1url + ", " + s2url + ": 1");
                } else {
                    urlSim = calUrlSim(s1url, s2url);
                    //log.info("url sim " + s1url + ", " + s2url + ": " + urlSim);
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
