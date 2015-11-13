package pack.knowyourdoctor.TabControllers;


import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import pack.knowyourdoctor.R;


public class Controller_Fragment_SLMC extends Fragment {
    WebView aboutSLMCWebView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.view_fragment_slmc, container, false);
        aboutSLMCWebView = (WebView) rootView.findViewById(R.id.slmcDetails);
        aboutSLMCWebView.setBackgroundColor(Color.TRANSPARENT);

        //Display about SLMC Text
        //StringBuilder textAboutSLMC = new StringBuilder("<html><body><p align=\"justify\" style=\"color:white\">");
        //textAboutSLMC.append(getString(R.string.about_SLMC));
        //textAboutSLMC.append("</p></body></html>");

        //aboutSLMCWebView.loadData(textAboutSLMC.toString(), "text/html", "utf-8");

        //Set URL link to SLMC Website
        TextView urlView = (TextView) rootView.findViewById(R.id.slmcURL);
        urlView.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.srilankamedicalcouncil.org"));
                startActivity(browserIntent);

            }
        });
        return rootView;
    }

    public void onClick(View v) {

    }

}
