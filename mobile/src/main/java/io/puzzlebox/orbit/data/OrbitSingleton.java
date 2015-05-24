package io.puzzlebox.orbit.data;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.media.SoundPool;

import io.puzzlebox.jigsaw.protocol.RBLService;
import io.puzzlebox.orbit.R;
import io.puzzlebox.orbit.protocol.AudioHandler;

/**
 * Created by sc on 5/3/15.
 */
public class OrbitSingleton {

	public int minimumScoreTarget = 40;
	public int scoreCurrent = 0;
	public int scoreLast = 0;
	public int scoreHigh = 0;

	public boolean tiltSensorControl = false;
	public int deviceWarningMessagesDisplayed = 0;

	public boolean demoActive = false;

	public boolean generateAudio = true;
	//	public boolean generateAudio = false;
	public boolean invertControlSignal = false;

	public boolean flightActive = false;

	/**
	 * Audio
	 *
	 * By default the flight control command is hard-coded into WAV files
	 * When "Generate Control Signal" is enabled the tones used to communicate
	 * with the infrared dongle are generated on-the-fly.
	 */
	public int audioFile = R.raw.throttle_hover_android_common;
	//	int audioFile = R.raw.throttle_hover_android_htc_one_x;

//	private SoundPool soundPool;
//	private int soundID;
//	boolean loaded = false;
	public SoundPool soundPool;
	public int soundID;
	public boolean loaded = false;

	public AudioHandler audioHandler = new AudioHandler();

//	public BluetoothGattCharacteristic characteristicTx = null;
//	public RBLService mBluetoothLeService;
//	public BluetoothAdapter mBluetoothAdapter;
//	public BluetoothDevice mDevice = null;
//	public String mDeviceAddress;
//
//	public boolean flag = true;
//	public boolean connState = false;
//	public boolean scanFlag = false;
//
//	public byte[] data = new byte[3];
////	public static final int REQUEST_ENABLE_BT = 1;
//	public final int REQUEST_ENABLE_BT = 1;
////	public static final long SCAN_PERIOD = 2000;
//	public final long SCAN_PERIOD = 2000;
//
////	final public static char[] hexArray = { '0', '1', '2', '3', '4', '5', '6',
////			  '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
//	final public char[] hexArray = { '0', '1', '2', '3', '4', '5', '6',
//			  '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };


//	private BluetoothGattCharacteristic characteristicTx = null;
//	private RBLService mBluetoothLeService;
//	private BluetoothAdapter mBluetoothAdapter;
//	private BluetoothDevice mDevice = null;
//	private String mDeviceAddress;
//
//	private boolean flag = true;
//	private boolean connState = false;
//	private boolean scanFlag = false;
//
//	private byte[] data = new byte[3];
//	private static final int REQUEST_ENABLE_BT = 1;
//	private static final long SCAN_PERIOD = 2000;
//
//	final private static char[] hexArray = { '0', '1', '2', '3', '4', '5', '6',
//			  '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };


	private static OrbitSingleton ourInstance = new OrbitSingleton();

	public static OrbitSingleton getInstance() {
		return ourInstance;
	}

	private OrbitSingleton() {
	}
}
