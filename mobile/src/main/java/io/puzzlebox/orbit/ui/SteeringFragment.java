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

import io.puzzlebox.jigsaw.data.DevicePuzzleboxOrbitSingleton;
import io.puzzlebox.orbit.R;
import io.puzzlebox.jigsaw.ui.JoystickView;

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

	private SensorManager sensorManager;
	private Sensor orientationSensor = null;

	private OnFragmentInteractionListener mListener;

	public SteeringFragment() {
		// Required empty public constructor
	}

	public static SteeringFragment newInstance() {
		SteeringFragment fragment = new SteeringFragment();
		Bundle args = new Bundle();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {

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

		buttonHover = (Button) v.findViewById(R.id.buttonHover);
		buttonHover.setOnClickListener(this);

		buttonForward = (Button) v.findViewById(R.id.buttonForward);
		buttonForward.setOnClickListener(this);

		buttonLeft = (Button) v.findViewById(R.id.buttonLeft);
		buttonLeft.setOnClickListener(this);

		buttonRight = (Button) v.findViewById(R.id.buttonRight);
		buttonRight.setOnClickListener(this);

		checkBoxTiltSensorControl = (CheckBox) v.findViewById(R.id.checkBoxTiltSensorControl);
		checkBoxTiltSensorControl.setOnClickListener(this);

		checkBoxTiltSensorControlThrottle = (CheckBox) v.findViewById(R.id.checkBoxTiltSensorControlThrottle);
		checkBoxTiltSensorControlThrottle.setOnClickListener(this);

		checkBoxTiltSensorControlThrottle.setVisibility(View.GONE); // Default should be hidden until Tilt Control is activated

		JoystickView joystick = (JoystickView) v.findViewById(io.puzzlebox.orbit.R.id.joystickViewSteering);
		joystick.setOnMoveListener(onMoveJoystick);

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

	@Override
	public void onResume() {
		onCheckBoxTiltSensorControlClicked();

		super.onResume();

		LocalBroadcastManager.getInstance(getActivity().getApplicationContext()).registerReceiver(
				mPacketReceiver, new IntentFilter("io.puzzlebox.jigsaw.protocol.thinkgear.packet"));
	}

	public void onPause() {
		if (sensorManager != null)
			sensorManager.unregisterListener(this);

		super.onPause();

		LocalBroadcastManager.getInstance(
				getActivity().getApplicationContext()).unregisterReceiver(
				mPacketReceiver);
	}

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
			case R.id.checkBoxTiltSensorControl:
				onCheckBoxTiltSensorControlClicked(v);
				break;
			case R.id.checkBoxTiltSensorControlThrottle:
				checkBoxTiltSensorControlThrottleClicked(v);
				break;
		}
	}

	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
		updateControlSignal();
	}

	private final JoystickView.OnMoveListener onMoveJoystick = new JoystickView.OnMoveListener(){
		public void onMove(int angle, int strength) {
			Log.v(TAG, "onMoveJoystick(int angle, int strength): " + angle + ", " + strength);

			if ((angle >= 60) && (angle <= 120)) {
				// Up
				int newThrottle = seekBarThrottle.getMax() - DevicePuzzleboxOrbitSingleton.getInstance().defaultControlThrottle;
				newThrottle = (int) (newThrottle * (strength / 100.0));
				newThrottle = DevicePuzzleboxOrbitSingleton.getInstance().defaultControlThrottle + newThrottle;
				seekBarThrottle.setProgress(newThrottle);
			}
			else if ((angle >= 240) && (angle <= 300)) {
				// Down
				int newThrottle = DevicePuzzleboxOrbitSingleton.getInstance().defaultControlThrottle;
				newThrottle = (int) (newThrottle * (strength / 100.0));
				newThrottle = DevicePuzzleboxOrbitSingleton.getInstance().defaultControlThrottle - newThrottle;
				seekBarThrottle.setProgress(newThrottle);
			}
		}
	};

	private final BroadcastReceiver mPacketReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			int eegAttention = Integer.valueOf(intent.getStringExtra("Attention"));
			int eegMeditation = Integer.valueOf(intent.getStringExtra("Meditation"));
			int eegSignal = Integer.valueOf(intent.getStringExtra("Signal Level"));

			progressBarAttention.setProgress(eegAttention);
			progressBarMeditation.setProgress(eegMeditation);
			progressBarSignal.setProgress(eegSignal);
			progressBarPower.setProgress(DevicePuzzleboxOrbitSingleton.getInstance().eegPower);
		}

	};

	public void onStartTrackingTouch(SeekBar seekBar) {
		/**
		 * Method required by SeekBar.OnSeekBarChangeListener
		 */
	}

	public void onStopTrackingTouch(SeekBar seekBar) {
		/*
		  Update control signal after moving a seekBar.
		 */
		updateControlSignal();
	}

	public void updateControlSignal() {
		// We subtract the current Yaw position from the maximum slider value
		// because smaller values instruct the helicopter to spin to the right
		// (clockwise if looking down from above) whereas intuitively moving
		// the slider to the left should cause it to spin left
		Integer[] command =  {
				seekBarThrottle.getProgress(),
				seekBarYaw.getMax() - seekBarYaw.getProgress(),
				seekBarPitch.getProgress(),
				1};

		DevicePuzzleboxOrbitSingleton.getInstance().puzzleboxOrbitAudioIRHandler.command = command;
		DevicePuzzleboxOrbitSingleton.getInstance().puzzleboxOrbitAudioIRHandler.updateControlSignal();
	}

	public void resetControlSignal(View v) {
		seekBarThrottle.setProgress(DevicePuzzleboxOrbitSingleton.getInstance().defaultControlThrottle);
		seekBarYaw.setProgress(DevicePuzzleboxOrbitSingleton.getInstance().defaultControlYaw);
		seekBarPitch.setProgress(DevicePuzzleboxOrbitSingleton.getInstance().defaultControlPitch);
		updateControlSignal();
	}

	public void setControlSignalHover(View v) {
		seekBarThrottle.setProgress(DevicePuzzleboxOrbitSingleton.getInstance().hoverControlThrottle);
		seekBarYaw.setProgress(DevicePuzzleboxOrbitSingleton.getInstance().hoverControlYaw);
		seekBarPitch.setProgress(DevicePuzzleboxOrbitSingleton.getInstance().hoverControlPitch);
		updateControlSignal();
	}

	public void setControlSignalForward(View v) {
		seekBarThrottle.setProgress(DevicePuzzleboxOrbitSingleton.getInstance().forwardControlThrottle);
		seekBarYaw.setProgress(DevicePuzzleboxOrbitSingleton.getInstance().forwardControlYaw);
		seekBarPitch.setProgress(DevicePuzzleboxOrbitSingleton.getInstance().forwardControlPitch);
		updateControlSignal();
	}

	public void setControlSignalLeft(View v) {
		seekBarThrottle.setProgress(DevicePuzzleboxOrbitSingleton.getInstance().leftControlThrottle);
		seekBarYaw.setProgress(DevicePuzzleboxOrbitSingleton.getInstance().leftControlYaw);
		seekBarPitch.setProgress(DevicePuzzleboxOrbitSingleton.getInstance().leftControlPitch);
		updateControlSignal();
	}

	public void setControlSignalRight(View v) {
		seekBarThrottle.setProgress(DevicePuzzleboxOrbitSingleton.getInstance().rightControlThrottle);
		seekBarYaw.setProgress(DevicePuzzleboxOrbitSingleton.getInstance().rightControlYaw);
		seekBarPitch.setProgress(DevicePuzzleboxOrbitSingleton.getInstance().rightControlPitch);
		updateControlSignal();
	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// No operation
	}

	public void onSensorChanged(SensorEvent sensorEvent) {
		// Sensor.TYPE_GRAVITY or TYPE_ACCELEROMETER methods
		DevicePuzzleboxOrbitSingleton.getInstance().tiltX = sensorEvent.values[1] * -4;
		DevicePuzzleboxOrbitSingleton.getInstance().tiltY = sensorEvent.values[0] * -8;

		if (DevicePuzzleboxOrbitSingleton.getInstance().referenceTiltX == 0) {
			DevicePuzzleboxOrbitSingleton.getInstance().referenceTiltX = DevicePuzzleboxOrbitSingleton.getInstance().tiltX;
			DevicePuzzleboxOrbitSingleton.getInstance().referenceTiltY = DevicePuzzleboxOrbitSingleton.getInstance().tiltY;
		}

		// Sensor.TYPE_GRAVITY
		if (checkBoxTiltSensorControlThrottle.isChecked()) {
			seekBarThrottle.setProgress(DevicePuzzleboxOrbitSingleton.getInstance().defaultControlThrottle - (int) ((DevicePuzzleboxOrbitSingleton.getInstance().tiltX * 2) - (DevicePuzzleboxOrbitSingleton.getInstance().referenceTiltX * 2)));
		} else {
			seekBarYaw.setProgress(DevicePuzzleboxOrbitSingleton.getInstance().defaultControlYaw + (int) (DevicePuzzleboxOrbitSingleton.getInstance().tiltY - DevicePuzzleboxOrbitSingleton.getInstance().referenceTiltY));
			seekBarPitch.setProgress(DevicePuzzleboxOrbitSingleton.getInstance().defaultControlPitch + (int) (DevicePuzzleboxOrbitSingleton.getInstance().tiltX - DevicePuzzleboxOrbitSingleton.getInstance().referenceTiltX));
		}
	}

	public void onCheckBoxTiltSensorControlClicked(View view) {
		// Drop the View as we don't use it
		// This is necessary for the onPause/onResume functions which don't receive a View
		onCheckBoxTiltSensorControlClicked(); // call the full method (this version is the View method called by GUI)

		if (checkBoxTiltSensorControl.isChecked()) {
			checkBoxTiltSensorControlThrottle.setVisibility(View.VISIBLE);
			Log.v(TAG, "checkBoxTiltSensorControlThrottle.setVisibility(View.VISIBLE)");
		} else {
			checkBoxTiltSensorControlThrottle.setVisibility(View.GONE);
			Log.v(TAG, "checkBoxTiltSensorControlThrottle.setVisibility(View.GONE)");
		}
	}

	public void onCheckBoxTiltSensorControlClicked() {

		if (checkBoxTiltSensorControl.isChecked()) {

			// register for tilt sensor events:
			sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);

			if (sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY) != null) {
				List<Sensor> gravSensors = sensorManager.getSensorList(Sensor.TYPE_GRAVITY);
				for (int i=0; i < gravSensors.size(); i++) {
					if (gravSensors.get(i).getVersion() >= 3) {
						// Use the version 3 gravity sensor.
						orientationSensor = gravSensors.get(i);
						Log.d(TAG, "Tilt Control: Using Gravity Sensor (version 3+)");
						break;
					}
				}

				if (orientationSensor == null) {
					// Fall back to using an earlier version gravity sensor.
					for (int i=0; i < gravSensors.size(); i++) {
						orientationSensor = gravSensors.get(i);
						Log.d(TAG, "Tilt Control: Using Gravity Sensor");
						break;
					}
				}
			}
			else if (sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION) != null) {
				orientationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
				Log.d(TAG, "Tilt Control: Using Accelerometer Sensor");
			}
			else {
				// Use the accelerometer.
				if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null){
					orientationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
					Log.d(TAG, "Tilt Control: Using Accelerometer Sensor");
				}
			}

			// if we can't access the orientation sensor then exit:
			if (orientationSensor == null) {
				Toast.makeText(getActivity().getApplicationContext(), "No Tilt Sensor Found", Toast.LENGTH_SHORT).show();
				Log.d(TAG, "Tilt Control: Not Found\n");
				checkBoxTiltSensorControl.setChecked(false);
				sensorManager.unregisterListener(this);
			} else {
				sensorManager.registerListener(this, orientationSensor, SensorManager.SENSOR_DELAY_UI);
			}

		} else {
			if (orientationSensor != null) {
				sensorManager.unregisterListener(this);
				DevicePuzzleboxOrbitSingleton.getInstance().referenceTiltX = 0;
				DevicePuzzleboxOrbitSingleton.getInstance().referenceTiltY = 0;
			}
		}
	}

	public void checkBoxTiltSensorControlThrottleClicked(View v) {

		DevicePuzzleboxOrbitSingleton.getInstance().referenceTiltX = 0;
		DevicePuzzleboxOrbitSingleton.getInstance().referenceTiltY = 0;

		if (checkBoxTiltSensorControl.isChecked()) {
			checkBoxTiltSensorControlThrottle.setVisibility(View.VISIBLE);
			Log.v(TAG, "checkBoxTiltSensorControlThrottle.setVisibility(View.VISIBLE)");
		} else {
			checkBoxTiltSensorControlThrottle.setVisibility(View.GONE);
			Log.v(TAG, "checkBoxTiltSensorControlThrottle.setVisibility(View.GONE)");
		}
	}
}
