package pack.knowyourdoctor.Tab_Controllers;


// created By Darrel Rayen
//Search for Doctors & Hospitals

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import Models.Model_GlobalValues;
import pack.knowyourdoctor.R;

public class Controller_Fragment_LocationFinding extends Fragment {

    EditText Tlocation;
    Button currentlocation;
    Button searchlocation;
    GoogleMap googleMap;
    MapView mapView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.view_fragment_locationfinding, container, false);


        currentlocation = (Button) rootView.findViewById(R.id.hospitalSearchBtn);
        searchlocation = (Button) rootView.findViewById(R.id.button);
        //final Intent viewmap  = new Intent(getActivity(), MapsActivity.class);
        Tlocation = (EditText) rootView.findViewById(R.id.editText);
        mapView = (MapView) rootView.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.onResume(); // Display the map immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());

        } catch (Exception e)

        {
            e.printStackTrace();
        }

        googleMap = mapView.getMap(); // Load Map
        googleMap.setMyLocationEnabled(true); // Blue pointer on current location

        currentlocation.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {


                //System.out.println(R.id.editText);
                googleMap.addMarker(new MarkerOptions().position(new LatLng(Model_GlobalValues.latitude, Model_GlobalValues.longtitude)).title("You are here")); // Create Marker
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(Model_GlobalValues.latitude, Model_GlobalValues.longtitude))); // Move camera
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(14)); // Zoom camera to the current Location


                //Start the Maps Activity
                //getActivity().startActivity(viewmap);

            }
        });

        searchlocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Getting user input location
                String location = Tlocation.getText().toString();
                //viewmap.putExtra("location", location);

                //Start the Maps Activity
                //getActivity().startActivity(viewmap);

            }
        });

        return rootView;
    }

}
