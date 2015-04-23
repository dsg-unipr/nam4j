package it.unipr.ce.dsg.gamidroid.utils;

import it.unipr.ce.dsg.gamidroid.R;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Settings menu list adapter.
 */
public class MenuListAdapter extends ArrayAdapter<MenuListElement> {
	
	public MenuListAdapter(Context context, int textViewResourceId) {
	    super(context, textViewResourceId);
	}

	public MenuListAdapter(Context context, int resource, List<MenuListElement> items) {
	    super(context, resource, items);
	}

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	
    	View v = convertView;

        if (v == null) {

            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.menu_list_view_row, null);

        }

        MenuListElement p = getItem(position);

        if (p != null) {

            TextView tt = (TextView) v.findViewById(R.id.TextViewTitleList);
            TextView tt1 = (TextView) v.findViewById(R.id.TextViewSubtitleList);
            
            if (tt != null) {
                tt.setText(p.getTitle());
            }
            if (tt1 != null) {
                tt1.setText(p.getSubTitle());
            }
        }

        return v;
    }
    
}
