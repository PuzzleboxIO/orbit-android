package io.puzzlebox.orbit;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import io.puzzlebox.jigsaw.ui.DrawerItem;
import io.puzzlebox.jigsaw.ui.EEGFragment;
import io.puzzlebox.jigsaw.ui.SessionFragment;
import io.puzzlebox.jigsaw.ui.SupportFragment;
import io.puzzlebox.orbit.ui.CreditsFragment;
import io.puzzlebox.orbit.ui.OrbitFragment;
import io.puzzlebox.orbit.ui.TutorialFragment;
import io.puzzlebox.orbit.ui.WelcomeFragment;


public class MainActivity extends io.puzzlebox.jigsaw.ui.MainActivity implements
        WelcomeFragment.OnFragmentInteractionListener,
        SessionFragment.OnFragmentInteractionListener,
        EEGFragment.OnFragmentInteractionListener,
        OrbitFragment.OnFragmentInteractionListener,
        SupportFragment.OnFragmentInteractionListener
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
                dataList.add(new DrawerItem(getString(R.string.title_fragment_tutorial), R.mipmap.ic_brain));
                dataList.add(new DrawerItem(getString(io.puzzlebox.jigsaw.R.string.title_fragment_session), io.puzzlebox.jigsaw.R.mipmap.ic_session));
                dataList.add(new DrawerItem(getString(io.puzzlebox.jigsaw.R.string.title_fragment_eeg), io.puzzlebox.jigsaw.R.mipmap.ic_eeg));
                dataList.add(new DrawerItem(getString(R.string.title_fragment_orbit), R.mipmap.ic_orbit));
                dataList.add(new DrawerItem(getString(R.string.title_fragment_support), io.puzzlebox.jigsaw.R.mipmap.ic_support));
                dataList.add(new DrawerItem(getString(R.string.title_fragment_credits), io.puzzlebox.jigsaw.R.mipmap.ic_puzzlebox));


                return dataList;
        }


        // ################################################################

        @Override
        public void SelectItem(int position) {

                android.app.Fragment fragment = null;
                Bundle args = new Bundle();
                String backStackName = "";
                switch (position) {
                        case 0:
                                backStackName = "welcome";
                                try{
                                        fragment = getFragmentManager().findFragmentByTag(backStackName);
                                } catch (Exception e) {
                                        e.printStackTrace();
                                }
                                if (fragment == null)
                                        fragment = new WelcomeFragment();
                                break;
                        case 1:
                                backStackName = "tutorial";
                                try{
                                        fragment = getFragmentManager().findFragmentByTag(backStackName);
                                } catch (Exception e) {
                                        e.printStackTrace();
                                }
                                if (fragment == null)
                                        fragment = new TutorialFragment();
                                break;
                        case 2:
                                backStackName = "session";
                                try{
                                        fragment = getFragmentManager().findFragmentByTag(backStackName);
                                } catch (Exception e) {
                                        e.printStackTrace();
                                }
                                if (fragment == null)
                                        fragment = new SessionFragment();
                                break;
                        case 3:
                                backStackName = "eeg";
                                try{
                                        fragment = getFragmentManager().findFragmentByTag(backStackName);
                                } catch (Exception e) {
                                        e.printStackTrace();
                                }
                                if (fragment == null)
                                        fragment = new EEGFragment();

                                break;
                        case 4:
                                backStackName = "orbit";
                                try{
                                        fragment = getFragmentManager().findFragmentByTag(backStackName);
                                } catch (Exception e) {
                                        e.printStackTrace();
                                }
                                if (fragment == null)
                                        fragment = new OrbitFragment();

                                break;
                        case 5:
                                backStackName = "support";
                                try{
                                        fragment = getFragmentManager().findFragmentByTag(backStackName);
                                } catch (Exception e) {
                                        e.printStackTrace();
                                }
                                if (fragment == null)
                                        fragment = new SupportFragment();

                                break;
                        case 6:
                                backStackName = "credits";
                                try{
                                        fragment = getFragmentManager().findFragmentByTag(backStackName);
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
                android.app.FragmentManager frgManager = getFragmentManager();
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

}
