package it.unipr.ce.dsg.gamidroid.activities;

import it.unipr.ce.dsg.gamidroid.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

/**
 * <p>
 * This class presents the splash screen to the user.
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

public class SplashScreenActivity extends Activity {

	// Splash screen timeout (milliseconds)
	private static int SPLASH_TIME_OUT = 1000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_splash);

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {

				/* Start main activity upon timer expiration */
				Intent i = new Intent(SplashScreenActivity.this,
						NAM4JAndroidActivity.class);
				startActivity(i);

				finish();
			}
		}, SPLASH_TIME_OUT);
	}

}