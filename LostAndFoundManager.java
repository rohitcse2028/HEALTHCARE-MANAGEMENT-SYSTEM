import java.util.*;

public class LostAndFoundManager {

    private final List<Item> items = new ArrayList<>();
    private final Map<String, List<Item>> categorizedItems = new HashMap<>();

    // synchronized for thread safety
    public synchronized void addItem(Item item) {
        items.add(item);

        categorizedItems.computeIfAbsent(item.getCategory(), c -> new ArrayList<>()).add(item);
    }

    public synchronized List<Item> getAllItems() {
        return new ArrayList<>(items);
    }

    public synchronized List<Item> getItemsByCategory(String category) {
        return new ArrayList<>(categorizedItems.getOrDefault(category, Collections.emptyList()));
    }

    public synchronized Optional<Item> searchById(String itemId) {
        return items.stream()
            .filter(i -> i.getItemId().equals(itemId))
            .findFirst();
    }
}
