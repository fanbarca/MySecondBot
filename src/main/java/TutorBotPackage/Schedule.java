package TutorBotPackage;
import java.util.ArrayList;

public class Schedule {
    /**
     *This class is for the Schedule object which stores the schedules of tutors.
     */
    private boolean[] avalible; // 0 = Monday … 6 = Sunday – “true” indicates that the tutor is available on that day
    private ArrayList<TimeInterval> availableTimes[]; // list of time intervals of availability - availableTimes[0] contains all the time intervals that the tutor is available at on Mondays

    public static final int MONDAY = 0;
    public static final int TUESDAY = 1;
    public static final int WEDNESDAY = 2;
    public static final int THURSDAY = 3;
    public static final int FRIDAY = 4;
    public static final int SATURDAY = 5;
    public static final int SUNDAY = 6;


    public Schedule()
    {

    }


}