public class Main {
    public static void main(String[] args) throws Exception {

        String booksPath = "C:\\Users\\KekkersV2\\Desktop\\LibManagement\\LibManagement\\src\\data\\books.csv";
        String membersPath = "C:\\Users\\KekkersV2\\Desktop\\LibManagement\\LibManagement\\src\\data\\members.csv";
        String loansPath = "C:\\Users\\KekkersV2\\Desktop\\LibManagement\\LibManagement\\src\\data\\loans.csv";

        LibManager manager = new LibManager();
        manager.loadFromCsv(booksPath, membersPath, loansPath);

        new LibraryCLI(manager, booksPath, loansPath).run();
    }
}
