package Models;

//Comment
public class CommentModel {
    private int commentID;
    private String comment;
    private double rate;
    private int noOfLikes;

    public CommentModel() {
    }

    public int getCommentID() {
        return commentID;
    }

    //Getters and setters
    public void setCommentID(int commentID) {
        this.commentID = commentID;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getNoOfLikes() {
        return noOfLikes;
    }

    public void setNoOfLikes(int noOfLikes) {
        this.noOfLikes = noOfLikes;
    }
}
