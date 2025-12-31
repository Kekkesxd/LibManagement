import java.util.Collections;
import java.util.List;

//<T> means it works with any type, not just books
public class Paginator<T> {

    //Full list of items ----Search results
    private final List<T> items;

    //Number of items shown per page
    private final int pageSize;

    //Stores the list and chosen page size
    public Paginator(List<T> items, int pageSize) {
        this.items = items;
        this.pageSize = pageSize;
    }

    //Calculates the total number of pages needed
    public int totalPages() {
        if (items.isEmpty()) return 0;
        return (items.size() + pageSize - 1) / pageSize;
    }

    //returns a sub-list for the requested page index
    public List<T> getPage(int pageIndex) {
        int start = pageIndex * pageSize;
        if (start >= items.size()) return Collections.emptyList();

        int end = Math.min(start + pageSize, items.size());
        return items.subList(start, end);
    }
}
