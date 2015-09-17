package pack.knowyourdoctor.adapter;

// created By Darrel Rayen
//Search for Doctors & Hospitals

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import pack.knowyourdoctor.Global_Values;
import pack.knowyourdoctor.MapsActivity;
import pack.knowyourdoctor.R;

public class View_Fragment_LocationFinding extends Fragment {

    EditText hospitalname;
    EditText city;
    EditText doctorname;
    Button currentlocation;
    Button searchlocation;
    GoogleMap googleMap;
    MapView mapView;

    public String jsonResult;
    String url ="http://sepandroidmobile.esy.es/doctorlist.php?latitude="+Global_Values.latitude+"&longtitude="+Global_Values.longtitude;
    //Webservices webservices = new Webservices();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.view_fragment_locationfinding, container, false);

        //Sync the UI fields with the created Variables
        currentlocation = (Button) rootView.findViewById(R.id.button);
        searchlocation = (Button) rootView.findViewById(R.id.button2);
        final Intent viewmap = new Intent(getActivity(), MapsActivity.class);
        hospitalname = (EditText) rootView.findViewById(R.id.editText);
        city = (EditText) rootView.findViewById(R.id.city);
        doctorname = (EditText) rootView.findViewById(R.id.name_doctor);
        mapView = (MapView) rootView.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        // Display the map immediately
        mapView.onResume();

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());

        } catch (Exception e) {
            e.printStackTrace();
        }
        // Load Google Map
        googleMap = mapView.getMap();

        // Blue pointer on current location
        googleMap.setMyLocationEnabled(true);

        currentlocation.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // Create Red Marker on Current Location
                googleMap.addMarker(new MarkerOptions().position(new LatLng(Global_Values.latitude, Global_Values.longtitude)).title("You are here"));
                //Move Camera to the Marker Location
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(Global_Values.latitude, Global_Values.longtitude)));
                // Zoom camera to the Marker Location
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(14));
                //Initiate Webservice to obtain nearest Doctors
                accessWebService();
                // Toast.makeText(getActivity().getBaseContext(),url,Toast.LENGTH_LONG);

            }
        });

        searchlocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Getting user input location
                if(("".equals(doctorname.getText())) && (!TextUtils.isEmpty(hospitalname.getText()) && (!TextUtils.isEmpty(city.getText()))))
                {
                    String location = hospitalname.getText().toString() + city.getText().toString();
                    viewmap.putExtra("location", location);

                    //Start the Maps Activity
                    getActivity().startActivity(viewmap);
                }


            }
        });

        return rootView;
    }

    //Create Async Task to obtain the Json object
    private class JsonReadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(params[0]);
            try {
                HttpResponse response = httpclient.execute(httppost);
                jsonResult = inputStreamToString(response.getEntity().getContent()).toString();

            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        private StringBuilder inputStreamToString(InputStream is) {
            String rLine = "";
            StringBuilder answer = new StringBuilder();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));

            try {
                while ((rLine = rd.readLine()) != null) {
                    answer.append(rLine);
                }
            } catch (IOException e) {
                e.printStackTrace();
                //Toast.makeText(th,"Unable to Action" + e.toString(), Toast.LENGTH_LONG).show();
            }
            return answer;
        }

        @Override
        protected void onPostExecute(String result)
        {
            RetreiveResults();
        }
    }

    //Spilt the Json object according to the Database Columns
    public void RetreiveResults() {

        try {
            JSONObject jsonResponse = new JSONObject(jsonResult);
            JSONArray jsonMainNode = jsonResponse.optJSONArray("nearest_doctors");

            for (int i = 0; i < jsonMainNode.length(); i++) {
                JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                String doctor_name = jsonChildNode.optString("doctor_name");
                String latitude = jsonChildNode.optString("latitude");
                String longitude = jsonChildNode.optString("longtitude");
                String date_time = jsonChildNode.optString("date_time");


                MarkerOptions marker = new MarkerOptions()
                        .position(new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude)))
                        .title("Dr." + doctor_name).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                // Adding marker
                googleMap.addMarker(marker);
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude)))
                        .zoom(12).build();
                googleMap.animateCamera(CameraUpdateFactory
                        .newCameraPosition(cameraPosition));

            }
        } catch (JSONException e) {

            e.printStackTrace();
            //Toast.makeText(getActivity().getApplicationContext(),url,Toast.LENGTH_SHORT).show();
            Toast.makeText(getActivity().getApplicationContext(),"Please try again later",Toast.LENGTH_SHORT).show();
        }

    }

    public void accessWebService() {

        try {
            JsonReadTask task = new JsonReadTask();
            // passes values for the urls string array
            task.execute(new String[]{url});

        }catch (NullPointerException ex){

            Toast.makeText(getActivity().getApplicationContext(),
                    "Unable to retreive Information", Toast.LENGTH_LONG).show();

        }
    }

}
