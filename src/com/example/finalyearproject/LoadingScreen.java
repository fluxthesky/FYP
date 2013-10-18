package com.example.finalyearproject;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class LoadingScreen extends Activity {

	private ImageView mLoadingSpinner;
	private Animation mAnimation;
	private Connecter mConnecter;
	private String URL = "http://melcolmlee.dyndns.org/results.php";
	private Elements mElementForMaxNumber;
	private Elements mElementForFoodcourtStatus;
	private int mMaxNumber = 10;
	private int mFoodcourtStatus = 1;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_loading_screen);
		
		mLoadingSpinner = (ImageView)findViewById(R.id.loading_screen_loading);
		mAnimation = AnimationUtils.loadAnimation(this, R.anim.anim);
		mLoadingSpinner.setAnimation(mAnimation);
		mConnecter = new Connecter(URL, new DoAfterFinishForDocs() {
			
			@Override
			public void finishLoading(Document doc, boolean loadingSuccess) {
				// TODO Auto-generated method stub
				
				if(loadingSuccess == true){
				
				try {
					mElementForMaxNumber = doc.select("m");
					mElementForFoodcourtStatus = doc.select("fs");
					mMaxNumber = Integer.parseInt(mElementForMaxNumber.get(0).text());
					mFoodcourtStatus = Integer.parseInt(mElementForFoodcourtStatus.get(0).text());

				} catch (Exception e) {
					// TODO Auto-generated catch block
 				}
				runOnUiThread(new Runnable() {
					public void run() {
						startMainActivity();

					}
				});
 				}
				
				else{
					
					runOnUiThread(new Runnable() {
						public void run() {
							showErrorLog();

						}
					});
 					
					
					
				}
				
				
				
			}
		});
		
		new Thread(mConnecter).start();
		
		
 	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.loading_screen, menu);
		return true;
	}
	
	private void showErrorLog(){
		
		
		AlertDialog.Builder errorLog = new Builder(this);
		errorLog.setTitle("Error");
		errorLog.setCancelable(false);
		errorLog.setMessage("Could not connect to server");
		errorLog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
				finish();
				
			}
		});
		
		errorLog.show();
		
		
		
		
	}
	
	private void startMainActivity(){
		
		

				
				Intent go = new Intent(getApplicationContext(),MainActivity.class);
				go.putExtra("mMaxCap", mMaxNumber);
				go.putExtra("mFoodCourtOpenOrClosed", mFoodcourtStatus);
				startActivity(go);
				
	}
	

}
