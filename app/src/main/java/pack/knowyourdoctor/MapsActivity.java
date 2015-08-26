package pack.knowyourdoctor;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        buildAlertMessageNoGps();
        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                //setUpMap();
                searchlocation();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */

    private void searchlocation(){

        String location = "";
        try{
       /* // Getting reference to EditText to get the user input location
        EditText Tlocation = (EditText) findViewById(R.id.editText);
        //Spinner loca = (Spinner)findViewById(R.id.spinner);

            System.out.println(R.id.editText);
        // Getting user input location
        String location = Tlocation.getText().toString();*/
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                location = extras.getString("location");
            }
            //    location!=null &&
            if( !location.equals("")){
                new GeocoderTask().execute(location + " Hospital Sri Lanka");
            }}
        catch (NullPointerException ex){

            ex.printStackTrace();
        }


    }
    private void setUpMap() {
        try {
            // mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));

            //Enable My location in Google Map
            mMap.setMyLocationEnabled(true);

            // Get LocationManager object from System Service LOCATION_SERVICE
            LocationManager locationmanager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


            if ( !locationmanager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
                buildAlertMessageNoGps();
            }

            // protected void buildAlertMessageNoGps()


            // Create a criteria object to retrieve provider
            Criteria criteria = new Criteria();

            // Get the name of the best provider
            String provider = locationmanager.getBestProvider(criteria, true);

            //Get current location
            Location mylocation = locationmanager.getLastKnownLocation(provider);

            //set map type
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);


            double latitude;

            //Get latitude of the currentlocation
            latitude = mylocation.getLatitude();

            //Get Longtitude of the current location';
            double longtitude = mylocation.getLongitude();

            // Create a Latlng object for the current location
            LatLng latlng = new LatLng(latitude, longtitude);

            //Show current location in GoogleMap
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));

            //Zoom in the Google Map

            mMap.animateCamera(CameraUpdateFactory.zoomTo(14));

            MarkerOptions marker = new MarkerOptions();
            marker.title("You're Here");
            marker.position(new LatLng(latitude, longtitude));
            mMap.addMarker(marker);

        }
        catch (NullPointerException ex){

            ex.printStackTrace();
        }
    }

    public void buildAlertMessageNoGps() {

        {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            dialog.cancel();
                        }
                    });
            final AlertDialog alert = builder.create();
            alert.show();
        }
    }

    // An AsyncTask class for accessing the GeoCoding Web Service
    public class GeocoderTask extends AsyncTask<String, Void, List<Address>> {


        @Override
        protected List<Address> doInBackground(String... locationName) {

            Geocoder geocoder = new Geocoder(getBaseContext());

            List<android.location.Address> addresses = null;

            try {

                // Getting a maximum of 3 Address that matches the input text
                addresses = geocoder.getFromLocationName(locationName[0], 3);

            } catch (IOException ex) {

                ex.printStackTrace();
            }
            return addresses;
        }


        @Override
        protected void onPostExecute(List<Address> addresses) {

            if (addresses == null || addresses.size() == 0) {

                Toast.makeText(getBaseContext(), "No Location found", Toast.LENGTH_SHORT).show();
            }

            //Add markerr for each matching context

            for (int i = 0; i < addresses.size(); i++) {


                Address address = (Address) addresses.get(i);

                // Creating an instance of GeoPoint, to display in Google Map

                LatLng latLng2 = new LatLng(address.getLatitude(), address.getLongitude());

                String addressText = String.format("%s,%s",
                        address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "",
                        address.getCountryName());

                address.setCountryCode("LK");

                MarkerOptions locationmarker = new MarkerOptions();
                locationmarker.position(latLng2);
                locationmarker.title(addressText);

                mMap.addMarker(locationmarker);

                //Locate the first Location
                if (i == 0)

                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng2));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(14));


            }
            // super.onPostExecute(addresses);
        }


    }
}
