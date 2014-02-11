package com.example.phonechatclient;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import common.*;

public class ContactAdapter extends ArrayAdapter<Client> {

	private LayoutInflater inflate;
	private TextView text;
	private CheckBox cb;
	private Context ca;
	private int resource;

	public ContactAdapter(Context context, int resource, ArrayList<Client> object) {
		super(context, resource, object);
		this.resource = resource;
		inflate = LayoutInflater.from(context);
		this.ca = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final Client c = getItem(position);
		View v = inflate.inflate(R.layout.drawer_list_item, parent,false);
		text = (TextView)v.findViewById(R.id.nomClient);
		text.setText(c.getName());
		text.setTextColor(Color.rgb(c.getTextColor().getR(), c.getTextColor().getG(), c.getTextColor().getB()));
		cb = (CheckBox)v.findViewById(R.id.caseCoche);
		cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked)
					((ConnexionActivity) ca).sendMessage(new Message(Message.ENABLE_CLIENT,c.getId()));
				else
					((ConnexionActivity) ca).sendMessage(new Message(Message.DISABLE_CLIENT,c.getId()));
			}
		});
		return v;
	}
}
