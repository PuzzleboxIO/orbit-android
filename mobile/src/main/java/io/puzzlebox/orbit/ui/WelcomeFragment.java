package io.puzzlebox.orbit.ui;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import io.puzzlebox.jigsaw.data.ConfigurationSingleton;
import io.puzzlebox.orbit.R;

public class WelcomeFragment extends io.puzzlebox.jigsaw.ui.WelcomeFragment {

	private final static String TAG = WelcomeFragment.class.getSimpleName();

	/**
	 * Configuration
	 */
	private VideoView mVideoView;
	private int position = 0;

	private OnTutorialListener mListenerTutorial;
	private OnDevicesListener mListenerDevices;

	public interface OnTutorialListener {
		void loadTutorial();
	}

	public interface OnDevicesListener {
		void loadDevices();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {

		LinearLayout.LayoutParams layoutParams;

		// Inflate the layout for this fragment
		View v = inflater.inflate(io.puzzlebox.orbit.R.layout.fragment_welcome, container, false);

		// Background video
		mVideoView = (VideoView) v.findViewById(R.id.video_view);

		try {
			mVideoView.setVideoURI(Uri.parse("android.resource://" +
					getActivity().getPackageName() +
					"/" + R.raw.splash_puzzlebox_orbit));

		} catch (Exception e) {
			Log.e("Error", e.getMessage());
			e.printStackTrace();
		}

		mVideoView.requestFocus();
		mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
			// Close the progress bar and play the video
			public void onPrepared(MediaPlayer mp) {
				mVideoView.seekTo(position);
				if (position == 0) {
					mVideoView.start();
				} else {
					mVideoView.pause();
				}
			}
		});

		mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
			public void onCompletion(MediaPlayer mp) {
				position = 0;
				mVideoView.seekTo(position);
				mVideoView.start();
			}
		});

		LinearLayout llWelcomeMessage = (LinearLayout) v.findViewById(R.id.layoutWelcomeMessage);
		llWelcomeMessage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				loadMain();
			}
		});

		RelativeLayout relativeLayoutWelcome = (RelativeLayout) v.findViewById(R.id.relativeLayoutWelcome);
		relativeLayoutWelcome.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				loadMain();
			}
		});

		LinearLayout llLogo = (LinearLayout) v.findViewById(R.id.linearLayoutLogo);

		ImageView imageViewLogo = (ImageView) v.findViewById(R.id.imageViewLogo);

		layoutParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		layoutParams.setMargins(16, 2, 16, 2);
		imageViewLogo.setLayoutParams(layoutParams);

		// Set logo banner to ~15% of vertical screen size
		int newHeight = (int) (0.15 * ConfigurationSingleton.getInstance().displayHeight);
		Log.d(TAG, "newHeight: " + newHeight);

		layoutParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT, newHeight);

		llLogo.setLayoutParams(layoutParams);

		return v;
	}

	public void loadMain() {
		if (mListenerTutorial != null)
			mListenerTutorial.loadTutorial();
		else
			Log.d(TAG, "mListenerTutorial was null");
		if (mListenerDevices != null)
			mListenerDevices.loadDevices();
		else
			Log.d(TAG, "mListenerDevices was null");
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListenerDevices = (OnDevicesListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement mListenerDevices");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListenerTutorial = null;
	}

	private class compatibilityWebViewClient extends WebViewClient {
		/***
		 * This class prevents Android from launching URLs in external browsers
		 *
		 * credit: http://stackoverflow.com/questions/2378800/clicking-urls-opens-default-browser
		 */
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}
	}
}
