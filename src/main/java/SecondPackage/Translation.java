package SecondPackage;

import org.telegram.telegrambots.meta.api.objects.Document;

import java.util.Date;
import java.util.Random;

public class Translation extends Order {
    private String direction;
    private Date orderTime;
    private int totalCost = 0;
    private int duration = 0;
    private String pages = "";
    private boolean hasOrdered = false;
    private boolean isfinished =false;
    private String deadline;
    String id;


    public String getId() {
        return id;
    }

    Translation() {
        super();
        Random rand = new Random();
        id = String.format("%04d", rand.nextInt(10000));
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration() {
        if (Integer.parseInt(pages)<5) this.duration = 1;
        else this.duration = Integer.parseInt(pages)/5;
    }
    public Date getOrderTime() {
        return orderTime;
    }
    public void setOrderTime(Date orderTime) {
        this.orderTime = orderTime;
    }


    public String getDirection(){
        return direction;
    }
    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getPages(){
        return pages;
    }
    public void setPages(String pages) {
        if (pages.contains(":zero:")&&this.pages.length()>0) this.pages+="0";
        else if (pages.contains(":one:")) this.pages+="1";
        else if (pages.contains(":two:")) this.pages+="2";
        else if (pages.contains(":three:")) this.pages+="3";
        else if (pages.contains(":four:")) this.pages+="4";
        else if (pages.contains(":five:")) this.pages+="5";
        else if (pages.contains(":six:")) this.pages+="6";
        else if (pages.contains(":seven:")) this.pages+="7";
        else if (pages.contains(":eight:")) this.pages+="8";
        else if (pages.contains(":nine:")) this.pages+="9";
    }

    public boolean hasOrdered() {
        return hasOrdered;
    }
    public void setOrdered(boolean flag) {
        this.hasOrdered = flag;
    }
    public boolean pagesBack() {
        if (pages != null && pages.length() > 0) {
            pages = pages.substring(0, pages.length() - 1);
            return true;
        } else return false;
    }
    public void  clearOrder(){
        setOrdered(false);
        setDirection(null);
        this.pages = "";
        setOrderTime(null);
    }

    public int getTotalCost() {
        return totalCost;
    }
    public void setTotalCost() {
        int cost = 0;
        if (direction.equals(AmabiliaBot.directions().get(0))) cost = Prices.ruseng;
        if (direction.equals(AmabiliaBot.directions().get(1))) cost = Prices.rusuzb;
        if (direction.equals(AmabiliaBot.directions().get(2))) cost = Prices.engrus;
        if (direction.equals(AmabiliaBot.directions().get(3))) cost = Prices.enguzb;
        if (direction.equals(AmabiliaBot.directions().get(4))) cost = Prices.uzbeng;
        if (direction.equals(AmabiliaBot.directions().get(5))) cost = Prices.uzbrus;
        this.totalCost = Integer.parseInt(this.pages) * cost;
    }



    public boolean Isfinished() {
        return isfinished;
    }
    public void setfinished(boolean isfinished) {
        this.isfinished = isfinished;
    }
    public String getDeadline() {
        return deadline;
    }
    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

}
