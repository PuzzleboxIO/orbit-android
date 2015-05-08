package io.puzzlebox.orbit.ui;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import io.puzzlebox.orbit.R;

/**
 * Created by sc on 5/8/15.
 */

public class TutorialFragment extends io.puzzlebox.jigsaw.ui.WelcomeFragment {

	private final static String TAG = TutorialFragment.class.getSimpleName();

	/**
	 * Configuration
	 */
//	static String URL = "file:///android_asset/tutorial/index.html";
	static String URL = "file:///android_asset/tutorial/contents.html";

	WebView webView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
//		View v = inflater.inflate(io.puzzlebox.jigsaw.R.layout.fragment_welcome, container, false);
		View v = inflater.inflate(R.layout.fragment_tutorial, container, false);

//		WebView webview = (WebView) v.findViewById(R.id.webViewTutorial);



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

//				if (keyCode == KeyEvent.KEYCODE_BACK) {
//					Log.d(TAG, "webView.destroy()");
////					webView.stopLoading();
//					webView.destroy();
////					return true;
//				}

				return super.onKeyDown(keyCode, event);
			}

		};


		webView.setLayoutParams(params);


		webView.getSettings().setJavaScriptEnabled(true);

		webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(false);

		webView.setWebViewClient(new compatibilityWebViewClient());

		webView.loadUrl(URL);




		ll.addView(webView);

		dynamicLayout.addView(ll);



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