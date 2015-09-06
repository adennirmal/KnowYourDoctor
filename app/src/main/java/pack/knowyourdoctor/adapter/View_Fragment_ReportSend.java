package pack.knowyourdoctor.adapter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import pack.knowyourdoctor.GmailSender;
import pack.knowyourdoctor.R;
import pack.knowyourdoctor.Validators.ContactNoValidation;
import pack.knowyourdoctor.Validators.NICValidation;
import pack.knowyourdoctor.Validators.RegNoValidation;
import pack.knowyourdoctor.Validators.RequiredFieldValidation;
import pack.knowyourdoctor.View_Home;
import pack.knowyourdoctor.View_ShowDoctorDetails;

public class View_Fragment_ReportSend extends Fragment {

    private String fakeRegNo;
    EditText dreg;
    String regNo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.view_fragment_reportsend, container, false);

        dreg = (EditText)rootView.findViewById(R.id.d_reg);

        //String regNo = View_Fragment_DoctorDetails.getRegNo();
        //String regNo;
        if (regNo == null)
        {
            regNo = "";
        }
        else
        {
            regNo = container.getTag().toString();
        }
        dreg.setText(regNo);

        final Button send = (Button)rootView.findViewById(R.id.btnsubmit);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText name = (EditText) rootView.findViewById(R.id.fullname);
                //final EditText nic = (EditText)rootView.findViewById(R.id.nic_no);
                final EditText contact = (EditText) rootView.findViewById(R.id.contact);

                final EditText dname = (EditText) rootView.findViewById(R.id.d_name);
                final EditText comment = (EditText)rootView.findViewById(R.id.comment);
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
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String reportUserName = name.getText().toString();
                            //String reportUserNIC = nic.getText().toString();
                            String reportContact = contact.getText().toString();

                            String reportDoctorRegNo = dreg.getText().toString();
                            String reportDoctorName = dname.getText().toString();
                            String reportComment = comment.getText().toString();
                            //String reportDoctorAddress = dcontact.getText().toString();

                            GmailSender sender = new GmailSender("knowyourdoctorslmc@gmail.com", "xc45A&qw7!b1");
                            String newline = System.getProperty("line.separator");
                            StringBuilder emailBody = new StringBuilder("");
                            emailBody.append("Reported User Details" + newline);
                            emailBody.append("------------------------------" + newline);
                            emailBody.append("Fullname:" + reportUserName + newline);
                            //emailBody.append("NIC No:" + reportUserNIC + newline);
                            emailBody.append("Contact No:" + reportContact + newline + newline);

                            emailBody.append("Fake Doctor Details" + newline);
                            emailBody.append("------------------------------" + newline);
                            emailBody.append("Registration No:" + reportDoctorRegNo + newline);
                            emailBody.append("Fullname:" + reportDoctorName + newline);
                            emailBody.append("Comment:" + reportComment + newline);
                            //emailBody.append("Address: " + reportDoctorAddress);
                            try {
                                sender.sendMail("Fake doctor Details", emailBody.toString(), "knowyourdoctorslmc@gmail.com", "yasipiraba@gmail.com");
                            } catch (Exception e) {
                                Log.e("SendMail", e.getMessage(), e);
                                System.out.println(e);
                            }
                        }
                    });
                    thread.start();
                    Toast.makeText(getActivity().getApplication().getApplicationContext(), "Report Submitted Successfully!", Toast.LENGTH_LONG).show();
                }
            }
        });
        return rootView;
    }

    @Override
    public void onResume() {
        if (regNo == null)
        {
            regNo = "";
        }
        dreg.setText(regNo);
        super.onResume();
    }


}