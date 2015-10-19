package pack.knowyourdoctor.Tab_Controllers;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

import java.util.ArrayList;

import WebServiceAccess.WebTask_GetHTMLContent;
import pack.knowyourdoctor.Adapters.Adapter_DoctorList;
import Models.Model_Doctor;
import pack.knowyourdoctor.MainControllers.Controller_WebTasks;
import pack.knowyourdoctor.R;
import ValidationRules.RegNoValidation;

public class Controller_Fragment_DoctorDetails extends Fragment {

    Context context;

    TextView txt;
    Adapter_DoctorList listAdapter;
    ExpandableListView listView;

    ArrayList<Model_Doctor> searchedDoctors;

    LinearLayout linearLayoutView;
    EditText regNoTE;

    public static String searchedRegNo;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.view_fragment_doctordetails, container, false);
        context = rootView.getContext();
        //Set object of search page
        //final TextView registryText = (TextView)rootView.findViewById(R.id.registryTextView);
        //report = new View_Fragment_ReportSend();
        //addListener(report);
        final TextView regNoText = (TextView) rootView.findViewById(R.id.regNoTextView);
        final TextView initialsText = (TextView) rootView.findViewById(R.id.initialTextView);
        //final TextView familyNameText = (TextView)rootView.findViewById(R.id.familyNameTextView);
        //final TextView otherNameText = (TextView)rootView.findViewById(R.id.otherNameTextView);
        final TextView nicNoText = (TextView) rootView.findViewById(R.id.nicNoTextView);
        final TextView addressText = (TextView) rootView.findViewById(R.id.addressTextView);

        //final Spinner regSpinner = (Spinner)rootView.findViewById(R.id.registry);
        regNoTE = (EditText) rootView.findViewById(R.id.regNo);
        final EditText initialsTE = (EditText) rootView.findViewById(R.id.initial);
        final EditText familyNameTE = (EditText) rootView.findViewById(R.id.familyName);
        final EditText otherNameTE = (EditText) rootView.findViewById(R.id.otherName);
        final EditText nicNo = (EditText) rootView.findViewById(R.id.docNIC);
        final EditText addressTE = (EditText) rootView.findViewById(R.id.address);

        //Fragment fragment_doctor_details = new Fragment();
        //Bundle bundle = new Bundle();
        //bundle.putString("RegNo", regNoTE.getText().toString());
        //fragment_doctor_details.setArguments(bundle);

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
                for (int i = 3; i < 7; i++) {
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

                //Setup part for display doctor details
                linearLayoutView = (LinearLayout) getActivity().findViewById(R.id.mainView);
                linearLayoutView.removeAllViewsInLayout();
                linearLayoutView.addView(View.inflate(context, R.layout.view_fragment_doctordetails_searched, null));
                linearLayoutView.setBackgroundColor(Color.parseColor("#f1f1f1"));

                //Setup search again button
                Button searchAgainBtn = (Button) linearLayoutView.findViewById(R.id.searchAgain);
                searchAgainBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ViewPager pager = (ViewPager) getActivity().findViewById(R.id.pager);
                        Tab_Controller mAdapter;
                        mAdapter = new Tab_Controller(getActivity().getSupportFragmentManager());
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

                listAdapter = new Adapter_DoctorList(context, searchedDoctors);
                listView.setAdapter(listAdapter);

                if (isNetworkAvailable()) {
                    ProgressBar pBar = (ProgressBar) linearLayoutView.findViewById(R.id.progressShow);
                    //Call controller and execute relevant async task
                    Controller_WebTasks webTaskController = new Controller_WebTasks();
                    webTaskController.executeGetHTMLTask(searchedDoctors, pBar, txt, listAdapter,
                            regNoTE, context, searchedRegNo, getActivity(), linearLayoutView, urlList);

                } else {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                    alertDialog.setTitle("Internet Connection error");
                    alertDialog.setMessage("Do you want to enable the Internet Connection?");
                    alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
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
                if (advanceSearch.getText().toString().compareTo("Advanced Search") == 0) {
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
                } else {
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
}



