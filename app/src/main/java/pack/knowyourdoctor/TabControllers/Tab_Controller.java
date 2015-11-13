package pack.knowyourdoctor.TabControllers;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import pack.knowyourdoctor.Constants.Numbers;

//Controller of tabs
public class Tab_Controller extends FragmentPagerAdapter {
    public String fakeRegNo;

    public Tab_Controller(FragmentManager fm) {
        super(fm);
    }

    //Get fragment according to the position
    @Override
    public Fragment getItem(int index) {
        switch (index) {
            case 0:
                return new Controller_Fragment_DoctorDetails();
            case 1:
                return new Controller_Fragment_LocationFinding();
            case 2:
                return new Controller_Fragment_ReportSend();
            case 3:
                return new Controller_Fragment_Facebook_Login();
            /*case 4:
                return new View_Fragment_SLMC();
            case 5:
                return new View_Fragment_Aboutus();
            case 4:
                return new View_Fragment_DoctorDetails_Searched();*/
        }
        return null;
    }

    //Get tab count of app
    @Override
    public int getCount() {
        return Numbers.TAB_COUNT;
    }
}
