package Models;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import pack.knowyourdoctor.R;

/**
 * Created by anthony on 10/26/2015.
 */
public class Model_GPS implements LocationListener {

    private static Model_GPS locationInstance = new Model_GPS();

    public static Model_GPS getInstance() {
        return locationInstance;
    }

    private Model_GPS() {
    }

    /*Check Whether GPS is active
     *@param context,locationmanager
     *@return boolean
     */
    public boolean isGPSActive(final Context context, LocationManager locationManager) {

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("GPS Validator");
            builder.setMessage(context.getResources().getString(R.string.enable_gps))
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(@SuppressWarnings("unused") final DialogInterface dialog
                                , @SuppressWarnings("unused") final int id) {
                            context.startActivity(new Intent(android.provider
                                    .Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog
                                , @SuppressWarnings("unused") final int id) {
                            dialog.cancel();
                        }
                    });
            final AlertDialog alert = builder.create();
            alert.show();
            return false;
        }
        return true;
    }

    /*Get Current Location
     *@param context
     *@return
     */
    public void getCurrentLocation(Context context) {

        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (isGPSActive(context, locationManager)) {
            // Creating an empty criteria object
            Criteria criteria = new Criteria();
            // Getting the name of the provider that meets the criteria
            String provider = locationManager.getBestProvider(criteria, false);

            if (provider != null && !provider.equals("")) {
                // Get the location from the given provider
                Location location = locationManager.getLastKnownLocation(provider);
                locationManager.requestLocationUpdates(provider, 20000, 1, this);

                if (location != null) {
                    onLocationChanged(location);
                } else {
                    Toast.makeText(context, context.getResources().getString(R.string.cannot_retrieve_location), Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(context, context.getResources().getString(R.string.no_provider), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Model_GlobalValues.latitude = location.getLatitude();
        Model_GlobalValues.longtitude = location.getLongitude();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
