package com.example.phonechatclient;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import common.Client;
import common.Message;
import android.os.Bundle;
import android.os.Environment;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

public class DownloadActivity extends ListActivity {


	public static final int MENU_DIRECTORY = Menu.FIRST+1;
	public static final int MENU_DOCUMENT = Menu.FIRST+2;
	public static final int MENU_DELETE = Menu.FIRST+3;
	public static final int MENU_SEND = Menu.FIRST+4;

	private TextView selection;
	private File rep;
	private int port;
	private String adresse;
	private File[] filename;
	private String cheminCourant;
	private File parentFile;
	private Client client;
	private ArrayList<Client> listeCli;
	private Socket socket;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private boolean running = true;
	private Spinner liste;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent connexion = getIntent();
		client = (Client) connexion.getSerializableExtra("client");
		port = connexion.getIntExtra("port", 30970);
		adresse = connexion.getStringExtra("adresse");
		setContentView(R.layout.activity_download);
		rep = Environment.getExternalStorageDirectory();
		if(rep.exists())
		{
			cheminCourant = rep.getAbsolutePath();
			filename = rep.listFiles();
			setListAdapter(new FileAdapter(this, android.R.layout.simple_list_item_1,filename));
			selection =(TextView)findViewById(R.id.nomRep);
			selection.setText(rep.getPath());
		}
		registerForContextMenu(getListView());
		new Thread(new ClientThread2()).start();
	}

	public void onListItemClick(ListView parent, View v, int pos, long id)
	{
		selection.setText(filename[pos].getPath());
		File enfant = filename[pos];
		loadRep(enfant);
	}

	public boolean onCreateOptionsMenu(Menu menu)
	{
		menu.add(Menu.NONE, MENU_DIRECTORY, Menu.NONE, "Nouveau repertoire")
		.setIcon(R.drawable.blank_file_icon);
		menu.add(Menu.NONE, MENU_DOCUMENT, Menu.NONE, "Nouveau document")
		.setIcon(R.drawable.directory_icon);

		return (super.onCreateOptionsMenu(menu));
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_DIRECTORY:
			Builder alert = new AlertDialog.Builder(this);
			alert.setTitle("Nouveau répertoire");
			final EditText champ = new EditText(this);
			alert.setView(champ);
			alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					File fbis = new File(cheminCourant+"/"+champ.getText());
					fbis.mkdir();
					loadRep(new File(cheminCourant));

				}
			});
			alert.setNegativeButton("Annuler", null);
			alert.show();

			break;
		case MENU_DOCUMENT:
			Builder alertDoc = new AlertDialog.Builder(this);
			alertDoc.setTitle("Nouveau document");
			final EditText champDoc = new EditText(this);
			alertDoc.setView(champDoc);
			alertDoc.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int which)
				{
					File f = new File(cheminCourant+"/"+champDoc.getText());
					try {
						f.createNewFile();
						loadRep(new File(cheminCourant));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
			alertDoc.setNegativeButton("Annuler", null);
			alertDoc.show();
			break;
		}

		return(super.onOptionsItemSelected(item));
	}

	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
	{
		menu.add(Menu.NONE, MENU_DELETE, Menu.NONE, "Supprimer");
		menu.add(Menu.NONE,MENU_SEND,Menu.NONE,"Envoyer");
		super.onCreateContextMenu(menu, v, menuInfo);
	}


	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info= (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
		//FileAdapter test = (FileAdapter)getListAdapter();
		switch (item.getItemId()) {
		case MENU_DELETE:

			filename[info.position].delete();
			loadRep(new File(cheminCourant));
			break;

		case MENU_SEND:
			AlertDialog.Builder builder = new AlertDialog.Builder(DownloadActivity.this);
			final View view = DownloadActivity.this.getLayoutInflater().inflate(R.layout.params, null);

			liste = (Spinner) view.findViewById(R.id.colorSelect);
			//liste.setAdapter(new ContactAdapter(DownloadActivity.this,android.R.layout.simple_spinner_item, listeCli));
			builder.setTitle("Paramètres");
			builder.setNegativeButton("Annuler", null);
			builder.setPositiveButton("Accepter", new DialogInterface.OnClickListener(){


				@Override
				public void onClick(DialogInterface dialog, int which) {
				}

			});
			builder.setView(view).show();
			break;
		}
		return (super.onContextItemSelected(item));

	}

	public boolean onKeyDown(int keyCode, KeyEvent event) 
	{

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			System.out.println("chemin :"+cheminCourant+" vs "+rep.getPath());
			if(!cheminCourant.equals(rep.getPath()))
			{
				selection.setText(parentFile.getPath());
				loadRep(parentFile);
			}
			else
			{
				this.finish();
			}
		}
		return false;

	}

	public void loadRep(File child)
	{

		if(child.exists() && child.isDirectory())
		{
			parentFile = child.getParentFile();
			cheminCourant = child.getAbsolutePath();
			filename = child.listFiles();
			System.out.println("chemin: "+child.getPath()+" nombre de fichier: "+filename.length);
			setListAdapter(new FileAdapter(this, android.R.layout.simple_list_item_1,filename));			
		}
	}

	public class ClientThread2 implements Runnable
	{

		public void run() {
			try {
				socket = new Socket(adresse,port);
			} catch (UnknownHostException e) {
				System.out.println("oups");
				e.printStackTrace();
			} catch (IOException e) {
				System.out.println("oups");
				e.printStackTrace();
			}
			
			if(socket != null)
			{
				Log.v("socket","le socket est initialisé");
			}
			else
			{
				Log.v("socket","le socket n'est pas initialisé");
			}

			try{
				in = new ObjectInputStream(socket.getInputStream());
				out = new ObjectOutputStream(socket.getOutputStream());
			}catch(IOException e)
			{

				System.out.println("Impossible d'initialiser les buffer");
			}

			sendMessage(new Message(Message.LIST_CLIENTS));

			while(running)
			{
				try {
					Message message = (Message) in.readObject();
					switch(message.getType()) {

					case Message.LISTE_CLIENTS:
						final ArrayList<Client> listeCli = message.getListe();
						final String[] clients = {};
						for(int i=0; i<listeCli.size();i++)
						{
							clients[i] = listeCli.get(i).getName();
						}
						Log.v("nbClients",String.valueOf(listeCli.size()));
						DownloadActivity.this.runOnUiThread(new Runnable() {

							@Override
							public void run() {
								ArrayAdapter<String> aa = new ArrayAdapter<String>(DownloadActivity.this,
										android.R.layout.simple_spinner_item,
										clients);
								aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
								liste.setAdapter(aa);

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
					System.out.println("Exception ecoute du serveur download activity: " + e);
					running = false;
					DownloadActivity.this.finish();
				}
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
}
