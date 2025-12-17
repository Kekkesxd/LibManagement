import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BookTest {

    @Test
    void borrowCopy_reducesAvailableCopies() {
        Book book = new Book(1, "Test", "Author", 2023, "Cat", 2);

        assertTrue(book.borrowCopy());
        assertEquals(1, book.getAvailableCopies());
    }
    @Test
    void borrowCopy_failsWhenNoCopiesLeft() {
        Book book = new Book(1, "Test", "Author", 2023, "Cat", 1);

        assertTrue(book.borrowCopy());      // takes the only copy
        assertFalse(book.borrowCopy());     // should fail now
        assertEquals(0, book.getAvailableCopies());
    }

    @Test
    void constructor_throwsException_whenTotalCopiesIsZero() {
        assertThrows(IllegalArgumentException.class, () ->
                new Book(1, "Test", "Author", 2023, "Cat", 0)
        );
    }

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
