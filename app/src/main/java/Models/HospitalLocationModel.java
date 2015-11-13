package Models;

//Hospital with it's location
public class HospitalLocationModel {
    private int id;
    private String name;
    private String address;
    private double latitude;
    private double longtitude;
    private String district;

    //Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setDistrict(String district) {
        this.district = district;
    }
}
