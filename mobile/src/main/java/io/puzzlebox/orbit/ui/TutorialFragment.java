package io.puzzlebox.orbit.ui;

import android.os.Bundle;
import androidx.appcompat.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import io.puzzlebox.orbit.R;

public class TutorialFragment extends io.puzzlebox.jigsaw.ui.WelcomeFragment {

	private final static String TAG = TutorialFragment.class.getSimpleName();

	/**
	 * Configuration
	 */
	static String URL = "file:///android_asset/tutorial/contents.html";

	WebView webView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View v = inflater.inflate(R.layout.fragment_tutorial, container, false);

		LinearLayout dynamicLayout = (LinearLayout) v.findViewById(R.id.dynamicLayout);

		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				  LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT);

		LinearLayout ll = new LinearLayout(getActivity().getBaseContext());
		ll.setOrientation(LinearLayout.VERTICAL);

		ll.setLayoutParams(params);

		webView = new WebView(getActivity()){

			@Override
			public boolean onKeyDown(int keyCode, KeyEvent event) {
				Log.d(TAG, "onTouch()");

				if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
					webView.goBack();
					return true;
				}
				return super.onKeyDown(keyCode, event);
			}
		};

		webView.setLayoutParams(params);

		webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(false);

		webView.setWebViewClient(new compatibilityWebViewClient());

		webView.loadUrl(URL);

		ll.addView(webView);

		dynamicLayout.addView(ll);

		return v;
	}

	private static class compatibilityWebViewClient extends WebViewClient {
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
