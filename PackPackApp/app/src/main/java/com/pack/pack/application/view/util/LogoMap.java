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
		logoMap.put("nytimes.com", "New_York_Times_logo_variation.jpg");
		logoMap.put("www.time.com", "time_dot_com.png");
		logoMap.put("time.com", "time_dot_com.png");
		logoMap.put("www.thehindu.com", "thehindu-icon.png");
		logoMap.put("thehindu.com", "thehindu-icon.png");
		logoMap.put("www.news18.com", "news18-logo.png");
		logoMap.put("news18.com", "news18-logo.png");
		logoMap.put("ndtv.com", "2000px-NDTV_logo.svg.png");
		logoMap.put("www.ndtv.com", "2000px-NDTV_logo.svg.png");
		logoMap.put("cbsnews.com", "CBS_News_logo.png");
		logoMap.put("www.cbsnews.com", "CBS_News_logo.png");
		logoMap.put("talksport.com", "talksport_400x400.jpg");
		logoMap.put("www.espncricinfo.com", "espncricinfo-6301-630x400.jpg");
		logoMap.put("espncricinfo.com", "espncricinfo-6301-630x400.jpg");
		logoMap.put("www.newscientist.com", "newscientisthero34_1476186101.jpg");
		logoMap.put("newscientist.com", "newscientisthero34_1476186101.jpg");
		logoMap.put("www.phys.org", "Phys.Org_.png");
		logoMap.put("phys.org", "Phys.Org_.png");
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