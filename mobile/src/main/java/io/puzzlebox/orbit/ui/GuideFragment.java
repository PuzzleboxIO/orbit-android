package io.puzzlebox.orbit.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;

import io.puzzlebox.jigsaw.data.ProfileSingleton;
import io.puzzlebox.jigsaw.ui.DialogInputJoystickFragment;
import io.puzzlebox.jigsaw.ui.DialogInputNeuroSkyMindWaveFragment;
import io.puzzlebox.jigsaw.ui.DialogOutputAudioIRFragment;
import io.puzzlebox.jigsaw.ui.DialogOutputSessionFragment;
import io.puzzlebox.jigsaw.ui.DialogProfilePuzzleboxOrbitFragment;
import io.puzzlebox.jigsaw.ui.DialogProfilePuzzleboxOrbitJoystickFragment;
import io.puzzlebox.jigsaw.ui.DialogProfilePuzzleboxOrbitJoystickMindwaveFragment;
import io.puzzlebox.jigsaw.ui.TileViewAnimator;
import io.puzzlebox.jigsaw.ui.TilesFragment;
import io.puzzlebox.orbit.BuildConfig;
import io.puzzlebox.orbit.R;

public class GuideFragment extends TilesFragment {

	private static final String TAG = GuideFragment.class.getSimpleName();

	double tileInputInsetScale = 8.0;
	double tileOutputInsetScale = 8.0;
	double tileProfileInsetScale = 0.1;

	private int counterToastMessages = 3;

	// Number of items visible in carousels.
	private static final float INITIAL_ITEMS_COUNT = 2.5F;

	// Input carousel container layout
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

	public GuideFragment() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {

		View v = inflater.inflate(io.puzzlebox.jigsaw.R.layout.fragment_tiles, container, false);

		// Get reference to carousel container
		mInputCarouselContainer = (LinearLayout) v.findViewById(io.puzzlebox.jigsaw.R.id.carousel_devices_input);
		mOutputCarouselContainer = (LinearLayout) v.findViewById(io.puzzlebox.jigsaw.R.id.carousel_devices_output);
		mProfileCarouselContainer = (LinearLayout) v.findViewById(io.puzzlebox.jigsaw.R.id.carousel_devices_profile);

		// Compute the width of a carousel item based on the screen width and number of initial items.
		final DisplayMetrics displayMetrics = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
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
	public void onViewCreated(@NonNull View v, Bundle savedInstanceState) {
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
		int inputTileCount = BuildConfig.EMOTIV_TILES_ENABLED
				? devicesInputResourcesTypedArray.length()
				: devicesInputResourcesTypedArray.length() - 1;
		for (int i = 0 ; i < inputTileCount ; ++i) {

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

			if (ProfileSingleton.getInstance().isActive("outputs", i)) {
				layersOutput[0] = new ColorDrawable( getResources().getColor(R.color.tileActivated));
			} else {
				layersOutput[0] = new ColorDrawable( getResources().getColor(R.color.white));
			}

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
		int maxSubtiles;
		int maxDimension;

		mProfileCarouselContainer.removeAllViewsInLayout();

		// Populate the profile input device row with items
		int profileTileCount = BuildConfig.EMOTIV_TILES_ENABLED
				? devicesProfileResourcesTypedArray.length()
				: devicesProfileResourcesTypedArray.length() - 1;
		for (int i = 0 ; i < profileTileCount ; ++i) {
			imageItem = new ImageView(getActivity());

			imageItem.setBackgroundResource(io.puzzlebox.jigsaw.R.drawable.shadow);

			layersTile = new Drawable[5];
			// Reference
			// layersTile[0]: Background/Highlight Color
			// layersTile[1]: Inputs
			// layersTile[2]: Outputs
			// layersTile[3]: Profile Icon
			// layersTile[4]: Resize Image

			layersTile[0] = ProfileSingleton.getInstance().getProfileTileColor(getContext(), i);

			id = devicesProfileResourcesTypedArray.getString(i);
			resource =  getResources().getIdentifier(id + "_input", "array", getActivity().getPackageName());
			inputArray = getResources().getStringArray(resource);
			resource =  getResources().getIdentifier(id + "_output", "array", getActivity().getPackageName());
			outputArray = getResources().getStringArray(resource);

			maxSubtiles = inputArray.length;
			if (outputArray.length > maxSubtiles)
				maxSubtiles = outputArray.length;

			layersInput = new Drawable[maxSubtiles + 1];

			layersInput[0] = new ColorDrawable( getResources().getColor(R.color.tileHighlight));

			for (int j = 0 ; j < maxSubtiles ; ++j) {
				try {
					layersInput[j+1] = getResources().getDrawable(
							getResources().getIdentifier(
									ProfileSingleton.getInstance().getDeviceIconPath(inputArray[j]),
									"drawable", getActivity().getPackageName())
					);
					layersInput[j+1].setBounds(0,0,tileDimension,tileDimension);
				} catch (Exception e) {
					layersInput[j+1] = new ColorDrawable(Color.TRANSPARENT);
					layersInput[j+1].setBounds(0,0,tileDimension,tileDimension);
				}
			}

			layerDrawableInput = new LayerDrawable(layersInput);

			for (int j = 1 ; j < layersInput.length ; ++j) {
				layerDrawableInput.setLayerInset(j, layersInput[j].getIntrinsicWidth() * (j-1), 0, layersInput[j].getIntrinsicWidth() * (layersInput.length - j), 0);
			}


			if (inputArray.length < maxSubtiles) {
				layerDrawableInput.setLayerInset(0, 0, 0, layersInput[1].getIntrinsicWidth() * (maxSubtiles - inputArray.length + 1), 0);
			} else {
				layerDrawableInput.setLayerInset(0, 0, 0, layersInput[1].getIntrinsicWidth(), 0);
			}


			layersTile[1] = layerDrawableInput.getCurrent();

			layersOutput = new Drawable[maxSubtiles + 1];
			layersOutput[0] = new ColorDrawable( getResources().getColor(R.color.tileHighlight));

			for (int j = 0 ; j < maxSubtiles ; ++j) {

				try {
					layersOutput[j+1] = getResources().getDrawable(
							getResources().getIdentifier(
									ProfileSingleton.getInstance().getDeviceIconPath(outputArray[j]),
									"drawable", getActivity().getPackageName())
					);
					layersOutput[j+1].setBounds(0,0,tileDimension,tileDimension);
				} catch (Exception e) {
					layersOutput[j+1] = new ColorDrawable(Color.TRANSPARENT);
					layersOutput[j+1].setBounds(0,0,tileDimension,tileDimension);
				}
			}

			layerDrawableOutput = new LayerDrawable(layersOutput);

			for (int j = 1 ; j < layersOutput.length ; ++j) {
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

			resourcePath = Uri.parse("android.resource://" + getActivity().getPackageName() + "/" + resource);

			try {
				InputStream inputStream = getContext().getContentResolver().openInputStream(resourcePath);
				layersTile[3] = Drawable.createFromStream(inputStream, resourcePath.toString() );
			} catch (FileNotFoundException e) {
				Log.e(TAG, "Error parsing Drawable: " + Arrays.toString(e.getStackTrace()));
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

	@Override
	public void showDialog(String type, int index) {

		FragmentManager fm = getFragmentManager();

		switch (type) {
			case "inputs":
				switch (index) {
					case 0:
						// Joystick
						DialogInputJoystickFragment dialogInputJoystickFragment = new DialogInputJoystickFragment();
						dialogInputJoystickFragment.show(fm, getResources().getString(io.puzzlebox.jigsaw.R.string.title_dialog_fragment_joystick));
						break;
					case 1:
						// NeuroSky MindWave Mobile
						DialogInputNeuroSkyMindWaveFragment dialogInputNeuroSkyMindWaveFragment = new DialogInputNeuroSkyMindWaveFragment();
						dialogInputNeuroSkyMindWaveFragment.show(fm, getResources().getString(io.puzzlebox.jigsaw.R.string.title_dialog_fragment_neurosky_mindwave));
						break;
					case 2:
						// Emotiv Insight
						try {
							androidx.fragment.app.DialogFragment dialogInputEmotivInsightFragment =
								(androidx.fragment.app.DialogFragment) Class
									.forName("io.puzzlebox.jigsaw.ui.DialogInputEmotivInsightFragment")
									.getDeclaredConstructor().newInstance();
							dialogInputEmotivInsightFragment.show(fm, getResources().getString(io.puzzlebox.jigsaw.R.string.title_dialog_fragment_emotiv_insight));
						} catch (ReflectiveOperationException e) {
							Toast.makeText(getActivity(), "Emotiv Insight SDK not available in this build", Toast.LENGTH_SHORT).show();
						}
						break;
				}
				break;

			case "outputs":
				switch (index) {
					case 0:
						// Audio IR Transmitter
						DialogOutputAudioIRFragment dialogOutputAudioIRFragment = new DialogOutputAudioIRFragment();
						dialogOutputAudioIRFragment.show(fm, getResources().getString(io.puzzlebox.jigsaw.R.string.title_dialog_fragment_audio_ir));
						break;
					case 1:
						// Session Data
						DialogOutputSessionFragment dialogOutputSessionFragment = new DialogOutputSessionFragment();
						dialogOutputSessionFragment.show(fm, getResources().getString(io.puzzlebox.jigsaw.R.string.title_dialog_fragment_session));
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
						// Puzzlebox Orbit with Joystick
						DialogProfilePuzzleboxOrbitJoystickFragment dialogProfilePuzzleboxOrbitJoystickFragment = new DialogProfilePuzzleboxOrbitJoystickFragment();
						dialogProfilePuzzleboxOrbitJoystickFragment.show(fm, getResources().getString(R.string.title_dialog_fragment_puzzlebox_orbit_joystick));
						break;
					case 1:
						// Puzzlebox Orbit with NeuroSky MindWave Mobile EEG
						DialogProfilePuzzleboxOrbitFragment dialogProfilePuzzleboxOrbitFragment = new DialogProfilePuzzleboxOrbitFragment();
						dialogProfilePuzzleboxOrbitFragment.show(fm, getResources().getString(R.string.title_dialog_fragment_puzzlebox_orbit));
						break;
					case 2:
						// Puzzlebox Orbit with NeuroSky MindWave Mobile EEG and Joystick
						DialogProfilePuzzleboxOrbitJoystickMindwaveFragment dialogPuzzleboxOrbitJoystickMindwaveFragment = new DialogProfilePuzzleboxOrbitJoystickMindwaveFragment();
						dialogPuzzleboxOrbitJoystickMindwaveFragment.show(fm, getResources().getString(R.string.title_dialog_fragment_puzzlebox_orbit_joystick_mindwave));
						break;
					case 3:
						// Puzzlebox Orbit with Emotiv Insight EEG and Joystick
						try {
							androidx.fragment.app.DialogFragment dialogPuzzleboxOrbitEmotivInsightFragment =
								(androidx.fragment.app.DialogFragment) Class
									.forName("io.puzzlebox.jigsaw.ui.DialogProfilePuzzleboxOrbitEmotivInsightFragment")
									.getDeclaredConstructor().newInstance();
							dialogPuzzleboxOrbitEmotivInsightFragment.show(fm, getResources().getString(R.string.title_dialog_fragment_puzzlebox_orbit_emotiv_insight));
						} catch (ReflectiveOperationException e) {
							Toast.makeText(getActivity(), "Emotiv Insight SDK not available in this build", Toast.LENGTH_SHORT).show();
						}
						break;
				}
				break;
		}
	}

	public void onPause() {
		super.onPause();
		LocalBroadcastManager.getInstance(
				getActivity().getApplicationContext()).unregisterReceiver(
				mTileReceiver);
	}

	public void onResume() {
		super.onResume();
		LocalBroadcastManager.getInstance(getActivity().getApplicationContext()).registerReceiver(
				mTileReceiver, new IntentFilter("io.puzzlebox.jigsaw.protocol.tile.event"));
	}

	private final BroadcastReceiver mTileReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String id = intent.getStringExtra("id");
			String name = intent.getStringExtra("name");
			String value = intent.getStringExtra("value");
			String category = intent.getStringExtra("category");

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
}
