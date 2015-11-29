package WebServiceAccess;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.view.View;
import android.webkit.WebView;
import android.widget.ProgressBar;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

//Retrieve all announcements from SLMC site
public class WebTask_RetrieveAnnouncements
        extends AsyncTask<String, Void, String>
        implements WebTask_Interface {
    private String desc;
    private String url;
    private WebView web;
    ProgressBar pBar;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public WebView getWeb() {
        return web;
    }

    public void setWeb(WebView web) {
        this.web = web;
    }

    public void setpBar(ProgressBar pBar) {
        this.pBar = pBar;
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            // Connect to the web site
            Document document = Jsoup.connect(url).get();
            // Using Elements to get the div data
            Elements description = document.select("div[class=page-general-text-container]");
            // Get the data in html format
            desc = description.toString();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        //Convert html data to text view
        web.loadData(desc, "text/html", "utf-8");
        pBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void executeWebTask() {
        this.execute(url);
    }
}

