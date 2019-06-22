import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import java.util.Date;

public class Order {
    private InlineKeyboardMarkup m = new InlineKeyboardMarkup();
    private ReplyKeyboardMarkup r = new ReplyKeyboardMarkup();
    private int id = 0;
    private Language lan;
    private String direction;
    private String pages = "";
    private User user;
    private String deadline;
    private Contact contact;
    private Document doc;
    private Date orderTime;
    private boolean hasOrdered = false;
    private boolean isfinished =false;
    private int totalCost = 0;
    private int duration = 0;

    public int getDuration() {
        return duration;
    }

    public void setDuration() {
        if (Integer.parseInt(pages)<5) this.duration = 1;
        else this.duration = Integer.parseInt(pages)/5;
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

    public int getTotalCost() {
        return totalCost;
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

    public boolean hasOrdered() {
        return hasOrdered;
    }

    public void setOrdered(boolean flag) {
        this.hasOrdered = flag;
    }



    public Date getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(Date orderTime) {
        this.orderTime = orderTime;
    }



    Order(User user){
        this.user = user;
    }
    public Document getDoc() {
        return doc;
    }

    public void setDoc(Document doc) {
        this.doc = doc;
    }

    public User getUser() {
        return user;
    }
    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public Integer getId() {
        return id;
    }
    public void setLanguage(Language lan) {
        this.lan = lan;
    }
    public void setDirection(String direction) {
        this.direction = direction;
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
    public String getDirection(){
        return direction;
    }

    public void setRM(ReplyKeyboardMarkup r) {
        this.r = r;
    }

    public void setIM(InlineKeyboardMarkup m) {
        this.m = m;
    }

    public String getPages(){
        return pages;
    }
    public InlineKeyboardMarkup getIM(){
        return m;
    }
    public ReplyKeyboardMarkup getRM(){
        return r;
    }
    public Language getLanguage(){
        return lan;
    }
    public boolean pagesBack() {
        if (pages != null && pages.length() > 0) {
            pages = pages.substring(0, pages.length() - 1);
        return true;
        } else return false;
    }

    public void  clearOrder(){
        setDoc(null);
        setOrdered(false);
        setDirection(null);
        this.pages = "";
        setOrderTime(null);
    }
}
