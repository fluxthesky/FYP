package com.example.finalyearproject;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.WindowManager;
import android.widget.TextView;

public class PopulationHistory extends Activity {
	private ArrayList<String> FOODCOURT_POPULATION_HISTORY = new ArrayList<String>(); 
	private TextView mTextView;
	private int SizeOfFoodcourtPopulationHistory;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_population_history);
		WindowManager.LayoutParams params = getWindow().getAttributes();  
 	    params.height = WindowManager.LayoutParams.WRAP_CONTENT;  
	    params.width = WindowManager.LayoutParams.WRAP_CONTENT;  
 	  
	    this.getWindow().setAttributes(params); 
		Intent get = getIntent();
		FOODCOURT_POPULATION_HISTORY = (ArrayList<String>) get.getSerializableExtra("PopulationHistory");
		SizeOfFoodcourtPopulationHistory = FOODCOURT_POPULATION_HISTORY.size();
		
	 
		
		
		
		
  
		
		mTextView = (TextView)findViewById(R.id.population_history);
 		mTextView.setText("\n");
		
		for(int i = SizeOfFoodcourtPopulationHistory; i > 0 ; i --){
			mTextView.append("There was " + FOODCOURT_POPULATION_HISTORY.get(i-1) + "\n" );

		}
		
	    
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.population_history, menu);
		return true;
	}

}
