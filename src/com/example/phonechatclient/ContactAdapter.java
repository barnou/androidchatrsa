package com.example.phonechatclient;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import common.*;

public class ContactAdapter extends ArrayAdapter<Contact> {

	private LayoutInflater inflate;
	private TextView text;
	private CheckBox cb;
	private Context ca;

	public ContactAdapter(Context context, int resource, ArrayList<Contact> object) {
		super(context, resource, object);
		inflate = LayoutInflater.from(context);
		this.ca = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = inflate.inflate(R.layout.drawer_list_item, parent,false);
		final Contact c = getItem(position);
		text = (TextView)v.findViewById(R.id.nomClient);
		text.setText(c.getNom());
		cb = (CheckBox)v.findViewById(R.id.caseCoche);
		cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked)
					((ConnexionActivity) ca).sendMessage(new Message(Message.ACTIVER_CLIENT,c.getId()));
				else
					((ConnexionActivity) ca).sendMessage(new Message(Message.DESACTIVER_CLIENT,c.getId()));
			}
		});
		return v;
	}

}
