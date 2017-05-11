package io.puzzlebox.orbit;

//import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import io.puzzlebox.jigsaw.data.SessionSingleton;
import io.puzzlebox.jigsaw.ui.DialogAudioIRFragment;
import io.puzzlebox.jigsaw.ui.DialogJoystickFragment;
import io.puzzlebox.jigsaw.ui.DialogNeuroSkyMindWaveFragment;
import io.puzzlebox.jigsaw.ui.DialogSessionFragment;
import io.puzzlebox.jigsaw.ui.DrawerItem;
//import io.puzzlebox.jigsaw.ui.EEGFragment;
//import io.puzzlebox.jigsaw.ui.SessionFragment;
import io.puzzlebox.jigsaw.ui.SupportFragment;
import io.puzzlebox.orbit.ui.DialogOutputAudioIRFragment;
import io.puzzlebox.orbit.ui.DialogPuzzleboxOrbitFragment;
import io.puzzlebox.orbit.ui.DialogPuzzleboxOrbitJoystickFragment;
//import io.puzzlebox.orbit.ui.SteeringFragment;
//import io.puzzlebox.orbit.ui.AdvancedFragment;
import io.puzzlebox.orbit.ui.CreditsFragment;
//import io.puzzlebox.orbit.ui.OrbitFragment;
import io.puzzlebox.orbit.ui.DialogPuzzleboxOrbitJoystickMindwaveFragment;
import io.puzzlebox.orbit.ui.GuideFragment;
import io.puzzlebox.orbit.ui.TutorialFragment;
import io.puzzlebox.orbit.ui.WelcomeFragment;

public class MainActivity extends io.puzzlebox.jigsaw.ui.MainActivity implements
		  WelcomeFragment.OnFragmentInteractionListener,
		  WelcomeFragment.OnDevicesListener,
		  GuideFragment.OnFragmentInteractionListener,
		  SupportFragment.OnFragmentInteractionListener,
		  DialogNeuroSkyMindWaveFragment.OnFragmentInteractionListener,
		  DialogJoystickFragment.OnFragmentInteractionListener,
//		  DialogAudioIRFragment.OnFragmentInteractionListener,
		  DialogOutputAudioIRFragment.OnFragmentInteractionListener,
		  DialogSessionFragment.OnFragmentInteractionListener,
		  DialogPuzzleboxOrbitFragment.OnFragmentInteractionListener,
		  DialogPuzzleboxOrbitJoystickFragment.OnFragmentInteractionListener,
		  DialogPuzzleboxOrbitJoystickMindwaveFragment.OnFragmentInteractionListener
{

	private final static String TAG = MainActivity.class.getSimpleName();

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	private CharSequence mTitle;
	private CharSequence mDrawerTitle;
	List<DrawerItem> dataList;

	// ################################################################

	@Override
	protected void onCreateCustom() {

		// For use with custom applications

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

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
//		dataList.add(new DrawerItem(getString(io.puzzlebox.jigsaw.R.string.title_fragment_session), io.puzzlebox.jigsaw.R.mipmap.ic_session));
//		dataList.add(new DrawerItem(getString(io.puzzlebox.jigsaw.R.string.title_fragment_eeg), io.puzzlebox.jigsaw.R.mipmap.ic_eeg));
//		dataList.add(new DrawerItem(getString(R.string.title_fragment_orbit), R.mipmap.ic_orbit));
////		dataList.add(new DrawerItem(getString(R.string.title_fragment_steering), R.mipmap.ic_joystick));
////		dataList.add(new DrawerItem(getString(R.string.title_fragment_settings), R.mipmap.ic_settings));
//		dataList.add(new DrawerItem(getString(R.string.title_fragment_tutorial), R.mipmap.ic_brain));
		dataList.add(new DrawerItem(getString(R.string.title_fragment_tutorial), R.mipmap.ic_support));
//		dataList.add(new DrawerItem(getString(R.string.title_fragment_support), io.puzzlebox.jigsaw.R.mipmap.ic_support));
		dataList.add(new DrawerItem(getString(R.string.title_fragment_support), io.puzzlebox.jigsaw.R.mipmap.ic_brain));
		dataList.add(new DrawerItem(getString(R.string.title_fragment_credits), io.puzzlebox.jigsaw.R.mipmap.ic_puzzlebox));


		return dataList;
	}


	// ################################################################

	@Override
	public void SelectItem(int position) {

//		android.app.Fragment fragment = null;
		Fragment fragment = null;
		Bundle args = new Bundle();
		String backStackName = "";
		switch (position) {
			case 0:
				backStackName = getResources().getString(R.string.title_fragment_welcome);
				try{
//					fragment = getFragmentManager().findFragmentByTag(backStackName);
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
//					fragment = getFragmentManager().findFragmentByTag(backStackName);
					fragment = getSupportFragmentManager().findFragmentByTag(backStackName);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (fragment == null)
					fragment = new GuideFragment();
				break;
//			case 2:
//				backStackName = getResources().getString(R.string.title_fragment_session);
//				try{
////					fragment = getFragmentManager().findFragmentByTag(backStackName);
//					fragment = getSupportFragmentManager().findFragmentByTag(backStackName);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//				if (fragment == null)
//					fragment = new SessionFragment();
//				break;
//			case 3:
//				backStackName = getResources().getString(R.string.title_fragment_eeg);
//				try{
////					fragment = getFragmentManager().findFragmentByTag(backStackName);
//					fragment = getSupportFragmentManager().findFragmentByTag(backStackName);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//				if (fragment == null)
//					fragment = new EEGFragment();
//
//				break;
//			case 4:
//				backStackName = getResources().getString(R.string.title_fragment_orbit);
//				try{
////					fragment = getFragmentManager().findFragmentByTag(backStackName);
//					fragment = getSupportFragmentManager().findFragmentByTag(backStackName);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//				if (fragment == null)
//					fragment = new OrbitFragment();
//
//				break;
////			case 5:
////				backStackName = getResources().getString(R.string.title_fragment_steering);
////				try{
////					fragment = getSupportFragmentManager().findFragmentByTag(backStackName);
////				} catch (Exception e) {
////					e.printStackTrace();
////				}
////				if (fragment == null)
////					fragment = new SteeringFragment();
////
////				break;
////			case 5:
////				backStackName = getResources().getString(R.string.title_fragment_settings);
////				try{
//////					fragment = getFragmentManager().findFragmentByTag(backStackName);
////					fragment = getSupportFragmentManager().findFragmentByTag(backStackName);
////				} catch (Exception e) {
////					e.printStackTrace();
////				}
////				if (fragment == null)
////					fragment = new AdvancedFragment();
////
////				break;
//			case 5:
			case 2:
				backStackName = getResources().getString(R.string.title_fragment_tutorial);
				try{
//					fragment = getFragmentManager().findFragmentByTag(backStackName);
					fragment = getSupportFragmentManager().findFragmentByTag(backStackName);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (fragment == null)
					fragment = new TutorialFragment();
				break;
//			case 6:
			case 3:
				backStackName = getResources().getString(R.string.title_fragment_support);
				try{
//					fragment = getFragmentManager().findFragmentByTag(backStackName);
					fragment = getSupportFragmentManager().findFragmentByTag(backStackName);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (fragment == null)
					fragment = new SupportFragment();

				break;
//			case 7:
			case 4:
				backStackName = getResources().getString(R.string.title_fragment_credits);
				try{
//					fragment = getFragmentManager().findFragmentByTag(backStackName);
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
//		android.app.FragmentManager frgManager = getFragmentManager();
//		android.app.FragmentManager frgManager = getSupportFragmentManager();
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
//			fragment = getFragmentManager().findFragmentByTag(backStackName);
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
//			fragment = getFragmentManager().findFragmentByTag(backStackName);
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

//		Log.e(TAG, "onRequestPermissionsResult(" + requestCode + ", " + permissions + ", " + grantResults + ")");

		if (requestCode == SessionSingleton.getInstance().getRequestExternalStorage()) {

//				Log.e(TAG, "requestCode: " + requestCode);

			// If request is cancelled, the result arrays are empty.
			if (grantResults.length > 0
					  && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

//					Log.e(TAG, "permission granted");

				Intent i = SessionSingleton.getInstance().getExportSessionIntent(this);

				if (i != null) {
					startActivity(i);
				} else {
					Toast.makeText(this.getApplicationContext(), "Error export session data for sharing", Toast.LENGTH_SHORT).show();
				}

			} else {

//					Log.e(TAG, "permission denied");
				Toast.makeText(this.getApplicationContext(), "Error export session data for sharing", Toast.LENGTH_SHORT).show();

			}
		}
	}



}
