package io.puzzlebox.orbit.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashMap;

import io.puzzlebox.jigsaw.data.ProfileSingleton;
import io.puzzlebox.jigsaw.ui.DialogAudioIRFragment;
import io.puzzlebox.jigsaw.ui.DialogJoystickFragment;
import io.puzzlebox.jigsaw.ui.DialogNeuroSkyMindWaveFragment;
import io.puzzlebox.jigsaw.ui.DialogSessionFragment;
import io.puzzlebox.jigsaw.ui.TileViewAnimator;
import io.puzzlebox.jigsaw.ui.TilesFragment;
import io.puzzlebox.orbit.R;

public class GuideFragment extends TilesFragment {

	private static final String TAG = GuideFragment.class.getSimpleName();

	/**
	 * Number of items visible in carousels.
	 */
	private static final float INITIAL_ITEMS_COUNT = 2.5F;

	/**
	 * Input carousel container layout
	 */
	private LinearLayout mInputCarouselContainer;
	private LinearLayout mOutputCarouselContainer;
	private LinearLayout mProfileCarouselContainer;

	TypedArray devicesInputResourcesTypedArray;
	TypedArray devicesOutputResourcesTypedArray;
	TypedArray devicesProfileResourcesTypedArray;

	TextView carouselDevicesOutputTextView;
	LinearLayout carouselDevicesOutput;
	TextView carouselDevicesProfileTextView;
	LinearLayout carouselDevicesProfile;

	int imageWidth;

	private OnFragmentInteractionListener mListener;

	public GuideFragment() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
									 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View v = inflater.inflate(io.puzzlebox.jigsaw.R.layout.fragment_tiles, container, false);

		// Get reference to carousel container
		mInputCarouselContainer = (LinearLayout) v.findViewById(io.puzzlebox.jigsaw.R.id.carousel_devices_input);
		mOutputCarouselContainer = (LinearLayout) v.findViewById(io.puzzlebox.jigsaw.R.id.carousel_devices_output);
		mProfileCarouselContainer = (LinearLayout) v.findViewById(io.puzzlebox.jigsaw.R.id.carousel_device_profile);


		// Compute the width of a carousel item based on the screen width and number of initial items.
		final DisplayMetrics displayMetrics = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//		final int imageWidth = (int) (displayMetrics.widthPixels / INITIAL_ITEMS_COUNT);
		imageWidth = (int) (displayMetrics.widthPixels / INITIAL_ITEMS_COUNT);

		// Get the array of input devices resources
//		final TypedArray devicesInputResourcesTypedArray = getResources().obtainTypedArray(io.puzzlebox.jigsaw.R.array.devices_input_icon_array);
//		final TypedArray devicesOutputResourcesTypedArray = getResources().obtainTypedArray(io.puzzlebox.jigsaw.R.array.devices_output_icon_array);
//		final TypedArray devicesProfileResourcesTypedArray = getResources().obtainTypedArray(io.puzzlebox.jigsaw.R.array.devices_profile_icon_array);
		devicesInputResourcesTypedArray = getResources().obtainTypedArray(io.puzzlebox.jigsaw.R.array.devices_input_icon_array);
		devicesOutputResourcesTypedArray = getResources().obtainTypedArray(io.puzzlebox.jigsaw.R.array.devices_output_icon_array);
//		devicesProfileResourcesTypedArray = getResources().obtainTypedArray(io.puzzlebox.jigsaw.R.array.devices_profile_icon_array);
//		devicesInputResourcesTypedArray = getResources().obtainTypedArray(io.puzzlebox.jigsaw.R.array.devices_input_array);
//		devicesOutputResourcesTypedArray = getResources().obtainTypedArray(io.puzzlebox.jigsaw.R.array.devices_output_array);
		devicesProfileResourcesTypedArray = getResources().obtainTypedArray(io.puzzlebox.jigsaw.R.array.devices_profile_array);


		carouselDevicesOutput = (LinearLayout) v. findViewById(R.id.carousel_devices_output);
		carouselDevicesOutputTextView = (TextView) v. findViewById(R.id.carousel_devices_output_textview);
		carouselDevicesProfile = (LinearLayout) v. findViewById(R.id.carousel_device_profile);
		carouselDevicesProfileTextView = (TextView) v. findViewById(R.id.carousel_device_profile_textview);


		displayInputCarousel();

		displayOutputCarousel();

		displayProfileCarousel();


		return v;

	}

	public void displayInputCarousel() {

		ImageView imageItem;

		// Populate the input devices carousel with items

		for (int i = 0 ; i < devicesInputResourcesTypedArray.length() ; ++i) {

			final int index = i;

			// Create new ImageView
			imageItem = new ImageView(getActivity());

			// Set the shadow background
			imageItem.setBackgroundResource(io.puzzlebox.jigsaw.R.drawable.shadow);

			// Set the image view resource
			imageItem.setImageResource(devicesInputResourcesTypedArray.getResourceId(i, -1));
//			imageItem.setImageResource(ProfileSingleton.getInstance().inputs.get(i).get("icon"));

			// Set the size of the image view to the previously computed value
			imageItem.setLayoutParams(new LinearLayout.LayoutParams(imageWidth, imageWidth));

			imageItem.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					showDialog("input", index);
				}
			});

			TileViewAnimator tileViewAnimator = new TileViewAnimator(getContext());
			tileViewAnimator.addView(imageItem);

			/// Add image view to the carousel container
//			mInputCarouselContainer.addView(imageItem);
			mInputCarouselContainer.addView(tileViewAnimator);
		}

		mInputCarouselContainer.setEnabled(true);
		mInputCarouselContainer.setVisibility(View.VISIBLE);
//		carouselDevicesInputTextView.setEnabled(true);
//		carouselDevicesInputTextView.setVisibility(View.VISIBLE);


//		for (HashMap<String, String> data : ProfileSingleton.getInstance().inputs) {
//			imageItem = new ImageView(getActivity());
//			imageItem.setBackgroundResource(io.puzzlebox.jigsaw.R.drawable.shadow);
////			imageItem.setImageResource(devicesOutputResourcesTypedArray.getResourceId(i, -1));
//			imageItem.setImageResource( g );
//			imageItem.setLayoutParams(new LinearLayout.LayoutParams(imageWidth, imageWidth));
//
//			final int index = i;
//			imageItem.setOnClickListener(new View.OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					showDialog("output", index);
//				}
//			});
//
//			TileViewAnimator tileViewAnimator = new TileViewAnimator(getContext());
//			tileViewAnimator.addView(imageItem);
//
////			mOutputCarouselContainer.addView(imageItem);
//			mOutputCarouselContainer.addView(tileViewAnimator);
//		}

	}


	public void displayOutputCarousel() {

		ImageView imageItem;

		// Populate the output devices carousel with items
		for (int i = 0; i < devicesOutputResourcesTypedArray.length(); ++i) {
			imageItem = new ImageView(getActivity());
			imageItem.setBackgroundResource(io.puzzlebox.jigsaw.R.drawable.shadow);
			imageItem.setImageResource(devicesOutputResourcesTypedArray.getResourceId(i, -1));
			imageItem.setLayoutParams(new LinearLayout.LayoutParams(imageWidth, imageWidth));

			final int index = i;
			imageItem.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					showDialog("output", index);
				}
			});

			TileViewAnimator tileViewAnimator = new TileViewAnimator(getContext());
			tileViewAnimator.addView(imageItem);

//			mOutputCarouselContainer.addView(imageItem);
			mOutputCarouselContainer.addView(tileViewAnimator);
		}

		mOutputCarouselContainer.setEnabled(true);
		mOutputCarouselContainer.setVisibility(View.VISIBLE);
		carouselDevicesOutputTextView.setEnabled(true);
		carouselDevicesOutputTextView.setVisibility(View.VISIBLE);

	}


	public void displayProfileCarousel() {

		ImageView imageItem;

		// Populate the device profile carousel with items
		for (int i = 0 ; i < devicesProfileResourcesTypedArray.length() ; ++i) {
			imageItem = new ImageView(getActivity());
			imageItem.setBackgroundResource(io.puzzlebox.jigsaw.R.drawable.shadow);

//			imageItem.setImageResource(devicesProfileResourcesTypedArray.getResourceId(i, -1));

			Log.e(TAG, "ProfileSingleton.getInstance().profiles.get(i).get(\"icon\"): " + ProfileSingleton.getInstance().profiles.get(i).get("icon"));
//			Log.e(TAG, "ProfileSingleton.getInstance().profiles.get(i).get(\"icon\"): " + ProfileSingleton.getInstance().profiles.get(i).get("icon").split("res/drawable/")[1]);

//			imageItem.setImageResource(ProfileSingleton.getInstance().inputs.get(i).get("icon"));
//			imageItem.setImageResource( ProfileSingleton.getAndroidDrawable(



			int resource = ProfileSingleton.getInstance().getId(
//					  ProfileSingleton.getInstance().profiles.get(i).get("icon").split("res/drawable/")[1], io.puzzlebox.jigsaw.R.drawable.class);
					  ProfileSingleton.getInstance().profiles.get(i).get("icon"), io.puzzlebox.jigsaw.R.drawable.class);



//			Uri imagePath = Uri.parse(
//					  ProfileSingleton.getInstance().profiles.get(i).get("icon"));

//			Uri imagePath = Uri.parse("android.resource://io.puzzlebox.jigsaw/" + resource);
			Uri imagePath = Uri.parse("android.resource://io.puzzlebox.orbit/" + resource);

			imageItem.setImageURI(imagePath);

// 			imageItem.setImageDrawable( ProfileSingleton.getAndroidDrawable(
//					  ProfileSingleton.getInstance().profiles.get(i).get("icon")));



//					  .inputs.get(i).get("icon"));



			imageItem.setLayoutParams(new LinearLayout.LayoutParams(imageWidth, imageWidth));

			final int index = i;
			imageItem.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					showDialog("profile", index);
				}
			});

			TileViewAnimator tileViewAnimator = new TileViewAnimator(getContext());
			tileViewAnimator.addView(imageItem);
//			mProfileCarouselContainer.addView(imageItem);
			mProfileCarouselContainer.addView(tileViewAnimator);
		}

		mProfileCarouselContainer.setEnabled(true);
		mProfileCarouselContainer.setVisibility(View.VISIBLE);
		carouselDevicesProfileTextView.setEnabled(true);
		carouselDevicesProfileTextView.setVisibility(View.VISIBLE);

	}


//	@Override
//	public void onAttach(Context context) {
//		super.onAttach(context);
//		if (context instanceof OnFragmentInteractionListener) {
//			mListener = (OnFragmentInteractionListener) context;
//		} else {
//			throw new RuntimeException(context.toString()
//					  + " must implement OnFragmentInteractionListener");
//		}
//	}
//
//	@Override
//	public void onDetach() {
//		super.onDetach();
//		mListener = null;
//	}
//
//	public interface OnFragmentInteractionListener {
//		void onFragmentInteraction(Uri uri);
//	}


	@Override
	public void showDialog(String type, int index) {

		FragmentManager fm = getFragmentManager();

		switch (type) {

			case "input":

				switch (index) {
					case 0:
						// NeuroSky MindWave Mobile
						DialogNeuroSkyMindWaveFragment dialogNeuroSkyMindWaveFragment = new DialogNeuroSkyMindWaveFragment();
						dialogNeuroSkyMindWaveFragment.show(fm, getResources().getString(io.puzzlebox.jigsaw.R.string.title_dialog_fragment_neurosky_mindwave));
						break;
					case 1:
						// Joystick
						DialogJoystickFragment dialogJoystickFragment = new DialogJoystickFragment();
						dialogJoystickFragment.show(fm, getResources().getString(io.puzzlebox.jigsaw.R.string.title_dialog_fragment_joystick));
						break;
				}
				break;

			case "output":

				switch (index) {
					case 0:
						// Audio IR Transmitter
						DialogAudioIRFragment dialogAudioIRFragment = new DialogAudioIRFragment();
						dialogAudioIRFragment.show(fm, getResources().getString(io.puzzlebox.jigsaw.R.string.title_dialog_fragment_audio_ir));
						break;
					case 1:
						// Session Data
						DialogSessionFragment dialogSessionFragment = new DialogSessionFragment();
						dialogSessionFragment.show(fm, getResources().getString(io.puzzlebox.jigsaw.R.string.title_fragment_session));
						break;
				}
				break;

			case "profile":

				switch (index) {
					case 0:
						// Puzzlebox Orbit
						DialogPuzzleboxOrbitFragment dialogPuzzleboxOrbitFragment = new DialogPuzzleboxOrbitFragment();
						dialogPuzzleboxOrbitFragment.show(fm, getResources().getString(R.string.title_fragment_orbit));
						break;
				}
				break;
		}
	}




}
