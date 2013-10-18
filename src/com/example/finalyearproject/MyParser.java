package com.example.finalyearproject;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class MyParser {

	
	public Document parseAndReturn(String RawData){
		
		Document doc = null;
		try {
			doc = Jsoup.parse(RawData);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return doc;
		
		
		
	}
	
	
	
	
	
}
