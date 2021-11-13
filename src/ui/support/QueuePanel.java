package ui.support;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class QueuePanel extends JPanel {

    private final String[] columnTitles = new String[]{"Title", "Album", "Artist", "Year", "Length", "LengthInS", "ID"};
    private final JTable queueList;

    public final JButton playNowButton = new JButton("Play Now");
    private final JButton removeSongButton = new JButton("Remove");

    private final WindowListener windowListener;

    // TODO Sincronizar
    public QueuePanel(
            ActionListener buttonListenerPlayNow,
            ActionListener buttonListenerRemove,
            ActionListener buttonListenerAddSong,
            String[][] queueArray) {
        JPanel queuePanelButtons = new JPanel();
        JScrollPane queueListPane = new JScrollPane();

        JButton addSongButton = new JButton("Add song...");

        queueList = new JTable();

        this.setLayout(new BorderLayout());
        queueListPane.setViewportView(queueList);
        updateQueueList(queueArray);
        queuePanelButtons.setLayout(new BoxLayout(queuePanelButtons, BoxLayout.X_AXIS));
        queuePanelButtons.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        queuePanelButtons.add(playNowButton);
        queuePanelButtons.add(Box.createRigidArea(new Dimension(5, 0)));
        queuePanelButtons.add(removeSongButton);
        queuePanelButtons.add(Box.createHorizontalGlue());
        queuePanelButtons.add(addSongButton);
        playNowButton.setEnabled(false);
        removeSongButton.setEnabled(false);
        this.add(queueListPane, BorderLayout.CENTER);
        this.add(queuePanelButtons, BorderLayout.PAGE_END);

        playNowButton.addActionListener(buttonListenerPlayNow);
        removeSongButton.addActionListener(buttonListenerRemove);
        addSongButton.addActionListener(buttonListenerAddSong);
        addSongButton.addActionListener(e -> addSongButton.setEnabled(false));
        windowListener = new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {
            }

            @Override
            public void windowClosing(WindowEvent e) {
            }

            @Override
            public void windowClosed(WindowEvent e) {
                addSongButton.setEnabled(true);
            }

            @Override
            public void windowIconified(WindowEvent e) {
            }

            @Override
            public void windowDeiconified(WindowEvent e) {
            }

            @Override
            public void windowActivated(WindowEvent e) {
            }

            @Override
            public void windowDeactivated(WindowEvent e) {
            }
        };
    }

    // TODO Sincronizar
    public void updateQueueList(String[][] queueArray) {
        queueList.setShowHorizontalLines(true);
        queueList.setDragEnabled(false);
        queueList.setColumnSelectionAllowed(false);
        queueList.getTableHeader().setReorderingAllowed(false);
        queueList.getTableHeader().setResizingAllowed(false);
        queueList.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        queueList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        queueList.setModel(new DefaultTableModel(queueArray, columnTitles) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
        queueList.getSelectionModel().addListSelectionListener(e -> {
            if (queueList.getSelectionModel().isSelectionEmpty()) {
                playNowButton.setEnabled(false);
                removeSongButton.setEnabled(false);
            } else {
                playNowButton.setEnabled(true);
                removeSongButton.setEnabled(true);
            }
        });
        ((DefaultTableCellRenderer) queueList.getTableHeader().getDefaultRenderer())
                .setHorizontalAlignment(JLabel.LEFT);
        queueList.getColumnModel().getColumn(0).setMinWidth(180);
        queueList.getColumnModel().getColumn(1).setMinWidth(180);
        queueList.getColumnModel().getColumn(2).setMinWidth(180);
        queueList.getColumnModel().getColumn(3).setMinWidth(70);
        queueList.getColumnModel().getColumn(4).setMinWidth(60);
        queueList.getColumnModel().getColumn(5).setMinWidth(0);
        queueList.getColumnModel().getColumn(6).setMinWidth(0);
        queueList.getColumnModel().getColumn(0).setPreferredWidth(180);
        queueList.getColumnModel().getColumn(1).setPreferredWidth(180);
        queueList.getColumnModel().getColumn(2).setPreferredWidth(180);
        queueList.getColumnModel().getColumn(3).setPreferredWidth(70);
        queueList.getColumnModel().getColumn(4).setPreferredWidth(60);
        queueList.getColumnModel().getColumn(5).setPreferredWidth(0);
        queueList.getColumnModel().getColumn(6).setPreferredWidth(0);
    }

    public int getSelectedSongID() {
        return Integer.parseInt(String.valueOf((queueList.getValueAt(queueList.getSelectedRow(), 6))));
    }

    public WindowListener getActionListener() {
        return windowListener;
    }
}
