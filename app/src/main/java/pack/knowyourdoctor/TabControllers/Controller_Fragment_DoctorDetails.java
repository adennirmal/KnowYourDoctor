package pack.knowyourdoctor.TabControllers;

import android.content.Context;
import android.graphics.Color;
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
import android.widget.Toast;

import java.util.ArrayList;

import Models.DoctorModel;
import Services.InternetCheck;
import ValidationRules.RegNoValidation;
import pack.knowyourdoctor.Constants.Strings;
import pack.knowyourdoctor.ListControllers.Adapter_DoctorList;
import pack.knowyourdoctor.MainControllers.Controller_WebTasks;
import pack.knowyourdoctor.R;

//Handle search page of doctors
public class Controller_Fragment_DoctorDetails extends Fragment {
    Context context;
    TextView txt;
    Adapter_DoctorList listAdapter;
    ExpandableListView listView;
    ArrayList<DoctorModel> searchedDoctors;
    LinearLayout linearLayoutView;
    EditText regNoTE;
    public static String searchedRegNo;

    //onCreate method - calls in the initializing the fragment
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.view_fragment_doctordetails,
                container, false);
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
                    regNoTE.setError(Strings.INVALID_REGNO);
                    return;
                }
                //Check internet is enabled or not
                if (InternetCheck.isNetworkAvailable(context)) {
                    ArrayList<String> urlList = new ArrayList<String>();
                    for (int i = 3; i < 7; i++) {
                        //Generate URL for four categories
                        StringBuilder url = new StringBuilder(Strings.URL_TO_SLMC_SEARCH);
                        //nothing selected in spinner
                        url.append(i);

                        //Generate URL
                        url.append(Strings.INITIALS + initialsTE.getText());
                        url.append(Strings.LASTNAME + familyNameTE.getText());
                        url.append(Strings.OTHERNAME + otherNameTE.getText());
                        url.append(Strings.REGNO + regNoTE.getText());
                        url.append(Strings.NIC + nicNo.getText());
                        url.append(Strings.PART_OF_ADDRESS + addressTE.getText());
                        url.append(Strings.SEARCH);
                        //replace all spaces with %20
                        String generatedURL = url.toString().replace(Strings.STRING_WITH_SPACE,
                                Strings.IGNORE_SPACES);

                        urlList.add(generatedURL);
                    }

                    //Setup part for display doctor details
                    linearLayoutView = (LinearLayout) getActivity().findViewById(R.id.mainView);
                    linearLayoutView.removeAllViewsInLayout();
                    linearLayoutView.addView(View.inflate(context,
                            R.layout.view_fragment_doctordetails_searched, null));
                    linearLayoutView.setBackgroundColor(Color.parseColor(Strings.WHITE_COLOR));

                    //Setup search again button
                    Button searchAgainBtn = (Button) linearLayoutView.findViewById(R.id.searchAgain);
                    searchAgainBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ViewPager pager = (ViewPager) getActivity().findViewById(R.id.fragmentViewer);
                            Tab_Controller mAdapter;
                            mAdapter = new Tab_Controller(getActivity().getSupportFragmentManager());
                            pager.setAdapter(mAdapter);
                            pager.setCurrentItem(0);
                            searchedDoctors.clear();
                        }
                    });

                    //Load Searched Doctor Details
                    txt = (TextView) linearLayoutView.findViewById(R.id.displayDetails);
                    txt.setText(Strings.LOADING_ALERT);

                    listView = (ExpandableListView) rootView.findViewById(R.id.expList);

                    searchedDoctors = new ArrayList<DoctorModel>();

                    listAdapter = new Adapter_DoctorList(context, searchedDoctors);
                    listView.setAdapter(listAdapter);


                    ProgressBar pBar = (ProgressBar) linearLayoutView.findViewById(R.id.progressShow);
                    //Call controller and execute relevant async task
                    Controller_WebTasks webTaskController = new Controller_WebTasks();
                    webTaskController.executeGetHTMLTask(searchedDoctors, pBar, txt, listAdapter,
                            regNoTE, context, searchedRegNo, getActivity(), linearLayoutView, urlList);

                } else {
                    /*AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                    alertDialog.setTitle(Strings.INTERNET_CONNECTION_ERROR);
                    alertDialog.setMessage(Strings.ENABLE_INTERNET_CONNECTION);
                    alertDialog.setPositiveButton(Strings.YES, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    alertDialog.setNeutralButton(Strings.NO, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    alertDialog.show();*/
                    Toast.makeText(context, "Sorry! please turn on your internet connection", Toast.LENGTH_LONG).show();
                }
            }
        });

        final Button advanceSearch = (Button) rootView.findViewById(R.id.advanceSearchBtn);
        advanceSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (advanceSearch.getText().toString().compareTo(Strings.ADVANCED_SEARCH) == 0) {
                    //visible advance search options
                    //TextViews
                    initialsText.setVisibility(View.VISIBLE);
                    nicNoText.setVisibility(View.VISIBLE);
                    addressText.setVisibility(View.VISIBLE);

                    //EditTexts
                    initialsTE.setVisibility(View.VISIBLE);
                    nicNo.setVisibility(View.VISIBLE);
                    addressTE.setVisibility(View.VISIBLE);

                    advanceSearch.setText(Strings.HIDE_ADVANCED_SEARCH);
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

                    advanceSearch.setText(Strings.ADVANCED_SEARCH);
                }
            }
        });
        return rootView;
    }
}



