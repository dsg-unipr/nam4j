package it.unipr.ce.dsg.gamidroid.utils;

import java.math.BigDecimal;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.util.Log;

/**
 * Classe per la gestione della rete internet e del gps.
 * 
 * @author Francesca Bassi, Laura Belli
 * 
 */
public class GpsAndNetworkManager {

	private LocationManager locationManager;
	private Location userLocation;

	private static final String TAG = "GpsAndNetworkManager";

	private Context context;

	public GpsAndNetworkManager(Context context) {
		this.context = context;
	}

	public Location getUserLocation() {
		return userLocation;
	}

	public void setUserLocation(Location userLocation) {
		this.userLocation = userLocation;
	}

	public LocationManager getLocationManager() {
		return locationManager;
	}

	public void setLocationManager(LocationManager locationManager) {
		this.locationManager = locationManager;
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	/**
	 * Metodo per avviare il rilevamento della posizione dell'utente sia da rete
	 * internet, sia da gps.
	 * 
	 * @param l
	 *            location listener
	 */
	public void startLocalizationService(LocationListener l) {
		if (locationManager == null) {
			Log.d(TAG, "Initiliazing LocationManager ...");
			locationManager = (LocationManager) context
					.getSystemService(Context.LOCATION_SERVICE);
		}

		Log
				.d(TAG,
						"Requesting Location Updates (NETWORK_PROVIDER,GPS_PROVIDER)...");

		locationManager.requestLocationUpdates(
				LocationManager.NETWORK_PROVIDER, 0, 0, l);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
				0, l);

	}

	/**
	 * Metodo per stopppare il listener del location manager.
	 * 
	 * @param l
	 *            location listener
	 */
	public void stopLocalizationService(LocationListener l) {
		if (locationManager != null) {
			Log.d(TAG, "Stopping LocationManager ...");
			locationManager.removeUpdates(l);

		}
	}

	/**
	 * Metodo che inizializza la locazione dell'utene in base al provider attivo
	 * (internet o gps)
	 */
	public void initUserLocation() {

		if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			this.userLocation = new Location(LocationManager.GPS_PROVIDER);
		} else if (locationManager
				.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
			this.userLocation = new Location(LocationManager.NETWORK_PROVIDER);
		} else
			this.userLocation = new Location(LocationManager.NETWORK_PROVIDER);

	}

	/**
	 * Metodo che inizializza la locazione custom scelta dall'utente.
	 */
	public void initUserLocationFromPick() {

		if (locationManager == null) {
			Log.d(TAG, "Initiliazing LocationManager ...");
			locationManager = (LocationManager) context
					.getSystemService(Context.LOCATION_SERVICE);
		}

		if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			this.userLocation = new Location(LocationManager.GPS_PROVIDER);
		} else if (locationManager
				.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
			this.userLocation = new Location(LocationManager.NETWORK_PROVIDER);
		} else
			this.userLocation = new Location(LocationManager.NETWORK_PROVIDER);

	}

	/**
	 * Metodo che arrotonda a 2 cifre decimali la locazione
	 * 
	 * @param location
	 *            latitudine e longitudine
	 * @return location arrotondata
	 */
	public static Location roundLocation(Location location) {

		BigDecimal bgLat = new BigDecimal(location.getLatitude());
		bgLat = bgLat.setScale(2, BigDecimal.ROUND_HALF_UP);
		double newLat = bgLat.doubleValue();

		BigDecimal bgLong = new BigDecimal(location.getLongitude());
		bgLong = bgLong.setScale(2, BigDecimal.ROUND_HALF_UP);
		double newLong = bgLong.doubleValue();

		Location roundedL = new Location(LocationManager.NETWORK_PROVIDER);
		roundedL.setLatitude(newLat);
		roundedL.setLongitude(newLong);

		return roundedL;

	}

	/**
	 * Metodo che controllo se la nuova location rilevata dai sensori e' cambiata
	 * in modo relativamente sostanziale rispetto a quella dell'utente.
	 * 
	 * @param location
	 * @return true se la location corrente e' sostanzialmente diversa oppure
	 *         false in caso contrario.
	 */
	public boolean filterLocationChanges(Location location) {

		Location roundedLocation = roundLocation(location);

		if (roundedLocation.getLatitude() != userLocation.getLatitude()
				|| roundedLocation.getLongitude() != userLocation
						.getLongitude()) {
			userLocation = new Location(roundedLocation);
			Log.v(TAG, "Location changed: " + userLocation.getLatitude() + " "
					+ userLocation.getLongitude());
			return true;
		} else
			return false;
	}

}
