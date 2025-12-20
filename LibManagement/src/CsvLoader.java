import java.io.*;
import java.util.*;
import java.time.LocalDate;

public class CsvLoader {

    public static List<Book> loadBooks(String filePath) throws IOException {
        List<Book> books = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line = br.readLine(); // header

            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;

                String[] p = line.split(",", -1);

                int id = Integer.parseInt(p[0].trim());
                String title = p[1].trim();
                String author = p[2].trim();
                int year = Integer.parseInt(p[3].trim());
                String category = p[4].trim();
                int totalCopies = Integer.parseInt(p[5].trim());
                int availableCopies = Integer.parseInt(p[6].trim());

                Book book = new Book(id, title, author, year, category, totalCopies);

                // adjust availability down to match CSV
                while (book.getAvailableCopies() > availableCopies) {
                    book.borrowCopy();
                }

                books.add(book);
            }
        }

        return books;
    }

    public static List<Member> loadMembers(String filePath) throws IOException {
        List<Member> members = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line = br.readLine(); // header

            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;

                String[] p = line.split(",", -1);

                String type = p[0].trim();
                String memberId = p[1].trim();
                String name = p[2].trim();
                String email = p[3].trim();
                int number = Integer.parseInt(p[4].trim());
                int maxBooksAllowed = Integer.parseInt(p[5].trim());

                if (type.equalsIgnoreCase("REG")) {
                    members.add(
                            new Member(name, memberId, email, number, maxBooksAllowed)
                    );
                } else if (type.equalsIgnoreCase("STU")) {
                    String studentId = p[6].trim();
                    String department = p[7].trim();

                    members.add(
                            new StudentMember(
                                    name, memberId, email, number,
                                    maxBooksAllowed, studentId, department
                            )
                    );
                } else {
                    throw new IllegalArgumentException("Unknown member type: " + type);
                }
            }
        }

        return members;
    }

    public static List<Loan> loadLoans(String filePath, List<Book> books, List<Member> members) throws IOException {
        List<Loan> loans = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line = br.readLine(); // header

            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;

                String[] p = line.split(",", -1);

                int bookId = Integer.parseInt(p[0].trim());
                String memberId = p[1].trim();

                LocalDate loanDate = LocalDate.parse(p[2].trim());
                LocalDate dueDate = LocalDate.parse(p[3].trim());

                String returnDateStr = p[4].trim();
                boolean returned = Boolean.parseBoolean(p[5].trim());

                Book book = findBookById(books, bookId);
                Member member = findMemberById(members, memberId);

                if (book == null) throw new IllegalStateException("Loan refers to missing bookId: " + bookId);
                if (member == null) throw new IllegalStateException("Loan refers to missing memberId: " + memberId);

                Loan loan = new Loan(book, member, loanDate);

                // Optional strict check: CSV dueDate must match fixed 14-day rule
                if (!loan.getDueDate().equals(dueDate)) {
                    throw new IllegalStateException("CSV dueDate does not match fixed rule for bookId=" + bookId);
                }

                if (returned) {
                    if (returnDateStr.isEmpty()) {
                        throw new IllegalStateException("returned=true but returnDate empty for bookId=" + bookId);
                    }
                    loan.markReturned(LocalDate.parse(returnDateStr));
                } else {
                    // If loan is active, the book must be unavailable for that copy
                    // so we "borrow" a copy now to reflect the active loan in availability
                    if (!book.borrowCopy()) {
                        throw new IllegalStateException("Not enough copies for active loan bookId=" + bookId);
                    }
                }

                loans.add(loan);
            }
        }

        return loans;
    }

    private static Book findBookById(List<Book> books, int id) {
        for (Book b : books) {
            if (b.getId() == id) return b;
        }
        return null;
    }

    private static Member findMemberById(List<Member> members, String memberId) {
        for (Member m : members) {
            if (m.getMemID().equals(memberId)) return m;
        }
        return null;
    }


}
