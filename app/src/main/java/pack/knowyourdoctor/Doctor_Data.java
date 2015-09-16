package pack.knowyourdoctor;

import java.io.Serializable;

/**
 * Created by Yasi on 9/8/2015.
 */
public class Doctor_Data implements Serializable {
    private String id;
    private String doctor_name;
    private String doctor_id;
    private String updated_at;

    public Doctor_Data(String id, String doctor_id, String doctor_name, String updated_at) {
        super();
        this.id =id;
        this.doctor_name = doctor_name;
        this.doctor_id = doctor_id;
        this.updated_at = updated_at;

    }

    //create getters
    public String getAutoID() {
        return id;
    }

    public String getName() {
        return doctor_name;
    }

    public String getID() {
        return doctor_id;
    }

    public String getUpdate() {
        return updated_at;
    }

    //create setters
    public void setAutoID(String id) {
        this.id= id;
    }

    public void setName(String doctor_name) {
        this.doctor_name = doctor_name;
    }

    public void setID(String doctor_id) {
        this.doctor_id= doctor_id;
    }

    public void setUpdated(String updated_at) {
        this.updated_at= updated_at;
    }

}
