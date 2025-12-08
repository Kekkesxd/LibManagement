


public class StudentMember extends Member {

    private String stuID;
    private String department;



    //increasing how many books can be borrowed for students
    public StudentMember(){
        super.setBooksborrowed(3);
    }

    //decreasing the fee for student members
    @Override
    public double CalcFee(long daysLate){
        return 2.5 * daysLate;
    }


    //setters
    public void setStuID(String stuID){
        this.stuID = stuID;
    }
    public void setDepartment(String department){
        this.department= department;
    }

    //Getters
    public String getStuID(){
        return stuID;
    }
    public String getDepartment(){
        return department;
    }
}
