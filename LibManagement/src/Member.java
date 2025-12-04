

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
    public void setMaxBooksAllowed(int maxBooksAllowed){
        this.maxBooksAllowed = maxBooksAllowed;
    }

    //Getters
    public String getName(){
        return name;
    }
    public String getMemID(){
        return memID;
    }
    public String getEmail(){
        return email;
    }
    public int getNumber(){
        return number;
    }
    public int getBooksborrowed(){
        return booksborrowed;
    }
    public int getMaxBooksAllowed(){
        return maxBooksAllowed;
    }

}
