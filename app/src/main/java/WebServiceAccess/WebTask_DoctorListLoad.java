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
import pack.knowyourdoctor.Constants.Strings;

//Retrieve doctors who are having submitted locations
public class WebTask_DoctorListLoad
        extends AsyncTask<String, Void, String>
        implements WebTask_Interface {
    private String jsonResult;
    private ArrayList<DoctorModel> doctorList;
    private ArrayList<String> doctorNames;
    private Spinner doctorNamesSpinner;
    private Context context;
    private JSONObject jObject;
    private String url;

    //Getter and setters
    public void setDoctorList(ArrayList<DoctorModel> doctorList) {
        this.doctorList = doctorList;
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
        HttpParams httpParameters = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParameters, 10000);
        HttpConnectionParams.setSoTimeout(httpParameters, 10000+12000);
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
            doctorNames = new ArrayList<>();
            //Retrieve Doctor Names
            JSONObject jsonResponse = new JSONObject(jsonResult);
            JSONArray doctorNameNode = jsonResponse.optJSONArray(Strings.JSON_LOCATED_DOC);

            for (int i = 0; i < doctorNameNode.length(); i++) {
                DoctorModel doctor = new DoctorModel();
                JSONObject jsonDoctor = doctorNameNode.getJSONObject(i);

                doctor.setRegNo(Integer.parseInt(jsonDoctor.optString(Strings.JSON_DOC_ID)));

                String doctorName = jsonDoctor.optString(Strings.JSON_DOC_NAME);
                doctor.setFullName(doctorName);

                //Set reg Date
                doctor.setRegDate(jsonDoctor.optString(Strings.JSON_DOC_REG_DATE));

                //Set reg No
                doctor.setAddress(jsonDoctor.optString(Strings.JSON_DOC_ADDRESS));

                //Set reg No
                doctor.setQualifications(jsonDoctor.optString(Strings.JSON_DOC_QUALIFICATION));

                doctorNames.add(doctorName);
                doctorList.add(doctor);
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>
                    (context, android.R.layout.simple_spinner_item, doctorNames);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            doctorNamesSpinner.setAdapter(adapter);
            doctorNamesSpinner.setSelection(0);

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
