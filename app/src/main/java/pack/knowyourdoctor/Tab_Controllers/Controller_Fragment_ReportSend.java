package pack.knowyourdoctor.Tab_Controllers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import WebServiceAccess.WebTask_ExecutePostRequests;
import pack.knowyourdoctor.MainControllers.Controller_Home;
import pack.knowyourdoctor.R;
import ValidationRules.RegNoValidation;

public class Controller_Fragment_ReportSend extends Fragment {
    Context context;
    private String fakeRegNo;
    EditText dreg;
    EditText name;
    EditText contact;
    EditText dname;
    EditText comment;

    String regNo;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        final View rootView = inflater.inflate(R.layout.view_fragment_reportsend, container, false);
        context = rootView.getContext();
        dreg = (EditText) rootView.findViewById(R.id.d_reg);

        //String regNo = View_Fragment_DoctorDetails.getRegNo();
        //String regNo;
        if (regNo == null) {
            regNo = "";
        } else {
            try {
                regNo = container.getTag().toString();
            } catch (NullPointerException e) {
                regNo = "";
            }
        }
        dreg.setText(regNo);

        final Button send = (Button) rootView.findViewById(R.id.btnsubmit);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = (EditText) rootView.findViewById(R.id.fullname);
                //final EditText nic = (EditText)rootView.findViewById(R.id.nic_no);
                contact = (EditText) rootView.findViewById(R.id.contact);

                dname = (EditText) rootView.findViewById(R.id.d_name);
                comment = (EditText) rootView.findViewById(R.id.comment);
                //final EditText dcontact = (EditText)rootView.findViewById(R.id.d_contact);

                //name.setHint(Html.fromHtml("<font size=\"16\">" + "Your Name" + "</font>"));

                //Validation
                boolean isValid = true;
                //if (RequiredFieldValidation.isEmpty(name.getText().toString())) {
                //  name.setError("Please enter your full name");
                //isValid = false;
                //}

                /*if(!NICValidation.isValidNIC(nic.getText().toString())){
                    nic.setError("Please enter your NIC number");
                    isValid = false;
                }*/

                //if (!ContactNoValidation.isValidContactNo(contact.getText().toString())) {
                //    contact.setError("Please enter your contact number as XXX-XXXXXXX");
                //    isValid = false;
                // }

                if (!RegNoValidation.isValidRegNoWithRequiredValidation(dreg.getText().toString())) {
                    dreg.setError("Please enter fake doctor's registration number");
                    isValid = false;
                }

                //if (RequiredFieldValidation.isEmpty(dname.getText().toString())) {
                //    dname.setError("Please enter fake doctor's name");
                //    isValid = false;
                //}

                /*if(!NICValidation.isValidNIC(dnicno.getText().toString())){
                    dnicno.setError("Please enter fake doctor's NIC number");
                    isValid = false;
                }

                if(RequiredFieldValidation.isEmpty(dcontact.getText().toString())){
                    dcontact.setError("Please enter fake doctor's address");
                    isValid = false;
                }*/

                if (isValid) {
                    final String reportUserName = name.getText().toString();
                    //String reportUserNIC = nic.getText().toString();
                    final String reportContact = contact.getText().toString();

                    final String reportDoctorRegNo = dreg.getText().toString();
                    final String reportDoctorName = dname.getText().toString();
                    final String reportComment = comment.getText().toString();

                    String newline = System.getProperty("line.separator");
                    StringBuilder emailBody = new StringBuilder("");
                    emailBody.append("Reported User Details" + newline);
                    emailBody.append("------------------------------" + newline);
                    emailBody.append("Fullname:" + reportUserName + newline);
                    emailBody.append("Contact No:" + reportContact + newline + newline);

                    emailBody.append("Fake Doctor Details" + newline);
                    emailBody.append("------------------------------" + newline);
                    emailBody.append("Registration No:" + reportDoctorRegNo + newline);
                    emailBody.append("Fullname:" + reportDoctorName + newline);
                    emailBody.append("Comment:" + reportComment + newline);

                    //Send fake doctor details to webservice
                    String baseURL = context.getResources().getString(R.string.webserviceLink);
                    StringBuilder url = new StringBuilder(baseURL);
                    url.append("PhoneAppControllers/FakeDoctorReportController/insertFakeDoctorReport/");
                    JSONObject fakeDocJSONObj = new JSONObject();
                    try {
                        fakeDocJSONObj.put("fakeDocID", reportDoctorRegNo);
                        fakeDocJSONObj.put("fakeDocName", reportDoctorName);
                        fakeDocJSONObj.put("reportedPerson", reportUserName);
                        fakeDocJSONObj.put("contactNo", reportContact);
                        fakeDocJSONObj.put("comment", reportComment);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    WebTask_ExecutePostRequests ratingTask = new WebTask_ExecutePostRequests();
                    ratingTask.setContext(context);
                    ratingTask.setMessage("Thanks for your report!!");
                    ratingTask.setjObject(fakeDocJSONObj);
                    // passes values for the urls string array
                    ratingTask.execute(url.toString());

                    //Send email
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("message/rfc822");
                    i.putExtra(Intent.EXTRA_EMAIL, new String[]{"knowyourdoctorslmc@gmail.com"});
                    i.putExtra(Intent.EXTRA_SUBJECT, "Fake doctor Details");
                    i.putExtra(Intent.EXTRA_TEXT, emailBody.toString());
                    try {
                        startActivity(Intent.createChooser(i, "Send mail..."));
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(context, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                    }
                    dreg.setText("");
                    name.setText("");
                    contact.setText("");
                    dname.setText("");
                    comment.setText("");
                }
            }
        });
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (regNo == null) {
            regNo = "";
        }
        dreg.setText(regNo);
    }
}