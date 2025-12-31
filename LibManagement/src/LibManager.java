import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.*;
import java.io.*;

public class LibManager {
    private List<Book> books;
    private List<Member> members;
    private List<Loan> loans;

    public LibManager(){
        this.books = new ArrayList<>();
        this.members = new ArrayList<>();
        this.loans = new ArrayList<>();
    }

    //Methods

    public Book findBookById(int id){
        for (Book b : books){
            if(b.getId() == id) return b;
        }
        return null;
    }

    public Member findMemberById(String memberId){
        for(Member m : members){
            if(m.getMemID().equalsIgnoreCase(memberId)) return m;
        }
        return null;
    }

    private int countActiveLoans(Member member){
        int count = 0;
        for (Loan loan : loans){
            if(!loan.isReturned() && loan.getMember().equals(member)){
                count++;
            }
        }
        return count;
    }

    public boolean borrowBook(Member member, Book book, LocalDate loanDate){
        if(!members.contains(member)){
            throw new IllegalStateException("Member is not registered in Library");
        }

        if(!books.contains(book)){
            throw new IllegalStateException("Book not found in library");
        }

        int activeLoans = countActiveLoans(member);
        if(activeLoans>=member.getMaxBooksAllowed()){
            throw new IllegalStateException("Already reached max borrow limit");
        }
        if (hasAcitveLoan(member, book)){
            throw new IllegalStateException("Member Already has this book on loan");
        }
        if (!book.borrowCopy()){
            throw new IllegalStateException("No Available copies of this book");
        }

        Loan loan = new Loan(book, member, loanDate);
        loans.add(loan);

        return true;
    }

    private boolean hasAcitveLoan(Member member, Book book){
        for(Loan loan : loans){
            if(!loan.isReturned() && loan.getMember().equals(member) && loan.getBook().equals(book)){
                return true;
            }
        }
        return false;
    }

    public double returnBook(Member member, Book book, LocalDate returnDate){
        for (Loan loan : loans){
            if(!loan.isReturned() && loan.getMember().equals(member) && loan.getBook().equals(book)){
                loan.markReturned(returnDate);

                long daysLate = ChronoUnit.DAYS.between(loan.getDueDate(), returnDate);
                if (daysLate<= 0){
                    return 0.0;
                }

                return member.calcLateFee(daysLate);
            }
        }

        throw new IllegalStateException("Active loan not found for this member or book");
    }


    public void addBook(Book book){
        books.add(book);
    }

    public void addMember(Member member){
        members.add(member);
    }

    public List<Book> getBooks(){
        return new ArrayList<>(books);
    }
    //To view All loans by system/Admin
    public List<Loan> getLoans(){
        return new ArrayList<>(loans);
    }
    //To view active loans for members
    public List<Loan> getActiveLoansMember(Member member) {
        List<Loan> result = new ArrayList<>();
        for (Loan loan : loans){
            if(!loan.isReturned() && loan.getMember().equals(member)){
                result.add(loan);
            }
        }
        return result;
    }
    //To view full loan history for members
    public List<Loan> getLoanHistory(Member member) {
        List<Loan> result = new ArrayList<>();

        for (Loan loan : loans) {
            if (loan.getMember().equals(member)) {
                result.add(loan);
            }
        }
        return result;
    }

    public List<Book> searchBooks(String keyword){
        List<Book> results = new ArrayList<>();
        for (Book book : books){
            if (book.matches(keyword)){
                results.add(book);
            }
        }
        return results;
    }

    //Loading from Csv

    public void loadFromCsv(String booksPath, String membersPath, String loansPath) throws IOException {
        this.books.addAll(CsvLoader.loadBooks(booksPath));
        this.members.addAll(CsvLoader.loadMembers(membersPath));
        this.loans.addAll(CsvLoader.loadLoans(loansPath, this.books, this.members));
    }
}
