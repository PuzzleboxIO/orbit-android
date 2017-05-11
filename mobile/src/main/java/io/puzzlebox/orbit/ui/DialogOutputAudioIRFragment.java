package io.puzzlebox.orbit.ui;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import io.puzzlebox.orbit.R;
import io.puzzlebox.orbit.data.OrbitSingleton;

/**
 * Created by sc on 5/8/17.
 */

//public class DialogOutputAudioIRFragment extends io.puzzlebox.jigsaw.ui.DialogAudioIRFragment {
public class DialogOutputAudioIRFragment extends DialogFragment {

	private final static String TAG = DialogOutputAudioIRFragment.class.getSimpleName();

	public final static String profileID = "puzzlebox_orbit_ir";

	// UI
	public Switch switchDetectTransmitter;
	public Switch switchDetectVolume;
	Button buttonDeviceEnable;
	Button buttonTestAudioIR;

	AudioManager audioManager;
	public int volumeMax;

	public boolean warningDetectTransmitterDisplayed = false;
	public boolean warningDetectVolumeDisplayed = false;

	private OnFragmentInteractionListener mListener;

	public DialogOutputAudioIRFragment() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
									 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View v = inflater.inflate(io.puzzlebox.jigsaw.R.layout.dialog_output_audio_ir, container, false);

		getDialog().getWindow().setTitle( getString(io.puzzlebox.jigsaw.R.string.title_dialog_fragment_audio_ir));
//		getDialog().getWindow().setTitle( getString(R.string.label_audio_ir_instruction));

		switchDetectTransmitter = (Switch) v.findViewById(io.puzzlebox.jigsaw.R.id.switchDetectTransmitter);
		switchDetectTransmitter.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				switchDetectTransmitterClicked(v);
			}
		});

		switchDetectVolume = (Switch) v.findViewById(io.puzzlebox.jigsaw.R.id.switchDetectVolume);
		switchDetectVolume.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				switchDetectVolumeClicked(v);
			}
		});

		buttonTestAudioIR = (Button) v.findViewById(io.puzzlebox.jigsaw.R.id.buttonTestAudioIR);
		buttonTestAudioIR.setVisibility(View.VISIBLE);
		buttonTestAudioIR.setEnabled(true);
		buttonTestAudioIR.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				demoMode(v);
			}
		});

		Button buttonDeviceCancel = (Button) v.findViewById(io.puzzlebox.jigsaw.R.id.buttonDeviceCancel);
		buttonDeviceCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				broadcastTileStatus("false");
				dismiss();
			}
		});

		buttonDeviceEnable = (Button) v.findViewById(io.puzzlebox.jigsaw.R.id.buttonDeviceEnable);
		buttonDeviceEnable.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				updateTileStatus();
				dismiss();
			}
		});

		audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
		volumeMax = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);


		/**
		 * AudioHandler
		 */
		if (!OrbitSingleton.getInstance().audioHandler.isAlive()) {
			OrbitSingleton.getInstance().audioHandler.start();
		}


		return v;
	}


	// ################################################################

	public void demoMode(View v) {

		/**
		 * Demo mode is called when the "Test Helicopter" button is pressed.
		 * This method can be easily adjusted for testing new features
		 * during development.
		 */

		Log.v(TAG, "Test Signal clicked");

		if (! OrbitSingleton.getInstance().flightActive) {

			OrbitSingleton.getInstance().flightActive = true;
			OrbitSingleton.getInstance().demoActive = true;

			buttonTestAudioIR.setText( getResources().getString(R.string.buttonTestAudioIRStop) );

			playControl();

		} else {

			OrbitSingleton.getInstance().flightActive = false;
			OrbitSingleton.getInstance().demoActive = false;

			stopControl();

			buttonTestAudioIR.setText(getResources().getString(R.string.buttonTestAudioIR));

		}


	} // demoMode


	// ################################################################

//	public void demoStop(View view) {
//
//		stopControl();
//
//	} // demoStop


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

		OrbitSingleton.getInstance().audioHandler.channel = channel;
		OrbitSingleton.getInstance().audioHandler.updateControlSignal();


	} // updateServiceBinderChannel

	// ################################################################

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	public interface OnFragmentInteractionListener {
		void onFragmentInteraction(Uri uri);
	}

	// ################################################################

	public void updateTileStatus() {

//		Log.e(TAG, "updateTileStatus()");

		String value;
		if (buttonDeviceEnable.isEnabled())
			value = "true";
		else
			value = "false";

		broadcastTileStatus(value);

	}

	// ################################################################

	public void broadcastTileStatus(String value) {

		Intent intent = new Intent("io.puzzlebox.jigsaw.protocol.tile.event");

		intent.putExtra("id", profileID);
		intent.putExtra("name", "active");
		intent.putExtra("value", value);
		intent.putExtra("category", "outputs");

//		LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
		LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);

	}

	// ################################################################

	public void onPause() {

		super.onPause();

		stopControl();

	} // onPause


	// ################################################################

	@Override
	public void onResume() {
		super.onResume();

		if ((audioManager != null) &&
				  ((audioManager.isWiredHeadsetOn()))) {
			switchDetectTransmitter.setChecked(true);

			if (!checkVolumeMax()) {
				Log.d(TAG, "Attempting to set Media volume to max");
				audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volumeMax, 0);
			}

			if (checkVolumeMax()) {
				switchDetectVolume.setChecked(true);
			}

		}

		updateReadyButton();

	}

	// ################################################################

	public void switchDetectTransmitterClicked(View v) {
		Log.v(TAG, "switchDetectTransmitterClicked: " + switchDetectTransmitter.isChecked());

		if (switchDetectVolume.isChecked()) {
			// Volume must be turned up to max each time transmitter is connected
			switchDetectVolume.setChecked(false);
		}

		// When Audio IR is not prepared hide Ready button from view
		buttonDeviceEnable.setEnabled(false);
		buttonDeviceEnable.setVisibility(View.INVISIBLE);

		// Check to see if headphone jack detects transmitter and if not Toast a warning message
		if (! audioManager.isWiredHeadsetOn() &&
				  (! warningDetectTransmitterDisplayed)) {
			Toast.makeText(getActivity().getApplicationContext(), getString(io.puzzlebox.jigsaw.R.string.toast_audio_ir_detect_transmitter_warning), Toast.LENGTH_LONG).show();
			warningDetectTransmitterDisplayed = true;
		}

//		Log.i("WiredHeadsetOn = ", audioManager.isWiredHeadsetOn()+"");
//		Log.i("MusicActive = ", audioManager.isMusicActive()+"");
//		Log.i("SpeakerphoneOn = ", audioManager.isSpeakerphoneOn()+"");

	}

	// ################################################################

	public void switchDetectVolumeClicked(View v) {
		Log.v(TAG, "switchDetectVolumeClicked: " + switchDetectVolume.isChecked());

		// Check to see if volume is max and if not Toast a warning message
		if ((! checkVolumeMax()) &&
				  (switchDetectVolume.isChecked()) &&
				  (! warningDetectVolumeDisplayed)) {
			Toast.makeText(getActivity().getApplicationContext(), getString(io.puzzlebox.jigsaw.R.string.toast_audio_ir_detect_volume_max_warning), Toast.LENGTH_LONG).show();
			warningDetectVolumeDisplayed = true;
		}

		updateReadyButton();

	}


	public boolean checkVolumeMax() {
		boolean value = false;
		if ((audioManager != null) &&
				  (audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) ==
							 volumeMax)) {
//							 audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC))) {
			value = true;
		}
		else {
			Log.i(TAG, "getStreamVolume(AudioManager.STREAM_MUSIC):" +
					  audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
			Log.i(TAG, "getStreamMaxVolume(AudioManager.STREAM_MUSIC):" +
					  audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
		}

		return value;
	}

	public void updateReadyButton() {
		if ((switchDetectVolume.isChecked()) && (switchDetectTransmitter.isChecked())) {
			// Once Audio IR is prepared present Ready button
			buttonDeviceEnable.setEnabled(true);
			buttonDeviceEnable.setVisibility(View.VISIBLE);
		} else {
			// When Audio IR is not prepared hide Ready button from view
			buttonDeviceEnable.setEnabled(false);
			buttonDeviceEnable.setVisibility(View.INVISIBLE);
		}
	}


	// ################################################################

	// TODO
	public void maximizeAudioVolume() {

		AudioManager audio = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
		int currentVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);

		if (currentVolume < audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC)) {

			Log.v(TAG, "Previous volume:" + currentVolume);

			Toast.makeText(getActivity().getApplicationContext(), "Automatically setting volume to maximum", Toast.LENGTH_SHORT).show();

			AudioManager audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
			audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
					  audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
					  AudioManager.FLAG_SHOW_UI);

		}


	} // maximizeAudioVolume


	// ################################################################



}