package pack.knowyourdoctor.Send_Details;

/**
 * Created by Yasi on 8/27/2015.
 */
public class Person {



    private String doctor_id;
    private String doctor_name;
    private String created_at;
    private String updated_at;



    public Object getID() {
        return doctor_id;
    }

    public Object getName() {
        return doctor_name;
    }

    public Object getCreate() {
        return created_at;
    }

    public Object getUpdate() {
        return updated_at;
    }




    public void setID(String doctor_id) {
        this.doctor_id= doctor_id;
    }

    public void setName(String doctor_name) {
        this.doctor_name = doctor_name;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at= updated_at;
    }



}
