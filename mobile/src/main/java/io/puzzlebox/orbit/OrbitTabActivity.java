package io.puzzlebox.orbit;

/** 
 * Puzzlebox Orbit
 *
 * Copyright Puzzlebox Productions, LLC (2012-2013)
 *
 * This code is released under the GNU Public License (GPL) version 2
 * For more information please refer to http://www.gnu.org/copyleft/gpl.html
 *
 */


import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.puzzlebox.orbit.R;
import io.puzzlebox.orbit.protocol.AudioHandler;
import io.puzzlebox.orbit.ui.FragmentTabAdvanced;
import io.puzzlebox.orbit.ui.FragmentTabFlightThinkGear;
import io.puzzlebox.orbit.ui.FragmentTabSupport;
import io.puzzlebox.orbit.ui.FragmentTabWelcome;

public class OrbitTabActivity extends FragmentActivity {


	/**
	 * Configuration
	 */
	int minAPIVersion = 14;
	String versionName;

	String eegDevice = "ThinkGear";
	//	String eegDevice = "Emotiv";

	int eegAttention = 0;
	int eegMeditation = 0;
	public int eegPower = 0;
	int eegSignal = 0;
	boolean demoFlightMode = false;
	Number[] rawEEG = new Number[512];
	int arrayIndex = 0;
	public boolean generateAudio = true;
	public boolean invertControlSignal = false;
	boolean tiltSensorControl = false;
	public int deviceWarningMessagesDisplayed = 0;


	/**
	 * UI
	 */
	TabHost mTabHost;
	ViewPager mViewPager;
	TabsAdapter mTabsAdapter;
	String idTabFragmentWelcome;
	String idTabFragmentFlightThinkGear;
	String idTabFragmentFlightEmotiv;
	String idTabFragmentAdvanced;
	String idTabFragmentSupport;


	Configuration config;

	String saveToAdvanced = null;

	LinearLayout layoutControl;
	LinearLayout layoutAudioService;
	LinearLayout layoutAdvancedOptions;
	LinearLayout layoutInvertControlSignal;
	View viewSpaceGenerateAudio;


	/**
	 * Logging
	 */
	/** set to "false" for production releases */
	//	boolean DEBUG = true;
	boolean DEBUG = false;
	String TAG = "OrbitTabActivity";


	/**
	 * Audio 
	 * 
	 * By default the flight control command is hard-coded into WAV files
	 * When "Generate Control Signal" is enabled the tones used to communicate 
	 * with the infrared dongle are generated on-the-fly.
	 */
	int audioFile = R.raw.throttle_hover_android_common;
	//	int audioFile = R.raw.throttle_hover_android_htc_one_x;

	private SoundPool soundPool;
	private int soundID;
	boolean loaded = false;


	/**
	 * AudioHandler
	 */
	AudioHandler audioHandler = new AudioHandler();


	/**
	 * Flight status
	 */
	boolean flightActive = false;


//	public void initializeSingleScreen() {
//		Log.v(TAG, "initializeSingleScreen");
//
//
//		Intent intent = new Intent(this, OrbitActivity.class);
//		startActivity(intent);
//
//
//	}


	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == 2) {
			finish();
		}
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {

		/** 
		 * This method is called when the program is first loaded
		 * or when it is re-loaded into memory after being killed
		 * by the Task Manager.
		 */

		super.onCreate(savedInstanceState);

		// If necessary support isn't detect, replace current activity with the single-screen version
		if (android.os.Build.VERSION.SDK_INT < minAPIVersion) {
//			initializeSingleScreen();
			return;
		}


		// Get package version
		try {
			versionName = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
		}
		catch (NameNotFoundException e) {
			Log.v(TAG, e.getMessage());
		}


		/**
		 * Configure the UI elements
		 */

		String tabWelcomeLabel = getResources().getString(R.string.tab_welcome);
		String tabFlightLabel = getResources().getString(R.string.tab_flight);
		String tabAdvancedLabel = getResources().getString(R.string.tab_advanced);
		String tabSupportLabel = getResources().getString(R.string.tab_support);


		config = getResources().getConfiguration();

		switch(config.screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) {
		case Configuration.SCREENLAYOUT_SIZE_SMALL:
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			tabWelcomeLabel = getResources().getString(R.string.tab_welcome_small);
			tabFlightLabel = getResources().getString(R.string.tab_flight_small);
			tabAdvancedLabel = getResources().getString(R.string.tab_advanced_small);
			tabSupportLabel = getResources().getString(R.string.tab_support_small);
			break;
		case Configuration.SCREENLAYOUT_SIZE_NORMAL:
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			break;
		case Configuration.SCREENLAYOUT_SIZE_LARGE:
			tabWelcomeLabel = getResources().getString(R.string.tab_welcome_large);
			tabFlightLabel = getResources().getString(R.string.tab_flight_large);
			tabAdvancedLabel = getResources().getString(R.string.tab_advanced_large);
			tabSupportLabel = getResources().getString(R.string.tab_support_large);
			break;
		case Configuration.SCREENLAYOUT_SIZE_XLARGE:
			tabWelcomeLabel = getResources().getString(R.string.tab_welcome_large);
			tabFlightLabel = getResources().getString(R.string.tab_flight_large);
			tabAdvancedLabel = getResources().getString(R.string.tab_advanced_large);
			tabSupportLabel = getResources().getString(R.string.tab_support_large);
			break;
		case Configuration.SCREENLAYOUT_SIZE_UNDEFINED:
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			break;
		}


		setContentView(R.layout.fragment_tabs_pager);

		if (eegDevice == "ThinkGear") {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}

		// Tabs
		mTabHost = (TabHost)findViewById(android.R.id.tabhost);
		mTabHost.setup();

		mViewPager = (ViewPager)findViewById(R.id.pager);

		mTabsAdapter = new TabsAdapter(this, mTabHost, mViewPager);

		mTabsAdapter.addTab(mTabHost.newTabSpec("Orbit").setIndicator(tabWelcomeLabel),
				FragmentTabWelcome.class, null);

		if (eegDevice == "ThinkGear") {
			mTabsAdapter.addTab(mTabHost.newTabSpec("Orbit").setIndicator(tabFlightLabel),
					FragmentTabFlightThinkGear.class, null);
		}
		//		else if (eegDevice == "Emotiv") {
		//			mTabsAdapter.addTab(mTabHost.newTabSpec("Orbit").setIndicator(tabFlightLabel),
		//					FragmentTabFlightEmotiv.class, null);
		//		}

		mTabsAdapter.addTab(mTabHost.newTabSpec("Orbit").setIndicator(tabAdvancedLabel),
				FragmentTabAdvanced.class, null);

		mTabsAdapter.addTab(mTabHost.newTabSpec("Orbit").setIndicator(tabSupportLabel),
				FragmentTabSupport.class, null);


		if (savedInstanceState != null) {
			mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));
		}


		layoutControl = (LinearLayout)findViewById(R.id.layoutControl);
		layoutAudioService = (LinearLayout)findViewById(R.id.layoutAudioService);
		layoutAdvancedOptions = (LinearLayout)findViewById(R.id.layoutAdvancedOptions);
		layoutInvertControlSignal = (LinearLayout)findViewById(R.id.layoutInvertControlSignal);


		/**
		 * Update settings according to default UI
		 */
		updateScreenLayout();


		/**
		 * Prepare audio stream
		 */

		// TODO Testing DroidDNA
//		maximizeAudioVolume(); // Automatically set media volume to maximum

		/** Set the hardware buttons to control the audio output */
		this.setVolumeControlStream(AudioManager.STREAM_MUSIC);

		/** Preload the flight control WAV file into memory */
		soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
		soundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
			public void onLoadComplete(SoundPool soundPool, int sampleId,
					int status) {
				loaded = true;
			}
		});
		soundID = soundPool.load(this, audioFile, 1);


		/**
		 * AudioHandler
		 */
		audioHandler.start();


	} // onCreate


	// ################################################################

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public boolean onCreateOptionsMenu(Menu menu) {

		SubMenu sub = menu.addSubMenu("About");
		sub.getItem().setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

		return true;

	} // onCreateOptionsMenu


	// ################################################################

	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home || item.getItemId() == 0) {
			Toast.makeText(this, "Puzzlebox Orbit\n Version: " + versionName, Toast.LENGTH_SHORT).show();
			return false;
		}
		Toast.makeText(this, "About menu: \"" + item.getTitle() + "\"", Toast.LENGTH_SHORT).show();
		return true;

	} // onOptionsItemSelected


	// ################################################################

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		try {
			outState.putString("tab", mTabHost.getCurrentTabTag());;
		}
		catch (NullPointerException e) {
			Log.v(TAG, "onSaveInstanceState() NullPointerException");
		}

	} // onSaveInstanceState


	// ################################################################

	/**
	 * This is a helper class that implements the management of tabs and all
	 * details of connecting a ViewPager with associated TabHost.  It relies on a
	 * trick.  Normally a tab host has a simple API for supplying a View or
	 * Intent that each tab will show.  This is not sufficient for switching
	 * between pages.  So instead we make the content part of the tab host
	 * 0dp high (it is not shown) and the TabsAdapter supplies its own dummy
	 * view to show as the tab content.  It listens to changes in tabs, and takes
	 * care of switch to the correct paged in the ViewPager whenever the selected
	 * tab changes.
	 */

	public static class TabsAdapter extends FragmentPagerAdapter
	implements TabHost.OnTabChangeListener, ViewPager.OnPageChangeListener {
		private final Context mContext;
		private final TabHost mTabHost;
		private final ViewPager mViewPager;
		private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();

		static final class TabInfo {
			@SuppressWarnings("unused")
			private final String tag;
			private final Class<?> clss;
			private final Bundle args;

			TabInfo(String _tag, Class<?> _class, Bundle _args) {
				tag = _tag;
				clss = _class;
				args = _args;
			}

		} // TabsAdapter


		// ################################################################

		static class DummyTabFactory implements TabHost.TabContentFactory {
			private final Context mContext;

			public DummyTabFactory(Context context) {
				mContext = context;
			}

			public View createTabContent(String tag) {
				View v = new View(mContext);
				v.setMinimumWidth(0);
				v.setMinimumHeight(0);
				return v;
			}
		}

		public TabsAdapter(FragmentActivity activity, TabHost tabHost, ViewPager pager) {
			super(activity.getSupportFragmentManager());
			mContext = activity;
			mTabHost = tabHost;
			mViewPager = pager;
			mTabHost.setOnTabChangedListener(this);
			mViewPager.setAdapter(this);
			mViewPager.setOnPageChangeListener(this);
		}

		public void addTab(TabHost.TabSpec tabSpec, Class<?> clss, Bundle args) {
			tabSpec.setContent(new DummyTabFactory(mContext));
			String tag = tabSpec.getTag();

			TabInfo info = new TabInfo(tag, clss, args);
			mTabs.add(info);
			mTabHost.addTab(tabSpec);
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return mTabs.size();
		}

		@Override
		public Fragment getItem(int position) {
			TabInfo info = mTabs.get(position);
			return Fragment.instantiate(mContext, info.clss.getName(), info.args);
		}

		public void onTabChanged(String tabId) {
			int position = mTabHost.getCurrentTab();
			mViewPager.setCurrentItem(position);
		}

		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
		}

		public void onPageSelected(int position) {
			// Unfortunately when TabHost changes the current tab, it kindly
			// also takes care of putting focus on it when not in touch mode.
			// This hack tries to prevent this from pulling focus out of our
			// ViewPager.
			TabWidget widget = mTabHost.getTabWidget();
			int oldFocusability = widget.getDescendantFocusability();
			widget.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
			mTabHost.setCurrentTab(position);
			widget.setDescendantFocusability(oldFocusability);
		}

		public void onPageScrollStateChanged(int state) {
		}
	} // TabsAdapter


	// ################################################################

	public void onPause() {

		Log.v(TAG, "onPause()");

		super.onPause();

	} // onPause


	// ################################################################

	@Override
	protected void onResume() {

		/**
		 * This method is called when the Activity has been
		 * resumed after being placed in the background
		 */

		Log.v(TAG, "onResume()");

		super.onResume();

	} // onResume


	// ################################################################

	public void onStop() {

		Log.v(TAG, "onStop()");

		super.onStop();

	} // onStop


	// ################################################################

	@Override
	public void onDestroy() {

		/**
		 * This method is called when the Activity is terminated
		 */

		try {

			if (audioHandler != null)
				audioHandler.shutdown();

		} catch (Exception e) {
			Log.v(TAG, "Exception: onDestroy()");
			e.printStackTrace();
		}

		super.onDestroy();

	} // onDestroy


	// ################################################################

	public boolean onUnbind(Intent intent) {

		Log.d(this.getClass().getName(), "UNBIND");

		try {

			if (audioHandler != null)
				audioHandler.shutdown();

		} catch (Exception e) {
			Log.v(TAG, "Exception: onUnbind()");
			e.printStackTrace();
		}

		return true;

	} // onUnbind


	// ################################################################

	public void setTabFragmentWelcome(String t) {
		idTabFragmentWelcome = t;
	}
	public void setTabFragmentFlightThinkGear(String t) {
		idTabFragmentFlightThinkGear = t;
	}
	public void setTabFragmentFlightEmotiv(String t) {
		idTabFragmentFlightEmotiv = t;
	}
	public void setTabFragmentAdvanced(String t) {
		idTabFragmentAdvanced = t;
	}
	public void setTabFragmentSupport(String t) {
		idTabFragmentSupport = t;
	}

	public String getTabFragmentWelcome() {
		return idTabFragmentWelcome;
	}
	public String getTabFragmentFlightThinkGear() {
		return idTabFragmentFlightThinkGear;
	}
	public String getTabFragmentFlightEmotiv() {
		return idTabFragmentFlightEmotiv;
	}
	public String getTabFragmentAdvanced() {
		return idTabFragmentAdvanced;
	}
	public String getTabFragmentSupport() {
		return idTabFragmentSupport;
	}

	public void putsaveToAdvanced(String textToSave) {
		saveToAdvanced = textToSave;
	}

	public String getsaveToAdvanced() {
		String t = saveToAdvanced;
		return t;
	}


	// ################################################################

	public void appendDebugConsole(String text) {

		/**
		 * This method is called to write a status message
		 * to the text display area, then automatically
		 * scroll to the bottom of visible text.
		 * (Currently disabled)
		 *
		 * One alternative method is to set android:layout_gravity="bottom"
		 * on the textView
		 *
		 * Another method is described here:
		 * http://stackoverflow.com/questions/5101448/android-auto-scrolling-down-the-edittextview-for-chat-apps
		 */


		FragmentTabAdvanced fragmentAdvanced =
				(FragmentTabAdvanced) getSupportFragmentManager().findFragmentByTag(getTabFragmentAdvanced());

//		if (fragmentAdvanced != null)
//			fragmentAdvanced.appendDebugConsole(text);


	} // appendDebugConsole


	// ################################################################

	public void updateScreenLayout() {

		switch(config.screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK){
		case Configuration.SCREENLAYOUT_SIZE_SMALL:
			Log.v(TAG, "screenLayout: small");
			updateScreenLayoutSmall();
			break;
		case Configuration.SCREENLAYOUT_SIZE_NORMAL:
			Log.v(TAG, "screenLayout: normal");
			updateScreenLayoutSmall();
			break;
		case Configuration.SCREENLAYOUT_SIZE_LARGE:
			Log.v(TAG, "screenLayout: large");
			break;
		case Configuration.SCREENLAYOUT_SIZE_XLARGE:
			Log.v(TAG, "screenLayout: xlarge");
			break;
		case Configuration.SCREENLAYOUT_SIZE_UNDEFINED:
			Log.v(TAG, "screenLayout: undefined");
			updateScreenLayoutSmall();
			break;
		}


	} // updateScreenLayout


	// ################################################################

	public void updateScreenLayoutSmall() {

		if (eegDevice == "ThinkGear") {
			FragmentTabFlightThinkGear fragmentFlight =
					(FragmentTabFlightThinkGear) getSupportFragmentManager().findFragmentByTag( getTabFragmentFlightThinkGear() );

			if (fragmentFlight != null)
				fragmentFlight.updateScreenLayoutSmall();

		}
		//		else if (eegDevice == "Emotiv") {
		//			FragmentTabFlightEmotiv fragmentFlight =
		//					(FragmentTabFlightEmotiv) getSupportFragmentManager().findFragmentByTag( getTabFragmentFlightEmotiv() );
		//
		//			if (fragmentFlight != null)
		//				fragmentFlight.updateScreenLayoutSmall();
		//		}


	} // updateScreenLayoutSmall


	// ################################################################

	public void maximizeAudioVolume() {

		AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		int currentVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);

		if (currentVolume < audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC)) {

			Log.v(TAG, "Previous volume:" + currentVolume);

			Toast.makeText(this, "Automatically setting volume to maximum", Toast.LENGTH_SHORT).show();

			AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
			audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
					audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
					AudioManager.FLAG_SHOW_UI);

		}


	} // maximizeAudioVolume


	// ################################################################

	public void updatePower() {

		/**
		 * This method updates the power level of the
		 * "Throttle" and triggers the audio stream
		 * which is used to fly the helicopter
		 */

		//		if (eegDevice == "ThinkGear") {
		//
		//			FragmentTabFlightThinkGear fragmentFlight =
		//					(FragmentTabFlightThinkGear) getSupportFragmentManager().findFragmentByTag( getTabFragmentFlightThinkGear() );
		//
		//			if (fragmentFlight != null)
		//				eegPower = fragmentFlight.updatePower();
		//
		//		}
		//		//		else if (eegDevice == "Emotiv") {
		//		//
		//		//			FragmentTabFlightEmotiv fragmentFlight =
		//		//					(FragmentTabFlightEmotiv) getSupportFragmentManager().findFragmentByTag( getTabFragmentFlightEmotiv() );
		//		//
		//		//			if (fragmentFlight != null)
		//		//				eegPower = fragmentFlight.updatePower();
		//		//
		//		//		}

		if (eegPower > 0) {

			/** Start playback of audio control stream */
			if (flightActive == false) {
				playControl();
			}

			updateScore();

			flightActive = true;

		} else {

			/** Land the helicopter */
			stopControl();

			resetCurrentScore();

		}

		if (DEBUG)
			Log.v(TAG, "flightActive: " + flightActive);


	} // updatePower


	// ################################################################

	public void playControl() {

		FragmentTabAdvanced fragmentAdvanced =
				(FragmentTabAdvanced) getSupportFragmentManager().findFragmentByTag( getTabFragmentAdvanced() );

		if (generateAudio) {

			/**
			 * Generate signal on the fly
			 */

			// Handle controlled descent thread if activated
			if ((fragmentAdvanced.orbitControlledDescentTask != null) &&
					(fragmentAdvanced.orbitControlledDescentTask.keepDescending)) {
				fragmentAdvanced.orbitControlledDescentTask.callStopAudio = false;
				fragmentAdvanced.orbitControlledDescentTask.keepDescending = false;
			}


			//			if (audioHandler != null) {

			//				serviceBinder.ifFlip = fragmentAdvanced.checkBoxInvertControlSignal.isChecked(); // if checked then flip
			audioHandler.ifFlip = invertControlSignal; // if checked then flip

			int channel = 0; // default "A"

			if (fragmentAdvanced != null)
				channel = fragmentAdvanced.radioGroupChannel.getCheckedRadioButtonId();

			//				if (demoFlightMode)
			//					updateAudioHandlerLoopNumberWhileMindControl(200);
			//				else
			//					updateAudioHandlerLoopNumberWhileMindControl(4500);
			//
			//			updateAudioHandlerLoopNumberWhileMindControl(5000);

			updateAudioHandlerLoopNumberWhileMindControl(-1); // Loop infinite for easier user testing

			updateAudioHandlerChannel(channel);

			audioHandler.mutexNotify();
			//			}


		} else {

			/**
			 * Play audio control file
			 */

			/** Getting the user sound settings */
			AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
			//			float actualVolume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
			float maxVolume = (float) audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
			//			float volume = actualVolume / maxVolume;

			audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (int) maxVolume, 0);
			/** Is the sound loaded already? */
			if (loaded) {
				//				soundPool.play(soundID, volume, volume, 1, 0, 1f);
				//				soundPool.setVolume(soundID, 1f, 1f);
				//				soundPool.play(soundID, maxVolume, maxVolume, 1, 0, 1f); // Fixes Samsung Galaxy S4 [SGH-M919]

				soundPool.play(soundID, 1f, 1f, 1, 0, 1f); // Fixes Samsung Galaxy S4 [SGH-M919]

				// TODO No visible effects of changing these variables on digital oscilloscope
				//				soundPool.play(soundID, 0.5f, 0.5f, 1, 0, 0.5f);
				if (DEBUG)
					Log.v(TAG, "Played sound");
			}

		}

	} // playControl


	// ################################################################

	public void stopControl() {

		FragmentTabAdvanced fragmentAdvanced =
				(FragmentTabAdvanced) getSupportFragmentManager().findFragmentByTag( getTabFragmentAdvanced() );


		// Initial Controlled Descent if activated by user
		if ((generateAudio) &&
				(flightActive) &&
				(fragmentAdvanced != null) &&
				(fragmentAdvanced.checkBoxControlledDescent.isChecked()) &&
				(audioHandler != null)) {

			fragmentAdvanced.registerControlledDescent();

		} else {

			stopAudio();

		}

		flightActive = false;


	} // stopControl


	// ################################################################

	public void stopAudio() {

		/**
		 * stop AudioTrack as well as destroy service.
		 */

		audioHandler.keepPlaying = false;

		/**
		 * Stop playing audio control file
		 */

		if (soundPool != null) {
			try {
				soundPool.stop(soundID);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}


	} // stopControl


	// ################################################################

	public void demoMode(View view) {

		/**
		 * Demo mode is called when the "Test Helicopter" button is pressed.
		 * This method can be easily adjusted for testing new features
		 * during development.
		 */

		Log.v(TAG, "Sending Test Signal to Helicopter");
		appendDebugConsole("Sending Test Signal to Helicopter\n");

		demoFlightMode = true;
		flightActive = true;

		FragmentTabAdvanced fragmentAdvanced =
				(FragmentTabAdvanced) getSupportFragmentManager().findFragmentByTag( getTabFragmentAdvanced() );

		//		if (fragmentAdvanced.checkBoxGenerateAudio.isChecked())
		if (generateAudio && (fragmentAdvanced != null))
			eegPower = fragmentAdvanced.seekBarThrottle.getProgress();
		else
			eegPower = 100;

		playControl();

		demoFlightMode = false;


	} // demoMode


	// ################################################################

	public void demoStop(View view) {

		eegPower = 0;

		stopControl();

	} // demoStop


	// ################################################################

	public void updateScore() {

		FragmentTabFlightThinkGear fragmentFlight =
				(FragmentTabFlightThinkGear) getSupportFragmentManager().findFragmentByTag( getTabFragmentFlightThinkGear() );

		if (fragmentFlight != null)
			fragmentFlight.updateScore();

	} // updateScore


	// ################################################################

	public void resetCurrentScore() {

		FragmentTabFlightThinkGear fragmentFlight =
				(FragmentTabFlightThinkGear) getSupportFragmentManager().findFragmentByTag( getTabFragmentFlightThinkGear() );

		if (fragmentFlight != null)
			fragmentFlight.resetCurrentScore();

	} // resetCurrentScore


	// ################################################################

	/**
	 * the audioHandler to update command
	 */
	public void updateAudioHandlerCommand(Integer[] command) {

		this.audioHandler.command = command;
		this.audioHandler.updateControlSignal();


	} // updateServiceBinderCommand


	// ################################################################

	/**
	 * the audioHandler to update channel
	 */
	public void updateAudioHandlerChannel(int channel) {

		this.audioHandler.channel = channel;
		this.audioHandler.updateControlSignal();


	} // updateServiceBinderChannel


	// ################################################################

	/**
	 * @param number the audioHandler to update loop number while mind control
	 */
	public void updateAudioHandlerLoopNumberWhileMindControl(int number) {

		this.audioHandler.loopNumberWhileMindControl = number;


	} // updateServiceBinderLoopNumberWhileMindControl


	// ################################################################

	public void resetControlSignal(View view) {

		/**
		 * Called when the "Reset" button is pressed
		 */

		FragmentTabAdvanced fragmentAdvanced =
				(FragmentTabAdvanced) getSupportFragmentManager().findFragmentByTag( getTabFragmentAdvanced() );

		if (fragmentAdvanced != null)
			fragmentAdvanced.resetControlSignal();


	} // resetControlSignal


	// ################################################################

	public void setControlSignalHover(View view) {

		/**
		 * Called when the "Hover" button is pressed
		 */

		FragmentTabAdvanced fragmentAdvanced =
				(FragmentTabAdvanced) getSupportFragmentManager().findFragmentByTag( getTabFragmentAdvanced() );

		if (fragmentAdvanced != null)
			fragmentAdvanced.setControlSignalHover();


	} // setControlSignalHover


	// ################################################################

	public void setControlSignalForward(View view) {

		/**
		 * Called when the "Forward" button is pressed
		 */

		FragmentTabAdvanced fragmentAdvanced =
				(FragmentTabAdvanced) getSupportFragmentManager().findFragmentByTag( getTabFragmentAdvanced() );

		if (fragmentAdvanced != null)
			fragmentAdvanced.setControlSignalForward();


	} // setControlSignalForward


	// ################################################################

	public void setControlSignalLeft(View view) {

		/**
		 * Called when the "Left" button is pressed
		 */

		FragmentTabAdvanced fragmentAdvanced =
				(FragmentTabAdvanced) getSupportFragmentManager().findFragmentByTag( getTabFragmentAdvanced() );

		if (fragmentAdvanced != null)
			fragmentAdvanced.setControlSignalLeft();


	} // setControlSignalLeft


	// ################################################################

	public void setControlSignalRight(View view) {

		/**
		 * Called when the "Right" button is pressed
		 */

		FragmentTabAdvanced fragmentAdvanced =
				(FragmentTabAdvanced) getSupportFragmentManager().findFragmentByTag( getTabFragmentAdvanced() );

		if (fragmentAdvanced != null)
			fragmentAdvanced.setControlSignalRight();


	} // setControlSignalRight


	// ################################################################

	public void sendMessage(View view) {

		Log.v(TAG, "sendMessage()");
		Toast.makeText(this, "Thank you for your feedback!", Toast.LENGTH_LONG).show();


		FragmentTabSupport fragmentSupport =
				(FragmentTabSupport) getSupportFragmentManager().findFragmentByTag( getTabFragmentSupport() );

		String name = fragmentSupport.editTextName.getText().toString();
		String email = fragmentSupport.editTextEmail.getText().toString();
		String message = fragmentSupport.editTextMessage.getText().toString();

		String contactURL = FragmentTabSupport.contactURL;


		message = message + "\n\n" + getDeviceDetails();


		EmailMessage emailMessage = new EmailMessage();

		emailMessage.setData(contactURL, name, email, message);

		emailMessage.execute();


		fragmentSupport.editTextName.setText("");
		fragmentSupport.editTextEmail.setText("");
		fragmentSupport.editTextMessage.setText("");


	} // sendMessage


	// ################################################################

	@SuppressLint("InlinedApi")
	public String getDeviceDetails() {

		String output = "";

		output = "Manufacturer: " + Build.MANUFACTURER + "\n";
		output = output + "Model: " + Build.MODEL + "\n";
		output = output + "Product: " + Build.PRODUCT + "\n";
		if (android.os.Build.VERSION.SDK_INT >= 8)
			output = output + "Hardware: " + Build.HARDWARE + "\n";
		output = output + "Device: " + Build.DEVICE + "\n";
		output = output + "Android Version: " + android.os.Build.VERSION.SDK_INT + "\n";


		return (output);


	} // getDeviceDetails


	// ################################################################
	// ################################################################

	class EmailMessage extends AsyncTask<String, Void, Object> {

		String contactURL = new String();
		String name = new String();
		String email = new String();
		String message = new String();

		public void setData(String contact, String full_name, String email_address, String content) {
			contactURL = contact;
			name = full_name;
			email = email_address;
			message = content;
		}

		protected Object doInBackground(String... vars) {
			try {

				// Create a new HttpClient and Post Header
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(contactURL);

				try {
					// Add your data
					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
					nameValuePairs.add(new BasicNameValuePair("email_name", name));
					nameValuePairs.add(new BasicNameValuePair("email_from", email));
					nameValuePairs.add(new BasicNameValuePair("email_subject", "[Orbit Support] (Android " + versionName + ")"));
					nameValuePairs.add(new BasicNameValuePair("email_body", message));
					httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

					// Execute HTTP Post Request
					@SuppressWarnings("unused")
					HttpResponse response = httpclient.execute(httppost);

				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;

		}

	} // emailMessage


} // OrbitTabActivity
