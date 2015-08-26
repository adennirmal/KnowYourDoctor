package pack.knowyourdoctor.adapter;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import pack.knowyourdoctor.Adapter_DoctorList;
import pack.knowyourdoctor.Model_Doctor;
import pack.knowyourdoctor.R;
import pack.knowyourdoctor.View_Home;

/**
 * A simple {@link Fragment} subclass.
 */
public class View_Fragment_DoctorDetails_Searched extends Fragment {

    //List display textview
    TextView txt;
    Context context;
    Adapter_DoctorList listAdapter;
    ExpandableListView listView;
    ArrayList<Model_Doctor> searchedDoctors;
    View rootView;
    String passedURL, searched_reg_no;
    /*
    public View_Fragment_DoctorDetails_Searched(String passedURL) {
        this.passedURL = passedURL;
    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.view_fragment_doctordetails_searched, container, false);
        searchedDoctors = new ArrayList<Model_Doctor>();

        txt = (TextView) rootView.findViewById(R.id.displayDetails);
        txt.setText("Loading list Please wait....");

        context = rootView.getContext();

        listView = (ExpandableListView) rootView.findViewById(R.id.expList);

        listAdapter = new Adapter_DoctorList(context,searchedDoctors);
        listView.setAdapter(listAdapter);

        passedURL = View_Home.urlBundle.get("url").toString();
        searched_reg_no = View_Home.urlBundle.get("RegNo").toString();

        //Bundle bundle = this.getArguments();
        //searched_reg_no = bundle.getString("RegNo");

        //String passedURL = getActivity().getIntent().getStringExtra("url");

        //Check internet connection is available or not
        if(isNetworkAvailable()) {
            //Start the background process
            new GetHTMLContent().execute(passedURL);
        }
        else{
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
            alertDialog.setTitle("Internet Connection error");
            alertDialog.setMessage("Do you want to enable the Internet Connection?");
            alertDialog.setPositiveButton("YES",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            alertDialog.setNeutralButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            alertDialog.show();
        }

        return rootView;
    }

    //Method to check internet connection availability
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    protected class GetHTMLContent extends AsyncTask<String, Integer, String> {
        String html;
        Thread t;

        @Override
        protected String doInBackground(String... params) {
            String url = params[0];
            Document doc = GetHTMLDocFromString(url);

            //Update Progress bar
            publishProgress(0);

            //Check parsed data has table tag
            if(doc.getElementById("r_table")!=null) {
                //Get number of pages

                Element resultsNumTag = doc.getElementsByTag("h2").get(0);

                //get numeric value from string,divide it by 20(one page contains 20 results) and store it inside the integer variable
                int noOfResults = Integer.parseInt(resultsNumTag.text().replaceAll("[a-zA-Z() ]+",""));
                //check noOfResults equals to multiplier of 20 or not (Example like 80)
                int noOfPages = noOfResults%20==0?noOfResults/20:(noOfResults/20)+1;
                //int noOfPages=1;
                /*if(doc.getElementById("p_n_links").hasText()) {
                    Elements navigationAnchors = doc.getElementById("p_n_links").getElementsByTag("a");
                    //This includes next anchor also (remove it use -1)
                    noOfPages = navigationAnchors.size()-1;
                }*/

                Element tableContent = doc.getElementById("r_table");
                Elements tableRows = tableContent.getElementsByTag("tr");


                //Iterate through webpages
                for(int i=0;i<noOfPages;i++){
                    int skipFirstRow = 0;
                    for (Element x : tableRows) {
                        //Skip first row because it contains headers of table
                        if (skipFirstRow == 0) {
                            skipFirstRow++;
                            continue;
                        }
                        //Get row details
                        Elements tds = x.getElementsByTag("td");

                        //Current Doctor
                        Model_Doctor currentDoctor = new Model_Doctor();
                        //Set reg No
                        currentDoctor.setRegNo(Integer.parseInt(tds.get(1).text()));

                        //Set reg Date
                        currentDoctor.setRegDate(tds.get(2).text());

                        //Set reg No
                        currentDoctor.setFullName(tds.get(3).text());

                        //Set reg No
                        currentDoctor.setAddress(tds.get(4).text());

                        //Set reg No
                        currentDoctor.setQualifications(tds.get(5).text());

                        searchedDoctors.add(currentDoctor);
                        //Update Progress bar
                    }
                    publishProgress((int) ((i / (float)noOfPages) * 100));
                    //Replace the start value of the url to navigate to next page
                    int nextPage = i+1;
                    if(nextPage!=noOfPages) {
                        url = url.replace("start=" + i, "start=" + nextPage);
                        doc = GetHTMLDocFromString(url);
                        tableContent = doc.getElementById("r_table");
                        tableRows = tableContent.getElementsByTag("tr");
                    }
                }
                //Update Progress bar
                publishProgress(100);
            }
            else{
                html = "No data found";
            }
            return html;
        }

        private Document GetHTMLDocFromString(String url){
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet(url);
            try {
                HttpResponse response = client.execute(request);

                html = "";
                InputStream in = response.getEntity().getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder str = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    str.append(line);
                }
                in.close();
                html = str.toString();

                Document doc = Jsoup.parse(html);

                //Check parsed data has table tag
                if(doc.getElementById("r_table")!=null) {
                    if (doc.getElementById("p_n_links").getElementsByTag("a") != null) {
                        //Elements navigationAnchors = doc.getElementById("p_n_links").getElementsByTag("a");
                        //This includes next anchor also (remove it use -1)
                        //noOfPages = navigationAnchors.size() - 1;
                    }

                    //Element tableContent = doc.getElementById("r_table");
                    //Elements tableRows = tableContent.getElementsByTag("tr");
                }
                return doc;
            }catch (IOException ex){
                return null;
            }
        }

        protected void onPostExecute(String result) {
            ProgressBar pBar = (ProgressBar) rootView.findViewById(R.id.progressShow);
            pBar.setVisibility(View.INVISIBLE);

            if(result.compareTo("No data found")!=0 && result.compareTo("Error")!=0) {
                txt.setText("Number of doctors found : " + searchedDoctors.size());
                //Collections.sort(searchedDoctors, new DoctorComparator());
                listAdapter.notifyDataSetChanged();
            }
            else{
                txt.setText(result);
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                alertDialog.setTitle("Registration Number (" + searched_reg_no + ") Doesn't Exist!");
                alertDialog.setMessage("Do you want to report to SLMC?");
                alertDialog.setPositiveButton("YES",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Fragment mFragment = new View_Fragment_ReportSend();
                        //getFragmentManager().beginTransaction().replace(R.id.fragmentShowDoctorDetails, mFragment).commit();
                    }
                });
                alertDialog.setNeutralButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                alertDialog.show();
            }
        }
        protected void onProgressUpdate(Integer... progress) {
            setProgressPercent(progress[0]);
        }
    }

    private void setProgressPercent(Integer progress) {
        TextView txt = (TextView) rootView.findViewById(R.id.displayDetails);
        txt.setText("Loading : " + progress +"% Completed");
        ProgressBar pBar = (ProgressBar) rootView.findViewById(R.id.progressShow);
        pBar.setProgress(progress);
        //listView.deferNotifyDataSetChanged();
        listAdapter.notifyDataSetChanged();
        //Collections.sort(searchedDoctors,new DoctorComparator());
        //listAdapter.notifyDataSetChanged();
    }

    //comparator to compare each doctor
    public class DoctorComparator implements Comparator<Model_Doctor> {
        @Override
        public int compare(Model_Doctor lhs, Model_Doctor rhs) {
            return lhs.getFullName().compareTo(rhs.getFullName());
        }
    }


}
