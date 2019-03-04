package com.pack.pack.application.view.util;

import com.pack.pack.application.data.util.ApiConstants;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public final class LogoMap {

	private static final Map<String, String> logoMap = new HashMap<String, String>();

	static {
		logoMap.put("timesofindia.indiatimes.com", ApiConstants.LOGO_BASE_URL + "times-of-india-logo.png");
		logoMap.put("www.nytimes.com", ApiConstants.LOGO_BASE_URL + "New_York_Times_logo_variation.jpg");
		logoMap.put("nytimes.com", ApiConstants.LOGO_BASE_URL + "New_York_Times_logo_variation.jpg");
		logoMap.put("www.time.com", ApiConstants.LOGO_BASE_URL + "time_dot_com.png");
		logoMap.put("time.com", ApiConstants.LOGO_BASE_URL + "time_dot_com.png");
		logoMap.put("www.thehindu.com", ApiConstants.LOGO_BASE_URL + "thehindu-icon.png");
		logoMap.put("thehindu.com", ApiConstants.LOGO_BASE_URL + "thehindu-icon.png");
		logoMap.put("www.news18.com", ApiConstants.LOGO_BASE_URL + "news18-logo.png");
		logoMap.put("news18.com", ApiConstants.LOGO_BASE_URL + "news18-logo.png");
		logoMap.put("ndtv.com", ApiConstants.LOGO_BASE_URL + "2000px-NDTV_logo.svg.png");
		logoMap.put("www.ndtv.com", ApiConstants.LOGO_BASE_URL + "2000px-NDTV_logo.svg.png");
		logoMap.put("cbsnews.com", ApiConstants.LOGO_BASE_URL + "CBS_News_logo.png");
		logoMap.put("www.cbsnews.com", ApiConstants.LOGO_BASE_URL + "CBS_News_logo.png");
		logoMap.put("talksport.com", ApiConstants.LOGO_BASE_URL + "talksport_400x400.jpg");
		logoMap.put("www.espncricinfo.com", ApiConstants.LOGO_BASE_URL + "espncricinfo-6301-630x400.jpg");
		logoMap.put("espncricinfo.com", ApiConstants.LOGO_BASE_URL + "espncricinfo-6301-630x400.jpg");
		logoMap.put("www.newscientist.com", ApiConstants.LOGO_BASE_URL + "newscientisthero34_1476186101.jpg");
		logoMap.put("newscientist.com", ApiConstants.LOGO_BASE_URL + "newscientisthero34_1476186101.jpg");
		logoMap.put("www.phys.org", ApiConstants.LOGO_BASE_URL + "Phys.Org_.png");
		logoMap.put("phys.org", ApiConstants.LOGO_BASE_URL + "Phys.Org_.png");
		logoMap.put("news.nationalgeographic.com", ApiConstants.LOGO_BASE_URL + "national-geographic.svg.png");
		logoMap.put("www.nationalgeographic.com", ApiConstants.LOGO_BASE_URL + "national-geographic.svg.png");
		logoMap.put("www.aljazeera.com", ApiConstants.LOGO_BASE_URL + "aljazeera.jpg");
		logoMap.put("aljazeera.com", ApiConstants.LOGO_BASE_URL + "aljazeera.jpg");
		logoMap.put("qz.com", ApiConstants.LOGO_BASE_URL + "qz_dot_com.jpg");
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