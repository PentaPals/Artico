package discount;

import java.sql.Timestamp;

public class NewDiscount {

    int ID;
    String name;
    double percentage;
    Timestamp start_date;
    int duration_days;

    public NewDiscount(int ID, String name, double percentage, Timestamp start_date, int duration_days) {
        this.ID = ID;
        this.name = name;
        this.percentage = percentage;
        this.start_date = start_date;
        this.duration_days = duration_days;
    }


    public int getID() {
        return ID;
    }

    public String getName() {
        return name;
    }

    public double getPercentage() {
        return percentage;
    }


    public Timestamp getStart_date() {
        return start_date;
    }



    public int getDuration_days() {
        return duration_days;
    }


}
