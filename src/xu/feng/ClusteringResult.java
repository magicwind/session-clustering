package xu.feng;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClusteringResult {
	static Logger log = LoggerFactory.getLogger(ClusteringResult.class);
	
	public static void main(String[] args) throws IOException {
		int maxCluster = 10;
		File cluster = new File("Cluster(10).csv");
		// Cluster  ObjectSeq
		Map<Integer, List<Integer>> clusterList = new HashMap<Integer, List<Integer>>();
		FileReader reader = new FileReader(cluster);
		BufferedReader br = new BufferedReader(reader);
		String line;
		while ((line = br.readLine()) != null) {
			String[] strArr = line.split(",");
			int clusterNo = Integer.parseInt(strArr[0], 10);
			int pointNo = Integer.parseInt(strArr[1], 10);
			
			if (clusterNo > 10) {
				continue;
			}
			
			List<Integer> listPoints = clusterList.get(clusterNo);
			if (listPoints == null) {
				listPoints = new ArrayList<Integer>();
				clusterList.put(clusterNo, listPoints);
			}
			listPoints.add(pointNo);
		}
		
		// 将Session列表从Map映射到List(按顺序排列)
		Map<String, List<String>> sessionMap = SessionSimilarity.readSession("session_url10.csv");
		
		List<List<String>> sessionList = new ArrayList<List<String>>();
		int sessionOrder = 0;
		for (String session_id : sessionMap.keySet()) {
			sessionList.add(sessionMap.get(session_id));
			sessionOrder++;
			log.info("session " + sessionOrder + ": " + session_id);
		}
		
		// print cluster's session and its urls
		for (int clusterNo : clusterList.keySet()) {
			List<Integer> sessionInCluster = clusterList.get(clusterNo);
			log.info("---cluster: " + clusterNo + ", num of sessions: " + sessionInCluster.size() + "---");
			for (int sessionNo : sessionInCluster) {
				List<String> urls = sessionList.get(sessionNo - 1);
				log.info("--session: " + sessionNo);
				for (String url : urls) {
					log.info("-url: " + url);
				}
			}
		}
	}

}
