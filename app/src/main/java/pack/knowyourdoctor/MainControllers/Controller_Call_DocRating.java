package pack.knowyourdoctor.MainControllers;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import Models.Model_Doctor;
import WebServiceAccess.WebTask_ExecutePostRequests;
import pack.knowyourdoctor.R;

public class Controller_Call_DocRating {
    public void executeRatingAndCommentTask(Model_Doctor selectedDoctor, String comment, Context con, String toastMessage) {
        String baseURL = con.getResources().getString(R.string.webserviceLink);
        StringBuilder url = new StringBuilder(baseURL);
        url.append("PhoneAppControllers/DoctorRatingController/insertNewRating/");
        JSONObject docJSONObj = new JSONObject();
        try {
            docJSONObj.put("docID", selectedDoctor.getRegNo());
            docJSONObj.put("docName", selectedDoctor.getFullName());
            docJSONObj.put("docAddress", selectedDoctor.getAddress());
            docJSONObj.put("docRegDate", selectedDoctor.getRegDate());
            docJSONObj.put("docQualifications", selectedDoctor.getQualifications());
            docJSONObj.put("comment", comment);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        WebTask_ExecutePostRequests ratingTask = new WebTask_ExecutePostRequests();
        ratingTask.setContext(con);
        ratingTask.setMessage(toastMessage);
        ratingTask.setjObject(docJSONObj);
        // passes values for the urls string array
        ratingTask.execute(url.toString());
    }
}