import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BookTest {

    //Testing if it reduces the available copies when borrowing
    @Test
    void borrowCopy_reducesAvailableCopies() {
        Book book = new Book(1, "Test", "Author", 2023, "Cat", 2);

        assertTrue(book.borrowCopy());
        System.out.println("Borrowed Copy now only: " + book.getAvailableCopies() + " Available out of " + book.getTotalCopies());
        assertEquals(1, book.getAvailableCopies());
    }

    //Testing if it fails or not to borrow when there's no more available copies
    @Test
    void borrowCopy_failsWhenNoCopiesLeft() {
        Book book = new Book(1, "Test", "Author", 2023, "Cat", 1);

        assertTrue(book.borrowCopy());      // takes the only copy
        System.out.println("Borrow Successful!...now only " + book.getAvailableCopies() + " copies available" );
        assertFalse(book.borrowCopy());     // should fail now
        System.out.println("Borrow Failed....No available copies left: " + book.getAvailableCopies());
        assertEquals(0, book.getAvailableCopies());
    }

    //Testing Construct ---- not allowing a book with 0 total copies to exist
    @Test
    void constructor_throwsException_whenTotalCopiesIsZero() {
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                new Book(1, "Test", "Author", 2023, "Cat", 0)
        );
        System.out.println("Exception thrown: " + ex.getMessage());
    }

    //Testing return method
    @Test
    void returnCopy_increasesAvailableCopiesUpToTotal() {
        Book book = new Book(1, "Test", "Author", 2023, "Cat", 1);

        book.borrowCopy();
        System.out.println("Now only: " + book.getAvailableCopies());
        book.returnCopy();
        System.out.println("Returned: " + book.getAvailableCopies());

        assertEquals(1, book.getAvailableCopies());
    }


}
