package io.puzzlebox.orbit.ui;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
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

//	double tileProfileInsetScale = 1.2;
	double tileProfileInsetScale = 1.5;
	double tileInputInsetScale = 8.0;
	double tileOutputInsetScale = 8.0;

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
			imageItem.setImageResource(devicesInputResourcesTypedArray.getResourceId(i, -1));
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
			imageItem.setImageResource(devicesOutputResourcesTypedArray.getResourceId(i, -1));
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
		Drawable[] layersTemp;
		LayerDrawable layerDrawable;

		mProfileCarouselContainer.removeAllViewsInLayout();

		// Populate the device profile carousel with items
		for (int i = 0 ; i < devicesProfileResourcesTypedArray.length() ; ++i) {
			imageItem = new ImageView(getActivity());

			imageItem.setBackgroundResource(io.puzzlebox.jigsaw.R.drawable.shadow);

			layersTile = new Drawable[4];

			// Background/Highlight Color
//			layersTile[0] = new ColorDrawable( getResources().getColor(R.color.white));
			layersTile[0] = new ColorDrawable( getResources().getColor(R.color.tileActivated));
//			layersTile[0] = new ColorDrawable( getResources().getColor(R.color.tileRequired));


			// Input Icon(s)
			layersTemp = new Drawable[3];
			layersTemp[0] = new ColorDrawable( getResources().getColor(R.color.WhiteTint));
			layersTemp[1] = r.getDrawable(R.drawable.carousel_neurosky_mindwave_mobile);
			layersTemp[2] = r.getDrawable(R.drawable.carousel_joystick);
			layerDrawable = new LayerDrawable(layersTemp);
			layerDrawable.setLayerInset(1, 0, 0, layersTemp[1].getIntrinsicWidth(), 0);
			layerDrawable.setLayerInset(2, layersTemp[1].getIntrinsicWidth(), 0, 0, 0);


			layersTile[1] = layerDrawable.getCurrent();


			// Output Icon(s)
			layersTemp = new Drawable[2];
			layersTemp[0] = new ColorDrawable( getResources().getColor(R.color.WhiteTint));
			layersTemp[1] = r.getDrawable(R.drawable.carousel_puzzlebox_orbit_ir);
			layerDrawable = new LayerDrawable(layersTemp);

			layersTile[2] = layerDrawable.getCurrent();


			// Profile Icon
			//Log.d(TAG, "ProfileSingleton.getInstance().profiles.get(i).get(\"icon\"): " + ProfileSingleton.getInstance().profiles.get(i).get("icon"));

			int resource = ProfileSingleton.getInstance().getId(
					  ProfileSingleton.getInstance().profiles.get(i).get("icon"), io.puzzlebox.jigsaw.R.drawable.class);

			Uri imagePath = Uri.parse("android.resource://io.puzzlebox.orbit/" + resource);

			try {
				InputStream inputStream = getContext().getContentResolver().openInputStream(imagePath);
				layersTile[3] = Drawable.createFromStream(inputStream, imagePath.toString() );
			} catch (FileNotFoundException e) {
//				layersTile[1] = getResources().getDrawable(R.drawable.default_image);
				Log.e(TAG, "Error parsing Drawable: " + e.getStackTrace());
			}


			layerDrawable = new LayerDrawable(layersTile);

			// Input icon inset
//			layerDrawable.setLayerInset(1, 0, 0, (int) (tileDimension * tileInputInsetScale), (int) (tileDimension * tileInputInsetScale));
			layerDrawable.setLayerInset(1, 0, 0, (int) (tileDimension * tileInputInsetScale) / 2, (int) (tileDimension * tileInputInsetScale));
			// Output icon inset
			layerDrawable.setLayerInset(2, (int) (tileDimension * tileOutputInsetScale), (int) (tileDimension * tileOutputInsetScale), 0, 0);
			// Profile icon inset
			layerDrawable.setLayerInset(3, (int) (tileDimension * tileProfileInsetScale), (int) (tileDimension * tileProfileInsetScale), (int) (tileDimension * tileProfileInsetScale), (int) (tileDimension * tileProfileInsetScale));

			imageItem.setImageDrawable(layerDrawable);


			imageItem.setLayoutParams(new LinearLayout.LayoutParams(tileDimension, tileDimension));


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
