package Models;

import java.util.ArrayList;

/**
 * Created by Home on 5/8/2015.
 */
public class Model_RatedDoctor extends Model_Doctor {

    private Model_Comment topComment;
    private ArrayList<Model_Comment> comments;

    public Model_RatedDoctor() {
        comments = new ArrayList<Model_Comment>();
    }

    public Model_Comment getTopComment() {
        return topComment;
    }

    public void setTopComment(Model_Comment topComment) {
        this.topComment = topComment;
    }

    public ArrayList<Model_Comment> getComments() {
        return comments;
    }

    public void setComments(ArrayList<Model_Comment> comments) {
        this.comments = comments;
    }
}
