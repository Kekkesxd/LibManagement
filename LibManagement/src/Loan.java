import java.time.LocalDate;

public class Loan {
    private Book book;
    private LocalDate loanDate;
    private LocalDate dueDate;


    public Loan(Book book, LocalDate loanDate, LocalDate dueDate){
        this.book = book;
        this.loanDate = loanDate;
        this.dueDate = loanDate.plusDays(20);
    }
    //Getters
    public Book getBook(){
        return book;
    }
    public LocalDate getLoanDate(){
        return loanDate;
    }
    public LocalDate getDueDate(){
        return dueDate;
    }

}