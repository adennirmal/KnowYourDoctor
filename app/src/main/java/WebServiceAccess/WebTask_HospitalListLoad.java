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

import Models.HospitalLocationModel;
import pack.knowyourdoctor.Constants.Numbers;
import pack.knowyourdoctor.Constants.Strings;

public class WebTask_HospitalListLoad
        extends AsyncTask<String, Void, String>
        implements WebTask_Interface {
    private String jsonResult;
    private ArrayList<HospitalLocationModel> hospitals;
    private ArrayList<String> hospitalNames;
    private Spinner hospitalNamesSpinner;
    private Context context;
    private JSONObject jObject;
    private String url;

    public ArrayList<HospitalLocationModel> getHospitals() {
        return hospitals;
    }

    public void setHospitals(ArrayList<HospitalLocationModel> hospitals) {
        this.hospitals = hospitals;
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

    public void setjObject(JSONObject jObject) {
        this.jObject = jObject;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    //Run in background thread - Execution of web task
    @Override
    protected String doInBackground(String... params) {
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(params[Numbers.ZERO]);
        ;
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
    protected void onPostExecute(String result) {
        try {
            hospitalNames = new ArrayList<String>();
            //get rated doctor details
            JSONObject jsonResponse = new JSONObject(jsonResult);
            JSONArray hostpitalDetailNode = jsonResponse.optJSONArray(Strings.JSON_HOSPITALS_ARRAY);

            for (int i = Numbers.ZERO; i < hostpitalDetailNode.length(); i++) {
                HospitalLocationModel hospital = new HospitalLocationModel();
                JSONObject jsonHospital = hostpitalDetailNode.getJSONObject(i);

                int locationID = Integer.parseInt(jsonHospital.optString(Strings.JSON_HOSPITAL_ID));
                hospital.setId(locationID);

                String hospitalName = jsonHospital.optString(Strings.JSON_HOSPITAL_NAME);
                hospital.setName(hospitalName);

                String hospitalAddress = jsonHospital.optString(Strings.JSON_HOSPITAL_ADDRESS);
                hospital.setAddress(hospitalAddress);

                String hospitalDistrict = jsonHospital.optString(Strings.JSON_HOSPITAL_DISTRICT);
                hospital.setDistrict(hospitalDistrict);

                Double hospitalLatitude = Double.parseDouble(
                        jsonHospital.optString(Strings.JSON_HOSPITAL_LATITUDE));
                hospital.setLatitude(hospitalLatitude);

                Double hospitalLongtitude = Double.parseDouble(
                        jsonHospital.optString(Strings.JSON_COMMENTS_LONGTITUDE));
                hospital.setLatitude(hospitalLongtitude);

                hospitalNames.add(hospitalName);
                hospitals.add(hospital);
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>
                    (context, android.R.layout.simple_spinner_item, hospitalNames);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            hospitalNamesSpinner.setAdapter(adapter);
            hospitalNamesSpinner.setSelection(0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //Method to start execution of current web task
    @Override
    public void executeWebTask() {
        this.execute(url);
    }
}
