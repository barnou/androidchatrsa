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
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
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
	private TextView screen;
	private ScrollView scrollView;
	private String longueurClef;
	private RSA rsa = new RSA();
	publicKey serverPublicKey;
	private String[] colors = {"black","blue","yellow","green","red","gray"};
	private String[] couleurs = {"noir","bleu","jaune","vert","rouge","gris"};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_connexion);
		Intent connexion = getIntent();
		port = Integer.parseInt(connexion.getStringExtra("port"));
		nom = connexion.getStringExtra("nom");
		adresse = connexion.getStringExtra("adresse");
		longueurClef = connexion.getStringExtra("longueurClef");
		Log.v("longueur",longueurClef);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);

		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
		getActionBar().setTitle(nom);


		message = (EditText) findViewById(R.id.msg);
		bouton = (Button)findViewById(R.id.sendMsg);
		screen = (TextView) findViewById(R.id.screen);
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return super.onCreateOptionsMenu(menu);
	}
	/* Called whenever we call invalidateOptionsMenu() */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// If the nav drawer is open, hide action items related to the content view
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
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
		/*case R.id.item1:
			Log.v("checked",String.valueOf(item.isChecked()));
			if(item.isChecked())
			{
				item.setChecked(false);
				crypter = false;
			}
			else
			{
				item.setChecked(true);
				crypter = true;
			}
			break;
		case R.id.item2:
			if(item.isChecked())
			{
				crypterRec = false;
				item.setChecked(false);
			}
			else
			{
				crypterRec = true;
				item.setChecked(true);
			}
			break;*/
		}
		return super.onOptionsItemSelected(item);
	}


	/* The click listner for ListView in the navigation drawer */




	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	class DeconnexionThread implements Runnable{

		@Override
		public void run() {
			sendMessage(new Message(Message.DECONNEXION, ""));
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
				Log.v("erreur","Impossible de se connecter");
				System.out.println("Impossible de se connecter: "+e);
				ConnexionActivity.this.finish();
			}

			try{
				in = new ObjectInputStream(socket.getInputStream());
				out = new ObjectOutputStream(socket.getOutputStream());
			}catch(IOException e)
			{
				System.out.println("Impossible d'initialiser les buffer");
			}

			try
			{
				ConnexionActivity.this.rsa.generateKeys();
				sendMessage(new Message(Message.CONNEXION, nom));
				sendMessage(new Message(Message.PUBLIC_KEY,ConnexionActivity.this.rsa.getPublic_key()));
			}
			catch (Exception e) {
				System.out.println("Exception doing login : " + e);
			}

			while(running)
			{
				try {
					Message message = (Message) in.readObject();

					switch(message.getType()) {
					case Message.PUBLIC_KEY:
						ConnexionActivity.this.serverPublicKey = message.getPublicKey();
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
								screen.setText(screen.getText()+"\n"+str);
								scrollView.fullScroll(ScrollView.FOCUS_DOWN);
								
							}
						}); // On affiche le message dans la zone d'affichage
						//System.out.println(str);
						//System.out.println("Fin mess cripté");
						break;

					case Message.MESSAGE:
						final String msg = message.getMessage();
						Log.v("message",msg);
						ConnexionActivity.this.runOnUiThread(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								screen.setText(screen.getText()+"\n"+msg);
								scrollView.fullScroll(ScrollView.FOCUS_DOWN);
							}
						});
						break;

					case Message.LISTES_CLIENTS:
						final ArrayList<Contact> liste = message.getListe();
						System.out.println("Il y a " + liste.size() + " clients connecté:");
						ConnexionActivity.this.runOnUiThread(new Runnable() {

							@Override
							public void run() {
								mDrawerList.setAdapter(new ContactAdapter(ConnexionActivity.this,
										R.layout.drawer_list_item, liste));
							}
						});

						//fenetre.refreshListeClient(liste);
						break;

					case Message.NOUVEAU_CLIENT:
						int idClient = message.getId();
						sendMessage(new Message(Message.ACTIVER_CLIENT, idClient));
						break;
					} 
				}
				catch(Exception e) {
					System.out.println("Exception ecoute du serveur: " + e);
				}
			}

			//out.writeObject(new Message(Message.CONNEXION, nomClient));



		}

	}

	 
	
	public void sendMessage(Message msg) {
		try {
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