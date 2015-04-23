package it.unipr.ce.dsg.namdroid.fragment;

import it.unipr.ce.dsg.namdroid.R;
import it.unipr.ce.dsg.namdroid.utils.Utils;
import it.unipr.ce.dsg.namdroid.utils.Utils.SupportedFonts;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SecondPageFragment extends Fragment {

    // Object to set the custom font for the textViews
	Typeface typeface;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.second_page_fragment, container, false);

        typeface = Utils.getCustomFont(getActivity(), SupportedFonts.HELVETICA_THIN);
        
        TextView numberTextView = (TextView)rootView.findViewById(R.id.dsg_contacts);
        numberTextView.setTypeface(typeface);

        return rootView;
    }
    
}
