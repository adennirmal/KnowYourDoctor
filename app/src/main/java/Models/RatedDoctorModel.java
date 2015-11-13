package Models;

import java.util.ArrayList;

//Doctor who's having comments
public class RatedDoctorModel extends DoctorModel {
    private CommentModel topComment;
    private ArrayList<CommentModel> comments;

    public RatedDoctorModel() {
        comments = new ArrayList<CommentModel>();
    }

    public ArrayList<CommentModel> getComments() {
        return comments;
    }

    public void setComments(ArrayList<CommentModel> comments) {
        this.comments = comments;
    }
}
