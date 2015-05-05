package it.unipr.ce.dsg.namdroid.activity;

import it.unipr.ce.dsg.namdroid.R;
import it.unipr.ce.dsg.namdroid.fragment.FirstPageFragment;
import it.unipr.ce.dsg.namdroid.fragment.SecondPageFragment;
import it.unipr.ce.dsg.namdroid.utils.Utils;
import it.unipr.ce.dsg.namdroid.utils.Utils.SupportedFonts;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle toggle;
	private FirstPageFragment firstPageFragment = null;
	private SecondPageFragment secondPageFragment = null;
	private int currentFragmentIndex = 0;
	private Context mContext;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
        	firstPageFragment  = new FirstPageFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.container, firstPageFragment).commit();
        }
        mContext = this;
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
		int id = item.getItemId();
		
		if (id == R.id.action_info) {
			SharedPreferences sharedPreferences = getSharedPreferences(Utils.PREFERENCES, Context.MODE_PRIVATE);
			final Dialog dialog = new Dialog(mContext);
            dialog.setContentView(R.layout.custom_dialog);
            dialog.setTitle("Contact address");
            TextView dialogTV = (TextView) dialog.findViewById(R.id.dialogTV);
            dialogTV.setText(sharedPreferences.getString(Utils.PEER_DESCRIPTOR, Utils.PD_NOT_AVAILABLE));
            Button dialogButton = (Button) dialog.findViewById(R.id.dialogButton);
            dialogButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.cancel();
                }
            });
            dialog.show();
            
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
}
