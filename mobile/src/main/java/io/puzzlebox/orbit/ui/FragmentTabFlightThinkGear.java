
package io.puzzlebox.orbit.ui;

import io.puzzlebox.orbit.OrbitTabActivity;
import io.puzzlebox.orbit.R;

import android.bluetooth.BluetoothAdapter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.neurosky.thinkgear.TGDevice;

import java.text.DecimalFormat;
import java.util.Arrays;

//import android.app.Fragment;


public class FragmentTabFlightThinkGear extends Fragment implements OnClickListener, SeekBar.OnSeekBarChangeListener {

	/**
	 * Create a new instance of FragmentTabFlight, providing "num"
	 * as an argument.
	 */
	FragmentTabFlightThinkGear newInstance(int num) {
		FragmentTabFlightThinkGear f = new FragmentTabFlightThinkGear();

		// Supply num input as an argument
		Bundle args = new Bundle();
		args.putInt("num", num);
		f.setArguments(args);

		return f;
	}


	/**
	 * Configuration
	 */
	int eegAttention = 0;
	int eegMeditation = 0;
	int eegPower = 0;
	int eegSignal = 0;
	boolean eegConnected = false;
	boolean eegConnecting = false;
	boolean demoFlightMode = false;
	Number[] rawEEG = new Number[512];
	int arrayIndex = 0;

	int minimumScoreTarget = 40;
	int scoreCurrent = 0;
	int scoreLast = 0;
	int scoreHigh = 0;


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
	Button connectButton;
	Button testFlightButton;
	Button stopTestButton;

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

	int[] thresholdValuesAttention = new int[101];
	int[] thresholdValuesMeditation = new int[101];
	int minimumPower = 0; // minimum power for the helicopter throttle
	int maximumPower = 100; // maximum power for the helicopter throttle

	int viewSpaceGenerateAudioWidth = 120;

	private final int EEG_RAW_HISTORY_SIZE = 512;            // number of points to plot in EEG history
	private XYPlot eegRawHistoryPlot = null;
	private SimpleXYSeries eegRawHistorySeries = null;


	LinearLayout layoutControl;
	LinearLayout layoutAudioService;
	LinearLayout layoutAdvancedOptions;
	LinearLayout layoutInvertControlSignal;
	View viewSpaceGenerateAudio;

	View v;

	int mNum;


	/**
	 * Logging
	 */
	/** set to "false" for production releases */
	//	boolean DEBUG = true;
	boolean DEBUG = false;
	String TAG = "FragmentTabFlightThinkGear";


	/**
	 * Bluetooth
	 */
	BluetoothAdapter bluetoothAdapter;
	//	ArrayList<String> pairedBluetoothDevices;


	/**
	 * NeuroSky ThinkGear Device
	 */
	TGDevice tgDevice;
	int tgSignal = 0;
	//	final boolean rawEnabled = false;
	final boolean rawEnabled = true;


	/**
	 * When creating, retrieve this instance's number from its arguments.
	 */
	@Override
	public void onCreate (Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		mNum = getArguments() != null ? getArguments().getInt("num") : 1;

	} // onCreate


	@SuppressWarnings("deprecation")
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		v = inflater.inflate(R.layout.fragment_flight_thinkgear, container, false);

		((OrbitTabActivity)getActivity()).setTabFragmentFlightThinkGear(getTag());

		config = getResources().getConfiguration();

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


		// setup the Raw EEG History plot
		eegRawHistoryPlot = (XYPlot) v.findViewById(R.id.eegRawHistoryPlot);
		eegRawHistorySeries = new SimpleXYSeries("Raw EEG");

		// Use index value as xVal, instead of explicit, user provided xVals.
		//		eegRawHistorySeries.useImplicitXVals();

		// Setup the boundary mode, boundary values only applicable in FIXED mode.

		if (eegRawHistoryPlot != null) {

			eegRawHistoryPlot.setDomainBoundaries(0, EEG_RAW_HISTORY_SIZE, BoundaryMode.FIXED);
			//		eegRawHistoryPlot.setDomainBoundaries(0, EEG_RAW_HISTORY_SIZE, BoundaryMode.AUTO);
			//		eegRawHistoryPlot.setRangeBoundaries(-32767, 32767, BoundaryMode.FIXED);
			//		eegRawHistoryPlot.setRangeBoundaries(-32767, 32767, BoundaryMode.AUTO);
			eegRawHistoryPlot.setRangeBoundaries(-256, 256, BoundaryMode.GROW);

			eegRawHistoryPlot.addSeries(eegRawHistorySeries, new LineAndPointFormatter(Color.rgb(200, 100, 100), Color.BLACK, null, null));

			// Thin out domain and range tick values so they don't overlap
			eegRawHistoryPlot.setDomainStepValue(5);
			eegRawHistoryPlot.setTicksPerRangeLabel(3);

			//		eegRawHistoryPlot.setRangeLabel("Amplitude");

			// Sets the dimensions of the widget to exactly contain the text contents
			eegRawHistoryPlot.getDomainLabelWidget().pack();
			eegRawHistoryPlot.getRangeLabelWidget().pack();

			// Only display whole numbers in labels
			eegRawHistoryPlot.getGraphWidget().setDomainValueFormat(new DecimalFormat("0"));
			eegRawHistoryPlot.getGraphWidget().setRangeValueFormat(new DecimalFormat("0"));

			// Hide domain and range labels
			eegRawHistoryPlot.getGraphWidget().setDomainLabelWidth(0);
			eegRawHistoryPlot.getGraphWidget().setRangeLabelWidth(0);

			// Hide legend
			eegRawHistoryPlot.getLegendWidget().setVisible(false);

			// setGridPadding(float left, float top, float right, float bottom) 
			eegRawHistoryPlot.getGraphWidget().setGridPadding(0, 0, 0, 0); 


			//		eegRawHistoryPlot.getGraphWidget().setDrawMarkersEnabled(false);

			//		final PlotStatistics histStats = new PlotStatistics(1000, false);
			//		eegRawHistoryPlot.addListener(histStats);

		}



		seekBarAttention = (SeekBar) v.findViewById(R.id.seekBarAttention);
		seekBarAttention.setOnSeekBarChangeListener(this);
		seekBarMeditation = (SeekBar) v.findViewById(R.id.seekBarMeditation);
		seekBarMeditation.setOnSeekBarChangeListener(this);


		imageViewStatus = (ImageView) v.findViewById(R.id.imageViewStatus);


		connectButton = (Button) v.findViewById(R.id.buttonConnect);
		connectButton.setOnClickListener(this);

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
		 * Prepare Bluetooth and NeuroSky ThinkGear EEG interface
		 */

		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		if (bluetoothAdapter == null) {
			// Alert user that Bluetooth is not available
			Toast.makeText(((OrbitTabActivity) getActivity()), "Bluetooth not available", Toast.LENGTH_LONG).show();

		} else {
			/** create the TGDevice */
			tgDevice = new TGDevice(bluetoothAdapter, handlerThinkGear);

			/** Retrieve a list of paired Bluetooth adapters */
			//			Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
			//			pairedBluetoothDevices = new ArrayList<String>(Arrays.asList(pairedDevices.toString()));
			/** 
			 * NOTE: To get device names iterate through pairedBluetoothDevices 
			 * and call the getName() method on each BluetoothDevice object. 
			 */
		}


		/**
		 * Update settings according to default UI
		 */

		updateScreenLayout();

		updatePowerThresholds();
		updatePower();


		return v;


	} // onCreateView()


	// ################################################################

	public void onPause() {

		Log.v(TAG, "onPause()");

		super.onPause();

		try {

			disconnectHeadset();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.v(TAG, "Exception: onPause()");
			e.printStackTrace();
		}

	} // onPause


	// ################################################################

	public void onResume() {

		super.onResume();

		/**
		 * This method is called when the Activity has been 
		 * resumed after being placed in the background
		 */

		if (eegConnected)
			setButtonText(R.id.buttonConnect, "Disconnect");

	} // onResume


	// ################################################################

	public void onStop() {

		Log.v(TAG, "onStop()");

		super.onStop();

		try {

			disconnectHeadset();


		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.v(TAG, "Exception: onStop()");
			e.printStackTrace();
		}

	} // onStop


	// ################################################################

	public void onDestroy() {

		/**
		 * This method is called when the Activity is terminated
		 */

		super.onDestroy();

		try {

			if(bluetoothAdapter != null)
				tgDevice.close();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.v(TAG, "Exception: onDestroy()");
			e.printStackTrace();
		}


	} // onDestroy


	// ################################################################

	protected void appendDebugConsole(String text) {

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


		try {
			((OrbitTabActivity)getActivity()).appendDebugConsole(text); // perform by parent activity
		}
		catch (Exception e) {
			Log.v(TAG, e.getMessage());
		}

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

		String button_test_fly_small = getResources().getString(R.string.button_test_fly_small);
		setButtonText(R.id.buttonTestFly, button_test_fly_small);

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


		// HTC Droid DNA - AndroidPlot has issues with OpenGL Render
		if ((Build.MANUFACTURER.contains("HTC")) &&
				(Build.MODEL.contains("HTC6435LVW"))) {
			
			Log.v(TAG, "Device detected: HTC Droid DNA");
			hideEEGRawHistory();
			
		}
		

	} // updateScreenLayoutSmall


	// ################################################################

	public void setButtonText(int buttonId, String text) {

		/**
		 * Shortcut for changing the text on a button
		 */

		Button button = (Button) v.findViewById(buttonId);
		button.setText(text);

	} // setButtonText


	// ################################################################

	public void updateStatusImage() {

		if(DEBUG) {
			Log.v(TAG, (new StringBuilder("Attention: ")).append(eegAttention).toString());
			Log.v(TAG, (new StringBuilder("Meditation: ")).append(eegMeditation).toString());
			Log.v(TAG, (new StringBuilder("Power: ")).append(eegPower).toString());
			Log.v(TAG, (new StringBuilder("Signal: ")).append(eegSignal).toString());
			Log.v(TAG, (new StringBuilder("Connecting: ")).append(eegConnecting).toString());
			Log.v(TAG, (new StringBuilder("Connected: ")).append(eegConnected).toString());
		}


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

	public void onClick(View v) {

		switch (v.getId()) {

		case R.id.buttonConnect:

			connectHeadset(v);

		}

	} // onClick


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

	private final Handler handlerThinkGear = new Handler() {

		/**
		 * Handles data packets from NeuroSky ThinkGear device
		 */

		public void handleMessage(Message msg) {

			parseEEG(msg);

		}

	}; // handlerThinkGear


	// ################################################################

	public void parseEEG(Message msg) {

		switch (msg.what) {

		case TGDevice.MSG_STATE_CHANGE:

			switch (msg.arg1) {
			case TGDevice.STATE_IDLE:
				break;
			case TGDevice.STATE_CONNECTING:
				if (DEBUG)
					Log.v(TAG, "Connecting to EEG");
				appendDebugConsole("Connecting to EEG\n");
				eegConnecting = true;
				eegConnected = false;
				updateStatusImage();
				break;
			case TGDevice.STATE_CONNECTED:
				if (DEBUG)
					Log.v(TAG, "EEG Connected");
				appendDebugConsole("Bluetooth Connected\n");
				setButtonText(R.id.buttonConnect, "Disconnect");
				eegConnecting = false;
				eegConnected = true;
				updateStatusImage();
				tgDevice.start();
				break;
			case TGDevice.STATE_NOT_FOUND:
				if (DEBUG)
					Log.v(TAG, "EEG headset not found");
				appendDebugConsole("EEG headset not found\n");
				eegConnecting = false;
				eegConnected = false;
				updateStatusImage();
				break;
			case TGDevice.STATE_NOT_PAIRED:
				if (DEBUG)
					Log.v(TAG, "EEG headset not paired");
				appendDebugConsole("EEG headset not paired\n");
				eegConnecting = false;
				eegConnected = false;
				updateStatusImage();
				break;
			case TGDevice.STATE_DISCONNECTED:
				if (DEBUG)
					Log.v(TAG, "EEG Disconnected");
				appendDebugConsole("EEG Disconnected\n");
				eegConnecting = false;
				eegConnected = false;
				updateStatusImage();
				disconnectHeadset();
				break;
			}

			break;

		case TGDevice.MSG_POOR_SIGNAL:
			//			Log.v(TAG, "PoorSignal: " + msg.arg1);
			eegSignal = calculateSignal(msg.arg1);
			progressBarSignal.setProgress(eegSignal);
			updateStatusImage();
			break;
		case TGDevice.MSG_ATTENTION:
			//			Log.v(TAG, "Attention: " + eegAttention);
			eegAttention = msg.arg1;
			progressBarAttention.setProgress(eegAttention);
			updatePower();

			break;
		case TGDevice.MSG_MEDITATION:
			eegMeditation = msg.arg1;
			if (DEBUG)
				Log.v(TAG, "Meditation: " + eegMeditation);
			progressBarMeditation.setProgress(eegMeditation);
			updatePower();

			break;
		case TGDevice.MSG_BLINK:
			//tv.append("Blink: " + msg.arg1 + "\n");
			break;
		case TGDevice.MSG_RAW_DATA:

			rawEEG[arrayIndex] = msg.arg1;
			arrayIndex = arrayIndex + 1;

			if (arrayIndex == EEG_RAW_HISTORY_SIZE - 1)
				updateEEGRawHistory(rawEEG);

			break;
		case TGDevice.MSG_RAW_COUNT:
			//tv.append("Raw Count: " + msg.arg1 + "\n");
			break;
		case TGDevice.MSG_RAW_MULTI:
			//TGRawMulti rawM = (TGRawMulti)msg.obj;
			//tv.append("Raw1: " + rawM.ch1 + "\nRaw2: " + rawM.ch2);
		case TGDevice.MSG_HEART_RATE:
			//				appendDebugConsole("Heart rate: " + msg.arg1 + "\n");
			break;
		case TGDevice.MSG_LOW_BATTERY:
			Toast.makeText(((OrbitTabActivity) getActivity()), "EEG battery low!", Toast.LENGTH_SHORT).show();
			break;
		default:
			break;
		}

	} // handleMessage


	// ################################################################

	public void connectHeadset(View view) {

		/**
		 * Called when the "Connect" button is pressed
		 */

		Log.v(TAG, "connectHeadset()");

		/** Stop audio stream */
//		((OrbitTabActivity)getActivity()).stopControl();

		if(bluetoothAdapter == null) {

			// Alert user that Bluetooth is not available
			Toast.makeText(((OrbitTabActivity) getActivity()), "Bluetooth not available", Toast.LENGTH_LONG).show();

		} else {

			if (tgDevice.getState() != TGDevice.STATE_CONNECTING && tgDevice.getState() != TGDevice.STATE_CONNECTED) {
				tgDevice.connect(rawEnabled);
				((OrbitTabActivity)getActivity()).maximizeAudioVolume(); // Automatically set media volume to maximum
			}


			else if (tgDevice.getState() == TGDevice.STATE_CONNECTED)
				/** "Disconnect" button was pressed */
				disconnectHeadset();

		}

	} // connectHeadset


	// ################################################################

	public void disconnectHeadset() {

		/**
		 * Called when "Disconnect" button is pressed
		 */

		eegConnecting = false;
		eegConnected = false;

		eegAttention = 0;
		eegMeditation = 0;
		eegSignal = 0;
		eegPower = 0;

		updateStatusImage();

		progressBarAttention.setProgress(eegAttention);
		progressBarMeditation.setProgress(eegMeditation);
		progressBarSignal.setProgress(eegSignal);
		progressBarPower.setProgress(eegPower);
		
		
		String id = ((OrbitTabActivity)getActivity()).getTabFragmentAdvanced();

		FragmentTabAdvanced fragmentAdvanced = 
				(FragmentTabAdvanced) getFragmentManager().findFragmentByTag(id);

		if (fragmentAdvanced != null) {
			fragmentAdvanced.progressBarAttention.setProgress(eegAttention);
			fragmentAdvanced.progressBarMeditation.setProgress(eegMeditation);
			fragmentAdvanced.progressBarSignal.setProgress(eegSignal);
			fragmentAdvanced.progressBarPower.setProgress(eegPower);
		}
		
		
		setButtonText(R.id.buttonConnect, "Connect");


		if (tgDevice.getState() == TGDevice.STATE_CONNECTED) {
			tgDevice.stop();
			tgDevice.close();

//			((OrbitTabActivity)getActivity()).stopControl();

			disconnectHeadset();

		}


	} // disconnectHeadset


	// ################################################################

	public int calculateSignal(int signal) {

		/** 
		 * The ThinkGear protocol states that a signal level of 200 will be
		 * returned when a clean ground/reference is not detected at the ear clip,
		 *  and a value of 0 when the signal is perfectly clear. We need to
		 *  convert this information into usable settings for the Signal
		 *  progress bar
		 */

		int value;

		switch (signal) {
		case 200:
			value = 0;
		case 0:
			value = 100;
		default:
			value = (int)(100 - ((signal / 200.0) * 100));
		}

		return(value);

	} // calculateSignal


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

				percentOfMaxPower = (float)( ((100 - attentionSeekValue) - (100 - i)) / (float)(100 - attentionSeekValue) );
				power = thresholdValuesAttention[i] + (int)( minimumPower + ((maximumPower - minimumPower) * percentOfMaxPower) );
				thresholdValuesAttention[i] = power;

			}
		}

		meditationSeekValue = seekBarMeditation.getProgress();
		if (meditationSeekValue > 0) {
			for (int i = meditationSeekValue; i < thresholdValuesMeditation.length; i++) {
				percentOfMaxPower = (float)( ((100 - meditationSeekValue) - (100 - i)) / (float)(100 - meditationSeekValue) );
				power = thresholdValuesMeditation[i] + (int)( minimumPower + ((maximumPower - minimumPower) * percentOfMaxPower) );
				thresholdValuesMeditation[i] = power;
			}
		}

	} // updatePowerThresholds


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

		// If control signal is being generated, set the
		// power level equal to the current throttle slider

		String id = ((OrbitTabActivity)getActivity()).getTabFragmentAdvanced();

		FragmentTabAdvanced fragmentAdvanced = 
				(FragmentTabAdvanced) getFragmentManager().findFragmentByTag(id);

		if (fragmentAdvanced != null) {
			if ((fragmentAdvanced.checkBoxGenerateAudio.isChecked()) && (speed > 0)) {
				speed = fragmentAdvanced.seekBarThrottle.getProgress();
			}
		}

		return(speed);

	} // calculateSpeed


	// ################################################################

	//	public int updatePower() {
	public void updatePower() {

		/**
		 * This method updates the power level of the 
		 * "Throttle" and triggers the audio stream
		 * which is used to fly the helicopter
		 */

		// Set Attention and Meditation to zero if we've lost signal
		if (eegSignal < 100) {
			eegAttention = 0;
			eegMeditation = 0;
			progressBarAttention.setProgress(eegAttention);
			progressBarMeditation.setProgress(eegMeditation);
		}

		eegPower = calculateSpeed();

		progressBarPower.setProgress(eegPower);

		((OrbitTabActivity)getActivity()).eegPower = eegPower;
//		((OrbitTabActivity)getActivity()).updatePower();


		String id = ((OrbitTabActivity)getActivity()).getTabFragmentAdvanced();

		FragmentTabAdvanced fragmentAdvanced = 
				(FragmentTabAdvanced) getFragmentManager().findFragmentByTag(id);

		if (fragmentAdvanced != null) {
			fragmentAdvanced.progressBarAttention.setProgress(eegAttention);
			fragmentAdvanced.progressBarMeditation.setProgress(eegMeditation);
			fragmentAdvanced.progressBarSignal.setProgress(eegSignal);
			fragmentAdvanced.progressBarPower.setProgress(eegPower);
		}


	} // updatePower


	// ################################################################

	public void hideEEGRawHistory() {

		Log.v(TAG, "hideEEGRawHistory()");

		if (eegRawHistoryPlot != null)
			eegRawHistoryPlot.setVisibility(View.GONE);


		//			removeView*(View)
		//			eegRawHistoryPlot.remove
		//			(XYPlot) v.findViewById(R.id.eegRawHistoryPlot)


	} // hideEEGRawHistory


	// ################################################################

	public void updateEEGRawHistory(Number[] rawEEG) {

		if (eegRawHistoryPlot != null) {
			eegRawHistoryPlot.removeSeries(eegRawHistorySeries);

			eegRawHistorySeries = new SimpleXYSeries(Arrays.asList(rawEEG), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Raw EEG");

			//		LineAndPointFormatter format = new LineAndPointFormatter(Color.rgb(200, 100, 100), Color.BLACK, null, null);
			//		LineAndPointFormatter format = new LineAndPointFormatter(Color.rgb(200, 100, 100), Color.TRANSPARENT, null, null);
			LineAndPointFormatter format = new LineAndPointFormatter(Color.rgb(0, 0, 0), Color.TRANSPARENT, null, null);

			//		format.getFillPaint().setAlpha(220);

			eegRawHistoryPlot.addSeries(eegRawHistorySeries, format);


			// redraw the Plots:
			eegRawHistoryPlot.redraw();

			rawEEG = new Number[512];
			arrayIndex = 0;
		}

	} // updateEEGRawHistory

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


} // FragmentTabFlightThinkGear
