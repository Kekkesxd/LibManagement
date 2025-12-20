import org.junit.jupiter.api.Test;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class LibManagerTest {

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
//Members viewing their own loans
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


}
