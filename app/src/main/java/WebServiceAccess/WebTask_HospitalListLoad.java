package WebServiceAccess;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import Models.Model_HospitalLocation;
import pack.knowyourdoctor.R;

public class WebTask_HospitalListLoad extends AsyncTask<String, Void, String> {
    private String jsonResult;
    private ArrayList<Model_HospitalLocation> hospitals;
    ArrayList<String> hospitalNames;
    private Spinner hospitalNamesSpinner;
    private Context context;
    private JSONObject jObject;

    public ArrayList<Model_HospitalLocation> getHospitals() {
        return hospitals;
    }

    public void setHospitals(ArrayList<Model_HospitalLocation> hospitals) {
        this.hospitals = hospitals;
    }

    public Spinner getHospitalNamesSpinner() {
        return hospitalNamesSpinner;
    }

    public void setHospitalNamesSpinner(Spinner hospitalNamesSpinner) {
        this.hospitalNamesSpinner = hospitalNamesSpinner;
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

    @Override
    protected String doInBackground(String... params) {
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(params[0]);
        ;
        try {
            httppost.setEntity(new StringEntity(jObject.toString(), "UTF-8"));
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

    private StringBuilder inputStreamToString(InputStream is) {
        String rLine = "";
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

    @Override
    protected void onPostExecute(String result) {
        try {
            hospitalNames = new ArrayList<String>();
            //get rated doctor details
            JSONObject jsonResponse = new JSONObject(jsonResult);
            JSONArray hostpitalDetailNode = jsonResponse.optJSONArray("hospitals");

            for (int i = 0; i < hostpitalDetailNode.length(); i++) {
                Model_HospitalLocation hospital = new Model_HospitalLocation();
                JSONObject jsonHospital = hostpitalDetailNode.getJSONObject(i);

                int locationID = Integer.parseInt(jsonHospital.optString("id"));
                hospital.setId(locationID);

                String hospitalName = jsonHospital.optString("name");
                hospital.setName(hospitalName);

                String hospitalAddress = jsonHospital.optString("address");
                hospital.setAddress(hospitalAddress);

                String hospitalDistrict = jsonHospital.optString("district");
                hospital.setDistrict(hospitalDistrict);

                Double hospitalLatitude = Double.parseDouble(jsonHospital.optString("latitude"));
                hospital.setLatitude(hospitalLatitude);

                Double hospitalLongtitude = Double.parseDouble(jsonHospital.optString("longtitude"));
                hospital.setLatitude(hospitalLongtitude);

                hospitalNames.add(hospitalName);
                hospitals.add(hospital);
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, hospitalNames);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            hospitalNamesSpinner.setAdapter(adapter);
            hospitalNamesSpinner.setSelection(0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
