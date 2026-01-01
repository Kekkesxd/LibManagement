import org.junit.jupiter.api.Test;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class LibManagerTest {


    //Tests if borrowing succeeds when member and book are registered, limit (max books allowed) not reached and a copy is available
    @Test
    void borrowBook_succeeds_whenRulesAreSatisfied() {
        LibManager manager = new LibManager();

        Book book = new Book(1, "Test", "Author", 2023, "Cat", 1);
        Member member = new Member("A", "M1", "a@test.com", 123);

        manager.addBook(book);
        manager.addMember(member);

        assertTrue(manager.borrowBook(member, book, LocalDate.now()));
        assertEquals(0, book.getAvailableCopies());
    }


    //Test borrowing when the maxBooksAllowed is exceeded
    @Test
    void borrowBook_throwsException_whenLimitExceeded() {
        LibManager manager = new LibManager();

        Book book1 = new Book(1, "B1", "A", 2023, "Cat", 1);
        Book book2 = new Book(2, "B2", "A", 2023, "Cat", 1);
        Book book3 = new Book(3, "B3", "A", 2023, "Cat", 1);

        Member member = new Member("A", "M1", "a@test.com", 123);

        manager.addMember(member);
        manager.addBook(book1);
        manager.addBook(book2);
        manager.addBook(book3);

        manager.borrowBook(member, book1, LocalDate.now());
        manager.borrowBook(member, book2, LocalDate.now());

        assertThrows(IllegalStateException.class, () ->
                manager.borrowBook(member, book3, LocalDate.now())
        );
    }
    //Testing returnBook
    @Test
    void returnBook_marksLoanReturned_andRestoresAvailability() {
        LibManager manager = new LibManager();

        Book book = new Book(1, "Test", "Author", 2023, "Cat", 1);
        Member member = new Member("A", "M1", "a@test.com", 123);

        manager.addBook(book);
        manager.addMember(member);

        manager.borrowBook(member, book, LocalDate.now());
        assertEquals(0, book.getAvailableCopies());

        manager.returnBook(member, book, LocalDate.now());

        assertEquals(1, book.getAvailableCopies());
        assertEquals(0, manager.getActiveLoansMember(member).size());
    }
    //Testing member loan view, it shows only the loans for only one member
    @Test
    void getActiveLoansForMember_returnsOnlyMembersLoans() {
    LibManager manager = new LibManager();

    Book book1 = new Book(1, "B1", "A", 2023, "Cat", 1);
    Book book2 = new Book(2, "B2", "A", 2023, "Cat", 1);

    Member m1 = new Member("A", "M1", "a@test.com", 123);
    Member m2 = new Member("B", "M2", "b@test.com", 456);

    manager.addBook(book1);
    manager.addBook(book2);
    manager.addMember(m1);
    manager.addMember(m2);

    manager.borrowBook(m1, book1, LocalDate.now());
    manager.borrowBook(m2, book2, LocalDate.now());

    assertEquals(1, manager.getActiveLoansMember(m1).size());
    assertEquals(1, manager.getActiveLoansMember(m2).size());
    }
    //Testing member borrow when there's no copies left
    @Test
    void borrowBook_throwsException_whenNoCopiesAvailable() {
        LibManager manager = new LibManager();
        Book book = new Book(1, "Test", "Author", 2023, "Cat", 1);
        Member m1 = new Member("A", "M1", "a@test.com", 123);
        Member m2 = new Member("B", "M2", "b@test.com", 456);

        manager.addBook(book);
        manager.addMember(m1);
        manager.addMember(m2);

        manager.borrowBook(m1, book, LocalDate.now());

        assertThrows(IllegalStateException.class, () ->
                manager.borrowBook(m2, book, LocalDate.now())
        );
    }

    //Testing double borrowing (trying to borrow the same book twice)
    @Test
    void borrowBook_throwsException_whenSameBookAlreadyLoanedToMember() {
        LibManager manager = new LibManager();
        Book book = new Book(1, "Test", "Author", 2023, "Cat", 2);
        Member member = new Member("A", "M1", "a@test.com", 123);

        manager.addBook(book);
        manager.addMember(member);

        manager.borrowBook(member, book, LocalDate.now());

        assertThrows(IllegalStateException.class, () ->
                manager.borrowBook(member, book, LocalDate.now())
        );
    }
    //Testing return fee when late
    @Test
    void returnBook_returnsLateFee_whenLate() {
        LibManager manager = new LibManager();
        Book book = new Book(1, "Test", "Author", 2023, "Cat", 1);
        Member member = new Member("A", "M1", "a@test.com", 123);

        manager.addBook(book);
        manager.addMember(member);

        LocalDate loanDate = LocalDate.of(2025, 1, 1);
        manager.borrowBook(member, book, loanDate);

        LocalDate returnDate = loanDate.plusDays(16); // 2 days late (due is +14)
        double fee = manager.returnBook(member, book, returnDate);

        assertEquals(10.0, fee); // 2 * 5
    }
    //Testing return fee when late for student member
    @Test
    void returnBook_usesStudentLateFeeRate_forStudentMember() {
        LibManager manager = new LibManager();
        Book book = new Book(1, "Test", "Author", 2023, "Cat", 1);
        Member student = new StudentMember("S", "M2", "s@test.com", 777, "S123", "CS");

        manager.addBook(book);
        manager.addMember(student);

        LocalDate loanDate = LocalDate.of(2025, 1, 1);
        manager.borrowBook(student, book, loanDate);

        LocalDate returnDate = loanDate.plusDays(16); // 2 days late
        double fee = manager.returnBook(student, book, returnDate);

        assertEquals(5.0, fee, 0.0001); // 2 * 2.5
    }
}
