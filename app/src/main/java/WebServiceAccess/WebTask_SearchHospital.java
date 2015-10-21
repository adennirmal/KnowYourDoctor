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

/**
 * Created by Darrel on 10/22/2015.
 */
public class WebTask_SearchHospital extends AsyncTask<String, Void, List<Address>> implements WebTask_Interface {

    private Context context;
    private GoogleMap mMap;
    private String hospitalName;

    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public GoogleMap getmMap() {
        return mMap;
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

    @Override
    public void executeWebTask() {

        this.execute(hospitalName + " Hospital Sri Lanka");
    }

    @Override
    protected List<Address> doInBackground(String... locationName) {
        Geocoder geocoder = new Geocoder(context);

        List<Address> addresses = null;

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

            Toast.makeText(context, "No Location found", Toast.LENGTH_SHORT).show();
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
            if (i == 0) {
                //Move Camera
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(latLng2)
                        .zoom(12).build();
                mMap.animateCamera(CameraUpdateFactory
                        .newCameraPosition(cameraPosition));
            }
        }
    }
}
