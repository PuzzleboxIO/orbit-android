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
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import io.puzzlebox.jigsaw.data.ProfileSingleton;
import io.puzzlebox.jigsaw.protocol.ThinkGearService;
import io.puzzlebox.jigsaw.ui.DialogOutputAudioIRFragment;
import io.puzzlebox.jigsaw.ui.JoystickView;
import io.puzzlebox.orbit.R;
import io.puzzlebox.orbit.data.OrbitSingleton;

import static io.puzzlebox.jigsaw.protocol.ThinkGearService.eegConnected;
import static io.puzzlebox.jigsaw.protocol.ThinkGearService.eegConnecting;
import static io.puzzlebox.jigsaw.protocol.ThinkGearService.eegSignal;

public class DialogProfilePuzzleboxOrbitJoystickMindwaveFragment extends DialogFragment
		  implements SeekBar.OnSeekBarChangeListener {

	private final static String TAG = DialogProfilePuzzleboxOrbitJoystickMindwaveFragment.class.getSimpleName();

	public final static String profileID = "profile_puzzlebox_orbit_joystick_mindwave";

	// UI
	Button buttonDeviceEnable;

	ProgressBar progressBarAttention;
	SeekBar seekBarAttention;
	ProgressBar progressBarMeditation;
	SeekBar seekBarMeditation;
	ProgressBar progressBarSignal;
	ProgressBar progressBarPower;

	public Switch switchThrottlePitch;

	public SeekBar seekBarThrottle;
	public SeekBar seekBarYaw;
	public SeekBar seekBarPitch;

	TextView textViewScore;
	TextView textViewLastScore;
	TextView textViewHighScore;

	ImageView imageViewStatus;

	/**
	 * Configuration
	 */
	public int eegPower = 0;
	int[] thresholdValuesAttention = new int[101];
	int[] thresholdValuesMeditation = new int[101];
	int minimumPower = 0; // minimum power for the Orbit
	int maximumPower = 100; // maximum power for the Orbit

	private OnFragmentInteractionListener mListener;

	public DialogProfilePuzzleboxOrbitJoystickMindwaveFragment() {
		// Required empty public constructor
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
									 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View v = inflater.inflate(R.layout.dialog_profile_puzzlebox_orbit_joystick_mindwave, container, false);

		getDialog().getWindow().setTitle( getString(R.string.title_dialog_fragment_puzzlebox_orbit_joystick_mindwave));

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

		seekBarAttention = (SeekBar) v.findViewById(R.id.seekBarAttention);
		seekBarAttention.setOnSeekBarChangeListener(this);
		seekBarMeditation = (SeekBar) v.findViewById(R.id.seekBarMeditation);
		seekBarMeditation.setOnSeekBarChangeListener(this);

		imageViewStatus = (ImageView) v.findViewById(R.id.imageViewStatus);

		textViewScore = (TextView) v.findViewById(R.id.textViewScore);
		textViewLastScore = (TextView) v.findViewById(R.id.textViewLastScore);
		textViewHighScore = (TextView) v.findViewById(R.id.textViewHighScore);

		switchThrottlePitch = (Switch) v.findViewById(R.id.switchThrottlePitch);
		switchThrottlePitch.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.e(TAG, "switchThrottlePitch.onClick(): " + switchThrottlePitch.isChecked());
				onSwitchClicked(switchThrottlePitch.isChecked());
			}
		});

		seekBarThrottle = (SeekBar) v.findViewById(R.id.seekBarThrottle);
		seekBarThrottle.setProgress(OrbitSingleton.getInstance().defaultControlThrottle);
		seekBarThrottle.setOnSeekBarChangeListener(this);

		seekBarYaw = (SeekBar) v.findViewById(R.id.seekBarYaw);
		seekBarYaw.setProgress(OrbitSingleton.getInstance().defaultControlYaw);
		seekBarYaw.setOnSeekBarChangeListener(this);

		seekBarPitch = (SeekBar) v.findViewById(R.id.seekBarPitch);
		seekBarPitch.setProgress(OrbitSingleton.getInstance().defaultControlPitch);

		JoystickView joystickView = (JoystickView) v.findViewById(R.id.joystickView);
		joystickView.setOnMoveListener(onMoveJoystick);


		Button buttonDeviceCancel = (Button) v.findViewById(R.id.buttonDeviceCancel);
		buttonDeviceCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});

		buttonDeviceEnable = (Button) v.findViewById(R.id.buttonDeviceEnable);
		buttonDeviceEnable.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});

		/**
		 * AudioHandler
		 */
//		if (!OrbitSingleton.getInstance().audioHandler.isAlive()) {
//			OrbitSingleton.getInstance().audioHandler.start();
//		}
		OrbitSingleton.getInstance().startAudioHandler();

		updatePowerThresholds();

		updateControlSignal();

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

		stopControl();

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
		window.setLayout((int)(size.x *0.98), WindowManager.LayoutParams.WRAP_CONTENT);

		window.setGravity(Gravity.CENTER);

		super.onResume();

		if (ProfileSingleton.getInstance().getValue(DialogOutputAudioIRFragment.profileID, "active").equals("true")) {
			playControl();
		} else {
			Toast.makeText(getActivity().getApplicationContext(), getString(R.string.toast_puzzlebox_orbit_joystick_audio_ir_warning), Toast.LENGTH_LONG).show();
		}

		updatePowerThresholds();
		updatePower();

		LocalBroadcastManager.getInstance(getActivity().getApplicationContext()).registerReceiver(
				  mPacketReceiver, new IntentFilter("io.puzzlebox.jigsaw.protocol.thinkgear.packet"));

		LocalBroadcastManager.getInstance(getActivity().getApplicationContext()).registerReceiver(
				  mEventReceiver, new IntentFilter("io.puzzlebox.jigsaw.protocol.thinkgear.event"));


	}


	// ################################################################

	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {

		updatePowerThresholds();

		updateControlSignal();

	} // onProgressChanged

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {

	}

	// ################################################################

	public void onSwitchClicked(Boolean activated) {

		if (! activated) {
			switchThrottlePitch.setText(R.string.label_puzzlebox_orbit_joystick_mindwave_switch_text);
		} else {
			switchThrottlePitch.setText(R.string.label_puzzlebox_orbit_joystick_mindwave_switch_text_alt);
		}

	}


	// ################################################################

	private JoystickView.OnMoveListener onMoveJoystick = new JoystickView.OnMoveListener(){
		public void onMove(int angle, int strength) {
			Log.v(TAG, "onMoveJoystick(int angle, int strength): " + angle + ", " + strength);

			if ((angle == 0) && (strength == 0)) {
				// Home position or no touch on joystick

//				seekBarX.setProgress(OrbitSingleton.getInstance().defaultJoystickYaw);
//
//				if (! switchThrottlePitch.isChecked())
//					seekBarY.setProgress(OrbitSingleton.getInstance().defaultJoystickThrottle);
//				else
//					seekBarY.setProgress(OrbitSingleton.getInstance().defaultJoystickPitch);


				if (! switchThrottlePitch.isChecked()) {
					seekBarThrottle.setProgress(OrbitSingleton.getInstance().defaultJoystickThrottle);
//					seekBarYaw.setProgress(OrbitSingleton.getInstance().defaultJoystickYaw);
//					seekBarThrottle.setProgress(OrbitSingleton.getInstance().defaultControlThrottle);
					seekBarYaw.setProgress(OrbitSingleton.getInstance().defaultControlYaw);
				} else {
//					seekBarPitch.setProgress(OrbitSingleton.getInstance().defaultJoystickPitch);
//					seekBarYaw.setProgress(OrbitSingleton.getInstance().defaultJoystickYaw);
					seekBarPitch.setProgress(OrbitSingleton.getInstance().defaultControlPitch);
					seekBarYaw.setProgress(OrbitSingleton.getInstance().defaultControlYaw);
				}

			}
//			else if ((angle >= 0) && (angle <= 180)) {
			else if ((angle >= 30) && (angle <= 150)) {
				// Up

				if (! switchThrottlePitch.isChecked()) {
					// Y Axis of joystick controls Throttle

					// Ensure lower half of seekBarThrottle can be accessed from the top half of throttle joystick
					int newY = (int) (seekBarThrottle.getMax() * (strength / 100.0));

					// Set a minimum about of throttle to send if anywhere above zero level
					// of Orbit. Normally it takes some small amount of throttle to trigger
					// any flight or visible reaction.
					if (newY < OrbitSingleton.getInstance().minimumJoystickThrottle)
						newY = OrbitSingleton.getInstance().minimumJoystickThrottle;

					seekBarThrottle.setProgress(newY);

				} else {
					// Y Axis of joystick controls throttle

					int newY = seekBarPitch.getMax() / 2;
					newY = (int) (newY * (strength / 100.0));
					newY = seekBarPitch.getMax() / 2 + newY;
					seekBarPitch.setProgress(newY);

				}

			}
//			else if ((angle >= 180) && (angle <= 359)) {
			else if ((angle >= 210) && (angle <= 330)) {
				// Down

				if (! switchThrottlePitch.isChecked()) {
					// Y Axis of joystick controls Throttle

					seekBarThrottle.setProgress(0);

				} else {
					// Y Axis of joystick controls throttle

					int newY = seekBarPitch.getMax() / 2;
					newY = (int) (newY * (strength / 100.0));
					newY = seekBarPitch.getMax() / 2 - newY;
					seekBarPitch.setProgress(newY);
				}

			}
			if ((angle >= 150) && (angle <= 210)) {
				// Left
				int newX = seekBarYaw.getMax() / 2;
				newX = (int) (newX * (strength / 100.0));
				newX = seekBarYaw.getMax() / 2 - newX;
				seekBarYaw.setProgress(newX);
			}
			else if ((angle >= 330) || (angle <= 30)) {
				// Right
				int newX = seekBarYaw.getMax() / 2;
				newX = (int) (newX * (strength / 100.0));
				newX = seekBarYaw.getMax() / 2 + newX;
				seekBarYaw.setProgress(newX);
			}

			updateControlSignal();

		}
	};


	// ################################################################

	public void updateControlSignal() {

		// We subtract the current Yaw position from the maximum slider value
		// because smaller values instruct the helicopter to spin to the right
		// (clockwise if looking down from above) whereas intuitively moving
		// the slider to the left should cause it to spin left

//		Integer[] command = {
//				  seekBarThrottle.getProgress(),
//				  seekBarYaw.getMax() - seekBarYaw.getProgress(),
//				  seekBarPitch.getProgress(),
//				  1};

//		Integer[] command = {
//				  OrbitSingleton.getInstance().defaultJoystickThrottle,
//				  OrbitSingleton.getInstance().defaultJoystickYaw,
//				  OrbitSingleton.getInstance().defaultJoystickPitch
//		};

//		if (! switchThrottlePitch.isChecked()) {
//
//			Integer[] command = {
//					  OrbitSingleton.getInstance().defaultJoystickThrottle,
//					  seekBarX.getMax() - seekBarX.getProgress(),
//					  seekBarY.getProgress(),
//					  1};
//
//			OrbitSingleton.getInstance().audioHandler.command = command;
//
//		} else {
//			Integer[] command = {
//					  seekBarY.getProgress(),
//					  seekBarX.getMax() - seekBarX.getProgress(),
//					  OrbitSingleton.getInstance().defaultJoystickPitch,
//					  1};
//
//			OrbitSingleton.getInstance().audioHandler.command = command;
//
//		}



		Integer[] command =  {
				  seekBarThrottle.getProgress(),
				  seekBarYaw.getMax() - seekBarYaw.getProgress(),
				  seekBarPitch.getProgress(),
				  OrbitSingleton.getInstance().defaultChannel};


		// Transmit zero Throttle power if not about EEG power threashold
		if (eegPower <= 0) {
			Log.e(TAG, "(eegPower <= 0)");
			command[0] = 0;
		}


		OrbitSingleton.getInstance().audioHandler.command = command;

		OrbitSingleton.getInstance().audioHandler.updateControlSignal();

	} // updateControlSignal


	// ################################################################

	/**
	 * @param number the audioHandler to update loop number while mind control
	 */
	public void updateAudioHandlerLoopNumberWhileMindControl(int number) {

//		this.audioHandler.loopNumberWhileMindControl = number;
		OrbitSingleton.getInstance().audioHandler.loopNumberWhileMindControl = number;


	} // updateServiceBinderLoopNumberWhileMindControl


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

	public void playControl() {

//		Log.d(TAG, "playControl()");

		OrbitSingleton.getInstance().flightActive = true;

		OrbitSingleton.getInstance().audioHandler.ifFlip = OrbitSingleton.getInstance().invertControlSignal; // if checked then flip

//		int channel = OrbitSingleton.getInstance().defaultChannel;

		updateAudioHandlerLoopNumberWhileMindControl(-1); // Loop infinite for easier user testing

//		updateAudioHandlerChannel(channel);
		updateAudioHandlerChannel(OrbitSingleton.getInstance().defaultChannel);

		OrbitSingleton.getInstance().audioHandler.mutexNotify();

	} // playControl


	// ################################################################

	public void stopControl() {

//		Log.d(TAG, "stopControl()");

		stopAudio();

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

	private BroadcastReceiver mPacketReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

			int eegAttention = Integer.valueOf(intent.getStringExtra("Attention"));
			int eegMeditation = Integer.valueOf(intent.getStringExtra("Meditation"));
			int eegSignal = Integer.valueOf(intent.getStringExtra("Signal Level"));

			progressBarAttention.setProgress(eegAttention);
			progressBarMeditation.setProgress(eegMeditation);
			progressBarSignal.setProgress(eegSignal);

			updateStatusImage();

			updatePower();

		}

	};

	// ################################################################

	private BroadcastReceiver mEventReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

			String name = intent.getStringExtra("name");
			String value = intent.getStringExtra("value");

			switch(name) {

				case "eegStatus":

					switch(value) {
						case "STATE_CONNECTING":
							updateStatusImage();
							break;
						case "STATE_CONNECTED":
							updateStatusImage();
							break;
						case "STATE_NOT_FOUND":
							Toast.makeText(context, "EEG Not Found", Toast.LENGTH_SHORT).show();
							updateStatusImage();
							buttonDeviceEnable.setEnabled(false);
							buttonDeviceEnable.setVisibility(View.INVISIBLE);
							break;
						case "STATE_NOT_PAIRED":
							Toast.makeText(context, "EEG Not Paired", Toast.LENGTH_SHORT).show();
							updateStatusImage();
							buttonDeviceEnable.setEnabled(false);
							buttonDeviceEnable.setVisibility(View.INVISIBLE);
							break;
						case "STATE_DISCONNECTED":
							Toast.makeText(context, "EEG Disconnected", Toast.LENGTH_SHORT).show();
							updateStatusImage();
							buttonDeviceEnable.setEnabled(false);
							buttonDeviceEnable.setVisibility(View.INVISIBLE);
							break;
						case "MSG_LOW_BATTERY":
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
//			if (! OrbitSingleton.getInstance().demoActive ) {
////				stopControl();
//				updateControlSignal();
//			}

			resetCurrentScore();

		}

		updateControlSignal();

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


}
