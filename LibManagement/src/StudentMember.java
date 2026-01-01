/*
* Represents a Student library member
* Students are allowed more borrowed books and their late fee are discounted
 */

public class StudentMember extends Member {

    private String stuID;
    private String department;

    //Constructor for Student members that forces the max borrow limit to 4
    public StudentMember(String name, String memberId, String email, int number,String stuID, String department) {
        super(name, memberId, email, number, 4);
        this.stuID = stuID;
        this.department = department;
    }

    //decreasing the fee for student members
    @Override
    public double calcLateFee(long daysLate){
        if(daysLate <= 0){
            return 0;
        }
        return 2.5 * daysLate;
    }

    //Getters
    public String getStuID(){
        return stuID;
    }
    public String getDepartment(){
        return department;
    }
}
