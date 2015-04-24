package it.unipr.ce.dsg.gamidroid.activities;

import it.unipr.ce.dsg.gamidroid.R;
import it.unipr.ce.dsg.gamidroid.devicemonitor.DeviceMonitorService;
import it.unipr.ce.dsg.gamidroid.devicemonitor.ResourceMonitor;
import it.unipr.ce.dsg.gamidroid.gaminode.GamiNode;
import it.unipr.ce.dsg.gamidroid.taskmanagerfm.UPCPFTaskDescriptor;
import it.unipr.ce.dsg.gamidroid.utils.Constants;
import it.unipr.ce.dsg.gamidroid.utils.FileManager;
import it.unipr.ce.dsg.gamidroid.utils.LocationUtils;
import it.unipr.ce.dsg.gamidroid.utils.MenuListAdapter;
import it.unipr.ce.dsg.gamidroid.utils.MenuListElement;
import it.unipr.ce.dsg.gamidroid.utils.Utils;
import it.unipr.ce.dsg.gamidroid.utils.Utils.Orientation;
import it.unipr.ce.dsg.s2p.centralized.interfaces.IEventListener;
import it.unipr.ce.dsg.s2p.centralized.message.JoinResponseMessage;
import it.unipr.ce.dsg.s2p.centralized.utils.Resource;
import it.unipr.ce.dsg.s2pchord.msg.MessageListener;
import it.unipr.ce.dsg.s2pchord.msg.PeerListMessage;
import it.unipr.ce.dsg.s2pchord.resource.ResourceDescriptor;
import it.unipr.ce.dsg.s2pchord.resource.ResourceListener;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * <p>
 * This class represents the main Activity of the Android application.
 * </p>
 * 
 * <p>
 * Copyright (c) 2013, Distributed Systems Group, University of Parma, Italy.
 * Permission is granted to copy, distribute and/or modify this document under
 * the terms of the GNU Free Documentation License, Version 1.3 or any later
 * version published by the Free Software Foundation; with no Invariant
 * Sections, no Front-Cover Texts, and no Back-Cover Texts. A copy of the
 * license is included in the section entitled "GNU Free Documentation License".
 * </p>
 * 
 * @author Alessandro Grazioli (grazioli@ce.unipr.it)
 * 
 */
public class NAM4JAndroidActivity extends FragmentActivity implements
		ConnectionCallbacks, OnConnectionFailedListener, LocationListener,
		OnMarkerClickListener, ResourceListener, MessageListener,
		IEventListener {

	/** Shared Preferences */
	SharedPreferences sharedPreferences;

	/** A request to connect to Location Services */
	private LocationRequest mLocationRequest;

	/** Stores the current instantiation of the location client in this object */
	private GoogleApiClient mGoogleApiClient;

	public static String TAG = "NAM4JAndroidActivity";
	final static int MAX_RESULT = 1;

	private FileManager fileManager;

	private UPCPFTaskDescriptor amiTask;

	private GoogleMap map;

	MarkerOptions markerOptions;
	private Marker marker;

	HashMap<String, Marker> ml;

	Context context;

	private RelativeLayout mainRL;
	private RelativeLayout listRL;
	private RelativeLayout titleBarRL;

	private LatLng currentLocation;

	private ProgressDialog dialog;

	private int animationDuration = 300;

	private boolean showingMenu = false;

	Orientation screenOrientation;

	ImageView connectButtonIv;

	/**
	 * The boolean is used to decide what to do after a LatLng has been
	 * geocoded. If true, it means that the user asked to publish a building so
	 * the related function is called. If false, the user asked to look for a
	 * resource in the LatLng, so the related function is called.
	 */
	private boolean requestPendingForBuilding = false;
	private boolean requestPendingForSensor = false;

	private boolean connected = false;

	/**
	 * The first time a location is available, the map is centered on it and the
	 * boolean is set to false so that further updates do not move the camera
	 */
	private boolean firstLocation = true;

	/** Initial zoom level */
	private int zoom = 15;

	/**
	 * Descriptors to set marker colors (blue ones are placed for assigned
	 * resources, red for researched resources)
	 */
	BitmapDescriptor bitmapDescriptorBlue, bitmapDescriptorRed, blueCircle;

	/** Set to true when the user asks to close the app. */
	private boolean close = false;

	Button menuButton, infoButton, centerButton;

	int screenWidth = 0;
	int screenHeight = 0;

	/** Ratio between the menu size and the screen width */
	double menuWidth = 0.66;

	ListView listView;

	boolean isTablet;

	/** Wi-fi monitor */
	WifiManager wifiManager;
	
	/** Sensors monitor */
	SensorManager sensorManager;

	LinearLayout cpuBar, memoryBar;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		context = this;

		isTablet = Utils.isTablet(this);

		// Setting the default network
		sharedPreferences = getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();
		editor.putString(Constants.NETWORK, Constants.DEFAULT_NETWORK);
		editor.commit();

		// Starting the resources monitoring service
		startService(new Intent(this, DeviceMonitorService.class));

		wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

		// Settings menu elements
		ArrayList<MenuListElement> listElements = new ArrayList<MenuListElement>();

		listElements.add(new MenuListElement(getResources().getString(
				R.string.connectMenuTitle), getResources().getString(
						R.string.connectMenuSubTitle)));
		listElements.add(new MenuListElement(getResources().getString(
				R.string.settingsMenuTitle), getResources().getString(
						R.string.settingsMenuSubTitle)));
		listElements.add(new MenuListElement(getResources().getString(
				R.string.exitMenuTitle), getResources().getString(
						R.string.exitMenuSubTitle)));

		listView = (ListView) findViewById(R.id.ListViewContent);

		MenuListAdapter adapter = new MenuListAdapter(this,
				R.layout.menu_list_view_row, listElements);
		listView.setAdapter(adapter);

		listView.setOnItemClickListener(new ListItemClickListener());

		mainRL = (RelativeLayout) findViewById(R.id.rlContainer);
		listRL = (RelativeLayout) findViewById(R.id.listContainer);

		cpuBar = (LinearLayout) findViewById(R.id.CpuBar);
		memoryBar = (LinearLayout) findViewById(R.id.MemoryBar);

		titleBarRL = (RelativeLayout) findViewById(R.id.titleLl);

		// Adding swipe gesture listener to the top bar
		titleBarRL.setOnTouchListener(new SwipeAndClickListener());

		menuButton = (Button) findViewById(R.id.menuButton);
		menuButton.setOnTouchListener(new SwipeAndClickListener());

		infoButton = (Button) findViewById(R.id.infoButton);
		infoButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				AlertDialog dialog = new AlertDialog(NAM4JAndroidActivity.this) {

					@Override
					public boolean dispatchTouchEvent(MotionEvent event) {
						dismiss();
						return false;
					}

				};

				String text = getResources().getString(R.string.aboutApp);
				String title = getResources().getString(R.string.nam4j);

				dialog.setMessage(text);
				dialog.setTitle(title);
				dialog.setCanceledOnTouchOutside(true);
				dialog.show();
			}
		});

		centerButton = (Button) findViewById(R.id.centerButton);
		centerButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				centerMap();
			}
		});

		isTablet = Utils.isTablet(context);

		int[] screenSize = Utils.getScreenSize(context, getWindow());
		screenWidth = screenSize[0];
		screenHeight = screenSize[1];

		// Updates the display orientation each time the device is rotated
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			screenOrientation = Orientation.LANDSCAPE;
		} else {
			screenOrientation = Orientation.PORTRAIT;
		}

		// If the device is portrait, the menu button is displayed, the menu is
		// hidden and the swipe listener is added to the menu bar
		if (screenOrientation == Orientation.PORTRAIT) {

			menuButton.setVisibility(View.VISIBLE);

			// Adding swipe gesture listener to the top bar
			titleBarRL.setOnTouchListener(new SwipeAndClickListener());

			if (isTablet) {
				menuWidth = 0.35;
			} else {
				menuWidth = 0.6;
			}
		} else {
			// If the device is a tablet in landscape, the menu button is
			// hidden, the menu is displayed, the swipe listener is not added to
			// the menu bar and the mainRL width is set to the window's width
			// minus the menu's width
			if (isTablet) {
				menuWidth = 0.2;
				menuButton.setVisibility(View.INVISIBLE);

				RelativeLayout.LayoutParams menuListLP = (LayoutParams) mainRL
						.getLayoutParams();

				// Setting the main view width as the container width without
				// the menu
				menuListLP.width = (int) (screenWidth * (1 - menuWidth));
				mainRL.setLayoutParams(menuListLP);

				displaySideMenu();
			} else {
				menuWidth = 0.4;
				menuButton.setVisibility(View.VISIBLE);

				// Adding swipe gesture listener to the top bar
				titleBarRL.setOnTouchListener(new SwipeAndClickListener());
			}
		}

		// Check if the device has the Google Play Services installed and
		// updated. They are necessary to use Google Maps
		int code = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);

		if (code != ConnectionResult.SUCCESS) {

			showErrorDialog(code);

			System.out.println("Google Play Services error");

			FrameLayout fl = (FrameLayout) findViewById(R.id.frameId);
			fl.removeAllViews();
		}
		else {
			// Create a new global location parameters object
			mLocationRequest = new LocationRequest();

			// Set the update interval
			mLocationRequest.setInterval(LocationUtils.UPDATE_INTERVAL_IN_MILLISECONDS);

			// Use high accuracy
			mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

			// Set the interval ceiling to one minute
			mLocationRequest.setFastestInterval(LocationUtils.FAST_INTERVAL_CEILING_IN_MILLISECONDS);

			bitmapDescriptorBlue = BitmapDescriptorFactory
					.defaultMarker(BitmapDescriptorFactory.HUE_AZURE);

			blueCircle = BitmapDescriptorFactory.fromBitmap(BitmapFactory
					.decodeResource(context.getResources(),
							R.drawable.blue_circle));

			bitmapDescriptorRed = BitmapDescriptorFactory
					.defaultMarker(BitmapDescriptorFactory.HUE_RED);

			// Create a new location client, using the enclosing class to handle
			// callbacks
			mGoogleApiClient = new GoogleApiClient.Builder(this)
					.addConnectionCallbacks(this)
					.addOnConnectionFailedListener(this)
					.addApi(LocationServices.API)
					.build();

			map = ((SupportMapFragment) getSupportFragmentManager()
					.findFragmentById(R.id.mapview)).getMap();

			if (map != null) {

				// Set default map center and zoom on Parma
				double lat = 44.7950156;
				double lgt = 10.32547;
				map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lgt), 12.0f));

				// Adding listeners to the map respectively for zoom level
				// change and onTap event
				map.setOnCameraChangeListener(getCameraChangeListener());
				map.setOnMapClickListener(getOnMapClickListener());

				// Set map type as normal (i.e. not the satellite view)
				map.setMapType(com.google.android.gms.maps.GoogleMap.MAP_TYPE_NORMAL);

				// Hide traffic layer
				map.setTrafficEnabled(false);

				// Enable the 'my-location' layer, which continuously draws an
				// indication of a user's current location and bearing, and
				// displays UI controls that allow the interaction with the
				// location itself
				// map.setMyLocationEnabled(true);

				ml = new HashMap<String, Marker>();

				// Get file manager for config files
				fileManager = FileManager.getFileManager();
				fileManager.createFiles();

				map.setOnMarkerClickListener(this);

			} else {
				AlertDialog.Builder dialog = new AlertDialog.Builder(this);
				dialog.setMessage("The map cannot be initialized.");
				dialog.setCancelable(true);
				dialog.setPositiveButton(getResources().getString(R.string.ok),
						new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.dismiss();
					}
				});
				dialog.show();
			}
		}
	}

	/**
	 * Method to display the menu of the app.
	 */
	private void displaySideMenu() {

		TranslateAnimation animation = null;

		// If it's a tablet in landscape, the menu is always displayed, else
		// it's activated by the user
		if (!isTablet || (isTablet && screenOrientation == Orientation.PORTRAIT)) {
			if (!showingMenu) {

				RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mainRL.getLayoutParams();

				animation = new TranslateAnimation(0, listRL.getMeasuredWidth()	- layoutParams.leftMargin, 0, 0);

				animation.setDuration(animationDuration);
				animation.setFillEnabled(true);
				animation.setAnimationListener(new AnimationListener() {

					@Override
					public void onAnimationStart(Animation animation) {}

					@Override
					public void onAnimationRepeat(Animation animation) {}

					@Override
					public void onAnimationEnd(Animation animation) {

						// At the end, set the final position as the current one
						RelativeLayout.LayoutParams lpList = (LayoutParams) mainRL.getLayoutParams();
						lpList.setMargins(listRL.getMeasuredWidth(), 0,	-listRL.getMeasuredWidth(), 0);
						mainRL.setLayoutParams(lpList);

						showingMenu = true;
					}
				});

			} else {

				RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mainRL.getLayoutParams();

				animation = new TranslateAnimation(0, -layoutParams.leftMargin, 0, 0);
				animation.setDuration(animationDuration);
				animation.setFillEnabled(true);
				animation.setAnimationListener(new AnimationListener() {

					@Override
					public void onAnimationStart(Animation animation) {}

					@Override
					public void onAnimationRepeat(Animation animation) {}

					@Override
					public void onAnimationEnd(Animation animation) {

						// At the end, set the final position as the current one
						RelativeLayout.LayoutParams mainContenrLP = (LayoutParams) mainRL.getLayoutParams();
						mainContenrLP.setMargins(0, 0, 0, 0);
						mainRL.setLayoutParams(mainContenrLP);

						showingMenu = false;
					}
				});
			}

			mainRL.startAnimation(animation);
		}

		else {
			// Showing the menu since the tablet is in landscape orientation
			RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mainRL.getLayoutParams();

			animation = new TranslateAnimation(0, listRL.getMeasuredWidth() - layoutParams.leftMargin, 0, 0);

			animation.setDuration(animationDuration);
			animation.setFillEnabled(true);
			animation.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationStart(Animation animation) {}

				@Override
				public void onAnimationRepeat(Animation animation) {}

				@Override
				public void onAnimationEnd(Animation animation) {

					// At the end, set the final position as the current one
					RelativeLayout.LayoutParams lpList = (LayoutParams) mainRL.getLayoutParams();
					lpList.setMargins(listRL.getMeasuredWidth(), 0,	-listRL.getMeasuredWidth(), 0);
					mainRL.setLayoutParams(lpList);

					showingMenu = true;
				}
			});

			mainRL.startAnimation(animation);
		}
	}

	public OnCameraChangeListener getCameraChangeListener() {
		return new OnCameraChangeListener() {
			@Override
			public void onCameraChange(CameraPosition position) {
				zoom = (int) position.zoom;
			}
		};
	}

	public OnMapClickListener getOnMapClickListener() {
		return new OnMapClickListener() {
			@Override
			public void onMapClick(LatLng arg0) {

				if (connected) {
					// Reverse geocoding the tapped location
					new ReverseGeocodingTask(getBaseContext()).execute(arg0);
				} else {
					Toast.makeText(getApplicationContext(), getResources().getString(R.string.not_connected), Toast.LENGTH_LONG).show();
				}
			}
		};
	}

	@Override
	public void onStop() {

		// If the client is connected
		if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
			stopLocationUpdates();
		}

		// After disconnect() is called, the client is considered "dead".
		if (mGoogleApiClient != null)
			mGoogleApiClient.disconnect();

		stopService(new Intent(context, DeviceMonitorService.class));

		// Unregistering the broadcast receivers
		// unregisterReceiver(bReceiver);
		unregisterReceiver(mConnReceiver);
		unregisterReceiver(wifiReceiver);

		super.onStop();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onStart() {

		super.onStart();

		if (mGoogleApiClient != null)
			mGoogleApiClient.connect();

		// Registering the broadcast receivers
		//registerReceiver(bReceiver, new IntentFilter(Constants.RECEIVED_RESOURCES_UPDATE));

		registerReceiver(mConnReceiver, new IntentFilter(
				ConnectivityManager.CONNECTIVITY_ACTION));

		registerReceiver(wifiReceiver, new IntentFilter(
				WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

		registerReceiver(batteryReceiver, new IntentFilter(
				Intent.ACTION_BATTERY_CHANGED));

	}
	
	@Override
	public void onResume() {
		super.onResume();

		int[] screenSize = Utils.getScreenSize(context, getWindow());
		screenWidth = screenSize[0];
		screenHeight = screenSize[1];

		final RelativeLayout listRL = (RelativeLayout) findViewById(R.id.listContainer);
		final RelativeLayout listRLContainer = (RelativeLayout) findViewById(R.id.rlListViewContent);
		final RelativeLayout shadowRL = (RelativeLayout) findViewById(R.id.shadowContainer);

		RelativeLayout.LayoutParams menuListLP = (LayoutParams) listRL.getLayoutParams();

		// Setting the ListView width as the container width without the shadow
		// RelativeLayout
		menuListLP.width = (int) (screenWidth * menuWidth);
		listRL.setLayoutParams(menuListLP);

		RelativeLayout.LayoutParams listP = (LayoutParams) listRLContainer
				.getLayoutParams();
		listP.width = (int) (screenWidth * menuWidth)
				- shadowRL.getLayoutParams().width;
		listRLContainer.setLayoutParams(listP);

	}

	/**
	 * Check if Google Play services are available before making a request.
	 * 
	 * @return true if Google Play services are available, false otherwise
	 */
	private boolean servicesConnected() {

		Log.d(NAM4JAndroidActivity.TAG, this.getString(R.string.checkingGooglePlayServicesAvailability));

		// Check that Google Play services is available
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

		if (ConnectionResult.SUCCESS == resultCode) {
			Log.d(NAM4JAndroidActivity.TAG,	this.getString(R.string.play_services_availability));
			return true;
		} else {
			showErrorDialog(resultCode);
			return false;
		}
	}

	@Override
	public void onConnected(Bundle bundle) {
		Log.d(NAM4JAndroidActivity.TAG, this.getString(R.string.location_services_connected));

		if (servicesConnected()) {

			// Centering map on last known location, if available
			Location mLastLocation = null;
			mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
			
			if (mLastLocation != null) {
				if (firstLocation) {
					firstLocation = false;
					LatLng myLocation = new LatLng(
							mLastLocation.getLatitude(),
							mLastLocation.getLongitude());

					map.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, zoom));
				}
			}

			startLocationUpdates();
		}
	}

	@Override
	public void onConnectionSuspended(int cause) {
		Log.d(NAM4JAndroidActivity.TAG, this.getString(R.string.disconnected_location_services));
	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {

		// Google Play services can resolve some errors it detects. If the error
		// has a resolution, try sending an Intent to start a Google Play
		// services activity that can resolve error.
		if (connectionResult.hasResolution()) {
			try {
				// Start an Activity that tries to resolve the error
				connectionResult.startResolutionForResult(this, LocationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST);
			} catch (IntentSender.SendIntentException e) {
				e.printStackTrace();
			}
		} else {
			// If no resolution is available, display a dialog to the user with
			// the error
			showErrorDialog(connectionResult.getErrorCode());
		}
	}

	/**
	 * Report location updates to the UI.
	 * 
	 * @param location
	 *            The updated location.
	 */
	@Override
	public void onLocationChanged(Location location) {
		if (location != null) {
			LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());
			currentLocation = myLocation;

			// Center map on current location just the first time a location is
			// available
			if (firstLocation) {
				firstLocation = false;
				map.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, zoom));
			}

			if (marker == null) {
				marker = map.addMarker(new MarkerOptions().position(currentLocation).icon(blueCircle));
			} else {
				marker.setPosition(currentLocation);
			}

		} else
			Log.d(NAM4JAndroidActivity.TAG, this.getString(R.string.location_not_available));
	}

	/** Method that centers the map on current user location. */
	private void centerMap() {
		if (currentLocation != null) {
			CameraPosition newCamPos = new CameraPosition(new LatLng(
					currentLocation.latitude, currentLocation.longitude), zoom,
					map.getCameraPosition().tilt, // use old tilt
					map.getCameraPosition().bearing); // use old bearing
			map.animateCamera(CameraUpdateFactory.newCameraPosition(newCamPos), 400, null);
		} else {
			Toast.makeText(context, getResources().getString(R.string.noLocationInfoAvailable), Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * In response to a request to start updates, send a request to Location
	 * Services.
	 */
	private void startLocationUpdates() {
		if (mGoogleApiClient != null) {
			LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
			Log.d(NAM4JAndroidActivity.TAG, this.getString(R.string.updates_started));
		} else {
			Log.d(NAM4JAndroidActivity.TAG, this.getString(R.string.updates_request_error));
		}
	}

	/**
	 * In response to a request to stop updates, send a request to Location
	 * Services.
	 */
	private void stopLocationUpdates() {
		if (mGoogleApiClient != null) {
			LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
			Log.d(NAM4JAndroidActivity.TAG, this.getString(R.string.updates_stopped));
		} else {
			Log.d(NAM4JAndroidActivity.TAG, this.getString(R.string.updates_stop_error));
		}
	}

	/**
	 * Show a dialog returned by Google Play services for the connection error
	 * code
	 * 
	 * @param errorCode
	 *            An error code returned from onConnectionFailed
	 */
	private void showErrorDialog(int errorCode) {

		// Get the error dialog from Google Play services
		Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(errorCode, this, LocationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST);

		// If Google Play services can provide an error dialog
		if (errorDialog != null) {

			// Create a new DialogFragment in which to show the error dialog
			ErrorDialogFragment errorFragment = new ErrorDialogFragment();

			// Set the dialog in the DialogFragment
			errorFragment.setDialog(errorDialog);

			// Show the error dialog in the DialogFragment
			errorFragment.show(getSupportFragmentManager(), NAM4JAndroidActivity.TAG);
		} else {
			Toast.makeText(context, "Incompatible version of Google Play Services", Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * Define a DialogFragment to display the error dialog generated in
	 * showErrorDialog.
	 */
	public static class ErrorDialogFragment extends DialogFragment {

		// Global field to contain the error dialog
		private Dialog mDialog;

		/**
		 * Default constructor. Sets the dialog field to null
		 */
		public ErrorDialogFragment() {
			super();
			mDialog = null;
		}

		/**
		 * Set the dialog to display
		 * 
		 * @param dialog
		 *            An error dialog
		 */
		public void setDialog(Dialog dialog) {
			mDialog = dialog;
		}

		/*
		 * This method must return a Dialog to the DialogFragment.
		 */
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			return mDialog;
		}

		/* Close the app when the user clicks on the dialog button */
		@Override
		public void onDismiss(DialogInterface dialog) {
			super.onDismiss(dialog);

			getActivity().finish();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		String message = getResources().getString(
				R.string.exitOnBackButtonPressed);

		if (keyCode == KeyEvent.KEYCODE_BACK) {

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(message)
			.setCancelable(false)
			.setPositiveButton(getResources().getString(R.string.yes),
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface iDialog,
						int id) {

					stopService(new Intent(context,
							DeviceMonitorService.class));

					// Unregistering the broadcast receivers
					//	unregisterReceiver(bReceiver);
					unregisterReceiver(mConnReceiver);
					unregisterReceiver(wifiReceiver);

					if (connected) {

						close = true;

						/*
						 * The progress dialog is not created by
						 * the AsyncTask because the peer just
						 * sends a message to the bootstrap.
						 * This takes very few time and the
						 * dialog would disappear almost
						 * immediately. By showing it before
						 * executing the AsyncTask, it can stay
						 * on the screen until the bootstrap
						 * informs the peer that it is part of
						 * the network. Thus, the dialog is
						 * removed from the function listening
						 * for received messages
						 * onReceivedMessage(String).
						 */
						dialog = new ProgressDialog(NAM4JAndroidActivity.this);
						dialog.setMessage(NAM4JAndroidActivity.this.getString(R.string.pwLeaving));
						dialog.show();

						new LeaveNetwork(null).execute();

						connected = false;

					} else {

						// finish();

						// AFTER LEAVING THE NETWORK, THE APP GETS CLOSED

						/*
						 * Notify the system to finalize and
						 * collect all objects of the app on
						 * exit so that the virtual machine
						 * running the app can be killed by the
						 * system without causing issues.
						 * 
						 * NOTE: If this is set to true then the
						 * virtual machine will not be killed
						 * until all of its threads have closed.
						 */
						System.runFinalizersOnExit(true);

						/*
						 * Force the system to close the app
						 * down completely instead of retaining
						 * it in the background. The virtual
						 * machine that runs the app will be
						 * killed. The app will be completely
						 * created as a new app in a new virtual
						 * machine running in a new process if
						 * the user starts the app again.
						 */
						System.exit(0);
					}
				}
			})
			.setNegativeButton(getResources().getString(R.string.no),
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,
						int id) {
					dialog.cancel();
				}
			});

			AlertDialog alert = builder.create();
			alert.show();

			return true;
		}

		else if (keyCode == KeyEvent.KEYCODE_MENU) {
			displaySideMenu();
		}

		return super.onKeyDown(keyCode, event);
		// return true;
	}

	/**
	 * Method to process a resource descriptor received on the network.
	 * 
	 * @param resourceDescriptor
	 * 			The resource descriptor
	 */
	private void processResourceDescriptor(String resourceDescriptor, String reason) {
		
		JSONObject obj;
		try {
			obj = new JSONObject(resourceDescriptor);

			JSONObject locationObj = obj.getJSONObject("location");
			JSONObject locationValue = new JSONObject(locationObj.getString("value"));
			JSONObject building = locationValue.getJSONObject("building");

			// The address to geocode
			String buildingValue = building.getString("value");

			String[] data = new String[]{buildingValue, reason};
			
			new GeocodeTask(getBaseContext()).execute(data);

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Method to manage a resource received on the Chord network.
	 * 
	 * @param rd
	 *            The resource descriptor
	 * 
	 * @param reason
	 *            A string defined in {@link Constants} class stating if the
	 *            resource has been received upon assignment or search
	 */
	@Override
	public void onReceivedResource(ResourceDescriptor rd, String reason) {
		// If the user leaves the network, the node may receive answers to
		// requests sent to the network before leaving it, so a check on
		// connected boolean is performed
		if (connected) {
			if (reason.equalsIgnoreCase(Constants.reasonResearched)) {
				Log.d(NAM4JAndroidActivity.TAG, "Received the researched resource");
				processResourceDescriptor(rd.getAttachment(), Constants.reasonResearched);
			} else if (reason.equalsIgnoreCase(Constants.reasonAssigned)) {
				Log.d(NAM4JAndroidActivity.TAG, "Received a resource for the peer to be its responsible");
				processResourceDescriptor(rd.getAttachment(), Constants.reasonAssigned);
			}
		}
	}
	
	/**
	 * Method to manage a resource received on the Mesh network upon research.
	 * 
	 * @param resource
	 *            The resource descriptor
	 */
	@Override
	public void onFoundSearchedResource(Resource resource) {
		// If the user leaves the network, the node may receive answers to
		// requests sent to the network before leaving it, so a check on
		// connected boolean is performed
		if (connected) {
			// Adding a marker on the map for the received resource
			Set<String> keySet = resource.getKeySet();
			for (String k : keySet) {
				processResourceDescriptor(resource.getValue(k), Constants.reasonResearched);
			}
		}
	}

	/**
	 * Method to manage a resource received on the Mesh network upon assignment.
	 * 
	 * @param resource
	 *            The resource descriptor
	 */
	@Override
	public void onReceivedResourceToBeResponsible(Resource resource) {
		// If the user leaves the network, the node may receive answers to
		// requests sent to the network before leaving it, so a check on
		// connected boolean is performed
		if (connected) {
			// Adding a marker on the map for the received resource
			Set<String> keySet = resource.getKeySet();
			for (String k : keySet) {
				processResourceDescriptor(resource.getValue(k), Constants.reasonAssigned);
			}
		}
	}

	private void startBuildingPublishActivity(String addresses) {
		requestPendingForBuilding = false;
		if (connected) {
			Bundle args = new Bundle();
			args.putParcelable("CurrentLocation", currentLocation);
			Intent i = new Intent(this, BuildingPublishActivity.class);
			i.putExtra("Address", addresses);
			i.putExtra("Bundle", args);
			startActivity(i);
		}
	}

	private void startSensorPublishActivity(String addresses) {
		requestPendingForSensor = false;
		if (connected) {
			Bundle args = new Bundle();
			args.putParcelable("CurrentLocation", currentLocation);
			Intent i = new Intent(this, SensorPublishActivity.class);
			i.putExtra("Address", addresses);
			i.putExtra("Bundle", args);
			startActivity(i);
		}
	}

	private void connect() {
		// The progress dialog is not created by the AsyncTask because the peer
		// just sends a message to the bootstrap. This takes very few time and
		// the dialog would disappear almost immediately. By showing it before
		// executing the AsyncTask, it can stay on the screen until the
		// bootstrap informs the peer that it is part of the network. Thus, the
		// dialog is removed from the function listening for received messages
		// onReceivedMessage(String).
		dialog = new ProgressDialog(NAM4JAndroidActivity.this);
		dialog.setMessage(NAM4JAndroidActivity.this.getString(R.string.pwJoining));
		dialog.show();
		new JoinNetwork(null).execute();
	}

	private void afterConnected() {
		sharedPreferences = getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE);
		String currentNetwork = sharedPreferences.getString(Constants.NETWORK, "");
		
		if (currentNetwork.equalsIgnoreCase(Constants.CHORD)) {
			GamiNode.addChordResourceListener(this);
			GamiNode.addChordMessageListener(this);
		} else if (currentNetwork.equalsIgnoreCase(Constants.MESH)) {
			GamiNode.addMeshResourceListener(this);
			GamiNode.addMeshMessageListener(this);
		}

		amiTask = new UPCPFTaskDescriptor("AmITask", "T1");
		amiTask.setState("UNSTARTED");
	}

	private void showSettings() {
		Intent i = new Intent(this, SettingsActivity.class);
		startActivity(i);
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		LatLng p = marker.getPosition();

		Log.d(NAM4JAndroidActivity.TAG, "Tapped " + p.latitude + " ; " + p.longitude);

		if (connected) {
			// Reverse geocode the tapped location
			new ReverseGeocodingTask(getBaseContext()).execute(p);
		} else {
			Toast.makeText(getApplicationContext(),
					getResources().getString(R.string.not_connected),
					Toast.LENGTH_LONG).show();
		}

		return false;
	}

	private void showAddress(final String addressTouch) {

		Log.d(NAM4JAndroidActivity.TAG, "Tapped address: " + addressTouch);

		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setMessage("Do you want to see information about building located in " + addressTouch + " ?");
		dialog.setCancelable(true);
		dialog.setPositiveButton(getResources().getString(R.string.yes),
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.dismiss();
				Intent intent = new Intent(NAM4JAndroidActivity.this,
						BuildingLookupActivity.class);

				intent.putExtra("AddressBuilding", addressTouch);
				NAM4JAndroidActivity.this.startActivity(intent);
			}
		});
		dialog.setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				dialog.dismiss();
			}
		});

		dialog.show();
	}

	private void leftNetwork() {
		if (dialog.isShowing()) {
			dialog.dismiss();
		}

		Toast.makeText(getApplicationContext(),	getResources().getString(R.string.disconnected_from_network), Toast.LENGTH_LONG).show();

		// Settings menu elements
		ArrayList<MenuListElement> listElements = new ArrayList<MenuListElement>();

		listElements.add(new MenuListElement(getResources().getString(
				R.string.connectMenuTitle), getResources().getString(
						R.string.connectMenuSubTitle)));
		listElements.add(new MenuListElement(getResources().getString(
				R.string.settingsMenuTitle), getResources().getString(
						R.string.settingsMenuSubTitle)));
		listElements.add(new MenuListElement(getResources().getString(
				R.string.exitMenuTitle), getResources().getString(
						R.string.exitMenuSubTitle)));

		MenuListAdapter adapter = new MenuListAdapter(this,
				R.layout.menu_list_view_row, listElements);
		listView.setAdapter(adapter);

		// Cleaning the map and the HashMap containing the known resources
		runOnUiThread(new Runnable() {
			public void run() {
				map.clear();
				ml.clear();
			}
		});
	}

	private void closeApp() {
		if (dialog.isShowing()) {
			dialog.dismiss();
		}

		// finish();
		System.runFinalizersOnExit(true);
		System.exit(0);
	}

	private void addReceivedResourceMarkerOnMap(final LatLng p, final String address, final String reason) {
		if (p != null) {
			if (ml.get(address) == null) {

				Log.d(NAM4JAndroidActivity.TAG,
						"Added a new marker on the map for position "
								+ p.latitude + " , " + p.longitude
								+ " for the researched resource");

				// The following Runnable is because the only way to interact
				// with the GUI is through the UI thread
				runOnUiThread(new Runnable() {
					public void run() {

						Toast.makeText(
								context,
								"Added a new marker on the map at address "
										+ address + " (" + p.latitude + ", "
										+ p.longitude
										+ ") for the researched resource",
										Toast.LENGTH_LONG).show();

						Marker marker = null;
						
						// Creating the marker whose color is set based on the
						// reason for which the resource has been received
						if (reason.equalsIgnoreCase(Constants.reasonResearched)) {
							// Researched resources have a red marker
							marker = map.addMarker(new MarkerOptions().position(p).icon(bitmapDescriptorRed).title(address).snippet(""));
							
						} else if (reason.equalsIgnoreCase(Constants.reasonAssigned)) {
							// Assigned resources have a blue marker
							marker = map.addMarker(new MarkerOptions().position(p).icon(bitmapDescriptorBlue).title(address).snippet(""));
						}

						if (marker != null) {
							ml.put(address, marker);
						}
					}
				});
				
			} else {
				System.out.println("Marker is already present");
			}
		}
	}

	@Override
	public void onReceivedMessage(String msg) {
		System.out.println("Received message of type " + msg);

		// Connected to the network
		if ((msg.equalsIgnoreCase(PeerListMessage.MSG_PEER_LIST) // Chord network join response
				|| msg.equalsIgnoreCase(JoinResponseMessage.MSG_KEY)) // Mesh network join response
				&& !connected) {

			// Settings menu elements
			ArrayList<MenuListElement> listElements = new ArrayList<MenuListElement>();

			listElements.add(new MenuListElement(getResources().getString(
					R.string.disconnectMenuTitle), getResources().getString(
							R.string.disconnectMenuSubTitle)));
			listElements.add(new MenuListElement(getResources().getString(
					R.string.publishBuildingMenuTitle), getResources()
					.getString(R.string.publishBuildingMenuSubTitle)));
			listElements.add(new MenuListElement(getResources().getString(
					R.string.publishSensorgMenuTitle), getResources()
					.getString(R.string.publishSensorgMenuSubTitle)));
			listElements.add(new MenuListElement(getResources().getString(
					R.string.exitMenuTitle), getResources().getString(
							R.string.exitMenuSubTitle)));

			final MenuListAdapter adapter = new MenuListAdapter(this,
					R.layout.menu_list_view_row, listElements);

			connected = true;

			runOnUiThread(new Runnable() {
				public void run() {
					if (dialog.isShowing()) {
						dialog.dismiss();
					}

					Toast.makeText(context,	getResources().getString(R.string.connection_started), Toast.LENGTH_LONG).show();
					listView.setAdapter(adapter);
				}
			});
		}
	}

	// BroadcastReceiver objects used to receive messages informing about
	// changes which occur in the state of the CPU, the memory and the wifi.

	// CPU and memory monitoring BroadcastReceiver (provided by
	// DeviceMonitorService class)
	private final BroadcastReceiver bReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {

			/* Checking if received message is from the monitoring Service */
			if (intent.getAction().equals(Constants.RECEIVED_RESOURCES_UPDATE)) {

				ResourceMonitor rm = (ResourceMonitor) intent
						.getSerializableExtra("resourceDescriptor");

				float usedCpuPerc = rm.getUsedCpuPerc();
				float availableMem = rm.getAvailableMem();
				float totalMem = rm.getTotalMem();

				float memUsagePerc = (new BigDecimal(
						Float.toString(availableMem / totalMem)).setScale(2,
								BigDecimal.ROUND_HALF_UP)).floatValue() * 100;

				/*
				 * String cpuUsageMsg = "CPU usage: " + (usedCpuPerc * 100) +
				 * "%";
				 * 
				 * String memUsageMsg = "Mem usage: " + memUsagePerc +
				 * "% (using " + availableMem + " MB out of " + totalMem +
				 * " MB)";
				 * 
				 * tvCPU.setText(cpuUsageMsg); tvMem.setText(memUsageMsg);
				 */

				LayoutParams memoryLp = (LayoutParams) memoryBar
						.getLayoutParams();
				memoryLp.height = (int) (10 * Math.ceil(memUsagePerc / 10));
				memoryBar.setLayoutParams(memoryLp);

				LayoutParams barLp = (LayoutParams) cpuBar.getLayoutParams();
				barLp.height = (int) (10 * Math.ceil((usedCpuPerc * 100) / 10));
				cpuBar.setLayoutParams(barLp);
			}
		}
	};

	// Network changes BroadcastReceiver (provided by Android's
	// ConnectivityManager class)
	private BroadcastReceiver mConnReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

			String networkStatus = "";

			boolean noConnectivity = intent.getBooleanExtra(
					ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);

			String reason = intent
					.getStringExtra(ConnectivityManager.EXTRA_REASON);

			// No connectivity is available
			if (noConnectivity) {
				networkStatus += "No Connectivity. The reason is: " + reason + "\n";
			}

			NetworkInfo currentNetworkInfo = (NetworkInfo) intent
					.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);

			NetworkInfo otherNetworkInfo = (NetworkInfo) intent
					.getParcelableExtra(ConnectivityManager.EXTRA_OTHER_NETWORK_INFO);

			if (currentNetworkInfo != null)
				networkStatus += "Current Network info: "
						+ currentNetworkInfo.getTypeName() + " ("
						+ currentNetworkInfo.getType() + ") ["
						+ currentNetworkInfo.getState() + "]\n";

			if (otherNetworkInfo != null)
				networkStatus += "Other Network info: "
						+ otherNetworkInfo.getTypeName() + " ("
						+ currentNetworkInfo.getType() + ") ["
						+ currentNetworkInfo.getState() + "]";

			// tvNetwork.setText(networkStatus);
			System.out.println("--- networkStatus = " + networkStatus);
		}
	};

	// Wifi monitoring BroadcastReceiver (provided by Android's WifiManager class)
	private BroadcastReceiver wifiReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

			List<ScanResult> results = wifiManager.getScanResults();

			List<String> networks = new ArrayList<String>();

			ScanResult bestSignal = null;

			String wifiMsg = results.size() + " access points found\n";

			for (ScanResult result : results) {

				if (!networks.contains(result.SSID)) {
					networks.add(result.SSID);
				}

				wifiMsg += result.SSID + " (strength: " + result.level + "); ";

				if (bestSignal == null
						|| WifiManager.compareSignalLevel(bestSignal.level,
								result.level) < 0)
					bestSignal = result;
			}

			wifiMsg += "\nThe strongest is: " + bestSignal.SSID;

			// tvWifi.setText(wifiMsg);
			System.out.println("--- wifiMsg = " + wifiMsg);

		}
	};

	// Battery monitoring broadcast receiver
	private BroadcastReceiver batteryReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
			boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING
					|| status == BatteryManager.BATTERY_STATUS_FULL;

			/*
			 * int chargePlug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED,
			 * -1); boolean usbCharge = chargePlug ==
			 * BatteryManager.BATTERY_PLUGGED_USB; boolean acCharge = chargePlug
			 * == BatteryManager.BATTERY_PLUGGED_AC;
			 */

			int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
			int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

			float batteryPct = level / (float) scale;

			String batteryStatus = "";

			if (isCharging)
				batteryStatus += "The battery is charging";
			else
				batteryStatus += "The battery is not charging";

			batteryStatus += "\nBattery is " + (batteryPct * 100) + "% charged";

			// tvBattery.setText(batteryStatus);
			System.out.println("--- batteryStatus = " + batteryStatus);
		}
	};

	private class ReverseGeocodingTask extends AsyncTask<LatLng, Void, String> {
		Context mContext;

		public ReverseGeocodingTask(Context context) {
			super();
			mContext = context;
		}

		// Finding address using reverse geocoding
		@Override
		protected String doInBackground(LatLng... params) {

			Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());

			double latitude = params[0].latitude;
			double longitude = params[0].longitude;

			List<Address> addresses = null;
			String addressTouch = null;

			try {
				addresses = geocoder.getFromLocation(latitude, longitude, 1);
			} catch (IOException e) {
				e.printStackTrace();
			}

			if (addresses != null && addresses.size() > 0) {
				addressTouch = addresses.get(0).getAddressLine(0);
			}

			if (addressTouch == null) {

				Log.d(NAM4JAndroidActivity.TAG, "Geocoder service is not available; trying with a http request to Google Maps...");

				HttpURLConnection urlConnection = null;
				try {
					URL url = new URL(
							"http://maps.googleapis.com/maps/api/geocode/json?latlng="
									+ latitude + "," + longitude
									+ "&sensor=false");
					urlConnection = (HttpURLConnection) url.openConnection();

					InputStream in = new BufferedInputStream(
							urlConnection.getInputStream());

					BufferedReader streamReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
					StringBuilder responseStrBuilder = new StringBuilder();

					String inputStr;
					while ((inputStr = streamReader.readLine()) != null)
						responseStrBuilder.append(inputStr);

					JSONObject obj = new JSONObject(responseStrBuilder.toString());

					JSONArray resultsArray = obj.getJSONArray("results");
					JSONObject resultsArrayFirstElement = resultsArray.getJSONObject(0);

					String formattedAddress = resultsArrayFirstElement.get("formatted_address").toString();
					System.out.println("Formatted address: " + formattedAddress);

					addressTouch = formattedAddress;
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

				finally {
					if(urlConnection != null)
						urlConnection.disconnect();
				}

				return addressTouch;
			} else {
				return addressTouch;
			}

		}

		@Override
		protected void onPostExecute(String addresses) {

			if (addresses != null) {
				if (requestPendingForBuilding)
					startBuildingPublishActivity(addresses);
				else if (requestPendingForSensor)
					startSensorPublishActivity(addresses);
				else
					showAddress(addresses);
			} else {
				Toast.makeText(
						getApplicationContext(),
						getResources().getString(
								R.string.geocoder_not_available),
								Toast.LENGTH_LONG).show();
			}
		}
	}

	private class GeocodeTask extends AsyncTask<String, Void, LatLng> {
		Context mContext;
		String strAddress;
		String reason;

		public GeocodeTask(Context context) {
			super();
			mContext = context;
		}

		// Reverse geocoding current location
		@Override
		protected LatLng doInBackground(String... params) {

			Geocoder coder = new Geocoder(mContext);
			List<Address> addresses = null;
			LatLng p1 = null;

			strAddress = params[0];
			reason = params[1];

			try {
				addresses = coder.getFromLocationName(strAddress, 5);
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (addresses != null && addresses.size() > 0) {
				Address location = addresses.get(0);

				p1 = new LatLng((float) (location.getLatitude()), (float) (location.getLongitude()));
			} else {
				Log.d(NAM4JAndroidActivity.TAG, "Geocoder service is not available; trying with a http request to Google Maps...");
				
				HttpURLConnection urlConnection = null;
				try {
					URL url = new URL(Constants.GEOCODER_ADDRESS + strAddress.replaceAll("\\s+", "+") + "&sensor=false");
					urlConnection = (HttpURLConnection) url.openConnection();

					InputStream in = new BufferedInputStream(
							urlConnection.getInputStream());

					BufferedReader streamReader = new BufferedReader(
							new InputStreamReader(in, "UTF-8"));
					StringBuilder responseStrBuilder = new StringBuilder();

					String inputStr;
					while ((inputStr = streamReader.readLine()) != null)
						responseStrBuilder.append(inputStr);

					JSONObject obj = new JSONObject(
							responseStrBuilder.toString());

					JSONArray resultsArray = obj.getJSONArray("results");
					JSONObject resultsArrayFirstElement = resultsArray
							.getJSONObject(0);

					JSONObject geometryObj = resultsArrayFirstElement
							.getJSONObject("geometry");
					JSONObject locationObj = geometryObj
							.getJSONObject("location");

					Float lat = Float.parseFloat(locationObj
							.getString("lat"));
					Float lng = Float.parseFloat(locationObj
							.getString("lng"));

					p1 = new LatLng((double) (lat), (double) (lng));

				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

				finally {
					urlConnection.disconnect();
				}
			}

			return p1;
		}

		@Override
		protected void onPostExecute(LatLng p) {

			if (p != null) {
				addReceivedResourceMarkerOnMap(p, strAddress, reason);
			} else {
				Toast.makeText(
						getApplicationContext(),
						getResources().getString(
								R.string.geocoder_not_available),
								Toast.LENGTH_LONG).show();
			}
		}
	}

	private class JoinNetwork extends AsyncTask<Void, Void, Void> {
		public JoinNetwork(Void v) {
			super();
		}

		@Override
		protected Void doInBackground(Void... params) {
			GamiNode.getAndroidGamiNode(context);
			return null;
		}

		@Override
		protected void onPostExecute(Void v) {
			afterConnected();
		}
	}

	private class LeaveNetwork extends AsyncTask<Void, Void, Void> {
		public LeaveNetwork(Void v) {
			super();
		}

		@Override
		protected Void doInBackground(Void... params) {
			GamiNode.disconnect();
			return null;
		}

		@Override
		protected void onPostExecute(Void v) {
			if (close) {
				closeApp();
			} else {
				leftNetwork();
			}
		}
	}

	class ListItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> clickedList,
				View clickedElement, final int clickedElementPosition,
				long clickedRowId) {

			if (showingMenu) {

				// If it's a tablet in landscape, the menu is always displayed
				// so no animation is necessary
				if (!isTablet || (isTablet && screenOrientation == Orientation.PORTRAIT)) {

					final RelativeLayout mainRL = (RelativeLayout) findViewById(R.id.rlContainer);
					final RelativeLayout listRL = (RelativeLayout) findViewById(R.id.listContainer);

					TranslateAnimation animation = new TranslateAnimation(0, -listRL.getMeasuredWidth(), 0, 0);

					animation.setDuration(animationDuration);
					animation.setFillEnabled(true);
					animation.setAnimationListener(new AnimationListener() {

						@Override
						public void onAnimationStart(Animation animation) {}

						@Override
						public void onAnimationRepeat(Animation animation) {}

						@Override
						public void onAnimationEnd(Animation animation) {

							// At the end, set the final position as the current one
							RelativeLayout.LayoutParams mainContainerLP = (LayoutParams) mainRL
									.getLayoutParams();
							mainContainerLP.setMargins(0, 0, 0, 0);
							mainRL.setLayoutParams(mainContainerLP);

							showingMenu = false;

							if (connected) {
								switch (clickedElementPosition) {
								case 0:

									AlertDialog.Builder bDialog = new AlertDialog.Builder(
											context);
									bDialog.setMessage(getResources()
											.getString(R.string.leave));
									bDialog.setCancelable(true);
									bDialog.setPositiveButton(
											getResources().getString(
													R.string.yes),
													new DialogInterface.OnClickListener() {
												public void onClick(
														DialogInterface iDialog,
														int id) {
													iDialog.dismiss();

													/*
													 * The progress dialog is
													 * not created by the
													 * AsyncTask because the
													 * peer just sends a message
													 * to the bootstrap. This
													 * takes very few time and
													 * the dialog would
													 * disappear almost
													 * immediately. By showing
													 * it before executing the
													 * AsyncTask, it can stay on
													 * the screen until the
													 * bootstrap informs the
													 * peer that it is part of
													 * the network. Thus, the
													 * dialog is removed from
													 * the function listening
													 * for received messages
													 * onReceivedMessage
													 * (String).
													 */
													dialog = new ProgressDialog(NAM4JAndroidActivity.this);
													dialog.setMessage(NAM4JAndroidActivity.this.getString(R.string.pwLeaving));
													dialog.show();

													new LeaveNetwork(null).execute();

													connected = false;
												}
											});
									bDialog.setNegativeButton(
										"No",
										new DialogInterface.OnClickListener() {

											@Override
											public void onClick(DialogInterface dialog,	int id) {
												dialog.dismiss();
											}
										});

									bDialog.show();
									break;

								case 1:
									if (currentLocation != null) {

										// Specifying that the GeoCoder class
										// must call the function to start
										// publishing by setting the boolean to
										// true
										requestPendingForBuilding = true;
										new ReverseGeocodingTask(getBaseContext()).execute(currentLocation);
									} else {
										Bundle args = new Bundle();
										args.putParcelable("CurrentLocation", null);
										Intent i = new Intent(context, BuildingPublishActivity.class);
										i.putExtra("Address", "");
										i.putExtra("Bundle", args);
										startActivity(i);
									}
									break;

								case 2:
									if (currentLocation != null) {

										// Specifying that the GeoCoder class must call the function to start
										// publishing by setting the boolean to true
										requestPendingForSensor = true;
										new ReverseGeocodingTask(getBaseContext()).execute(currentLocation);
									} else {
										Bundle args = new Bundle();
										args.putParcelable("CurrentLocation", null);
										Intent i = new Intent(context, SensorPublishActivity.class);
										i.putExtra("Address", "");
										i.putExtra("Bundle", args);
										startActivity(i);
									}
									break;

								case 3:
									AlertDialog.Builder builder = new AlertDialog.Builder(context);
									builder.setMessage(
											context.getResources()
											.getString(
													R.string.exitOnBackButtonPressed))
													.setCancelable(false)
													.setPositiveButton(
															getResources().getString(
																	R.string.yes),
																	new DialogInterface.OnClickListener() {
																public void onClick(
																		DialogInterface iDialog,
																		int id) {

																	stopService(new Intent(context, DeviceMonitorService.class));

																	// Unregistering the broadcast receivers
																	//unregisterReceiver(bReceiver);
																	unregisterReceiver(mConnReceiver);
																	unregisterReceiver(wifiReceiver);

																	if (connected) {
																		close = true;
																		dialog = new ProgressDialog(NAM4JAndroidActivity.this);
																		dialog.setMessage(NAM4JAndroidActivity.this.getString(R.string.pwLeaving));
																		dialog.show();
																		new LeaveNetwork(null).execute();
																		connected = false;
																	} else {
																		System.runFinalizersOnExit(true);
																		System.exit(0);
																	}
																}
															})
															.setNegativeButton(
																	getResources().getString(
																			R.string.no),
																			new DialogInterface.OnClickListener() {
																		public void onClick(
																				DialogInterface dialog,
																				int id) {
																			dialog.cancel();
																		}
																	});

									AlertDialog alert = builder.create();
									alert.show();
									break;
								}
							} else {
								switch (clickedElementPosition) {
									case 0:
										connect();
										Log.d(NAM4JAndroidActivity.TAG, context.getString(R.string.menu_connect));
										break;
	
									case 1:
										showSettings();
										Log.d(NAM4JAndroidActivity.TAG, context.getString(R.string.menu_settings));
										break;
	
									case 2:
										AlertDialog.Builder builder = new AlertDialog.Builder(
												context);
										builder.setMessage(
												context.getResources()
												.getString(
														R.string.exitOnBackButtonPressed))
														.setCancelable(false)
														.setPositiveButton(
																getResources().getString(
																		R.string.yes),
																		new DialogInterface.OnClickListener() {
																	public void onClick(
																			DialogInterface iDialog,
																			int id) {
	
																		stopService(new Intent(
																				context,
																				DeviceMonitorService.class));
	
																		// Unregistering the broadcast receivers
																		//unregisterReceiver(bReceiver);
																		unregisterReceiver(mConnReceiver);
																		unregisterReceiver(wifiReceiver);
	
																		if (connected) {
	
																			close = true;
	
																			dialog = new ProgressDialog(
																					NAM4JAndroidActivity.this);
																			dialog.setMessage(NAM4JAndroidActivity.this
																					.getString(R.string.pwLeaving));
																			dialog.show();
	
																			new LeaveNetwork(
																					null)
																			.execute();
	
																			connected = false;
	
																		} else {
																			System.runFinalizersOnExit(true);
	
																			System.exit(0);
																		}
																	}
																})
																.setNegativeButton(
																		getResources().getString(
																				R.string.no),
																				new DialogInterface.OnClickListener() {
																			public void onClick(
																					DialogInterface dialog,
																					int id) {
																				dialog.cancel();
																			}
																		});
	
										AlertDialog alert = builder.create();
										alert.show();
										break;
										
									default: break;
								}
							}
						}
					});

					mainRL.startAnimation(animation);
				}

				else {
					switch (clickedElementPosition) {
						case 0:
							connect();
							Log.d(NAM4JAndroidActivity.TAG,	context.getString(R.string.menu_connect));
							break;
	
						case 1:
							showSettings();
							Log.d(NAM4JAndroidActivity.TAG,	context.getString(R.string.menu_settings));
							break;
							
						case 2:
							AlertDialog.Builder builder = new AlertDialog.Builder(context);
							builder.setMessage(
									context.getResources().getString(
											R.string.exitOnBackButtonPressed))
											.setCancelable(false)
											.setPositiveButton(
													getResources().getString(R.string.yes),
													new DialogInterface.OnClickListener() {
														public void onClick(DialogInterface iDialog, int id) {
															stopService(new Intent(context, DeviceMonitorService.class));
	
															// Unregistering the broadcast receivers
															// unregisterReceiver(bReceiver);
															unregisterReceiver(mConnReceiver);
															unregisterReceiver(wifiReceiver);
	
															if (connected) {
																close = true;
																dialog = new ProgressDialog(NAM4JAndroidActivity.this);
																dialog.setMessage(NAM4JAndroidActivity.this.getString(R.string.pwLeaving));
																dialog.show();
																new LeaveNetwork(null).execute();
																connected = false;
															} else {
																System.runFinalizersOnExit(true);
																System.exit(0);
															}
														}
													})
													.setNegativeButton(
															getResources().getString(R.string.no),
															new DialogInterface.OnClickListener() {
																public void onClick(
																		DialogInterface dialog,
																		int id) {
																	dialog.cancel();
																}
															});
	
							AlertDialog alert = builder.create();
							alert.show();
							break;
							
						default: break;
					}
				}
			}
		}
	}

	public class SwipeAndClickListener implements OnTouchListener {

		static final String logTag = "ActivitySwipeDetector";

		int sourceId;

		// To distinguish between click and swipe, set the max duration for a
		// click in milliseconds
		private static final int MAX_CLICK_DURATION = 200;

		// Instant the user clicks
		private long startClickTime;

		boolean isAnimating = false;

		// Change sensitivity based on screen resolution
		static final int MIN_DISTANCE = 50;

		private float downX, downY, upX, upY;

		public SwipeAndClickListener() {}

		public void onRightToLeftSwipe() {

			RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mainRL.getLayoutParams();

			TranslateAnimation animation = null;

			final RelativeLayout mainRL = (RelativeLayout) findViewById(R.id.rlContainer);

			if (layoutParams.leftMargin >= listRL.getMeasuredWidth() / 2) {
				animation = new TranslateAnimation(0, listRL.getMeasuredWidth() - layoutParams.leftMargin, 0, 0);
			} else {
				animation = new TranslateAnimation(0, -layoutParams.leftMargin, 0, 0);
			}

			animation.setDuration(animationDuration);
			animation.setFillEnabled(true);
			animation.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationStart(Animation animation) {}

				@Override
				public void onAnimationRepeat(Animation animation) {}

				@Override
				public void onAnimationEnd(Animation animation) {

					// At the end, set the final position as the current one
					RelativeLayout.LayoutParams mainContainerLP = (LayoutParams) mainRL.getLayoutParams();

					if (mainContainerLP.leftMargin >= listRL.getMeasuredWidth() / 2) {
						mainContainerLP.setMargins(listRL.getMeasuredWidth(), 0, -listRL.getMeasuredWidth(), 0);
						showingMenu = true;
					} else {
						mainContainerLP.setMargins(0, 0, 0, 0);
						showingMenu = false;
					}

					mainRL.setLayoutParams(mainContainerLP);
					isAnimating = false;
				}
			});

			mainRL.startAnimation(animation);
		}

		public void onLeftToRightSwipe() {
			RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mainRL.getLayoutParams();

			TranslateAnimation animation = null;

			if (layoutParams.leftMargin >= listRL.getMeasuredWidth() / 2) {
				animation = new TranslateAnimation(0, listRL.getMeasuredWidth() - layoutParams.leftMargin, 0, 0);
			} else {
				animation = new TranslateAnimation(0, -layoutParams.leftMargin, 0, 0);
			}

			animation.setDuration(animationDuration);
			animation.setFillEnabled(true);
			animation.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationStart(Animation animation) {}

				@Override
				public void onAnimationRepeat(Animation animation) {}

				@Override
				public void onAnimationEnd(Animation animation) {

					// At the end, set the final position as the current one
					RelativeLayout.LayoutParams mainContainerLP = (LayoutParams) mainRL.getLayoutParams();

					if (mainContainerLP.leftMargin >= listRL.getMeasuredWidth() / 2) {
						mainContainerLP.setMargins(listRL.getMeasuredWidth(), 0, -listRL.getMeasuredWidth(), 0);
						showingMenu = true;
					} else {
						mainContainerLP.setMargins(0, 0, 0, 0);
						showingMenu = false;
					}

					mainRL.setLayoutParams(mainContainerLP);
					isAnimating = false;
				}
			});

			mainRL.startAnimation(animation);
		}

		public void onTopToBottomSwipe() {}

		public void onBottomToTopSwipe() {}

		public boolean onTouch(View v, MotionEvent event) {

			sourceId = v.getId();

			switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN: {
	
					startClickTime = Calendar.getInstance().getTimeInMillis();
	
					downX = event.getX();
					downY = event.getY();
					return true;
				}
				case MotionEvent.ACTION_UP: {
	
					long clickDuration = Calendar.getInstance().getTimeInMillis()
							- startClickTime;
	
					if (clickDuration < MAX_CLICK_DURATION) {
	
						// A click event has occurred
	
						// Manage click event just if the button was clicked. If
						// the user pressed the bar, ignore it
						if (sourceId == menuButton.getId()) {
							displaySideMenu();
						}
	
					} else {
						// A swipe event has occurred
	
						upX = event.getX();
						upY = event.getY();
	
						float deltaX = downX - upX;
						float deltaY = downY - upY;
	
						if (!isAnimating) {
							if (!showingMenu) {
								RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mainRL.getLayoutParams();
								if (layoutParams.leftMargin > 0) {
									isAnimating = true;
									onRightToLeftSwipe();
								}
							} else {
								RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mainRL.getLayoutParams();
								if (layoutParams.leftMargin < listRL.getMeasuredWidth()) {
									isAnimating = true;
									onLeftToRightSwipe();
								}
							}
						}
	
						// swipe horizontal?
						if (Math.abs(deltaX) > MIN_DISTANCE) {
	
							// left or right
							if (deltaX < 0) {
								// this.onLeftToRightSwipe();
								return true;
							}
	
							if (deltaX > 0) {
								// this.onRightToLeftSwipe();
								return true;
							}
						} else {}
	
						// swipe vertical?
						if (Math.abs(deltaY) > MIN_DISTANCE) {
							// top or down
							if (deltaY < 0) {
								this.onTopToBottomSwipe();
								return true;
							}
							if (deltaY > 0) {
								this.onBottomToTopSwipe();
								return true;
							}
						} else {}
	
						// no horizontal nor vertical swipe
						return false;
					}
				}
				case MotionEvent.ACTION_MOVE: {
					upX = event.getX();
					float deltaX = downX - upX;
	
					RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mainRL.getLayoutParams();
	
					if (!showingMenu) {
						if (!isAnimating && ((layoutParams.leftMargin - deltaX >= 0) && (layoutParams.leftMargin - deltaX <= listRL.getMeasuredWidth()))) {
							layoutParams.leftMargin -= deltaX;
							layoutParams.rightMargin += deltaX;
							mainRL.setLayoutParams(layoutParams);
						}
					} else {
						if (!isAnimating && ((layoutParams.leftMargin - deltaX <= listRL.getMeasuredWidth()) && (layoutParams.leftMargin - deltaX >= 0))) {
							layoutParams.leftMargin -= deltaX;
							layoutParams.rightMargin += deltaX;
							mainRL.setLayoutParams(layoutParams);
						}
					}
					return false;
				}
			}
			return false;
		}
	}
}
