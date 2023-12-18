package com.example.healthcheck;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class contact extends AppCompatActivity {
    Button add;
    final int PICK_CONTACT=1;
    private static final String LOG_TAG = "error occurred";
    ListView listView;//List of all contact
    View my_view;//view of each contact
    String[] items;//name of all chosen contacts
    String information;//information that need to be sent
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact);
        this.add = this.findViewById(R.id.add);
        //getting total overview of patient from Show class
        try{
            Intent i = getIntent();
            Bundle bundle = i.getExtras();
            information = bundle.getString("information", "");
        }catch (Exception e){
            Log.i(LOG_TAG,"error!");
        }
        //setting up list of contacts
        this.listView = this.findViewById(R.id.listView);
        String contact = readFromFile(getApplicationContext());
        //all_info containing chosen contact information
        String[] all_info = contact.split("\r\n|\r|\n");
        String[] names = new String[all_info.length/2];
        int count = 0;
        if(all_info.length > 0) {
            for (int i = 1; i < all_info.length - 1; i = i + 2) {
                if (!all_info[i].equals("")) {
                    names[count] = all_info[i] + " : " + all_info[i + 1];
                    count++;
                }
            }
        }
        items = names;
        customAdapter customAdapter = new customAdapter();
        listView.setAdapter(customAdapter);
        //if click on each item Messenger class execute with needed information to send message
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String contact = String.valueOf(listView.getItemAtPosition(i));
                String number = find_phone_number(contact);
                startActivity(new Intent(getApplicationContext(), Messenger.class)
                        .putExtra("number", number)
                        .putExtra("contact", contact)
                        .putExtra("information", information));
            }
        });
        this.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                askPermission();
                Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                startActivityForResult(pickContact, PICK_CONTACT);
                //onActivityResult
            }
        });
    }
    //going to home page if back button pressed
    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        switch (requestCode) {
            case PICK_CONTACT:
                if (resultCode == RESULT_OK) {
                    Uri contactUri = intent.getData();
                    Cursor nameCursor = getContentResolver().query(contactUri, null, null, null, null);
                    if (nameCursor.moveToFirst()) {
                        String name = nameCursor.getString(nameCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                        String number = nameCursor.getString(nameCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        //writing name and number of chosen contact to a file to save it
                        writeToFile(name + "\n" + number, getApplicationContext());
                        nameCursor.close();
                    }
                }
                //going back to contact class
                startActivity(new Intent(getApplicationContext(), contact.class));
                break;
        }
    }
    private void askPermission()  {
        // for permission to access External Storage.
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            // Check if we have Call permission
            int permission = ActivityCompat.checkSelfPermission(getApplicationContext(),
                    Manifest.permission.READ_CONTACTS);
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // If don't have permission so prompt the user.
                this.requestPermissions(
                        new String[]{Manifest.permission.READ_CONTACTS},
                        1000
                );
            }
        }
    }
    //writing to a file to save data
    private void writeToFile(String data, Context context) {
        try {
            //getting content of a file
            String before = readFromFile(getApplicationContext());
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("config.txt",Context.MODE_PRIVATE));
            //setting up a String to write in a file
            String clear = before + "\n" + data;
            //deleting empty lines
            clear = clear.replaceAll("(?m)^[ \t]*\r?\n", "");
            outputStreamWriter.append(clear);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }
    //reading data from the config file
    private String readFromFile(Context context) {
        String ret = "";
        try {
            InputStream inputStream = context.openFileInput("config.txt");
            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();
                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append("\n").append(receiveString);
                }
                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }
        return ret;
    }
    //finding number of the chosen name
    public String find_phone_number(String name){
        for (int i = 0; i < items.length; i++) {
            if(items[i].contains(name)){
                return items[i].substring(items[i].indexOf(": ")+2);
            }
        }
        return null;
    }
    //setting listview to view contacts
    class customAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return items.length;
        }
        @Override
        public Object getItem(int i) {
            return items[i];
        }
        @Override
        public long getItemId(int i) {
            return 0;
        }
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            //reading list_item.xml and creating the view for each contact
            my_view = getLayoutInflater().inflate(R.layout.list_item, null);
            TextView contact_name = my_view.findViewById(R.id.contact);
            contact_name.setSelected(true);
            contact_name.setText(items[i]);
            return my_view;//returning view of each item in contact list
        }
    }
}