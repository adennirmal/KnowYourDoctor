package WebServiceAccess;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

import Models.UniversityModel;
import pack.knowyourdoctor.ListControllers.Adapter_UniversityList;

//Retrieve all foreign universities
public class WebTask_RetrieveForeignUniversities
        extends AsyncTask<String, Void, String>
        implements WebTask_Interface {
    private ArrayList universityList;
    private String url;
    private ListView listview;
    private Context context;
    private ProgressBar pBar;
    private TextView noOfUniTE;

    public void setListview(ListView listview) {
        this.listview = listview;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    public void setContext(Context context) {
        this.context = context;
    }

    public void setpBar(ProgressBar pBar) {
        this.pBar = pBar;
    }

    public void setNoOfUniTE(TextView noOfUniTE) {
        this.noOfUniTE = noOfUniTE;
    }

    @Override
    protected String doInBackground(String... params) {
        // Create an array
        universityList = new ArrayList<UniversityModel>();

        try {
            // Connect to the Website URL
            Document doc = Jsoup.connect(url).get();
            // Identify Table id "fu_table"
            for (Element table : doc.select("table[id=fu_table]")) {

                // Identify all the table row's(tr)
                for (Element row : table.select("tr:gt(0)")) {
                    // Identify all the table cell's(td)
                    Elements tds = row.select("td");
                    // Get the first, second and third td

                    UniversityModel currentUni = new UniversityModel();
                    currentUni.setName(tds.get(0).text());
                    currentUni.setAddress(tds.get(1).text());
                    currentUni.setQualification(tds.get(2).text());
                    universityList.add(currentUni);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        pBar.setVisibility(View.INVISIBLE);
        // Pass the results
        Adapter_UniversityList adapter = new Adapter_UniversityList(context, universityList);
        noOfUniTE.setText("Number of Universities Found : " + universityList.size());
        listview.setAdapter(adapter);
    }

    @Override
    public void executeWebTask() {
        this.execute(url);
    }
}
