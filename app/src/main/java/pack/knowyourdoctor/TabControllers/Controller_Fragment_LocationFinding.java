package pack.knowyourdoctor.TabControllers;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

import java.util.ArrayList;

import Models.DoctorModel;
import Models.GPSModel;
import Models.GlobalValueModel;
import ValidationRules.RequiredFieldValidation;
import pack.knowyourdoctor.Constants.Strings;
import pack.knowyourdoctor.MainControllers.Controller_WebTasks;
import pack.knowyourdoctor.R;

import static com.google.android.gms.common.api.GoogleApiClient.*;

//Search for Doctors & Hospitals
public class Controller_Fragment_LocationFinding
        extends Fragment
        implements LocationListener, OnConnectionFailedListener, ConnectionCallbacks {
    EditText Tlocation;
    Button nearestHospitalofDoctorbtn;
    Button searchHospitalbtn;
    GoogleMap googleMap;
    MapView mapView;
    Context context;
    Spinner doctorName;
    StringBuilder url;
    ArrayList<DoctorModel> _doctorModels;
    JSONObject currentLocationJSON = new JSONObject();

    //onCreate method - calls in the initializing the dialog
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.view_fragment_locationfinding, container, false);
        context = rootView.getContext();

        //Get Current Location
        GPSModel modelGps = GPSModel.getInstance();
        modelGps.getCurrentLocation(context);

        searchHospitalbtn = (Button) rootView.findViewById(R.id.hospitalSearchBtn);
        nearestHospitalofDoctorbtn = (Button) rootView.findViewById(R.id.nearestHospitalofDoctor);
        Tlocation = (EditText) rootView.findViewById(R.id.hospitalName);
        doctorName = (Spinner) rootView.findViewById(R.id.doctorName);
        mapView = (MapView) rootView.findViewById(R.id.mapView);
        _doctorModels = new ArrayList<>();
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        final Controller_WebTasks controller_webTasks = new Controller_WebTasks();

        url = new StringBuilder(context.getResources().getString(R.string.webserviceLink));
        url.append(Strings.GET_ALL_LOCATED_DOCTORS);

        try {
            //Assign Current Coordinates
            currentLocationJSON.put(Strings.JSON_LATITUDE, GlobalValueModel.latitude);
            currentLocationJSON.put(Strings.JSON_LONGTITUDE, GlobalValueModel.longtitude);
            //Initiallize the Google Map
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {

            e.printStackTrace();
        }
        //Execute Controller
        controller_webTasks.executeDoctorListLoadTask(context, _doctorModels, doctorName,
                currentLocationJSON, url.toString());
        // Load Map
        googleMap = mapView.getMap();
        // Blue pointer on current location
        googleMap.setMyLocationEnabled(true);
        nearestHospitalofDoctorbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create Marker
                googleMap.addMarker(new MarkerOptions().position(new LatLng(GlobalValueModel.latitude,
                        GlobalValueModel.longtitude)).title(context.getResources()
                        .getString(R.string.Your_current_location))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                // Move camera
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(GlobalValueModel.latitude,
                        GlobalValueModel.longtitude)));
                // Zoom camera to the current Location
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(context.getResources()
                        .getInteger(R.integer.zoom_into)));
            }
        });

        searchHospitalbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!RequiredFieldValidation.isEmpty(Tlocation.getText().toString())) {
                    // Getting user input location
                    googleMap.clear();
                    googleMap.addMarker(new MarkerOptions().position(new LatLng(GlobalValueModel.latitude,
                            GlobalValueModel.longtitude)).title(context.getResources()
                            .getString(R.string.Your_current_location))
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                    String location = Tlocation.getText().toString();
                    controller_webTasks.executeSearchHospitalTask(context, location, googleMap);
                } else {
                    Tlocation.setError(context.getResources().getString(R.string.hospital_name_required));
                }
            }
        });
        return rootView;
    }

    @Override
    public void onLocationChanged(Location location) {
        //GlobalValueModel.latitude = location.getLatitude();
        //GlobalValueModel.longtitude = location.getLongitude();
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

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
