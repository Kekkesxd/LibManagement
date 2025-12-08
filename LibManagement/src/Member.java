import javax.naming.Name;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public class Member {
    private String name;
    private String memID;
    private String email;
    private int number;
    private int booksBorrowed;
    private int maxBooksAllowed;
    private ArrayList<Loan> loans = new ArrayList<>(maxBooksAllowed);

    public Member(){
        this.maxBooksAllowed = 2;
    }

    public void loanBook(Book book, LocalDate loanDate, LocalDate dueDate){
        if (booksBorrowed == maxBooksAllowed){
            System.out.println("Cannot borrow any more books, you hit the max cap");
            return;
        }
        Loan loan = new Loan(book, loanDate, dueDate);
            System.out.println(memID + " Has borrowed: " + book + "On: " + loanDate + " and it's due on: " + dueDate );
            booksBorrowed++;
            loans.add(loan);
    }

    public void returnBook(Book book, LocalDate returnDate){
        for (int i = 0; i< maxBooksAllowed; i++){
            if (loans.get(i).getBook().equals(book)){
                System.out.println("Book found....returning");
                if(loans.get(i).getDueDate().isAfter(returnDate)){
                    long daysLate = ChronoUnit.DAYS.between(loans.get(i).getDueDate(), returnDate);
                    CalcFee(daysLate);
                }
                loans.remove(i);
                break;
            }else if (i == maxBooksAllowed - 1 && loans.get(i).getBook() != book){
                System.out.println("Book not found");
                return;
            }
        }
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
        this.booksBorrowed = booksborrowed;
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
        return booksBorrowed;
    }
    public int getMaxBooksAllowed(){
        return maxBooksAllowed;
    }
    //Setting the fee for loan being late for normal members
    public double CalcFee(long daysLate){
        return 5 * daysLate;
    }

}
