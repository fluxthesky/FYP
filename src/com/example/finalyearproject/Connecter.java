package com.example.finalyearproject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.jsoup.nodes.Document;

import android.util.Log;


public class Connecter implements Runnable{ // This class connects to website
	
	DoAfterFinishForDocs DAF; // Special case, I'm not using this for links, but rather raw HTML data
	String link;
	boolean status;
	public Connecter(String link,DoAfterFinishForDocs DAF) {
		// TODO Auto-generated constructor stub
		this.link = link;
		this.DAF = DAF;
	}
	public Connecter(){
		
	}
	
	public String connect(String link)
	{
		
		String url = link;
		HttpURLConnection connect;
		String result;
		String result2 = null;
		BufferedReader br;
 		
		try {
			connect = (HttpURLConnection) (new URL(url)).openConnection();
			connect.setRequestMethod("GET");
			connect.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/28.0.1500.95 Safari/537.36");
			br = new BufferedReader(new InputStreamReader(connect.getInputStream()));
			Log.i(MyTag.Tag, "Started connecting");

			while ((result = br.readLine()) != null)
			{
				 result2 += result +"\n";
			}
			status = true;

			Log.i(MyTag.Tag, "Finished connecting");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			
				e.printStackTrace();
				status = false;

				Log.i(MyTag.Tag, "Error at Connecter.connect()");
		}
        return result2;
        
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
 		String RawData = connect(link);
 		MyParser parse = new MyParser();
 		Document doc = null;
		try {
			Log.i(MyTag.Tag, "Starting parsing..");
			doc = parse.parseAndReturn(RawData);
			Log.i(MyTag.Tag, "Finished parsing");
 		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
 		}
		DAF.finishLoading(doc,status);
	}
	
	
	
	
}
