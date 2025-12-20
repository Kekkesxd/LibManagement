import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MemberTest {

    @Test
    void calculateLateFee_returnsZero_whenNotLate() {
        Member m = new Member("A", "M1", "a@test.com", 555);

        assertEquals(0.0, m.calcLateFee(0));
        assertEquals(0.0, m.calcLateFee(-3));
    }

    @Test
    void calculateLateFee_chargesFivePerDay_whenLate() {
        Member m = new Member("A", "M1", "a@test.com", 555);

        assertEquals(15.0, m.calcLateFee(3));
    }

    @Test
    void constructor_setsDefaultMaxBooksAllowedToTwo() {
        Member m = new Member("A", "M1", "a@test.com", 555);

        assertEquals(2, m.getMaxBooksAllowed());
    }

    @Test
    void constructor_allowsCustomMaxBooksAllowed() {
        Member m = new Member("A", "M1", "a@test.com", 555, 6);

        assertEquals(6, m.getMaxBooksAllowed());
    }

    @Test
    void calculateLateFee_chargesLowerRate_forStudents() {
        StudentMember s = new StudentMember(
                "S", "M2", "s@test.com", 777,
                4, "S123", "CS"
        );

        assertEquals(7.5, s.calcLateFee(3));
        assertEquals(0.0, s.calcLateFee(0));
    }

    @Test
    void polymorphism_callsOverriddenMethod_throughMemberReference() {
        Member m = new StudentMember(
                "S", "M2", "s@test.com", 777,
                4, "S123", "CS"
        );

        assertEquals(7.5, m.calcLateFee(3));
    }

    @Test
    void student_maxBooksAllowed_isFour() {
        Member m = new StudentMember(
                "F", "N22", "a@test.com", 20000039,
                4, "S1322", "CS"
        );

        assertEquals(4, m.getMaxBooksAllowed());
    }
}
