package pack.knowyourdoctor.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Home on 3/6/2015.
 */
public class TabPagerAdapter extends FragmentPagerAdapter {

    public String fakeRegNo;
    public TabPagerAdapter(FragmentManager fm) {
        super(fm);

    }
    //static String url;
    @Override
    public Fragment getItem(int index) {
        switch (index) {
            case 0:
                return new View_Fragment_DoctorDetails();
            case 1:
                return new View_Fragment_LocationFinding();
            case 2:
                return new View_Fragment_ReportSend();
            case 3:
                return new View_Fragment_Facebook_Login();
            /*case 4:
                return new View_Fragment_SLMC();
            case 5:
                return new View_Fragment_Aboutus();
            case 4:
                return new View_Fragment_DoctorDetails_Searched();*/
        }
        return null;
    }

    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return 4;
    }
}
