package com.example.ankurbaranwal.feedback.Model;

public class People
{
    String Id;
    String Subject;
    String Locality;
    String Feedback;
    String image;

    public People()
    {

    }

    public People(String id, String subject, String locality, String feedback,String image) {
        this.Id = id;
        this.Subject = subject;
        this.Locality = locality;
        this.Feedback = feedback;
        this.image = image;

    }

    public String getId() {
        return Id;
    }

    public String getSubject() {
        return Subject;
    }

    public String getLocality() {
        return Locality;
    }

    public String getFeedback() {
        return Feedback;
    }
    public String getImage()
    {
        return image;
    }
}
