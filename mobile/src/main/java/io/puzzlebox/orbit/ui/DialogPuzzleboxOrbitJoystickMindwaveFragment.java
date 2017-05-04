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
import android.widget.SeekBar;

import io.puzzlebox.jigsaw.ui.JoystickView;
import io.puzzlebox.orbit.R;

public class DialogPuzzleboxOrbitJoystickMindwaveFragment extends DialogFragment {

	private final static String TAG = DialogPuzzleboxOrbitJoystickMindwaveFragment.class.getSimpleName();

	// UI
	public SeekBar seekBarX;
	public SeekBar seekBarY;
	Button buttonDeviceEnable;


	private OnFragmentInteractionListener mListener;

	public DialogPuzzleboxOrbitJoystickMindwaveFragment() {
		// Required empty public constructor
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
									 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View v = inflater.inflate(R.layout.dialog_profile_puzzlebox_orbit_joystick_mindwave, container, false);

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

		return v;	}

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

	private JoystickView.OnMoveListener onMoveJoystick = new JoystickView.OnMoveListener(){
		public void onMove(int angle, int strength) {
			Log.v(TAG, "onMoveJoystick(int angle, int strength): " + angle + ", " + strength);

			if ((angle >= 60) && (angle <= 120)) {
				// Up
//				int newThrottle = seekBarX.getMax() - OrbitSingleton.getInstance().defaultControlThrottle;
				int newX = seekBarX.getMax() / 2;
				newX = (int) (newX * (strength / 100.0));
//				newThrottle = OrbitSingleton.getInstance().defaultControlThrottle + newThrottle;
//				newX = newX.getInstance().defaultControlThrottle + newX;
				newX = seekBarX.getMax() / 2 + newX;
				seekBarX.setProgress(newX);
			}
			else if ((angle >= 240) && (angle <= 300)) {
				// Down
//				int newX = OrbitSingleton.getInstance().defaultControlThrottle;
				int newX = seekBarX.getMax() / 2;
				newX = (int) (newX * (strength / 100.0));
//				newX = OrbitSingleton.getInstance().defaultControlThrottle - newThrottle;
				newX = seekBarX.getMax() / 2 - newX;
				seekBarX.setProgress(newX);
			}
			if ((angle >= 150) && (angle <= 210)) {
				// Left
				int newY = seekBarY.getMax() / 2;
				newY = (int) (newY * (strength / 100.0));
				newY = seekBarY.getMax() / 2 - newY;
				seekBarY.setProgress(newY);
			}
			else if ((angle >= 330) || (angle <= 30)) {
				// Right
				int newY = seekBarY.getMax() / 2;
				newY = (int) (newY * (strength / 100.0));
				newY = seekBarY.getMax() / 2 + newY;
				seekBarY.setProgress(newY);
			}

		}
	};


}
