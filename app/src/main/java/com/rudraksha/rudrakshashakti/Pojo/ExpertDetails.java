package com.rudraksha.rudrakshashakti.Pojo;

import java.util.List;

public class ExpertDetails {
    List<String> services,languages,poojas;
    String name,fathersName,emailId,whatsappNo,upiNo,dateOfBirth,gender,state,city,mainService,background,experience,profilePic,rating,uid,underReview,price,referral,myCode;



    public ExpertDetails() {
    }

    public ExpertDetails(List<String> services, List<String> languages, List<String> poojas, String name, String fathersName, String emailId, String whatsappNo, String upiNo, String dateOfBirth, String gender, String state, String city, String mainService, String background, String experience, String profilePic, String rating, String uid, String underReview, String price, String referral, String myCode) {
        this.services = services;
        this.languages = languages;
        this.poojas = poojas;
        this.name = name;
        this.fathersName = fathersName;
        this.emailId = emailId;
        this.whatsappNo = whatsappNo;
        this.upiNo = upiNo;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.state = state;
        this.city = city;
        this.mainService = mainService;
        this.background = background;
        this.experience = experience;
        this.profilePic = profilePic;
        this.rating = rating;
        this.uid = uid;
        this.underReview = underReview;
        this.price = price;
        this.referral = referral;
        this.myCode = myCode;
    }

    public List<String> getPoojas() {
        return poojas;
    }

    public void setPoojas(List<String> poojas) {
        this.poojas = poojas;
    }

    public String getMyCode() {
        return myCode;
    }

    public void setMyCode(String myCode) {
        this.myCode = myCode;
    }

    public String getPrice() {
        return price;
    }

    public String getReferral() {
        return referral;
    }

    public void setReferral(String referral) {
        this.referral = referral;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getUnderReview() {
        return underReview;
    }

    public void setUnderReview(String underReview) {
        this.underReview = underReview;
    }

    public List<String> getLanguages() {
        return languages;
    }

    public void setLanguages(List<String> languages) {
        this.languages = languages;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFathersName() {
        return fathersName;
    }

    public void setFathersName(String fathersName) {
        this.fathersName = fathersName;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getWhatsappNo() {
        return whatsappNo;
    }

    public void setWhatsappNo(String whatsappNo) {
        this.whatsappNo = whatsappNo;
    }

    public String getUpiNo() {
        return upiNo;
    }

    public void setUpiNo(String upiNo) {
        this.upiNo = upiNo;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getMainService() {
        return mainService;
    }

    public void setMainService(String mainService) {
        this.mainService = mainService;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public List<String> getServices() {
        return services;
    }

    public void setServices(List<String> services) {
        this.services = services;
    }
}
