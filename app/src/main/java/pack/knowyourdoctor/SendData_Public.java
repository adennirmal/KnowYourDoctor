package pack.knowyourdoctor;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import pack.knowyourdoctor.Send_Details.Person;
import pack.knowyourdoctor.Validators.RegNoValidation;


/**
 * Created by Yasi on 8/27/2015.
 */
public class SendData_Public extends Activity implements OnClickListener {

    TextView D_IsConnected;
    EditText DoctorID;
    EditText DoctorName;
    Button btnPost;
    //EditText DoctorUpdate;
    //EditText DoctorCreate;
    AlertDialog levelDialog;
    Person person;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_report_listview);

        DoctorID = (EditText) findViewById(R.id.doctorid1);
        DoctorName = (EditText) findViewById(R.id.doctorname1);
        btnPost = (Button) findViewById(R.id.btnpublic);
        D_IsConnected = (TextView) findViewById(R.id.D_sConnected);
        //DoctorUpdate =(EditText)findViewById(R.id.DoctorUpdate);
        //DoctorCreate =(EditText)findViewById(R.id.DoctorCreate);
        //View list.....................................................................................................................
        Button viewlist = (Button)findViewById(R.id.viewlist);
        viewlist.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final CharSequence[] items = {"View Fake Doctor List","Share Details via Email"};

                AlertDialog.Builder builder = new AlertDialog.Builder(SendData_Public.this);
                builder.setTitle("Select One Option");
                builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {

                        switch (item) {
                            case 0:
                                Intent g = new Intent(getApplicationContext(),Doctor_Listview.class);
                                startActivity(g);
                                break;
                            case 1:
                                Intent h = new Intent(getApplicationContext(),SendMail_Public.class);
                                startActivity(h);
                                break;
                            //case 3:

                               // Toast.makeText(getBaseContext(), "Enter Doctor Details Here!", Toast.LENGTH_LONG).show();
                                //break;
                        }
                        levelDialog.dismiss();
                    }
                });
                levelDialog = builder.create();
                levelDialog.show();

            }
        });
     //.................................................................................................................................

    // share data.......................................................................................................................
        View.OnClickListener handler = new View.OnClickListener() {
            public void onClick(View v) {
                switch (v.getId()) {

                    case R.id.btnsendpublic:
                        if (!RegNoValidation.isValidRegNoWithRequiredValidation(DoctorID.getText().toString())) {
                            DoctorID.setError("Please enter Doctor Id");
                            break;

                        }
                        if (DoctorName.getText().toString().equals(""))
                        {
                            DoctorName.setText("Unknown");
                            //break;
                        }

                        //if (!validate())
                        // Toast.makeText(getBaseContext(), "Enter data!", Toast.LENGTH_LONG).show();
                        if (RegNoValidation.isValidRegNoWithRequiredValidation(DoctorID.getText().toString())) {

                            ShareData();
                            break;
                        }

                        //case R.id.buttonShareImage:
                        // shareImage();
                        //break;
                }
            }
        };


        findViewById(R.id.btnsendpublic).setOnClickListener(handler);

        //.............................................................................................................................


       //Send data to list.......................................................................................................

        if(isConnected()){
            D_IsConnected.setText("You are connected");
        }
        else{
            D_IsConnected.setText("You aren't connected");
        }

        btnPost.setOnClickListener(this);

        }

    public static String POST(String url, Person person){
        InputStream inputStream = null;
        String result = "";
        try {

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);

            String json = "";

            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("doctor_id", person.getID());
            jsonObject.accumulate("doctor_name", person.getName());
            //jsonObject.accumulate("created_at",person.getCreate());
            //jsonObject.accumulate("updated_at",person.getUpdate());


            json = jsonObject.toString();

            StringEntity send = new StringEntity(json);
            httpPost.setEntity(send);

            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            HttpResponse httpResponse = httpclient.execute(httpPost);
            inputStream = httpResponse.getEntity().getContent();


            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }


        return result;
    }

    public boolean isConnected(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }
    @Override
    public void onClick(View view) {



        switch(view.getId()) {

            case R.id.btnpublic:
                if (!RegNoValidation.isValidRegNoWithRequiredValidation(DoctorID.getText().toString())) {
                    DoctorID.setError("Please enter Doctor Id");
                    break;

                }
                if (DoctorName.getText().toString().trim().equals("")) {
                    DoctorName.setText("Unknown");
                    //break;
                }

                if (RegNoValidation.isValidRegNoWithRequiredValidation(DoctorID.getText().toString())){


                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:

                                    new HttpAsyncTask().execute("http://yasi24.esy.es/doctor/public/doctors");
                                    break;
                                case DialogInterface.BUTTON_NEGATIVE:

                                    Toast.makeText(SendData_Public.this, "Canceled", Toast.LENGTH_LONG).show();
                                    break;
                            }
                        }
                    };
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("Are you sure want to submit?") .setPositiveButton("Yes", dialogClickListener) .setNegativeButton("No", dialogClickListener).show();

                }
        }

    }
    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            person = new Person();
            person.setID(DoctorID.getText().toString());
            person.setName(DoctorName.getText().toString());
            //person.setCreated_at(DoctorCreate.getText().toString());
            //person.setUpdated_at(DoctorUpdate.getText().toString());

            return POST(urls[0],person);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            System.out.print(result);
            Toast.makeText(getBaseContext(), "Data Sent!", Toast.LENGTH_LONG).show();

            Intent u = new Intent(getApplicationContext(),Doctor_Listview.class);
            startActivity(u);

        }
    }

    private boolean validate(){
        if(DoctorID.getText().toString().trim().equals(""))
            return false;
        else if(DoctorName.getText().toString().trim().equals(""))
            return false;
        //else if(DoctorCreate.getText().toString().trim().equals(""))
            //return false;
        //else if(DoctorUpdate.getText().toString().trim().equals(""))
            //return false;

        else
            return true;
    }
    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

//Share data.......................................................................................................
    private void ShareData() {
        String id = DoctorID.getText().toString();
        String name = DoctorName.getText().toString();

        String newline = System.getProperty("line.separator");
        String results ="-------------SLMC------------" + newline+
                "Fake Doctor Details" + newline+
                "----------------------------------" +newline+
                "Registration No :" +id + newline+
                "Doctor Name     :" +name + newline;

        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_SUBJECT, "Fake Doctor Details");
        share.putExtra(Intent.EXTRA_TEXT, results);
        startActivity(Intent.createChooser(share, "Share Data"));
    }


}
