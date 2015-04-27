package it.unipr.ce.dsg.namdroid.fragment;

import it.unipr.ce.dsg.nam4j.impl.FunctionalModule;
import it.unipr.ce.dsg.nam4j.impl.NetworkedAutonomicMachine.Action;
import it.unipr.ce.dsg.nam4j.impl.NetworkedAutonomicMachine.MigrationSubject;
import it.unipr.ce.dsg.nam4j.impl.mobility.utils.MobilityUtils;
import it.unipr.ce.dsg.nam4j.impl.mobility.xmlparser.SAXHandler;
import it.unipr.ce.dsg.nam4j.impl.service.Service;
import it.unipr.ce.dsg.namdroid.R;
import it.unipr.ce.dsg.namdroid.interfaces.IMobilityItemIsAvailableObserver;
import it.unipr.ce.dsg.namdroid.utils.MobilityUtilsAndroid;
import it.unipr.ce.dsg.namdroid.utils.S2PMigrationTestNam;
import it.unipr.ce.dsg.namdroid.utils.Utils;
import it.unipr.ce.dsg.namdroid.utils.Utils.SupportedFonts;

import java.io.File;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class FirstPageFragment extends Fragment implements IMobilityItemIsAvailableObserver {

	private S2PMigrationTestNam nam;
	TextView numberTextView;
	Button requestService;
	Button requestFM;
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.first_page_fragment, container, false);
        numberTextView = (TextView) rootView.findViewById(R.id.infoTextView);
        numberTextView.setTypeface(Utils.getCustomFont(getActivity(), SupportedFonts.HELVETICA_THIN));
        requestService = (Button) rootView.findViewById(R.id.requestServiceButton);
        requestService.setTypeface(Utils.getCustomFont(getActivity(), SupportedFonts.HELVETICA_THIN));
        requestFM = (Button) rootView.findViewById(R.id.requestFMButton);
        requestFM.setTypeface(Utils.getCustomFont(getActivity(), SupportedFonts.HELVETICA_THIN));
        init();
        return rootView;
    }
    
    private void init() {
		this.nam = S2PMigrationTestNam.getInstance(getActivity());
		
		// Register for item availability events
		this.nam.addIMobilityItemIsAvailableObserver(this);
		
		requestService.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				nam.performRequestServiceTest();
			}
		});
		
		requestFM.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				nam.performRequestFMTest();
			}
		});
	}
    
    @Override
	public void onItemIsAvailable(String itemName, String mainClassName, MigrationSubject role, Action action, Object state) {
    	
    	// Adding to class loader
    	Object obj = MobilityUtilsAndroid.addToClassPath(getActivity(), this.nam, itemName, mainClassName, role, action);
    	
    	if(state != null) {
    		System.out.println("--- State management for file " + itemName + " ---");
    		
    		if(role == MigrationSubject.FM) {
    			FunctionalModule tfm = (FunctionalModule) state;
				tfm.getFunctionalModuleRunnable().restoreState();
				tfm.getFunctionalModuleRunnable().resume();
    			
    		} else if(role == MigrationSubject.SERVICE) {
	    		Service s = (Service) state;
				
				// Getting the associated functional module from the
				// service info file. The id of the info file
				// corresponds to the name of the dex file with no
				// extension (replacing ".dex" with "").
				SAXHandler handler = MobilityUtils.parseXMLFile((new File(itemName)).getName().replace(MobilityUtils.ANDROID_FILE_EXTENSION, ""), nam);
				String functionalModuleId = handler.getFunctionalModuleForService().getId();
				
				System.out.println("------ The id of the FM to which the service has to be added is " + functionalModuleId);
				
				SAXHandler infoFmHandler = MobilityUtils.parseXMLFile(functionalModuleId, nam);
				String fmCompleteMainClassName = infoFmHandler.getLibraryInformation().getMainClass();
				
				System.out.println("--------- The main class name for the FM to which the Service is associated is " + fmCompleteMainClassName + " - instantiating it and adding the Service to such a Functional Module...");
				
				// Adding the service to the associated functional module
				FunctionalModule fm = MobilityUtilsAndroid.addServiceToFm(getActivity(), s, functionalModuleId, this.nam, fmCompleteMainClassName);
				s.setFunctionalModule(fm);
	
				// Resuming the service execution
				s.getServiceRunnable().restoreState();
				s.getServiceRunnable().resume();
    		}
    		
    	} else {
	    	
    		if (action == Action.COPY) {
		    	if (mainClassName != null) {
		    		
		    		// The item is not a dependency so the execution starts
		    		
					if (role.equals(MigrationSubject.FM)) {
						FunctionalModule fm = (FunctionalModule) obj;
						fm.getFunctionalModuleRunnable().start();
					}
					else if (role.equals(MigrationSubject.SERVICE)) {
						Service s = (Service) obj;
						
						// Getting the associated functional module from the
						// service info file. The id of the info file
						// corresponds to the name of the dex file with no
						// extension (replacing ".dex" with "").
						SAXHandler handler = MobilityUtils.parseXMLFile((new File(itemName)).getName().replace(MobilityUtils.ANDROID_FILE_EXTENSION, ""), nam);
						String functionalModuleId = handler.getFunctionalModuleForService().getId();
						
						System.out.println("------ The id of the FM to which the service has to be added is " + functionalModuleId);
						
						SAXHandler infoFmHandler = MobilityUtils.parseXMLFile(functionalModuleId, nam);
						String fmCompleteMainClassName = infoFmHandler.getLibraryInformation().getMainClass();
						
						System.out.println("--------- The main class name for the FM to which the Service is associated is " + fmCompleteMainClassName + " - instantiating it and adding the Service to such a Functional Module...");
						
						// Adding the service to the associated functional module
						FunctionalModule fm = MobilityUtilsAndroid.addServiceToFm(getActivity(), s, functionalModuleId, this.nam, fmCompleteMainClassName);
						s.setFunctionalModule(fm);
		
						s.getServiceRunnable().start();
					}
					
		    	} else {
		    		System.out.println(MobilityUtils.DEPENDENCY_ADDED);
		    	}
	    	}
    	}
	}

}
