public class Main {
    public static void main(String[] args) {

        // Create a new member
        Member m = new Member();

        // Set member details
        m.setName("Alice");
        m.setMemID("M001");
        m.setEmail("alice@example.com");
        m.setNumber(12345678);
        m.setBooksborrowed(1);

        // Print everything to verify
        System.out.println("=== Member Information ===");
        System.out.println("Name: " + m.getName());
        System.out.println("ID: " + m.getMemID());
        System.out.println("Email: " + m.getEmail());
        System.out.println("Phone Number: " + m.getNumber());
        System.out.println("Books Borrowed: " + m.getBooksborrowed());
        System.out.println("Max Books Allowed: " + m.getMaxBooksAllowed());

        // Test changing max books allowed
        System.out.println("\nUpdating max books allowed to 5...");
        m.setMaxBooksAllowed(5);

        System.out.println("New Max Books Allowed: " + m.getMaxBooksAllowed());
    }
}
