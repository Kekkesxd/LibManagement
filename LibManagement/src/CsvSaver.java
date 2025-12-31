import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class CsvSaver {

    public static void saveBooks(String filePath, List<Book> books) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            bw.write("id,title,author,year,category,totalCopies,availableCopies");
            bw.newLine();

            for (Book b : books) {
                bw.write(b.getId() + "," +
                        escape(b.getTitle()) + "," +
                        escape(b.getAuthor()) + "," +
                        b.getYear() + "," +
                        escape(b.getCategory()) + "," +
                        b.getTotalCopies() + "," +
                        b.getAvailableCopies());
                bw.newLine();
            }
        }
    }

    public static void saveLoans(String filePath, List<Loan> loans) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            bw.write("bookId,memberId,loanDate,dueDate,returnDate,returned");
            bw.newLine();

            for (Loan l : loans) {
                int bookId = l.getBook().getId();
                String memberId = l.getMember().getMemID();

                LocalDate loanDate = l.getLoanDate();
                LocalDate dueDate = l.getDueDate();
                LocalDate returnDate = l.getReturnDate();

                bw.write(bookId + "," +
                        memberId + "," +
                        loanDate + "," +
                        dueDate + "," +
                        (returnDate == null ? "" : returnDate) + "," +
                        l.isReturned());
                bw.newLine();
            }
        }
    }

    // Minimal CSV escaping (handles commas/quotes)
    private static String escape(String s) {
        if (s == null) return "";
        if (s.contains(",") || s.contains("\"")) {
            s = s.replace("\"", "\"\"");
            return "\"" + s + "\"";
        }
        return s;
    }
}
