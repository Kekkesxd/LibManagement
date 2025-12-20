public class Main {
    public static void main(String[] args) throws Exception {

        LibManager manager = new LibManager();

        // load all CSV data
        manager.loadFromCsv("C:\\Users\\KekkersV2\\Desktop\\LibManagement\\LibManagement\\src\\data\\books.csv",
                "C:\\Users\\KekkersV2\\Desktop\\LibManagement\\LibManagement\\src\\data\\members.csv",
                "C:\\Users\\KekkersV2\\Desktop\\LibManagement\\LibManagement\\src\\data\\loans.csv");

        // start CLI
        new LibraryCLI(manager).run();
    }
}
