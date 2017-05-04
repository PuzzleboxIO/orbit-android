package io.puzzlebox.orbit.ui;

import android.content.Context;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import io.puzzlebox.jigsaw.data.ConfigurationSingleton;
import io.puzzlebox.jigsaw.data.ProfileSingleton;
import io.puzzlebox.jigsaw.ui.JoystickView;
import io.puzzlebox.orbit.R;
import io.puzzlebox.orbit.data.OrbitSingleton;

public class DialogPuzzleboxOrbitJoystickFragment extends DialogFragment {

	private final static String TAG = DialogPuzzleboxOrbitJoystickFragment.class.getSimpleName();

	public final static String profileID = "profile_puzzlebox_orbit_joystick";

	// UI
	public SeekBar seekBarThrottle;
	public SeekBar seekBarYaw;
	public SeekBar seekBarPitch;
	Button buttonDeviceEnable;

	private static int paddingJoysticks = 20;

	private OnFragmentInteractionListener mListener;

	public DialogPuzzleboxOrbitJoystickFragment() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
									 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View v = inflater.inflate(R.layout.dialog_profile_puzzlebox_orbit_joystick, container, false);

		seekBarThrottle = (SeekBar) v.findViewById(R.id.seekBarThrottle);
//		seekBarThrottle.setProgress(seekBarThrottle.getMax() / 2);
		seekBarThrottle.setProgress(OrbitSingleton.getInstance().defaultJoystickThrottle);
//		seekBarThrottle.setOnSeekBarChangeListener(this);

		seekBarYaw = (SeekBar) v.findViewById(R.id.seekBarYaw);
		seekBarYaw.setProgress(OrbitSingleton.getInstance().defaultJoystickYaw);
//		seekBarYaw.setProgress(seekBarYaw.getMax() / 2);
//		seekBarYaw.setOnSeekBarChangeListener(this);

		seekBarPitch = (SeekBar) v.findViewById(R.id.seekBarPitch);
		seekBarPitch.setProgress(OrbitSingleton.getInstance().defaultJoystickPitch);
//		seekBarPitch.setProgress(seekBarPitch.getMax() / 2);
//		seekBarPitch.setOnSeekBarChangeListener(this);

		LinearLayout llJoysticks = (LinearLayout) v.findViewById(R.id.linearLayoutJoysticks);

		JoystickView joystickViewThrottle = (JoystickView) v.findViewById(R.id.joystickViewThrottle);
		joystickViewThrottle.setOnMoveListener(onMoveJoystickThrottle);

		JoystickView joystickViewYawPitch = (JoystickView) v.findViewById(R.id.joystickViewYawPitch);
		joystickViewYawPitch.setOnMoveListener(onMoveJoystickYawPitch);


		ViewGroup.LayoutParams lp = llJoysticks.getLayoutParams();
		lp = joystickViewThrottle.getLayoutParams();

		Log.e(TAG, "ConfigurationSingleton.getInstance().displayWidth: " + ConfigurationSingleton.getInstance().displayWidth);
		Log.e(TAG, "lp.width: " + lp.width);

		if (((int) (ConfigurationSingleton.getInstance().displayWidth / 2))
				  < (lp.width * 2 + paddingJoysticks * 2)) {

			Log.e(TAG, "here");

			lp.width = ((int) (ConfigurationSingleton.getInstance().displayWidth / 2)) - paddingJoysticks;
			joystickViewThrottle.setLayoutParams(lp);

			lp = joystickViewYawPitch.getLayoutParams();
			lp.width = ((int) (ConfigurationSingleton.getInstance().displayWidth / 2)) - paddingJoysticks;
			joystickViewYawPitch.setLayoutParams(lp);
		}


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



		/**
		 * AudioHandler
		 */

		if (!OrbitSingleton.getInstance().audioHandler.isAlive()) {


			/**
			 * Prepare audio stream
			 */

			/** Set the hardware buttons to control the audio output */
//			getActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);

			/** Preload the flight control WAV file into memory */
//			OrbitSingleton.getInstance().soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
//			OrbitSingleton.getInstance().soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
//				public void onLoadComplete(SoundPool soundPool,
//													int sampleId,
//													int status) {
//					OrbitSingleton.getInstance().loaded = true;
//				}
//			});
//			OrbitSingleton.getInstance().soundID = OrbitSingleton.getInstance().soundPool.load(getActivity().getApplicationContext(), OrbitSingleton.getInstance().audioFile, 1);


			OrbitSingleton.getInstance().audioHandler.start();


		}



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

		stopControl();

	} // onPause


	// ################################################################

	public void onResume() {

		super.onResume();

		// TODO (hardcoded)
//		if (ProfileSingleton.getInstance().isActive("outputs", 0))
//		if (profiles.get(1).get)
		if (ProfileSingleton.getInstance().getStatus(profileID).equals("available"))
			playControl();
		else
			Toast.makeText(getActivity().getApplicationContext(), getString(R.string.toast_puzzlebox_orbit_joystick_audio_ir_warning), Toast.LENGTH_LONG).show();

	}

	// ################################################################

	private JoystickView.OnMoveListener onMoveJoystickYawPitch = new JoystickView.OnMoveListener(){
		public void onMove(int angle, int strength) {
			Log.v(TAG, "onMoveJoystickYawPitch(int angle, int strength): " + angle + ", " + strength);

			if ((angle == 0) && (strength == 0)) {
				seekBarYaw.setProgress(OrbitSingleton.getInstance().defaultJoystickYaw);
				seekBarPitch.setProgress(OrbitSingleton.getInstance().defaultJoystickPitch);
			}

			if ((angle >= 60) && (angle <= 120)) {
				// Up
				int newX = seekBarPitch.getMax() / 2;
				newX = (int) (newX * (strength / 100.0));
				newX = seekBarPitch.getMax() / 2 + newX;
				seekBarPitch.setProgress(newX);
			}
			else if ((angle >= 240) && (angle <= 300)) {
				// Down
				int newX = seekBarPitch.getMax() / 2;
				newX = (int) (newX * (strength / 100.0));
				newX = seekBarPitch.getMax() / 2 - newX;
				seekBarPitch.setProgress(newX);
			}

			if ((angle >= 150) && (angle <= 210)) {
				// Left
				int newY = seekBarYaw.getMax() / 2;
				newY = (int) (newY * (strength / 100.0));
				newY = seekBarYaw.getMax() / 2 - newY;
				seekBarYaw.setProgress(newY);
			}
			else if ((angle >= 330) || (angle <= 30)) {
				// Right
				int newY = seekBarYaw.getMax() / 2;
				newY = (int) (newY * (strength / 100.0));
				newY = seekBarYaw.getMax() / 2 + newY;
				seekBarYaw.setProgress(newY);
			}

			updateControlSignal();

		}
	};


	// ################################################################

	private JoystickView.OnMoveListener onMoveJoystickThrottle = new JoystickView.OnMoveListener(){
		public void onMove(int angle, int strength) {
			Log.v(TAG, "onMoveJoystickThrottle(int angle, int strength): " + angle + ", " + strength);

			if ((angle == 0) && (strength == 0)) {
				seekBarThrottle.setProgress(OrbitSingleton.getInstance().defaultJoystickThrottle);
			}
//			else if ((angle >= 0) && (angle <= 180)) {
			else if ((angle >= 30) && (angle <= 150)) {
				// Up
				int newX = (int) (seekBarThrottle.getMax() * (strength / 100.0));
				// Ensure lower half of seekBarThrottle can be accessed from the top half of throttle joystick
				seekBarThrottle.setProgress(newX);
			}
//			else if ((angle >= 180) && (angle <= 359)) {
			else if ((angle >= 210) && (angle <= 330)) {
				// Down
				seekBarThrottle.setProgress(0);
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
		Integer[] command =  {
				  seekBarThrottle.getProgress(),
				  seekBarYaw.getMax() - seekBarYaw.getProgress(),
				  seekBarPitch.getProgress(),
				  1};

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

		Log.d(TAG, "playControl()");

		OrbitSingleton.getInstance().flightActive = true;

		OrbitSingleton.getInstance().audioHandler.ifFlip = OrbitSingleton.getInstance().invertControlSignal; // if checked then flip

		int channel = 0; // default "A"

		updateAudioHandlerLoopNumberWhileMindControl(-1); // Loop infinite for easier user testing

		updateAudioHandlerChannel(channel);

		OrbitSingleton.getInstance().audioHandler.mutexNotify();

	} // playControl


	// ################################################################

	public void stopControl() {

		Log.d(TAG, "stopControl()");

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




}
