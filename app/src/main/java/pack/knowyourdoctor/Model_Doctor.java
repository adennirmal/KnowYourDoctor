package pack.knowyourdoctor;

import java.io.Serializable;

/**
 * Created by Home on 3/8/2015.
 */
public class Model_Doctor implements Serializable {
    private int regNo;
    private String regDate;
    private String fullName;
    private String address;
    private String qualifications;

    public Model_Doctor() {
    }

    public Model_Doctor(int regNo, String regDate, String address, String fullName, String qualifications) {
        this.regNo = regNo;
        this.regDate = regDate;
        this.address = address;
        this.fullName = fullName;
        this.qualifications = qualifications;
    }

    //Setters
    public void setRegNo(int regNo) {
        this.regNo = regNo;
    }

    public void setRegDate(String regDate) {
        this.regDate = regDate;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setQualifications(String qualifications) {
        this.qualifications = qualifications;
    }

    //Getter
    public int getRegNo() {
        return regNo;
    }

    public String getRegDate() {
        return regDate;
    }

    public String getFullName() {
        return fullName;
    }

    public String getAddress() {
        return address;
    }

    public String getQualifications() {
        return qualifications;
    }

    public String getDoctorDetails(){
        String details = "";
        details += "Registration No : " + regNo + "\n";
        details += "Registered Date : " + regDate + "\n";
        details += "Full Name : " + fullName + "\n";
        details += "Address : " + address + "\n";
        details += "Qualifications : " + qualifications + "\n";

        return details;
    }
}
