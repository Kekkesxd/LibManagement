import java.time.LocalDate;

//Represents a single book loan in the library
public class Loan {
    private Book book;
    private Member member;
    private LocalDate loanDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private boolean returned;

    //Creates a new loan and then sets the duedate to 14 days after the book has been loaned
    public Loan(Book book, Member member, LocalDate loanDate){
        this.book = book;
        this.member = member;
        this.loanDate = loanDate;
        this.dueDate = loanDate.plusDays(14);
        this.returned = false;
        this.returnDate = null;
    }

    //Marks the loan as returned and restores the book availability
    public void markReturned(LocalDate returnDate){
        if(returned){
            return;
        }else {
            this.returnDate = returnDate;
            this.returned = true;
            book.returnCopy();
        }
    }

    // Used ONLY when loading from CSV so we don't change book availability.
    public void loadAsReturned(LocalDate returnDate) {
        this.returnDate = returnDate;
        this.returned = true;
    }

    //Getters
    public Book getBook(){
        return book;
    }
    public Member getMember(){
        return member;
    }
    public boolean isReturned(){
        return returned;
    }
    public LocalDate getReturnDate(){
        return returnDate;
    }
    public LocalDate getDueDate(){
        return dueDate;
    }
    public LocalDate getLoanDate(){
        return loanDate;
    }

}