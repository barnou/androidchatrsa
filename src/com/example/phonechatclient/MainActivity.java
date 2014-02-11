package com.example.phonechatclient;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Spinner;


public class MainActivity extends Activity {

	private String nom;
	private int port;
	private String adresse;
	private Button connexion;
	private static String[] choix = {"128","256","512","1024","2048"};
	private Spinner liste;
	private String longueurClef = choix[0];
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		connexion = (Button)findViewById(R.id.connexion);
		connexion.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent connexion = new Intent(MainActivity.this,ConnexionActivity.class);
				connexion.putExtra("port", String.valueOf(port));
				connexion.putExtra("nom", nom);
				connexion.putExtra("adresse", adresse);
				connexion.putExtra("longueurClef",longueurClef);
				MainActivity.this.startActivity(connexion);
			}
		});

		liste = (Spinner)findViewById(R.id.selectClef);
		liste.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int pos, long arg3) {
				longueurClef = choix[pos];
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});
		ArrayAdapter<String> aa = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item,
				choix);
		aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		liste.setAdapter(aa);

		final EditText editNom = (EditText) findViewById(R.id.editNom);
		nom = editNom.getText().toString();

		final EditText editPort = (EditText)findViewById(R.id.editPort);
		port = Integer.parseInt(editPort.getText().toString());

		final EditText editAdresse = (EditText)findViewById(R.id.editAdresse);
		adresse = editAdresse.getText().toString();

		editNom.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				if(editAdresse.getText().toString().length()!=0&&editPort.getText().toString().length()!=0&&editNom.getText().toString().length()!=0)
				{
					adresse = editAdresse.getText().toString();
					port = Integer.parseInt(editPort.getText().toString());
					nom = editNom.getText().toString();					
					connexion.setEnabled(true);
				}
				else
				{
					connexion.setEnabled(false);
				}
			}
		});
		editPort.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				if(editAdresse.getText().toString().length()!=0&&editPort.getText().toString().length()!=0&&editNom.getText().toString().length()!=0)
				{
					adresse = editAdresse.getText().toString();
					port = Integer.parseInt(editPort.getText().toString());
					nom = editNom.getText().toString();					
					connexion.setEnabled(true);
				}
				else
				{
					connexion.setEnabled(false);
				}				
			}
		});
		editAdresse.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				if(editAdresse.getText().toString().length()!=0&&editPort.getText().toString().length()!=0&&editNom.getText().toString().length()!=0)
				{
					adresse = editAdresse.getText().toString();
					port = Integer.parseInt(editPort.getText().toString());
					nom = editNom.getText().toString();
					connexion.setEnabled(true);
				}
				else
				{
					connexion.setEnabled(false);
				}				
			}
		});
	}


	/*public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}*/

}
