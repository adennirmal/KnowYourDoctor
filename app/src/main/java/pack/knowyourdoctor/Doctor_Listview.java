package pack.knowyourdoctor;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Doctor_Listview extends Activity {

    private DoctorDataAdapter adpt;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_main);

        adpt  = new DoctorDataAdapter(new ArrayList<Doctor_Data>(), this);
        ListView lView = (ListView) findViewById(R.id.listview);

        lView.setAdapter(adpt);

        // Execute async load task
        (new AsyncListViewLoader()).execute("http://yasi24.esy.es/doctor/public/doctors");
    }


    private class AsyncListViewLoader extends AsyncTask<String, Void, List<Doctor_Data>> {
        private final ProgressDialog dialog = new ProgressDialog(Doctor_Listview.this);

        @Override
        protected void onPostExecute(List<Doctor_Data> result) {
            super.onPostExecute(result);
            dialog.dismiss();
            adpt.setItemList(result);
            adpt.notifyDataSetChanged();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Downloading contacts...");
            dialog.show();
        }

        @Override
        protected List<Doctor_Data> doInBackground(String... params) {
            List<Doctor_Data> result = new ArrayList<Doctor_Data>();

            try {
                URL u = new URL(params[0]);

                HttpURLConnection conn = (HttpURLConnection) u.openConnection();
                conn.setRequestMethod("GET");

                conn.connect();
                InputStream is = conn.getInputStream();

                // Reading the output stream
                byte[] b = new byte[1024];
                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                while ( is.read(b) != -1)
                    baos.write(b);

                String JSONResp = new String(baos.toByteArray());

                JSONArray arr = new JSONArray(JSONResp);
                for (int i=0; i < arr.length(); i++) {
                    result.add(convertContact(arr.getJSONObject(i)));
                }
                return result;
            }
            catch(Throwable t) {
                t.printStackTrace();
            }
            return null;
        }

        private Doctor_Data convertContact(JSONObject obj) throws JSONException {
            String id = obj.getString("id");
            String doctor_name = obj.getString("doctor_name");
            String doctor_id = obj.getString("doctor_id");
            String updated_at = obj.getString("updated_at");

            return new Doctor_Data(id,doctor_id,doctor_name,updated_at);
        }

    }



}
