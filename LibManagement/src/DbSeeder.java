import java.io.BufferedWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.*;

/**
 * Generates books.csv, members.csv, loans.csv.
 * Books are fetched from Open Library Search API (real titles/authors/years).
 *
 * IMPORTANT: Your CsvLoader uses line.split(",") (not a real CSV parser),
 * so we sanitize commas out of fields to avoid breaking column alignment.
 *
 * Open Library Search API docs: https://openlibrary.org/dev/docs/api/search
 */
public class DbSeeder{

    // ======= CONFIG (change these) =======
    private static final Path OUT_DIR = Path.of("src/data");
    private static final int N_BOOKS = 250;
    private static final int N_MEMBERS = 120;
    private static final int N_ACTIVE_LOANS = 100;
    private static final int N_RETURNED_LOANS = 400;

    // categories you already use in your project
    private static final List<String> CATEGORIES = List.of(
            "Programming", "Computer Science", "Data Science", "AI",
            "Science", "History", "Fiction", "Fantasy",
            "Philosophy", "Business", "Economics"
    );

    // map your categories -> Open Library "subject" queries
    // (you can tweak these words for better results)
    private static final Map<String, List<String>> CATEGORY_TO_SUBJECTS = Map.ofEntries(
            Map.entry("Programming",      List.of("programming", "software", "coding", "software_engineering")),
            Map.entry("Computer Science", List.of("computer_science", "algorithms", "data_structures", "operating_systems")),
            Map.entry("Data Science",     List.of("data_science", "statistics", "data_mining")),
            Map.entry("AI",               List.of("artificial_intelligence", "machine_learning", "neural_networks")),
            Map.entry("Science",          List.of("science", "physics", "biology", "chemistry")),
            Map.entry("History",          List.of("history", "world_history", "ancient_history", "modern_history")),
            Map.entry("Fiction",          List.of("fiction", "novels", "literature")),
            Map.entry("Fantasy",          List.of("fantasy", "epic_fantasy")),
            Map.entry("Philosophy",       List.of("philosophy", "ethics", "metaphysics")),
            Map.entry("Business",         List.of("business", "management", "entrepreneurship")),
            Map.entry("Economics",        List.of("economics", "microeconomics", "macroeconomics"))
    );



    // ====================================

    private static final Random rng = new Random(42);

    // Your exact CSV headers
    private static final String BOOKS_HEADER = "id,title,author,year,category,totalCopies,availableCopies";
    private static final String MEMBERS_HEADER = "type,memberId,name,email,number,maxBooksAllowed,studentId,department";
    private static final String LOANS_HEADER = "bookId,memberId,loanDate,dueDate,returnDate,returned";

    private record BookRow(int id, String title, String author, int year, String category,
                           int totalCopies, int availableCopies) {}

    private record MemberRow(String type, String memberId, String name, String email, int number,
                             int maxBooksAllowed, String studentId, String department) {}

    private record LoanRow(int bookId, String memberId, LocalDate loanDate, LocalDate dueDate,
                           LocalDate returnDate, boolean returned) {}

    private static final HttpClient http = HttpClient.newHttpClient();

    public static void main(String[] args) throws Exception {
        Files.createDirectories(OUT_DIR);

        List<BookRow> books = fetchRealBooks(N_BOOKS);
        List<MemberRow> members = generateMembers(N_MEMBERS);

        LoanGenResult loanResult = generateLoansAndUpdateAvailability(books, members, N_ACTIVE_LOANS, N_RETURNED_LOANS);
        books = loanResult.updatedBooks;
        List<LoanRow> loans = loanResult.loans;

        writeBooksCsv(OUT_DIR.resolve("books.csv"), books);
        writeMembersCsv(OUT_DIR.resolve("members.csv"), members);
        writeLoansCsv(OUT_DIR.resolve("loans.csv"), loans);

        System.out.println("✅ Generated real-books CSV DB in: " + OUT_DIR.toAbsolutePath());
        System.out.println("Books: " + books.size());
        System.out.println("Members: " + members.size());
        System.out.println("Loans: " + loans.size());
    }

    // ----------------- REAL BOOK FETCHING -----------------

    /**
     * Fetches books from Open Library Search API by subject and merges into a single list.
     * Uses simple string parsing (no external JSON libs) to keep it plug-and-play.
     *
     * API docs: https://openlibrary.org/dev/docs/api/search
     */
    private static List<BookRow> fetchRealBooks(int targetCount) throws Exception {
        List<BookRow> results = new ArrayList<>();
        Set<String> seenKey = new HashSet<>(); // de-dupe by (title|author|year)

        int idCounter = 1;

        // keep cycling categories until we reach target
        int safety = 0;
        while (results.size() < targetCount && safety < 200) {
            safety++;

            String category = CATEGORIES.get(rng.nextInt(CATEGORIES.size()));
            List<String> subjects = CATEGORY_TO_SUBJECTS.getOrDefault(category, List.of("books"));
            String subject = subjects.get(rng.nextInt(subjects.size()));

            // random-ish page for variety
            int page = 1 + rng.nextInt(10);
            int limit = 50; // decent chunk per request

            String url = "https://openlibrary.org/search.json?" +
                    "subject=" + enc(subject) +
                    "&limit=" + limit +
                    "&page=" + page;

            String json = httpGet(url);

            // extract a bunch of docs blocks and pull fields from them
            List<Map<String, String>> docs = extractDocs(json);

            for (Map<String, String> doc : docs) {
                if (results.size() >= targetCount) break;

                String title = doc.getOrDefault("title", "").trim();
                String author = doc.getOrDefault("author_name", "").trim();
                String yearStr = doc.getOrDefault("first_publish_year", "").trim();

                if (title.isEmpty() || author.isEmpty() || yearStr.isEmpty()) continue;

                int year;
                try {
                    year = Integer.parseInt(yearStr);
                } catch (NumberFormatException ignore) {
                    continue;
                }

                // sanitize commas/quotes/newlines so your split(",") CSV loader won't break
                title = sanitize(title);
                author = sanitize(author);

                String key = title.toLowerCase() + "|" + author.toLowerCase() + "|" + year;
                if (seenKey.contains(key)) continue;
                seenKey.add(key);

                int total = 2 + rng.nextInt(7); // 2..8
                int available = total;

                results.add(new BookRow(idCounter++, title, author, year, category, total, available));
            }
        }

        if (results.size() < targetCount) {
            System.out.println("⚠️ Only fetched " + results.size() + " books. You can lower N_BOOKS or increase safety/pages.");
        }

        return results;
    }

    private static String httpGet(String url) throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("User-Agent", "UniversityLibrarySeeder/1.0 (educational project)")
                .GET()
                .build();

        HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());
        if (res.statusCode() != 200) {
            throw new IOException("HTTP " + res.statusCode() + " fetching: " + url);
        }
        return res.body();
    }

    private static String enc(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }

    /**
     * VERY lightweight JSON pulling:
     * We look for "docs":[{...},{...}] and for each doc we pull:
     * - "title"
     * - "first_publish_year"
     * - first author from "author_name":[...]
     *
     * Good enough for seeding.
     */
    private static List<Map<String, String>> extractDocs(String json) {
        List<Map<String, String>> docs = new ArrayList<>();

        int docsIdx = json.indexOf("\"docs\"");
        if (docsIdx < 0) return docs;

        int arrStart = json.indexOf('[', docsIdx);
        if (arrStart < 0) return docs;

        int arrEnd = findMatchingBracket(json, arrStart, '[', ']');
        if (arrEnd < 0) return docs;

        String arr = json.substring(arrStart + 1, arrEnd);

        // split objects roughly by "},{" boundaries (good enough for this API response)
        String[] objects = arr.split("\\},\\s*\\{");
        for (String objRaw : objects) {
            String obj = objRaw;
            if (!obj.startsWith("{")) obj = "{" + obj;
            if (!obj.endsWith("}")) obj = obj + "}";

            Map<String, String> m = new HashMap<>();
            String title = extractJsonString(obj, "\"title\"");
            String year = extractJsonNumber(obj, "\"first_publish_year\"");
            String author = extractFirstArrayString(obj, "\"author_name\"");

            if (title != null) m.put("title", title);
            if (year != null) m.put("first_publish_year", year);
            if (author != null) m.put("author_name", author);

            docs.add(m);
        }
        return docs;
    }

    private static int findMatchingBracket(String s, int start, char open, char close) {
        int depth = 0;
        for (int i = start; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == open) depth++;
            else if (c == close) {
                depth--;
                if (depth == 0) return i;
            }
        }
        return -1;
    }

    private static String extractJsonString(String obj, String key) {
        int k = obj.indexOf(key);
        if (k < 0) return null;
        int colon = obj.indexOf(':', k);
        if (colon < 0) return null;
        int firstQuote = obj.indexOf('"', colon + 1);
        if (firstQuote < 0) return null;
        int secondQuote = obj.indexOf('"', firstQuote + 1);
        if (secondQuote < 0) return null;
        return obj.substring(firstQuote + 1, secondQuote);
    }

    private static String extractJsonNumber(String obj, String key) {
        int k = obj.indexOf(key);
        if (k < 0) return null;
        int colon = obj.indexOf(':', k);
        if (colon < 0) return null;

        int i = colon + 1;
        while (i < obj.length() && Character.isWhitespace(obj.charAt(i))) i++;

        int j = i;
        while (j < obj.length() && (Character.isDigit(obj.charAt(j)) || obj.charAt(j) == '-')) j++;

        if (j == i) return null;
        return obj.substring(i, j);
    }

    private static String extractFirstArrayString(String obj, String key) {
        int k = obj.indexOf(key);
        if (k < 0) return null;
        int colon = obj.indexOf(':', k);
        if (colon < 0) return null;
        int arrStart = obj.indexOf('[', colon);
        if (arrStart < 0) return null;
        int arrEnd = obj.indexOf(']', arrStart);
        if (arrEnd < 0) return null;

        String arr = obj.substring(arrStart + 1, arrEnd).trim();
        // first quoted item
        int q1 = arr.indexOf('"');
        if (q1 < 0) return null;
        int q2 = arr.indexOf('"', q1 + 1);
        if (q2 < 0) return null;
        return arr.substring(q1 + 1, q2);
    }

    private static String sanitize(String s) {
        // remove commas because your loader uses split(",")
        return s.replace(",", "")
                .replace("\n", " ")
                .replace("\r", " ")
                .replace("\"", "");
    }

    // ----------------- MEMBERS + LOANS -----------------

    private static final String[] DEPARTMENTS = {"CS", "EE", "ME", "BIO", "BUS", "MATH"};
    private static final String[] FIRST_NAMES = {"Alice","Bob","Charlie","Dina","Efe","Fatma","Gokhan","Hakan","Ipek","Jale","Kerem","Lina","Mert","Nisa","Ozan","Pelin"};

    private static List<MemberRow> generateMembers(int n) {
        List<MemberRow> members = new ArrayList<>(n);
        for (int i = 1; i <= n; i++) {
            String memberId = "M" + i;
            String name = FIRST_NAMES[rng.nextInt(FIRST_NAMES.length)] + " " + (char)('A' + (i % 26));
            String email = memberId.toLowerCase() + "@test.com";
            int number = 100000 + rng.nextInt(900000);

            boolean isStudent = (i % 3 != 0); // ~2/3
            if (isStudent) {
                String studentId = "S" + (1000 + rng.nextInt(9000));
                String dept = DEPARTMENTS[rng.nextInt(DEPARTMENTS.length)];
                members.add(new MemberRow("STU", memberId, name, email, number, 4, studentId, dept));
            } else {
                members.add(new MemberRow("REG", memberId, name, email, number, 2, "", ""));
            }
        }
        return members;
    }

    private static class LoanGenResult {
        final List<BookRow> updatedBooks;
        final List<LoanRow> loans;
        LoanGenResult(List<BookRow> updatedBooks, List<LoanRow> loans) {
            this.updatedBooks = updatedBooks;
            this.loans = loans;
        }
    }

    private static LoanGenResult generateLoansAndUpdateAvailability(List<BookRow> books, List<MemberRow> members,
                                                                    int nActive, int nReturned) {
        Map<Integer, Integer> avail = new HashMap<>();
        Map<Integer, Integer> total = new HashMap<>();
        for (BookRow b : books) {
            avail.put(b.id, b.availableCopies);
            total.put(b.id, b.totalCopies);
        }

        Map<String, Integer> maxAllowed = new HashMap<>();
        for (MemberRow m : members) maxAllowed.put(m.memberId, m.maxBooksAllowed);

        Map<String, Integer> activeCount = new HashMap<>();
        for (MemberRow m : members) activeCount.put(m.memberId, 0);

        Set<String> activePair = new HashSet<>(); // memberId#bookId
        List<LoanRow> loans = new ArrayList<>();

        LocalDate today = LocalDate.now();

        // Active loans (affect availability)
        int made = 0;
        int attempts = 0;
        while (made < nActive && attempts < nActive * 80) {
            attempts++;

            MemberRow m = members.get(rng.nextInt(members.size()));
            BookRow b = books.get(rng.nextInt(books.size()));

            if (avail.get(b.id) <= 0) continue;
            if (activeCount.get(m.memberId) >= maxAllowed.get(m.memberId)) continue;

            String key = m.memberId + "#" + b.id;
            if (activePair.contains(key)) continue;

            LocalDate loanDate = today.minusDays(1 + rng.nextInt(30));
            LocalDate dueDate = loanDate.plusDays(14);

            loans.add(new LoanRow(b.id, m.memberId, loanDate, dueDate, null, false));

            avail.put(b.id, avail.get(b.id) - 1);
            activeCount.put(m.memberId, activeCount.get(m.memberId) + 1);
            activePair.add(key);
            made++;
        }

        // Returned loans (history only)
        for (int i = 0; i < nReturned; i++) {
            MemberRow m = members.get(rng.nextInt(members.size()));
            BookRow b = books.get(rng.nextInt(books.size()));

            LocalDate loanDate = today.minusDays(30 + rng.nextInt(365));
            LocalDate dueDate = loanDate.plusDays(14);

            boolean late = rng.nextDouble() < 0.35;
            LocalDate returnDate = late ? dueDate.plusDays(1 + rng.nextInt(10))
                    : dueDate.minusDays(rng.nextInt(4));

            loans.add(new LoanRow(b.id, m.memberId, loanDate, dueDate, returnDate, true));
        }

        // Apply new availability into books.csv
        List<BookRow> updated = new ArrayList<>(books.size());
        for (BookRow b : books) {
            int newAvail = avail.get(b.id);
            newAvail = Math.max(0, Math.min(newAvail, total.get(b.id)));
            updated.add(new BookRow(b.id, b.title, b.author, b.year, b.category, b.totalCopies, newAvail));
        }

        return new LoanGenResult(updated, loans);
    }

    // ----------------- CSV WRITERS -----------------

    private static void writeBooksCsv(Path path, List<BookRow> books) throws IOException {
        try (BufferedWriter bw = Files.newBufferedWriter(path)) {
            bw.write(BOOKS_HEADER);
            bw.newLine();
            for (BookRow b : books) {
                bw.write(b.id + "," + b.title + "," + b.author + "," + b.year + "," + b.category + "," + b.totalCopies + "," + b.availableCopies);
                bw.newLine();
            }
        }
    }

    private static void writeMembersCsv(Path path, List<MemberRow> members) throws IOException {
        try (BufferedWriter bw = Files.newBufferedWriter(path)) {
            bw.write(MEMBERS_HEADER);
            bw.newLine();
            for (MemberRow m : members) {
                bw.write(m.type + "," + m.memberId + "," + m.name + "," + m.email + "," + m.number + "," + m.maxBooksAllowed + "," + m.studentId + "," + m.department);
                bw.newLine();
            }
        }
    }

    private static void writeLoansCsv(Path path, List<LoanRow> loans) throws IOException {
        try (BufferedWriter bw = Files.newBufferedWriter(path)) {
            bw.write(LOANS_HEADER);
            bw.newLine();
            for (LoanRow l : loans) {
                String returnDate = (l.returnDate == null) ? "" : l.returnDate.toString();
                bw.write(l.bookId + "," + l.memberId + "," + l.loanDate + "," + l.dueDate + "," + returnDate + "," + l.returned);
                bw.newLine();
            }
        }
    }
}
