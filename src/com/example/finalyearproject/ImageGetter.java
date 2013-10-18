package com.example.finalyearproject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ImageGetter implements Runnable {

	private String URL;
	private HelperInterfaceForImage DAF; 
	Bitmap loadedimage;
	public ImageGetter(String URL, HelperInterfaceForImage DAF) {
		// TODO Auto-generated constructor stub
		this.URL = URL;
		this.DAF = DAF;
	}
	
	
	
	

	
	public interface HelperInterfaceForImage{
		
		
		void finishedLoading(Bitmap loadedimage);
		void errorConnecting();

		
		
	}

 

	@Override
	public void run() {
		
 
		
		try {
			URL ImageUrl = new  URL(URL);
			loadedimage = BitmapFactory.decodeStream(ImageUrl.openStream());
			DAF.finishedLoading(loadedimage);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			DAF.errorConnecting();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			DAF.errorConnecting();

		}	
		
	}
	
	
	
}
