package pack.knowyourdoctor.adapter;


// created By Darrel Rayen
//Search for Doctors & Hospitals
import android.content.DialogInterface;
import android.content.Intent;
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

import pack.knowyourdoctor.Global_Values;
import pack.knowyourdoctor.MapsActivity;
import pack.knowyourdoctor.R;

public class View_Fragment_LocationFinding extends Fragment {

    EditText Tlocation;
    Button currentlocation;
    Button searchlocation;
    GoogleMap googleMap;
    MapView mapView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.view_fragment_locationfinding, container, false);


        currentlocation = (Button) rootView.findViewById(R.id.button);
        searchlocation = (Button) rootView.findViewById(R.id.button2);
        final Intent viewmap  = new Intent(getActivity(), MapsActivity.class);
        Tlocation = (EditText) rootView.findViewById(R.id.editText);
        mapView = (MapView) rootView.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.onResume(); // Display the map immediately

            try
                {
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
                googleMap.addMarker(new MarkerOptions().position(new LatLng(Global_Values.latitude, Global_Values.longtitude)).title("You are here")); // Create Marker
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(Global_Values.latitude, Global_Values.longtitude))); // Move camera
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
                viewmap.putExtra("location", location);

                //Start the Maps Activity
                getActivity().startActivity(viewmap);

            }
        });

     return rootView;
    }

}
