package WebServiceAccess;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

import Models.GlobalValueModel;
import pack.knowyourdoctor.Constants.Numbers;
import pack.knowyourdoctor.Constants.Strings;
import pack.knowyourdoctor.MainControllers.Controller_WebTasks;
import pack.knowyourdoctor.R;

//Retrieve all hospitals
public class WebTask_SearchHospital
        extends AsyncTask<String, Void, List<Address>>
        implements WebTask_Interface {
    private Context context;
    private GoogleMap mMap;
    private String hospitalName;

    //Getters and setters
    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public void setmMap(GoogleMap mMap) {
        this.mMap = mMap;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    //Run in background thread - Execution of web task
    @Override
    protected List<Address> doInBackground(String... locationName) {
        Geocoder geocoder = new Geocoder(context);

        List<Address> addresses = null;

        try {
            // Getting a maximum of 3 Address that matches the input text
            addresses = geocoder.getFromLocationName(locationName[Numbers.ZERO], Numbers.THREE);

        } catch (IOException ex) {

            ex.printStackTrace();
        }
        return addresses;
    }

    //Execute after the web task executed
    @Override
    protected void onPostExecute(List<Address> addresses) {
        if (addresses == null || addresses.size() == Numbers.ZERO) {
            Toast.makeText(context, context.getResources().getString(R.string.hospital_not_located)
                    + Strings.STRING_WITH_SPACE + hospitalName, Toast.LENGTH_SHORT).show();
        }
        //Add markerr for each matching context
        for (int i = Numbers.ZERO; i < addresses.size(); i++) {
            Address address = addresses.get(i);

            double hospitalLat = address.getLatitude();
            double hospitalLang = address.getLongitude();
            // Creating an instance of GeoPoint, to display in Google Map
            LatLng latLng2 = new LatLng(hospitalLat, hospitalLang);

            String addressText = String.format(Strings.STRING_FORMAT,
                    address.getMaxAddressLineIndex() > Numbers.ZERO ?
                            address.getAddressLine(Numbers.ZERO) : Strings.EMPTY_STRING,
                    address.getCountryName());

            address.setCountryCode(Strings.SRI_LANKA_CODE);

            MarkerOptions locationmarker = new MarkerOptions();
            locationmarker.position(latLng2);
            locationmarker.title(addressText);

            mMap.addMarker(locationmarker);

            //Locate the first Location
            if (i == Numbers.ZERO) {
                //Move Camera
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(latLng2)
                        .zoom(12).build();
                mMap.animateCamera(CameraUpdateFactory
                        .newCameraPosition(cameraPosition));

                //Set source and destination
                LatLng source = new LatLng(GlobalValueModel.latitude, GlobalValueModel.longtitude);
                LatLng destination = new LatLng(hospitalLat, hospitalLang);
                String url = getDirectionsUrl(source, destination);

                Controller_WebTasks controller_webTasks = new Controller_WebTasks();
                controller_webTasks.executeSetMapPath(url, mMap);
            }
        }
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

        return url;
    }

    //Method to start execution of current web task
    @Override
    public void executeWebTask() {
        this.execute(hospitalName + Strings.HOSPITAL_SL);
    }
}
