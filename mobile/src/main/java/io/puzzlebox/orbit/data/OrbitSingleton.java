package io.puzzlebox.orbit.data;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.media.SoundPool;
import android.util.Log;

import io.puzzlebox.jigsaw.protocol.RBLService;
import io.puzzlebox.orbit.R;
import io.puzzlebox.orbit.protocol.AudioHandler;

/**
 * Created by sc on 5/3/15.
 */
public class OrbitSingleton {

	private final static String TAG = OrbitSingleton.class.getSimpleName();

	private static OrbitSingleton ourInstance = new OrbitSingleton();

	public static OrbitSingleton getInstance() {
		return ourInstance;
	}

	private OrbitSingleton() {
	}

	/**
	 * EEG Configuration
	 */

	public int eegPower = 0;


	/**
	 * Flight Configuration
	 */

	public int defaultTargetAttention = 72;
	public int defaultTargetMeditation = 0;

	public int minimumScoreTarget = 40;
	public int scoreCurrent = 0;
	public int scoreLast = 0;
	public int scoreHigh = 0;

	public boolean tiltSensorControl = false;
	public int deviceWarningMessagesDisplayed = 0;

	public boolean demoActive = false;

	public boolean flightActive = false;
	public boolean orbitActive = false;


	/**
	 * Joystick Configuration
	 */
	public int defaultJoystickThrottle = 0;
	public int minimumJoystickThrottle = 20;
	//	public int defaultJoystickYaw = 63;
	public int defaultJoystickYaw = 49;
	public int defaultJoystickPitch = 31;


	/**
	 * Advanced Configuration
	 */
	public int defaultControlThrottle = 80;
	//	int defaultControlYaw = 78;
	public int defaultControlYaw = 49;
	public int defaultControlPitch = 31;

	public int hoverControlThrottle = 80;
	//	int hoverControlYaw = 78;
	public int hoverControlYaw = 49;
	public int hoverControlPitch = 31;

	public int forwardControlThrottle = 80;
	//	int forwardControlYaw = 78;
	public int forwardControlYaw = 49;
	public int forwardControlPitch = 50;

	public int leftControlThrottle = 80;
	//	int leftControlYaw = 42;
	public int leftControlYaw = 13;
	public int leftControlPitch = 31;

	public int rightControlThrottle = 80;
	public int rightControlYaw = 114;
	//		int rightControlYaw = 13;
	public int rightControlPitch = 31;

	public float tiltX = 0;
	public float tiltY = 0;
	public float referenceTiltX = 0;
	public float referenceTiltY = 0;


	/**
	 * Audio
	 *
	 * By default the flight control command is hard-coded into WAV files
	 * When "Generate Control Signal" is enabled the tones used to communicate
	 * with the infrared dongle are generated on-the-fly.
	 */
	public int audioFile = R.raw.throttle_hover_android_common;
	//	int audioFile = R.raw.throttle_hover_android_htc_one_x;

	//	public int defaultChannel = 0; // B
	public int defaultChannel = 1; // A

	public boolean generateAudio = true;
	public boolean invertControlSignal = false;

	public SoundPool soundPool;
	public int soundID;
	public boolean loaded = false;

	public AudioHandler audioHandler = new AudioHandler();


	// ################################################################

	public void resetControlSignal() {

		audioHandler.command = new Integer[]{
				  defaultControlThrottle,
				  defaultControlYaw,
				  defaultControlPitch,
				  defaultChannel};

		audioHandler.updateControlSignal();

	}


	// ################################################################

	public void startAudioHandler() {

		if (!OrbitSingleton.getInstance().audioHandler.isAlive()) {

			try {
				audioHandler.start();
			} catch (Exception e) {
//				Log.w(TAG, "Exception starting AudioHandler: " + e.getStackTrace());
				Log.w(TAG, "Exception starting AudioHandler: " + e.getStackTrace());
				e.printStackTrace();

				OrbitSingleton.getInstance().resetAudioHandler();

			}
		}

	}

	// ################################################################

	public void resetAudioHandler() {
		audioHandler.shutdown();
		audioHandler = new AudioHandler();
		audioHandler.start();
	}


}
