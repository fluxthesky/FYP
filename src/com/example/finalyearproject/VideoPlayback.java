

// THIS CLASS IS NOT USED IN THIS CURRENT APP, IT WAS WRITTEN FOR EXPERIMENTATION PURPOSE ONLY

package com.example.finalyearproject;

import java.io.IOException;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;
import android.widget.VideoView;

public class VideoPlayback extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(
		    WindowManager.LayoutParams.FLAG_FULLSCREEN,  
		     WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_video_playback);
		
		
		VideoView vid = (VideoView)findViewById(R.id.video);
		String url = "rtsp://r4---sn-a5m7zu7d.c.youtube.com/CiILENy73wIaGQlxP1xqXhhexRMYDSANFEgGUgZ2aWRlb3MM/0/0/0/video.3gp"; // your URL here
		vid.setVideoPath(url);	
		
		vid.start();
 
		
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.video_playback, menu);
		return true;
	}

}
