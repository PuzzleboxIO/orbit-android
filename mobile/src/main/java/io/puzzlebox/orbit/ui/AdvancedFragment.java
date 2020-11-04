package io.puzzlebox.orbit.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import org.apache.http.conn.util.InetAddressUtils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import io.puzzlebox.jigsaw.data.DevicePuzzleboxOrbitSingleton;
import io.puzzlebox.orbit.R;

public class AdvancedFragment extends Fragment
		implements View.OnClickListener,
		SeekBar.OnSeekBarChangeListener,
		SensorEventListener
{

	private final static String TAG = AdvancedFragment.class.getSimpleName();

	private OnFragmentInteractionListener mListener;

	/**
	 * UI
	 */
	Configuration config;

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

	CheckBox checkBoxAdvancedOptions;
	CheckBox checkBoxGenerateAudio;
	CheckBox checkBoxInvertControlSignal;
	CheckBox checkBoxTiltSensorControl;
	CheckBox checkBoxTiltSensorControlThrottle;
	public CheckBox checkBoxControlledDescent;
	public RadioGroup radioGroupChannel;
	RadioButton radioButtonChannelA;
	RadioButton radioButtonChannelB;
	RadioButton radioButtonChannelC;
	View viewSpaceRadioGroupChannel;

	//	TextView tv;
	int viewSpaceGenerateAudioWidth = 120;

	public OrbitControlledDescentTask orbitControlledDescentTask;

	private SensorManager sensorManager;
	private Sensor orientationSensor = null;

	LinearLayout layoutControl;
	LinearLayout layoutAudioService;
	LinearLayout layoutAdvancedOptions;
	LinearLayout layoutInvertControlSignal;
	View viewSpaceGenerateAudio;

	View v;

	public static AdvancedFragment newInstance() {
		AdvancedFragment fragment = new AdvancedFragment();
		Bundle args = new Bundle();
		fragment.setArguments(args);
		return fragment;
	}

	public AdvancedFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.fragment_advanced, container, false);

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


		radioGroupChannel = (RadioGroup) v.findViewById(R.id.radioGroupChannel);
		radioButtonChannelA = (RadioButton) v.findViewById(R.id.radioA);
		radioButtonChannelB = (RadioButton) v.findViewById(R.id.radioB);
		radioButtonChannelC = (RadioButton) v.findViewById(R.id.radioC);
		radioButtonChannelA.setChecked(true);

		viewSpaceGenerateAudio = (View) v.findViewById(R.id.viewSpaceGenerateAudio);

		checkBoxGenerateAudio = (CheckBox) v.findViewById(R.id.checkBoxGenerateAudio);
		checkBoxGenerateAudio.setOnClickListener(this);

		checkBoxInvertControlSignal = (CheckBox) v.findViewById(R.id.checkBoxInvertControlSignal);
		checkBoxInvertControlSignal.setOnClickListener(this);

		checkBoxTiltSensorControl = (CheckBox) v.findViewById(R.id.checkBoxTiltSensorControl);
		checkBoxTiltSensorControl.setOnClickListener(this);

		checkBoxTiltSensorControlThrottle = (CheckBox) v.findViewById(R.id.checkBoxTiltSensorControlThrottle);
		checkBoxTiltSensorControlThrottle.setOnClickListener(this);

		checkBoxTiltSensorControlThrottle.setVisibility(View.GONE); // Default should be hidden until Tilt Control is activated

		checkBoxControlledDescent = (CheckBox) v.findViewById(R.id.checkBoxControlledDescent);
		checkBoxControlledDescent.setOnClickListener(this);

		viewSpaceRadioGroupChannel = (View) v.findViewById(R.id.viewSpaceRadioGroupChannel);
		android.view.ViewGroup.LayoutParams layoutParams = viewSpaceRadioGroupChannel.getLayoutParams();
		layoutParams.width = 5;
		viewSpaceRadioGroupChannel.setLayoutParams(layoutParams);

		Log.d(TAG, "Android version: " + Integer.valueOf(android.os.Build.VERSION.SDK_INT) + "\n" );

		ArrayList<String> localAddresses = getLocalIpAddresses();

		Iterator<String> it = localAddresses.iterator();
		while(it.hasNext()) {
			Log.d(TAG, "Local IP Address: " + it.next().toString() + "\n");
		}

		layoutControl = (LinearLayout) v.findViewById(R.id.layoutControl);
		layoutAudioService = (LinearLayout) v.findViewById(R.id.layoutAudioService);
		layoutAdvancedOptions = (LinearLayout) v.findViewById(R.id.layoutAdvancedOptions);
		layoutInvertControlSignal = (LinearLayout) v.findViewById(R.id.layoutInvertControlSignal);

		/**
		 * Set Custom Device Settings
		 */
		setDeviceCustomSettings();

		/**
		 * Update settings according to default UI
		 */
		updateScreenLayout();
		updateAdvancedOptions();

		return v;
	}

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

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

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

	@Override
	public void onResume() {
		updateAdvancedOptions();
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

	public static ArrayList<String> getLocalIpAddresses() {
		ArrayList<String> addresses = new ArrayList<>();
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					String ipv4  = inetAddress.getHostAddress();
					if (!inetAddress.isLoopbackAddress() && InetAddressUtils.isIPv4Address(ipv4)) {
						addresses.add(inetAddress.getHostAddress().toString());
					}
				}
			}
			return addresses;
		} catch (SocketException e) {
			e.printStackTrace();
		}
		return addresses;
	}

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
	}

	public void updateScreenLayoutSmall() {
		String generateSignalLabel = getResources().getString(R.string.checkbox_generate_audio_small);
		checkBoxGenerateAudio.setText(generateSignalLabel);

		String invertSignalLabel = getResources().getString(R.string.checkbox_invert_control_signal_small);
		checkBoxInvertControlSignal.setText(invertSignalLabel);

		String checkbox_tilt_sensor_control_small = getResources().getString(R.string.checkbox_tilt_sensor_control_small);
		checkBoxTiltSensorControl.setText(checkbox_tilt_sensor_control_small);

		String checkbox_tilt_sensor_control_throttle_small = getResources().getString(R.string.checkbox_tilt_sensor_control_throttle_small);
		checkBoxTiltSensorControlThrottle.setText(checkbox_tilt_sensor_control_throttle_small);
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
			case R.id.checkBoxGenerateAudio:
				onCheckBoxGenerateAudioClicked(v);
				break;
			case R.id.checkBoxInvertControlSignal:
				onCheckBoxInvertControlSignalClicked(v);
				break;
			case R.id.checkBoxTiltSensorControl:
				onCheckBoxTiltSensorControlClicked(v);
				break;
			case R.id.checkBoxTiltSensorControlThrottle:
				checkBoxTiltSensorControlThrottleClicked(v);
				break;
			case R.id.checkBoxControlledDescent:
				checkBoxControlledDescentClicked(v);
				break;
		}
	}

	public void onCheckBoxGenerateAudioClicked(View view) {

		DevicePuzzleboxOrbitSingleton.getInstance().generateAudio = checkBoxGenerateAudio.isChecked();

		if ( DevicePuzzleboxOrbitSingleton.getInstance().generateAudio ) {
			checkBoxInvertControlSignal.setVisibility(View.VISIBLE);
			checkBoxTiltSensorControl.setVisibility(View.VISIBLE);
			if (checkBoxTiltSensorControl.isChecked())
				checkBoxTiltSensorControlThrottle.setVisibility(View.VISIBLE);
		} else {
			checkBoxInvertControlSignal.setVisibility(View.GONE);
			checkBoxTiltSensorControl.setVisibility(View.GONE);
			checkBoxTiltSensorControlThrottle.setVisibility(View.GONE);
		}
	}

	public void onCheckBoxInvertControlSignalClicked(View view) {
		DevicePuzzleboxOrbitSingleton.getInstance().invertControlSignal = checkBoxInvertControlSignal.isChecked();
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

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@SuppressWarnings("unused")
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
		} else {
			checkBoxTiltSensorControlThrottle.setVisibility(View.GONE);
		}
	}

	public void checkBoxControlledDescentClicked(View v) {
		// If Controlled Descent checkBox is unchecked during an
		// active controlled descent, abort descent and reset throttle
		if ((! checkBoxControlledDescent.isChecked()) &&
				(orbitControlledDescentTask != null) &&
				(orbitControlledDescentTask.keepDescending)) {
			orbitControlledDescentTask.callStopAudio = false;
			orbitControlledDescentTask.keepDescending = false;
		}
	}

	public void updateAdvancedOptions() {
	}

	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
		// Abort controlled descent thread if activated and slide adjusted by human touch
		if ((fromTouch) &&
				(orbitControlledDescentTask != null) &&
				(orbitControlledDescentTask.keepDescending)) {
			//			orbitControlledDescentTask.callStopAudio = false;
			orbitControlledDescentTask.resetThrottleToPrevious = false;
			orbitControlledDescentTask.keepDescending = false;
		}
		updateControlSignal();
	}

	public void onStartTrackingTouch(SeekBar seekBar) {
		/**
		 * Method required by SeekBar.OnSeekBarChangeListener
		 */
	}

	public void onStopTrackingTouch(SeekBar seekBar) {
		/**
		 * Update control signal after moving a seekBar.
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

	public void registerControlledDescent() {
		// Abort if a controlled descent task is in progress or
		// has already been called at least once in this cycle
		if ((orbitControlledDescentTask != null) &&
				orbitControlledDescentTask.descentActivated)
			return;

		orbitControlledDescentTask = new OrbitControlledDescentTask();
		orbitControlledDescentTask.execute();
	}

	public void unregisterControlledDescent(boolean callStopAudio) {
	}

	public void setDeviceCustomSettings() {
		/**
		 * Performs customs settings according to http://orbit.puzzlebox.info/tracker/wiki/OrbitDeviceCompatibility
		 */
		String deviceManufacturer = Build.MANUFACTURER; //.toLowerCase(Locale.ENGLISH);
		String deviceModel = Build.MODEL; //.toLowerCase(Locale.ENGLISH);
		String deviceDevice = Build.DEVICE; //.toLowerCase(Locale.ENGLISH);

		Log.d(TAG, "Device Information:\n");

		// Nexus 7
		if ((deviceManufacturer.contains("asus")) &&
				(deviceModel.contains("Nexus 7")) &&
				(deviceDevice.contains("grouper"))) {
			Log.d(TAG, "Device detected: Google Nexus 7 (2012)");
		}

		// Samsung Galaxy S4 (US)
		else if ((deviceManufacturer.contains("samsung")) &&
				(deviceModel.contains("SGH-M919"))) {
			Log.d(TAG, "Device detected: Samsung Galaxy S4 (US)");
			if (checkBoxGenerateAudio.isChecked())
				checkBoxGenerateAudio.performClick();
		}

		// Samsung Galaxy S4 (EU)
		else if ((deviceManufacturer.contains("samsung")) &&
				(deviceModel.contains("GT-I9505"))) {
			Log.d(TAG, "Device detected: Samsung Galaxy S4 (EU)");
			if (checkBoxGenerateAudio.isChecked())
				checkBoxGenerateAudio.performClick();
		}

		// HTC One X
		else if ((deviceManufacturer.contains("HTC")) &&
				(deviceModel.contains("HTC One X"))) {
			Log.d(TAG, "Device detected: HTC One X");
			if (! checkBoxInvertControlSignal.isChecked())
				checkBoxInvertControlSignal.performClick();
		}

		// HTC Droid DNA
		else if ((deviceManufacturer.contains("HTC")) &&
				(deviceModel.contains("HTC6435LVW"))) {
			Log.d(TAG, "Device detected: HTC Droid DNA");
			if (! checkBoxInvertControlSignal.isChecked())
				checkBoxInvertControlSignal.performClick();

			// Display a warning pop-up about this device, but not more than twice
			if (DevicePuzzleboxOrbitSingleton.getInstance().deviceWarningMessagesDisplayed < 2) {
				Toast.makeText(getActivity().getApplicationContext(),
						"Warning: HTC Droid DNA detected, which has known compatibility issues with infrared transmitter. Contact Support for more information.", Toast.LENGTH_LONG).show();
				DevicePuzzleboxOrbitSingleton.getInstance().deviceWarningMessagesDisplayed =
						DevicePuzzleboxOrbitSingleton.getInstance().deviceWarningMessagesDisplayed + 1;
			}
		}

		// HTC One
		else if ((deviceManufacturer.contains("HTC")) &&
				(deviceModel.contains("HTC One"))) {
			Log.d(TAG, "Device detected: HTC One");
			if (! checkBoxInvertControlSignal.isChecked())
				checkBoxInvertControlSignal.performClick();

			// Display a warning pop-up about this device, but not more than twice
			if (DevicePuzzleboxOrbitSingleton.getInstance().deviceWarningMessagesDisplayed < 2) {
				Toast.makeText(getActivity().getApplicationContext(),
						"Warning: HTC One detected, which has known compatibility issues with infrared transmitter. Contact Support for more information.", Toast.LENGTH_LONG).show();
				DevicePuzzleboxOrbitSingleton.getInstance().deviceWarningMessagesDisplayed =
						DevicePuzzleboxOrbitSingleton.getInstance().deviceWarningMessagesDisplayed + 1;
			}
		}

		else {
			Log.d(TAG, "No custom device settings found.");
		}
	}

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


	public class OrbitControlledDescentTask extends AsyncTask<Integer, Void, Integer> {

		private final String TAG = "OrbitControlledDescent";
		AdvancedFragment fragmentAdvanced;

		private final int throttleTarget = 28; // 30 is the minimum to keep the blades spinning
		private double throttleStep = 0.5;
		private final int throttleStepDefault = 1;
		private final int throttleStepSleepDefault = 100;
		private final double throttleStepAccelerateThreshold = 0.667; // 0.667 = 2/3rds between current Throttle and point where blades stop spinning
		private final double throttleStepAccelerateMultiplyer = 2;
		private final double throttleStepAccelerateValue = 0.2; // 0.5 = divide sleep time in half (example: from 100ms to 50ms)

		public int throttlePrevious;
		public boolean resetThrottleToPrevious = true;
		public boolean keepDescending = true;
		public boolean callStopAudio = true;
		public boolean descentActivated = false;

		protected Integer doInBackground(Integer... params) {
			throttlePrevious = fragmentAdvanced.seekBarThrottle.getProgress();
			throttleStep = throttleStepDefault;

			int throttleStepSleep = throttleStepSleepDefault;
			double throttleNew = throttlePrevious;
			boolean stepAccelerated = false;
			descentActivated = true;

			while ((keepDescending) &&
					(fragmentAdvanced.seekBarThrottle.getProgress() > throttleTarget) &&
					(! fragmentAdvanced.checkBoxTiltSensorControlThrottle.isChecked()) &&
					(fragmentAdvanced.checkBoxControlledDescent.isChecked())) {

				// Increase steps after time as the Orbit falls quickly at lower power levels
				if ((! stepAccelerated) &&
						(fragmentAdvanced.seekBarThrottle.getProgress() - throttleTarget) <=
								(int) ((throttlePrevious - throttleTarget) * throttleStepAccelerateThreshold)) {
					throttleStep = throttleStep * throttleStepAccelerateMultiplyer;
					throttleStepSleep = (int) (throttleStepSleep * throttleStepAccelerateValue);
					stepAccelerated = true;
				}

				throttleNew = throttleNew - throttleStep;
				fragmentAdvanced.seekBarThrottle.setProgress( (int) throttleNew );

				try {
					Thread.sleep(throttleStepSleep);
				} catch (InterruptedException e) {
					Log.e(TAG, "Thread InterruptedException: " + e);
					e.printStackTrace();
				} // sleep is in milliseconds
			}
			return 1;
		}

		@Override
		protected void onPostExecute(Integer params) {
			if (resetThrottleToPrevious)
				fragmentAdvanced.seekBarThrottle.setProgress(throttlePrevious);

			fragmentAdvanced.unregisterControlledDescent(callStopAudio);

			descentActivated = false;
		}
	}
}
