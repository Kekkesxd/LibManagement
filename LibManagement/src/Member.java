

public class Member {
    private String name;
    private String memID;
    private String email;
    private int number;
    private int booksborrowed;
    private int maxBooksAllowed;

    public Member(){
        this.maxBooksAllowed = 2;
    }

    //Setters
    public void setName(String name){
        this.name = name;
    }
    public void setMemID(String memID){
        this.memID = memID;
    }
    public void setEmail(String email){
        this.email = email;
    }
    public void setNumber(int number){
        this.number = number;
    }
    public void setBooksborrowed(int booksborrowed){
        this.booksborrowed = booksborrowed;
    }

    //Getters
    public String getName(){
        return name;
    }
    public String getMemID(){
        return memID;
    }
    
}
