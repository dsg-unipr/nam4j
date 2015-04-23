package it.unipr.ce.dsg.namdroid.activity;

import it.unipr.ce.dsg.namdroid.R;
import it.unipr.ce.dsg.namdroid.fragment.FirstPageFragment;
import it.unipr.ce.dsg.namdroid.fragment.SecondPageFragment;
import it.unipr.ce.dsg.namdroid.utils.Utils;
import it.unipr.ce.dsg.namdroid.utils.Utils.SupportedFonts;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class MainActivity extends ActionBarActivity {

	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle toggle;
	private FirstPageFragment firstPageFragment = null;
	private SecondPageFragment secondPageFragment = null;
	private int currentFragmentIndex = 0;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
        	firstPageFragment  = new FirstPageFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.container, firstPageFragment).commit();
        }
        Toolbar toolbar = (Toolbar)findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(toolbar);
        mDrawerLayout  = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        toggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(toggle);
        setupDrawerUI();
	}
	
	private void setupDrawerUI(){
        Button buttonOne = (Button)findViewById(R.id.buttonOne);
        buttonOne.setTypeface(Utils.getCustomFont(this, SupportedFonts.HELVETICA_THIN));
        buttonOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.closeDrawers();
                if(currentFragmentIndex != 0) {
                	if(firstPageFragment == null) {
                		firstPageFragment  = new FirstPageFragment();
                	}
                	FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                	// Set animation before replacing fragments or it will be ignored
                	transaction.setCustomAnimations(R.anim.animate_right_in, R.anim.animate_right_out);
                	transaction.replace(R.id.container, firstPageFragment);
                	transaction.commit();
                	currentFragmentIndex = 0;
                }
            }
        });

        Button buttonTwo = (Button)findViewById(R.id.buttonTwo);
        buttonTwo.setTypeface(Utils.getCustomFont(this, SupportedFonts.HELVETICA_THIN));
        buttonTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.closeDrawers();
                if(currentFragmentIndex != 1) {
                	if(secondPageFragment == null) {
                		secondPageFragment  = new SecondPageFragment();
                	}
                	FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                	// Set animation before replacing fragments or it will be ignored
                	transaction.setCustomAnimations(R.anim.animate_left_in, R.anim.animate_left_out);
                	transaction.replace(R.id.container, secondPageFragment);
                	transaction.commit();
                	currentFragmentIndex = 1;
            	}
            }
        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        toggle.syncState();
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		
		//int id = item.getItemId();
		// if (id == R.id.action_settings) {
		//	return true;
		//}
		
		return super.onOptionsItemSelected(item);
	}
}
