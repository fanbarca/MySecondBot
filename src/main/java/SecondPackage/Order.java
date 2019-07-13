package SecondPackage;


public class Order {


    private Integer sentMessage;
    private Integer receivedMes;
    private Integer image;
    private String language;
    private String number;
    private String firstName;

    public Order(String firstName,
				 String number,
				 String language,
				 String receivedMes,
				 String sentMessage,
				 String image) {
		this.firstName = firstName;
		this.number = number;
		this.language = language;
		this.image = Integer.parseInt(image);
		this.receivedMes = Integer.parseInt(receivedMes);
		this.sentMessage = Integer.parseInt(sentMessage);
	}
	public Order(String firstName,
				 String number,
				 String language,
				 String receivedMes) {
		this.firstName = firstName;
		this.number = number;
		this.language = language;
		this.receivedMes = Integer.parseInt(receivedMes);
		this.sentMessage = null;
		this.image = null;
	}
	public Order() {
		this.firstName = null;
		this.number = null;
		this.language = null;
		this.receivedMes = null;
		this.sentMessage = null;
		this.image = null;
	}

	public String getNumber() {
		return number;
	}


	public void setNumber(String number) {
		this.number = number;
	}


	public String getLanguage() {
		return language;
	}


	public void setLanguage(String language) {
		this.language = language;
	}


	public Integer getImage() {
		return image;
	}


	public void setImage(Integer image) {
		this.image = image;
	}


	public Integer getReceivedMes() {
		return receivedMes;
	}


	public void setReceivedMes(Integer receivedMes) {
		this.receivedMes = receivedMes;
	}


	public Integer getSentMessage() {
		return sentMessage;
	}


	public void setSentMessage(Integer sentMessage) {
		this.sentMessage = sentMessage;
	}


	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
}
