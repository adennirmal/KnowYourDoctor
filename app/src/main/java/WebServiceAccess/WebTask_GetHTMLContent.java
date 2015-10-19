package WebServiceAccess;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.EditText;
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

import Models.Model_Doctor;
import pack.knowyourdoctor.Adapters.Adapter_DoctorList;
import pack.knowyourdoctor.R;
import pack.knowyourdoctor.Tab_Controllers.Tab_Controller;

/**
 * Created by Home on 10/19/2015.
 */
public class WebTask_GetHTMLContent extends AsyncTask<ArrayList<String>, Integer, String> implements WebTask_Interface {
    String html;
    ArrayList<Model_Doctor> searchedDoctors;
    ProgressBar pBar;
    TextView txt;
    Adapter_DoctorList listAdapter;
    EditText regNoTE;
    Context context;
    String searchedRegNo;
    FragmentActivity currentActivity;
    LinearLayout linearLayoutView;
    ArrayList<String> urls;

    public ArrayList<Model_Doctor> getSearchedDoctors() {
        return searchedDoctors;
    }

    public void setSearchedDoctors(ArrayList<Model_Doctor> searchedDoctors) {
        this.searchedDoctors = searchedDoctors;
    }

    public ProgressBar getpBar() {
        return pBar;
    }

    public void setpBar(ProgressBar pBar) {
        this.pBar = pBar;
    }

    public TextView getTxt() {
        return txt;
    }

    public void setTxt(TextView txt) {
        this.txt = txt;
    }

    public Adapter_DoctorList getListAdapter() {
        return listAdapter;
    }

    public void setListAdapter(Adapter_DoctorList listAdapter) {
        this.listAdapter = listAdapter;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public EditText getRegNoTE() {
        return regNoTE;
    }

    public void setRegNoTE(EditText regNoTE) {
        this.regNoTE = regNoTE;
    }

    public String getSearchedRegNo() {
        return searchedRegNo;
    }

    public void setSearchedRegNo(String searchedRegNo) {
        this.searchedRegNo = searchedRegNo;
    }

    public FragmentActivity getCurrentActivity() {
        return currentActivity;
    }

    public void setCurrentActivity(FragmentActivity currentActivity) {
        this.currentActivity = currentActivity;
    }

    public LinearLayout getLinearLayoutView() {
        return linearLayoutView;
    }

    public void setLinearLayoutView(LinearLayout linearLayoutView) {
        this.linearLayoutView = linearLayoutView;
    }

    public ArrayList<String> getUrls() {
        return urls;
    }

    public void setUrls(ArrayList<String> urls) {
        this.urls = urls;
    }

    @Override
    protected String doInBackground(ArrayList<String>... params) {
        ArrayList<String> passedURLs = params[0];

        //Update Progress bar
        publishProgress(0);
        int urlNo = 1;
        for (String url : passedURLs) {
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
            } else {
                html = "No data found";
            }
            publishProgress((int) ((urlNo / (float) passedURLs.size()) * 100));
            urlNo++;
        }
        //Update Progress bar
        publishProgress(100);
        return html;
    }

    private Document GetHTMLDocFromString(String url) {
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
        } catch (IOException ex) {
            return null;
        }
    }

    protected void onPostExecute(String result) {
        pBar.setVisibility(View.INVISIBLE);

        if (!searchedDoctors.isEmpty()) {
            txt.setText("Number of doctors found : " + searchedDoctors.size());
            //Collections.sort(searchedDoctors, new DoctorComparator());
            listAdapter.notifyDataSetChanged();
        } else {
            txt.setText("No Data Found");
            searchedRegNo = regNoTE.getText().toString();
            //To detect reg no is entered or not
            if (searchedRegNo.compareTo("") != 0) {
                //Detect fake doctor id
                if (searchedDoctors.isEmpty()) {
                    final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                    alertDialog.setTitle("Registration Number (" + searchedRegNo + ") Doesn't Exist!");
                    alertDialog.setMessage("Do you want to report to SLMC?");
                    alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ViewPager pager = (ViewPager) currentActivity.findViewById(R.id.pager);
                            Tab_Controller mAdapter;
                            pager.setTag(searchedRegNo);
                            mAdapter = new Tab_Controller(currentActivity.getSupportFragmentManager());
                            //mAdapter.fakeRegNo = searchedRegNo;
                            // mAdapter.getItem(2).setArguments(bundle);
                            pager.setAdapter(mAdapter);
                            pager.setCurrentItem(2);
                        }
                    });
                    alertDialog.setNeutralButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ViewPager pager = (ViewPager) currentActivity.findViewById(R.id.pager);
                            Tab_Controller mAdapter;
                            mAdapter = new Tab_Controller(currentActivity.getSupportFragmentManager());
                            pager.setAdapter(mAdapter);
                            pager.setCurrentItem(0);
                            searchedDoctors.clear();
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

    private void setProgressPercent(Integer progress) {
        TextView txt = (TextView) linearLayoutView.findViewById(R.id.displayDetails);
        txt.setText("Loading : " + progress + "% Completed");
        ProgressBar pBar = (ProgressBar) linearLayoutView.findViewById(R.id.progressShow);
        pBar.setProgress(progress);
        listAdapter.notifyDataSetChanged();
        //Collections.sort(searchedDoctors,new DoctorComparator());
    }

    @Override
    public void executeWebTask() {
        this.execute(urls);
    }
}
