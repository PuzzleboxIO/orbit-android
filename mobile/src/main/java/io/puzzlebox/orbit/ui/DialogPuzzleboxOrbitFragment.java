package io.puzzlebox.orbit.ui;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;

import io.puzzlebox.jigsaw.ui.DialogJoystickFragment;
import io.puzzlebox.orbit.R;

public class DialogPuzzleboxOrbitFragment extends DialogFragment {

	private final static String TAG = DialogPuzzleboxOrbitFragment.class.getSimpleName();

	// UI
	Button buttonDeviceEnable;

	private OnFragmentInteractionListener mListener;

	public DialogPuzzleboxOrbitFragment() {
		// Required empty public constructor
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
									 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View v = inflater.inflate(R.layout.fragment_dialog_puzzlebox_orbit, container, false);

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

		return v;

	}

	public void onButtonPressed(Uri uri) {
		if (mListener != null) {
			mListener.onFragmentInteraction(uri);
		}
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
}
