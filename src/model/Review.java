
package model;

import java.util.Date;

public class Review {
    private int id;
    private String userName;
    private int rating;
    private String comment;
    private Date reviewDate;

    // getters e setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public Date getReviewDate() { return reviewDate; }
    public void setReviewDate(Date reviewDate) { this.reviewDate = reviewDate; }
}