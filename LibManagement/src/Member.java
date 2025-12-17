
public class Member {
    private String name;
    private String memID;
    private String email;
    private int number;
    private int maxBooksAllowed;




    public Member(String name, String memID, String email, int number, int maxBooksAllowed){
        this.name = name;
        this.memID = memID;
        this.email = email;
        this.number = number;
        this.maxBooksAllowed = maxBooksAllowed;
    }

    public Member(String name, String memID, String email, int number){
        this(name, memID, email, number, 2);
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
    public int getMaxBooksAllowed(){
        return maxBooksAllowed;
    }

    //Setting the fee for loan being late for normal members
    public double calcLateFee(long daysLate){
        if(daysLate <= 0 ){
            return 0;
        }
        return 5 * daysLate;
    }

}
