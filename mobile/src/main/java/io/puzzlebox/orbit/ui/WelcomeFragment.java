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
import android.widget.LinearLayout;
import android.widget.VideoView;

import io.puzzlebox.orbit.R;

/**
 * Created by sc on 5/8/15.
 */

public class WelcomeFragment extends io.puzzlebox.jigsaw.ui.WelcomeFragment {

	private final static String TAG = WelcomeFragment.class.getSimpleName();

	/**
	 * Configuration
	 */

	static String URL = "file:///android_asset/tutorial/index.html";

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
		// Inflate the layout for this fragment
//		View v = inflater.inflate(io.puzzlebox.jigsaw.R.layout.fragment_welcome, container, false);
		View v = inflater.inflate(io.puzzlebox.orbit.R.layout.fragment_welcome, container, false);


//		WebView webview = (WebView) v.findViewById(R.id.webViewWelcome);
//
//		webview.getSettings().setJavaScriptEnabled(true);
//
//		webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(false);
//
//		webview.setWebViewClient(new compatibilityWebViewClient());
//
//		webview.loadUrl(URL);


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


		LinearLayout ll = (LinearLayout) v.findViewById(R.id.layoutWelcome);
		ll.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d(TAG, "onClick");
//				if (mListenerTutorial != null)
//					mListenerTutorial.loadTutorial();
//				else
//					Log.d(TAG, "mListenerTutorial was null");
				if (mListenerDevices != null)
					mListenerDevices.loadDevices();
				else
					Log.d(TAG, "mListenerDevices was null");
			}
		});


		return v;

	}


	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
//			mListenerTutorial = (OnTutorialListener) activity;
			mListenerDevices = (OnDevicesListener) activity;
		} catch (ClassCastException e) {
//			throw new ClassCastException(activity.toString() + " must implement mListenerTutorial");
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