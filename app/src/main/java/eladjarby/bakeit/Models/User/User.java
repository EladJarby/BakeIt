package eladjarby.bakeit.Models.User;

/**
 * Created by EladJ on 27/6/2017.
 */

public class User {
    private String ID;
    private String userEmail;
    private String userTown;
    private String userImage;
    private String userFirstName;
    private String userLastName;

    public User() {
    }

    public User(String ID, String userEmail, String userTown, String userImage, String userFirstName, String userLastName) {
        this.ID = ID;
        this.userEmail = userEmail;
        this.userTown = userTown;
        this.userImage = userImage;
        this.userFirstName = userFirstName;
        this.userLastName = userLastName;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserTown() {
        return userTown;
    }

    public void setUserTown(String userTown) {
        this.userTown = userTown;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getUserFirstName() {
        return userFirstName;
    }

    public void setUserFirstName(String userFirstName) {
        this.userFirstName = userFirstName;
    }

    public String getUserLastName() {
        return userLastName;
    }

    public void setUserLastName(String userLastName) {
        this.userLastName = userLastName;
    }
}
