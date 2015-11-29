package pack.knowyourdoctor.MainControllers;

import android.app.Dialog;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;

import org.json.JSONObject;

import java.util.ArrayList;

import Models.DoctorModel;
import Models.HospitalLocationModel;
import Models.RatedDoctorModel;
import WebServiceAccess.WebTask_DoctorListLoad;
import WebServiceAccess.WebTask_ExecutePostRequests;
import WebServiceAccess.WebTask_GetDoctorLocations;
import WebServiceAccess.WebTask_GetHTMLContent;
import WebServiceAccess.WebTask_HospitalListLoad;
import WebServiceAccess.WebTask_RatingListLoad;
import WebServiceAccess.WebTask_RetrieveAnnouncements;
import WebServiceAccess.WebTask_RetrieveForeignUniversities;
import WebServiceAccess.WebTask_SearchHospital;
import pack.knowyourdoctor.ListControllers.Adapter_Comments;
import pack.knowyourdoctor.ListControllers.Adapter_DoctorList;


//Control all web service tasks
public class Controller_WebTasks {
    private WebTask_ExecutePostRequests postRequestTask;
    private WebTask_GetHTMLContent getHTMLTask;
    private WebTask_HospitalListLoad hospitalListLoad;
    private WebTask_RatingListLoad ratingListLoadTask;
    private WebTask_SearchHospital searchHospital;
    private WebTask_DoctorListLoad doctorListLoad;
    private WebTask_GetDoctorLocations getAllLocations;
    private WebTask_RetrieveAnnouncements retrieveAnnouncements;
    private WebTask_RetrieveForeignUniversities retrieveForeignUniversities;

    //Constructor
    public Controller_WebTasks() {
        this.postRequestTask = new WebTask_ExecutePostRequests();
        this.getHTMLTask = new WebTask_GetHTMLContent();
        this.hospitalListLoad = new WebTask_HospitalListLoad();
        this.ratingListLoadTask = new WebTask_RatingListLoad();
        this.searchHospital = new WebTask_SearchHospital();
        this.doctorListLoad = new WebTask_DoctorListLoad();
        this.getAllLocations = new WebTask_GetDoctorLocations();
        this.retrieveAnnouncements = new WebTask_RetrieveAnnouncements();
        this.retrieveForeignUniversities = new WebTask_RetrieveForeignUniversities();
    }

    //Handler for all POST requests
    public void executePostRequestTaks(Context context, String message,
                                       JSONObject jObject, String url) {
        this.postRequestTask.setContext(context);
        this.postRequestTask.setMessage(message);
        this.postRequestTask.setjObject(jObject);
        this.postRequestTask.setUrl(url);
        this.postRequestTask.executeWebTask();
    }

    //Execute get HTML Web request
    public void executeGetHTMLTask(ArrayList<DoctorModel> searchedDoctors,
                                   ProgressBar pBar, TextView txt, Adapter_DoctorList listAdapter,
                                   EditText regNoTE, Context context, String searchedRegNo,
                                   FragmentActivity currentActivity, LinearLayout linearLayoutView,
                                   ArrayList<String> urls) {
        this.getHTMLTask.setSearchedDoctors(searchedDoctors);
        this.getHTMLTask.setpBar(pBar);
        this.getHTMLTask.setTxt(txt);
        this.getHTMLTask.setListAdapter(listAdapter);
        this.getHTMLTask.setRegNoTE(regNoTE);
        this.getHTMLTask.setContext(context);
        this.getHTMLTask.setSearchedRegNo(searchedRegNo);
        this.getHTMLTask.setCurrentActivity(currentActivity);
        this.getHTMLTask.setLinearLayoutView(linearLayoutView);
        this.getHTMLTask.setUrls(urls);
        this.getHTMLTask.executeWebTask();
    }

    //Retrieve all hospitals
    public void executeHospitalListLoadTask(ArrayList<HospitalLocationModel> hospitals,
                                            Spinner hospitalNamesSpinner,
                                            Context context, JSONObject jObject, String url) {
        this.hospitalListLoad.setHospitals(hospitals);
        this.hospitalListLoad.setjObject(jObject);
        this.hospitalListLoad.setUrl(url);
        this.hospitalListLoad.setContext(context);
        this.hospitalListLoad.setHospitalNamesSpinner(hospitalNamesSpinner);
        this.hospitalListLoad.executeWebTask();
    }

    //Retrieve all comments of given doctor
    public void executeRatingListLoadTask(RatedDoctorModel ratedDoc,
                                          Context context, ListView ratedDocComments,
                                          Adapter_Comments listAdapter, EditText newComment,
                                          TextView commentDoctorTextView, Dialog ratingsDialog,
                                          String url, DoctorModel selectedDoctor) {
        this.ratingListLoadTask.setRatedDoc(ratedDoc);
        this.ratingListLoadTask.setContext(context);
        this.ratingListLoadTask.setListAdapter(listAdapter);
        this.ratingListLoadTask.setNewComment(newComment);
        this.ratingListLoadTask.setCommentDoctorTextView(commentDoctorTextView);
        this.ratingListLoadTask.setRatingsDialog(ratingsDialog);
        this.ratingListLoadTask.setUrl(url);
        this.ratingListLoadTask.setSelectedDoctor(selectedDoctor);
        this.ratingListLoadTask.executeWebTask();
    }

    //Retrieve location of searched hospital
    public void executeSearchHospitalTask(Context context, String hospitalName,
                                          GoogleMap googleMap) {
        this.searchHospital.setContext(context);
        this.searchHospital.setHospitalName(hospitalName);
        this.searchHospital.setmMap(googleMap);
        this.searchHospital.executeWebTask();
    }

    //Retrieve doctors who are having submitted locations
    public void executeDoctorListLoadTask(Context context, ArrayList<DoctorModel> _doctorModels,
                                          Spinner spinner, JSONObject jsonObject, String url) {
        this.doctorListLoad.setContext(context);
        this.doctorListLoad.setDoctorList(_doctorModels);
        this.doctorListLoad.setDoctorNamesSpinner(spinner);
        this.doctorListLoad.setjObject(jsonObject);
        this.doctorListLoad.setUrl(url);
        this.doctorListLoad.executeWebTask();
    }

    //Retrieve all doctor locations
    public void executeGetAllLocationsTask(Context context, DoctorModel selectedDoctor,
                                           ArrayList<HospitalLocationModel> hospitals,
                                           JSONObject jsonObject, GoogleMap googleMap, String url) {
        this.getAllLocations.setContext(context);
        this.getAllLocations.setSelectedDoctor(selectedDoctor);
        this.getAllLocations.setHospitals(hospitals);
        this.getAllLocations.setjObject(jsonObject);
        this.getAllLocations.setmMap(googleMap);
        this.getAllLocations.setUrl(url);
        this.getAllLocations.executeWebTask();
    }

    //Retrieve all news in SLMC
    public void executeRetrieveAnnouncementsTask(String url, WebView webView, ProgressBar pBar) {
        this.retrieveAnnouncements.setUrl(url);
        this.retrieveAnnouncements.setWeb(webView);
        this.retrieveAnnouncements.setpBar(pBar);
        this.retrieveAnnouncements.executeWebTask();
    }

    //Retrieve all universities
    public void executeRetrieveForeignUniversitiesTask(String url, ListView uniListView,
                                                       Context context, ProgressBar pBar, TextView noOfUniTE) {
        this.retrieveForeignUniversities.setUrl(url);
        this.retrieveForeignUniversities.setListview(uniListView);
        this.retrieveForeignUniversities.setContext(context);
        this.retrieveForeignUniversities.setpBar(pBar);
        this.retrieveForeignUniversities.setNoOfUniTE(noOfUniTE);
        this.retrieveForeignUniversities.executeWebTask();
    }

}
