package com.sallerbaba.cube26;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private ProgressDialog pDialog;
    private ListView lv;
    ArrayAdapter<String> adapter;
    EditText inputSearch;

    private static String url = "http://hackerearth.0x10.info/api/payment_portals?type=json&query=list_gateway";
    // JSON Node names
    private static final String TAG_CURRENCIES = "currencies";
    private static final String TAG_ID = "id";
    private static final String TAG_NAME = "name";
    private static final String TAG_IMAGE = "image";
    private static final String TAG_DISCRIPTION = "discription";
    private static final String TAG_BRANDING = "branding";
    private static final String TAG_RATING = "rating";
    private static final String TAG_SETUP_FEE = "setup_fee";
    private static final String TAG_TRANSACTION_FEE = "transaction_fee";

    // contacts JSONArray
    JSONArray contacts = null;

    // Hashmap for ListView
    ArrayList<HashMap<String, String>> contactList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        contactList = new ArrayList<HashMap<String, String>>();
        lv = (ListView) findViewById(R.id.list_view);
        //ListView lv = getListView();

        // Listview on item click listener
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // getting values from selected ListItem
                String name = ((TextView) view.findViewById(R.id.name))
                        .getText().toString();
                String cost = ((TextView) view.findViewById(R.id.tranctionfee))
                        .getText().toString();
                String description = ((TextView) view.findViewById(R.id.branding))
                        .getText().toString();

                // Starting single contact activity
                Intent in = new Intent(getApplicationContext(), PayUbiz.class);
                in.putExtra(TAG_NAME, name);
                in.putExtra(TAG_TRANSACTION_FEE, cost);
                in.putExtra(TAG_DISCRIPTION, description);
                startActivity(in);

            }
        });

        // Calling async task to get json
        new GetContacts().execute();
    }


    private class GetContacts extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url, ServiceHandler._GET);

            Log.d("Response: ", "> " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    contacts = jsonObj.getJSONArray(TAG_RATING);

                    // looping through All Contacts
                    for (int i = 0; i < contacts.length(); i++) {
                        JSONObject c = contacts.getJSONObject(i);

                        String id = c.getString(TAG_ID);
                        String name = c.getString(TAG_NAME);
                        String image = c.getString(TAG_IMAGE);
                        String discription = c.getString(TAG_DISCRIPTION);
                        String branding = c.getString(TAG_BRANDING);

                        // Phone node is JSON Object
                        JSONObject rating = c.getJSONObject(TAG_RATING);
                        String setup_fee = rating.getString(TAG_SETUP_FEE);
                        String transaction_fee = rating.getString(TAG_TRANSACTION_FEE);
                        String currencies = rating.getString(TAG_CURRENCIES);

                        // tmp hashmap for single contact
                        HashMap<String, String> contact = new HashMap<String, String>();

                        // adding each child node to HashMap key => value
                        contact.put(TAG_ID, id);
                        contact.put(TAG_NAME, name);
                        contact.put(TAG_IMAGE, image);
                        contact.put(TAG_BRANDING, branding);

                        // adding contact to contact list
                        contactList.add(contact);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();

            ListAdapter adapter = new SimpleAdapter(
                    MainActivity.this, contactList, R.layout.item_list,
                    new String[]{TAG_NAME, TAG_IMAGE, TAG_RATING}, new int[]{R.id.name, R.id.iv_img, R.id.ratig});

            //setListAdapter(adapter);
        }

    String products[] = {"PayUMoney", "Authorize.Net", "PayUMoney", "VT Direct", "PayUMoney", "Paypal", "PayUMoney", "Stripe", "PayUMoney", "Paytm", "PayUMoney", "Paytm"};
    }
}
