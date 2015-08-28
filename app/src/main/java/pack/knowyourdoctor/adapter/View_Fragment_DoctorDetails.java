package pack.knowyourdoctor.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
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

import pack.knowyourdoctor.Adapter_DoctorList;
import pack.knowyourdoctor.Model_Doctor;
import pack.knowyourdoctor.R;
import pack.knowyourdoctor.Validators.RegNoValidation;
import pack.knowyourdoctor.View_Home;

public class View_Fragment_DoctorDetails extends Fragment {

    Context context;

    TextView txt;
    Adapter_DoctorList listAdapter;
    ExpandableListView listView;

    ArrayList<Model_Doctor> searchedDoctors;

    LinearLayout linearLayoutView;

    String searchedRegNo;

    GetHTMLContent readHTMLPages;
    boolean isCancelled = false;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.view_fragment_doctordetails, container, false);
        context = rootView.getContext();
        //Set object of search page
        //final TextView registryText = (TextView)rootView.findViewById(R.id.registryTextView);
        final TextView regNoText = (TextView) rootView.findViewById(R.id.regNoTextView);
        final TextView initialsText = (TextView)rootView.findViewById(R.id.initialTextView);
        //final TextView familyNameText = (TextView)rootView.findViewById(R.id.familyNameTextView);
        //final TextView otherNameText = (TextView)rootView.findViewById(R.id.otherNameTextView);
        final TextView nicNoText = (TextView)rootView.findViewById(R.id.nicNoTextView);
        final TextView addressText = (TextView)rootView.findViewById(R.id.addressTextView);

        //final Spinner regSpinner = (Spinner)rootView.findViewById(R.id.registry);
        final EditText regNoTE = (EditText)rootView.findViewById(R.id.regNo);
        final EditText initialsTE = (EditText)rootView.findViewById(R.id.initial);
        final EditText familyNameTE = (EditText)rootView.findViewById(R.id.familyName);
        final EditText otherNameTE = (EditText)rootView.findViewById(R.id.otherName);
        final EditText nicNo = (EditText)rootView.findViewById(R.id.docNIC);
        final EditText addressTE = (EditText)rootView.findViewById(R.id.address);

        //final LinearLayout layout = (LinearLayout)rootView.findViewById(R.id.middleSemiColon);

        //hide advance search options
        //TextViews
        initialsText.setVisibility(View.INVISIBLE);
        nicNoText.setVisibility(View.INVISIBLE);
        addressText.setVisibility(View.INVISIBLE);

        //EditTexts
        initialsTE.setVisibility(View.INVISIBLE);
        nicNo.setVisibility(View.INVISIBLE);
        addressTE.setVisibility(View.INVISIBLE);

        Button button = (Button) rootView.findViewById(R.id.search);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                //validate
                if (!RegNoValidation.isValidRegNo(regNoTE.getText().toString())) {
                    regNoTE.setError("Invalid Registration Number");
                    return;
                }

                ArrayList<String> urlList = new ArrayList<String>();
                for(int i=3;i<7;i++) {
                    //Generate URL for four categories
                    StringBuilder url = new StringBuilder("http://www.srilankamedicalcouncil.org/registry.php?start=0&registry=");
                    //nothing selected in spinner
                    url.append(i);

                    //Generate URL
                    url.append("&initials=" + initialsTE.getText());
                    url.append("&last_name=" + familyNameTE.getText());
                    url.append("&other_name=" + otherNameTE.getText());
                    url.append("&reg_no=" + regNoTE.getText());
                    url.append("&nic=" + nicNo.getText());
                    url.append("&part_of_address=" + addressTE.getText());
                    url.append("&search=Search");
                    //replace all spaces with %20
                    String generatedURL = url.toString().replace(" ", "%20");

                    urlList.add(generatedURL);
                }

                searchedRegNo = regNoTE.getText().toString();

                //Setup part for display doctor details
                linearLayoutView = (LinearLayout) getActivity().findViewById(R.id.mainView);
                linearLayoutView.removeAllViewsInLayout();
                linearLayoutView.addView(View.inflate(context, R.layout.view_fragment_doctordetails_searched, null));
                linearLayoutView.setBackgroundColor(Color.parseColor("#f1f1f1"));

                //Setup search again button
                Button searchAgainBtn = (Button)linearLayoutView.findViewById(R.id.searchAgain);
                searchAgainBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ViewPager pager = (ViewPager) getActivity().findViewById(R.id.pager);
                        TabPagerAdapter mAdapter;
                        mAdapter = new TabPagerAdapter(getActivity().getSupportFragmentManager());
                        pager.setAdapter(mAdapter);
                        pager.setCurrentItem(0);
                        searchedDoctors.clear();
                    }
                });

                //Load Searched Doctor Details
                txt = (TextView) linearLayoutView.findViewById(R.id.displayDetails);
                txt.setText("Loading list Please wait....");

                listView = (ExpandableListView) rootView.findViewById(R.id.expList);

                searchedDoctors = new ArrayList<Model_Doctor>();

                listAdapter = new Adapter_DoctorList(context,searchedDoctors);
                listView.setAdapter(listAdapter);

                if(isNetworkAvailable()) {
                    //Start the background process
                    readHTMLPages = new GetHTMLContent();
                    readHTMLPages.execute(urlList);
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
            }
        });

        final Button advanceSearch = (Button) rootView.findViewById(R.id.advanceSearchBtn);
        advanceSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if(advanceSearch.getText().toString().compareTo("Advanced Search")==0) {
                    //visible advance search options
                    //TextViews
                    initialsText.setVisibility(View.VISIBLE);
                    nicNoText.setVisibility(View.VISIBLE);
                    addressText.setVisibility(View.VISIBLE);

                    //EditTexts
                    initialsTE.setVisibility(View.VISIBLE);
                    nicNo.setVisibility(View.VISIBLE);
                    addressTE.setVisibility(View.VISIBLE);

                    advanceSearch.setText("Hide Advance Search");
                }else{
                    //hide advance search options
                    //TextViews
                    initialsText.setVisibility(View.INVISIBLE);
                    nicNoText.setVisibility(View.INVISIBLE);
                    addressText.setVisibility(View.INVISIBLE);

                    //EditTexts
                    initialsTE.setVisibility(View.INVISIBLE);
                    nicNo.setVisibility(View.INVISIBLE);
                    addressTE.setVisibility(View.INVISIBLE);

                    advanceSearch.setText("Advanced Search");
                }
            }
        });
        return rootView;
    }

    //Method to check internet connection availability
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    protected class GetHTMLContent extends AsyncTask<ArrayList<String>, Integer, String> {
        String html;

        @Override
        protected String doInBackground(ArrayList<String>... params) {
            ArrayList<String> passedURLs = params[0];

            //Update Progress bar
            publishProgress(0);
            int urlNo = 1;
            for(String url: passedURLs) {
                Document doc = GetHTMLDocFromString(url);

                //Check parsed data has table tag
                if (doc.getElementById("r_table") != null) {
                    //Get number of pages
                    Element resultsNumTag = doc.getElementsByTag("h2").get(0);
                    //get numeric value from string,divide it by 20(one page contains 20 results) and store it inside the integer variable
                    int noOfResults = Integer.parseInt(resultsNumTag.text().replaceAll("[a-zA-Z() ]+", ""));
                    //check noOfResults equals to multiplier of 20 or not (Example like 80)
                    int noOfPages = noOfResults % 20 == 0 ? noOfResults / 20 : (noOfResults / 20) + 1;

                    Element tableContent = doc.getElementById("r_table");
                    Elements tableRows = tableContent.getElementsByTag("tr");

                    //Iterate through webpages
                    for (int i = 0; i < noOfPages; i++) {
                        int skipFirstRow = 0;
                        for (Element currentElement : tableRows) {
                            //Skip first row because it contains headers of table
                            if (skipFirstRow == 0) {
                                skipFirstRow++;
                                continue;
                            }
                            //Get row details
                            Elements tds = currentElement.getElementsByTag("td");

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
                        //Replace the start value of the url to navigate to next page
                        int nextPage = i + 1;
                        if (nextPage != noOfPages) {
                            url = url.replace("start=" + i, "start=" + nextPage);
                            doc = GetHTMLDocFromString(url);
                            tableContent = doc.getElementById("r_table");
                            tableRows = tableContent.getElementsByTag("tr");
                        }
                    }
                }
                else {
                    html = "No data found";
                }
                publishProgress((int) ((urlNo / (float) passedURLs.size()) * 100));
                urlNo++;
            }
            //Update Progress bar
            publishProgress(100);
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
                return doc;
            }catch (IOException ex){
                return null;
            }
        }

        protected void onPostExecute(String result) {
            ProgressBar pBar = (ProgressBar) linearLayoutView.findViewById(R.id.progressShow);
            pBar.setVisibility(View.INVISIBLE);

            if(!searchedDoctors.isEmpty()) {
                txt.setText("Number of doctors found : " + searchedDoctors.size());
                //Collections.sort(searchedDoctors, new DoctorComparator());
                listAdapter.notifyDataSetChanged();
            }
            else{
                txt.setText("No Data Found");
                //To detect reg no is entered or not
                if(searchedRegNo.compareTo("")!=0) {
                    //Detect fake doctor id
                    if(searchedDoctors.isEmpty()) {
                        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                        alertDialog.setTitle("Registration Number (" + searchedRegNo + ") Doesn't Exist!");
                        alertDialog.setMessage("Do you want to report to SLMC?");
                        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ViewPager pager = (ViewPager) getActivity().findViewById(R.id.pager);
                                TabPagerAdapter mAdapter;
                                mAdapter = new TabPagerAdapter(getActivity().getSupportFragmentManager());
                                pager.setAdapter(mAdapter);
                                pager.setCurrentItem(2);
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
            }
        }
        protected void onProgressUpdate(Integer... progress) {
            setProgressPercent(progress[0]);
        }
    }

    private void setProgressPercent(Integer progress) {
        TextView txt = (TextView) linearLayoutView.findViewById(R.id.displayDetails);
        txt.setText("Loading : " + progress +"% Completed");
        ProgressBar pBar = (ProgressBar) linearLayoutView.findViewById(R.id.progressShow);
        pBar.setProgress(progress);
        listAdapter.notifyDataSetChanged();
        //Collections.sort(searchedDoctors,new DoctorComparator());
    }
}

