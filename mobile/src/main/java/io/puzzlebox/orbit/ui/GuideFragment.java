package io.puzzlebox.orbit.ui;

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
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.InputStream;

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

	double tileInputInsetScale = 8.0;
	double tileOutputInsetScale = 8.0;
	//	double tileProfileInsetScale = 1.2;
//	double tileProfileInsetScale = 1.5;
	double tileProfileInsetScale = 0.1;

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

//	LinearLayout carouselDevicesOutput;
//	LinearLayout carouselDevicesProfile;

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
//		final TypedArray devicesInputResourcesTypedArray = getResources().obtainTypedArray(io.puzzlebox.jigsaw.R.array.devices_input_icon_array);
//		final TypedArray devicesOutputResourcesTypedArray = getResources().obtainTypedArray(io.puzzlebox.jigsaw.R.array.devices_output_icon_array);
//		final TypedArray devicesProfileResourcesTypedArray = getResources().obtainTypedArray(io.puzzlebox.jigsaw.R.array.devices_profile_icon_array);
		devicesInputResourcesTypedArray = getResources().obtainTypedArray(io.puzzlebox.jigsaw.R.array.devices_input_icon_array);
		devicesOutputResourcesTypedArray = getResources().obtainTypedArray(io.puzzlebox.jigsaw.R.array.devices_output_icon_array);
//		devicesProfileResourcesTypedArray = getResources().obtainTypedArray(io.puzzlebox.jigsaw.R.array.devices_profile_icon_array);
//		devicesInputResourcesTypedArray = getResources().obtainTypedArray(io.puzzlebox.jigsaw.R.array.devices_input_array);
//		devicesOutputResourcesTypedArray = getResources().obtainTypedArray(io.puzzlebox.jigsaw.R.array.devices_output_array);
		devicesProfileResourcesTypedArray = getResources().obtainTypedArray(io.puzzlebox.jigsaw.R.array.devices_profile_array);


		carouselDevicesInputTextView = (TextView) v. findViewById(R.id.carousel_devices_input_textview);
		carouselDevicesOutputTextView = (TextView) v. findViewById(R.id.carousel_devices_output_textview);
		carouselDevicesProfileTextView = (TextView) v. findViewById(R.id.carousel_device_profile_textview);

//		carouselDevicesOutput = (LinearLayout) v. findViewById(R.id.carousel_devices_output);
//		carouselDevicesProfile = (LinearLayout) v. findViewById(R.id.carousel_device_profile);


		displayInputCarousel();

		displayOutputCarousel();

		displayProfileCarousel();


		return v;

	}

	public void displayInputCarousel() {

		ImageView imageItem;

		mInputCarouselContainer.removeAllViewsInLayout();

		// Populate the input devices carousel with items

		for (int i = 0 ; i < devicesInputResourcesTypedArray.length() ; ++i) {

			final int index = i;

			// Create new ImageView
			imageItem = new ImageView(getActivity());

			// Set the shadow background
			imageItem.setBackgroundResource(io.puzzlebox.jigsaw.R.drawable.shadow);

			// Set the image view resource
//			imageItem.setImageResource(devicesInputResourcesTypedArray.getResourceId(i, -1));
			imageItem.setImageResource(
					  getResources().getIdentifier(devicesInputResourcesTypedArray.getString(i), "drawable", getActivity().getPackageName()));
//			imageItem.setImageResource(ProfileSingleton.getInstance().inputs.get(i).get("icon"));

			// Set the size of the image view to the previously computed value
			imageItem.setLayoutParams(new LinearLayout.LayoutParams(tileDimension, tileDimension));

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
		carouselDevicesInputTextView.setEnabled(true);
		carouselDevicesInputTextView.setVisibility(View.VISIBLE);

	}


	public void displayOutputCarousel() {

		ImageView imageItem;

		mOutputCarouselContainer.removeAllViewsInLayout();

		// Populate the output devices carousel with items
		for (int i = 0; i < devicesOutputResourcesTypedArray.length(); ++i) {
			imageItem = new ImageView(getActivity());
			imageItem.setBackgroundResource(io.puzzlebox.jigsaw.R.drawable.shadow);
//			imageItem.setImageResource(devicesOutputResourcesTypedArray.getResourceId(i, -1));
			imageItem.setImageResource(
					  getResources().getIdentifier(devicesOutputResourcesTypedArray.getString(i), "drawable", getActivity().getPackageName()));
			imageItem.setLayoutParams(new LinearLayout.LayoutParams(tileDimension, tileDimension));

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
		Resources r = getResources();
		Drawable[] layersTile;
		Drawable[] layersInput;
		Drawable[] layersOutput;
//		Drawable[] layersProfile;
		LayerDrawable layerDrawableTile;
		LayerDrawable layerDrawableInput;
		LayerDrawable layerDrawableOutput;
//		LayerDrawable layerDrawableProfile;

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


//			layersTile = new Drawable[4];
			layersTile = new Drawable[5];
			// layersTile[0]: Background/Highlight Color
			// layersTile[1]: Inputs
			// layersTile[2]: Outputs
			// layersTile[3]: Profile Icon
			// layersTile[4]: Resize Image


			// Background/Highlight Color
//			layersTile[0] = new ColorDrawable( getResources().getColor(R.color.white));
//			layersTile[0] = new ColorDrawable(Color.TRANSPARENT); // TODO
//			layersTile[0] = new ColorDrawable( getResources().getColor(R.color.tileActivated));
//			layersTile[0] = new ColorDrawable( getResources().getColor(R.color.tileRequired));
//			layersTile[0] = new ColorDrawable( getResources().getColor(R.color.tileAvailable));
			layersTile[0] = new ColorDrawable( getResources().getColor(R.color.tileDisabled));


			id = devicesProfileResourcesTypedArray.getString(i);
			resource =  getResources().getIdentifier(id + "_input", "array", getActivity().getPackageName());
			inputArray = getResources().getStringArray(resource);
			resource =  getResources().getIdentifier(id + "_output", "array", getActivity().getPackageName());
			outputArray = getResources().getStringArray(resource);

//			Log.e(TAG, "array: " + getResources().getStringArray(resource));


			maxSubtiles = inputArray.length;
			if (outputArray.length > maxSubtiles)
				maxSubtiles = outputArray.length;

			Log.e(TAG, "maxSubtiles: " + maxSubtiles);


//			layersInput = new Drawable[5];
//			layersInput = new Drawable[inputArray.length + 1];
			layersInput = new Drawable[maxSubtiles + 1];

//			layersInput[0] = new ColorDrawable( getResources().getColor(R.color.WhiteTint));
			layersInput[0] = new ColorDrawable( getResources().getColor(R.color.tileHighlight));
//			layersInput[0] = new ColorDrawable(Color.TRANSPARENT);

//			layersInput[0].setBounds(1536, 1536, 0, 0);

//			layersInput[0].setBounds(0,0,tileDimension*inputArray.length,tileDimension);
//			layersInput[0].setBounds(0,0,tileDimension*(inputArray.length - 1),tileDimension);


//			for (int j = 0 ; j < inputArray.length ; ++j) {
			for (int j = 0 ; j < maxSubtiles ; ++j) {

//				layersInput[1] = ProfileSingleton.getInstance().getDeviceDrawable(inputArray[j]);
				try {
					Log.e(TAG, "input device: " + inputArray[j]);
					layersInput[j+1] = getResources().getDrawable(
							  getResources().getIdentifier(
										 ProfileSingleton.getInstance().getDeviceIconPath(inputArray[j]),
										 "drawable", getActivity().getPackageName())
					);
					layersInput[j+1].setBounds(0,0,tileDimension,tileDimension);
				} catch (Exception e) {
					Log.d(TAG, "Exception: " + e.getStackTrace());

//					layersInput[j+1] = new ColorDrawable(Color.TRANSPARENT);
//					layersInput[j+1].setBounds(0,0,tileDimension,tileDimension);

					layersInput[j+1] = getResources().getDrawable(io.puzzlebox.jigsaw.R.drawable.carousel_blank);
					layersInput[j+1].setBounds(0,0,tileDimension,tileDimension);

				}
//				getResources().getIdentifier(devicesOutputResourcesTypedArray.getString(i), "drawable", getActivity().getPackageName()));

			}

			layerDrawableInput = new LayerDrawable(layersInput);

//			layerDrawableInput.setLayerInset(1, 0, 0, tileDimension, 0);

//			for (int j = 1 ; j <= inputArray.length ; ++j) {
////				Log.e(TAG, "input index: " + j + ", tileDimension * (j-1): " + tileDimension * (j-1) + ", tileDimension * (j): " + tileDimension * (j));
////				layerDrawableInput.setLayerInset(j, tileDimension * (j-1), 0, tileDimension * (j), 0);
//////				Log.e(TAG, "input index: " + j + ", tileDimension * (j-1): " + (int) tileInputInsetScale * tileDimension * (j-1) + ", tileDimension * (j): " + (int) tileInputInsetScale * tileDimension * (j));
//////				layerDrawableInput.setLayerInset(j, (int) tileInputInsetScale * tileDimension * (j-1), 0, (int) tileInputInsetScale * tileDimension * (j), 0);
//				Log.e(TAG, "input index: " + j + ", " + layersInput[j].getIntrinsicWidth() * (j-1) + ", 0, " + layersInput[j].getIntrinsicWidth() * (inputArray.length - j) + ", 0");
//				layerDrawableInput.setLayerInset(j, layersInput[j].getIntrinsicWidth() * (j-1), 0, layersInput[j].getIntrinsicWidth() * (inputArray.length - j), 0);
//			}



//			Log.e(TAG, "layersInput.length: " + layersInput.length);

//			for (int j = 1 ; j <= inputArray.length ; ++j) {
			for (int j = 1 ; j < layersInput.length ; ++j) {
				Log.e(TAG, "input index: " + j + ", " + layersInput[j].getIntrinsicWidth() * (j-1) + ", 0, " + layersInput[j].getIntrinsicWidth() * (layersInput.length - j) + ", 0");
				layerDrawableInput.setLayerInset(j, layersInput[j].getIntrinsicWidth() * (j-1), 0, layersInput[j].getIntrinsicWidth() * (layersInput.length - j), 0);
			}


			if (inputArray.length < maxSubtiles) {
				Log.d(TAG, "(inputArray.length < maxSubtiles)");
				layerDrawableInput.setLayerInset(0, 0, 0, layersInput[1].getIntrinsicWidth() * (maxSubtiles - inputArray.length + 1), 0);
			} else {
				layerDrawableInput.setLayerInset(0, 0, 0, layersInput[1].getIntrinsicWidth(), 0);
			}

//			Log.e(TAG, "Blank: " + layersInput[1].getIntrinsicWidth() * (layersInput.length - 1));
////			layerDrawableInput.setLayerInset(0, 0, 0, layersInput[1].getIntrinsicWidth() * (layersInput.length - 1), 0);
//			layerDrawableInput.getDrawable(0).setBounds(0, 0, layersInput[1].getIntrinsicWidth() * (layersInput.length - 1), 0);


			// TODO
//			switch (i) {
//				case 0:
////					layerDrawableInput.setLayerInset(1, 0, 0, tileDimension, 0);
////					layerDrawableInput.setLayerInset(1, 0, 0, 0, 0);
//					break;
//				case 1:
////					layerDrawableInput.setLayerInset(1, 0, 0, layersInput[2].getIntrinsicWidth(), 0);
////					layerDrawableInput.setLayerInset(2, layersInput[1].getIntrinsicWidth(), 0, 0, 0);
//					layerDrawableInput.setLayerInset(1, 0, 0, tileDimension * 3, 0);
//					layerDrawableInput.setLayerInset(2, tileDimension, 0, tileDimension * 2, 0);
//					layerDrawableInput.setLayerInset(3, tileDimension * 2, 0, tileDimension, 0);
//					layerDrawableInput.setLayerInset(4, tileDimension * 3, 0, 0, 0);
//					break;
//				case 2:
//////					layerDrawableInput.setLayerInset(1, 0, 0, tileDimension, 0);
////					layerDrawableInput.setLayerInset(1, 0, 0, tileDimension * 4, 0);
////					layerDrawableInput.setLayerInset(2, tileDimension * 4, 0, 0, 0);
//					layerDrawableInput.setLayerInset(1, 0, 0, layersInput[2].getIntrinsicWidth() + layersInput[3].getIntrinsicWidth(), 0);
//					layerDrawableInput.setLayerInset(2, layersInput[1].getIntrinsicWidth(), 0, layersInput[3].getIntrinsicWidth(), 0);
//					layerDrawableInput.setLayerInset(3, layersInput[1].getIntrinsicWidth() + layersInput[2].getIntrinsicWidth(), 0, 0, 0);
//					break;
//
//			}


//			layerDrawableInput.setLayerInset(1, 0, 0, layersInput[2].getIntrinsicWidth() + layersInput[3].getIntrinsicWidth() + layersInput[4].getIntrinsicWidth(), 0);
//			layerDrawableInput.setLayerInset(2, layersInput[1].getIntrinsicWidth(), 0, layersInput[3].getIntrinsicWidth() + layersInput[4].getIntrinsicWidth(), 0);
//			layerDrawableInput.setLayerInset(3, layersInput[1].getIntrinsicWidth() + layersInput[2].getIntrinsicWidth(), 0, layersInput[4].getIntrinsicWidth(), 0);
//			layerDrawableInput.setLayerInset(4, layersInput[1].getIntrinsicWidth() + layersInput[2].getIntrinsicWidth() + layersInput[3].getIntrinsicWidth(), 0, 0, 0);


			// TODO
//			layerDrawableInput.setBounds(0, 0, 1536, 1536);


			layersTile[1] = layerDrawableInput.getCurrent();


//			resource =  getResources().getIdentifier(id + "_output", "array", getActivity().getPackageName());
//			outputArray = getResources().getStringArray(resource);

//			layersOutput = new Drawable[outputArray.length + 1];
			layersOutput = new Drawable[maxSubtiles + 1];
//			layersOutput[0] = new ColorDrawable( getResources().getColor(R.color.WhiteTint));
			layersOutput[0] = new ColorDrawable( getResources().getColor(R.color.tileHighlight));
//			layersOutput[0].setBounds(0,0,tileDimension*outputArray.length,tileDimension);
			layersOutput[0].setBounds(0,0,tileDimension,tileDimension);

//			for (int j = 0 ; j < outputArray.length ; ++j) {
			for (int j = 0 ; j < maxSubtiles ; ++j) {

				try {
					Log.e(TAG, "output device: " + outputArray[j]);

					layersOutput[j+1] = getResources().getDrawable(
							  getResources().getIdentifier(
										 ProfileSingleton.getInstance().getDeviceIconPath(outputArray[j]),
										 "drawable", getActivity().getPackageName())
					);
					layersOutput[j+1].setBounds(0,0,tileDimension,tileDimension);
				} catch (Exception e) {
					Log.d(TAG, "Exception: " + e.getStackTrace());
//				if (layersOutput[j+1] == null) {
					layersOutput[j+1] = new ColorDrawable(Color.TRANSPARENT);
					layersOutput[j+1].setBounds(0,0,tileDimension,tileDimension);
				}

			}

			layerDrawableOutput = new LayerDrawable(layersOutput);

//			for (int j = 1 ; j <= outputArray.length ; ++j) {
//				layerDrawableOutput.setLayerInset(j, tileDimension * (j-1), 0, 0, 0);
//			}


//			for (int j = 1 ; j <= outputArray.length ; ++j) {
////				Log.e(TAG, "output index: " + j + ", tileDimension * (j-1): " + tileDimension * (j-1) + ", tileDimension * (j): " + tileDimension * (j));
////				Log.e(TAG, "output index: " + j + ", " + tileDimension * (j-1) + ", 0, " + tileDimension * (j) + ", 0");
////				Log.e(TAG, "output index: " + j + ", " + tileDimension * (j-1) + ", 0, " + tileDimension * (j) + ", 0");
////				Log.e(TAG, "output index: " + j + ", " + layersOutput[j].getIntrinsicWidth() * (j-1) + ", 0, " + layersOutput[j].getIntrinsicWidth() * (j) + ", 0");
//				Log.e(TAG, "output index: " + j + ", " + layersOutput[j].getIntrinsicWidth() * (j-1) + ", 0, " + layersOutput[j].getIntrinsicWidth() * (outputArray.length - j) + ", 0");
////				layerDrawableOutput.setLayerInset(j, tileDimension * (j-1), 0, tileDimension * (j), 0);
////				layerDrawableOutput.setLayerInset(j, layersOutput[j].getIntrinsicWidth() * (j-1), 0, layersOutput[j].getIntrinsicWidth() * (j), 0);
//				layerDrawableOutput.setLayerInset(j, layersOutput[j].getIntrinsicWidth() * (j-1), 0, layersOutput[j].getIntrinsicWidth() * (outputArray.length - j), 0);
//
//			}


//			for (int j = 1 ; j < layersOutput.length ; ++j) {
//				Log.e(TAG, "output index: " + j + ", " + layersOutput[j].getIntrinsicWidth() * (j-1) + ", 0, " + layersOutput[j].getIntrinsicWidth() * (layersOutput.length - j) + ", 0");
//				layerDrawableOutput.setLayerInset(j, layersOutput[j].getIntrinsicWidth() * (j-1), 0, layersOutput[j].getIntrinsicWidth() * (layersOutput.length - j), 0);
//			}






			for (int j = 1 ; j < layersOutput.length ; ++j) {
				Log.e(TAG, "output index: " + j + ", " + layersOutput[j].getIntrinsicWidth() * (layersOutput.length - j) + ", 0, " + layersOutput[j].getIntrinsicWidth() * (j-1) + ", 0");
				layerDrawableOutput.setLayerInset(j, layersOutput[j].getIntrinsicWidth() * (layersOutput.length - j), 0, layersOutput[j].getIntrinsicWidth() * (j-1), 0);
			}

			if (outputArray.length < maxSubtiles) {
				Log.d(TAG, "(outputArray.length < maxSubtiles)");
				layerDrawableOutput.setLayerInset(0, layersOutput[1].getIntrinsicWidth() * (maxSubtiles - outputArray.length + 1), 0, 0, 0);
			} else {
				layerDrawableOutput.setLayerInset(0, layersOutput[1].getIntrinsicWidth(), 0, 0, 0);
			}

//			if (i == 1) {
//				Log.e(TAG, "(i == 1)");
//				layerDrawableOutput.setLayerInset(0, layersOutput[1].getIntrinsicWidth() * 2, 0, 0, 0);
//			}

//			layerDrawableOutput.setLayerInset(0, layersOutput[1].getIntrinsicWidth() - (maxSubtiles - layersOutput.length), 0, 0, 0);





//			for (int j = 0 ; j < layersOutput.length ; ++j) {
//				Log.e(TAG, "output index: " + j + ", " + layersOutput[j].getIntrinsicWidth() * (layersOutput.length - j) + ", 0, " + layersOutput[j].getIntrinsicWidth() * (j-1) + ", 0");
//				layerDrawableOutput.setLayerInset(j, layersOutput[j].getIntrinsicWidth() * (layersOutput.length - j), 0, layersOutput[j].getIntrinsicWidth() * (j-1), 0);
//			}



//			if (i == 0) {
//				Log.e(TAG, "(i == 0)");
//				layerDrawableOutput.setLayerInset(0, 1536, 0, 0, 0);
//			}




//			if (i == 0) {
//				Log.e(TAG, "(i == 0)");
//				Log.d(TAG, "layerDrawableOutput.getNumberOfLayers(): " + layerDrawableOutput.getNumberOfLayers());
//
////				layerDrawableOutput.setLayerInset(1, 0, 0, layersOutput[1].getIntrinsicWidth() * (layersOutput.length - 1), 0);
////				layerDrawableOutput.setLayerInset(2, layersOutput[2].getIntrinsicWidth(), 0, layersOutput[2].getIntrinsicWidth() * (layersOutput.length - 1), 0);
////				layerDrawableOutput.setLayerInset(1, 0, 0, 3072, 0);
////				layerDrawableOutput.setLayerInset(2, 1536, 0, 1536, 0);
//				layerDrawableOutput.setLayerInset(1, 3072, 0, 0, 0);
//				layerDrawableOutput.setLayerInset(2, 1536, 0, 1536, 0);
//
////				layerDrawableOutput.setLayerInset(j, layersOutput[j].getIntrinsicWidth() * (j - 1), 0, layersOutput[j].getIntrinsicWidth() * (layersOutput.length - j), 0)
//			} else {
////			}
//				for (int j = 1 ; j < layersOutput.length ; ++j) {
//					Log.e(TAG, "output index: " + j + ", " + layersOutput[j].getIntrinsicWidth() * (j-1) + ", 0, " + layersOutput[j].getIntrinsicWidth() * (layersOutput.length - j) + ", 0");
//					layerDrawableOutput.setLayerInset(j, layersOutput[j].getIntrinsicWidth() * (j-1), 0, layersOutput[j].getIntrinsicWidth() * (layersOutput.length - j), 0);
//				}
//			}






			// TODO
//			switch (i) {
//				case 0:
////					layerDrawableOutput.setLayerInset(1, 0, 0, 0, 0);
//					break;
//				case 1:
////					layerDrawableOutput.setLayerInset(1, 0, 0, tileDimension * 4, 0);
////					layerDrawableOutput.setLayerInset(2, tileDimension * 4, 0, 0, 0);
//					layerDrawableOutput.setLayerInset(1, 0, 0, layersOutput[2].getIntrinsicWidth() + layersOutput[3].getIntrinsicWidth() + layersOutput[4].getIntrinsicWidth(), 0);
//					Log.e(TAG, "1, 0, 0, " + (layersOutput[2].getIntrinsicWidth() + layersOutput[3].getIntrinsicWidth() + layersOutput[4].getIntrinsicWidth()) + ", 0");
//
//					layerDrawableOutput.setLayerInset(2, layersOutput[1].getIntrinsicWidth(), 0, layersOutput[3].getIntrinsicWidth() + layersOutput[4].getIntrinsicWidth(), 0);
//					Log.e(TAG, "2, " + layersOutput[1].getIntrinsicWidth() + ", 0, " + (layersOutput[3].getIntrinsicWidth() + layersOutput[4].getIntrinsicWidth()) + ", 0");
//
//					layerDrawableOutput.setLayerInset(3, layersOutput[1].getIntrinsicWidth() + layersOutput[2].getIntrinsicWidth(), 0, layersOutput[4].getIntrinsicWidth(), 0);
//					Log.e(TAG, "3, " + (layersOutput[1].getIntrinsicWidth() + layersOutput[2].getIntrinsicWidth()) + ", 0, " + layersOutput[4].getIntrinsicWidth() + ", 0");
//
//					layerDrawableOutput.setLayerInset(4, layersOutput[1].getIntrinsicWidth() + layersOutput[2].getIntrinsicWidth() + layersOutput[3].getIntrinsicWidth(), 0, 0, 0);
//					Log.e(TAG, "4, " + (layersOutput[1].getIntrinsicWidth() + layersOutput[2].getIntrinsicWidth() + layersOutput[3].getIntrinsicWidth()) + ", 0, 0, 0");
//
//
//
//					Log.e(TAG, "layersOutput[1].getIntrinsicWidth(): " + layersOutput[1].getIntrinsicWidth());
//					Log.e(TAG, "layersOutput[2].getIntrinsicWidth(): " + layersOutput[2].getIntrinsicWidth());
//					Log.e(TAG, "layersOutput[3].getIntrinsicWidth(): " + layersOutput[3].getIntrinsicWidth());
//					Log.e(TAG, "layersOutput[4].getIntrinsicWidth(): " + layersOutput[4].getIntrinsicWidth());
//
//
//
//					break;
//				case 2:
//					layerDrawableOutput.setLayerInset(1, layersOutput[1].getIntrinsicWidth() * 2, 0, 0, 0);
//					break;
//
//			}




//			layerDrawableOutput.setLayerInset(0, tileDimension * 4, 0, 0, 0);
//			layerDrawableOutput.setLayerInset(1, tileDimension * 4 , 0, 0, 0);

			layersTile[2] = layerDrawableOutput.getCurrent();


//
////			resource = ProfileSingleton.getInstance().getId(
////					  ProfileSingleton.getInstance().profiles.get(i).get("input"), String[].class);
//			resource = ProfileSingleton.getInstance().getId(
//					  id + "_input", String[].class);
//
//			resourcePath = Uri.parse("android.resource://io.puzzlebox.orbit/" + resource);
////			resourcePath = Uri.parse("android.resource://io.puzzlebox.jigsaw/" + resource);
//
//			Log.e(TAG, "resourcePath: " + resourcePath);
//
////			inputArray =  getString(resourcePath);





			// Input Icon(s)
////			layersInput = new Drawable[5];
////			layersInput[0] = new ColorDrawable( getResources().getColor(R.color.WhiteTint));
////			layersInput[1] = r.getDrawable(R.drawable.carousel_neurosky_mindwave_mobile);
//			layersInput[2] = r.getDrawable(R.drawable.carousel_joystick);
//			layersInput[3] = r.getDrawable(R.drawable.carousel_watch_android);
////			layersInput[4] = r.getDrawable(R.mipmap.ic_session_color);
//			layersInput[4] = r.getDrawable(R.drawable.carousel_puzzlebox_orbit);
////			layersInput[4] = r.getDrawable(R.drawable.carousel_openbci_cyton_8_channel);
////			layersInput[4] = new ColorDrawable(Color.TRANSPARENT);
//			layersInput[4].setBounds(new Rect(0, 0, tileDimension, tileDimension));
//			layerDrawableInput = new LayerDrawable(layersInput);
//
//
////			layerDrawableInput.setLayerInset(1, 0, 0, layersInput[1].getIntrinsicWidth(), 0);
////			layerDrawableInput.setLayerInset(2, layersInput[1].getIntrinsicWidth(), 0, 0, 0);
////			layerDrawableInput.setLayerInset(3, layersInput[1].getIntrinsicWidth(), 0, 0, 0);
//
//
//			layerDrawableInput.setLayerInset(1, 0, 0, layersInput[2].getIntrinsicWidth() + layersInput[3].getIntrinsicWidth() + layersInput[4].getIntrinsicWidth(), 0);
//			layerDrawableInput.setLayerInset(2, layersInput[1].getIntrinsicWidth(), 0, layersInput[3].getIntrinsicWidth() + layersInput[4].getIntrinsicWidth(), 0);
//			layerDrawableInput.setLayerInset(3, layersInput[1].getIntrinsicWidth() + layersInput[2].getIntrinsicWidth(), 0, layersInput[4].getIntrinsicWidth(), 0);
//			layerDrawableInput.setLayerInset(4, layersInput[1].getIntrinsicWidth() + layersInput[2].getIntrinsicWidth() + layersInput[3].getIntrinsicWidth(), 0, 0, 0);


////			layerDrawableInput.setLayerInset(1, 0, 0, layersInput[1].getIntrinsicWidth() * 3, 0);
//			layerDrawableInput.setLayerInset(1, 0, 0, layersInput[1].getIntrinsicWidth(), 0);
////			layerDrawableInput.setLayerInset(1, 0, 0, layersInput[1].getIntrinsicWidth() + layersInput[2].getIntrinsicWidth(), 0);
////			layerDrawableInput.setLayerInset(1, 0, 0, layersInput[1].getIntrinsicWidth() + layersInput[2].getIntrinsicWidth(), 0);
////			layerDrawableInput.setLayerInset(2, layersInput[1].getIntrinsicWidth(), 0, layersInput[1].getIntrinsicWidth() * 2, 0);
////			layerDrawableInput.setLayerInset(2, layersInput[1].getIntrinsicWidth(), 0, layersInput[2].getIntrinsicWidth(), 0);
////			layerDrawableInput.setLayerInset(2, layersInput[1].getIntrinsicWidth() + layersInput[2].getIntrinsicWidth(), 0, 0, 0);
//			layerDrawableInput.setLayerInset(2, layersInput[1].getIntrinsicWidth(), 0, 0, 0);
////			layerDrawableInput.setLayerInset(3, layersInput[1].getIntrinsicWidth() * 2, 0, layersInput[1].getIntrinsicWidth(), 0);
//			layerDrawableInput.setLayerInset(3, layersInput[1].getIntrinsicWidth(), 0, 0, 0);
////			layerDrawableInput.setLayerInset(4, layersInput[1].getIntrinsicWidth() * 3, 0, 0, 0);

//			layerDrawableInput.set

//			layersTile[1] = layerDrawableInput.getCurrent();


//			// Output Icon(s)
////			layersOutput = new Drawable[2];
//			layersOutput = new Drawable[5];
//			layersOutput[0] = new ColorDrawable( getResources().getColor(R.color.WhiteTint));
////			layersOutput[0] = new ColorDrawable(Color.TRANSPARENT);
//
//			layersOutput[1] = new ColorDrawable(Color.TRANSPARENT);
//			layersOutput[1].setBounds(new Rect(0, 0, tileDimension, tileDimension));
//			layersOutput[2] = new ColorDrawable(Color.TRANSPARENT);
//			layersOutput[2].setBounds(new Rect(0, 0, tileDimension, tileDimension));
//			layersOutput[3] = new ColorDrawable(Color.TRANSPARENT);
//			layersOutput[3].setBounds(new Rect(0, 0, tileDimension, tileDimension));
//
//			layersOutput[4] = r.getDrawable(R.drawable.carousel_puzzlebox_orbit_ir);
//
////			layersOutput[1] = r.getDrawable(R.drawable.carousel_puzzlebox_orbit_ir);
//			layerDrawableOutput = new LayerDrawable(layersOutput);
//
//
////			layerDrawableOutput.setLayerInset(4, layersOutput[1].getIntrinsicWidth() + layersOutput[2].getIntrinsicWidth() + layersOutput[3].getIntrinsicWidth(), 0, 0, 0);
////			layerDrawableOutput.setLayerInset(4, tileDimension * 3, 0, 0, 0);
//			layerDrawableOutput.setLayerInset(0, layersOutput[4].getIntrinsicWidth() * 3, 0, 0, 0);
//			layerDrawableOutput.setLayerInset(4, layersOutput[4].getIntrinsicWidth() * 3, 0, 0, 0);


//			// Output Icon(s)
//			layersOutput = new Drawable[2];
////			layersOutput = new Drawable[5];
//			layersOutput[0] = new ColorDrawable( getResources().getColor(R.color.WhiteTint));
////			layersOutput[0] = new ColorDrawable(Color.TRANSPARENT);
//
////			layersOutput[1] = new ColorDrawable(Color.TRANSPARENT);
////			layersOutput[1].setBounds(new Rect(0, 0, tileDimension, tileDimension));
////			layersOutput[2] = new ColorDrawable(Color.TRANSPARENT);
////			layersOutput[2].setBounds(new Rect(0, 0, tileDimension, tileDimension));
////			layersOutput[3] = new ColorDrawable(Color.TRANSPARENT);
////			layersOutput[3].setBounds(new Rect(0, 0, tileDimension, tileDimension));
////
////			layersOutput[4] = r.getDrawable(R.drawable.carousel_puzzlebox_orbit_ir);
//
//			layersOutput[1] = r.getDrawable(R.drawable.carousel_puzzlebox_orbit_ir);

//			layerDrawableOutput = new LayerDrawable(layersOutput);


//////			layerDrawableOutput.setLayerInset(4, layersOutput[1].getIntrinsicWidth() + layersOutput[2].getIntrinsicWidth() + layersOutput[3].getIntrinsicWidth(), 0, 0, 0);
//////			layerDrawableOutput.setLayerInset(4, tileDimension * 3, 0, 0, 0);
////			layerDrawableOutput.setLayerInset(0, layersOutput[4].getIntrinsicWidth() * 3, 0, 0, 0);
//			layerDrawableOutput.setLayerInset(0, tileDimension * 4, 0, 0, 0);
////			layerDrawableOutput.setLayerInset(4, layersOutput[4].getIntrinsicWidth() * 3, 0, 0, 0);
////			layerDrawableOutput.setLayerInset(1, tileDimension, 0, tileDimension * 2, 0);
////			layerDrawableOutput.setLayerInset(1, tileDimension * 3 , 0, tileDimension, 0);
//			layerDrawableOutput.setLayerInset(1, tileDimension * 4 , 0, 0, 0);



//			layersTile[2] = layerDrawableOutput.getCurrent();


			// Profile Icon
			//Log.d(TAG, "ProfileSingleton.getInstance().profiles.get(i).get(\"icon\"): " + ProfileSingleton.getInstance().profiles.get(i).get("icon"));

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

//			int max = inputArray.length;
//			if (outputArray.length > max)
//				max = outputArray.length;

			// TODO
//			if (inputArray.length == outputArray.length) {
//				layerDrawableTile.setLayerInset(1, 0, 0, (int) (tileDimension * tileInputInsetScale), (int) (tileDimension * tileInputInsetScale));
//				layerDrawableTile.setLayerInset(2, (int) (tileDimension * tileOutputInsetScale), (int) (tileDimension * tileOutputInsetScale), 0, 0);
//			} else if (inputArray.length > outputArray.length) {
//				layerDrawableTile.setLayerInset(1, 0, 0, (int) (tileDimension * tileInputInsetScale) / maxSubtiles, (int) (tileDimension * tileInputInsetScale));
////				layerDrawableTile.setLayerInset(1, 0, 0, (int) (tileDimension * tileInputInsetScale) / (inputArray.length - outputArray.length), (int) (tileDimension * tileInputInsetScale));
//				layerDrawableTile.setLayerInset(2, (int) (tileDimension * tileOutputInsetScale), (int) (tileDimension * tileOutputInsetScale), 0, 0);
//			} else {
//				layerDrawableTile.setLayerInset(1, 0, 0, (int) (tileDimension * tileInputInsetScale), (int) (tileDimension * tileInputInsetScale));
//				layerDrawableTile.setLayerInset(2, (int) (tileDimension * tileOutputInsetScale) / maxSubtiles, (int) (tileDimension * tileOutputInsetScale), 0, 0);
////				layerDrawableTile.setLayerInset(2, (int) (tileDimension * tileOutputInsetScale) / (outputArray.length - inputArray.length), (int) (tileDimension * tileOutputInsetScale), 0, 0);
//			}

			maxDimension = layerDrawableTile.getIntrinsicWidth();
			if (layerDrawableTile.getIntrinsicHeight() > maxDimension)
				maxDimension = layerDrawableTile.getIntrinsicHeight();

			Log.e(TAG, "maxDimension: " + maxDimension);

//			switch (inputArray.length) {
//
////				case 1:
////
////					switch (outputArray.length) {
////						case 1:
////							Log.d(TAG, "1,1");
////							layerDrawableTile.setLayerInset(1, 0, 0, (int) (tileDimension * tileInputInsetScale), (int) (tileDimension * tileInputInsetScale));
////							layerDrawableTile.setLayerInset(2, (int) (tileDimension * tileOutputInsetScale), (int) (tileDimension * tileOutputInsetScale), 0, 0);
////							break;
////					}
////					break;
//
////				case 3:
////					Log.d(TAG, "3," + outputArray.length);
////					layerDrawableTile.setLayerInset(1, 0, 0, (int) (tileDimension * tileInputInsetScale), (int) (tileDimension * tileInputInsetScale));
////					layerDrawableTile.setLayerInset(2, (int) (tileDimension * tileOutputInsetScale), (int) (tileDimension * tileOutputInsetScale * 1.575), 0, 0);
//////					layerDrawableTile.setLayerInset(3, (int) (tileDimension * tileProfileInsetScale), (int) (tileDimension * tileProfileInsetScale), (int) (tileDimension * tileProfileInsetScale), (int) (tileDimension * tileProfileInsetScale));
////					break;
//
//				case 4:
//					Log.d(TAG, "4," + outputArray.length);
////					layerDrawableTile.setLayerInset(1, 0, 0, (int) (tileDimension * tileInputInsetScale) / 4, (int) (tileDimension * tileInputInsetScale * 1.575));
////					layerDrawableTile.setLayerInset(1, 0, 0, (int) (tileDimension * tileInputInsetScale) / 4, (int) (tileDimension * tileInputInsetScale));
////					layerDrawableTile.setLayerInset(1, 0, 0, (int) (tileDimension * tileInputInsetScale) / 5, (int) (tileDimension * tileInputInsetScale));
//					layerDrawableTile.setLayerInset(1, 0, 0, (int) (tileDimension * tileInputInsetScale), (int) (tileDimension * tileInputInsetScale));
////					layerDrawableTile.setLayerInset(2, (int) (tileDimension * tileOutputInsetScale), (int) (tileDimension * tileOutputInsetScale * 1.575), 0, 0);
////					layerDrawableTile.setLayerInset(2, (int) (tileDimension * tileOutputInsetScale), (int) (tileDimension * tileOutputInsetScale * 1.575), 0, 0);
////					layerDrawableTile.setLayerInset(2, (int) (tileDimension * tileOutputInsetScale), (int) (tileDimension * tileOutputInsetScale * 1.2), 0, 0);
//					layerDrawableTile.setLayerInset(2, (int) (tileDimension * tileOutputInsetScale), (int) (tileDimension * tileOutputInsetScale), 0, 0);
////					layerDrawableTile.setLayerInset(3, (int) (tileDimension * tileProfileInsetScale), (int) (tileDimension * tileProfileInsetScale), (int) (tileDimension * tileProfileInsetScale), (int) (tileDimension * tileProfileInsetScale));
//					break;
//
//				default:
//					Log.d(TAG, "default: " + i + "," + outputArray.length);
//					layerDrawableTile.setLayerInset(1, 0, 0, (int) (tileDimension * tileInputInsetScale), (int) (tileDimension * tileInputInsetScale));
//					layerDrawableTile.setLayerInset(2, (int) (tileDimension * tileOutputInsetScale), (int) (tileDimension * tileOutputInsetScale), 0, 0);
//					break;
//
//			}

			layerDrawableTile.setLayerInset(1, 0, 0, (int) (tileDimension * tileInputInsetScale), (int) (tileDimension * tileInputInsetScale));
			layerDrawableTile.setLayerInset(2, (int) (tileDimension * tileOutputInsetScale), (int) (tileDimension * tileOutputInsetScale), 0, 0);

			maxDimension = layerDrawableTile.getIntrinsicWidth();
			if (layerDrawableTile.getIntrinsicHeight() > maxDimension)
				maxDimension = layerDrawableTile.getIntrinsicHeight();

			Log.e(TAG, "maxDimension: " + maxDimension);


//			layerDrawableTile.setLayerInset(3, (int) (tileDimension * tileProfileInsetScale), (int) (tileDimension * tileProfileInsetScale), (int) (tileDimension * tileProfileInsetScale), (int) (tileDimension * tileProfileInsetScale));
			layerDrawableTile.setLayerInset(3, (int) (maxDimension * tileProfileInsetScale), (int) (maxDimension * tileProfileInsetScale), (int) (maxDimension * tileProfileInsetScale), (int) (maxDimension * tileProfileInsetScale));

//			layerDrawableTile.setLayerInset(1, 0, 0, (int) (tileDimension * tileInputInsetScale) / maxSubtiles, (int) (tileDimension * tileInputInsetScale));
//			layerDrawableTile.setLayerInset(2, (int) (tileDimension * tileOutputInsetScale) / maxSubtiles, (int) (tileDimension * tileOutputInsetScale), 0, 0);
//			layerDrawableTile.setLayerInset(3, (int) (tileDimension * tileProfileInsetScale), (int) (tileDimension * tileProfileInsetScale), (int) (tileDimension * tileProfileInsetScale), (int) (tileDimension * tileProfileInsetScale));

//			layerDrawableTile.setLayerInset(1, 0, 0, (int) (tileDimension * tileInputInsetScale) / (inputArray.length), (int) (tileDimension * tileInputInsetScale));
//			layerDrawableTile.setLayerInset(2, (int) (tileDimension * tileOutputInsetScale) / (outputArray.length), (int) (tileDimension * tileOutputInsetScale), 0, 0);
//			layerDrawableTile.setLayerInset(3, (int) (tileDimension * tileProfileInsetScale), (int) (tileDimension * tileProfileInsetScale), (int) (tileDimension * tileProfileInsetScale), (int) (tileDimension * tileProfileInsetScale));


//			layerDrawableTile.setLayerInset(1, 0, 0, (int) (tileDimension * tileInputInsetScale) / (inputArray.length), (int) (tileDimension * tileInputInsetScale));
//			layerDrawableTile.setLayerInset(3, (int) (tileDimension * tileProfileInsetScale), (int) (tileDimension * tileProfileInsetScale), (int) (tileDimension * tileProfileInsetScale), (int) (tileDimension * tileProfileInsetScale));
//			layerDrawableTile.setLayerInset(2, (int) (tileDimension * tileOutputInsetScale), (int) (tileDimension * tileOutputInsetScale), 0, 0);



////			layerDrawableTile.setLayerInset(1, 0, 0, (int) (tileDimension * tileInputInsetScale) / 4, (int) (tileDimension * tileInputInsetScale * 1.575));
////			layerDrawableTile.setLayerInset(1, 0, 0, (int) (tileDimension * tileInputInsetScale), (int) (tileDimension * tileInputInsetScale * 1.575));
//			layerDrawableTile.setLayerInset(1, 0, 0, (int) (tileDimension * tileInputInsetScale), (int) (tileDimension * tileInputInsetScale));
//			layerDrawableTile.setLayerInset(2, (int) (tileDimension * tileOutputInsetScale), (int) (tileDimension * tileOutputInsetScale * 1.575), 0, 0);
////			layerDrawableTile.setLayerInset(2, (int) (tileDimension * tileOutputInsetScale), (int) (tileDimension * tileOutputInsetScale), 0, 0);
//			layerDrawableTile.setLayerInset(3, (int) (tileDimension * tileProfileInsetScale), (int) (tileDimension * tileProfileInsetScale), (int) (tileDimension * tileProfileInsetScale), (int) (tileDimension * tileProfileInsetScale));

////			layerDrawableTile.setLayerInset(1, 0, 0, (int) (tileDimension * tileInputInsetScale) / 5, (int) (tileDimension * tileInputInsetScale * 1.52));
////			layerDrawableTile.setLayerInset(1, 0, 0, (int) (tileDimension * tileInputInsetScale) / 5, (int) (tileDimension * tileInputInsetScale * 1.52));
////			layerDrawableTile.setLayerInset(1, 0, 0, (int) (tileDimension * tileInputInsetScale), (int) (tileDimension * tileInputInsetScale));
////			layerDrawableTile.setLayerInset(1, 0, 0, (int) (tileDimension * tileInputInsetScale) / 4, (int) (tileDimension * tileInputInsetScale));
//			layerDrawableTile.setLayerInset(1, 0, 0, (int) (tileDimension * tileInputInsetScale) / 4, (int) (tileDimension * tileInputInsetScale * 1.575));
////			layerDrawableTile.setLayerInset(1, 0, 0, (int) (tileDimension * tileInputInsetScale) / 5, (int) (tileDimension * tileProfileInsetScale));
////			layerDrawableTile.setLayerInset(1, 0, 0, (int) (tileDimension * tileInputInsetScale) / 5, (int) (tileDimension * tileInputInsetScale + (tileDimension * tileProfileInsetScale)));
////			layerDrawableTile.setLayerInset(2, (int) (tileDimension * tileOutputInsetScale) / 5, (int) (tileDimension * tileOutputInsetScale * 1.5), 0, 0);
////			layerDrawableTile.setLayerInset(2, (int) (tileDimension * tileOutputInsetScale), (int) (tileDimension * tileOutputInsetScale * 1.5), 0, 0);
////			layerDrawableTile.setLayerInset(2, (int) (tileDimension * tileOutputInsetScale), (int) (tileDimension * tileOutputInsetScale), 0, 0);
////			layerDrawableTile.setLayerInset(2, (int) (tileDimension * tileOutputInsetScale), (int) (tileDimension * tileOutputInsetScale), 0, 0);
//			layerDrawableTile.setLayerInset(2, (int) (tileDimension * tileOutputInsetScale), (int) (tileDimension * tileOutputInsetScale * 1.575), 0, 0);
//			layerDrawableTile.setLayerInset(3, (int) (tileDimension * tileProfileInsetScale), (int) (tileDimension * tileProfileInsetScale), (int) (tileDimension * tileProfileInsetScale), (int) (tileDimension * tileProfileInsetScale));


//			// Input icon inset
////			layerDrawableTile.setLayerInset(1, 0, 0, (int) (tileDimension * tileInputInsetScale), (int) (tileDimension * tileInputInsetScale));
////			layerDrawableTile.setLayerInset(1, 0, 0, (int) (tileDimension * tileInputInsetScale) / 4, (int) (tileDimension * tileInputInsetScale));
////			layerDrawableTile.setLayerInset(1, 0, 0, (int) (tileDimension * tileInputInsetScale) / 4, (int) (tileDimension * tileInputInsetScale * 1.6));
////			layerDrawableTile.setLayerInset(1, 0, 0, (int) (tileDimension * tileInputInsetScale) / 4, (int) (tileDimension * tileInputInsetScale * 1.5));
//			layerDrawableTile.setLayerInset(1, 0, 0, (int) (tileDimension * tileInputInsetScale) / 5, (int) (tileDimension * tileInputInsetScale * 1.5));
////			layerDrawableTile.setLayerInset(2, 0, 0, (int) (tileDimension * tileInputInsetScale) / 4, (int) (tileDimension * tileInputInsetScale));
//			// Output icon inset
////			layerDrawableTile.setLayerInset(2, (int) (tileDimension * tileOutputInsetScale), (int) (tileDimension * tileOutputInsetScale), 0, 0);
////			layerDrawableTile.setLayerInset(2, (int) (tileDimension * tileOutputInsetScale) / 4, (int) (tileDimension * tileOutputInsetScale * 1.6), 0, 0);
////			layerDrawableTile.setLayerInset(2, (int) (tileDimension * tileOutputInsetScale) / 4, (int) (tileDimension * tileOutputInsetScale * 1.5), 0, 0);
//			layerDrawableTile.setLayerInset(2, (int) (tileDimension * tileOutputInsetScale) / 5, (int) (tileDimension * tileOutputInsetScale * 1.5), 0, 0);
////			layerDrawableTile.setLayerInset(2, (int) (tileDimension * tileOutputInsetScale), (int) (tileDimension * tileOutputInsetScale * 1.6), 0, 0);
//			// Profile icon inset
//			layerDrawableTile.setLayerInset(3, (int) (tileDimension * tileProfileInsetScale), (int) (tileDimension * tileProfileInsetScale), (int) (tileDimension * tileProfileInsetScale), (int) (tileDimension * tileProfileInsetScale));


			Log.e(TAG, "layerDrawableTile.getIntrinsicWidth(): " + layerDrawableTile.getIntrinsicWidth());
			Log.e(TAG, "layerDrawableTile.getIntrinsicHeight(): " + layerDrawableTile.getIntrinsicHeight());

			maxDimension = layerDrawableTile.getIntrinsicWidth();
			if (layerDrawableTile.getIntrinsicHeight() > maxDimension)
				maxDimension = layerDrawableTile.getIntrinsicHeight();

			Log.e(TAG, "maxDimension: " + maxDimension);

////			layersTile[4] = new ColorDrawable(Color.TRANSPARENT);
//			layersTile[4].setBounds(0,0,maxDimension,maxDimension);


//			layerDrawableTile.setLayerInset(4, 0, 0, maxDimension, maxDimension);
			layerDrawableTile.setLayerInset(4, (int) maxDimension / 2, (int) maxDimension / 2, (int) maxDimension / 2, (int) maxDimension / 2);


//			layerDrawableTile.setLayerInset(1, 0, 0, (int) (tileDimension * tileInputInsetScale), (int) (tileDimension / tileInputInsetScale));
//			layerDrawableTile.setLayerInset(1, 0, 0, (int) (tileDimension * tileInputInsetScale), (int) (maxDimension - (maxDimension / tileInputInsetScale)));
//			layerDrawableTile.setLayerInset(1, 0, 0, (int) (maxDimension * tileInputInsetScale), (int) (maxDimension * tileInputInsetScale));
//			layerDrawableTile.setLayerInset(1, 0, 0, (int) (maxDimension / tileInputInsetScale), (int) (maxDimension / tileInputInsetScale));

			layerDrawableTile.setLayerInset(1, 0, 0, (int) (tileDimension * tileInputInsetScale), (int) (maxDimension - (maxDimension / tileInputInsetScale)));
			layerDrawableTile.setLayerInset(2, (int) (tileDimension * tileOutputInsetScale), (int) (maxDimension - (maxDimension / tileInputInsetScale)), 0, 0);


			layerDrawableTile.setLayerInset(3, (int) (maxDimension * tileProfileInsetScale), (int) (maxDimension * tileProfileInsetScale), (int) (maxDimension * tileProfileInsetScale), (int) (maxDimension * tileProfileInsetScale));


			maxDimension = layerDrawableTile.getIntrinsicWidth();
			if (layerDrawableTile.getIntrinsicHeight() > maxDimension)
				maxDimension = layerDrawableTile.getIntrinsicHeight();

			Log.e(TAG, "maxDimension: " + maxDimension);

			layerDrawableTile.setLayerInset(4, (int) maxDimension / 2, (int) maxDimension / 2, (int) maxDimension / 2, (int) maxDimension / 2);

//			Drawable temp = layerDrawableTile;
//
//			Log.e(TAG, "temp: " + temp.getIntrinsicWidth());
//			Log.e(TAG, "temp: " + temp.getIntrinsicHeight());
//
//			temp.setBounds(0,0,tileDimension,tileDimension);


////			Log.e(TAG, "layerDrawableTile.getIntrinsicWidth(): " + layerDrawableTile.getIntrinsicWidth());
////			Log.e(TAG, "layerDrawableTile.getIntrinsicHeight(): " + layerDrawableTile.getIntrinsicHeight());
//			Log.e(TAG, "temp: " + temp.getIntrinsicWidth());
//			Log.e(TAG, "temp: " + temp.getIntrinsicHeight());

//			Drawable temp = new ColorDrawable(Color.TRANSPARENT);
//			temp.setBounds(0, 0, maxDimension, maxDimension);
//			temp.set

//			layerDrawableTile.addLayer(temp);


//			Log.e(TAG, "layersTile[3].getIntrinsicWidth(): " + layersTile[3].getIntrinsicWidth());
//			Log.e(TAG, "layersTile[3].getIntrinsicHeight(): " + layersTile[3].getIntrinsicHeight());
//
//			Log.e(TAG, "layerDrawableTile.getDrawable(3).getIntrinsicWidth(): " + layerDrawableTile.getDrawable(3).getIntrinsicWidth());
//			Log.e(TAG, "layerDrawableTile.getDrawable(3).getIntrinsicHeight(): " + layerDrawableTile.getDrawable(3).getIntrinsicHeight());
//


			imageItem.setImageDrawable(layerDrawableTile);
//			imageItem.setImageDrawable(temp);



//			imageItem.setBackground(layerDrawableTile);

//			Log.e(TAG, "imageItem.getDrawable().getIntrinsicWidth(): " + imageItem.getDrawable().getIntrinsicWidth());
//			Log.e(TAG, "imageItem.getDrawable().getIntrinsicHeight(): " + imageItem.getDrawable().getIntrinsicHeight());

//			imageItem.setAdjustViewBounds(true);

//			imageItem.setScaleType(ImageView.ScaleType.CENTER);
//			imageItem.setScaleType(ImageView.ScaleType.CENTER_CROP); // Close
//			imageItem.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
//			imageItem.setScaleType(ImageView.ScaleType.FIT_CENTER);
//			imageItem.setScaleType(ImageView.ScaleType.FIT_END);
//			imageItem.setScaleType(ImageView.ScaleType.FIT_START); // Close
//			imageItem.setScaleType(ImageView.ScaleType.FIT_XY);
//			imageItem.setScaleType(ImageView.ScaleType.MATRIX);

//			ImageView iv = new ImageView(getContext());

//			LinearLayout ll = new LinearLayout(new LinearLayout.LayoutParams(tileDimension, tileDimension));

//			imageItem.setMinimumHeight(imageItem.getDrawable().getIntrinsicWidth());
//			imageItem.setMaxHeight(imageItem.getDrawable().getIntrinsicWidth());
//
//			Log.e(TAG, "imageItem.getDrawable().getIntrinsicWidth(): " + imageItem.getDrawable().getIntrinsicWidth());
//			Log.e(TAG, "imageItem.getDrawable().getIntrinsicHeight(): " + imageItem.getDrawable().getIntrinsicHeight());

			imageItem.setLayoutParams(new LinearLayout.LayoutParams(tileDimension, tileDimension));

//			imageItem.getLayoutParams().height = 12800;
//			imageItem.getLayoutParams().width = 12800;




			Log.d(TAG, "imageItem.getDrawable().getIntrinsicWidth(): " + imageItem.getDrawable().getIntrinsicWidth());
			Log.d(TAG, "imageItem.getDrawable().getIntrinsicHeight(): " + imageItem.getDrawable().getIntrinsicHeight());

			Log.d(TAG, "layerDrawableInput.getIntrinsicWidth(): " + layerDrawableInput.getIntrinsicWidth());
			Log.d(TAG, "layerDrawableInput.getIntrinsicHeight(): " + layerDrawableInput.getIntrinsicHeight());

//			Log.d(TAG, "layersInput[1].getIntrinsicWidth(): " + layersInput[1].getIntrinsicWidth());
//			Log.d(TAG, "layersInput[1].getIntrinsicHeight(): " + layersInput[1].getIntrinsicHeight());


			for (int j = 0 ; j < layersInput.length; j++) {
				Log.d(TAG, "layersInput[" + j + "].getIntrinsicWidth(): " + layersInput[j].getIntrinsicWidth());
				Log.d(TAG, "layersInput[" + j + "].getIntrinsicHeight(): " + layersInput[j].getIntrinsicHeight());
			}




			Log.d(TAG, "imageItem.getDrawable().getIntrinsicWidth(): " + imageItem.getDrawable().getIntrinsicWidth());
			Log.d(TAG, "imageItem.getDrawable().getIntrinsicHeight(): " + imageItem.getDrawable().getIntrinsicHeight());

			Log.d(TAG, "layerDrawableOutput.getIntrinsicWidth(): " + layerDrawableOutput.getIntrinsicWidth());
			Log.d(TAG, "layerDrawableOutput.getIntrinsicHeight(): " + layerDrawableOutput.getIntrinsicHeight());

			for (int j = 0 ; j < layersOutput.length; j++) {
				Log.d(TAG, "layersOutput[" + j + "].getIntrinsicWidth(): " + layersOutput[j].getIntrinsicWidth());
				Log.d(TAG, "layersOutput[" + j + "].getIntrinsicHeight(): " + layersOutput[j].getIntrinsicHeight());
			}




//			for (int j = 0 ; j < layersTile)
//			layersTile[1].setBounds(0, 0, 1536, 1536);


			final int index = i;
			imageItem.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					showDialog("profile", index);
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
