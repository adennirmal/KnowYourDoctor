package pack.knowyourdoctor.MainControllers;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
//import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ogaclejapan.arclayout.ArcLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import Models.Model_Doctor;
import Models.Model_GlobalValues;
import WebServiceAccess.WebTask_ExecutePostRequests;
import pack.knowyourdoctor.AnimationPack.AnimatorUtils;
import pack.knowyourdoctor.R;
import pack.knowyourdoctor.Tab_Controllers.Tab_Controller;


public class Controller_Home extends FragmentActivity implements View.OnClickListener, ViewPager.OnPageChangeListener, ActionBar.TabListener, LocationListener {
    private Context context;
    View mFab;
    View mMenuLayout;
    ArcLayout mArcLayout;
    ViewPager viewDisplay;
    Button docDetailsBTN;
    Button reportFakeDocBTN;
    Button locationDetailBTN;
    Button likeDocDetailsBTN;
    Button faceBookShareBTN;
    Button aboutSLMCBTN;
    Button aboutDevelopersBTN;
    private Tab_Controller mAdapter;
    private ActionBar actionBar;
    LocationManager locationManager;
    Location location;
    String provider;
    Dialog fragmentsDialog;

    Button lastSelectedButton;

    public static Bundle urlBundle;

    // Tab titles
    ////private String[] tabs = { "Search", "Locate Doctor","Report","FB","SLMC","About Us"};
    final int[] icons = {R.drawable.search_selector, R.drawable.location_selector, R.drawable.report_selector, R.drawable.fb_selector};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_home);

        //prevent app stop
        urlBundle = new Bundle();
        String url = getResources().getString(R.string.slmc_url);
        urlBundle.putString("url", url);

        mFab = findViewById(R.id.fab);
        mMenuLayout = findViewById(R.id.menu_layout);
        mArcLayout = (ArcLayout) findViewById(R.id.arc_layout);
        viewDisplay = (ViewPager) findViewById(R.id.pager);

        actionBar = getActionBar();
        mAdapter = new Tab_Controller(getSupportFragmentManager());

        viewDisplay.setAdapter(mAdapter);

        actionBar.setHomeButtonEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        viewDisplay.setAdapter(mAdapter);

        for (int i = 0; i < icons.length; i++) {
            actionBar.addTab(actionBar.newTab().setIcon(icons[i]).setTabListener(this));
        }

        context = getApplicationContext();

        //Define buttons
        docDetailsBTN = (Button) findViewById(R.id.docDetailsBtn);
        reportFakeDocBTN = (Button) findViewById(R.id.reportFakeDocBtn);
        locationDetailBTN = (Button) findViewById(R.id.locationDetailsBtn);
        faceBookShareBTN = (Button) findViewById(R.id.facebookShareBtn);

        lastSelectedButton = docDetailsBTN;
        docDetailsBTN.setBackgroundResource(R.drawable.light_blue_oval_selector);

        //Detect slide
        viewDisplay.setOnPageChangeListener(this);

        mMenuLayout.setOnClickListener(this);

        for (int i = 0, size = mArcLayout.getChildCount(); i < size; i++) {
            mArcLayout.getChildAt(i).setOnClickListener(this);
        }

        mFab.setOnClickListener(this);

        if (this.getIntent().hasExtra("SelectedDoc")) {
            Model_Doctor selectedDoctor = (Model_Doctor) this.getIntent().getSerializableExtra("SelectedDoc");
            Controller_Doctor_Rating ratingDialog = new Controller_Doctor_Rating();
            ratingDialog.setArguments(selectedDoctor, context);
            ratingDialog.show(getSupportFragmentManager(), null);
        }


        //Load Coordinates
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE); // Getting LocationManager object

        Criteria criteria = new Criteria();// Creating an empty criteria object

        provider = locationManager.getBestProvider(criteria, false);// Getting the name of the provider that meets the criteria

        if (provider != null && !provider.equals("")) {

            // Get the location from the given provider
            location = locationManager.getLastKnownLocation(provider);

            locationManager.requestLocationUpdates(provider, 20000, 1, this);

            if (location != null)
                onLocationChanged(location);

        } else {
            Toast.makeText(context, getResources().getString(R.string.no_provider), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_view__home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.view_fragment_slmc:
                fragmentsDialog = new Dialog(Controller_Home.this);
                fragmentsDialog.setTitle("About SLMC");
                fragmentsDialog.setContentView(R.layout.view_fragment_slmc);

                WebView aboutSLMCWebView = (WebView) fragmentsDialog.findViewById(R.id.slmcDetails);
                aboutSLMCWebView.setBackgroundColor(Color.TRANSPARENT);

                //Display about SLMC Text
                StringBuilder textAboutSLMC = new StringBuilder("<html><body><p align=\"justify\" style=\"color:black\">");
                textAboutSLMC.append(getString(R.string.about_SLMC));
                textAboutSLMC.append("</p></body></html>");

                aboutSLMCWebView.loadData(textAboutSLMC.toString(), "text/html", "utf-8");

                //Set URL link to SLMC Website
                TextView urlView = (TextView) fragmentsDialog.findViewById(R.id.slmcURL);
                urlView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.srilankamedicalcouncil.org"));
                        startActivity(browserIntent);

                    }
                });

                fragmentsDialog.show();
                break;
            case R.id.view_fragment_aboutUs:
                fragmentsDialog = new Dialog(Controller_Home.this);
                fragmentsDialog.setContentView(R.layout.view_fragment_aboutus);
                fragmentsDialog.setTitle("About Us");
                final RatingBar ratingBar = (RatingBar) fragmentsDialog.findViewById(R.id.ratingBar);

                ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                    public void onRatingChanged(RatingBar ratingBar, final float rating, boolean fromUser) {
                        TextView ratingLevelTV = (TextView) fragmentsDialog.findViewById(R.id.rateLevelTV);
                        String rate = getRatingTextFrom(rating);
                        ratingLevelTV.setText(rate);
                    }
                });

                Button button = (Button) fragmentsDialog.findViewById(R.id.rateBtn);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        String baseURL = context.getResources().getString(R.string.webserviceLink);
                        StringBuilder url = new StringBuilder(baseURL);
                        float rating = ratingBar.getRating();
                        String rateLevelText = getRatingTextFrom(rating);
                        url.append("PhoneAppControllers/AppRatingController/insertAppNewRating/");
                        JSONObject ratingJSONObj = new JSONObject();
                        try {
                            ratingJSONObj.put("rating", rating);
                            ratingJSONObj.put("ratingLevelText", rateLevelText);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        WebTask_ExecutePostRequests ratingTask = new WebTask_ExecutePostRequests();
                        ratingTask.setContext(context);
                        ratingTask.setMessage("Thanks for your support!!");
                        ratingTask.setjObject(ratingJSONObj);
                        // passes values for the urls string array
                        ratingTask.execute(url.toString());
                    }
                });


                fragmentsDialog.show();
                break;
        }
        return true;
    }

    private String getRatingTextFrom(float rate) {
        int rateLevel = (int) rate;
        String rateLevelString;
        switch (rateLevel) {
            case 0:
                rateLevelString = "Very Bad App";
                break;
            case 1:
                rateLevelString = "Bad App";
                break;
            case 2:
                rateLevelString = "App is in average level";
                break;
            case 3:
                rateLevelString = "Good App";
                break;
            case 4:
                rateLevelString = "Very Good App";
                break;
            default:
                rateLevelString = "Excellent App";
        }
        return rateLevelString;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fab) {
            onFabClick(v);
            return;
        }

        if (v instanceof Button) {
            switch (((Button) v).getId()) {
                case R.id.docDetailsBtn:
                    viewDisplay.setCurrentItem(0);
                    docDetailsBTN.setBackgroundResource(R.drawable.light_blue_oval_selector);
                    viewDisplay.setBackgroundColor(Color.parseColor("#ffffff"));
                    break;
                case R.id.locationDetailsBtn:
                    viewDisplay.setCurrentItem(1);
                    viewDisplay.setBackgroundColor(Color.parseColor("#ffffff"));
                    break;
                case R.id.reportFakeDocBtn:
                    viewDisplay.setCurrentItem(2);
                    viewDisplay.setBackgroundColor(Color.parseColor("#ffffff"));
                    break;
                case R.id.facebookShareBtn:
                    viewDisplay.setCurrentItem(3);
                    viewDisplay.setBackgroundColor(Color.parseColor("#f1f1f1"));
                    break;
            }
        }

        //Hide menu if user click anywhere in the screen
        if (v.getId() == mMenuLayout.getId()) {
            hideMenu();
        }
    }

    private void onFabClick(View v) {
        if (v.isSelected()) {
            hideMenu();
        } else {
            showMenu();
        }
        v.setSelected(!v.isSelected());
    }

    @SuppressWarnings("NewApi")
    private void showMenu() {
        mMenuLayout.setVisibility(View.VISIBLE);

        List<Animator> animList = new ArrayList<>();

        for (int i = 0, len = mArcLayout.getChildCount(); i < len; i++) {
            animList.add(createShowItemAnimator(mArcLayout.getChildAt(i)));
        }

        AnimatorSet animSet = new AnimatorSet();
        animSet.setDuration(400);
        animSet.setInterpolator(new OvershootInterpolator());
        animSet.playTogether(animList);
        animSet.start();
    }

    @SuppressWarnings("NewApi")
    private void hideMenu() {

        List<Animator> animList = new ArrayList<>();

        for (int i = mArcLayout.getChildCount() - 1; i >= 0; i--) {
            animList.add(createHideItemAnimator(mArcLayout.getChildAt(i)));
        }

        AnimatorSet animSet = new AnimatorSet();
        animSet.setDuration(400);
        animSet.setInterpolator(new AnticipateInterpolator());
        animSet.playTogether(animList);
        animSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mMenuLayout.setVisibility(View.INVISIBLE);
            }
        });
        animSet.start();
    }

    private Animator createShowItemAnimator(View item) {

        float dx = mFab.getX() - item.getX();
        float dy = mFab.getY() - item.getY();

        item.setRotation(0f);
        item.setTranslationX(dx);
        item.setTranslationY(dy);

        Animator anim = ObjectAnimator.ofPropertyValuesHolder(
                item,
                AnimatorUtils.rotation(0f, 720f),
                AnimatorUtils.translationX(dx, 0f),
                AnimatorUtils.translationY(dy, 0f)
        );

        return anim;
    }

    private Animator createHideItemAnimator(final View item) {
        float dx = mFab.getX() - item.getX();
        float dy = mFab.getY() - item.getY();

        Animator anim = ObjectAnimator.ofPropertyValuesHolder(
                item,
                AnimatorUtils.rotation(720f, 0f),
                AnimatorUtils.translationX(0f, dx),
                AnimatorUtils.translationY(0f, dy)
        );

        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                item.setTranslationX(0f);
                item.setTranslationY(0f);
            }
        });

        return anim;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        getActionBar().setSelectedNavigationItem(position);
        switch (position) {
            case 0:
                viewDisplay.setCurrentItem(0);
                SelectedButtonDisplay(docDetailsBTN);
                viewDisplay.setBackgroundColor(Color.parseColor("#ffffff"));
                break;
            case 1:
                viewDisplay.setCurrentItem(1);
                SelectedButtonDisplay(locationDetailBTN);
                viewDisplay.setBackgroundColor(Color.parseColor("#ffffff"));
                break;
            case 2:
                viewDisplay.setCurrentItem(2);
                SelectedButtonDisplay(reportFakeDocBTN);
                viewDisplay.setBackgroundColor(Color.parseColor("#ffffff"));
                break;
            case 3:
                viewDisplay.setCurrentItem(3);
                SelectedButtonDisplay(faceBookShareBTN);
                viewDisplay.setBackgroundColor(Color.parseColor("#f1f1f1"));
                break;
            case 4:
                viewDisplay.setCurrentItem(4);
                SelectedButtonDisplay(aboutSLMCBTN);
                viewDisplay.setBackgroundColor(Color.parseColor("#ffffff"));
                break;
            case 5:
                viewDisplay.setCurrentItem(5);
                SelectedButtonDisplay(aboutDevelopersBTN);
                viewDisplay.setBackgroundColor(Color.parseColor("#ffffff"));
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private void SelectedButtonDisplay(Button b) {
        lastSelectedButton.setBackgroundResource(R.drawable.path_white_oval);
        b.setBackgroundResource(R.drawable.light_blue_oval_selector);
        lastSelectedButton = b;
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, android.app.FragmentTransaction ft) {
        viewDisplay.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, android.app.FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, android.app.FragmentTransaction ft) {

    }

    @Override
    public void onLocationChanged(Location location) {
        Model_GlobalValues.latitude = location.getLatitude();
        Model_GlobalValues.longtitude = location.getLongitude();

        Toast.makeText(context, "Loading Coordinates", Toast.LENGTH_LONG);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
