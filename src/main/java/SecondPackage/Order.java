package SecondPackage;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

import java.util.ArrayList;
import java.util.List;

public class Order {
    private InlineKeyboardMarkup m = new InlineKeyboardMarkup();
    private ReplyKeyboardMarkup r = new ReplyKeyboardMarkup();
    private Language lan;
    private User user;
    private Contact contact;
    private List<Translation> ordersList = new ArrayList<Translation>();

    Order(User user){
        this.user = user;
    }

    public Order() {

    }

    public List<Translation> getOrdersList() {
        return ordersList;
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
    public Language getLanguage(){
        return lan;
    }
    public void setLanguage(Language lan) {
        this.lan = lan;
    }
    public ReplyKeyboardMarkup getRM(){
        return r;
    }
    public void setRM(ReplyKeyboardMarkup r) {
        this.r = r;
    }
    public InlineKeyboardMarkup getIM(){
        return m;
    }
    public void setIM(InlineKeyboardMarkup m) {
        this.m = m;
    }

}
