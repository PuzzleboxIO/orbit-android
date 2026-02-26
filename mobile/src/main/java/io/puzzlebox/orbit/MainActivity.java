package io.puzzlebox.orbit;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.os.Messenger;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import io.puzzlebox.jigsaw.data.SessionSingleton;
import io.puzzlebox.jigsaw.ui.EmotivInsightFragmentListener;
import io.puzzlebox.jigsaw.ui.DialogInputJoystickFragment;
import io.puzzlebox.jigsaw.ui.DialogInputNeuroSkyMindWaveFragment;
import io.puzzlebox.jigsaw.ui.DialogOutputSessionFragment;
import io.puzzlebox.jigsaw.ui.DrawerItem;
import io.puzzlebox.jigsaw.ui.SupportFragment;
import io.puzzlebox.jigsaw.ui.DialogOutputAudioIRFragment;
import io.puzzlebox.jigsaw.ui.DialogProfilePuzzleboxOrbitFragment;
import io.puzzlebox.jigsaw.ui.DialogProfilePuzzleboxOrbitJoystickFragment;
import io.puzzlebox.orbit.ui.CreditsFragment;
import io.puzzlebox.jigsaw.ui.DialogProfilePuzzleboxOrbitJoystickMindwaveFragment;
import io.puzzlebox.orbit.ui.GuideFragment;
import io.puzzlebox.orbit.ui.TutorialFragment;
import io.puzzlebox.orbit.ui.WelcomeFragment;

public class MainActivity extends io.puzzlebox.jigsaw.ui.MainActivity implements
		WelcomeFragment.OnFragmentInteractionListener,
		WelcomeFragment.OnDevicesListener,
		GuideFragment.OnFragmentInteractionListener,
		SupportFragment.OnFragmentInteractionListener,
		DialogInputJoystickFragment.OnFragmentInteractionListener,
		DialogInputNeuroSkyMindWaveFragment.OnFragmentInteractionListener,
		EmotivInsightFragmentListener,
		DialogOutputAudioIRFragment.OnFragmentInteractionListener,
		DialogOutputSessionFragment.OnFragmentInteractionListener,
		DialogProfilePuzzleboxOrbitJoystickFragment.OnFragmentInteractionListener,
		DialogProfilePuzzleboxOrbitFragment.OnFragmentInteractionListener,
		DialogProfilePuzzleboxOrbitJoystickMindwaveFragment.OnFragmentInteractionListener {

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	List<DrawerItem> dataList;

	@Override
	protected void onCreateCustom() {

		// For use with custom applications
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		// Hide default keyboard popup
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		// Hide default keyboard popup
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		// Permission must be requested to use Bluetooth.
		// Android 12+ (API 31+) requires BLUETOOTH_SCAN and BLUETOOTH_CONNECT at runtime.
		// Earlier versions required ACCESS_COARSE_LOCATION for BLE scanning.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
			java.util.ArrayList<String> needed = new java.util.ArrayList<>();
			if (checkSelfPermission(android.Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED)
				needed.add(android.Manifest.permission.BLUETOOTH_SCAN);
			if (checkSelfPermission(android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED)
				needed.add(android.Manifest.permission.BLUETOOTH_CONNECT);
			if (!needed.isEmpty())
				requestPermissions(needed.toArray(new String[0]), PERMISSION_REQUEST_BLUETOOTH);
		} else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			// Android 6.0–11: location permission required for BLE scanning
			if (this.checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
				final AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle(getResources().getString(R.string.main_dialog_title));
				builder.setMessage(getResources().getString(R.string.main_dialog_message));
				builder.setPositiveButton(android.R.string.ok, null);
				builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
					@Override
					@TargetApi(Build.VERSION_CODES.M)
					public void onDismiss(DialogInterface dialog) {
						requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
					}
				});
				builder.show();
			}
		}

		dataList = getDrawerDataList();

		final CharSequence mDrawerTitle = getTitle();
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.navigation_drawer);

		Toolbar mToolbar = new Toolbar(this);

		ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				mToolbar, io.puzzlebox.jigsaw.R.string.drawer_open,
				io.puzzlebox.jigsaw.R.string.drawer_close) {
			public void onDrawerClosed(View view) {
				invalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) {
				getSupportActionBar().setTitle(mDrawerTitle);
				invalidateOptionsMenu();
			}
		};
		mDrawerLayout.addDrawerListener(mDrawerToggle);
	}

	protected List<DrawerItem> getDrawerDataList() {
		List<DrawerItem> dataList = new ArrayList<>();

		dataList.add(new DrawerItem(getString(io.puzzlebox.jigsaw.R.string.title_fragment_welcome), io.puzzlebox.jigsaw.R.mipmap.ic_puzzlebox));
		dataList.add(new DrawerItem(getString(R.string.title_fragment_tiles), R.mipmap.ic_joystick));
		dataList.add(new DrawerItem(getString(R.string.title_fragment_tutorial), R.mipmap.ic_support));
		dataList.add(new DrawerItem(getString(R.string.title_fragment_support), io.puzzlebox.jigsaw.R.mipmap.ic_brain));
		dataList.add(new DrawerItem(getString(R.string.title_fragment_credits), io.puzzlebox.jigsaw.R.mipmap.ic_puzzlebox));

		return dataList;
	}

	@Override
	public void SelectItem(int position) {

		Fragment fragment = null;
		Bundle args = new Bundle();
		String backStackName = "";
		switch (position) {
			case 0:
				backStackName = getResources().getString(R.string.title_fragment_welcome);
				try{
					fragment = getSupportFragmentManager().findFragmentByTag(backStackName);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (fragment == null)
					fragment = new WelcomeFragment();
				break;
			case 1:
				backStackName = getResources().getString(R.string.title_fragment_tiles);
				try{
					fragment = getSupportFragmentManager().findFragmentByTag(backStackName);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (fragment == null)
					fragment = new GuideFragment();
				break;
			case 2:
				backStackName = getResources().getString(R.string.title_fragment_tutorial);
				try{
					fragment = getSupportFragmentManager().findFragmentByTag(backStackName);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (fragment == null)
					fragment = new TutorialFragment();
				break;
			case 3:
				backStackName = getResources().getString(R.string.title_fragment_support);
				try{
					fragment = getSupportFragmentManager().findFragmentByTag(backStackName);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (fragment == null)
					fragment = new SupportFragment();
				break;
			case 4:
				backStackName = getResources().getString(R.string.title_fragment_credits);
				try{
					fragment = getSupportFragmentManager().findFragmentByTag(backStackName);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (fragment == null)
					fragment = new CreditsFragment();
				break;
			default:
				break;
		}
		if (fragment != null)
			fragment.setArguments(args);
		FragmentManager frgManager = getSupportFragmentManager();
		frgManager.beginTransaction().replace(io.puzzlebox.jigsaw.R.id.container, fragment)
				.addToBackStack(backStackName)
				.commit();

		if (mDrawerList != null) {
			mDrawerList.setItemChecked(position, true);
			setTitle(dataList.get(position).getItemName());
			mDrawerLayout.closeDrawer(mDrawerList);
		}
	}

	public void loadDevices() {

		Fragment fragment = null;
		Bundle args = new Bundle();
		String backStackName = getResources().getString(R.string.title_fragment_tiles);

		try{
			fragment = getSupportFragmentManager().findFragmentByTag(backStackName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (fragment == null)
			fragment = new GuideFragment();

		fragment.setArguments(args);
		FragmentManager frgManager = getSupportFragmentManager();
		frgManager.beginTransaction().replace(io.puzzlebox.jigsaw.R.id.container, fragment)
				.addToBackStack(backStackName)
				.commit();
	}

	@Override
	public void onRequestPermissionsResult(int requestCode,
										   @NonNull String[] permissions,
										   @NonNull int[] grantResults) {

		super.onRequestPermissionsResult(requestCode, permissions, grantResults);

		if (requestCode == SessionSingleton.getInstance().getRequestExternalStorage()) {

			// If request is cancelled, the result arrays are empty.
			if (grantResults.length > 0
					&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {

				Intent i = SessionSingleton.getInstance().getExportSessionIntent(this);

				if (i != null) {
					startActivity(i);
				} else {
					Toast.makeText(this.getApplicationContext(), "Error export session data for sharing", Toast.LENGTH_SHORT).show();
				}
			} else {
				Toast.makeText(this.getApplicationContext(), "Error export session data for sharing", Toast.LENGTH_SHORT).show();
			}

		}
		// BLUETOOTH and LOCATION results are handled by super.onRequestPermissionsResult()
	}

	/**
	 * Class for interacting with the Emotiv Insight service.
	 * Uses reflection so this file compiles even when the Emotiv SDK JARs are
	 * absent from the build (the service class is excluded in that case).
	 */
	private final ServiceConnection mConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			try {
				Class<?> cls = Class.forName("io.puzzlebox.jigsaw.data.DeviceEmotivInsightSingleton");
				Object instance = cls.getMethod("getInstance").invoke(null);
				cls.getField("mService").set(instance, new Messenger(service));
				cls.getField("mBound").set(instance, true);
			} catch (ReflectiveOperationException e) { /* SDK absent */ }
		}

		public void onServiceDisconnected(ComponentName className) {
			try {
				Class<?> cls = Class.forName("io.puzzlebox.jigsaw.data.DeviceEmotivInsightSingleton");
				Object instance = cls.getMethod("getInstance").invoke(null);
				cls.getField("mService").set(instance, null);
				cls.getField("mBound").set(instance, false);
			} catch (ReflectiveOperationException e) { /* SDK absent */ }
		}
	};

	@Override
	protected void onStart() {
		super.onStart();
		// Bind to Emotiv service if SDK is available in this build
		try {
			Class<?> serviceClass = Class.forName("io.puzzlebox.jigsaw.service.EmotivInsightService");
			bindService(new Intent(this, serviceClass), mConnection, Context.BIND_AUTO_CREATE);
		} catch (ClassNotFoundException e) { /* Emotiv SDK not available */ }
	}

	@Override
	protected void onStop() {
		super.onStop();
		// Unbind from Emotiv service if it was bound
		try {
			Class<?> cls = Class.forName("io.puzzlebox.jigsaw.data.DeviceEmotivInsightSingleton");
			Object instance = cls.getMethod("getInstance").invoke(null);
			boolean mBound = (boolean) cls.getField("mBound").get(instance);
			if (mBound) {
				unbindService(mConnection);
				cls.getField("mBound").set(instance, false);
			}
		} catch (ReflectiveOperationException e) { /* SDK absent */ }
	}
}
