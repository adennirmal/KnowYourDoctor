package WebServiceAccess;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import Models.DoctorModel;
import Models.GlobalValueModel;
import Models.HospitalLocationModel;
import pack.knowyourdoctor.Constants.Numbers;
import pack.knowyourdoctor.Constants.Strings;
import pack.knowyourdoctor.MainControllers.Controller_WebTasks;

//To retrieve all locations for given doctor
public class WebTask_GetDoctorLocations
        extends AsyncTask<String, String, String>
        implements WebTask_Interface {
    String jsonResult;
    private DoctorModel selectedDoctor;
    private Context context;
    private JSONObject jObject;
    private String url;
    private ArrayList<HospitalLocationModel> hospitals;
    private GoogleMap mMap;

    //Getters and setters
    public DoctorModel getSelectedDoctor() {
        return selectedDoctor;
    }

    public GoogleMap getmMap() {
        return mMap;
    }

    public void setmMap(GoogleMap mMap) {
        this.mMap = mMap;
    }

    public void setSelectedDoctor(DoctorModel selectedDoctor) {
        this.selectedDoctor = selectedDoctor;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public JSONObject getjObject() {
        return jObject;
    }

    public void setjObject(JSONObject jObject) {
        this.jObject = jObject;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public ArrayList<HospitalLocationModel> getHospitals() {
        return hospitals;
    }

    public void setHospitals(ArrayList<HospitalLocationModel> hospitals) {
        this.hospitals = hospitals;
    }

    //Run in background thread - Execution of web task
    @Override
    protected String doInBackground(String... params) {
        HttpParams httpParameters = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParameters, 10000);
        HttpConnectionParams.setSoTimeout(httpParameters, 10000 + 12000);
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(params[0]);
        httppost.setParams(httpParameters);
        try {
            httppost.setEntity(new StringEntity(jObject.toString(), Strings.TEXT_TYPE));
            HttpResponse response = httpclient.execute(httppost);
            jsonResult = inputStreamToString(response.getEntity().getContent()).toString();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonResult;
    }

    //Convert Character Stream to StringBuilder object
    private StringBuilder inputStreamToString(InputStream is) {
        String rLine;
        StringBuilder questionString = new StringBuilder();
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
        try {
            while ((rLine = rd.readLine()) != null) {
                questionString.append(rLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return questionString;
    }

    //Execute after the web task executed
    @Override
    protected void onPostExecute(String s) {
        try {
            //get locations
            JSONObject jsonResponse = new JSONObject(jsonResult);
            JSONArray hostpitalDetailNode = jsonResponse.optJSONArray("locationsOfDoctor");

            for (int i = Numbers.ZERO; i < hostpitalDetailNode.length(); i++) {
                HospitalLocationModel hospital = new HospitalLocationModel();
                JSONObject jsonHospital = hostpitalDetailNode.getJSONObject(i);

                int locationID = Integer.parseInt(jsonHospital.optString(Strings.JSON_HOSPITAL_ID));
                hospital.setId(locationID);

                String hospitalName = jsonHospital.optString(Strings.JSON_HOSPITAL_NAME);
                hospital.setName(hospitalName);

                String hospitalAddress = jsonHospital.optString(Strings.JSON_HOSPITAL_ADDRESS);
                hospital.setAddress(hospitalAddress);

                Double hospitalLatitude = Double.parseDouble(
                        jsonHospital.optString(Strings.JSON_HOSPITAL_LATITUDE));
                hospital.setLatitude(hospitalLatitude);

                Double hospitalLongtitude = Double.parseDouble(
                        jsonHospital.optString(Strings.JSON_COMMENTS_LONGTITUDE));
                hospital.setLatitude(hospitalLongtitude);

                hospitals.add(hospital);

                String lastseenDate = jsonHospital.optString("lastRatedDate");
                /*SimpleDateFormat parseFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SS");
                SimpleDateFormat printFormat = new SimpleDateFormat("HH:mm:ss");
                Date date = parseFormat.parse(lastseenDate);*/
                //System.out.println(printFormat.format(date));


                //Add markers to the Doctors Location
                mMap.addMarker(new MarkerOptions().position(new LatLng(hospitalLatitude,
                        hospitalLongtitude))
                        .title(selectedDoctor.getFullName())
                        .snippet("Last seen at " + hospital.getName() + " at " + lastseenDate));

                //Move Camera
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(hospitalLatitude, hospitalLongtitude))
                        .zoom(12).build();
                mMap.animateCamera(CameraUpdateFactory
                        .newCameraPosition(cameraPosition));

                //Set source and destination
                LatLng source = new LatLng(GlobalValueModel.latitude, GlobalValueModel.longtitude);
                LatLng destination = new LatLng(hospitalLatitude, hospitalLongtitude);
                String url = getDirectionsUrl(source, destination);

                Controller_WebTasks controller_webTasks = new Controller_WebTasks();
                controller_webTasks.executeSetMapPath(url, mMap);
            }
        } catch (JSONException e) {
            e.printStackTrace();
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
        this.execute(url);
    }
}
