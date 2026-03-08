package io.puzzlebox.orbit.ui;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import androidx.annotation.NonNull;

import io.puzzlebox.jigsaw.data.ConfigurationSingleton;
import io.puzzlebox.orbit.R;

public class WelcomeFragment extends io.puzzlebox.jigsaw.ui.WelcomeFragment {

	private final static String TAG = WelcomeFragment.class.getSimpleName();

	/**
	 * Configuration
	 */
	private VideoView mVideoView;
	private int position = 0;

	private OnDevicesListener mListenerDevices;

	public interface OnDevicesListener {
		void loadDevices();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {

		// Inflate the layout for this fragment
		View v = inflater.inflate(io.puzzlebox.orbit.R.layout.fragment_welcome, container, false);

		// Background video
		mVideoView = v.findViewById(R.id.video_view);

		try {
			mVideoView.setVideoURI(Uri.parse("android.resource://" +
					requireActivity().getPackageName() +
					"/" + R.raw.splash_puzzlebox_orbit));

		} catch (Exception e) {
			Log.e(TAG, "Error setting video URI", e);
		}

		mVideoView.requestFocus();
		mVideoView.setOnPreparedListener(mp -> {
			// Close the progress bar and play the video
			mVideoView.seekTo(position);
			if (position == 0) {
				mVideoView.start();
			} else {
				mVideoView.pause();
			}
		});

		mVideoView.setOnCompletionListener(mp -> {
			position = 0;
			mVideoView.seekTo(position);
			mVideoView.start();
		});

		LinearLayout llWelcomeMessage = v.findViewById(R.id.layoutWelcomeMessage);
		llWelcomeMessage.setOnClickListener(v1 -> loadMain());

		RelativeLayout relativeLayoutWelcome = v.findViewById(R.id.relativeLayoutWelcome);
		relativeLayoutWelcome.setOnClickListener(v1 -> loadMain());

		ImageView imageViewLogo = v.findViewById(R.id.imageViewLogo);

		// Set logo banner to ~15% of vertical screen size
		int newHeight = (int) (0.15 * ConfigurationSingleton.getInstance().displayHeight);
		Log.d(TAG, "newHeight: " + newHeight);

		RelativeLayout.LayoutParams rlParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT, newHeight);
		rlParams.setMargins(16, 2, 16, 2);
		rlParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		imageViewLogo.setLayoutParams(rlParams);

		return v;
	}

	public void loadMain() {
		if (mListenerDevices != null)
			mListenerDevices.loadDevices();
		else
			Log.d(TAG, "mListenerDevices was null");
	}

	@Override
	public void onAttach(@NonNull Context context) {
		super.onAttach(context);
		try {
			mListenerDevices = (OnDevicesListener) context;
		} catch (ClassCastException e) {
			throw new ClassCastException(context + " must implement mListenerDevices");
		}
	}

}
