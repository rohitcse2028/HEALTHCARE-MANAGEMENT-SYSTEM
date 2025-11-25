import javax.swing.*;
import java.awt.*;
import java.util.List;

public class LostAndFoundFrameThread extends JFrame {

    private LostAndFoundManager manager = new LostAndFoundManager();
    private DefaultListModel<String> listModel = new DefaultListModel<>();
    private JList<String> list = new JList<>(listModel);

    private volatile boolean running = true;

    public LostAndFoundFrameThread() {
        setTitle("Lost & Found - Multithreaded Version");
        setSize(500, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        add(new JScrollPane(list), BorderLayout.CENTER);

        startBackgroundUpdater();
    }

    private void startBackgroundUpdater() {
        Thread updater = new Thread(() -> {
            while (running) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ignored) {}

                refreshList();
            }
        });

        updater.setDaemon(true);
        updater.start();
    }

    private void refreshList() {
        SwingUtilities.invokeLater(() -> {
            listModel.clear();
            List<Item> all = manager.getAllItems();
            for (Item item : all) {
                listModel.addElement(item.getItemId() + " - " + item.getName());
            }
        });
    }

    public void stopThread() {
        running = false;
    }
}
