package com.rudraksha.rudrakshashakti.Pojo;

import java.util.List;

public class ExpertCourseDetails {

    List<String> languages,courses;
    String availableForCourses,DurationOfCourse,courseMode,sessions,BasicCoursePrice,AdvanceCoursePrice,expertNote;




    public ExpertCourseDetails(){

    }

    public ExpertCourseDetails(List<String> courses, List<String> languages, String availableForCourses, String courseMode, String DurationOfCourse, String sessions, String BasicCoursePrice, String AdvanceCoursePrice, String expertNote){
        this.courses = courses;
        this.languages = languages;
        this.availableForCourses = availableForCourses;
        this.DurationOfCourse = DurationOfCourse;
        this.courseMode = courseMode;
        this.sessions = sessions;
        this.BasicCoursePrice = BasicCoursePrice;
        this.AdvanceCoursePrice = AdvanceCoursePrice;
        this.expertNote = expertNote;


    }

    public List<String> getCourses() {
        return courses;
    }

    public void setCourses(List<String> courses) {
        this.courses = courses;
    }

    public String getavailableForCourses() {
        return availableForCourses;
    }

    public void setavailableForCourses(String availableForCourses) {
        this.availableForCourses = availableForCourses;
    }

    public String getCourseMode() {
        return courseMode;
    }

    public void setCourseMode(String courseMode) {
        this.courseMode = courseMode;
    }
    public String getDurationOfCourse() {
        return DurationOfCourse;
    }

    public void setDurationOfCourse(String DurationOfCourse) {
        this.DurationOfCourse = DurationOfCourse;
    }

    public String getsessions() {
        return sessions;
    }

    public void setsessions(String sessions) {
        this.sessions = sessions;
    }

    public String getBasicCourcePrice() {
        return BasicCoursePrice;
    }

    public void setBasicCoursePrice(String coursePrice) {
        this.BasicCoursePrice = coursePrice;
    }

    public String getAdvanceCoursePrice() {
        return AdvanceCoursePrice;
    }

    public void setAdvanceCoursePrice(String AdvanceCoursePrice) {
        this.AdvanceCoursePrice = AdvanceCoursePrice;
    }

    public String getexpertNote() {
        return expertNote;
    }

    public void setexpertNote(String expertNote) {
        this.expertNote = expertNote;
    }
}
