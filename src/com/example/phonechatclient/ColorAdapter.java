package com.example.phonechatclient;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;

public class ColorAdapter extends ArrayAdapter<String> {
	
	private LayoutInflater inflate;
	private Context ca;
	private LinearLayout ln;
	private String[] colors;
	private String[] couleurs;

	public ColorAdapter(Context context, int resource,String[] object,String[] objectFr) {
		super(context, resource, objectFr);
		this.ca = context;
		this.inflate = LayoutInflater.from(context);
		this.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		this.colors = object;
		this.couleurs = objectFr;
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = inflate.inflate(R.layout.colorlist,parent,false);
		ln = (LinearLayout) v.findViewById(R.id.ligne);
		ln.setBackgroundColor(Color.parseColor(colors[position]));
		
		return v;
	}

}
