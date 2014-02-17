package com.example.finalyearproject;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fima.cardsui.objects.Card;
import com.fima.cardsui.views.CardUI;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class MainActivity extends Activity {
	private CardUI mCardUI;

	private String FOODCOURT = "Foodcourt"; 

	private int LOADING = 1;
	private int FOODCOURT_POPULATION = 0;
	private Gson mGson;

	private ArrayList<String> FOODCOURT_POPULATION_HISTORY = new ArrayList<String>(); 

	private int REFRESH_INTERVAL = 1000;
	private int REFRESH_INTERVAL_FOR_IMAGE = 60000;

	private Bitmap mFoodcourtCapture;
	/*private String URL = "http://melcolmlee.dyndns.org/results.php";*/
	private String URL = "http://164.78.251.47/result.php";
	private String UrlOfImage = "http://164.78.252.56/ipcam/fcimages/fc4.jpg";

	//http://164.78.252.56/ipcam/fcimages/fc5.jpg
	//ftp://melcolmlee.dyndns.org/disk2/pub/westwing.jpg





	private ImageGetter mImageGetter;
	private ImageGetter mImageGetterForHand;
	private Environment mEnvironment;

	private Elements mElements;
	private Elements mElements2;

	private Thread threadStarter;
	private Thread threadStarterForImages;

	private Handler hand;
	private Handler handforimages;

	private Runnable mRunnable;
	private Runnable mRunnableForImage;

	private boolean SHOW_POPULATION_HISTORY = false;
	private boolean STARTED_POPULATION_HISTORY_ACTIVITY = false;
	private boolean PAUSED_APP = false;
	private String tag = "w";
	private String fc41 = "fc41";
	private String fc42 = "fc42";
	private Calendar mCalendar;

	private File ImageToSave;
	private String imageWhatTimeRefreshed;

	Connecter mConnecter;
	Connecter mConnecterForHand;
	private File folder;
	private File tempimage;

	MyArrayAdapter mArrayAdapter;
	ArrayList<String> mArrayList;
	private SharedPreferences mSharedPreferences;
	private SharedPreferences.Editor mEditor;
	private Animation clickedanimation;

	private Card mFoodcourtPopulation;
	private Card mStats;
	private Card mLiveScreenCapture;

	ListView mDrawerList;
	DrawerLayout mDrawerLayout;
	MenuItem load;
	private String PREFS_NAME = "SavedFile";
	private MediaScannerConnection mMediaScannerConnection;
	private int mMaxCap;
	private int mFoodCourtOpenOrClosed;
	private int mSeatsRemaining = 4;
	private String mCrowdStatus = "Normal";
	private ActionBarDrawerToggle mDrawerToggle;
	private boolean LOADING_SUCCESS;
	private boolean LOADFINISHED = false;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Intent get = getIntent();
		mMaxCap = get.getIntExtra("mMaxCap", 10);
		mFoodCourtOpenOrClosed = get.getIntExtra("mFoodCourtOpenOrClosed", 1);


		mSharedPreferences =  getApplicationContext().getSharedPreferences(PREFS_NAME, 0);
		clickedanimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.clickedanimation);

		REFRESH_INTERVAL = mSharedPreferences.getInt("REFRESH_INTERVAL",  REFRESH_INTERVAL);
		REFRESH_INTERVAL_FOR_IMAGE = mSharedPreferences.getInt("REFRESH_INTERVAL_FOR_IMAGE",  REFRESH_INTERVAL_FOR_IMAGE);
		String loadedJson = mSharedPreferences.getString("FOODCOURT_POPULATION_HISTORY", null);
		if(loadedJson !=null){
			mGson = new  Gson();
			Type collectionType = new TypeToken<Collection<String>>(){}.getType();
			FOODCOURT_POPULATION_HISTORY = mGson.fromJson(loadedJson, collectionType);

		}

		mMediaScannerConnection = new MediaScannerConnection(getApplicationContext(), new MediaScannerConnectionClient() {

			@Override
			public void onScanCompleted(String path, Uri uri) {
				// TODO Auto-generated method stub
				mMediaScannerConnection.disconnect();
			}

			@Override
			public void onMediaScannerConnected() {
				// TODO Auto-generated method stub
				mMediaScannerConnection.scanFile(ImageToSave.toString(), null);

			}
		});

		mCalendar = Calendar.getInstance();
		mEnvironment = new Environment();
		ImageToSave = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "pic.jpg");
		ImageToSave.setReadable(true);
		mArrayList = new ArrayList<String>();
		mArrayList.add("Set refresh interval of population" );
		mArrayList.add("Set refresh interval of image" );
		mArrayList.add("Refresh population history");


		mRunnable = new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				populationUpdate();
			}
		};

		mRunnableForImage = new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				imageUpdate();
			}
		};

		mArrayAdapter = new MyArrayAdapter(this,mArrayList);
		mConnecter = new Connecter(URL,new DoAfterFinishForDocs() {

			@Override
			public void finishLoading(Document doc , boolean loadingSuccess) {
				LOADING_SUCCESS = loadingSuccess;

				try {
					mElements = doc.select(fc41);
					mElements2 = doc.select(fc42);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				mCalendar = Calendar.getInstance();

				try {
					FOODCOURT_POPULATION = Integer.parseInt(mElements.get(0).text()) + Integer.parseInt(mElements2.get(0).text());
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					FOODCOURT_POPULATION = -1;

				}
				if(FOODCOURT_POPULATION >= 0){
					FOODCOURT_POPULATION_HISTORY.add(String.valueOf(FOODCOURT_POPULATION) +" people at " + DateFormat.format(" hh:mma", mCalendar.getTime()));
				}

				runOnUiThread(
						new Runnable() {
							public void run() {
								// TODO Auto-generated method stub
								LOADFINISHED = true;
								initCards();
								clearLoading();

							}
						});


			}
		});




		mConnecterForHand = new Connecter(URL, new DoAfterFinishForDocs() {

			@Override
			public void finishLoading(Document doc , boolean loadingSuccess) {
				// TODO Auto-generated method stub

				LOADING_SUCCESS = loadingSuccess;

				try {
					mElements = doc.select(fc41);
					mElements2 = doc.select(fc42);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				mCalendar = Calendar.getInstance();

				try {
					FOODCOURT_POPULATION = Integer.parseInt(mElements.get(0).text()) + Integer.parseInt(mElements2.get(0).text());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					FOODCOURT_POPULATION = -1;
				}
				if(FOODCOURT_POPULATION >= 0){
					FOODCOURT_POPULATION_HISTORY.add(String.valueOf(FOODCOURT_POPULATION) +" people at " + DateFormat.format(" hh:mma", mCalendar.getTime()));
				}



				runOnUiThread(
						new Runnable() {
							public void run() {
								// TODO Auto-generated method stub
								LOADFINISHED = true;
								initCards();
								clearLoading();
								hand.postDelayed(mRunnable, REFRESH_INTERVAL);

							}
						});



			}
		});


		mImageGetter = new ImageGetter(UrlOfImage, new ImageGetter.HelperInterfaceForImage() {

			@Override
			public void finishedLoading(final Bitmap loadedimage) {
				// TODO Auto-generated method stub

				runOnUiThread(new Runnable() {
					public void run() {
						if(loadedimage !=null){
							mFoodcourtCapture = Bitmap.createScaledBitmap(loadedimage, 1024, 1024, true);
						}
						initCards();
						clearLoading();
						try {
							imageWhatTimeRefreshed = "Refreshed at " + DateFormat.format("hh:mma", mCalendar.getTime());
						} catch (Exception e) {
							// TODO Auto-generated catch block
							imageWhatTimeRefreshed = "";
						}
					}
				});


			}

			@Override
			public void errorConnecting() {
				// TODO Auto-generated method stub
				runOnUiThread(new Runnable() {
					public void run() {
						Toast.makeText(getBaseContext(), "Couldn't retreive live screen capture", Toast.LENGTH_SHORT).show();

					}
				});
			}
		});


		mImageGetterForHand= new ImageGetter(UrlOfImage, new ImageGetter.HelperInterfaceForImage() {

			@Override
			public void finishedLoading(final Bitmap loadedimage) {
				// TODO Auto-generated method stub

				runOnUiThread(new Runnable() {
					public void run() {
						if(loadedimage !=null){
							mFoodcourtCapture = Bitmap.createScaledBitmap(loadedimage, 1024, 768, true);
						}
						initCards();
						clearLoading();
						handforimages.postDelayed(mRunnableForImage,REFRESH_INTERVAL_FOR_IMAGE);
						try {
							imageWhatTimeRefreshed = "Refreshed at " + DateFormat.format("hh:mma", mCalendar.getTime());
						} catch (Exception e) {
							// TODO Auto-generated catch block
							imageWhatTimeRefreshed = "";
						}

					}
				});


			}

			@Override
			public void errorConnecting() {
				// TODO Auto-generated method stub
				runOnUiThread(new Runnable() {
					public void run() {
						Toast.makeText(getBaseContext(), "Couldn't retreive live screen capture", Toast.LENGTH_SHORT).show();
						handforimages.postDelayed(mRunnableForImage,REFRESH_INTERVAL_FOR_IMAGE);


					}
				});
			}
		});



		mDrawerList = (ListView)findViewById(R.id.left_menu);
		mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
		mDrawerList.setAdapter(mArrayAdapter);
		mDrawerList.setDivider(this.getResources().getDrawable(R.drawable.divider2));
		mDrawerList.setDividerHeight(1);
		mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub

				switch(arg2){

				case 0:
					showDialogRefreshInterval();
					break;

				case 1:
					showDialogRefreshIntervalForImage();
					break;

				case 2:
					refreshPopulationHistory();
					break;


				}



			}
		});


		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.back, R.string.hello_world, R.string.hello_world);
		mDrawerLayout.setDrawerListener(mDrawerToggle);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		initCards();




		threadStarter = new Thread(mConnecter);
		threadStarterForImages = new Thread(mImageGetter);




	}



	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		if(STARTED_POPULATION_HISTORY_ACTIVITY == false){
			handforimages.removeCallbacksAndMessages(null);
			hand.removeCallbacksAndMessages(null);
			PAUSED_APP = true;
		}
		mGson = new Gson();
		String SavedJsonString = mGson.toJson(FOODCOURT_POPULATION_HISTORY);
		mEditor = mSharedPreferences.edit();
		mEditor.putString("FOODCOURT_POPULATION_HISTORY", SavedJsonString);
		mEditor.commit();

		super.onPause();

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		STARTED_POPULATION_HISTORY_ACTIVITY = false;
		if(PAUSED_APP == true){
			imageUpdate();
			populationUpdate();
			PAUSED_APP = false;
		}
		if(tempimage != null){
			tempimage.delete();
		}

		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		handforimages.removeCallbacksAndMessages(null);
		hand.removeCallbacksAndMessages(null);
		mGson = new Gson();
		String SavedJsonString = mGson.toJson(FOODCOURT_POPULATION_HISTORY);
		mEditor = mSharedPreferences.edit();
		mEditor.putString("FOODCOURT_POPULATION_HISTORY", SavedJsonString);
		mEditor.commit();
		if(tempimage != null){
			tempimage.delete();
		}
		super.onDestroy();
	}

	private void refreshPopulationHistory(){


		FOODCOURT_POPULATION_HISTORY.clear();



	}

	private void showDialogRefreshInterval(){


		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle("Enter time in miliseconds");
		final EditText text = new EditText(this);
		builder.setView(text);
		builder.setNegativeButton("Cancel", null);
		builder.setPositiveButton("Enter", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub

				try {
					REFRESH_INTERVAL = Integer.parseInt(text.getText().toString());
					mEditor = mSharedPreferences.edit();
					mEditor.putInt("REFRESH_INTERVAL", REFRESH_INTERVAL);
					mEditor.commit();
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					Toast.makeText(getBaseContext(), "Please enter only a number!", Toast.LENGTH_SHORT).show();
				}



			}


		});


		builder.show();

	}


	private void showDialogRefreshIntervalForImage(){


		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle("Enter time in miliseconds");
		final EditText text = new EditText(this);
		builder.setView(text);
		builder.setNegativeButton("Cancel", null);
		builder.setPositiveButton("Enter", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub

				try {
					REFRESH_INTERVAL_FOR_IMAGE = Integer.parseInt(text.getText().toString());
					mEditor = mSharedPreferences.edit();
					mEditor.putInt("REFRESH_INTERVAL_FOR_IMAGE", REFRESH_INTERVAL_FOR_IMAGE);
					mEditor.commit();
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					Toast.makeText(getBaseContext(), "Please enter only a number!", Toast.LENGTH_SHORT).show();
				}



			}


		});


		builder.show();

	}


	private void updateCards(){


		if(threadStarter.isAlive() != true){
			threadStarter = new Thread(mConnecter);

			threadStarter.start();
			showLoading();
		}


		if(threadStarterForImages.isAlive() != true){

			threadStarterForImages = new Thread(mImageGetter);
			threadStarterForImages.start();
			showLoading();

		}


	}




	private void populationUpdate(){



		if(threadStarter.isAlive() != true){
			threadStarter = new Thread(mConnecterForHand);

			threadStarter.start();
			showLoading();
		}


	}

	private void imageUpdate(){

		if(threadStarterForImages.isAlive() != true){

			threadStarterForImages = new Thread(mImageGetterForHand);
			threadStarterForImages.start();
			showLoading();

		}
	}


	private void sharePopulation(){

		Intent go = new Intent(Intent.ACTION_SEND);
		go.setType("text/plain");
		go.putExtra(Intent.EXTRA_TEXT, "There are now " + FOODCOURT_POPULATION + " people in " + FOODCOURT);
		startActivity(go);

	}

	private void shareImage(){

		Intent go = new Intent(Intent.ACTION_SEND);
		go.setType("image/jpeg");
		FileOutputStream fos;

		try {
			tempimage = new File(Environment.getExternalStorageDirectory(), "temp.jpg");
			fos = new FileOutputStream(tempimage);
			mFoodcourtCapture.compress(Bitmap.CompressFormat.JPEG, 100, fos);
			fos.flush();
			go.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(tempimage));
			startActivity(go);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}




	}

	private void saveImageToSDCard(){

		FileOutputStream fos;
		try {
			fos = new FileOutputStream(ImageToSave);
			mFoodcourtCapture.compress(Bitmap.CompressFormat.JPEG, 100, fos);
			fos.flush();
			Toast.makeText(getBaseContext(), "Image Saved!", Toast.LENGTH_SHORT).show();
			mMediaScannerConnection.connect();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			Toast.makeText(getBaseContext(), "File Not Found!", Toast.LENGTH_SHORT).show();

			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Toast.makeText(getBaseContext(), "IOException!", Toast.LENGTH_SHORT).show();

			e.printStackTrace();
		}




	}

	private void shareStats(){



		String data = "Crowd Status : " + mCrowdStatus + "\n"+ "Number of chairs remaining : " + mSeatsRemaining;


		Intent go = new Intent(Intent.ACTION_SEND);
		go.setType("text/plain");
		go.putExtra(Intent.EXTRA_TEXT, data);
		startActivity(go);


	}

	private void initCards(){




		mCardUI = (CardUI)findViewById(R.id.cardsview);


		try {
			mCardUI.clearCards();
		} catch (Exception e) {
			// TODO Auto-generated catch block
		}


		mFoodcourtPopulation = new Card(FOODCOURT) {




			@Override
			public View getCardContent(Context context) {
				View view = LayoutInflater.from(context).inflate(R.layout.card_ex, null);

				((TextView) view.findViewById(R.id.title)).setText(title);
				TextView text = (TextView)view.findViewById(R.id.description);
				final ImageView iv = (ImageView)view.findViewById(R.id.showmore);
				final ImageView share = (ImageView)view.findViewById(R.id.share);
				share.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						share.startAnimation(clickedanimation);
						sharePopulation();
					}




				});
				iv.setVisibility(View.INVISIBLE);
				if(SHOW_POPULATION_HISTORY != true){

					if(FOODCOURT_POPULATION <= 0 ){
						if(LOADING_SUCCESS == true){
							text.setText("There are now " + 0 + " people in " + FOODCOURT);
						}
						else if (LOADING_SUCCESS == false){

							text.setText("Loading...");


						}






					}
					else{
						text.setText("There are now " + FOODCOURT_POPULATION + " people in " + FOODCOURT);
					}

				}

				else {

					try {
						if(FOODCOURT_POPULATION <= 0){

							if(LOADING_SUCCESS == true){
								text.setText("There are now " + 0 + " people in " + FOODCOURT);
								text.append("\n");

							}
							else{

								text.setText("Loading...");
								text.append("\n");


							}


						}else{
							text.setText("There are now " + FOODCOURT_POPULATION + " people in " + FOODCOURT);
							text.append("\n");
						}
						int i = FOODCOURT_POPULATION_HISTORY.size();
						if(i>3){
							iv.setVisibility(View.VISIBLE);
							iv.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View v) {
									// TODO Auto-generated method stub
									iv.startAnimation(clickedanimation);
									goToPopulationHistory();	
								}
							});

						}
						for(; i > 0 ; i --){
							text.append("There was " + FOODCOURT_POPULATION_HISTORY.get(i-1) + "\n" );

						}



					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}



				}



				return view;
			}


		};

		mFoodcourtPopulation.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				/*if(SHOW_POPULATION_HISTORY ==false){
				SHOW_POPULATION_HISTORY = true;
				initCards();
				}
				else{
					SHOW_POPULATION_HISTORY = false;
					initCards();


				}*/
				if(SHOW_POPULATION_HISTORY == true){
					SHOW_POPULATION_HISTORY = false;
				}else{
					SHOW_POPULATION_HISTORY = true;
				}
				initCards();


			}
		});


		mStats = new Card("Statistics"){

			@Override
			public View getCardContent(Context context) {
				// TODO Auto-generated method stub
				final View view = LayoutInflater.from(context).inflate(R.layout.card_stats, null);
				TextView title = (TextView)view.findViewById(R.id.titlestats);

				TextView crowdStatus = (TextView)view.findViewById(R.id.crowdstatus);
				TextView remainingChairs = (TextView)view.findViewById(R.id.aprox_number_of_chairs_remaining);
				final ImageView shareStats = (ImageView)view.findViewById(R.id.sharestats);
				title.setText("Statistics");



				if(LOADING_SUCCESS == true){
					if(FOODCOURT_POPULATION < ((mMaxCap/10) * 2)){ // less than 20%
						crowdStatus.setText("Crowd Status : Quiet  ");
					}
					if (((mMaxCap/10) * 2) <= FOODCOURT_POPULATION && FOODCOURT_POPULATION < ((mMaxCap/10) * 4)) // more than 20%, less than 40%
					{

						crowdStatus.setText("Crowd Status : Normal  ");


					}

					if (((mMaxCap/10) * 4) <= FOODCOURT_POPULATION && FOODCOURT_POPULATION < ((mMaxCap/10) * 6)) // more than 40%, less than 60%
					{


						crowdStatus.setText("Crowd Status : Busy  ");


					}

					if (((mMaxCap/10) * 6) <= FOODCOURT_POPULATION && FOODCOURT_POPULATION < ((mMaxCap/10) * 8))// more than 60%, less than 80%
					{


						crowdStatus.setText("Crowd Status : Crowded  ");


					}

					if (((mMaxCap/10) * 8) <= FOODCOURT_POPULATION && FOODCOURT_POPULATION < ((mMaxCap/10) * 10))// more than 80 %, less than 100%
					{


						crowdStatus.setText("Crowd Status : Very Crowded  ");


					}


					if (((mMaxCap/10) * 10) <=  FOODCOURT_POPULATION)// more than 100 %
					{


						crowdStatus.setText("Crowd Status : Full  ");


					}
				}

				else {

					crowdStatus.setText("Loading... ");


				}

				mSeatsRemaining = mMaxCap - FOODCOURT_POPULATION;

				if(mSeatsRemaining < 0){

					mSeatsRemaining = 0;

				}
				if(LOADING_SUCCESS == true){
					remainingChairs.setText("Estimated number of chairs remaining : " + mSeatsRemaining);
				}


				shareStats.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						shareStats.startAnimation(clickedanimation);
						shareStats();
					}
				});




				return view;

			}






		};

		mLiveScreenCapture = new Card("Live screen capture") {

			@Override
			public View getCardContent(Context context) {
				// TODO Auto-generated method stub
				final View view = LayoutInflater.from(context).inflate(R.layout.card_picture, null);

				Animation rotate = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim);


				TextView desc = (TextView)view.findViewById(R.id.description);
				ImageView iv = (ImageView)view.findViewById(R.id.imageView1);
				iv.startAnimation(rotate);
				final ImageView save = (ImageView)view.findViewById(R.id.save);
				final ImageView sharepic = (ImageView)view.findViewById(R.id.sharepic);
				sharepic.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						sharepic.startAnimation(clickedanimation);
						shareImage();
					}
				});
				save.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						save.startAnimation(clickedanimation);
						saveImageToSDCard();
					}
				});
				if(mFoodcourtCapture == null){
					iv.setImageResource(R.drawable.spinner);
				}
				else if(mFoodcourtCapture != null){
					iv.setImageBitmap(mFoodcourtCapture);
					iv.clearAnimation();
					desc.setText(imageWhatTimeRefreshed);
				}

				return view;

			}
		};



		mCardUI.addCard(mFoodcourtPopulation);
		mCardUI.addCard(mStats);
		mCardUI.addCard(mLiveScreenCapture);



		mCardUI.setSwipeable(false);



		mCardUI.refresh();

	}

	private void goToPopulationHistory(){

		Intent go = new Intent(getApplicationContext(),PopulationHistory.class);
		go.putExtra("PopulationHistory", FOODCOURT_POPULATION_HISTORY);
		STARTED_POPULATION_HISTORY_ACTIVITY = true;
		startActivity(go);

	}
	private void clearLoading(){

		if(load.getActionView() != null){
			load.getActionView().clearAnimation();
			load.setActionView(null);
		}
	}

	private void showLoading(){
		LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		ImageView loading = (ImageView) inflater.inflate(R.layout.pic, null);
		Animation rotate = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim);

		loading.startAnimation(rotate);
		if (load.getActionView() == null){
			load.setActionView(loading);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub

		if(item.getItemId() == R.id.refresh){


			updateCards();
			
			/*Intent i = new Intent(getApplicationContext(), VideoPlayback.class);
			startActivity(i);*/ // Experimental code please ignore


		}
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}


		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		menu.add(Menu.NONE, LOADING, Menu.NONE, "").setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS);
		
		getMenuInflater().inflate(R.menu.refresh,menu);
		load = menu.findItem(1);

		hand = new Handler();
		hand.post(mRunnable);

		handforimages = new Handler();
		handforimages.post(mRunnableForImage);

		return true;
	}

}
