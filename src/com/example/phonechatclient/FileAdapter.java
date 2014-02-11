package com.example.phonechatclient;

import java.io.File;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class FileAdapter extends ArrayAdapter<File>{

	private LayoutInflater inflate;
	private ImageView image;
	private TextView path;
	
	public FileAdapter(Context context, int textViewResourceId,
			File[] objects) {
		super(context, textViewResourceId, objects);
		inflate = LayoutInflater.from(context);
	}
	
	public View  getView(int position, View convertView, ViewGroup parent)
	{
		View v = inflate.inflate(R.layout.lignedownload, parent,false);
		File f = getItem(position);
		image = (ImageView)v.findViewById(R.id.image);
		path = (TextView)v.findViewById(R.id.path);
		if(f.isDirectory())
		{
			image.setImageResource(R.drawable.directory_icon);
		}
		else
		{
			image.setImageResource(R.drawable.blank_file_icon);
		}
		path.setText(f.getName());
		return v;
		
	}

}
