package io.puzzlebox.orbit.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import io.puzzlebox.jigsaw.protocol.ThinkGearService;
import io.puzzlebox.orbit.R;
import io.puzzlebox.orbit.data.OrbitSingleton;

import static io.puzzlebox.jigsaw.protocol.ThinkGearService.eegConnected;
import static io.puzzlebox.jigsaw.protocol.ThinkGearService.eegConnecting;
import static io.puzzlebox.jigsaw.protocol.ThinkGearService.eegSignal;

public class DialogPuzzleboxOrbitFragment extends DialogFragment
		  implements SeekBar.OnSeekBarChangeListener {

	private final static String TAG = DialogPuzzleboxOrbitFragment.class.getSimpleName();

	/**
	 * Configuration
	 */
	public int eegPower = 0;

	// UI
	Button buttonDeviceEnable;

	ProgressBar progressBarAttention;
	SeekBar seekBarAttention;
	ProgressBar progressBarMeditation;
	SeekBar seekBarMeditation;
	ProgressBar progressBarSignal;
	ProgressBar progressBarPower;
//	ProgressBar progressBarBlink;

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
	int minimumPower = 0; // minimum power for the bloom
	int maximumPower = 100; // maximum power for the bloom

	private static Intent intentThinkGear;

	private OnFragmentInteractionListener mListener;

	public DialogPuzzleboxOrbitFragment() {
		// Required empty public constructor
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
									 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View v = inflater.inflate(R.layout.dialog_profile_puzzlebox_orbit, container, false);

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

		progressBarPower = (ProgressBar) v.findViewById(R.id.progressBarPower);
		ShapeDrawable progressBarPowerDrawable = new ShapeDrawable(new RoundRectShape(roundedCorners, null,null));
		String progressBarPowerColor = "#FFFF00";
		progressBarPowerDrawable.getPaint().setColor(Color.parseColor(progressBarPowerColor));
		ClipDrawable progressPower = new ClipDrawable(progressBarPowerDrawable, Gravity.LEFT, ClipDrawable.HORIZONTAL);
		progressBarPower.setProgressDrawable(progressPower);
		progressBarPower.setBackgroundDrawable(getResources().getDrawable(android.R.drawable.progress_horizontal));

//		progressBarBlink = (ProgressBar) v.findViewById(R.id.progressBarBlink);
//		ShapeDrawable progressBarRangeDrawable = new ShapeDrawable();
////		String progressBarBlinkColor = "#FF00FF";
////		String progressBarBlinkColor = "#990099";
//		String progressBarBlinkColor = "#BBBBBB";
//		progressBarRangeDrawable.getPaint().setColor(Color.parseColor(progressBarBlinkColor));
//		ClipDrawable progressRange = new ClipDrawable(progressBarRangeDrawable, Gravity.LEFT, ClipDrawable.HORIZONTAL);
//		progressBarBlink.setProgressDrawable(progressRange);
//		progressBarBlink.setBackgroundDrawable(getResources().getDrawable(android.R.drawable.progress_horizontal));
//
//		progressBarBlink.setMax(ThinkGearService.blinkRangeMax);


		seekBarAttention = (SeekBar) v.findViewById(R.id.seekBarAttention);
		seekBarAttention.setOnSeekBarChangeListener(this);
		seekBarMeditation = (SeekBar) v.findViewById(R.id.seekBarMeditation);
		seekBarMeditation.setOnSeekBarChangeListener(this);


		imageViewStatus = (ImageView) v.findViewById(R.id.imageViewStatus);


//		textViewLabelScores = (TextView) v.findViewById(R.id.textViewLabelScores);
		textViewLabelScore = (TextView) v.findViewById(R.id.textViewLabelScore);
		textViewLabelLastScore = (TextView) v.findViewById(R.id.textViewLabelLastScore);
		textViewLabelHighScore = (TextView) v.findViewById(R.id.textViewLabelHighScore);

//		viewSpaceScore = (View) v.findViewById(R.id.viewSpaceScore);
//		viewSpaceScoreLast = (View) v.findViewById(R.id.viewSpaceScoreLast);
//		viewSpaceScoreHigh = (View) v.findViewById(R.id.viewSpaceScoreHigh);

		textViewScore = (TextView) v.findViewById(R.id.textViewScore);
		textViewLastScore = (TextView) v.findViewById(R.id.textViewLastScore);
		textViewHighScore = (TextView) v.findViewById(R.id.textViewHighScore);


		// Hide the "Scores" label by default
//		textViewLabelScores.setVisibility(View.GONE);
//		viewSpaceScore.setVisibility(View.GONE);


		Button buttonDeviceCancel = (Button) v.findViewById(io.puzzlebox.jigsaw.R.id.buttonDeviceCancel);
		buttonDeviceCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});

		buttonDeviceEnable = (Button) v.findViewById(io.puzzlebox.jigsaw.R.id.buttonDeviceEnable);
		buttonDeviceEnable.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});


		intentThinkGear = new Intent(getActivity(), ThinkGearService.class);


		/**
		 * AudioHandler
		 */

		if (!OrbitSingleton.getInstance().audioHandler.isAlive()) {


			/**
			 * Prepare audio stream
			 */

			// TODO
//			maximizeAudioVolume(); // Automatically set media volume to maximum

			/** Set the hardware buttons to control the audio output */
			getActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);

			/** Preload the flight control WAV file into memory */
			OrbitSingleton.getInstance().soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
			OrbitSingleton.getInstance().soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
				public void onLoadComplete(SoundPool soundPool,
													int sampleId,
													int status) {
					OrbitSingleton.getInstance().loaded = true;
				}
			});
			OrbitSingleton.getInstance().soundID = OrbitSingleton.getInstance().soundPool.load(getActivity().getApplicationContext(), OrbitSingleton.getInstance().audioFile, 1);


			OrbitSingleton.getInstance().audioHandler.start();


		}


		if (OrbitSingleton.getInstance().flightActive)
			buttonTestFlight.setText(getResources().getString(R.string.button_stop_test));

		/**
		 * Update settings according to default UI
		 */

		// TODO
//		updateScreenLayout();

		updatePowerThresholds();
//		updatePower();


		return v;

	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		if (context instanceof OnFragmentInteractionListener) {
			mListener = (OnFragmentInteractionListener) context;
		} else {
			throw new RuntimeException(context.toString()
					  + " must implement OnFragmentInteractionListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	public interface OnFragmentInteractionListener {
		void onFragmentInteraction(Uri uri);
	}

	// ################################################################

	public void onPause() {

		super.onPause();

		LocalBroadcastManager.getInstance(
				  getActivity().getApplicationContext()).unregisterReceiver(
				  mPacketReceiver);

		LocalBroadcastManager.getInstance(
				  getActivity().getApplicationContext()).unregisterReceiver(
				  mEventReceiver);

	} // onPause


	// ################################################################

	public void onResume() {

		// Store access variables for window and blank point

		Window window = getDialog().getWindow();

		Point size = new Point();

		// Store dimensions of the screen in `size`
		Display display = window.getWindowManager().getDefaultDisplay();

		display.getSize(size);

		// Set the width of the dialog proportional to a percentage of the screen width
//		window.setLayout((int) (size.x * 0.9), WindowManager.LayoutParams.WRAP_CONTENT);
//		window.setLayout((int) (size.x * 0.975), WindowManager.LayoutParams.WRAP_CONTENT);
		window.setLayout((int) (size.x * 0.98), WindowManager.LayoutParams.WRAP_CONTENT);

		// Set the dimensions  of the dialog proportional to a percentage of the screen dimensions
//		window.setLayout((int) (size.x * 0.95), (int) (size.y * 0.935));

		window.setGravity(Gravity.CENTER);

		// Call super onResume after sizing
		super.onResume();

		updatePowerThresholds();
		updatePower();

		LocalBroadcastManager.getInstance(getActivity().getApplicationContext()).registerReceiver(
				  mPacketReceiver, new IntentFilter("io.puzzlebox.jigsaw.protocol.thinkgear.packet"));

		LocalBroadcastManager.getInstance(getActivity().getApplicationContext()).registerReceiver(
				  mEventReceiver, new IntentFilter("io.puzzlebox.jigsaw.protocol.thinkgear.event"));

	}


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

//			if ((! buttonDeviceEnable.isEnabled()) && (eegSignal == ThinkGearService.signalSignalMax)) {
//				// This setting requires the quality of the EEG sensor's
//				// contact with skin hit to 100% at least once since the
//				// headset was last connected.
//				buttonDeviceEnable.setEnabled(true);
//				buttonDeviceEnable.setVisibility(View.VISIBLE);
//			}

//			progressBarBlink.setProgress(0);

//			updateEEGRawHistory(SessionSingleton.getInstance().getCurrentRawEEG());

//			updateSessionTime();

			updateStatusImage();

//			Log.e(TAG, "mPacketReceiver: eegConnected: " + eegConnected);
//			if (eegConnected.equals("true"))
//				setButtonText(R.id.buttonConnectEEG, "Disconnect EEG");
//			else
//				setButtonText(R.id.buttonConnectEEG, "Connect EEG");


			updatePower();



		}

	};

	// ################################################################

	private BroadcastReceiver mEventReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

//			String action = intent.getAction();

			String name = intent.getStringExtra("name");
			String value = intent.getStringExtra("value");

			switch(name) {

				case "eegStatus":

					switch(value) {
						case "STATE_CONNECTING":
							updateStatusImage();
//							setButtonText(io.puzzlebox.jigsaw.R.id.buttonConnectEEG, getResources().getString(io.puzzlebox.jigsaw.R.string.buttonStatusNeuroSkyMindWaveConnecting));
							break;
						case "STATE_CONNECTED":
//							Toast.makeText(context, "EEG Connected", Toast.LENGTH_SHORT).show();
							updateStatusImage();
//							setButtonText(io.puzzlebox.jigsaw.R.id.buttonConnectEEG, getResources().getString(io.puzzlebox.jigsaw.R.string.buttonStatusNeuroSkyMindWaveDisconnect));
//							buttonDeviceEnable.setEnabled(true);
//							buttonDeviceEnable.setVisibility(View.VISIBLE);
							break;
						case "STATE_NOT_FOUND":
							Toast.makeText(context, "EEG Not Found", Toast.LENGTH_SHORT).show();
							updateStatusImage();
//							setButtonText(io.puzzlebox.jigsaw.R.id.buttonConnectEEG, getResources().getString(io.puzzlebox.jigsaw.R.string.buttonStatusNeuroSkyMindWaveConnect));
							buttonDeviceEnable.setEnabled(false);
							buttonDeviceEnable.setVisibility(View.INVISIBLE);
							break;
						case "STATE_NOT_PAIRED":
							Toast.makeText(context, "EEG Not Paired", Toast.LENGTH_SHORT).show();
							updateStatusImage();
//							setButtonText(io.puzzlebox.jigsaw.R.id.buttonConnectEEG, getResources().getString(io.puzzlebox.jigsaw.R.string.buttonStatusNeuroSkyMindWaveConnect));
							buttonDeviceEnable.setEnabled(false);
							buttonDeviceEnable.setVisibility(View.INVISIBLE);
							break;
						case "STATE_DISCONNECTED":
//							Toast.makeText(context, "EEG Disconnected", Toast.LENGTH_SHORT).show();
							updateStatusImage();
//							setButtonText(io.puzzlebox.jigsaw.R.id.buttonConnectEEG, getResources().getString(io.puzzlebox.jigsaw.R.string.buttonStatusNeuroSkyMindWaveConnect));
							buttonDeviceEnable.setEnabled(false);
							buttonDeviceEnable.setVisibility(View.INVISIBLE);
							break;
						case "MSG_LOW_BATTERY":
//							Toast.makeText(context, "EEG Battery Low", Toast.LENGTH_SHORT).show();
							Toast.makeText(context, io.puzzlebox.jigsaw.R.string.buttonStatusNeuroSkyMindWaveBatteryLow, Toast.LENGTH_SHORT).show();
							updateStatusImage();
							break;
					}

					break;

				case "eegBlink":
					Log.d(TAG, "Blink: " + value + "\n");
//					if (Integer.parseInt(value) > ThinkGearService.blinkRangeMax) {
//						value = "" + ThinkGearService.blinkRangeMax;
//					}
//					try {
//						progressBarBlink.setProgress(Integer.parseInt(value));
//					} catch (NumberFormatException e) {
//						e.printStackTrace();
//					}
					break;

			}

		}

	};

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
//
		if(eegPower > 0) {
			imageViewStatus.setImageResource(R.drawable.status_4_active);
			return;
		}

		if(eegSignal > 90) {
			imageViewStatus.setImageResource(R.drawable.status_3_processing);
			return;
		}

		if(eegConnected) {
			imageViewStatus.setImageResource(R.drawable.status_2_connected);
			return;
		}

		if(eegConnecting) {
			imageViewStatus.setImageResource(R.drawable.status_1_connecting);
			return;
		} else {
			imageViewStatus.setImageResource(R.drawable.status_default);
			return;
		}

	} // updateStatusImage


	// ################################################################

	// TODO
//	@Override
//	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//
//		menu.add("Share")
//				  .setOnMenuItemClickListener(this.mShareButtonClickListener)
//				  .setIcon(android.R.drawable.ic_menu_share)
//				  .setShowAsAction(SHOW_AS_ACTION_ALWAYS);
//
//		super.onCreateOptionsMenu(menu, inflater);
//
//	}


	// ################################################################

	// TODO
//	MenuItem.OnMenuItemClickListener mShareButtonClickListener = new MenuItem.OnMenuItemClickListener() {
//
//		@Override
//		public boolean onMenuItemClick(MenuItem item) {
//
//			Intent i = SessionSingleton.getInstance().getExportSessionIntent(getActivity().getApplicationContext(), item);
//
//			if (i != null) {
//				startActivity(i);
//			} else {
//				Toast.makeText(getActivity().getApplicationContext(), "Error export session data for sharing", Toast.LENGTH_SHORT).show();
//			}
//
//			return false;
//		}
//	};


	// ################################################################


	// ################################################################

	// TODO
//	private void setOrbitActivate() {
//
//		Log.d(TAG, "setOrbitActivate");
//
//		if (! OrbitSingleton.getInstance().orbitActive) {
////			getActivity().startService(intentThinkGear);
//			OrbitSingleton.getInstance().orbitActive = true;
//		} else {
//			setOrbitDeactivate();
//		}
//
////		OrbitSingleton.getInstance().flag = true;
////		OrbitSingleton.getInstance().connState = true;
////
////		servoSeekBar.setEnabled(OrbitSingleton.getInstance().flag);
////		connectBloom.setText("Disconnect Bloom");
////
////		buttonDemo.setEnabled(true);
//		buttonConnectOrbit.setEnabled(true);
//	}


	// ################################################################

	// TODO
//	private void setOrbitDeactivate() {
//
//		Log.d(TAG, "setOrbitDeactivate");
//
//		OrbitSingleton.getInstance().orbitActive = false;
//
////		OrbitSingleton.getInstance().flag = false;
////		OrbitSingleton.getInstance().connState = false;
////
////		servoSeekBar.setEnabled(OrbitSingleton.getInstance().flag);
////		connectBloom.setText("Connect Bloom");
////
////		buttonDemo.setEnabled(false);
//
//		buttonConnectOrbit.setEnabled(false);
////
////		progressBarRange.setProgress(0);
//	}


	// ################################################################


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

		if (eegConnected) {

			if (eegSignal < 100) {
				ThinkGearService.eegAttention = 0;
				ThinkGearService.eegMeditation = 0;
				progressBarAttention.setProgress(ThinkGearService.eegAttention);
				progressBarMeditation.setProgress(ThinkGearService.eegMeditation);
			}

			ThinkGearService.eegPower = calculateSpeed();
			eegPower = ThinkGearService.eegPower;

			progressBarPower.setProgress(ThinkGearService.eegPower);


		}


		// TODO
//		if (MuseService.eegConnected) {
//
////			Log.d(TAG, "MuseService.eegConnected: eegSignal: " + MuseService.eegSignal);
////			if (MuseService.eegSignal < 100) {
////				MuseService.eegConcentration = 0;
////				MuseService.eegMellow = 0;
////				progressBarAttention.setProgress(MuseService.eegConcentration);
////				progressBarMeditation.setProgress(MuseService.eegMellow);
////			}
//
//			MuseService.eegPower = calculateSpeed();
//
//			progressBarPower.setProgress(MuseService.eegPower);
//			eegPower = MuseService.eegPower;
//
//
//		}


		OrbitSingleton.getInstance().eegPower = eegPower;


		if (eegPower > 0) {

			/** Start playback of audio control stream */
			if (!OrbitSingleton.getInstance().flightActive) {
				playControl();
			}

			updateScore();

			OrbitSingleton.getInstance().flightActive = true;

		} else {

			/** Land the helicopter */
			if (! OrbitSingleton.getInstance().demoActive )
				stopControl();

			resetCurrentScore();

		}

		Log.d(TAG, "flightActive: " + OrbitSingleton.getInstance().flightActive);



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
				  (eegAttentionTarget > OrbitSingleton.getInstance().minimumScoreTarget))
			eegAttentionScore = eegAttentionTarget - OrbitSingleton.getInstance().minimumScoreTarget;

		if ((eegMeditation >= eegMeditationTarget) &&
				  (eegMeditationTarget > OrbitSingleton.getInstance().minimumScoreTarget))
			eegMeditationScore = eegMeditationTarget - OrbitSingleton.getInstance().minimumScoreTarget;

		if (eegAttentionScore > eegMeditationScore)
			OrbitSingleton.getInstance().scoreCurrent = OrbitSingleton.getInstance().scoreCurrent + eegAttentionScore;
		else
			OrbitSingleton.getInstance().scoreCurrent = OrbitSingleton.getInstance().scoreCurrent + eegMeditationScore;

		textViewScore.setText(Integer.toString(OrbitSingleton.getInstance().scoreCurrent));

		if (OrbitSingleton.getInstance().scoreCurrent > OrbitSingleton.getInstance().scoreHigh) {
			OrbitSingleton.getInstance().scoreHigh = OrbitSingleton.getInstance().scoreCurrent;
			textViewHighScore.setText(Integer.toString(OrbitSingleton.getInstance().scoreHigh));
		}


		// Catch anyone gaming the system with one slider
		// below the minimum threshold and the other over.
		// For example, setting Meditation to 1% will keep helicopter
		// activated even if Attention is below target
		if ((eegAttention < eegAttentionTarget) && (eegMeditation < OrbitSingleton.getInstance().minimumScoreTarget))
			resetCurrentScore();
		if ((eegMeditation < eegMeditationTarget) && (eegAttention < OrbitSingleton.getInstance().minimumScoreTarget))
			resetCurrentScore();
		if ((eegAttention < OrbitSingleton.getInstance().minimumScoreTarget) && (eegMeditation < OrbitSingleton.getInstance().minimumScoreTarget))
			resetCurrentScore();


	} // updateScore


	// ################################################################

	public void resetCurrentScore() {

		if (OrbitSingleton.getInstance().scoreCurrent > 0)
			textViewLastScore.setText(Integer.toString(OrbitSingleton.getInstance().scoreCurrent));
		OrbitSingleton.getInstance().scoreCurrent = 0;
		textViewScore.setText(Integer.toString(OrbitSingleton.getInstance().scoreCurrent));

	} // resetCurrentScore


	// ################################################################


	// ################################################################

	public void playControl() {

		Log.d(TAG, "playControl()");


		// TODO Convert to service

//		FragmentTabAdvanced fragmentAdvanced =
//				  (FragmentTabAdvanced) getActivity().getSupportFragmentManager().findFragmentByTag( getTabFragmentAdvanced() );
//
		if (OrbitSingleton.getInstance().generateAudio) {

			/**
			 * Generate signal on the fly
			 */

//			// Handle controlled descent thread if activated
//			if ((fragmentAdvanced.orbitControlledDescentTask != null) &&
//					  (fragmentAdvanced.orbitControlledDescentTask.keepDescending)) {
//				fragmentAdvanced.orbitControlledDescentTask.callStopAudio = false;
//				fragmentAdvanced.orbitControlledDescentTask.keepDescending = false;
//			}


			//			if (audioHandler != null) {

			//				serviceBinder.ifFlip = fragmentAdvanced.checkBoxInvertControlSignal.isChecked(); // if checked then flip
			OrbitSingleton.getInstance().audioHandler.ifFlip = OrbitSingleton.getInstance().invertControlSignal; // if checked then flip

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

			OrbitSingleton.getInstance().audioHandler.mutexNotify();

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
			if (OrbitSingleton.getInstance().loaded) {
				//				soundPool.play(soundID, volume, volume, 1, 0, 1f);
				//				soundPool.setVolume(soundID, 1f, 1f);
				//				soundPool.play(soundID, maxVolume, maxVolume, 1, 0, 1f); // Fixes Samsung Galaxy S4 [SGH-M919]

				OrbitSingleton.getInstance().soundPool.play(OrbitSingleton.getInstance().soundID, 1f, 1f, 1, 0, 1f); // Fixes Samsung Galaxy S4 [SGH-M919]

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
//				  (audioHandler != null)) {
//
//			fragmentAdvanced.registerControlledDescent();
//
//		} else {
//
		stopAudio();
//
//		}
//
		OrbitSingleton.getInstance().flightActive = false;


	} // stopControl


	// ################################################################

	public void stopAudio() {

		/**
		 * stop AudioTrack as well as destroy service.
		 */

		OrbitSingleton.getInstance().audioHandler.keepPlaying = false;

		/**
		 * Stop playing audio control file
		 */

		if (OrbitSingleton.getInstance().soundPool != null) {
			try {
				OrbitSingleton.getInstance().soundPool.stop(OrbitSingleton.getInstance().soundID);
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



		if (! OrbitSingleton.getInstance().flightActive) {


//		demoFlightMode = true;
			OrbitSingleton.getInstance().flightActive = true;
			OrbitSingleton.getInstance().demoActive = true;
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

			OrbitSingleton.getInstance().flightActive = false;
			OrbitSingleton.getInstance().demoActive = false;

			stopControl();

			buttonTestFlight.setText(getResources().getString(R.string.button_test_fly));

		}


	} // demoMode


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
	 * the audioHandler to update command
	 */
	public void updateAudioHandlerCommand(Integer[] command) {

//		this.audioHandler.command = command;
//		this.audioHandler.updateControlSignal();
		OrbitSingleton.getInstance().audioHandler.command = command;
		OrbitSingleton.getInstance().audioHandler.updateControlSignal();


	} // updateServiceBinderCommand


	// ################################################################

	/**
	 * the audioHandler to update channel
	 */
	public void updateAudioHandlerChannel(int channel) {

//		this.audioHandler.channel = channel;
//		this.audioHandler.updateControlSignal();
		OrbitSingleton.getInstance().audioHandler.channel = channel;
		OrbitSingleton.getInstance().audioHandler.updateControlSignal();


	} // updateServiceBinderChannel


	// ################################################################

	/**
	 * @param number the audioHandler to update loop number while mind control
	 */
	public void updateAudioHandlerLoopNumberWhileMindControl(int number) {

//		this.audioHandler.loopNumberWhileMindControl = number;
		OrbitSingleton.getInstance().audioHandler.loopNumberWhileMindControl = number;


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
