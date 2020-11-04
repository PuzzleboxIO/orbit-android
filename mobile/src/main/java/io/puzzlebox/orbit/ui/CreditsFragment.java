package io.puzzlebox.orbit.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import io.puzzlebox.orbit.R;

public class CreditsFragment extends io.puzzlebox.jigsaw.ui.WelcomeFragment {

	static String URL = "file:///android_asset/tutorial/credits.html";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.fragment_credits, container, false);

		WebView webview = (WebView) v.findViewById(R.id.webViewCredits);

		webview.loadUrl(URL);

		return v;
	}
}
