package io.puzzlebox.orbit.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Toast;

import java.util.List;

import io.puzzlebox.orbit.R;
import io.puzzlebox.jigsaw.ui.JoystickView;
import io.puzzlebox.orbit.data.OrbitSingleton;

public class SteeringFragment extends Fragment
		  implements View.OnClickListener,
		  SeekBar.OnSeekBarChangeListener,
		  SensorEventListener
{

	private final static String TAG = SteeringFragment.class.getSimpleName();

	ProgressBar progressBarAttention;
	ProgressBar progressBarMeditation;
	ProgressBar progressBarSignal;
	ProgressBar progressBarPower;

	public SeekBar seekBarThrottle;
	public SeekBar seekBarYaw;
	public SeekBar seekBarPitch;

	Button buttonHover;
	Button buttonForward;
	Button buttonLeft;
	Button buttonRight;

	CheckBox checkBoxTiltSensorControl;
	CheckBox checkBoxTiltSensorControlThrottle;

//	private JoystickView joystickView;

	private SensorManager sensorManager;
	private Sensor orientationSensor = null;

	private OnFragmentInteractionListener mListener;

	public SteeringFragment() {
		// Required empty public constructor
	}

	public static SteeringFragment newInstance(String param1, String param2) {
		SteeringFragment fragment = new SteeringFragment();
		Bundle args = new Bundle();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container,
//									 Bundle savedInstanceState) {
//		// Inflate the layout for this fragment
//		return inflater.inflate(R.layout.fragment_steering, container, false);
//	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
									 Bundle savedInstanceState) {


//		getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);


		// Inflate the layout for this fragment
		View v = inflater.inflate(io.puzzlebox.orbit.R.layout.fragment_steering, container, false);


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


		seekBarThrottle = (SeekBar) v.findViewById(R.id.seekBarThrottle);
		seekBarThrottle.setOnSeekBarChangeListener(this);

		seekBarYaw = (SeekBar) v.findViewById(R.id.seekBarYaw);
		seekBarYaw.setOnSeekBarChangeListener(this);

		seekBarPitch = (SeekBar) v.findViewById(R.id.seekBarPitch);
		seekBarPitch.setOnSeekBarChangeListener(this);


//		Button buttonHover = (Button) v.findViewById(R.id.buttonHover);
		buttonHover = (Button) v.findViewById(R.id.buttonHover);
		buttonHover.setOnClickListener(this);

//		Button buttonForward = (Button) v.findViewById(R.id.buttonForward);
		buttonForward = (Button) v.findViewById(R.id.buttonForward);
		buttonForward.setOnClickListener(this);

//		Button buttonLeft = (Button) v.findViewById(R.id.buttonLeft);
		buttonLeft = (Button) v.findViewById(R.id.buttonLeft);
		buttonLeft.setOnClickListener(this);

//		Button buttonRight = (Button) v.findViewById(R.id.buttonRight);
		buttonRight = (Button) v.findViewById(R.id.buttonRight);
		buttonRight.setOnClickListener(this);


		checkBoxTiltSensorControl = (CheckBox) v.findViewById(R.id.checkBoxTiltSensorControl);
		checkBoxTiltSensorControl.setOnClickListener(this);
//		checkBoxTiltSensorControl.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				onCheckBoxTiltSensorControlClicked(v);
//			}
//		});

		checkBoxTiltSensorControlThrottle = (CheckBox) v.findViewById(R.id.checkBoxTiltSensorControlThrottle);
		checkBoxTiltSensorControlThrottle.setOnClickListener(this);
//		checkBoxTiltSensorControlThrottle.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				checkBoxTiltSensorControlThrottleClicked(v);
//			}
//		});

		checkBoxTiltSensorControlThrottle.setVisibility(View.GONE); // Default should be hidden until Tilt Control is activated


		JoystickView joystick = (JoystickView) v.findViewById(io.puzzlebox.orbit.R.id.joystickViewSteering);
		joystick.setOnMoveListener(onMoveJoystick);


		return v;
	}


// ################################################################

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

	@Override
	public void onResume() {

//		updateAdvancedOptions();
		onCheckBoxTiltSensorControlClicked();

		super.onResume();


		LocalBroadcastManager.getInstance(getActivity().getApplicationContext()).registerReceiver(
				  mPacketReceiver, new IntentFilter("io.puzzlebox.jigsaw.protocol.thinkgear.packet"));


	}


	// ################################################################

	public void onPause() {

		Log.v(TAG, "onPause()");

		if (sensorManager != null)
			sensorManager.unregisterListener(this);

		super.onPause();


		LocalBroadcastManager.getInstance(
				  getActivity().getApplicationContext()).unregisterReceiver(
				  mPacketReceiver);


	} // onPause


// ################################################################

	public void onClick(View v) {

		switch (v.getId()) {

			case R.id.buttonHover:
				setControlSignalHover(v);
				break;
			case R.id.buttonForward:
				setControlSignalForward(v);
				break;
			case R.id.buttonLeft:
				setControlSignalLeft(v);
				break;
			case R.id.buttonRight:
				setControlSignalRight(v);
				break;
//			case R.id.checkBoxGenerateAudio:
//				onCheckBoxGenerateAudioClicked(v);
//				break;
//			case R.id.checkBoxInvertControlSignal:
//				onCheckBoxInvertControlSignalClicked(v);
//				break;
			case R.id.checkBoxTiltSensorControl:
				onCheckBoxTiltSensorControlClicked(v);
				break;
			case R.id.checkBoxTiltSensorControlThrottle:
				checkBoxTiltSensorControlThrottleClicked(v);
				break;
//			case R.id.checkBoxControlledDescent:
//				checkBoxControlledDescentClicked(v);
//				break;


		}

	} // onClick


	// ################################################################

	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {

		// Abort controlled descent thread if activated and slide adjusted by human touch
//		if ((fromTouch) &&
//				  (orbitControlledDescentTask != null) &&
//				  (orbitControlledDescentTask.keepDescending)) {
//			//			orbitControlledDescentTask.callStopAudio = false;
//			orbitControlledDescentTask.resetThrottleToPrevious = false;
//			orbitControlledDescentTask.keepDescending = false;
//		}

		updateControlSignal();


	} // onProgressChanged


	// ################################################################

	private JoystickView.OnMoveListener onMoveJoystick = new JoystickView.OnMoveListener(){
		public void onMove(int angle, int strength) {
			Log.v(TAG, "onMoveJoystick(int angle, int strength): " + angle + ", " + strength);

//			String command = moveDualShock4(angle, strength);
//			Log.v(TAG, "moveDualShock4(): command: \"" + command + "\"");
////			broadcastCommandBluetooth("joystick", "ls: " + angle + "," + strength);
//			broadcastCommandBluetooth("joystick", command);


			if ((angle >= 60) && (angle <= 120)) {
				// Up
				int newThrottle = seekBarThrottle.getMax() - OrbitSingleton.getInstance().defaultControlThrottle;
				newThrottle = (int) (newThrottle * (strength / 100.0));
				newThrottle = OrbitSingleton.getInstance().defaultControlThrottle + newThrottle;
				seekBarThrottle.setProgress(newThrottle);
			}
			else if ((angle >= 240) && (angle <= 300)) {
				// Down
				int newThrottle = OrbitSingleton.getInstance().defaultControlThrottle;
				newThrottle = (int) (newThrottle * (strength / 100.0));
				newThrottle = OrbitSingleton.getInstance().defaultControlThrottle - newThrottle;
				seekBarThrottle.setProgress(newThrottle);
			}

		}
	};


	// ################################################################

	private BroadcastReceiver mPacketReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

			int eegAttention = Integer.valueOf(intent.getStringExtra("Attention"));
			int eegMeditation = Integer.valueOf(intent.getStringExtra("Meditation"));
			int eegSignal = Integer.valueOf(intent.getStringExtra("Signal Level"));
//			int eegPower = Integer.valueOf(intent.getStringExtra("Power"));

//			Log.e(TAG, "eegAttention: " + eegAttention);

			progressBarAttention.setProgress(eegAttention);
			progressBarMeditation.setProgress(eegMeditation);
			progressBarSignal.setProgress(eegSignal);
//			progressBarPower.setProgress(eegPower);
			progressBarPower.setProgress(OrbitSingleton.getInstance().eegPower);

//			updatePower();


//			updateSessionTime();




		}

	};


	// ################################################################

	public void onStartTrackingTouch(SeekBar seekBar) {

		/**
		 * Method required by SeekBar.OnSeekBarChangeListener
		 */

	} // onStartTrackingTouch


	// ################################################################

	public void onStopTrackingTouch(SeekBar seekBar) {

		/*
		  Update control signal after moving a seekBar.
		 */

		Log.v(TAG, "onStopTrackingTouch()");

		updateControlSignal();


	} // onStopTrackingTouch




	// ################################################################

	public void updateControlSignal() {
//	public void updateControlSignal(View v) {

		// We subtract the current Yaw position from the maximum slider value
		// because smaller values instruct the helicopter to spin to the right
		// (clockwise if looking down from above) whereas intuitively moving
		// the slider to the left should cause it to spin left
		Integer[] command =  {
				  seekBarThrottle.getProgress(),
				  seekBarYaw.getMax() - seekBarYaw.getProgress(),
				  seekBarPitch.getProgress(),
				  1};


//		((OrbitTabActivity)getActivity()).updateAudioHandlerCommand(command);

		OrbitSingleton.getInstance().audioHandler.command = command;
		OrbitSingleton.getInstance().audioHandler.updateControlSignal();


	} // updateControlSignal


	// ################################################################

	public void resetControlSignal(View v) {

		seekBarThrottle.setProgress(OrbitSingleton.getInstance().defaultControlThrottle);
		seekBarYaw.setProgress(OrbitSingleton.getInstance().defaultControlYaw);
		seekBarPitch.setProgress(OrbitSingleton.getInstance().defaultControlPitch);

		updateControlSignal();


	} // resetControlSignal


	// ################################################################

	public void setControlSignalHover(View v) {

		seekBarThrottle.setProgress(OrbitSingleton.getInstance().hoverControlThrottle);
		seekBarYaw.setProgress(OrbitSingleton.getInstance().hoverControlYaw);
		seekBarPitch.setProgress(OrbitSingleton.getInstance().hoverControlPitch);

		updateControlSignal();


	} // setControlSignalHover


	// ################################################################

	public void setControlSignalForward(View v) {

		seekBarThrottle.setProgress(OrbitSingleton.getInstance().forwardControlThrottle);
		seekBarYaw.setProgress(OrbitSingleton.getInstance().forwardControlYaw);
		seekBarPitch.setProgress(OrbitSingleton.getInstance().forwardControlPitch);

		updateControlSignal();


	} // setControlSignalForward


	// ################################################################

	public void setControlSignalLeft(View v) {

		seekBarThrottle.setProgress(OrbitSingleton.getInstance().leftControlThrottle);
		seekBarYaw.setProgress(OrbitSingleton.getInstance().leftControlYaw);
		seekBarPitch.setProgress(OrbitSingleton.getInstance().leftControlPitch);

		updateControlSignal();


	} // setControlSignalLeft


	// ################################################################

	public void setControlSignalRight(View v) {

		seekBarThrottle.setProgress(OrbitSingleton.getInstance().rightControlThrottle);
		seekBarYaw.setProgress(OrbitSingleton.getInstance().rightControlYaw);
		seekBarPitch.setProgress(OrbitSingleton.getInstance().rightControlPitch);

		updateControlSignal();


	} // setControlSignalRight


	// ################################################################

	public void onAccuracyChanged(Sensor sensor, int accuracy) {

		// No operation

	} // onAccuracyChanged


	// ################################################################

	public void onSensorChanged(SensorEvent sensorEvent) {

		// Sensor.TYPE_ORIENTATION method
		//		tiltX = sensorEvent.values[1];
		//		tiltY = -sensorEvent.values[2]; // invert the Y axis so that negative values equal left

		// Sensor.TYPE_GRAVITY or TYPE_ACCELEROMETER methods
		OrbitSingleton.getInstance().tiltX = sensorEvent.values[1] * -4;
		OrbitSingleton.getInstance().tiltY = sensorEvent.values[0] * -8;

		//		appendDebugConsole("X: " + tiltX + ", Y: " + tiltY + "\n");

		if (OrbitSingleton.getInstance().referenceTiltX == 0) {
			OrbitSingleton.getInstance().referenceTiltX = OrbitSingleton.getInstance().tiltX;
			OrbitSingleton.getInstance().referenceTiltY = OrbitSingleton.getInstance().tiltY;
		}

		// Sensor.TYPE_OPERATION method
		//		seekBarYaw.setProgress(defaultControlYaw + (int) (tiltY - referenceTiltY));
		//		seekBarPitch.setProgress(defaultControlPitch + (int) ((tiltX / 2) - (referenceTiltX / 2)));

		// Sensor.TYPE_GRAVITY
		if (checkBoxTiltSensorControlThrottle.isChecked()) {
			seekBarThrottle.setProgress(OrbitSingleton.getInstance().defaultControlThrottle - (int) ((OrbitSingleton.getInstance().tiltX * 2) - (OrbitSingleton.getInstance().referenceTiltX * 2)));
		} else {
			seekBarYaw.setProgress(OrbitSingleton.getInstance().defaultControlYaw + (int) (OrbitSingleton.getInstance().tiltY - OrbitSingleton.getInstance().referenceTiltY));
			seekBarPitch.setProgress(OrbitSingleton.getInstance().defaultControlPitch + (int) (OrbitSingleton.getInstance().tiltX - OrbitSingleton.getInstance().referenceTiltX));
		}

	} // onSensorChanged


	// ################################################################

	public void onCheckBoxTiltSensorControlClicked(View view) {
		// Drop the View as we don't use it
		// This is necessary for the onPause/onResume functions which don't receive a View

		Log.v(TAG, "onCheckBoxTiltSensorControlClicked");

		onCheckBoxTiltSensorControlClicked(); // call the full method (this version is the View method called by GUI)

		if (checkBoxTiltSensorControl.isChecked()) {
			checkBoxTiltSensorControlThrottle.setVisibility(View.VISIBLE);
			Log.v(TAG, "checkBoxTiltSensorControlThrottle.setVisibility(View.VISIBLE)");
		} else {
			checkBoxTiltSensorControlThrottle.setVisibility(View.GONE);
			Log.v(TAG, "checkBoxTiltSensorControlThrottle.setVisibility(View.GONE)");
		}

	} // onCheckBoxTiltSensorControlClicked(View view)


	// ################################################################

	//	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
//	@SuppressWarnings("unused")
	public void onCheckBoxTiltSensorControlClicked() {

		if (checkBoxTiltSensorControl.isChecked()) {

			// register for tilt sensor events:
//			sensorManager = (SensorManager) ((OrbitTabActivity)getActivity()).getSystemService(Context.SENSOR_SERVICE);
			sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);


			if (sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY) != null) {
				List<Sensor> gravSensors = sensorManager.getSensorList(Sensor.TYPE_GRAVITY);
				for (int i=0; i < gravSensors.size(); i++) {
					if (gravSensors.get(i).getVersion() >= 3) {
						// Use the version 3 gravity sensor.
						orientationSensor = gravSensors.get(i);
						Log.v(TAG, "Tilt Control: Using Gravity Sensor (version 3+)");
//						appendDebugConsole("Tilt Control: Using Gravity Sensor\n");
						break;
					}
				}

				if (orientationSensor == null) {
					// Fall back to using an earlier version gravity sensor.
					for (int i=0; i < gravSensors.size(); i++) {
						orientationSensor = gravSensors.get(i);
						Log.v(TAG, "Tilt Control: Using Gravity Sensor");
//						appendDebugConsole("Tilt Control: Using Gravity Sensor\n");
						break;
					}
				}
			}

			else if (sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION) != null) {
				orientationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
				Log.v(TAG, "Tilt Control: Using Accelerometer Sensor");
//				appendDebugConsole("Tilt Control: Using Linear Accelerometer Sensor\n");
			}

			else {

				// Use the accelerometer.
				if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null){
					orientationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
					Log.v(TAG, "Tilt Control: Using Accelerometer Sensor");
//					appendDebugConsole("Tilt Control: Using Accelerometer Sensor\n");
				}

			}

			// if we can't access the orientation sensor then exit:
			if (orientationSensor == null) {
//				Toast.makeText(((OrbitTabActivity) getActivity()), "No Tilt Sensor Found", Toast.LENGTH_SHORT).show();
				Toast.makeText(getActivity().getApplicationContext(), "No Tilt Sensor Found", Toast.LENGTH_SHORT).show();
//				appendDebugConsole("Tilt Control: Not Found\n");
				Log.d(TAG, "Tilt Control: Not Found\n");
				checkBoxTiltSensorControl.setChecked(false);
				sensorManager.unregisterListener(this);
			} else {
				sensorManager.registerListener(this, orientationSensor, SensorManager.SENSOR_DELAY_UI);
			}

		} else {
			if (orientationSensor != null) {
				sensorManager.unregisterListener(this);
				OrbitSingleton.getInstance().referenceTiltX = 0;
				OrbitSingleton.getInstance().referenceTiltY = 0;
			}
		}


	} // onCheckBoxTiltSensorControlClicked


	// ################################################################

	public void checkBoxTiltSensorControlThrottleClicked(View v) {

		OrbitSingleton.getInstance().referenceTiltX = 0;
		OrbitSingleton.getInstance().referenceTiltY = 0;

		Log.v(TAG, "onCheckBoxTiltSensorControlThrottleClicked");


		// Disabled due to problems with "Tilt Sensor Control" becoming stuck checked
		//		if ((checkBoxTiltSensorControlThrottle.isChecked()) &&
		//				(! checkBoxTiltSensorControl.isChecked())) {
		//			checkBoxTiltSensorControl.setChecked(true);
		//		}


		if (checkBoxTiltSensorControl.isChecked()) {
			checkBoxTiltSensorControlThrottle.setVisibility(View.VISIBLE);
			Log.v(TAG, "checkBoxTiltSensorControlThrottle.setVisibility(View.VISIBLE)");
		} else {
			checkBoxTiltSensorControlThrottle.setVisibility(View.GONE);
			Log.v(TAG, "checkBoxTiltSensorControlThrottle.setVisibility(View.GONE)");
		}


	} // checkBoxTiltSensorControlThrottleClicked


}
