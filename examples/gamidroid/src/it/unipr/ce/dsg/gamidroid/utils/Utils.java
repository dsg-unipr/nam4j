package it.unipr.ce.dsg.gamidroid.utils;

import java.security.MessageDigest;
import java.util.Random;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.Window;

public class Utils {

	public enum Orientation {
		LANDSCAPE, PORTRAIT
	};

	/**
	 * Method to check if the app is running on a smartphone or on a tablet.
	 * 
	 * @param mContext
	 *            The context of the application.
	 * 
	 * @return true if the app is running on a tablet, false otherwise
	 */
	public static boolean isTablet(Context mContext) {
		return (mContext.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
	}

	/**
	 * Method to get the size of the screen.
	 */
	public static int[] getScreenSize(Context mContext, Window win) {
		final DisplayMetrics metrics = mContext.getResources()
				.getDisplayMetrics();

		Rect rect = new Rect();
		win.getDecorView().getWindowVisibleDisplayFrame(rect);
		int statusHeight = rect.top;
		int contentViewTop = win.findViewById(Window.ID_ANDROID_CONTENT)
				.getTop();
		int titleHeight = contentViewTop - statusHeight;

		/* Getting screen size */
		int screenWidth = metrics.widthPixels;
		int screenHeight = metrics.heightPixels - titleHeight - statusHeight;

		return new int[] { screenWidth, screenHeight };
	}

	/**
	 * Method to generate a random string used as a name for peers.
	 * 
	 * @return A pseudo-random string
	 */
	public static String generateRandomString() {
		try {
			Random random = new Random();
			String key = Integer.valueOf(
					(random.nextInt() + 1) * (random.nextInt() + 1)).toString();
			byte[] bytesOfMessage = key.getBytes("UTF-8");
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] thedigest = md.digest(bytesOfMessage);
			// convert the byte to hex format method 1
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < thedigest.length; i++) {
				sb.append(Integer.toString((thedigest[i] & 0xff) + 0x100, 16)
						.substring(1));
			}
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
