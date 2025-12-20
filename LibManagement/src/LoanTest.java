import org.junit.jupiter.api.Test;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class LoanTest {
    @Test
    void loan_initialState_isCorrect() {
        Book book = new Book(1, "Test", "Author", 2023, "Cat", 1);
        Member member = new Member("A", "M1", "a@test.com", 123);
        LocalDate loanDate = LocalDate.of(2024, 1, 1);

        Loan loan = new Loan(book, member, loanDate);

        assertFalse(loan.isReturned());
        assertNull(loan.getReturnDate());
        assertEquals(loanDate.plusDays(14), loan.getDueDate());
    }

    @Test
    void markReturned_marksLoanAndReturnsBook() {
        Book book = new Book(1, "Test", "Author", 2023, "Cat", 1);
        Member member = new Member("A", "M1", "a@test.com", 123);
        LocalDate loanDate = LocalDate.of(2024, 1, 1);

        book.borrowCopy(); // simulate borrow
        Loan loan = new Loan(book, member, loanDate);

        LocalDate returnDate = LocalDate.of(2024, 1, 10);
        loan.markReturned(returnDate);

        assertTrue(loan.isReturned());
        assertEquals(returnDate, loan.getReturnDate());
        assertEquals(1, book.getAvailableCopies());
    }

    @Test
    void markReturned_calledTwice_doesNothingSecondTime() {
        Book book = new Book(1, "Test", "Author", 2023, "Cat", 1);
        Member member = new Member("A", "M1", "a@test.com", 123);
        LocalDate loanDate = LocalDate.now();

        book.borrowCopy();
        Loan loan = new Loan(book, member, loanDate);

        loan.markReturned(LocalDate.now());
        loan.markReturned(LocalDate.now().plusDays(1));

        assertEquals(1, book.getAvailableCopies());
    }

}
