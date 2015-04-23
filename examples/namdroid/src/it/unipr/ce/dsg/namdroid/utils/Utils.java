package it.unipr.ce.dsg.namdroid.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.view.Window;

public class Utils {

	public enum Orientation {
		LANDSCAPE, PORTRAIT
	};
	
	public enum SupportedFonts {
		HELVETICA_ULTRALIGHT, HELVETICA_THIN;
		
		public int toInt() {
			switch (this) {
				case HELVETICA_ULTRALIGHT : return 0;
				case  HELVETICA_THIN : return 1;
				default: throw new IllegalArgumentException();
			}
		}
	};
	
	private static String[] supportedFonts = {
			"fonts/helvetica_neue_ultralight.otf",	// Helvetica Ultralight
			"fonts/helvetica_neue_thin.otf"			// Helvetica Thin
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
	 * Method to get the custom font for text views.
	 * 
	 * @param mContext
	 *            The context of the application.
	 * 
	 * @return the TypeFace representing the custom font for the text views
	 */
	public static Typeface getCustomFont(Context mContext, SupportedFonts font) {
		return Typeface.createFromAsset(mContext.getAssets(), supportedFonts[font.toInt()]);
	}

}
