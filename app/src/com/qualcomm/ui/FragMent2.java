package com.qualcomm.ui;

import qcom.hackathon.collab.download.R;
import android.app.ActionBar.LayoutParams;
import android.app.Fragment;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class FragMent2 extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
	     View rootView = inflater.inflate(R.layout.list_button, container, false);
	     LinearLayout linearLayout = (LinearLayout) rootView.findViewById(R.id.list_button_linearlayout);	     
	     
	     ListView listView = new ListView(getActivity());
	     ArrayAdapter<String> array = new ArrayAdapter<String>(getActivity(),
	    		 android.R.layout.simple_list_item_1);
	     Bundle myBundle=getArguments();
	     String[] arry = myBundle.getStringArray("list");
	     for (String str : arry)
	    	 array.add(str);
	     listView.setAdapter(array);
	     //add list in 
	     linearLayout.addView(listView);
	     
	     //add text 
	     TextView dataAmount= new TextView(getActivity());
	     dataAmount.setText("DATA NEEDED: " + 100/(arry.length+1) + "MB");
	     android.widget.LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
	     lp.gravity = Gravity.CENTER;
	     linearLayout.addView(dataAmount, lp);
	     
	     //add button
	     Button downloadButton = new Button(getActivity());
	     downloadButton.setText("START DOWNLOAD");
	     linearLayout.addView(downloadButton, lp);
         return rootView;		
	}

}

