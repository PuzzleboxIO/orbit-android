package io.puzzlebox.orbit.ui;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.VideoView;

import io.puzzlebox.orbit.R;

/**
 * Created by sc on 5/8/15.
 */

public class WelcomeFragment extends io.puzzlebox.jigsaw.ui.WelcomeFragment {

	/**
	 * Configuration
	 */
	static String URL = "file:///android_asset/tutorial/index.html";

	private VideoView mVideoView;
	private int position = 0;

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
			public void onCompletion (MediaPlayer mp) {
				position = 0;
				mVideoView.seekTo(position);
				mVideoView.start();
			}
		});




		return v;

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