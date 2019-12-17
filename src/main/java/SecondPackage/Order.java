package SecondPackage;

import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import java.util.ArrayList;
import java.util.List;

public class Order  {

    private String id;
    private String sentMessage;
    private String receivedMes;
    private String image;
    private String language;
    private String number;
    private String firstName;
    private String address;
    private boolean alert;
    private Thread messageKiller;
    
    
    


    public Order(String firstName,
				 String number,
				 String language,
				 String id,
				 String receivedMes,
				 String sentMessage,
				 String image) {
		images = new ArrayList<>();
		this.firstName = firstName;
		this.number = number;
		this.language = language;
        this.id = id;
		this.receivedMes = receivedMes;
		this.sentMessage = sentMessage;
    	this.image = image;
	}

    public void newThread() {
		if (messageKiller!=null) messageKiller.interrupt();
        messageKiller = new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                Thread.sleep(1000*60);
                Bot bot = new Bot();
                bot.deleteMessage(image, id);
                image = null;
            } catch (InterruptedException e) {
                //e.printStackTrace();
            }
        }
        });
        messageKiller.start();
	}

    
    
	public Order(String firstName,
				 String number,
				 String language,
				 String id) {
		images = new ArrayList<>();
		this.firstName = firstName;
		this.number = number;
		this.language = language;
        this.id = id;
		this.receivedMes = null;
		this.sentMessage = null;
    	this.image = null;
	}
    
    
    
	public Order() {
		images = new ArrayList<>();
		this.firstName = null;
		this.number = null;
		this.language = null;
        this.id = null;
		this.receivedMes = null;
		this.sentMessage = null;
    	this.image = null;
	}

    
    
    public List<String> getImages() {
		return images;
	}

	public void setImages(List<String> images) {
		this.images = images;
	}

	private List<String> images;
	private String listener;

	public String getNumber() {
		return number;
	}


	public void setNumber(String number) {
		this.number = number;
    }

	public String getAddress() {
		return address;
	}


	public void setAddress(String address) {
		this.address = address;
	}
	public boolean getAlert() {
		return alert;
	}


	public void setAlert(boolean alert) {
		this.alert = alert;
	}

	public String getLanguage() {
		return language;
	}


	public void setLanguage(String language) {
		this.language = language;
	}


	public String getImage() {
		return image;
	}


	public void setImage(String image) {
		this.image = image;
	}


	public String getReceivedMes() {
		return receivedMes;
	}


	public void setReceivedMes(String receivedMes) {
		this.receivedMes = receivedMes;
	}


	public String getSentMessage() {
		return sentMessage;
	}


	public void setSentMessage(String sentMessage) {
		this.sentMessage = sentMessage;
	}


	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getListener() {
		return listener;
	}

	public void setListener(String listener) {
		this.listener = listener;
	}
    public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
}
