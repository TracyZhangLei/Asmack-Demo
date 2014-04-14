package com.example.smackdemo.adapter;

import java.util.List;
import java.util.Map;

import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;


/***
 * rosterentry list adapter
 * 
 * @author tracyZhang
 *
 */

public class MyFriendExpadableAdapter extends BaseExpandableListAdapter {
	
	private List<RosterGroup> headers;
	private Map<RosterGroup,List<RosterEntry>> childs;
	private Context cxt;

	public MyFriendExpadableAdapter( Context cxt,List<RosterGroup> headers,
			Map<RosterGroup, List<RosterEntry>> childs) {
		super();
		this.headers = headers;
		this.childs = childs;
		this.cxt = cxt;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return childs.get(headers.get(groupPosition)).get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		RosterEntry childEntry = (RosterEntry) getChild(groupPosition, childPosition);
		if(null == convertView){
			convertView = LayoutInflater.from(cxt).inflate(android.R.layout.simple_expandable_list_item_1, null);
		}
		TextView child = (TextView) convertView.findViewById(android.R.id.text1);
		child.setText(childEntry.getName());
		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		// TODO Auto-generated method stub
		return childs.get(headers.get(groupPosition)).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		// TODO Auto-generated method stub
		return headers.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		// TODO Auto-generated method stub
		return headers.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		// TODO Auto-generated method stub
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		RosterGroup group = (RosterGroup) getGroup(groupPosition);
		if(null == convertView){
			convertView = LayoutInflater.from(cxt).inflate(android.R.layout.simple_expandable_list_item_1, null);
		}
		TextView header = (TextView) convertView.findViewById(android.R.id.text1);
		header.setText(group.getName());
		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return true;
	}

}
