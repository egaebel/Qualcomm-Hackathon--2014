package com.qualcomm.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class FragMent1 extends Fragment {

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
		return listView;
	}
}

