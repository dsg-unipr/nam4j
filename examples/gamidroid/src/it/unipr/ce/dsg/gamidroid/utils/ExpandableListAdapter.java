package it.unipr.ce.dsg.gamidroid.utils;

import it.unipr.ce.dsg.gamidroid.R;
import it.unipr.ce.dsg.gamidroid.interfaces.INetworkChosen;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

	private Context mContext;
	private List<String> listDataHeader;
	private HashMap<String, List<String>> listDataChild;
	private static INetworkChosen mCallback;

	public ExpandableListAdapter(Context context, List<String> listDataHeader,
			HashMap<String, List<String>> listChildData) {
		this.mContext = context;
		this.listDataHeader = listDataHeader;
		this.listDataChild = listChildData;
		mCallback = (INetworkChosen) context;
	}

	@Override
	public Object getChild(int groupPosition, int childPosititon) {
		return this.listDataChild.get(this.listDataHeader.get(groupPosition))
				.get(childPosititon);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public View getChildView(final int groupPosition, final int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {

		final String childText = (String) getChild(groupPosition, childPosition);

		if (convertView == null) {
			LayoutInflater infalInflater = (LayoutInflater) this.mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = infalInflater.inflate(R.layout.list_item, null);
		}

		TextView txtListChild = (TextView) convertView
				.findViewById(R.id.lblListItem);

		txtListChild.setText(childText);

		convertView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mCallback.networkChosen(childText, groupPosition);
			}
		});

		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return this.listDataChild.get(this.listDataHeader.get(groupPosition)).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return this.listDataHeader.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return this.listDataHeader.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {

		if (convertView == null) {
			LayoutInflater infalInflater = (LayoutInflater) this.mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = infalInflater.inflate(R.layout.list_group, null);
		}

		String group = (String) getGroup(groupPosition);

		RelativeLayout rl = (RelativeLayout) convertView;

		String[] expandableListTitle = group.split("_");

		((TextView) rl.getChildAt(0)).setText(expandableListTitle[0]);

		SharedPreferences sharedPreferences = mContext.getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE);
		String currentNetwork = sharedPreferences.getString(Constants.NETWORK, "");
		
		if (expandableListTitle.length > 1) {
			((TextView) rl.getChildAt(1)).setText(currentNetwork);
		}

		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}
}
