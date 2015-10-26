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

import Models.Model_Doctor;

/**
 * Created by Darrel on 10/24/2015.
 */
public class WebTask_DoctorListLoad extends AsyncTask<String, Void, String> implements WebTask_Interface {

    private String jsonResult;
    private ArrayList<Model_Doctor> doctorList;
    private ArrayList<String> doctorNames;
    private Spinner doctorNamesSpinner;
    private Context context;
    private JSONObject jObject;
    private String url;

    public WebTask_DoctorListLoad() {
        this.jsonResult = jsonResult;
        this.doctorList = doctorList;
        this.doctorNamesSpinner = doctorNamesSpinner;
        this.context = context;
        this.jObject = jObject;
        this.url = url;
    }

    public String getJsonResult() {
        return jsonResult;
    }

    public void setJsonResult(String jsonResult) {
        this.jsonResult = jsonResult;
    }

    public ArrayList<Model_Doctor> getDoctorList() {
        return doctorList;
    }

    public void setDoctorList(ArrayList<Model_Doctor> doctorList) {
        this.doctorList = doctorList;
    }

    public Spinner getDoctorNamesSpinner() {
        return doctorNamesSpinner;
    }

    public void setDoctorNamesSpinner(Spinner doctorNamesSpinner) {
        this.doctorNamesSpinner = doctorNamesSpinner;
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

    @Override
    protected String doInBackground(String... params) {
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(params[0]);
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
    protected void onPostExecute(String s) {
        try {
            doctorNames = new ArrayList<>();
            //Retrieve Doctor Names
            JSONObject jsonResponse = new JSONObject(jsonResult);
            JSONArray doctorNameNode = jsonResponse.optJSONArray("locatedDoctors");

            for (int i = 0; i < doctorNameNode.length(); i++) {
                Model_Doctor doctor = new Model_Doctor();
                JSONObject jsonDoctor = doctorNameNode.getJSONObject(i);

                String doctorName = jsonDoctor.optString("docname");
                doctor.setFullName(doctorName);
                doctorNames.add(doctorName);
                doctorList.add(doctor);
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, doctorNames);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            doctorNamesSpinner.setAdapter(adapter);
            doctorNamesSpinner.setSelection(0);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void executeWebTask() {
        this.execute(url);
    }
}
