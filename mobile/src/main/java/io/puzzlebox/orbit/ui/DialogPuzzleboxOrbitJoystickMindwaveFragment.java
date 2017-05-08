package io.puzzlebox.orbit.ui;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.Toast;

import io.puzzlebox.jigsaw.data.ConfigurationSingleton;
import io.puzzlebox.jigsaw.data.ProfileSingleton;
import io.puzzlebox.jigsaw.ui.DialogAudioIRFragment;
import io.puzzlebox.jigsaw.ui.JoystickView;
import io.puzzlebox.orbit.R;
import io.puzzlebox.orbit.data.OrbitSingleton;

public class DialogPuzzleboxOrbitJoystickMindwaveFragment extends DialogFragment
		  implements SeekBar.OnSeekBarChangeListener {

	private final static String TAG = DialogPuzzleboxOrbitJoystickMindwaveFragment.class.getSimpleName();

	public final static String profileID = "profile_puzzlebox_orbit_joystick_mindwave";

	// UI
	public Switch switchThrottlePitch;
	public SeekBar seekBarX;
	public SeekBar seekBarY;

//	public SeekBar seekBarThrottle;
//	public SeekBar seekBarYaw;
//	public SeekBar seekBarPitch;

	Button buttonDeviceEnable;

	private static int paddingJoysticks = 20;


	private OnFragmentInteractionListener mListener;

	public DialogPuzzleboxOrbitJoystickMindwaveFragment() {
		// Required empty public constructor
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
									 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View v = inflater.inflate(R.layout.dialog_profile_puzzlebox_orbit_joystick_mindwave, container, false);

		getDialog().getWindow().setTitle( getString(R.string.title_dialog_fragment_puzzlebox_orbit_joystick_mindwave));

		switchThrottlePitch = (Switch) v.findViewById(R.id.switchThrottlePitch);
		switchThrottlePitch.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
//				onCheckBoxInvertControlSignalClicked(v);
				Log.e(TAG, "switchThrottlePitch.onClick(): " + switchThrottlePitch.isChecked());
				onSwitchClicked(switchThrottlePitch.isChecked());
			}
		});

		seekBarX = (SeekBar) v.findViewById(io.puzzlebox.jigsaw.R.id.seekBarX);
		seekBarX.setProgress(seekBarX.getMax() / 2);
		seekBarX.setOnSeekBarChangeListener(this);

		seekBarY = (SeekBar) v.findViewById(io.puzzlebox.jigsaw.R.id.seekBarY);
		seekBarY.setProgress(seekBarY.getMax() / 2);
		seekBarY.setOnSeekBarChangeListener(this);

		JoystickView joystickView = (JoystickView) v.findViewById(io.puzzlebox.jigsaw.R.id.joystickView);
		joystickView.setOnMoveListener(onMoveJoystick);


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
			OrbitSingleton.getInstance().audioHandler.start();
		}

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

		stopControl();

	} // onPause


	// ################################################################

	public void onResume() {

		super.onResume();

//		if (ProfileSingleton.getInstance().getStatus(profileID).equals("available"))
		if (ProfileSingleton.getInstance().getValue(DialogAudioIRFragment.profileID, "active").equals("true"))
			playControl();
		else
			Toast.makeText(getActivity().getApplicationContext(), getString(R.string.toast_puzzlebox_orbit_joystick_audio_ir_warning), Toast.LENGTH_LONG).show();

	}


	// ################################################################

	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {

//		updateControlSignal();

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
				seekBarX.setProgress(OrbitSingleton.getInstance().defaultJoystickYaw);

				if (! switchThrottlePitch.isChecked())
					seekBarY.setProgress(OrbitSingleton.getInstance().defaultJoystickThrottle);
				else
					seekBarY.setProgress(OrbitSingleton.getInstance().defaultJoystickPitch);
			}
//			else if ((angle >= 0) && (angle <= 180)) {
			else if ((angle >= 30) && (angle <= 150)) {
				// Up

				// Ensure lower half of seekBarThrottle can be accessed from the top half of throttle joystick
				int newY = (int) (seekBarY.getMax() * (strength / 100.0));

				// Set a minimum about of throttle to send if anywhere above zero level
				// of Orbit. Normally it takes some small amount of throttle to trigger
				// any flight or visible reaction.
				if (newY < OrbitSingleton.getInstance().minimumJoystickThrottle)
					newY = OrbitSingleton.getInstance().minimumJoystickThrottle;

				seekBarY.setProgress(newY);

//				newY = seekBarY.getMax() / 2;
//				newY = (int) (newY * (strength / 100.0));
//				newY = seekBarY.getMax() / 2 + newY;
//				seekBarY.setProgress(newY);

			}
//			else if ((angle >= 180) && (angle <= 359)) {
			else if ((angle >= 210) && (angle <= 330)) {
				// Down
				seekBarY.setProgress(0);
			}
			else if ((angle >= 150) && (angle <= 210)) {
				// Left
				int newX = seekBarX.getMax() / 2;
				newX = (int) (newX * (strength / 100.0));
				newX = seekBarX.getMax() / 2 - newX;
				seekBarX.setProgress(newX);
			}
			else if ((angle >= 330) || (angle <= 30)) {
				// Right
				int newX = seekBarX.getMax() / 2;
				newX = (int) (newX * (strength / 100.0));
				newX = seekBarX.getMax() / 2 + newX;
				seekBarX.setProgress(newX);
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

		if (! switchThrottlePitch.isChecked()) {

			Integer[] command = {
					  OrbitSingleton.getInstance().defaultJoystickThrottle,
					  seekBarX.getMax() - seekBarX.getProgress(),
					  seekBarY.getProgress(),
					  1};

			OrbitSingleton.getInstance().audioHandler.command = command;

		} else {
			Integer[] command = {
					  seekBarY.getProgress(),
					  seekBarX.getMax() - seekBarX.getProgress(),
					  OrbitSingleton.getInstance().defaultJoystickPitch,
					  1};

			OrbitSingleton.getInstance().audioHandler.command = command;

		}

//		OrbitSingleton.getInstance().audioHandler.command = command;
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

		int channel = OrbitSingleton.getInstance().defaultChannel;

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
