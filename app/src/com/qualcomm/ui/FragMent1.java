package com.qualcomm.ui;

import java.util.List;

import qcom.hackathon.collab.download.WiFiDConnectionManager;
import qcom.hackathon.collab.download.WiFiP2pService;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class FragMent1 extends ListFragment{

	private String arry[] = { "default1", "default2", "default3", "default4"};
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ListView listView = new ListView(getActivity());
		ArrayAdapter<String> array = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_1);
		Bundle myBundle=getArguments();
		this.arry = myBundle.getStringArray("list");
		for (String str : arry)
			array.add(str);
		listView.setAdapter(array);
		listView.setClickable(true);
		return listView;
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int pos, long id) {
	  super.onListItemClick(l, v, pos, id);
	  Toast.makeText(getActivity(), (String)l.getItemAtPosition(pos)+ " was clicked", Toast.LENGTH_SHORT).show();
      Activity mAct = getActivity();
      ((MainActivity)mAct).wel2peer();
	}	
}

