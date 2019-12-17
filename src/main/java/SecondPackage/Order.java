package SecondPackage;


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
		messageKiller.interrupt();
        
	}

    
    
	public Order(String firstName,
				 String number,
				 String language,
				 String id) {
        Order(firstName,
				 number,
				 language,
				 id,
                 null,
				 null,
				 null);
	}
    
    
    
	public Order() {
		Order(null,
				 null,
				 null,
				 null,
				 null,
                 null,
				 null);
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
