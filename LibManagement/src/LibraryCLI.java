import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class LibraryCLI {

    private final LibManager manager;
    private final Scanner scanner = new Scanner(System.in);

    public LibraryCLI(LibManager manager) {
        this.manager = manager;
    }

    public void run() {
        while (true) {
            System.out.println("\n=== Library Search ===");
            System.out.print("Search keyword (or 'q' to quit): ");
            String keyword = scanner.nextLine().trim();

            if (keyword.equalsIgnoreCase("q")) break;

            List<Book> results = new ArrayList<>(manager.searchBooks(keyword));
            if (results.isEmpty()) {
                System.out.println("No results found.");
                continue;
            }

            // Sorting menu
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

            // Pagination
            int pageSize = 30; // How many books shown per page
            Paginator<Book> paginator = new Paginator<>(results, pageSize);
            int page = 0;

            while (true) {
                System.out.println("\nPage " + (page + 1) + "/" + paginator.totalPages()
                        + " | results=" + results.size());

                List<Book> pageItems = paginator.getPage(page);
                for (Book b : pageItems) {
                    System.out.println(
                            "#" + b.getId() + " | " +
                                    b.getTitle() + " | " +
                                    b.getAuthor() + " | " +
                                    b.getCategory() + " | " +
                                    "available=" + b.getAvailableCopies() + "/" + b.getTotalCopies()
                    );
                }

                System.out.println("\nCommands: [n] next  [p] prev  [r] new search  [q] quit");
                String cmd = scanner.nextLine().trim();

                if (cmd.equalsIgnoreCase("n") && page < paginator.totalPages() - 1) {
                    page++;
                } else if (cmd.equalsIgnoreCase("p") && page > 0) {
                    page--;
                } else if (cmd.equalsIgnoreCase("r")) {
                    break; // break to new search
                } else if (cmd.equalsIgnoreCase("q")) {
                    return; // quit program
                }
            }
        }
    }
}
