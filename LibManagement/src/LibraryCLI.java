import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.time.LocalDate;

public class LibraryCLI {
    private final String booksPath;
    private final String loansPaths;
    private final LibManager manager;
    private final Scanner scanner = new Scanner(System.in);

    public LibraryCLI(LibManager manager, String booksPath, String loansPaths) {
        this.manager = manager;
        this.booksPath = booksPath;
        this.loansPaths = loansPaths;
    }

    public void run() {
        while (true) {
            System.out.println("\n=== Library Menu ===");
            System.out.println("1) Search books");
            System.out.println("2) View member active loans");
            System.out.println("3) Borrow book");
            System.out.println("4) Return book");
            System.out.println("5) Quit");
            System.out.print("Choice: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> searchBooksFlow();
                case "2" -> viewActiveLoansFlow();
                case "3" -> borrowBookFlow();
                case "4" -> returnBookFlow();
                case "5" -> {return;}
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    //Search for Books
    private void searchBooksFlow() {
        while (true) {
            System.out.println("\n=== Library Search ===");
            System.out.print("Search keyword (or 'b' to go back): ");
            String keyword = scanner.nextLine().trim();

            if (keyword.equalsIgnoreCase("b")) return;

            List<Book> results = new ArrayList<>(manager.searchBooks(keyword));
            if (results.isEmpty()) {
                System.out.println("No results found.");
                continue;
            }

            System.out.println("Sort by: 1) Title  2) Author  3) Availability");
            System.out.print("Choice: ");
            String sortChoice = scanner.nextLine().trim();

            if (sortChoice.equals("1")) {
                results.sort((a, b) -> a.getTitle().compareToIgnoreCase(b.getTitle()));
            } else if (sortChoice.equals("2")) {
                results.sort((a, b) -> a.getAuthor().compareToIgnoreCase(b.getAuthor()));
            } else if (sortChoice.equals("3")) {
                results.sort((a, b) -> Integer.compare(b.getAvailableCopies(), a.getAvailableCopies()));
            }

            int pageSize = 30;
            Paginator<Book> paginator = new Paginator<>(results, pageSize);
            int page = 0;

            while (true) {
                System.out.println("\nPage " + (page + 1) + "/" + paginator.totalPages()
                        + " | results=" + results.size());

                for (Book b : paginator.getPage(page)) {
                    System.out.println(
                            "#" + b.getId() + " | " +
                                    b.getTitle() + " | " +
                                    b.getAuthor() + " | " +
                                    b.getCategory() + " | " +
                                    "available=" + b.getAvailableCopies() + "/" + b.getTotalCopies()
                    );
                }

                System.out.println("\nCommands: [n] next  [p] prev  [r] new search  [b] back to menu");
                String cmd = scanner.nextLine().trim();

                if (cmd.equalsIgnoreCase("n") && page < paginator.totalPages() - 1) {
                    page++;
                } else if (cmd.equalsIgnoreCase("p") && page > 0) {
                    page--;
                } else if (cmd.equalsIgnoreCase("r")) {
                    break;
                } else if (cmd.equalsIgnoreCase("b")) {
                    return;
                }
            }
        }
    }

    //Viewing Active Loans
    private void viewActiveLoansFlow() {
        System.out.print("Enter member ID (or 'b' to go back): ");
        String memberId = scanner.nextLine().trim();

        if (memberId.equalsIgnoreCase("b")) return;

        Member member = manager.findMemberById(memberId);
        if (member == null) {
            System.out.println("Member not found.");
            return;
        }

        List<Loan> active = manager.getActiveLoansMember(member);
        if (active.isEmpty()) {
            System.out.println("No active loans for " + member.getName() + ".");
            return;
        }

        System.out.println("\nActive loans for " + member.getName() + " (" + member.getMemID() + ")");
        for (Loan loan : active) {
            Book b = loan.getBook();
            System.out.println("#" + b.getId() + " | " + b.getTitle() + " | due: " + loan.getDueDate());
        }
    }

    private void borrowBookFlow() {
        System.out.print("Enter Member ID (or 'b' to go back): ");
        String memberId = scanner.nextLine().trim();
        if (memberId.equalsIgnoreCase("b")) return;

        Member member = manager.findMemberById(memberId);
        if (member == null) {
            System.out.println("Member not found");
            return;
        }

        System.out.print("Enter book ID (or 'b' to go back): ");
        String bookInput = scanner.nextLine().trim();
        if (bookInput.equalsIgnoreCase("b")) return;

        int bookid;
        try {
            bookid = Integer.parseInt(bookInput);
        } catch (NumberFormatException e) {
            System.out.println("Book ID must be a number");
            return;
        }

        Book book = manager.findBookById(bookid);
        if (book == null) {
            System.out.println("Book not found");
            return;
        }
        try {
            manager.borrowBook(member, book, LocalDate.now());
            System.out.println("Borrowed successfully");
            System.out.println("Due date will be: " + LocalDate.now().plusDays(14));

            //Saving info to Csv file
            CsvSaver.saveBooks(booksPath, manager.getBooks());
            CsvSaver.saveLoans(loansPaths, manager.getLoans());
            System.out.println("Saved to CSV");

        } catch (RuntimeException ex) {
            System.out.println("Borrow failed: " + ex.getMessage());
        }catch (IOException e){
            System.out.println("Borrowed but failed to save to CSV: " + e.getMessage());
        }
    }

    private void returnBookFlow(){
        System.out.print("Enter Member ID (or 'b' to go back): ");
        String memberId = scanner.nextLine().trim();
        if(memberId.equalsIgnoreCase("b")) return;

        Member member = manager.findMemberById(memberId);
        if(member == null){
            System.out.println("Member not found");
            return;
        }

        System.out.print("Enter book ID (or 'b' to go back): ");
        String bookinput = scanner.nextLine().trim();
        if(bookinput.equalsIgnoreCase("b")) return;

        int bookId;
        try {
            bookId = Integer.parseInt(bookinput);
        }catch (NumberFormatException e) {
            System.out.println("Book ID must be a number");
            return;
        }

        Book book = manager.findBookById(bookId);
        if(book == null){
            System.out.println("Book not found");
            return;
        }

        try {
            double fee = manager.returnBook(member, book, LocalDate.now());

            if( fee > 0){
                System.out.println("Book was returned late");
                System.out.println("Late fee amounts to: " + fee);
            } else {
                System.out.println("Book was returned on time");
            }

            //Saving info to Csv file
            CsvSaver.saveBooks(booksPath, manager.getBooks());
            CsvSaver.saveLoans(loansPaths, manager.getLoans());
            System.out.println("Saved to CSV");

        }catch (RuntimeException ex){
            System.out.println("Return failed:" + ex.getMessage());
        } catch (IOException ex){
            System.out.println("Returned but failed to save to CSV: " + ex.getMessage());
        }
    }
}
