package com.pack.pack.application.view.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public final class LogoMap {

	private static final Map<String, String> logoMap = new HashMap<String, String>();

	static {
		logoMap.put("timesofindia.indiatimes.com", "times-of-india-logo.png");
		logoMap.put("www.nytimes.com", "New_York_Times_logo_variation.jpg");
		logoMap.put("www.time.com", "time_dot_com.png");
		logoMap.put("www.thehindu.com", "thehindu-icon.png");
		logoMap.put("talksport.com", "talksport_400x400.jpg");
		logoMap.put("www.espncricinfo.com", "espncricinfo-6301-630x400.jpg");
		logoMap.put("espncricinfo.com", "espncricinfo-6301-630x400.jpg");
		logoMap.put("www.newscientist.com", "newscientisthero34_1476186101.jpg");
		logoMap.put("newscientist.com", "newscientisthero34_1476186101.jpg");
		logoMap.put("news.nationalgeographic.com", "national-geographic.svg.png");
		logoMap.put("www.nationalgeographic.com", "national-geographic.svg.png");
		logoMap.put("www.aljazeera.com", "aljazeera.jpg");
		logoMap.put("aljazeera.com", "aljazeera.jpg");
		logoMap.put("qz.com", "qz_dot_com.jpg");
	}
	
	private LogoMap() {
	}
	
	public static String get(String link) {
		try {
			URL url = new URL(link);
			return logoMap.get(url.getHost());
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		}
	}
}