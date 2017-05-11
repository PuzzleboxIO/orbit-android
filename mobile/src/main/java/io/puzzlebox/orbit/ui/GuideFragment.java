package io.puzzlebox.orbit.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;

import io.puzzlebox.jigsaw.data.ProfileSingleton;
//import io.puzzlebox.jigsaw.ui.DialogAudioIRFragment;
import io.puzzlebox.jigsaw.ui.DialogJoystickFragment;
import io.puzzlebox.jigsaw.ui.DialogNeuroSkyMindWaveFragment;
import io.puzzlebox.jigsaw.ui.DialogSessionFragment;
import io.puzzlebox.jigsaw.ui.TileViewAnimator;
import io.puzzlebox.jigsaw.ui.TilesFragment;
import io.puzzlebox.orbit.R;

public class GuideFragment extends TilesFragment {

	private static final String TAG = GuideFragment.class.getSimpleName();

	double tileInputInsetScale = 8.0;
	double tileOutputInsetScale = 8.0;
	//	double tileProfileInsetScale = 1.2;
//	double tileProfileInsetScale = 1.5;
	double tileProfileInsetScale = 0.1;

	private int counterToastMessages = 3;

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

	private TextView carouselDevicesInputTextView;
	private TextView carouselDevicesOutputTextView;
	private TextView carouselDevicesProfileTextView;

	int tileDimension;

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
		mProfileCarouselContainer = (LinearLayout) v.findViewById(io.puzzlebox.jigsaw.R.id.carousel_devices_profile);


		// Compute the width of a carousel item based on the screen width and number of initial items.
		final DisplayMetrics displayMetrics = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//		final int tileDimension = (int) (displayMetrics.widthPixels / INITIAL_ITEMS_COUNT);
		tileDimension = (int) (displayMetrics.widthPixels / INITIAL_ITEMS_COUNT);

		// Get the array of input devices resources
		devicesInputResourcesTypedArray = getResources().obtainTypedArray(io.puzzlebox.jigsaw.R.array.devices_input_icon_array);
		devicesOutputResourcesTypedArray = getResources().obtainTypedArray(io.puzzlebox.jigsaw.R.array.devices_output_icon_array);
		devicesProfileResourcesTypedArray = getResources().obtainTypedArray(io.puzzlebox.jigsaw.R.array.devices_profile_array);

		carouselDevicesInputTextView = (TextView) v. findViewById(R.id.carousel_devices_input_textview);
		carouselDevicesOutputTextView = (TextView) v. findViewById(R.id.carousel_devices_output_textview);
		carouselDevicesProfileTextView = (TextView) v. findViewById(R.id.carousel_device_profile_textview);


		displayInputCarousel();

		displayOutputCarousel();

		displayProfileCarousel();


		return v;

	}


	@Override
	public void onViewCreated(View v, Bundle savedInstanceState) {
		super.onViewCreated(v, savedInstanceState);

		// Change to high-speed animation after first display
		ProfileSingleton.getInstance().tilesAnimationId = R.anim.tiles_fast;
	}


	public void displayInputCarousel() {

		ImageView imageItem;
		Drawable[] layersInput;
		LayerDrawable layerDrawableInput;

		mInputCarouselContainer.removeAllViewsInLayout();

		// Populate the input devices carousel with items

		for (int i = 0 ; i < devicesInputResourcesTypedArray.length() ; ++i) {

			final int index = i;

			// Create new ImageView
			imageItem = new ImageView(getActivity());

			// Set the shadow background
			imageItem.setBackgroundResource(io.puzzlebox.jigsaw.R.drawable.shadow);

			layersInput = new Drawable[2];
			if (ProfileSingleton.getInstance().isActive("inputs", i)) {
				layersInput[0] = new ColorDrawable( getResources().getColor(R.color.tileActivated));
			} else {
				layersInput[0] = new ColorDrawable( getResources().getColor(R.color.white));
			}

			// Set the image view resource
//			imageItem.setImageResource(
//					  getResources().getIdentifier(devicesInputResourcesTypedArray.getString(i), "drawable", getActivity().getPackageName()));

			layersInput[1] =  getResources().getDrawable(
					  getResources().getIdentifier(devicesInputResourcesTypedArray.getString(i), "drawable", getActivity().getPackageName()));

			layerDrawableInput = new LayerDrawable(layersInput);

			imageItem.setImageDrawable(layerDrawableInput);

			// Set the size of the image view to the previously computed value
			imageItem.setLayoutParams(new LinearLayout.LayoutParams(tileDimension, tileDimension));

			imageItem.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					showDialog("inputs", index);
				}
			});

			TileViewAnimator tileViewAnimator = new TileViewAnimator(getContext());
			tileViewAnimator.addView(imageItem);

			/// Add image view to the carousel container
			mInputCarouselContainer.addView(tileViewAnimator);
		}

		mInputCarouselContainer.setEnabled(true);
		mInputCarouselContainer.setVisibility(View.VISIBLE);
		carouselDevicesInputTextView.setEnabled(true);
		carouselDevicesInputTextView.setVisibility(View.VISIBLE);

	}


	public void displayOutputCarousel() {

		ImageView imageItem;
		Drawable[] layersOutput;
		LayerDrawable layerDrawableOutput;


		mOutputCarouselContainer.removeAllViewsInLayout();

		// Populate the output devices carousel with items
		for (int i = 0; i < devicesOutputResourcesTypedArray.length(); ++i) {
			imageItem = new ImageView(getActivity());
			imageItem.setBackgroundResource(io.puzzlebox.jigsaw.R.drawable.shadow);

			layersOutput = new Drawable[2];


//			Log.e(TAG, "(ProfileSingleton.getInstance().isActive(\"outputs\", i): " + (ProfileSingleton.getInstance().isActive("output", i)));

			if (ProfileSingleton.getInstance().isActive("outputs", i)) {
				layersOutput[0] = new ColorDrawable( getResources().getColor(R.color.tileActivated));
			} else {
				layersOutput[0] = new ColorDrawable( getResources().getColor(R.color.white));
			}

//			switch (ProfileSingleton.getInstance().outputs.get(i).get("status")) {
//				case "true":
//					layersOutput[0] = new ColorDrawable( getResources().getColor(R.color.tileActivated));
//					break;
//				case "false":
//					layersOutput[0] = new ColorDrawable( getResources().getColor(R.color.white));
//					break;
//			}

//			layersOutput[0] = new ColorDrawable( getResources().getColor(R.color.tileRequired));

//			imageItem.setImageResource(
//					  getResources().getIdentifier(devicesOutputResourcesTypedArray.getString(i), "drawable", getActivity().getPackageName()));

			layersOutput[1] =  getResources().getDrawable(
					  getResources().getIdentifier(devicesOutputResourcesTypedArray.getString(i), "drawable", getActivity().getPackageName()));

			layerDrawableOutput = new LayerDrawable(layersOutput);

			imageItem.setImageDrawable(layerDrawableOutput);

			imageItem.setLayoutParams(new LinearLayout.LayoutParams(tileDimension, tileDimension));


			final int index = i;
			imageItem.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					showDialog("outputs", index);
				}
			});

			TileViewAnimator tileViewAnimator = new TileViewAnimator(getContext());
			tileViewAnimator.addView(imageItem);

			mOutputCarouselContainer.addView(tileViewAnimator);
		}

		mOutputCarouselContainer.setEnabled(true);
		mOutputCarouselContainer.setVisibility(View.VISIBLE);
		carouselDevicesOutputTextView.setEnabled(true);
		carouselDevicesOutputTextView.setVisibility(View.VISIBLE);

	}


	public void displayProfileCarousel() {

		ImageView imageItem;
		Resources r = getResources();
		Drawable[] layersTile;
		Drawable[] layersInput;
		Drawable[] layersOutput;
		LayerDrawable layerDrawableTile;
		LayerDrawable layerDrawableInput;
		LayerDrawable layerDrawableOutput;

		String id;
		String[] inputArray;
		String[] outputArray;
		int resource;
		Uri resourcePath;
		int maxSubtiles = 1;
		int maxDimension;

		mProfileCarouselContainer.removeAllViewsInLayout();


		// Populate the profile input device row with items
		for (int i = 0 ; i < devicesProfileResourcesTypedArray.length() ; ++i) {
			imageItem = new ImageView(getActivity());

			imageItem.setBackgroundResource(io.puzzlebox.jigsaw.R.drawable.shadow);


			layersTile = new Drawable[5];
			// layersTile[0]: Background/Highlight Color
			// layersTile[1]: Inputs
			// layersTile[2]: Outputs
			// layersTile[3]: Profile Icon
			// layersTile[4]: Resize Image


			// Background/Highlight Color
//			layersTile[0] = new ColorDrawable( getResources().getColor(R.color.white));
//			layersTile[0] = new ColorDrawable(Color.TRANSPARENT);
//			layersTile[0] = new ColorDrawable( getResources().getColor(R.color.tileActivated));
//			layersTile[0] = new ColorDrawable( getResources().getColor(R.color.tileRequired));
//			layersTile[0] = new ColorDrawable( getResources().getColor(R.color.tileAvailable));
//			layersTile[0] = new ColorDrawable( getResources().getColor(R.color.tileDisabled));

//			if (ProfileSingleton.getInstance().isActive("profiles", i)) {
//				layersTile[0] = new ColorDrawable( getResources().getColor(R.color.tileActivated));
//			} else {
//				layersTile[0] = new ColorDrawable( getResources().getColor(R.color.white));
//			}

			layersTile[0] = ProfileSingleton.getInstance().getProfileTileColor(getContext(), i);


			id = devicesProfileResourcesTypedArray.getString(i);
			resource =  getResources().getIdentifier(id + "_input", "array", getActivity().getPackageName());
			inputArray = getResources().getStringArray(resource);
			resource =  getResources().getIdentifier(id + "_output", "array", getActivity().getPackageName());
			outputArray = getResources().getStringArray(resource);


			maxSubtiles = inputArray.length;
			if (outputArray.length > maxSubtiles)
				maxSubtiles = outputArray.length;

//			Log.d(TAG, "maxSubtiles: " + maxSubtiles);

			layersInput = new Drawable[maxSubtiles + 1];

			layersInput[0] = new ColorDrawable( getResources().getColor(R.color.tileHighlight));

			for (int j = 0 ; j < maxSubtiles ; ++j) {
				try {
//					Log.e(TAG, "input device: " + inputArray[j]);
					layersInput[j+1] = getResources().getDrawable(
							  getResources().getIdentifier(
										 ProfileSingleton.getInstance().getDeviceIconPath(inputArray[j]),
										 "drawable", getActivity().getPackageName())
					);
					layersInput[j+1].setBounds(0,0,tileDimension,tileDimension);
				} catch (Exception e) {
//					Log.d(TAG, "Exception: " + e.getStackTrace());
					layersInput[j+1] = new ColorDrawable(Color.TRANSPARENT);
					layersInput[j+1].setBounds(0,0,tileDimension,tileDimension);

				}

			}

			layerDrawableInput = new LayerDrawable(layersInput);

			for (int j = 1 ; j < layersInput.length ; ++j) {
//				Log.e(TAG, "input index: " + j + ", " + layersInput[j].getIntrinsicWidth() * (j-1) + ", 0, " + layersInput[j].getIntrinsicWidth() * (layersInput.length - j) + ", 0");
				layerDrawableInput.setLayerInset(j, layersInput[j].getIntrinsicWidth() * (j-1), 0, layersInput[j].getIntrinsicWidth() * (layersInput.length - j), 0);
			}


			if (inputArray.length < maxSubtiles) {
//				Log.d(TAG, "(inputArray.length < maxSubtiles)");
				layerDrawableInput.setLayerInset(0, 0, 0, layersInput[1].getIntrinsicWidth() * (maxSubtiles - inputArray.length + 1), 0);
			} else {
				layerDrawableInput.setLayerInset(0, 0, 0, layersInput[1].getIntrinsicWidth(), 0);
			}


			layersTile[1] = layerDrawableInput.getCurrent();

			layersOutput = new Drawable[maxSubtiles + 1];
			layersOutput[0] = new ColorDrawable( getResources().getColor(R.color.tileHighlight));

			for (int j = 0 ; j < maxSubtiles ; ++j) {

				try {
//					Log.d(TAG, "output device: " + outputArray[j]);

					layersOutput[j+1] = getResources().getDrawable(
							  getResources().getIdentifier(
										 ProfileSingleton.getInstance().getDeviceIconPath(outputArray[j]),
										 "drawable", getActivity().getPackageName())
					);
					layersOutput[j+1].setBounds(0,0,tileDimension,tileDimension);
				} catch (Exception e) {
//					Log.d(TAG, "Exception: " + e.getStackTrace());
					layersOutput[j+1] = new ColorDrawable(Color.TRANSPARENT);
					layersOutput[j+1].setBounds(0,0,tileDimension,tileDimension);
				}

			}

			layerDrawableOutput = new LayerDrawable(layersOutput);


			for (int j = 1 ; j < layersOutput.length ; ++j) {
//				Log.e(TAG, "output index: " + j + ", " + layersOutput[j].getIntrinsicWidth() * (layersOutput.length - j) + ", 0, " + layersOutput[j].getIntrinsicWidth() * (j-1) + ", 0");
				layerDrawableOutput.setLayerInset(j, layersOutput[j].getIntrinsicWidth() * (layersOutput.length - j), 0, layersOutput[j].getIntrinsicWidth() * (j-1), 0);
			}

			if (outputArray.length < maxSubtiles) {
				layerDrawableOutput.setLayerInset(0, layersOutput[1].getIntrinsicWidth() * (maxSubtiles - outputArray.length + 1), 0, 0, 0);
			} else {
				layerDrawableOutput.setLayerInset(0, layersOutput[1].getIntrinsicWidth(), 0, 0, 0);
			}


			layersTile[2] = layerDrawableOutput.getCurrent();

			resource = ProfileSingleton.getInstance().getId(
					  ProfileSingleton.getInstance().profiles.get(i).get("icon"), io.puzzlebox.jigsaw.R.drawable.class);

			resourcePath = Uri.parse("android.resource://io.puzzlebox.orbit/" + resource);

			try {
				InputStream inputStream = getContext().getContentResolver().openInputStream(resourcePath);
				layersTile[3] = Drawable.createFromStream(inputStream, resourcePath.toString() );
			} catch (FileNotFoundException e) {
//				layersTile[3] = getResources().getDrawable(R.drawable.default_image);
				Log.e(TAG, "Error parsing Drawable: " + e.getStackTrace());
			}

			layersTile[4] = new ColorDrawable(Color.TRANSPARENT);

			layersTile[1].setBounds(0, 0, 1536, 1536);

			layerDrawableTile = new LayerDrawable(layersTile);

			layerDrawableTile.setLayerInset(1, 0, 0, (int) (tileDimension * tileInputInsetScale), (int) (tileDimension * tileInputInsetScale));
			layerDrawableTile.setLayerInset(2, (int) (tileDimension * tileOutputInsetScale), (int) (tileDimension * tileOutputInsetScale), 0, 0);

			maxDimension = layerDrawableTile.getIntrinsicWidth();
			if (layerDrawableTile.getIntrinsicHeight() > maxDimension)
				maxDimension = layerDrawableTile.getIntrinsicHeight();

			layerDrawableTile.setLayerInset(3, (int) (maxDimension * tileProfileInsetScale), (int) (maxDimension * tileProfileInsetScale), (int) (maxDimension * tileProfileInsetScale), (int) (maxDimension * tileProfileInsetScale));

			maxDimension = layerDrawableTile.getIntrinsicWidth();
			if (layerDrawableTile.getIntrinsicHeight() > maxDimension)
				maxDimension = layerDrawableTile.getIntrinsicHeight();

//			layerDrawableTile.setLayerInset(4, 0, 0, maxDimension, maxDimension);
			layerDrawableTile.setLayerInset(4, (int) maxDimension / 2, (int) maxDimension / 2, (int) maxDimension / 2, (int) maxDimension / 2);


			layerDrawableTile.setLayerInset(1, 0, 0, (int) (tileDimension * tileInputInsetScale), (int) (maxDimension - (maxDimension / tileInputInsetScale)));
			layerDrawableTile.setLayerInset(2, (int) (tileDimension * tileOutputInsetScale), (int) (maxDimension - (maxDimension / tileInputInsetScale)), 0, 0);


			layerDrawableTile.setLayerInset(3, (int) (maxDimension * tileProfileInsetScale), (int) (maxDimension * tileProfileInsetScale), (int) (maxDimension * tileProfileInsetScale), (int) (maxDimension * tileProfileInsetScale));


			maxDimension = layerDrawableTile.getIntrinsicWidth();
			if (layerDrawableTile.getIntrinsicHeight() > maxDimension)
				maxDimension = layerDrawableTile.getIntrinsicHeight();


			layerDrawableTile.setLayerInset(4, (int) maxDimension / 2, (int) maxDimension / 2, (int) maxDimension / 2, (int) maxDimension / 2);

			imageItem.setImageDrawable(layerDrawableTile);

			imageItem.setLayoutParams(new LinearLayout.LayoutParams(tileDimension, tileDimension));


			final int index = i;
			imageItem.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					showDialog("profiles", index);
				}
			});

			TileViewAnimator tileViewAnimator = new TileViewAnimator(getContext());
			tileViewAnimator.addView(imageItem);
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

			case "inputs":

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

			case "outputs":

				switch (index) {
					case 0:
						// Audio IR Transmitter
//						DialogAudioIRFragment dialogAudioIRFragment = new DialogAudioIRFragment();
//						dialogAudioIRFragment.show(fm, getResources().getString(io.puzzlebox.jigsaw.R.string.title_dialog_fragment_audio_ir));
						DialogOutputAudioIRFragment dialogOutputAudioIRFragment = new DialogOutputAudioIRFragment();
						dialogOutputAudioIRFragment.show(fm, getResources().getString(io.puzzlebox.jigsaw.R.string.title_dialog_fragment_audio_ir));
						break;
					case 1:
						// Session Data
						DialogSessionFragment dialogSessionFragment = new DialogSessionFragment();
						dialogSessionFragment.show(fm, getResources().getString(io.puzzlebox.jigsaw.R.string.title_dialog_fragment_session));
						break;
				}
				break;

			case "profiles":

				if (! ProfileSingleton.getInstance().profiles.get(index).get("status").equals("available")) {
					// Skip warning message from block access after a number of attempts
					--counterToastMessages;
					if (counterToastMessages > 2) {
						Toast.makeText(getActivity().getApplicationContext(), getString(R.string.toast_warning_profile_unavailable), Toast.LENGTH_LONG).show();
						break;
					} else if (counterToastMessages > 0) {
						Toast.makeText(getActivity().getApplicationContext(), getString(R.string.toast_warning_profile_unavailable), Toast.LENGTH_SHORT).show();
						break;
					} else {
						counterToastMessages = 3;
					}
				}

				switch (index) {
					case 0:
						// Puzzlebox Orbit with EEG
						DialogPuzzleboxOrbitFragment dialogPuzzleboxOrbitFragment = new DialogPuzzleboxOrbitFragment();
						dialogPuzzleboxOrbitFragment.show(fm, getResources().getString(R.string.title_dialog_fragment_puzzlebox_orbit));
						break;
					case 1:
						// Puzzlebox Orbit with Joystick
						DialogPuzzleboxOrbitJoystickFragment dialogPuzzleboxOrbitJoystickFragment = new DialogPuzzleboxOrbitJoystickFragment();
						dialogPuzzleboxOrbitJoystickFragment.show(fm, getResources().getString(R.string.title_dialog_fragment_puzzlebox_orbit_joystick));
						break;
					case 2:
						// Puzzlebox Orbit with EEG and Joystick
						DialogPuzzleboxOrbitJoystickMindwaveFragment dialogPuzzleboxOrbitJoystickMindwaveFragment = new DialogPuzzleboxOrbitJoystickMindwaveFragment();
						dialogPuzzleboxOrbitJoystickMindwaveFragment.show(fm, getResources().getString(R.string.title_dialog_fragment_puzzlebox_orbit_joystick_mindwave));
						break;
				}
				break;
		}
	}


	// ################################################################

	public void onPause() {

		super.onPause();

		LocalBroadcastManager.getInstance(
				  getActivity().getApplicationContext()).unregisterReceiver(
				  mTileReceiver);

	} // onPause


	// ################################################################

	public void onResume() {

		super.onResume();

		LocalBroadcastManager.getInstance(getActivity().getApplicationContext()).registerReceiver(
				  mTileReceiver, new IntentFilter("io.puzzlebox.jigsaw.protocol.tile.event"));

	}


	// ################################################################

	private BroadcastReceiver mTileReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

			String id = intent.getStringExtra("id");
			String name = intent.getStringExtra("name");
			String value = intent.getStringExtra("value");
			String category = intent.getStringExtra("category");

//			Log.e(TAG, "mTileReceiver.onReceive() id: " + id);
//			Log.e(TAG, "mTileReceiver.onReceive() name: " + name);
//			Log.e(TAG, "mTileReceiver.onReceive() value: " + value);
//			Log.e(TAG, "mTileReceiver.onReceive() category: " + category);

			ProfileSingleton.getInstance().updateStatus(id, name , value);

			switch(category) {
				case "inputs":
					displayInputCarousel();
					displayProfileCarousel();
					break;
				case "outputs":
					displayOutputCarousel();
					displayProfileCarousel();
					break;
				case "profiles":
					displayProfileCarousel();
					break;
			}

		}

	};


	// ################################################################

	public Drawable scaleImage (Drawable image, float scaleFactor) {

		if ((image == null) || !(image instanceof BitmapDrawable)) {
			return image;
		}

		Bitmap b = ((BitmapDrawable)image).getBitmap();

		int sizeX = Math.round(image.getIntrinsicWidth() * scaleFactor);
		int sizeY = Math.round(image.getIntrinsicHeight() * scaleFactor);

		Bitmap bitmapResized = Bitmap.createScaledBitmap(b, sizeX, sizeY, false);

		image = new BitmapDrawable(getResources(), bitmapResized);

		return image;

	}


}
