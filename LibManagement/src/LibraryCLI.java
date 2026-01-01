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


    //Construct stores manager and file paths for saving
    public LibraryCLI(LibManager manager, String booksPath, String loansPaths) {
        this.manager = manager;
        this.booksPath = booksPath;
        this.loansPaths = loansPaths;
    }

    //Main menu loop: keeps running until Quit is picked
    public void run() {
        while (true) {
            System.out.println("\n=== Library Menu ===");
            System.out.println("1) Browse Library books");
            System.out.println("2) Search books");
            System.out.println("3) View member active loans");
            System.out.println("4) Borrow book");
            System.out.println("5) Return book");
            System.out.println("6) Quit");
            System.out.print("Choice: ");

            String choice = scanner.nextLine().trim();

            //Switch cases for the flow method based on user choice
            switch (choice) {
                case "1" -> browseBooksFlow();
                case "2" -> searchBooksFlow();
                case "3" -> viewActiveLoansFlow();
                case "4" -> borrowBookFlow();
                case "5" -> returnBookFlow();
                case "6" -> {return;}
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private void browseBooksFlow(){
        List<Book> results = new ArrayList<>(manager.getBooks());

        if(results.isEmpty()){
            System.out.println("Library is Empty");
            return;
        }


        System.out.println("Sort by: 1) Title  2) Author  3) Category 4) Availability 5) ID");
        System.out.print("Choice: ");
        String sortChoice = scanner.nextLine().trim();

        switch (sortChoice) {
            case "1" ->
                    results.sort((a, b) -> a.getTitle().compareToIgnoreCase(b.getTitle()));
            case "2" ->
                    results.sort((a, b) -> a.getAuthor().compareToIgnoreCase(b.getAuthor()));
            case "3" ->
                    results.sort((a, b) -> a.getCategory().compareToIgnoreCase(b.getCategory()));
            case "4" ->
                    results.sort((a, b) -> Integer.compare(b.getAvailableCopies(), a.getAvailableCopies()));
            case "5" ->
                    results.sort((a, b) -> Integer.compare(a.getId(), b.getId()));
            default -> {
                System.out.println("Invalid sort option....Defaulting to ID");
                results.sort((a, b) -> Integer.compare(a.getId(), b.getId()));
            }
        }

        int pageSize = 25; //How many books shown per page
        Paginator<Book> paginator = new Paginator<>(results, pageSize); //Displays all books with sorting + pagination
        int page = 0;

        while (true) {
            System.out.println("\n=== Browse Library ===");
            System.out.println("Page " + (page + 1) + "/" + paginator.totalPages()
                    + " | total books=" + results.size());

            for (Book b : paginator.getPage(page)) {
                System.out.println(
                        "#" + b.getId() + " | " +
                                b.getTitle() + " | " +
                                b.getAuthor() + " | " +
                                b.getCategory() + " | " +
                                "available=" + b.getAvailableCopies() + "/" + b.getTotalCopies()
                );
            }

            System.out.println("\nCommands: [n] next  [p] prev  [b] back to menu");
            String cmd = scanner.nextLine().trim();

            if (cmd.equalsIgnoreCase("n") && page < paginator.totalPages() - 1) {
                page++;
            } else if (cmd.equalsIgnoreCase("p") && page > 0) {
                page--;
            } else if (cmd.equalsIgnoreCase("b")) {
                return;
            }
        }
    }
    //Search for Books by keyword, allows sorting and displays results with pagination
    private void searchBooksFlow() {
        while (true) {
            System.out.println("\n=== Library Search ===");
            System.out.print("Search keyword (or 'b' to go back): ");
            String keyword = scanner.nextLine().trim();

            if (keyword.equalsIgnoreCase("b")) return;

            //Search using LibManager
            List<Book> results = new ArrayList<>(manager.searchBooks(keyword));
            if (results.isEmpty()) {
                System.out.println("No results found.");
                continue;
            }

            //Asking the user how they want to sort the results
            System.out.println("Sort by: 1) Title  2) Author  3) Availability");
            System.out.print("Choice: ");
            String sortChoice = scanner.nextLine().trim();

            //Sorting results
            switch (sortChoice) {
                case "1" ->
                        results.sort((a, b) -> a.getTitle().compareToIgnoreCase(b.getTitle()));
                case "2" ->
                        results.sort((a, b) -> a.getAuthor().compareToIgnoreCase(b.getAuthor()));
                case "3" ->
                        results.sort((a, b) -> Integer.compare(b.getAvailableCopies(), a.getAvailableCopies()));
                default ->
                        System.out.println("Invalid sort option, Keeping original order");
            }


            //How many books there is per page
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

                //Pagination commands
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

    //Viewing Active Loans for a specific member
    private void viewActiveLoansFlow() {
        System.out.print("Enter member ID (or 'b' to go back): ");
        String memberId = scanner.nextLine().trim();

        if (memberId.equalsIgnoreCase("b")) return;

        //Find member by ID
        Member member = manager.findMemberById(memberId);
        if (member == null) {
            System.out.println("Member not found.");
            return;
        }

        // Get the active loans for that member
        List<Loan> active = manager.getActiveLoansMember(member);
        if (active.isEmpty()) {
            System.out.println("No active loans for " + member.getName() + ".");
            return;
        }

        //Displaying the loan details
        System.out.println("\nActive loans for " + member.getName() + " (" + member.getMemID() + ")");
        for (Loan loan : active) {
            Book b = loan.getBook();
            System.out.println("#" + b.getId() + " | " + b.getTitle() + " | due: " + loan.getDueDate());
        }
    }

    //reads IDs, calls borrowBook from libManager and saves the changes to CSV
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

        //Converts user input from string to int so it can be used as a Book ID
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

    //marks books returned, prints the late fee and saves changes to CSV
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
