/*
* Represents a book in the library
* Stores book details and manages the available and total copies of the book
* implements Searchable to allow keyword-based searching
 */

public class Book implements Searchable {

    private int id;
    private String title;
    private String author;
    private int year;
    private String category;

    private int totalCopies;
    private int availableCopies;

    //Constructor for book data and copy counts
    public Book(int id, String title, String author, int year, String category, int totalCopies){
        if(totalCopies <= 0 ){
            throw new IllegalArgumentException("Total copies must be greater than 0");
        }

        this.id = id;
        this.title = title;
        this.author = author;
        this.year = year;
        this.category = category;
        this.totalCopies = totalCopies;
        this.availableCopies = totalCopies;
    }

    //methods

    //for borrowing books, works if there are available copies
    public boolean borrowCopy(){
        if(availableCopies > 0 ){
            --availableCopies;
            return true;
        }
        return false;
    }

    //returns the borrowed copies and adds them to available copies while making sure only the owned/total copies are returned
    public void returnCopy(){
        if(totalCopies > availableCopies){
            availableCopies++;
        }
    }

    //Checks if the book matches a search keyword
    @Override
    public boolean matches(String keyword) {
        if (keyword == null) {
            return false;
        }

        String search = keyword.toLowerCase();

        return (title != null && title.toLowerCase().contains(search))
                || (author != null && author.toLowerCase().contains(search))
                || (category != null && category.toLowerCase().contains(search));
    }

    //getters
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public int getYear() {
        return year;
    }

    public String getCategory() {
        return category;
    }

    public int getAvailableCopies() {
        return availableCopies;
    }

    public int getTotalCopies() {
        return totalCopies;
    }
}

