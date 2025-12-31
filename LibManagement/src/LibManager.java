import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
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

    //Finds book in the library using it's ID
    public Book findBookById(int id){
        for (Book b : books){
            if(b.getId() == id) return b;
        }
        return null;
    }

    //Finds registered member by their ID
    public Member findMemberById(String memberId){
        for(Member m : members){
            if(m.getMemID().equalsIgnoreCase(memberId)) return m;
        }
        return null;
    }

    //Counts how many active loans a member has that hadn't been returned yet.
    private int countActiveLoans(Member member){
        int count = 0;
        for (Loan loan : loans){
            if(!loan.isReturned() && loan.getMember().equals(member)){
                count++;
            }
        }
        return count;
    }

    //Borrows a book to a member on a given date while enforcing logic rules, then creates a new loan and reduces the book's available copies
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
        if (hasActiveLoan(member, book)){
            throw new IllegalStateException("Member Already has this book on loan");
        }
        if (!book.borrowCopy()){
            throw new IllegalStateException("No Available copies of this book");
        }

        Loan loan = new Loan(book, member, loanDate);
        loans.add(loan);

        return true;
    }

    //Checks if the member has the same book on an active loan to prevent borrowing the same book twice
    private boolean hasActiveLoan(Member member, Book book){
        for(Loan loan : loans){
            if(!loan.isReturned() && loan.getMember().equals(member) && loan.getBook().equals(book)){
                return true;
            }
        }
        return false;
    }

    //marks the loan as returned and checks for the due date, then calculates late fee if it was returned later then it's due date.
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

    //Adds a book to the library collections (Yet to be implemented)
    public void addBook(Book book){
        books.add(book);
    }
    //Adds a member to the library collections (Yet to be implemented)
    public void addMember(Member member){
        members.add(member);
    }


    //shows a copy of the book list
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

    //To view full loan history for members(active and returned) (Not Yet implemented)
    /*public List<Loan> getLoanHistory(Member member) {
        List<Loan> result = new ArrayList<>();

        for (Loan loan : loans) {
            if (loan.getMember().equals(member)) {
                result.add(loan);
            }
        }
        return result;
    }*/

    //Searches books using the searchable.matches() rule
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
