package pack.knowyourdoctor.TabControllers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import Services.InternetCheck;
import ValidationRules.ContactNoValidation;
import ValidationRules.NICValidation;
import ValidationRules.NameValidation;
import ValidationRules.RequiredFieldValidation;
import WebServiceAccess.WebTask_ExecutePostRequests;
import pack.knowyourdoctor.Constants.Strings;
import pack.knowyourdoctor.R;
import ValidationRules.RegNoValidation;

//Fake Doctor Details handle fragment
public class Controller_Fragment_ReportSend extends Fragment {
    Context context;
    EditText dreg;
    EditText name;
    EditText contact;
    EditText dname;
    EditText comment;
    String fakeRegNo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        final View rootView = inflater.inflate(R.layout.view_fragment_reportsend, container, false);
        context = rootView.getContext();
        dreg = (EditText) rootView.findViewById(R.id.d_reg);

        try {
            fakeRegNo = container.getTag().toString();
        } catch (NullPointerException e) {
            fakeRegNo = Strings.EMPTY_STRING;
        }
        dreg.setText(fakeRegNo);

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

                //Validation
                boolean isValid = true;
                if (!NameValidation.isValidName(name.getText().toString())) {
                    name.setError(Strings.INVALID_REPORTER_NAME);
                    isValid = false;
                }

                /*if(!NICValidation.isValidNIC(nic.getText().toString())){
                    nic.setError("Please enter your NIC number");
                    isValid = false;
                }

                if (!ContactNoValidation.isValidContactNo(contact.getText().toString())) {
                    contact.setError("Please enter your contact number as XXX-XXXXXXX");
                    isValid = false;
                }*/

                if (!RegNoValidation.isValidRegNoWithRequiredValidation(dreg.getText().toString())) {
                    dreg.setError(Strings.INVALID_FAKE_REGNO);
                    isValid = false;
                }

                if (!NameValidation.isValidName(dname.getText().toString())) {
                    dname.setError(Strings.INVALID_FAKE_DOC_NAME);
                    isValid = false;
                }

                /*
                if(!NICValidation.isValidNIC(dnicno.getText().toString())){
                    dnicno.setError("Please enter fake doctor's NIC number");
                    isValid = false;
                }

                if(RequiredFieldValidation.isEmpty(dcontact.getText().toString())){
                    dcontact.setError("Please enter fake doctor's address");
                    isValid = false;
                }*/

                if (isValid) {
                    //Check internet is enabled or not
                    if (InternetCheck.isNetworkAvailable(context)) {
                        final String reportUserName = name.getText().toString();
                        //String reportUserNIC = nic.getText().toString();
                        final String reportContact = contact.getText().toString();

                        final String reportDoctorRegNo = dreg.getText().toString();
                        final String reportDoctorName = dname.getText().toString();
                        final String reportComment = comment.getText().toString();

                        //Send fake doctor details to webservice
                        String baseURL = context.getResources().getString(R.string.webserviceLink);
                        StringBuilder url = new StringBuilder(baseURL);
                        url.append(Strings.INSERT_FAKE_DOC_DETAILS);
                        JSONObject fakeDocJSONObj = new JSONObject();
                        try {
                            fakeDocJSONObj.put(Strings.JSON_FAKEDOC_ID_TEXT, reportDoctorRegNo);
                            fakeDocJSONObj.put(Strings.JSON_FAKEDOC_NAME_TEXT, reportDoctorName);
                            fakeDocJSONObj.put(Strings.JSON_REPORTED_PERSON_TEXT, reportUserName);
                            fakeDocJSONObj.put(Strings.JSON_REPORTED_CONTACT_TEXT, reportContact);
                            fakeDocJSONObj.put(Strings.JSON_COMMENT_TEXT, reportComment);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        WebTask_ExecutePostRequests ratingTask = new WebTask_ExecutePostRequests();
                        ratingTask.setContext(context);
                        ratingTask.setMessage(Strings.THANKING_TEXT);
                        ratingTask.setjObject(fakeDocJSONObj);
                        // passes values for the urls string array
                        ratingTask.execute(url.toString());

                        //Send email
                        Intent i = new Intent(Intent.ACTION_SEND);
                        i.setType(Strings.MSG_TYPE);
                        i.putExtra(Intent.EXTRA_EMAIL, new String[]{Strings.RECEIVER_MAIL_ADD});
                        i.putExtra(Intent.EXTRA_SUBJECT, Strings.EMAIL_SUBJECT);

                        //Format the email body to send
                        String newline = System.getProperty(Strings.LINE_SEPERATOR);
                        StringBuilder emailBody = new StringBuilder(Strings.EMPTY_STRING);
                        emailBody.append(Strings.REPORTED_USER_HEADING + newline);
                        emailBody.append(Strings.HORIZONTAL_SEPERATOR + newline);
                        emailBody.append(Strings.FULLNAME_TEXT + reportUserName + newline);
                        emailBody.append(Strings.CONTACTNO_TEXT + reportContact + newline + newline);

                        emailBody.append(Strings.FAKE_DOCTOR_HEADING + newline);
                        emailBody.append(Strings.HORIZONTAL_SEPERATOR + newline);
                        emailBody.append(Strings.FAKE_REGNO_TEXT + reportDoctorRegNo + newline);
                        emailBody.append(Strings.FULLNAME_TEXT + reportDoctorName + newline);
                        emailBody.append(Strings.COMMENT_TEXT + reportComment + newline);

                        i.putExtra(Intent.EXTRA_TEXT, emailBody.toString());

                        try {
                            startActivity(Intent.createChooser(i, Strings.SENDING_MSG));
                        } catch (android.content.ActivityNotFoundException ex) {
                            Toast.makeText(context, Strings.NO_EMAIL_CLIENT_MSG, Toast.LENGTH_SHORT).show();
                        }
                        dreg.setText(Strings.EMPTY_STRING);
                        name.setText(Strings.EMPTY_STRING);
                        contact.setText(Strings.EMPTY_STRING);
                        dname.setText(Strings.EMPTY_STRING);
                        comment.setText(Strings.EMPTY_STRING);
                    } else {
                        Toast.makeText(context, "Sorry! please turn on your internet connection", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (fakeRegNo == null) {
            fakeRegNo = Strings.EMPTY_STRING;
        }
        dreg.setText(fakeRegNo);
    }
}