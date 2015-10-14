package Models;

/**
 * Created by Home on 6/17/2015.
 */
public class Model_FBUser {

    private String fb_user_name;
    private String fb_user_id;

    public Model_FBUser() {
    }

    public Model_FBUser(String fb_user_name, String fb_user_id) {
        this.fb_user_name = fb_user_name;
        this.fb_user_id = fb_user_id;
    }


    public String getFb_user_name() {
        return fb_user_name;
    }

    public String getFb_user_id() {
        return fb_user_id;
    }

    public void setFb_user_name(String fb_user_name) {
        this.fb_user_name = fb_user_name;
    }

    public void setFb_user_id(String fb_user_id) {
        this.fb_user_id = fb_user_id;
    }
}
