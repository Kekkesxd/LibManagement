import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MemberTest {

    //Testing late fee when it's returned on time
    @Test
    void calculateLateFee_returnsZero_whenNotLate() {
        Member m = new Member("A", "M1", "a@test.com", 555);

        assertEquals(0.0, m.calcLateFee(0));
        assertEquals(0.0, m.calcLateFee(-3));
    }

    //Testing late fee for normal member
    @Test
    void calculateLateFee_chargesFivePerDay_whenLate() {
        Member m = new Member("A", "M1", "a@test.com", 555);

        double fee = m.calcLateFee(3);
        System.out.println("Member late fee for 3 days: " + fee);
        assertEquals(15.0, fee);
    }
    //Testing the default construct
    @Test
    void constructor_setsDefaultMaxBooksAllowedToTwo() {
        Member m = new Member("A", "M1", "a@test.com", 555);

        assertEquals(2, m.getMaxBooksAllowed());
    }
    //Testing the construct with flexible max books allowed
    @Test
    void constructor_allowsCustomMaxBooksAllowed() {
        Member m = new Member("A", "M1", "a@test.com", 555, 6);

        assertEquals(6, m.getMaxBooksAllowed());
    }
    //Testing late fee for Students
    @Test
    void calculateLateFee_chargesLowerRate_forStudents() {
        StudentMember s = new StudentMember(
                "S", "M2", "s@test.com", 777, "S123", "CS"
        );
        double fee = s.calcLateFee(3);
        System.out.println("Student late fee for 3 days: " + fee);

        assertEquals(7.5, fee, 0.0001);
    }
    //Testing Method Overriding
    @Test
    void polymorphism_callsOverriddenMethod_throughMemberReference() {
        Member m = new StudentMember(
                "S", "M2", "s@test.com", 777, "S123", "CS"
        );

        double fee = m.calcLateFee(3);
        System.out.println("Late fee from Member ref (StudentMember object): " + fee);

        assertEquals(7.5, fee, 0.0001);
    }
    //Testing the max books allowed for students
    @Test
    void student_maxBooksAllowed_isFour() {
        Member m = new StudentMember(
                "F", "N22", "a@test.com", 20000039, "S1322", "CS"
        );

        assertEquals(4, m.getMaxBooksAllowed());
    }
}
