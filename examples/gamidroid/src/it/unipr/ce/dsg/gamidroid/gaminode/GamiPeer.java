package it.unipr.ce.dsg.gamidroid.gaminode;

import android.util.Log;
import it.unipr.ce.dsg.gamidroid.activities.NAM4JAndroidActivity;
import it.unipr.ce.dsg.s2p.peer.Peer;
import it.unipr.ce.dsg.s2p.sip.Address;
import it.unipr.ce.dsg.s2pchord.PeerConfig;

public class GamiPeer extends Peer {

	private String bootstrap = null;

	public GamiPeer(String pathConfig, String key, String peerName,
			int peerPort) {
		super(pathConfig, key, peerName, peerPort);
		this.nodeConfig.keepalive_time=0;
		Log.d(NAM4JAndroidActivity.TAG, "AndroidPeer created !");
	}

	@Override
	protected void onDeliveryMsgFailure(String arg0, Address arg1, String arg2) {
		Log.d(NAM4JAndroidActivity.TAG, "AndroidPeer onDeliveryMsgFailure !");	
	}

	@Override
	protected void onDeliveryMsgSuccess(String arg0, Address arg1, String arg2) {
		Log.d(NAM4JAndroidActivity.TAG, "AndroidPeer onDeliveryMsgSuccess !");

	}
	
	public void setPeerConfig(PeerConfig conf)
	{
		Log.d(NAM4JAndroidActivity.TAG, "AndroidPeer setPeerConfig !");
		this.bootstrap  = conf.bootstrap_peer;
	}

	public String getBootstrap() {
		return bootstrap;
	}

	public void setBootstrap(String bootstrap) {
		this.bootstrap = bootstrap;
	}
	
}
