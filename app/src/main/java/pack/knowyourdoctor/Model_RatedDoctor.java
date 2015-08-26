package pack.knowyourdoctor;

import java.util.ArrayList;

/**
 * Created by Home on 5/8/2015.
 */
public class Model_RatedDoctor extends Model_Doctor {

    private double averageRating;
    private ArrayList<Model_Comment> comments;

    public Model_RatedDoctor() {
        comments = new ArrayList<Model_Comment>();
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    public ArrayList<Model_Comment> getComments() {
        return comments;
    }

    public void setComments(ArrayList<Model_Comment> comments) {
        this.comments = comments;
    }
}
