package com.example.app_4;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MessageButton extends View {

	public MessageButton(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v;

        v = inflater.inflate(R.layout.fab_layout, parent, false);
        return v;
        
	}

}
