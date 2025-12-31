public class Main {
    public static void main(String[] args) throws Exception {


        //CSV data Files
        String booksPath = "C:\\Users\\KekkersV2\\Desktop\\LibManagement\\LibManagement\\src\\data\\books.csv";
        String membersPath = "C:\\Users\\KekkersV2\\Desktop\\LibManagement\\LibManagement\\src\\data\\members.csv";
        String loansPath = "C:\\Users\\KekkersV2\\Desktop\\LibManagement\\LibManagement\\src\\data\\loans.csv";

        //Create the lib manager (handles all borrowing/returning logic)
        LibManager manager = new LibManager();

        //Load data from CSV files into the system
        manager.loadFromCsv(booksPath, membersPath, loansPath);

        new LibraryCLI(manager, booksPath, loansPath).run();
    }
}
