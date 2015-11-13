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

import Models.DoctorModel;
import pack.knowyourdoctor.Constants.Numbers;
import pack.knowyourdoctor.Constants.Strings;
import pack.knowyourdoctor.ListControllers.Adapter_DoctorList;
import pack.knowyourdoctor.R;
import pack.knowyourdoctor.TabControllers.Tab_Controller;

//Retrieve HTML content from web site and get doctor details from it
public class WebTask_GetHTMLContent
        extends AsyncTask<ArrayList<String>, Integer, String>
        implements WebTask_Interface {
    String html;
    ArrayList<DoctorModel> searchedDoctors;
    ProgressBar pBar;
    TextView txt;
    Adapter_DoctorList listAdapter;
    EditText regNoTE;
    Context context;
    String searchedRegNo;
    FragmentActivity currentActivity;
    LinearLayout linearLayoutView;
    ArrayList<String> urls;

    //Getters and setters
    public void setSearchedDoctors(ArrayList<DoctorModel> searchedDoctors) {
        this.searchedDoctors = searchedDoctors;
    }

    public void setpBar(ProgressBar pBar) {
        this.pBar = pBar;
    }

    public void setTxt(TextView txt) {
        this.txt = txt;
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

    public void setRegNoTE(EditText regNoTE) {
        this.regNoTE = regNoTE;
    }

    public void setSearchedRegNo(String searchedRegNo) {
        this.searchedRegNo = searchedRegNo;
    }

    public void setCurrentActivity(FragmentActivity currentActivity) {
        this.currentActivity = currentActivity;
    }

    public void setLinearLayoutView(LinearLayout linearLayoutView) {
        this.linearLayoutView = linearLayoutView;
    }

    public void setUrls(ArrayList<String> urls) {
        this.urls = urls;
    }

    //Run in background thread - Execution of web task
    @Override
    protected String doInBackground(ArrayList<String>... params) {
        ArrayList<String> passedURLs = params[0];

        //Update Progress bar
        publishProgress(0);
        int urlNo = 1;
        for (String url : passedURLs) {
            Document doc = GetHTMLDocFromString(url);

            //Check parsed data has table tag
            if (doc.getElementById(Strings.TABLE_ID) != null) {
                //Get number of pages
                Element resultsNumTag = doc.getElementsByTag(Strings.HEADING_TYPE_2).get(0);
                /*get numeric value from string,
                divide it by 20(one page contains 20 results) and
                store it inside the integer variable*/
                int noOfResults = Integer.parseInt(resultsNumTag.text().replaceAll(
                        Strings.REPLACING_REG_EX, Strings.EMPTY_STRING));
                //check noOfResults equals to multiplier of 20 or not (Example like 80)
                int noOfPages = noOfResults % Numbers.PAGE_DOC_LIMIT == Numbers.ZERO ?
                        noOfResults / Numbers.PAGE_DOC_LIMIT :
                        (noOfResults / 20) + Numbers.ONE;

                Element tableContent = doc.getElementById(Strings.TABLE_ID);
                Elements tableRows = tableContent.getElementsByTag(Strings.TABLE_ROW);

                //Iterate through webpages
                for (int i = Numbers.ZERO; i < noOfPages; i++) {
                    int skipFirstRow = Numbers.ZERO;
                    for (Element currentElement : tableRows) {
                        //Skip first row because it contains headers of table
                        if (skipFirstRow == Numbers.ZERO) {
                            skipFirstRow++;
                            continue;
                        }
                        //Get row details
                        Elements tds = currentElement.getElementsByTag(Strings.TABLE_DATA);

                        //Current Doctor
                        DoctorModel currentDoctor = new DoctorModel();
                        //Set reg No
                        currentDoctor.setRegNo(Integer.parseInt(tds.get(Numbers.COLUMN_ONE).text()));

                        //Set reg Date
                        currentDoctor.setRegDate(tds.get(Numbers.COLUMN_TWO).text());

                        //Set reg No
                        currentDoctor.setFullName(tds.get(Numbers.COLUMN_THREE).text());

                        //Set reg No
                        currentDoctor.setAddress(tds.get(Numbers.COLUMN_FOUR).text());

                        //Set reg No
                        currentDoctor.setQualifications(tds.get(Numbers.COLUMN_FIVE).text());

                        searchedDoctors.add(currentDoctor);
                        //Update Progress bar
                    }
                    //Replace the start value of the url to navigate to next page
                    int nextPage = i + Numbers.ONE;
                    if (nextPage != noOfPages) {
                        url = url.replace(Strings.START_WITH_EQUALS + i,
                                Strings.START_WITH_EQUALS + nextPage);
                        doc = GetHTMLDocFromString(url);
                        tableContent = doc.getElementById(Strings.TABLE_ID);
                        tableRows = tableContent.getElementsByTag(Strings.TABLE_ROW);
                    }
                }
            } else {
                html = Strings.EMPTY_STRING;
            }
            publishProgress((int) ((urlNo / (float) passedURLs.size()) * Numbers.HUNDRED));
            urlNo++;
        }
        //Update Progress bar
        publishProgress(Numbers.HUNDRED);
        return html;
    }

    private Document GetHTMLDocFromString(String url) {
        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(url);
        try {
            HttpResponse response = client.execute(request);

            html = Strings.EMPTY_STRING;
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

    //Execute after the web task executed
    @Override
    protected void onPostExecute(String result) {
        pBar.setVisibility(View.INVISIBLE);

        if (!searchedDoctors.isEmpty()) {
            txt.setText(Strings.NO_OF_DOC_TEXT + searchedDoctors.size());
            //Collections.sort(searchedDoctors, new DoctorComparator());
            listAdapter.notifyDataSetChanged();
        } else {
            txt.setText(Strings.NO_DATA_FOUND);
            searchedRegNo = regNoTE.getText().toString();
            //To detect reg no is entered or not
            if (searchedRegNo.compareTo(Strings.EMPTY_STRING) != Numbers.ZERO) {
                //Detect fake doctor id
                if (searchedDoctors.isEmpty()) {
                    final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                    alertDialog.setTitle(
                            Strings.REG_NO_TEXT + searchedRegNo + Strings.DOESNOT_EXISTS);
                    alertDialog.setMessage(Strings.REPORT_TO_SLMC);
                    alertDialog.setPositiveButton(Strings.YES, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ViewPager pager = (ViewPager) currentActivity.findViewById(R.id.fragmentViewer);
                            Tab_Controller mAdapter;
                            pager.setTag(searchedRegNo);
                            mAdapter = new Tab_Controller(currentActivity.getSupportFragmentManager());
                            //mAdapter.fakeRegNo = searchedRegNo;
                            // mAdapter.getItem(2).setArguments(bundle);
                            pager.setAdapter(mAdapter);
                            pager.setCurrentItem(Numbers.TWO);
                        }
                    });
                    alertDialog.setNeutralButton(Strings.NO, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ViewPager pager = (ViewPager) currentActivity.findViewById(R.id.fragmentViewer);
                            Tab_Controller mAdapter;
                            mAdapter = new Tab_Controller(currentActivity.getSupportFragmentManager());
                            pager.setAdapter(mAdapter);
                            pager.setCurrentItem(Numbers.ZERO);
                            searchedDoctors.clear();
                        }
                    });
                    alertDialog.show();
                }
            }
        }
    }

    protected void onProgressUpdate(Integer... progress) {
        setProgressPercent(progress[Numbers.ZERO]);
    }

    private void setProgressPercent(Integer progress) {
        TextView txt = (TextView) linearLayoutView.findViewById(R.id.displayDetails);
        txt.setText(Strings.LOADING + progress + Strings.COMPLETED);
        ProgressBar pBar = (ProgressBar) linearLayoutView.findViewById(R.id.progressShow);
        pBar.setProgress(progress);
        listAdapter.notifyDataSetChanged();
        //Collections.sort(searchedDoctors,new DoctorComparator());
    }

    //Method to start execution of current web task
    @Override
    public void executeWebTask() {
        this.execute(urls);
    }
}
