package com.example.phonechatclient;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import common.*;

public class ConnexionActivity extends Activity {
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	private boolean crypter = true;
	private int port;
	private String nom;
	private String adresse;
	private Socket socket;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private boolean running = true;
	private EditText message;
	private Button bouton;
	private LinearLayout screen;
	private ScrollView scrollView;
	private String longueurClef;
	private RSA rsa;
	publicKey serverPublicKey;
	private String[] colors = {"black","blue","yellow","green","red","gray"};
	private String[] couleurs = {"noir","bleu","jaune","vert","rouge","gris"};
	private MyColor textColor;
	private Client client;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_connexion);
		Intent connexion = getIntent();
		port = Integer.parseInt(connexion.getStringExtra("port"));
		nom = connexion.getStringExtra("nom");
		adresse = connexion.getStringExtra("adresse");
		longueurClef = connexion.getStringExtra("longueurClef");
		rsa = new RSA(Integer.parseInt(longueurClef));
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);

		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
		getActionBar().setTitle(nom);
		


		message = (EditText) findViewById(R.id.msg);
		bouton = (Button)findViewById(R.id.sendMsg);
		screen = (LinearLayout) findViewById(R.id.screen);
		scrollView = (ScrollView) findViewById(R.id.scrollView1);
		bouton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if(crypter)
				{
					sendMessage(new Message(Message.CRYPTED_MESSAGE, ConnexionActivity.this.encrypt(nom+" dit: "+message.getText().toString())));
				}
				else
				{
					sendMessage(new Message(Message.MESSAGE,message.getText().toString()));
				}
				message.setText("");
			}
		});
		message.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(message.getText().toString().length()!=0)
				{
					bouton.setEnabled(true);
				}
				else
				{
					bouton.setEnabled(false);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				if(message.getText().toString().length()!=0)
				{
					bouton.setEnabled(true);
				}
				else
				{
					bouton.setEnabled(false);
				}

			}

			@Override
			public void afterTextChanged(Editable s) {
				if(message.getText().toString().length()!=0)
				{
					bouton.setEnabled(true);
				}
				else
				{
					bouton.setEnabled(false);
				}

			}
		});


		mDrawerToggle = new ActionBarDrawerToggle(
				this,                  /* host Activity */
				mDrawerLayout,         /* DrawerLayout object */
				R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
				R.string.drawer_open,  /* "open drawer" description for accessibility */
				R.string.drawer_close  /* "close drawer" description for accessibility */
				) {
			public void onDrawerClosed(View view) {
				invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
			}

			public void onDrawerOpened(View drawerView) {
				invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
			}
		};

		mDrawerLayout.setDrawerListener(mDrawerToggle);
		new Thread(new ClientThread()).start();;


	}

	public void onDestroy()
	{
		super.onDestroy();
		running = false;
		new Thread(new DeconnexionThread()).start();
		//new Thread(new DeconnexionThread()).start();
		//socket.close();

	}

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return super.onCreateOptionsMenu(menu);
	}
	/* Called whenever we call invalidateOptionsMenu() */
	public boolean onPrepareOptionsMenu(Menu menu) {
		// If the nav drawer is open, hide action items related to the content view
		return super.onPrepareOptionsMenu(menu);
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		// The action bar home/up action should open or close the drawer.
		// ActionBarDrawerToggle will take care of this.
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle action buttons
		switch(item.getItemId()) {

		case R.id.param:
			AlertDialog.Builder builder = new AlertDialog.Builder(ConnexionActivity.this);
			final View view = ConnexionActivity.this.getLayoutInflater().inflate(R.layout.params, null);

			final CheckBox cb = (CheckBox)view.findViewById(R.id.checkCrypte);
			final Spinner liste = (Spinner) view.findViewById(R.id.colorSelect);
			liste.setAdapter(new ColorAdapter(ConnexionActivity.this, android.R.layout.simple_spinner_item,colors, couleurs));
			cb.setChecked(crypter);
			builder.setTitle("Paramètres");
			builder.setNegativeButton("Annuler", null);
			builder.setPositiveButton("Accepter", new DialogInterface.OnClickListener(){


				@Override
				public void onClick(DialogInterface dialog, int which) {
					crypter = cb.isChecked();					
				}

			});
			builder.setView(view).show();
			break;
		/*case R.id.file:
			Intent download = new Intent(ConnexionActivity.this,DownloadActivity.class);
			download.putExtra("client", client);
			download.putExtra("port", port);
			download.putExtra("adresse", adresse);
			ConnexionActivity.this.startActivity(download);
			break;*/

		}
		return super.onOptionsItemSelected(item);
	}

	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}


	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	class DeconnexionThread implements Runnable{

		@Override
		public void run() {
			sendMessage(new Message(Message.DECONNECTION, ""));
			try {
				if(socket != null)
				{
					socket.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	class ClientThread implements Runnable{


		@Override
		public void run() {

			try{
				socket = new Socket(adresse,port);
			}catch(IOException e)
			{
				ConnexionActivity.this.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						ConnexionActivity.this.finish();
					}
				});
				Log.v("erreur","Impossible de se connecter");
				System.out.println("Impossible de se connecter: "+e);
			}

			if(socket != null)
			{


				try{
					in = new ObjectInputStream(socket.getInputStream());
					out = new ObjectOutputStream(socket.getOutputStream());
				}catch(IOException e)
				{

					System.out.println("Impossible d'initialiser les buffer");
				}

				try
				{
					int R = (int)(Math.random()*256);
					int G = (int)(Math.random()*256);
					int B= (int)(Math.random()*256);
					textColor = new MyColor(R, G, B);
					ConnexionActivity.this.rsa.generateKeys();
					client = new Client(nom,rsa.getPublic_key(),textColor,Client.MOBILE);
					sendMessage(new Message(Message.CONNECTION, client));
				}
				catch (Exception e) {
					System.out.println("Exception doing login : " + e);
				}

				while(running)
				{
					try {
						Message message = (Message) in.readObject();
						final MyColor couleur = message.getColor();
						switch(message.getType()) {
						case Message.CONNECTION:
							serverPublicKey = message.getPublicKey();
							break;
						case Message.CRYPTED_MESSAGE:
							System.out.println("Début mess cripté");
							String[] mess = message.getMsg();
							final String str;
							if(crypter)
							{
								str = ConnexionActivity.this.rsa.decrypt(mess);
							}
							else
							{
								StringBuilder sb = new StringBuilder();
								for (int i = 0; i < mess.length; i++) {
									sb.append(mess[i]);


								}
								str = sb.toString();
							}
							ConnexionActivity.this.runOnUiThread(new Runnable() {

								public void run() {
									TextView text = new TextView(ConnexionActivity.this);
									text.setText(str);
									text.setTextColor(Color.rgb(couleur.getR(), couleur.getG(), couleur.getB()));
									text.setPadding(0, 10, 0, 0);
									text.setEms(10);
									screen.addView(text);
									scrollView.fullScroll(ScrollView.FOCUS_DOWN);
									
								}
							}); // On affiche le message dans la zone d'affichage
							//System.out.println(str);
							//System.out.println("Fin mess cripté");
							break;

						case Message.MESSAGE:
							final String msg = message.getMessage();
							ConnexionActivity.this.runOnUiThread(new Runnable() {

								@Override
								public void run() {
									// TODO Auto-generated method stub
									TextView texte = new TextView(ConnexionActivity.this);
									texte.setText(msg);
									texte.setTextColor(Color.rgb(couleur.getR(), couleur.getG(), couleur.getB()));
									texte.setPadding(0, 10, 0, 0);
									texte.setEms(10);
									screen.addView(texte);
									scrollView.fullScroll(ScrollView.FOCUS_DOWN);
								}
							});
							break;

						case Message.LIST_CLIENTS:
							final ArrayList<Client> liste = message.getListe();
							System.out.println("Il y a " + liste.size() + " clients connecté");
							ConnexionActivity.this.runOnUiThread(new Runnable() {

								@Override
								public void run() {
									mDrawerList.setAdapter(new ContactAdapter(ConnexionActivity.this,
											R.layout.drawer_list_item, liste));
								}
							});

							//fenetre.refreshListeClient(liste);
							break;

						case Message.NEW_CLIENT:
							int idClient = message.getId();
							sendMessage(new Message(Message.ENABLE_CLIENT, idClient));
							break;
						} 
					}
					catch(Exception e) {
						System.out.println("Exception ecoute du serveur: " + e);
						running = false;
						ConnexionActivity.this.finish();
					}
				}

				//out.writeObject(new Message(Message.CONNEXION, nomClient));

			}
			else
			{
				ConnexionActivity.this.runOnUiThread(new Runnable() {

					@Override
					public void run() {

						Toast.makeText(ConnexionActivity.this,"Impossible de contacter le serveur" , Toast.LENGTH_LONG).show();
					}
				});
				ConnexionActivity.this.finish();
			}

		}

	}



	public void sendMessage(Message msg) {
		try {
			out.reset();
			out.writeObject(msg);
			out.flush();
		}
		catch(Exception e) {
			System.out.println("Exception envoie message vers le serveur: " + e);
		}
	}

	public String[] encrypt(String str) {
		return this.rsa.encrypt(str, serverPublicKey);
	}
}