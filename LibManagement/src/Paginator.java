import java.util.Collections;
import java.util.List;

public class Paginator<T> {
    private final List<T> items;
    private final int pageSize;

    public Paginator(List<T> items, int pageSize) {
        this.items = items;
        this.pageSize = pageSize;
    }

    public int totalPages() {
        if (items.isEmpty()) return 0;
        return (items.size() + pageSize - 1) / pageSize;
    }

    public List<T> getPage(int pageIndex) {
        int start = pageIndex * pageSize;
        if (start >= items.size()) return Collections.emptyList();

        int end = Math.min(start + pageSize, items.size());
        return items.subList(start, end);
    }
}
