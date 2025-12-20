import java.time.LocalDate;

public class Loan {
    private Book book;
    private Member member;
    private LocalDate loanDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private boolean returned;


    public Loan(Book book, Member member, LocalDate loanDate){
        this.book = book;
        this.member = member;
        this.loanDate = loanDate;
        this.dueDate = loanDate.plusDays(14);
        this.returned = false;
        this.returnDate = null;
    }

    public void markReturned(LocalDate returnDate){
        if(returned){
            return;
        }else {
            this.returnDate = returnDate;
            this.returned = true;
            book.returnCopy();
        }
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

}