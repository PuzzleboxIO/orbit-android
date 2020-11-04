package io.puzzlebox.orbit.ui;

import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.puzzlebox.orbit.R;

public class SupportFragment extends io.puzzlebox.jigsaw.ui.WelcomeFragment {

	private final static String TAG = SupportFragment.class.getSimpleName();

	/**
	 * Configuration
	 */
	static String supportURL = "file:///android_asset/tutorial/support.html";
	static String contactURL = "http://puzzlebox.io/cgi-bin/puzzlebox/support_contact/puzzlebox_orbit_support_gateway.py";
	String versionName = "";

	/**
	 * UI
	 */
	EditText editTextName;
	EditText editTextEmail;
	EditText editTextMessage;
	Button buttonSendMessage;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {

		// Inflate the layout for this fragment
		View v = inflater.inflate(R.layout.fragment_support, container, false);

		try {
			versionName = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;
		}
		catch (PackageManager.NameNotFoundException e) {
			Log.e(TAG, e.getMessage());
		}

		editTextName = (EditText) v.findViewById(R.id.editTextName);
		editTextEmail = (EditText) v.findViewById(R.id.editTextEmail);
		editTextMessage = (EditText) v.findViewById(R.id.editTextMessage);

		buttonSendMessage = (Button) v.findViewById(R.id.buttonSendMessage);
		buttonSendMessage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				sendMessage();
			}
		});

		WebView webview = (WebView) v.findViewById(R.id.webViewSupport);

		webview.getSettings().setJavaScriptEnabled(true);

		webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(false);

		webview.loadUrl(supportURL);

		return v;
	}

	public void sendMessage() {
		Toast.makeText(getActivity().getApplicationContext(), "Thank you for your feedback!", Toast.LENGTH_LONG).show();

		String name = editTextName.getText().toString();
		String email = editTextEmail.getText().toString();
		String message = editTextMessage.getText().toString();

		message = message + "\n\n" + getDeviceDetails();

		EmailMessage emailMessage = new EmailMessage();

		emailMessage.setData(contactURL, name, email, message);

		emailMessage.execute();

		editTextName.setText("");
		editTextEmail.setText("");
		editTextMessage.setText("");
	}

	public String getDeviceDetails() {

		String output = "";

		output = "Manufacturer: " + Build.MANUFACTURER + "\n";
		output = output + "Model: " + Build.MODEL + "\n";
		output = output + "Product: " + Build.PRODUCT + "\n";
		if (android.os.Build.VERSION.SDK_INT >= 8)
			output = output + "Hardware: " + Build.HARDWARE + "\n";
		output = output + "Device: " + Build.DEVICE + "\n";
		output = output +
				getResources().getString(R.string.app_name) +
				" Version: " + versionName + "\n";
		output = output + "Android Version: " + android.os.Build.VERSION.SDK_INT + "\n";

		return (output);
	}


	class EmailMessage extends AsyncTask<String, Void, Object> {

		String contactURL = "";
		String name = "";
		String email = "";
		String message = "";

		public void setData(String contact, String full_name, String email_address, String content) {
			contactURL = contact;
			name = full_name;
			email = email_address;
			message = content;
		}

		protected Object doInBackground(String... vars) {
			try {
				// Create a new HttpClient and Post Header
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(contactURL);

				try {
					// Add your data
					List<NameValuePair> nameValuePairs = new ArrayList<>(2);
					nameValuePairs.add(new BasicNameValuePair("email_name", name));
					nameValuePairs.add(new BasicNameValuePair("email_from", email));
					nameValuePairs.add(new BasicNameValuePair("email_subject", "[" +
							getResources().getString(R.string.app_name) +
							" Support] (Android " + versionName + ")"));
					nameValuePairs.add(new BasicNameValuePair("email_body", message));
					httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

					// Execute HTTP Post Request
					@SuppressWarnings("unused")
					HttpResponse response = httpclient.execute(httppost);

				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	}
}
