package io.puzzlebox.orbit.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import io.puzzlebox.orbit.R;

/**
 * Created by sc on 5/8/15.
 */

public class WelcomeFragment extends io.puzzlebox.jigsaw.ui.WelcomeFragment {

	/**
	 * Configuration
	 */
	static String URL = "file:///android_asset/tutorial/index.html";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
//		return inflater.inflate(io.puzzlebox.jigsaw.R.layout.fragment_welcome, container, false);

		View v = inflater.inflate(R.layout.fragment_welcome, container, false);

		WebView webview = (WebView) v.findViewById(R.id.webViewWelcome);

		webview.getSettings().setJavaScriptEnabled(true);

		webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(false);

		webview.setWebViewClient(new compatibilityWebViewClient());

		webview.loadUrl(URL);

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