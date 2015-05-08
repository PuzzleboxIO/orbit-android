/**
 * Puzzlebox Jigsaw
 * Copyright 2015 Puzzlebox Productions, LLC
 * License: GNU Affero General Public License Version 3
 */

package io.puzzlebox.orbit;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
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
import android.os.IBinder;
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

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import io.puzzlebox.orbit.data.OrbitSingleton;
import io.puzzlebox.jigsaw.data.SessionSingleton;
import io.puzzlebox.jigsaw.protocol.MuseService;
import io.puzzlebox.jigsaw.protocol.RBLGattAttributes;
import io.puzzlebox.jigsaw.protocol.RBLService;
import io.puzzlebox.jigsaw.protocol.ThinkGearService;
import io.puzzlebox.orbit.protocol.AudioHandler;
import io.puzzlebox.orbit.ui.FragmentTabAdvanced;
import io.puzzlebox.orbit.ui.FragmentTabFlightThinkGear;

import static android.view.MenuItem.SHOW_AS_ACTION_ALWAYS;

//import io.puzzlebox.jigsaw.R;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class OrbitFragment extends Fragment
		  implements SeekBar.OnSeekBarChangeListener {

	private final static String TAG = OrbitFragment.class.getSimpleName();

	private OnFragmentInteractionListener mListener;

	/**
	 * Configuration
	 */
	int eegPower = 0;

	public boolean generateAudio = true;
	public boolean invertControlSignal = false;
	boolean tiltSensorControl = false;
	public int deviceWarningMessagesDisplayed = 0;

	int minimumScoreTarget = 40;
	int scoreCurrent = 0;
	int scoreLast = 0;
	int scoreHigh = 0;

	boolean demoFlightMode = false;


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

//	private static TextView textViewSessionTime;

	int[] thresholdValuesAttention = new int[101];
	int[] thresholdValuesMeditation = new int[101];
	int minimumPower = 0; // minimum power for the bloom
	int maximumPower = 100; // maximum power for the bloom


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

//		progressBarRange = (ProgressBar) v.findViewById(R.id.progressBarRange);
////		ShapeDrawable progressBarRangeDrawable = new ShapeDrawable(new RoundRectShape(roundedCorners, null,null));
//		ShapeDrawable progressBarRangeDrawable = new ShapeDrawable();
////		String progressBarRangeColor = "#FF00FF";
//		String progressBarRangeColor = "#990099";
//		progressBarRangeDrawable.getPaint().setColor(Color.parseColor(progressBarRangeColor));
//		ClipDrawable progressRange = new ClipDrawable(progressBarRangeDrawable, Gravity.LEFT, ClipDrawable.HORIZONTAL);
//		progressBarRange.setProgressDrawable(progressRange);
//		progressBarRange.setBackgroundDrawable(getResources().getDrawable(android.R.drawable.progress_horizontal));
//
////		progressBarRange.setMax(128 + 127);
//		progressBarRange.setMax(bloomRangeMax);


//		progressBarBloom = (ProgressBar) v.findViewById(R.id.progressBarBloom);
//		ShapeDrawable progressBarBloomDrawable = new ShapeDrawable(new RoundRectShape(roundedCorners, null,null));
//		String progressBarBloomColor = "#7F0000";
//		progressBarBloomDrawable.getPaint().setColor(Color.parseColor(progressBarBloomColor));
//		ClipDrawable progressBloom = new ClipDrawable(progressBarBloomDrawable, Gravity.LEFT, ClipDrawable.HORIZONTAL);
//		progressBarBloom.setProgressDrawable(progressBloom);
//		progressBarBloom.setBackgroundDrawable(getResources().getDrawable(android.R.drawable.progress_horizontal));


//		// setup the Raw EEG History plot
//		eegRawHistoryPlot = (XYPlot) v.findViewById(R.id.eegRawHistoryPlot);
//		eegRawHistorySeries = new SimpleXYSeries("Raw EEG");
//
//		// Use index value as xVal, instead of explicit, user provided xVals.
//		//		eegRawHistorySeries.useImplicitXVals();
//
//		// Setup the boundary mode, boundary values only applicable in FIXED mode.
//
//		if (eegRawHistoryPlot != null) {
//
//			eegRawHistoryPlot.setDomainBoundaries(0, EEG_RAW_HISTORY_SIZE, BoundaryMode.FIXED);
//			//		eegRawHistoryPlot.setDomainBoundaries(0, EEG_RAW_HISTORY_SIZE, BoundaryMode.AUTO);
//			//		eegRawHistoryPlot.setRangeBoundaries(-32767, 32767, BoundaryMode.FIXED);
//			//		eegRawHistoryPlot.setRangeBoundaries(-32767, 32767, BoundaryMode.AUTO);
//			eegRawHistoryPlot.setRangeBoundaries(-256, 256, BoundaryMode.GROW);
//
//			eegRawHistoryPlot.addSeries(eegRawHistorySeries, new LineAndPointFormatter(Color.rgb(200, 100, 100), Color.BLACK, null, null));
//
//			// Thin out domain and range tick values so they don't overlap
//			eegRawHistoryPlot.setDomainStepValue(5);
//			eegRawHistoryPlot.setTicksPerRangeLabel(3);
//
//			//		eegRawHistoryPlot.setRangeLabel("Amplitude");
//
//			// Sets the dimensions of the widget to exactly contain the text contents
//			eegRawHistoryPlot.getDomainLabelWidget().pack();
//			eegRawHistoryPlot.getRangeLabelWidget().pack();
//
//			// Only display whole numbers in labels
//			eegRawHistoryPlot.getGraphWidget().setDomainValueFormat(new DecimalFormat("0"));
//			eegRawHistoryPlot.getGraphWidget().setRangeValueFormat(new DecimalFormat("0"));
//
//			// Hide domain and range labels
//			eegRawHistoryPlot.getGraphWidget().setDomainLabelWidth(0);
//			eegRawHistoryPlot.getGraphWidget().setRangeLabelWidth(0);
//
//			// Hide legend
//			eegRawHistoryPlot.getLegendWidget().setVisible(false);
//
//			// setGridPadding(float left, float top, float right, float bottom)
//			eegRawHistoryPlot.getGraphWidget().setGridPadding(0, 0, 0, 0);
//
//
//			//		eegRawHistoryPlot.getGraphWidget().setDrawMarkersEnabled(false);
//
//			//		final PlotStatistics histStats = new PlotStatistics(1000, false);
//			//		eegRawHistoryPlot.addListener(histStats);
//
//		}


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
		 * Update settings according to default UI
		 */

		updateScreenLayout();

		updatePowerThresholds();
		updatePower();


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


//		if (!OrbitSingleton.getInstance().mBluetoothAdapter.isEnabled()) {
//			Intent enableBtIntent = new Intent(
//					BluetoothAdapter.ACTION_REQUEST_ENABLE);
//			startActivityForResult(enableBtIntent, OrbitSingleton.getInstance().REQUEST_ENABLE_BT);
//		}

//		getActivity().registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
//
//		Intent gattServiceIntent = new Intent(getActivity(),
//				RBLService.class);
//		getActivity().bindService(gattServiceIntent, mServiceConnection, getActivity().BIND_AUTO_CREATE);

//		if (OrbitSingleton.getInstance().connState)
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
//		OrbitSingleton.getInstance().flag = false;
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

			Intent i = SessionSingleton.getInstance().getExportSessionIntent(getActivity().getApplicationContext(), item);

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

//	private void setButtonEnable() {
//		OrbitSingleton.getInstance().flag = true;
//		OrbitSingleton.getInstance().connState = true;
//
//		servoSeekBar.setEnabled(OrbitSingleton.getInstance().flag);
//		connectBloom.setText("Disconnect Bloom");
//
//		buttonDemo.setEnabled(true);
//	}


	// ################################################################

//	private void setButtonDisable() {
//		OrbitSingleton.getInstance().flag = false;
//		OrbitSingleton.getInstance().connState = false;
//
//		servoSeekBar.setEnabled(OrbitSingleton.getInstance().flag);
//		connectBloom.setText("Connect Bloom");
//
//		buttonDemo.setEnabled(false);
//
//		progressBarRange.setProgress(0);
//	}


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

//		String button_test_fly_small = getResources().getString(R.string.button_test_fly_small);
//		setButtonText(R.id.buttonTestFly, button_test_fly_small);

		textViewLabelScores.setVisibility(View.VISIBLE);
		viewSpaceScore.setVisibility(View.VISIBLE);


		android.view.ViewGroup.LayoutParams layoutParams;

		layoutParams = (android.view.ViewGroup.LayoutParams) viewSpaceScoreLast.getLayoutParams();
		layoutParams.width = 10;
		viewSpaceScoreLast.setLayoutParams(layoutParams);

		layoutParams = (android.view.ViewGroup.LayoutParams) viewSpaceScoreHigh.getLayoutParams();
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

		if (ThinkGearService.eegConnected) {

			if (ThinkGearService.eegSignal < 100) {
				ThinkGearService.eegAttention = 0;
				ThinkGearService.eegMeditation = 0;
				progressBarAttention.setProgress(ThinkGearService.eegAttention);
				progressBarMeditation.setProgress(ThinkGearService.eegMeditation);
			}

			ThinkGearService.eegPower = calculateSpeed();
			eegPower = ThinkGearService.eegPower;

			progressBarPower.setProgress(ThinkGearService.eegPower);


		}

		if (MuseService.eegConnected) {

//			Log.d(TAG, "MuseService.eegConnected: eegSignal: " + MuseService.eegSignal);
//			if (MuseService.eegSignal < 100) {
//				MuseService.eegConcentration = 0;
//				MuseService.eegMellow = 0;
//				progressBarAttention.setProgress(MuseService.eegConcentration);
//				progressBarMeditation.setProgress(MuseService.eegMellow);
//			}

			MuseService.eegPower = calculateSpeed();

			progressBarPower.setProgress(MuseService.eegPower);
			eegPower = MuseService.eegPower;


		}


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

			Log.d(TAG, "flightActive: " + flightActive);



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
				  (eegAttentionTarget > minimumScoreTarget))
			eegAttentionScore = eegAttentionTarget - minimumScoreTarget;

		if ((eegMeditation >= eegMeditationTarget) &&
				  (eegMeditationTarget > minimumScoreTarget))
			eegMeditationScore = eegMeditationTarget - minimumScoreTarget;

		if (eegAttentionScore > eegMeditationScore)
			scoreCurrent = scoreCurrent + eegAttentionScore;
		else
			scoreCurrent = scoreCurrent + eegMeditationScore;

		textViewScore.setText(Integer.toString(scoreCurrent));

		if (scoreCurrent > scoreHigh) {
			scoreHigh = scoreCurrent;
			textViewHighScore.setText(Integer.toString(scoreHigh));
		}


		// Catch anyone gaming the system with one slider
		// below the minimum threshold and the other over.
		// For example, setting Meditation to 1% will keep helicopter
		// activated even if Attention is below target
		if ((eegAttention < eegAttentionTarget) && (eegMeditation < minimumScoreTarget))
			resetCurrentScore();
		if ((eegMeditation < eegMeditationTarget) && (eegAttention < minimumScoreTarget))
			resetCurrentScore();
		if ((eegAttention < minimumScoreTarget) && (eegMeditation < minimumScoreTarget))
			resetCurrentScore();


	} // updateScore


	// ################################################################

	public void resetCurrentScore() {

		if (scoreCurrent > 0)
			textViewLastScore.setText(Integer.toString(scoreCurrent));
		scoreCurrent = 0;
		textViewScore.setText(Integer.toString(scoreCurrent));

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

		// TODO Convert to service

//		FragmentTabAdvanced fragmentAdvanced =
//				  (FragmentTabAdvanced) getActivity().getSupportFragmentManager().findFragmentByTag( getTabFragmentAdvanced() );
//
//		if (generateAudio) {
//
//			/**
//			 * Generate signal on the fly
//			 */
//
//			// Handle controlled descent thread if activated
//			if ((fragmentAdvanced.orbitControlledDescentTask != null) &&
//					  (fragmentAdvanced.orbitControlledDescentTask.keepDescending)) {
//				fragmentAdvanced.orbitControlledDescentTask.callStopAudio = false;
//				fragmentAdvanced.orbitControlledDescentTask.keepDescending = false;
//			}
//
//
//			//			if (audioHandler != null) {
//
//			//				serviceBinder.ifFlip = fragmentAdvanced.checkBoxInvertControlSignal.isChecked(); // if checked then flip
//			audioHandler.ifFlip = invertControlSignal; // if checked then flip
//
//			int channel = 0; // default "A"
//
//			if (fragmentAdvanced != null)
//				channel = fragmentAdvanced.radioGroupChannel.getCheckedRadioButtonId();
//
//			//				if (demoFlightMode)
//			//					updateAudioHandlerLoopNumberWhileMindControl(200);
//			//				else
//			//					updateAudioHandlerLoopNumberWhileMindControl(4500);
//			//
//			//			updateAudioHandlerLoopNumberWhileMindControl(5000);
//
//			updateAudioHandlerLoopNumberWhileMindControl(-1); // Loop infinite for easier user testing
//
//			updateAudioHandlerChannel(channel);
//
//			audioHandler.mutexNotify();
//			//			}
//
//
//		} else {
//
//			/**
//			 * Play audio control file
//			 */
//
//			/** Getting the user sound settings */
//			AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
//			//			float actualVolume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
//			float maxVolume = (float) audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
//			//			float volume = actualVolume / maxVolume;
//
//			audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (int) maxVolume, 0);
//			/** Is the sound loaded already? */
//			if (loaded) {
//				//				soundPool.play(soundID, volume, volume, 1, 0, 1f);
//				//				soundPool.setVolume(soundID, 1f, 1f);
//				//				soundPool.play(soundID, maxVolume, maxVolume, 1, 0, 1f); // Fixes Samsung Galaxy S4 [SGH-M919]
//
//				soundPool.play(soundID, 1f, 1f, 1, 0, 1f); // Fixes Samsung Galaxy S4 [SGH-M919]
//
//				// TODO No visible effects of changing these variables on digital oscilloscope
//				//				soundPool.play(soundID, 0.5f, 0.5f, 1, 0, 0.5f);
//				if (DEBUG)
//					Log.v(TAG, "Played sound");
//			}
//
//		}

	} // playControl


	// ################################################################

	public void stopControl() {

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
//				  (audioHandler != null)) {
//
//			fragmentAdvanced.registerControlledDescent();
//
//		} else {
//
//			stopAudio();
//
//		}
//
//		flightActive = false;


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

		// TODO Convert to service

//		/**
//		 * Demo mode is called when the "Test Helicopter" button is pressed.
//		 * This method can be easily adjusted for testing new features
//		 * during development.
//		 */
//
//		Log.v(TAG, "Sending Test Signal to Helicopter");
////		appendDebugConsole("Sending Test Signal to Helicopter\n");
//
//		demoFlightMode = true;
//		flightActive = true;
//
//		FragmentTabAdvanced fragmentAdvanced =
//				  (FragmentTabAdvanced) getSupportFragmentManager().findFragmentByTag( getTabFragmentAdvanced() );
//
//		//		if (fragmentAdvanced.checkBoxGenerateAudio.isChecked())
//		if (generateAudio && (fragmentAdvanced != null))
//			eegPower = fragmentAdvanced.seekBarThrottle.getProgress();
//		else
//			eegPower = 100;
//
//		playControl();
//
//		demoFlightMode = false;


	} // demoMode


	// ################################################################

	public void demoStop(View view) {

		eegPower = 0;

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
