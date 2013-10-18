package com.example.finalyearproject;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class MyArrayAdapter extends ArrayAdapter<String> {

	  private final Context context;
	  private final ArrayList<String> values;
	  
	  public MyArrayAdapter (Context context, ArrayList<String> values) {
	    super(context, R.layout.list_view, values);
	    this.context = context;
	    this.values = values;
	  }
	
	  @Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		  
		  LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		  View mView = inflater.inflate(R.layout.list_view, parent,false);
		  
		  
		  TextView Desc = (TextView)mView.findViewById(R.id.Desc);
		  Desc.setText(values.get(position));
 
		  
		  return mView;
				
	  }
	  
	  
	
}
