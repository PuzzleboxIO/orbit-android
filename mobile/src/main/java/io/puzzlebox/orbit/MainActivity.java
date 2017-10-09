package io.puzzlebox.orbit;

//import android.app.Fragment;
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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import io.puzzlebox.jigsaw.data.DeviceEmotivInsightSingleton;
import io.puzzlebox.jigsaw.data.SessionSingleton;
import io.puzzlebox.jigsaw.protocol.EmotivInsightService;
import io.puzzlebox.jigsaw.ui.DialogInputEmotivInsightFragment;
import io.puzzlebox.jigsaw.ui.DialogInputJoystickFragment;
import io.puzzlebox.jigsaw.ui.DialogInputNeuroSkyMindWaveFragment;
import io.puzzlebox.jigsaw.ui.DialogOutputSessionFragment;
import io.puzzlebox.jigsaw.ui.DrawerItem;
//import io.puzzlebox.jigsaw.ui.EEGFragment;
//import io.puzzlebox.jigsaw.ui.SessionFragment;
import io.puzzlebox.jigsaw.ui.SupportFragment;
import io.puzzlebox.orbit.ui.DialogOutputAudioIRFragment;
import io.puzzlebox.orbit.ui.DialogProfilePuzzleboxOrbitEmotivInsightFragment;
import io.puzzlebox.orbit.ui.DialogProfilePuzzleboxOrbitFragment;
import io.puzzlebox.orbit.ui.DialogProfilePuzzleboxOrbitJoystickFragment;
//import io.puzzlebox.orbit.ui.SteeringFragment;
//import io.puzzlebox.orbit.ui.AdvancedFragment;
import io.puzzlebox.orbit.ui.CreditsFragment;
//import io.puzzlebox.orbit.ui.OrbitFragment;
import io.puzzlebox.orbit.ui.DialogProfilePuzzleboxOrbitJoystickMindwaveFragment;
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
		  DialogInputEmotivInsightFragment.OnFragmentInteractionListener,
		  DialogOutputAudioIRFragment.OnFragmentInteractionListener,
		  DialogOutputSessionFragment.OnFragmentInteractionListener,
		  DialogProfilePuzzleboxOrbitJoystickFragment.OnFragmentInteractionListener,
		  DialogProfilePuzzleboxOrbitFragment.OnFragmentInteractionListener,
		  DialogProfilePuzzleboxOrbitJoystickMindwaveFragment.OnFragmentInteractionListener,
		  DialogProfilePuzzleboxOrbitEmotivInsightFragment.OnFragmentInteractionListener
{

	private final static String TAG = MainActivity.class.getSimpleName();

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	private CharSequence mTitle;
	private CharSequence mDrawerTitle;
	List<DrawerItem> dataList;

	private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

	// ################################################################

	@Override
	protected void onCreateCustom() {

		// For use with custom applications

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		// Hide default keyboard popup
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


		// Hide default keyboard popup
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		// Permission must be requested to scan Bluetooth in Android 6.0 and later
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			// Android M permission check
			if (this.checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
				final AlertDialog.Builder builder = new AlertDialog.Builder(this);
//				builder.setTitle("This app needs your permission");
//				builder.setTitle(getResources().getString(android.R.string.main_dialog_title));
//				builder.setMessage(getResources().getString(android.R.string.main_dialog_message));
				builder.setTitle(getResources().getString(R.string.main_dialog_title));
				builder.setMessage(getResources().getString(R.string.main_dialog_message));
				builder.setPositiveButton(android.R.string.ok, null);
				builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
					@Override
					@TargetApi(Build.VERSION_CODES.M)
					public void onDismiss(DialogInterface dialog) {
						requestPermissions(new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
					}
				});
				builder.show();
			}
		}

		dataList = getDrawerDataList();

		mTitle = mDrawerTitle = getTitle();
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.navigation_drawer);


		Toolbar mToolbar = new Toolbar(this);

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				  mToolbar, io.puzzlebox.jigsaw.R.string.drawer_open,
				  io.puzzlebox.jigsaw.R.string.drawer_close) {
			public void onDrawerClosed(View view) {
//                                getSupportActionBar().setTitle(mTitle);
				invalidateOptionsMenu(); // creates call to
				// onPrepareOptionsMenu()
			}

			public void onDrawerOpened(View drawerView) {
				getSupportActionBar().setTitle(mDrawerTitle);
				invalidateOptionsMenu(); // creates call to
				// onPrepareOptionsMenu()
			}
		};

		mDrawerLayout.setDrawerListener(mDrawerToggle);

	}


	// ################################################################

	protected List<DrawerItem> getDrawerDataList() {
		List<DrawerItem> dataList = new ArrayList<>();

		dataList.add(new DrawerItem(getString(io.puzzlebox.jigsaw.R.string.title_fragment_welcome), io.puzzlebox.jigsaw.R.mipmap.ic_puzzlebox));
		dataList.add(new DrawerItem(getString(R.string.title_fragment_tiles), R.mipmap.ic_joystick));
		dataList.add(new DrawerItem(getString(R.string.title_fragment_tutorial), R.mipmap.ic_support));
		dataList.add(new DrawerItem(getString(R.string.title_fragment_support), io.puzzlebox.jigsaw.R.mipmap.ic_brain));
		dataList.add(new DrawerItem(getString(R.string.title_fragment_credits), io.puzzlebox.jigsaw.R.mipmap.ic_puzzlebox));


		return dataList;
	}


	// ################################################################

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
		} else {
			Log.w(TAG, "mDrawerList == null");
		}

	}


	// ################################################################

	public void loadTutorial() {

		Fragment fragment = null;
		Bundle args = new Bundle();
		String backStackName = getResources().getString(R.string.title_fragment_tutorial);

		try{
			fragment = getSupportFragmentManager().findFragmentByTag(backStackName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (fragment == null)
			fragment = new TutorialFragment();

		fragment.setArguments(args);
		FragmentManager frgManager = getSupportFragmentManager();
		frgManager.beginTransaction().replace(io.puzzlebox.jigsaw.R.id.container, fragment)
				  .addToBackStack(backStackName)
				  .commit();

	}


	// ################################################################

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


	// ################################################################

	@Override
	public void onRequestPermissionsResult(int requestCode,
														String permissions[], int[] grantResults) {

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
	}


	// ################################################################

	/**
	 * Class for interacting with the main interface of the service.
	 */
	private ServiceConnection mConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			// This is called when the connection with the service has been
			// established, giving us the object we can use to
			// interact with the service.  We are communicating with the
			// service using a Messenger, so here we get a client-side
			// representation of that from the raw IBinder object.
			DeviceEmotivInsightSingleton.getInstance().mService = new Messenger(service);
			DeviceEmotivInsightSingleton.getInstance().mBound = true;
		}

		public void onServiceDisconnected(ComponentName className) {
			// This is called when the connection with the service has been
			// unexpectedly disconnected -- that is, its process crashed.
			DeviceEmotivInsightSingleton.getInstance().mService = null;
			DeviceEmotivInsightSingleton.getInstance().mBound = false;
		}
	};

	@Override
	protected void onStart() {
		super.onStart();
		// Bind to the service
		bindService(new Intent(this, EmotivInsightService.class), mConnection,
				  Context.BIND_AUTO_CREATE);
	}

	@Override
	protected void onStop() {
		super.onStop();
		// Unbind from the service
		if (DeviceEmotivInsightSingleton.getInstance().mBound) {
			unbindService(mConnection);
			DeviceEmotivInsightSingleton.getInstance().mBound = false;
		}
	}


}
