package aincoder.app.poyshabd;


import java.util.Date;

/**
 * Created by Abhi on 20 Jan 2018 020.
 */

public class User {
    String UserID,FirstName,LastName,Phone,NID,PhotoURL,Gender,Password;
    Date sessionExpiryDate;


    public void setUserID(String UserID) {
        this.UserID = UserID;
    }
    public void setFirstName(String FirstName) {
        this.FirstName = FirstName;
    }
    public void setLastName(String LastName) {
        this.LastName = LastName;
    }
    public void setNID(String NID) {
        this.NID = NID;
    }
    public void setPhotoURL(String PhotoURL) {
        this.PhotoURL = PhotoURL;
    }
    public void setGender(String Gender) {
        this.Gender = Gender;
    }
    public void setPhone(String Phone) {
        this.Phone = Phone;
    }
    public void setPassword(String Password) {
        this.Password = Password;
    }
    public void setSessionExpiryDate(Date sessionExpiryDate) {
        this.sessionExpiryDate = sessionExpiryDate;
    }


    public String getUserID() {
        return UserID;
    }
    public String getFirstName() {
        return FirstName;
    }
    public String getLastName() {
        return LastName;
    }
    public String getPhone() {
        return Phone;
    }
    public String getNID() {
        return NID;
    }
    public String getPhotoURL() {
        return PhotoURL;
    }
    public String getGender() {
        return Gender;
    }
    public String getPassword() {
        return Password;
    }
    public Date getSessionExpiryDate() {
        return sessionExpiryDate;
    }
}
