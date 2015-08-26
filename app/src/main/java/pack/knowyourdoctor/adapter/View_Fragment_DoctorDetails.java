package pack.knowyourdoctor.adapter;

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
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import pack.knowyourdoctor.R;
import pack.knowyourdoctor.Validators.RegNoValidation;
import pack.knowyourdoctor.View_Home;

public class View_Fragment_DoctorDetails extends Fragment {

    Context context;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.view_fragment_doctordetails, container, false);
        context = rootView.getContext();
        //Set object of search page
        final TextView registryText = (TextView)rootView.findViewById(R.id.registryTextView);
        final TextView regNoText = (TextView)rootView.findViewById(R.id.regNoTextView);
        final TextView initialsText = (TextView)rootView.findViewById(R.id.initialTextView);
        final TextView familyNameText = (TextView)rootView.findViewById(R.id.familyNameTextView);
        final TextView otherNameText = (TextView)rootView.findViewById(R.id.otherNameTextView);
        final TextView nicNoText = (TextView)rootView.findViewById(R.id.nicNoTextView);
        final TextView addressText = (TextView)rootView.findViewById(R.id.addressTextView);

        final Spinner regSpinner = (Spinner)rootView.findViewById(R.id.registry);
        final EditText regNoTE = (EditText)rootView.findViewById(R.id.regNo);
        final EditText initialsTE = (EditText)rootView.findViewById(R.id.initial);
        final EditText familyNameTE = (EditText)rootView.findViewById(R.id.familyName);
        final EditText otherNameTE = (EditText)rootView.findViewById(R.id.otherName);
        final EditText nicNo = (EditText)rootView.findViewById(R.id.docNIC);
        final EditText addressTE = (EditText)rootView.findViewById(R.id.address);

        //final LinearLayout layout = (LinearLayout)rootView.findViewById(R.id.middleSemiColon);

        //hide advance search options
        //TextViews
        familyNameText.setVisibility(View.INVISIBLE);
        otherNameText.setVisibility(View.INVISIBLE);
        nicNoText.setVisibility(View.INVISIBLE);
        addressText.setVisibility(View.INVISIBLE);

        //EditTexts
        familyNameTE.setVisibility(View.INVISIBLE);
        otherNameTE.setVisibility(View.INVISIBLE);
        nicNo.setVisibility(View.INVISIBLE);
        addressTE.setVisibility(View.INVISIBLE);

        //layout.setVisibility(View.INVISIBLE);

        Button button = (Button) rootView.findViewById(R.id.search);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                //To format index to compatible with url
                int selectedIndex = regSpinner.getSelectedItemPosition();

                if(selectedIndex==0){
                    TextView tvRegError = (TextView)rootView.findViewById(R.id.regSpinnerError);
                    tvRegError.requestFocus();
                    tvRegError.setError("Please Select a value for Registry");
                    View selectedView = regSpinner.getSelectedView();
                    TextView selectedTextView = (TextView) selectedView;
                    selectedTextView.setError("");
                    return;
                }
                //validate
                if (!RegNoValidation.isValidRegNo(regNoTE.getText().toString())) {
                    regNoTE.setError("Invalid Registration Number");
                    return;
                }

                //Generate URL
                StringBuilder url = new StringBuilder("http://www.srilankamedicalcouncil.org/registry.php?start=0&registry=");
                //nothing selected in spinner
                if(selectedIndex!=0) {
                    selectedIndex += 2;
                    url.append(selectedIndex);
                }

                //Generate URL
                url.append("&initials="+initialsTE.getText());
                url.append("&last_name="+familyNameTE.getText());
                url.append("&other_name="+otherNameTE.getText());
                url.append("&reg_no="+regNoTE.getText());
                url.append("&nic=" + nicNo.getText());
                url.append("&part_of_address="+addressTE.getText());
                url.append("&search=Search");
                //replace all spaces with %20
                String generatedURL = url.toString().replace(" ","%20");

                View_Home.urlBundle.putString("url", generatedURL);

                //TabPagerAdapter.url = generatedURL;

                ViewPager pager = (ViewPager)((View_Home)getActivity()).findViewById(R.id.pager);
                pager.setCurrentItem(8);
                pager.setBackgroundColor(Color.parseColor("#f1f1f1"));

                //View abc = inflater.inflate(R.layout.view_fragment_doctordetails_searched, container, false);
                //pager.addView(abc);

                /*View_Fragment_DoctorDetails_Searched fragment2 = new View_Fragment_DoctorDetails_Searched();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.pager, fragment2);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();*/
            }
        });


        final Button advanceSearch = (Button) rootView.findViewById(R.id.advanceSearchBtn);
        advanceSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if(advanceSearch.getText().toString().compareTo("Advanced Search")==0) {
                    //visible advance search options
                    //TextViews
                    familyNameText.setVisibility(View.VISIBLE);
                    otherNameText.setVisibility(View.VISIBLE);
                    nicNoText.setVisibility(View.VISIBLE);
                    addressText.setVisibility(View.VISIBLE);

                    //EditTexts
                    familyNameTE.setVisibility(View.VISIBLE);
                    otherNameTE.setVisibility(View.VISIBLE);
                    nicNo.setVisibility(View.VISIBLE);
                    addressTE.setVisibility(View.VISIBLE);

                    //layout.setVisibility(View.VISIBLE);
                    advanceSearch.setText("Hide Advance Search");
                }else{
                    //hide advance search options
                    //TextViews
                    familyNameText.setVisibility(View.INVISIBLE);
                    otherNameText.setVisibility(View.INVISIBLE);
                    nicNoText.setVisibility(View.INVISIBLE);
                    addressText.setVisibility(View.INVISIBLE);

                    //EditTexts
                    familyNameTE.setVisibility(View.INVISIBLE);
                    otherNameTE.setVisibility(View.INVISIBLE);
                    nicNo.setVisibility(View.INVISIBLE);
                    addressTE.setVisibility(View.INVISIBLE);

                    //layout.setVisibility(View.INVISIBLE);
                    advanceSearch.setText("Advanced Search");
                }
            }
        });
        return rootView;
    }
}

