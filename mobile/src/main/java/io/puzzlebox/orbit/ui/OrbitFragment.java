/**
 * Puzzlebox Jigsaw
 * Copyright 2015 Puzzlebox Productions, LLC
 * License: GNU Affero General Public License Version 3
 */

package io.puzzlebox.orbit.ui;

import android.annotation.TargetApi;
import android.app.Activity;
//import android.app.Fragment;
import android.support.v4.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import io.puzzlebox.jigsaw.data.DevicePuzzleboxOrbitSingleton;
import io.puzzlebox.jigsaw.service.InteraXonMuseService;
import io.puzzlebox.jigsaw.service.NeuroSkyThinkGearService;
import io.puzzlebox.orbit.R;
import io.puzzlebox.jigsaw.data.SessionSingleton;

import static android.view.MenuItem.SHOW_AS_ACTION_ALWAYS;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class OrbitFragment extends Fragment
		  implements SeekBar.OnSeekBarChangeListener {

	private final static String TAG = OrbitFragment.class.getSimpleName();

	private OnFragmentInteractionListener mListener;

	/**
	 * Configuration
	 */
	int eegPower = 0;

//	public boolean generateAudio = true;
//	//	public boolean generateAudio = false;
//	public boolean invertControlSignal = false;
//	boolean tiltSensorControl = false;
//	public int deviceWarningMessagesDisplayed = 0;

//	int minimumScoreTarget = 40;
//	int scoreCurrent = 0;
//	int scoreLast = 0;
//	int scoreHigh = 0;

//	boolean demoFlightMode = false;


	/**
	 * UI
	 */
	Configuration config;
	ProgressBar progressBarAttention;
	SeekBar seekBarAttention;
	ProgressBar progressBarMeditation;
	SeekBar seekBarMeditation;
	ProgressBar progressBarSignal;
	ProgressBar progressBarPower;

	TextView textViewLabelScores;
	TextView textViewLabelScore;
	TextView textViewLabelLastScore;
	TextView textViewLabelHighScore;
	View viewSpaceScore;
	View viewSpaceScoreLast;
	View viewSpaceScoreHigh;
	TextView textViewScore;
	TextView textViewLastScore;
	TextView textViewHighScore;

	ImageView imageViewStatus;

	Button buttonConnectOrbit;

//	private static TextView textViewSessionTime;

	int[] thresholdValuesAttention = new int[101];
	int[] thresholdValuesMeditation = new int[101];
	int minimumPower = 0; // minimum power for the bloom
	int maximumPower = 100; // maximum power for the bloom


//	/**
//	 * Audio
//	 *
//	 * By default the flight control command is hard-coded into WAV files
//	 * When "Generate Control Signal" is enabled the tones used to communicate
//	 * with the infrared dongle are generated on-the-fly.
//	 */
//	int audioFile = R.raw.throttle_hover_android_common;
//	//	int audioFile = R.raw.throttle_hover_android_htc_one_x;
//
//	private SoundPool soundPool;
//	private int soundID;
//	boolean loaded = false;


	/**
	 * PuzzleboxOrbitAudioIRHandler
	 */
//	PuzzleboxOrbitAudioIRHandler puzzleboxOrbitAudioIRHandler = new PuzzleboxOrbitAudioIRHandler();


	/**
	 * Flight status
	 */
//	boolean flightActive = false;


	// ################################################################

	public static OrbitFragment newInstance() {
		OrbitFragment fragment = new OrbitFragment();
		Bundle args = new Bundle();
		fragment.setArguments(args);
		return fragment;
	}


	// ################################################################

	public OrbitFragment() {
		// Required empty public constructor
	}


	// ################################################################

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}


	// ################################################################

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {

		// Inflate the layout for this fragment
		View v = inflater.inflate(R.layout.fragment_orbit, container, false);

//		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
//		setContentView(R.layout.main);
//		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title);


		buttonConnectOrbit = (Button) v.findViewById(R.id.buttonConnectOrbit);
		buttonConnectOrbit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setOrbitActivate();
			}
		});


		Button buttonTestFlight = (Button) v.findViewById(R.id.buttonTestFlight);
		buttonTestFlight.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				demoMode(v);
			}
		});


		progressBarAttention = (ProgressBar) v.findViewById(R.id.progressBarAttention);
		final float[] roundedCorners = new float[] { 5, 5, 5, 5, 5, 5, 5, 5 };
		ShapeDrawable progressBarAttentionDrawable = new ShapeDrawable(new RoundRectShape(roundedCorners, null,null));
		String progressBarAttentionColor = "#FF0000";
		progressBarAttentionDrawable.getPaint().setColor(Color.parseColor(progressBarAttentionColor));
		ClipDrawable progressAttention = new ClipDrawable(progressBarAttentionDrawable, Gravity.LEFT, ClipDrawable.HORIZONTAL);
		progressBarAttention.setProgressDrawable(progressAttention);
		progressBarAttention.setBackgroundDrawable(getResources().getDrawable(android.R.drawable.progress_horizontal));

		progressBarMeditation = (ProgressBar) v.findViewById(R.id.progressBarMeditation);
		ShapeDrawable progressBarMeditationDrawable = new ShapeDrawable(new RoundRectShape(roundedCorners, null,null));
		String progressBarMeditationColor = "#0000FF";
		progressBarMeditationDrawable.getPaint().setColor(Color.parseColor(progressBarMeditationColor));
		ClipDrawable progressMeditation = new ClipDrawable(progressBarMeditationDrawable, Gravity.LEFT, ClipDrawable.HORIZONTAL);
		progressBarMeditation.setProgressDrawable(progressMeditation);
		progressBarMeditation.setBackgroundDrawable(getResources().getDrawable(android.R.drawable.progress_horizontal));

		progressBarSignal = (ProgressBar) v.findViewById(R.id.progressBarSignal);
		ShapeDrawable progressBarSignalDrawable = new ShapeDrawable(new RoundRectShape(roundedCorners, null,null));
		String progressBarSignalColor = "#00FF00";
		progressBarSignalDrawable.getPaint().setColor(Color.parseColor(progressBarSignalColor));
		ClipDrawable progressSignal = new ClipDrawable(progressBarSignalDrawable, Gravity.LEFT, ClipDrawable.HORIZONTAL);
		progressBarSignal.setProgressDrawable(progressSignal);
		progressBarSignal.setBackgroundDrawable(getResources().getDrawable(android.R.drawable.progress_horizontal));
		//		progressBarSignal.setProgress(tgSignal);

		progressBarPower = (ProgressBar) v.findViewById(R.id.progressBarPower);
		ShapeDrawable progressBarPowerDrawable = new ShapeDrawable(new RoundRectShape(roundedCorners, null,null));
		String progressBarPowerColor = "#FFFF00";
		progressBarPowerDrawable.getPaint().setColor(Color.parseColor(progressBarPowerColor));
		ClipDrawable progressPower = new ClipDrawable(progressBarPowerDrawable, Gravity.LEFT, ClipDrawable.HORIZONTAL);
		progressBarPower.setProgressDrawable(progressPower);
		progressBarPower.setBackgroundDrawable(getResources().getDrawable(android.R.drawable.progress_horizontal));


		seekBarAttention = (SeekBar) v.findViewById(R.id.seekBarAttention);
		seekBarAttention.setOnSeekBarChangeListener(this);
		seekBarMeditation = (SeekBar) v.findViewById(R.id.seekBarMeditation);
		seekBarMeditation.setOnSeekBarChangeListener(this);


		imageViewStatus = (ImageView) v.findViewById(R.id.imageViewStatus);


		textViewLabelScores = (TextView) v.findViewById(R.id.textViewLabelScores);
		textViewLabelScore = (TextView) v.findViewById(R.id.textViewLabelScore);
		textViewLabelLastScore = (TextView) v.findViewById(R.id.textViewLabelLastScore);
		textViewLabelHighScore = (TextView) v.findViewById(R.id.textViewLabelHighScore);

		viewSpaceScore = (View) v.findViewById(R.id.viewSpaceScore);
		viewSpaceScoreLast = (View) v.findViewById(R.id.viewSpaceScoreLast);
		viewSpaceScoreHigh = (View) v.findViewById(R.id.viewSpaceScoreHigh);

		textViewScore = (TextView) v.findViewById(R.id.textViewScore);
		textViewLastScore = (TextView) v.findViewById(R.id.textViewLastScore);
		textViewHighScore = (TextView) v.findViewById(R.id.textViewHighScore);


		// Hide the "Scores" label by default
		textViewLabelScores.setVisibility(View.GONE);
		viewSpaceScore.setVisibility(View.GONE);



		/**
		 * PuzzleboxOrbitAudioIRHandler
		 */

		if (!DevicePuzzleboxOrbitSingleton.getInstance().puzzleboxOrbitAudioIRHandler.isAlive()) {


			/**
			 * Prepare audio stream
			 */

			maximizeAudioVolume(); // Automatically set media volume to maximum

			/** Set the hardware buttons to control the audio output */
			getActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);

			/** Preload the flight control WAV file into memory */
			DevicePuzzleboxOrbitSingleton.getInstance().soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
			DevicePuzzleboxOrbitSingleton.getInstance().soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
				public void onLoadComplete(SoundPool soundPool,
				                           int sampleId,
				                           int status) {
					DevicePuzzleboxOrbitSingleton.getInstance().loaded = true;
				}
			});
			DevicePuzzleboxOrbitSingleton.getInstance().soundID = DevicePuzzleboxOrbitSingleton.getInstance().soundPool.load(getActivity().getApplicationContext(), DevicePuzzleboxOrbitSingleton.getInstance().audioFile, 1);


			DevicePuzzleboxOrbitSingleton.getInstance().puzzleboxOrbitAudioIRHandler.start();


		}


		if (DevicePuzzleboxOrbitSingleton.getInstance().flightActive)
			buttonTestFlight.setText(getResources().getString(R.string.button_stop_test));

		/**
		 * Update settings according to default UI
		 */

		updateScreenLayout();

		updatePowerThresholds();
//		updatePower();


		return v;

	}


	// ################################################################

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnFragmentInteractionListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					  + " must implement OnFragmentInteractionListener");
		}
	}


	// ################################################################

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}


	// ################################################################

	/**
	 * This interface must be implemented by activities that contain this
	 * fragment to allow an interaction in this fragment to be communicated
	 * to the activity and potentially other fragments contained in that
	 * activity.
	 * <p/>
	 * See the Android Training lesson <a href=
	 * "http://developer.android.com/training/basics/fragments/communicating.html"
	 * >Communicating with Other Fragments</a> for more information.
	 */
	public interface OnFragmentInteractionListener {
		void onFragmentInteraction(Uri uri);
	}


	// ################################################################

	@Override
	public void onResume() {

		super.onResume();


		updatePowerThresholds();
		updatePower();


//		if (!DevicePuzzleboxOrbitSingleton.getInstance().mBluetoothAdapter.isEnabled()) {
//			Intent enableBtIntent = new Intent(
//					BluetoothAdapter.ACTION_REQUEST_ENABLE);
//			startActivityForResult(enableBtIntent, DevicePuzzleboxOrbitSingleton.getInstance().REQUEST_ENABLE_BT);
//		}

//		getActivity().registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
//
//		Intent gattServiceIntent = new Intent(getActivity(),
//				RedBearLabsService.class);
//		getActivity().bindService(gattServiceIntent, mServiceConnection, getActivity().BIND_AUTO_CREATE);

//		if (DevicePuzzleboxOrbitSingleton.getInstance().connState)
//			setButtonText(R.id.connectBloom, "Disconnect Bloom");

//		updateSessionTime();

		LocalBroadcastManager.getInstance(getActivity().getApplicationContext()).registerReceiver(
				  mPacketReceiver, new IntentFilter("io.puzzlebox.jigsaw.protocol.thinkgear.packet"));


	}


	// ################################################################

	public void onPause() {

		Log.v(TAG, "onPause()");

		super.onPause();


		LocalBroadcastManager.getInstance(
				  getActivity().getApplicationContext()).unregisterReceiver(
				  mPacketReceiver);

//		getActivity().unregisterReceiver(mGattUpdateReceiver);
//
//		getActivity().unbindService(mServiceConnection);


	} // onPause


	// ################################################################

//	@Override
//	public void onDestroy() {
//		super.onDestroy();
//
//		try {
//			if (mServiceConnection != null)
//				getActivity().unbindService(mServiceConnection);
//		} catch (IllegalArgumentException e) {
//			Log.w(TAG, "Exception in onDestroy(): " + e);
//		}
//
//	}


	// ################################################################

//	@Override
//	public void onStop() {
//		super.onStop();
//
//		DevicePuzzleboxOrbitSingleton.getInstance().flag = false;
//
//		getActivity().unregisterReceiver(mGattUpdateReceiver);
//
//	}


	// ################################################################

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

		menu.add("Share")
				  .setOnMenuItemClickListener(this.mShareButtonClickListener)
				  .setIcon(android.R.drawable.ic_menu_share)
				  .setShowAsAction(SHOW_AS_ACTION_ALWAYS);

		super.onCreateOptionsMenu(menu, inflater);

	}


	// ################################################################

	MenuItem.OnMenuItemClickListener mShareButtonClickListener = new MenuItem.OnMenuItemClickListener() {

		@Override
		public boolean onMenuItemClick(MenuItem item) {

//			Intent i = SessionSingleton.getInstance().getExportSessionIntent(getActivity().getApplicationContext(), item);
			Intent i = SessionSingleton.getInstance().getExportSessionIntent(getActivity().getApplicationContext());

			if (i != null) {
				startActivity(i);
			} else {
				Toast.makeText(getActivity().getApplicationContext(), "Error export session data for sharing", Toast.LENGTH_SHORT).show();
			}

			return false;
		}
	};


	// ################################################################

//	private void resetSession() {
//
//		Log.d(TAG, "SessionSingleton.getInstance().resetSession()");
//		SessionSingleton.getInstance().resetSession();
//
//		textViewSessionTime.setText( R.string.session_time );
//
//		Toast.makeText((getActivity().getApplicationContext()),
//				  "Session data reset",
//				  Toast.LENGTH_SHORT).show();
//
//	}


	// ################################################################

	private BroadcastReceiver mPacketReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

			int eegAttention = Integer.valueOf(intent.getStringExtra("Attention"));
			int eegMeditation = Integer.valueOf(intent.getStringExtra("Meditation"));
			int eegSignal = Integer.valueOf(intent.getStringExtra("Signal Level"));

//			Log.e(TAG, "eegAttention: " + eegAttention);

			progressBarAttention.setProgress(eegAttention);
			progressBarMeditation.setProgress(eegMeditation);
			progressBarSignal.setProgress(eegSignal);

			updatePower();


//			updateSessionTime();




		}

	};


	// ################################################################

	private void setOrbitActivate() {

		Log.d(TAG, "setOrbitActivate");

		if (! DevicePuzzleboxOrbitSingleton.getInstance().orbitActive) {
//			getActivity().startService(intentThinkGear);
			DevicePuzzleboxOrbitSingleton.getInstance().orbitActive = true;
		} else {
			setOrbitDeactivate();
		}

//		DevicePuzzleboxOrbitSingleton.getInstance().flag = true;
//		DevicePuzzleboxOrbitSingleton.getInstance().connState = true;
//
//		servoSeekBar.setEnabled(DevicePuzzleboxOrbitSingleton.getInstance().flag);
//		connectBloom.setText("Disconnect Bloom");
//
//		buttonDemo.setEnabled(true);
		buttonConnectOrbit.setEnabled(true);
	}


	// ################################################################

	private void setOrbitDeactivate() {

		Log.d(TAG, "setOrbitDeactivate");

		DevicePuzzleboxOrbitSingleton.getInstance().orbitActive = false;

//		DevicePuzzleboxOrbitSingleton.getInstance().flag = false;
//		DevicePuzzleboxOrbitSingleton.getInstance().connState = false;
//
//		servoSeekBar.setEnabled(DevicePuzzleboxOrbitSingleton.getInstance().flag);
//		connectBloom.setText("Connect Bloom");
//
//		buttonDemo.setEnabled(false);

		buttonConnectOrbit.setEnabled(false);
//
//		progressBarRange.setProgress(0);
	}


	// ################################################################

	public void updateScreenLayout() {

//		switch(config.screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK){
//			case Configuration.SCREENLAYOUT_SIZE_SMALL:
//				Log.v(TAG, "screenLayout: small");
//				updateScreenLayoutSmall();
//				break;
//			case Configuration.SCREENLAYOUT_SIZE_NORMAL:
//				Log.v(TAG, "screenLayout: normal");
//				updateScreenLayoutSmall();
//				break;
//			case Configuration.SCREENLAYOUT_SIZE_LARGE:
//				Log.v(TAG, "screenLayout: large");
//				break;
//			case Configuration.SCREENLAYOUT_SIZE_XLARGE:
//				Log.v(TAG, "screenLayout: xlarge");
//				break;
//			case Configuration.SCREENLAYOUT_SIZE_UNDEFINED:
//				Log.v(TAG, "screenLayout: undefined");
//				updateScreenLayoutSmall();
//				break;
//		}

	} // updateScreenLayout


	// ################################################################

	public void updateScreenLayoutSmall() {

//		String button_test_fly_small = getResources().getString(R.string.button_test_fly_small);
//		setButtonText(R.id.buttonTestFly, button_test_fly_small);

		textViewLabelScores.setVisibility(View.VISIBLE);
		viewSpaceScore.setVisibility(View.VISIBLE);


		android.view.ViewGroup.LayoutParams layoutParams;

		layoutParams = viewSpaceScoreLast.getLayoutParams();
		layoutParams.width = 10;
		viewSpaceScoreLast.setLayoutParams(layoutParams);

		layoutParams = viewSpaceScoreHigh.getLayoutParams();
		layoutParams.width = 10;
		viewSpaceScoreHigh.setLayoutParams(layoutParams);


		String labelScore = getResources().getString(R.string.textview_label_score_small);
		textViewLabelScore.setText(labelScore);

		String labelLastScore = getResources().getString(R.string.textview_label_last_score_small);
		textViewLabelLastScore.setText(labelLastScore);

		String labelHighScore = getResources().getString(R.string.textview_label_high_score_small);
		textViewLabelHighScore.setText(labelHighScore);


//		// HTC Droid DNA - AndroidPlot has issues with OpenGL Render
//		if ((Build.MANUFACTURER.contains("HTC")) &&
//				  (Build.MODEL.contains("HTC6435LVW"))) {
//
//			Log.v(TAG, "Device detected: HTC Droid DNA");
//			hideEEGRawHistory();
//
//		}


	} // updateScreenLayoutSmall


	// ################################################################

//	public void setButtonText(int buttonId, String text) {
//
//		/**
//		 * Shortcut for changing the text on a button
//		 */
//
//		Button button = (Button) v.findViewById(buttonId);
//		button.setText(text);
//
//	} // setButtonText


	// ################################################################

	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {

		updatePowerThresholds();
		//		updatePower();

	} // onProgressChanged


	// ################################################################

	public void onStartTrackingTouch(SeekBar seekBar) {

		/**
		 * Method required by SeekBar.OnSeekBarChangeListener
		 */


	} // onStartTrackingTouch


	// ################################################################

	public void onStopTrackingTouch(SeekBar seekBar) {

		Log.v(TAG, "onStopTrackingTouch()");


	} // onStopTrackingTouch


	// ################################################################

	public void updatePowerThresholds() {

		/**
		 * The "Power" level refers to the Puzzlebox Orbit helicopter's
		 * throttle setting. Typically this is an "off" or "on" state,
		 * meaning the helicopter is either flying or not flying at all.
		 * However this method could be used to increase the throttle
		 * or perhaps the forward motion of the helicopter to a level
		 * proportionate to how far past their target brainwave levels
		 * are set (via the progress bar sliders).
		 */

		int power;
		int attentionSeekValue;
		int meditationSeekValue;
		float percentOfMaxPower;

		// Reset all values to zero
		for (int i = 0; i < thresholdValuesAttention.length; i++) {
			thresholdValuesAttention[i] = 0;
			thresholdValuesMeditation[i] = 0;
		}

		attentionSeekValue = seekBarAttention.getProgress();
		if (attentionSeekValue > 0) {
			for (int i = attentionSeekValue; i < thresholdValuesAttention.length; i++) {

				/**
				 *  Slider @ 70
				 *
				 * Attention @ 70
				 * Percentage = 0% ((100-70) - (100-70)) / (100-70)
				 * Power = 60 (minimumPower)
				 *
				 * Slider @ 70
				 * Attention @ 80
				 * Percentage = 33% ((100-70) - (100-80)) / (100-70)
				 * Power = 73
				 *
				 * Slider @ 70
				 * Attention @ 90
				 * Percentage = 66% ((100-70) - (100-90)) / (100-70)
				 * Power = 86
				 *
				 * Slider @ 70
				 * Attention @ 100
				 * Percentage = 100% ((100-70) - (100-100)) / (100-70)
				 * Power = 100
				 */

				percentOfMaxPower = ( ((100 - attentionSeekValue) - (100 - i)) / (float)(100 - attentionSeekValue) );
				power = thresholdValuesAttention[i] + (int)( minimumPower + ((maximumPower - minimumPower) * percentOfMaxPower) );
				thresholdValuesAttention[i] = power;

			}
		}

		meditationSeekValue = seekBarMeditation.getProgress();
		if (meditationSeekValue > 0) {
			for (int i = meditationSeekValue; i < thresholdValuesMeditation.length; i++) {
				percentOfMaxPower = ( ((100 - meditationSeekValue) - (100 - i)) / (float)(100 - meditationSeekValue) );
				power = thresholdValuesMeditation[i] + (int)( minimumPower + ((maximumPower - minimumPower) * percentOfMaxPower) );
				thresholdValuesMeditation[i] = power;
			}
		}

	} // updatePowerThresholds


	// ################################################################

	public void updatePower() {

		/**
		 * This method updates the power level of the
		 * "Throttle" and triggers the audio stream
		 * which is used to fly the helicopter
		 */

		if (NeuroSkyThinkGearService.eegConnected) {

			if (NeuroSkyThinkGearService.eegSignal < 100) {
				NeuroSkyThinkGearService.eegAttention = 0;
				NeuroSkyThinkGearService.eegMeditation = 0;
				progressBarAttention.setProgress(NeuroSkyThinkGearService.eegAttention);
				progressBarMeditation.setProgress(NeuroSkyThinkGearService.eegMeditation);
			}

			NeuroSkyThinkGearService.eegPower = calculateSpeed();
			eegPower = NeuroSkyThinkGearService.eegPower;

			progressBarPower.setProgress(NeuroSkyThinkGearService.eegPower);


		}

		if (InteraXonMuseService.eegConnected) {

//			Log.d(TAG, "InteraXonMuseService.eegConnected: eegSignal: " + InteraXonMuseService.eegSignal);
//			if (InteraXonMuseService.eegSignal < 100) {
//				InteraXonMuseService.eegConcentration = 0;
//				InteraXonMuseService.eegMellow = 0;
//				progressBarAttention.setProgress(InteraXonMuseService.eegConcentration);
//				progressBarMeditation.setProgress(InteraXonMuseService.eegMellow);
//			}

			InteraXonMuseService.eegPower = calculateSpeed();

			progressBarPower.setProgress(InteraXonMuseService.eegPower);
			eegPower = InteraXonMuseService.eegPower;


		}


		DevicePuzzleboxOrbitSingleton.getInstance().eegPower = eegPower;


		if (eegPower > 0) {

			/** Start playback of audio control stream */
			if (!DevicePuzzleboxOrbitSingleton.getInstance().flightActive) {
				playControl();
			}

			updateScore();

			DevicePuzzleboxOrbitSingleton.getInstance().flightActive = true;

		} else {

			/** Land the helicopter */
			if (! DevicePuzzleboxOrbitSingleton.getInstance().demoActive )
				stopControl();

			resetCurrentScore();

		}

		Log.d(TAG, "flightActive: " + DevicePuzzleboxOrbitSingleton.getInstance().flightActive);



	} // updatePower


	// ################################################################

	public int calculateSpeed() {

		/**
		 * This method is used for calculating whether
		 * or not the "Attention" or "Meditation" levels
		 * are sufficient to trigger the helicopter throttle
		 */

		int attention = progressBarAttention.getProgress();
		int meditation = progressBarMeditation.getProgress();
		int attentionSeekValue = seekBarAttention.getProgress();
		int meditationSeekValue = seekBarMeditation.getProgress();

		int speed = 0;

		if (attention > attentionSeekValue)
			speed = thresholdValuesAttention[attention];
		if (meditation > meditationSeekValue)
			speed = speed + thresholdValuesMeditation[meditation];

		if (speed > maximumPower)
			speed = maximumPower;
		if (speed < minimumPower)
			speed = 0;


		return(speed);


	} // calculateSpeed


//	// ################################################################
//
//	public void updateEEGRawHistory(Number[] rawEEG) {
//
//		if (eegRawHistoryPlot != null) {
//			eegRawHistoryPlot.removeSeries(eegRawHistorySeries);
//
//			eegRawHistorySeries = new SimpleXYSeries(Arrays.asList(rawEEG), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Raw EEG");
//
//			//		LineAndPointFormatter format = new LineAndPointFormatter(Color.rgb(200, 100, 100), Color.BLACK, null, null);
//			//		LineAndPointFormatter format = new LineAndPointFormatter(Color.rgb(200, 100, 100), Color.TRANSPARENT, null, null);
//			LineAndPointFormatter format = new LineAndPointFormatter(Color.rgb(0, 0, 0), Color.TRANSPARENT, null, null);
//
//			//		format.getFillPaint().setAlpha(220);
//
//			eegRawHistoryPlot.addSeries(eegRawHistorySeries, format);
//
//
//			// redraw the Plots:
//			eegRawHistoryPlot.redraw();
//
//			rawEEG = new Number[512];
//			arrayIndex = 0;
//		}
//
//	} // updateEEGRawHistory


	// ################################################################

	public void updateScore() {

		/**
		 * Score points based on target slider levels
		 * If you pass your goal with either Attention or Meditation
		 * the higher target of the two will counts as points per second.
		 *
		 * Minimum threshold for points is set as "minimumScoreTarget"
		 *
		 * For example, assume minimumScoreTarget is 40%.
		 * If your target Attention is 60% and you go past to reach 80%
		 * you will receive 20 points per second (60-40). If your
		 * target is 80% and you reach 80% you will receive 40
		 * points per second (80-40).
		 *
		 * You can set both Attention and Meditation targets at the
		 * same time. Reaching either will fly the helicopter but you
		 * will only receive points for the higher-scoring target of
		 * the two.
		 *
		 */

		int eegAttentionScore = 0;
		int eegAttention = progressBarAttention.getProgress();
		int eegAttentionTarget = seekBarAttention.getProgress();

		int eegMeditationScore = 0;
		int eegMeditation = progressBarMeditation.getProgress();
		int eegMeditationTarget = seekBarMeditation.getProgress();

		if ((eegAttention >= eegAttentionTarget) &&
				  (eegAttentionTarget > DevicePuzzleboxOrbitSingleton.getInstance().minimumScoreTarget))
			eegAttentionScore = eegAttentionTarget - DevicePuzzleboxOrbitSingleton.getInstance().minimumScoreTarget;

		if ((eegMeditation >= eegMeditationTarget) &&
				  (eegMeditationTarget > DevicePuzzleboxOrbitSingleton.getInstance().minimumScoreTarget))
			eegMeditationScore = eegMeditationTarget - DevicePuzzleboxOrbitSingleton.getInstance().minimumScoreTarget;

		if (eegAttentionScore > eegMeditationScore)
			DevicePuzzleboxOrbitSingleton.getInstance().scoreCurrent = DevicePuzzleboxOrbitSingleton.getInstance().scoreCurrent + eegAttentionScore;
		else
			DevicePuzzleboxOrbitSingleton.getInstance().scoreCurrent = DevicePuzzleboxOrbitSingleton.getInstance().scoreCurrent + eegMeditationScore;

		textViewScore.setText(Integer.toString(DevicePuzzleboxOrbitSingleton.getInstance().scoreCurrent));

		if (DevicePuzzleboxOrbitSingleton.getInstance().scoreCurrent > DevicePuzzleboxOrbitSingleton.getInstance().scoreHigh) {
			DevicePuzzleboxOrbitSingleton.getInstance().scoreHigh = DevicePuzzleboxOrbitSingleton.getInstance().scoreCurrent;
			textViewHighScore.setText(Integer.toString(DevicePuzzleboxOrbitSingleton.getInstance().scoreHigh));
		}


		// Catch anyone gaming the system with one slider
		// below the minimum threshold and the other over.
		// For example, setting Meditation to 1% will keep helicopter
		// activated even if Attention is below target
		if ((eegAttention < eegAttentionTarget) && (eegMeditation < DevicePuzzleboxOrbitSingleton.getInstance().minimumScoreTarget))
			resetCurrentScore();
		if ((eegMeditation < eegMeditationTarget) && (eegAttention < DevicePuzzleboxOrbitSingleton.getInstance().minimumScoreTarget))
			resetCurrentScore();
		if ((eegAttention < DevicePuzzleboxOrbitSingleton.getInstance().minimumScoreTarget) && (eegMeditation < DevicePuzzleboxOrbitSingleton.getInstance().minimumScoreTarget))
			resetCurrentScore();


	} // updateScore


	// ################################################################

	public void resetCurrentScore() {

		if (DevicePuzzleboxOrbitSingleton.getInstance().scoreCurrent > 0)
			textViewLastScore.setText(Integer.toString(DevicePuzzleboxOrbitSingleton.getInstance().scoreCurrent));
		DevicePuzzleboxOrbitSingleton.getInstance().scoreCurrent = 0;
		textViewScore.setText(Integer.toString(DevicePuzzleboxOrbitSingleton.getInstance().scoreCurrent));

	} // resetCurrentScore


	// ################################################################

	public void updateStatusImage() {

//		if(DEBUG) {
//			Log.v(TAG, (new StringBuilder("Attention: ")).append(eegAttention).toString());
//			Log.v(TAG, (new StringBuilder("Meditation: ")).append(eegMeditation).toString());
//			Log.v(TAG, (new StringBuilder("Power: ")).append(eegPower).toString());
//			Log.v(TAG, (new StringBuilder("Signal: ")).append(eegSignal).toString());
//			Log.v(TAG, (new StringBuilder("Connecting: ")).append(eegConnecting).toString());
//			Log.v(TAG, (new StringBuilder("Connected: ")).append(eegConnected).toString());
//		}


//		if(eegConnected) {
//			imageViewStatus.setImageResource(R.raw.status_2_connected);
//			return;
//		}
//
//		if(eegConnecting) {
//			imageViewStatus.setImageResource(R.raw.status_1_connecting);
//			return;
//		} else {
//			imageViewStatus.setImageResource(R.raw.status_default);
//			return;
//		}		if(eegPower > 0) {
//			imageViewStatus.setImageResource(R.raw.status_4_active);
//			return;
//		}
//
//		if(eegSignal > 90) {
//			imageViewStatus.setImageResource(R.raw.status_3_processing);
//			return;
//		}


	} // updateStatusImage


	// ################################################################

	public void maximizeAudioVolume() {

		AudioManager audio = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
		int currentVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);

		if (currentVolume < audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC)) {

			Log.v(TAG, "Previous volume:" + currentVolume);

			Toast.makeText(getActivity().getApplicationContext(), "Automatically setting volume to maximum", Toast.LENGTH_SHORT).show();

			AudioManager audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
			audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
					  audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
					  AudioManager.FLAG_SHOW_UI);

		}


	} // maximizeAudioVolume


	// ################################################################

	public void playControl() {

		Log.d(TAG, "playControl()");


		// TODO Convert to service

//		FragmentTabAdvanced fragmentAdvanced =
//				  (FragmentTabAdvanced) getActivity().getSupportFragmentManager().findFragmentByTag( getTabFragmentAdvanced() );
//
		if (DevicePuzzleboxOrbitSingleton.getInstance().generateAudio) {

			/**
			 * Generate signal on the fly
			 */

//			// Handle controlled descent thread if activated
//			if ((fragmentAdvanced.orbitControlledDescentTask != null) &&
//					  (fragmentAdvanced.orbitControlledDescentTask.keepDescending)) {
//				fragmentAdvanced.orbitControlledDescentTask.callStopAudio = false;
//				fragmentAdvanced.orbitControlledDescentTask.keepDescending = false;
//			}


			//			if (puzzleboxOrbitAudioIRHandler != null) {

			//				serviceBinder.ifFlip = fragmentAdvanced.checkBoxInvertControlSignal.isChecked(); // if checked then flip
			DevicePuzzleboxOrbitSingleton.getInstance().puzzleboxOrbitAudioIRHandler.ifFlip = DevicePuzzleboxOrbitSingleton.getInstance().invertControlSignal; // if checked then flip

			int channel = 0; // default "A"

//			if (fragmentAdvanced != null)
//				channel = fragmentAdvanced.radioGroupChannel.getCheckedRadioButtonId();

			//				if (demoFlightMode)
			//					updateAudioHandlerLoopNumberWhileMindControl(200);
			//				else
			//					updateAudioHandlerLoopNumberWhileMindControl(4500);
			//
			//			updateAudioHandlerLoopNumberWhileMindControl(5000);

			updateAudioHandlerLoopNumberWhileMindControl(-1); // Loop infinite for easier user testing

			updateAudioHandlerChannel(channel);

			DevicePuzzleboxOrbitSingleton.getInstance().puzzleboxOrbitAudioIRHandler.mutexNotify();

			//			}
//
//
		} else {

			/**
			 * Play audio control file
			 */

			/** Getting the user sound settings */
			AudioManager audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
			//			float actualVolume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
			float maxVolume = (float) audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
			//			float volume = actualVolume / maxVolume;

			audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (int) maxVolume, 0);
			/** Is the sound loaded already? */
			if (DevicePuzzleboxOrbitSingleton.getInstance().loaded) {
				//				soundPool.play(soundID, volume, volume, 1, 0, 1f);
				//				soundPool.setVolume(soundID, 1f, 1f);
				//				soundPool.play(soundID, maxVolume, maxVolume, 1, 0, 1f); // Fixes Samsung Galaxy S4 [SGH-M919]

				DevicePuzzleboxOrbitSingleton.getInstance().soundPool.play(DevicePuzzleboxOrbitSingleton.getInstance().soundID, 1f, 1f, 1, 0, 1f); // Fixes Samsung Galaxy S4 [SGH-M919]

				// TODO No visible effects of changing these variables on digital oscilloscope
				//				soundPool.play(soundID, 0.5f, 0.5f, 1, 0, 0.5f);
//				if (DEBUG)
				Log.v(TAG, "Played sound");
			}

		}

	} // playControl


	// ################################################################

	public void stopControl() {

		Log.d(TAG, "stopControl()");

		// TODO Convert to service

//		FragmentTabAdvanced fragmentAdvanced =
//				  (FragmentTabAdvanced) getSupportFragmentManager().findFragmentByTag( getTabFragmentAdvanced() );
//
//
//		// Initial Controlled Descent if activated by user
//		if ((generateAudio) &&
//				  (flightActive) &&
//				  (fragmentAdvanced != null) &&
//				  (fragmentAdvanced.checkBoxControlledDescent.isChecked()) &&
//				  (puzzleboxOrbitAudioIRHandler != null)) {
//
//			fragmentAdvanced.registerControlledDescent();
//
//		} else {
//
		stopAudio();
//
//		}
//
		DevicePuzzleboxOrbitSingleton.getInstance().flightActive = false;


	} // stopControl


	// ################################################################

	public void stopAudio() {

		/**
		 * stop AudioTrack as well as destroy service.
		 */

		DevicePuzzleboxOrbitSingleton.getInstance().puzzleboxOrbitAudioIRHandler.keepPlaying = false;

		/**
		 * Stop playing audio control file
		 */

		if (DevicePuzzleboxOrbitSingleton.getInstance().soundPool != null) {
			try {
				DevicePuzzleboxOrbitSingleton.getInstance().soundPool.stop(DevicePuzzleboxOrbitSingleton.getInstance().soundID);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}


	} // stopControl


	// ################################################################

	public void demoMode(View v) {

		/**
		 * Demo mode is called when the "Test Helicopter" button is pressed.
		 * This method can be easily adjusted for testing new features
		 * during development.
		 */

		Log.v(TAG, "Test Flight clicked");


		Button buttonTestFlight = (Button) v.findViewById(R.id.buttonTestFlight);



		if (! DevicePuzzleboxOrbitSingleton.getInstance().flightActive) {


//		demoFlightMode = true;
			DevicePuzzleboxOrbitSingleton.getInstance().flightActive = true;
			DevicePuzzleboxOrbitSingleton.getInstance().demoActive = true;
//
//		FragmentTabAdvanced fragmentAdvanced =
//				  (FragmentTabAdvanced) getSupportFragmentManager().findFragmentByTag( getTabFragmentAdvanced() );
//
//		//		if (fragmentAdvanced.checkBoxGenerateAudio.isChecked())
//		if (generateAudio && (fragmentAdvanced != null))
//			eegPower = fragmentAdvanced.seekBarThrottle.getProgress();
//		else
//			eegPower = 100;

			buttonTestFlight.setText( getResources().getString(R.string.button_stop_test) );
//
			playControl();

//		demoFlightMode = false;

		} else {

			DevicePuzzleboxOrbitSingleton.getInstance().flightActive = false;
			DevicePuzzleboxOrbitSingleton.getInstance().demoActive = false;

			stopControl();

			buttonTestFlight.setText(getResources().getString(R.string.button_test_fly));

		}


	} // testFlight


	// ################################################################

	public void demoStop(View view) {

//		eegPower = 0;

		stopControl();

	} // demoStop


	// ################################################################

//	public void updateScore() {
//
//		FragmentTabFlightThinkGear fragmentFlight =
//				  (FragmentTabFlightThinkGear) getSupportFragmentManager().findFragmentByTag( getTabFragmentFlightThinkGear() );
//
//		if (fragmentFlight != null)
//			fragmentFlight.updateScore();
//
//	} // updateScore


	// ################################################################

//	public void resetCurrentScore() {
//
//		FragmentTabFlightThinkGear fragmentFlight =
//				  (FragmentTabFlightThinkGear) getSupportFragmentManager().findFragmentByTag( getTabFragmentFlightThinkGear() );
//
//		if (fragmentFlight != null)
//			fragmentFlight.resetCurrentScore();
//
//	} // resetCurrentScore


	// ################################################################

	/**
	 * the puzzleboxOrbitAudioIRHandler to update command
	 */
	public void updateAudioHandlerCommand(Integer[] command) {

//		this.puzzleboxOrbitAudioIRHandler.command = command;
//		this.puzzleboxOrbitAudioIRHandler.updateControlSignal();
		DevicePuzzleboxOrbitSingleton.getInstance().puzzleboxOrbitAudioIRHandler.command = command;
		DevicePuzzleboxOrbitSingleton.getInstance().puzzleboxOrbitAudioIRHandler.updateControlSignal();


	} // updateServiceBinderCommand


	// ################################################################

	/**
	 * the puzzleboxOrbitAudioIRHandler to update channel
	 */
	public void updateAudioHandlerChannel(int channel) {

//		this.puzzleboxOrbitAudioIRHandler.channel = channel;
//		this.puzzleboxOrbitAudioIRHandler.updateControlSignal();
		DevicePuzzleboxOrbitSingleton.getInstance().puzzleboxOrbitAudioIRHandler.channel = channel;
		DevicePuzzleboxOrbitSingleton.getInstance().puzzleboxOrbitAudioIRHandler.updateControlSignal();


	} // updateServiceBinderChannel


	// ################################################################

	/**
	 * @param number the puzzleboxOrbitAudioIRHandler to update loop number while mind control
	 */
	public void updateAudioHandlerLoopNumberWhileMindControl(int number) {

//		this.puzzleboxOrbitAudioIRHandler.loopNumberWhileMindControl = number;
		DevicePuzzleboxOrbitSingleton.getInstance().puzzleboxOrbitAudioIRHandler.loopNumberWhileMindControl = number;


	} // updateServiceBinderLoopNumberWhileMindControl


	// ################################################################

	public void resetControlSignal(View view) {

//		/**
//		 * Called when the "Reset" button is pressed
//		 */
//
//		FragmentTabAdvanced fragmentAdvanced =
//				  (FragmentTabAdvanced) getSupportFragmentManager().findFragmentByTag( getTabFragmentAdvanced() );
//
//		if (fragmentAdvanced != null)
//			fragmentAdvanced.resetControlSignal();


	} // resetControlSignal


	// ################################################################

	public void setControlSignalHover(View view) {

//		/**
//		 * Called when the "Hover" button is pressed
//		 */
//
//		FragmentTabAdvanced fragmentAdvanced =
//				  (FragmentTabAdvanced) getSupportFragmentManager().findFragmentByTag( getTabFragmentAdvanced() );
//
//		if (fragmentAdvanced != null)
//			fragmentAdvanced.setControlSignalHover();


	} // setControlSignalHover


	// ################################################################

	public void setControlSignalForward(View view) {

//		/**
//		 * Called when the "Forward" button is pressed
//		 */
//
//		FragmentTabAdvanced fragmentAdvanced =
//				  (FragmentTabAdvanced) getSupportFragmentManager().findFragmentByTag( getTabFragmentAdvanced() );
//
//		if (fragmentAdvanced != null)
//			fragmentAdvanced.setControlSignalForward();


	} // setControlSignalForward


	// ################################################################

	public void setControlSignalLeft(View view) {

//		/**
//		 * Called when the "Left" button is pressed
//		 */
//
//		FragmentTabAdvanced fragmentAdvanced =
//				  (FragmentTabAdvanced) getSupportFragmentManager().findFragmentByTag( getTabFragmentAdvanced() );
//
//		if (fragmentAdvanced != null)
//			fragmentAdvanced.setControlSignalLeft();


	} // setControlSignalLeft


	// ################################################################

	public void setControlSignalRight(View view) {

//		/**
//		 * Called when the "Right" button is pressed
//		 */
//
//		FragmentTabAdvanced fragmentAdvanced =
//				  (FragmentTabAdvanced) getSupportFragmentManager().findFragmentByTag( getTabFragmentAdvanced() );
//
//		if (fragmentAdvanced != null)
//			fragmentAdvanced.setControlSignalRight();


	} // setControlSignalRight



}
