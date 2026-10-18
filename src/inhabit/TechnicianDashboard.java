package inhabit;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.*;

// Maintenance Technician & Facility Staff Redressal Dashboard
public class TechnicianDashboard extends JFrame {
    private Staff currentStaff;
    private ComplaintDAO complaintDAO;

    private JTable techTable;
    private DefaultTableModel techModel;
    private JTextField tfSearch;
    private JComboBox<String> cbCategory;
    private JComboBox<String> cbStatus;
    private JComboBox<String> cbWing;
    private JLabel lblCount;

    // Stat card labels
    private JLabel lblStatAssigned, lblStatDept, lblStatConcerning, lblStatResolved;

    public TechnicianDashboard(Staff staff) {
        this.currentStaff = staff;
        this.complaintDAO = new ComplaintDAO();

        setTitle("InHabit - Maintenance & Technician Redressal Portal (" + staff.getDepartment() + ")");
        setSize(1080, 720);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Header Panel
        JPanel pTop = new JPanel(new BorderLayout(15, 10));
        pTop.setBackground(new Color(15, 23, 42)); // Slate Navy
        pTop.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JPanel pBrand = new JPanel(new GridLayout(2, 1, 0, 2));
        pBrand.setOpaque(false);
        JLabel lblBrand = new JLabel("InHabit | Facility Redressal Desk");
        lblBrand.setFont(new Font("SansSerif", Font.BOLD, 19));
        lblBrand.setForeground(Color.WHITE);

        JLabel lblSub = new JLabel("Department: " + staff.getDepartment() + " | Trade: " + staff.getRole());
        lblSub.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblSub.setForeground(new Color(148, 163, 184));
        pBrand.add(lblBrand);
        pBrand.add(lblSub);

        JPanel pUserMeta = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        pUserMeta.setOpaque(false);

        JLabel lblStaffInfo = new JLabel(staff.getFullName() + " (Staff ID: " + staff.getStaffId() + ")");
        lblStaffInfo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblStaffInfo.setForeground(new Color(226, 232, 240));

        JButton btnLogout = UIUtils.createButton("Logout", new Color(220, 38, 38));
        btnLogout.setPreferredSize(new Dimension(85, 30));
        btnLogout.addActionListener(e -> {
            dispose();
            new LoginFrame();
        });

        pUserMeta.add(lblStaffInfo);
        pUserMeta.add(btnLogout);

        pTop.add(pBrand, BorderLayout.WEST);
        pTop.add(pUserMeta, BorderLayout.EAST);
        add(pTop, BorderLayout.NORTH);

        // Center Content: Stats + Search/Filter Separator + Table
        JPanel pCenter = new JPanel(new BorderLayout(15, 15));
        pCenter.setBackground(new Color(248, 250, 252));
        pCenter.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        // Top of Center: Workload Cards + Toolbar
        JPanel pControls = new JPanel();
        pControls.setLayout(new BoxLayout(pControls, BoxLayout.Y_AXIS));
        pControls.setOpaque(false);

        // Workload Cards
        pControls.add(createStatsPanel());
        pControls.add(Box.createVerticalStrut(15));

        // Action Toolbar
        JPanel tbActions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        tbActions.setOpaque(false);

        JButton btnClaim = UIUtils.createButton("Claim Ticket", new Color(37, 99, 235));
        JButton btnUpdateStatus = UIUtils.createButton("Update Redressal", new Color(16, 185, 129));
        JButton btnDetails = UIUtils.createSecondaryButton("View Full Ticket");
        JButton btnRefresh = UIUtils.createSecondaryButton("Refresh Workload");

        tbActions.add(btnClaim);
        tbActions.add(btnUpdateStatus);
        tbActions.add(btnDetails);
        tbActions.add(btnRefresh);

        JLabel lblHint = new JLabel(" (Select ticket to assign to yourself or update maintenance status)");
        lblHint.setFont(new Font("SansSerif", Font.ITALIC, 12));
        lblHint.setForeground(new Color(100, 116, 139));
        tbActions.add(lblHint);

        pControls.add(tbActions);
        pControls.add(Box.createVerticalStrut(10));

        JSeparator sep = new JSeparator(SwingConstants.HORIZONTAL);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
        pControls.add(sep);
        pControls.add(Box.createVerticalStrut(8));

        // Search & Separator Toolbar
        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        searchBar.setOpaque(false);

        JLabel lblSearch = new JLabel("🔍 Search Tickets:");
        lblSearch.setFont(new Font("SansSerif", Font.BOLD, 12));
        searchBar.add(lblSearch);

        tfSearch = new JTextField(15);
        tfSearch.setFont(new Font("SansSerif", Font.PLAIN, 12));
        tfSearch.setToolTipText("Search title, room, description, or ticket ID...");
        searchBar.add(tfSearch);

        searchBar.add(createVerticalSeparator());

        JLabel lblCat = new JLabel("Category:");
        lblCat.setFont(new Font("SansSerif", Font.PLAIN, 12));
        searchBar.add(lblCat);

        String[] categories = {"All Categories", "Internet", "Plumbing", "Mess", "Cleanliness", "Electrical", "Security"};
        cbCategory = new JComboBox<>(categories);
        cbCategory.setFont(new Font("SansSerif", Font.PLAIN, 12));
        // Auto-select department matching category if applicable
        selectMatchingCategory(staff.getDepartment());
        searchBar.add(cbCategory);

        searchBar.add(createVerticalSeparator());

        JLabel lblStat = new JLabel("Status:");
        lblStat.setFont(new Font("SansSerif", Font.PLAIN, 12));
        searchBar.add(lblStat);

        String[] statuses = {"All Statuses", "Open", "Under Review", "In Progress", "Resolved", "Concerning Only"};
        cbStatus = new JComboBox<>(statuses);
        cbStatus.setFont(new Font("SansSerif", Font.PLAIN, 12));
        searchBar.add(cbStatus);

        searchBar.add(createVerticalSeparator());

        JLabel lblWing = new JLabel("Wing:");
        lblWing.setFont(new Font("SansSerif", Font.PLAIN, 12));
        searchBar.add(lblWing);

        String[] wings = {"All Wings", "Wing Alpha", "Wing Beta", "Wing Charlie", "Wing Delta"};
        cbWing = new JComboBox<>(wings);
        cbWing.setFont(new Font("SansSerif", Font.PLAIN, 12));
        searchBar.add(cbWing);

        JButton btnClear = UIUtils.createSecondaryButton("✕ Clear");
        btnClear.setPreferredSize(new Dimension(75, 26));
        btnClear.setFont(new Font("SansSerif", Font.PLAIN, 11));
        searchBar.add(btnClear);

        lblCount = new JLabel("...");
        lblCount.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblCount.setForeground(new Color(71, 85, 105));
        searchBar.add(lblCount);

        pControls.add(searchBar);
        pCenter.add(pControls, BorderLayout.NORTH);

        // Table
        String[] cols = {"Ticket ID", "Title", "Category", "Location", "Status", "Votes", "Priority", "Assigned To"};
        techModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        techTable = new JTable(techModel);
        techTable.setRowHeight(28);
        techTable.setFont(new Font("SansSerif", Font.PLAIN, 13));
        techTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        techTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Priority badge renderer
        techTable.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, v, s, f, r, c);
                if ("CONCERNING".equals(v)) {
                    comp.setForeground(new Color(220, 38, 38));
                    setFont(getFont().deriveFont(Font.BOLD));
                } else {
                    comp.setForeground(new Color(100, 116, 139));
                }
                return comp;
            }
        });

        loadTableData();
        pCenter.add(new JScrollPane(techTable), BorderLayout.CENTER);
        add(pCenter, BorderLayout.CENTER);

        // Listeners for live search & separator filters
        tfSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { loadTableData(); }
            public void removeUpdate(DocumentEvent e) { loadTableData(); }
            public void changedUpdate(DocumentEvent e) { loadTableData(); }
        });

        cbCategory.addActionListener(e -> loadTableData());
        cbStatus.addActionListener(e -> loadTableData());
        cbWing.addActionListener(e -> loadTableData());

        btnClear.addActionListener(e -> {
            tfSearch.setText("");
            cbCategory.setSelectedIndex(0);
            cbStatus.setSelectedIndex(0);
            cbWing.setSelectedIndex(0);
            loadTableData();
        });

        // Claim Ticket action
        btnClaim.addActionListener(e -> {
            int row = techTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Please select a ticket to claim.", "Select Ticket", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int ticketId = (int) techModel.getValueAt(row, 0);
            boolean ok = complaintDAO.assignStaff(ticketId, currentStaff.getStaffId());
            if (ok) {
                complaintDAO.updateStatus(ticketId, "In Progress");
                JOptionPane.showMessageDialog(this, "Ticket #" + ticketId + " has been assigned to you and set to 'In Progress'!", "Ticket Claimed", JOptionPane.INFORMATION_MESSAGE);
                loadTableData();
            }
        });

        // Update Redressal Status action
        btnUpdateStatus.addActionListener(e -> {
            int row = techTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Please select a ticket to update.", "Select Ticket", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int ticketId = (int) techModel.getValueAt(row, 0);
            String currentSt = (String) techModel.getValueAt(row, 4);

            String[] options = {"Under Review", "In Progress", "Resolved", "Closed"};
            String newSt = (String) JOptionPane.showInputDialog(this,
                    "Select maintenance redressal status for Ticket #" + ticketId + ":",
                    "Update Redressal", JOptionPane.QUESTION_MESSAGE, null, options, currentSt);

            if (newSt != null && !newSt.equals(currentSt)) {
                complaintDAO.updateStatus(ticketId, newSt);
                // If technician updates status, also ensure staff assignment is logged
                complaintDAO.assignStaff(ticketId, currentStaff.getStaffId());
                loadTableData();
                JOptionPane.showMessageDialog(this, "Ticket #" + ticketId + " status updated to " + newSt + "!", "Status Updated", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // View Details action
        btnDetails.addActionListener(e -> openSelectedDetails());
        techTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    openSelectedDetails();
                }
            }
        });

        btnRefresh.addActionListener(e -> loadTableData());

        setVisible(true);
    }

    private void selectMatchingCategory(String dept) {
        if (dept == null) return;
        String d = dept.toLowerCase();
        if (d.contains("electric")) cbCategory.setSelectedItem("Electrical");
        else if (d.contains("plumb")) cbCategory.setSelectedItem("Plumbing");
        else if (d.contains("internet") || d.contains("it")) cbCategory.setSelectedItem("Internet");
        else if (d.contains("mess") || d.contains("food")) cbCategory.setSelectedItem("Mess");
        else if (d.contains("security")) cbCategory.setSelectedItem("Security");
    }

    private JComponent createVerticalSeparator() {
        JSeparator s = new JSeparator(SwingConstants.VERTICAL);
        s.setPreferredSize(new Dimension(2, 18));
        s.setForeground(new Color(203, 213, 225));
        return s;
    }

    private JPanel createStatsPanel() {
        JPanel p = new JPanel(new GridLayout(1, 4, 15, 0));
        p.setOpaque(false);

        lblStatDept = new JLabel("0");
        lblStatAssigned = new JLabel("0");
        lblStatConcerning = new JLabel("0");
        lblStatResolved = new JLabel("0");

        p.add(createMiniCard("Trade Workload", lblStatDept, new Color(37, 99, 235)));
        p.add(createMiniCard("Assigned to Me", lblStatAssigned, new Color(13, 148, 136)));
        p.add(createMiniCard("Critical Issues", lblStatConcerning, new Color(225, 29, 72)));
        p.add(createMiniCard("Resolved Tickets", lblStatResolved, new Color(16, 185, 129)));

        return p;
    }

    private JPanel createMiniCard(String title, JLabel valLabel, Color accent) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, accent),
                BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));

        JLabel lTitle = new JLabel(title);
        lTitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lTitle.setForeground(new Color(100, 116, 139));

        valLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        valLabel.setForeground(new Color(15, 23, 42));

        card.add(lTitle, BorderLayout.NORTH);
        card.add(valLabel, BorderLayout.CENTER);
        return card;
    }

    private void openSelectedDetails() {
        int row = techTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a ticket row to view details.", "Select Ticket", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int ticketId = (int) techModel.getValueAt(row, 0);
        Complaint c = complaintDAO.getComplaintById(ticketId);
        UIUtils.showComplaintDetailsDialog(this, c, currentStaff, complaintDAO, this::loadTableData);
    }

    private void loadTableData() {
        techModel.setRowCount(0);
        String kw = (tfSearch != null) ? tfSearch.getText() : "";
        String cat = (cbCategory != null) ? (String) cbCategory.getSelectedItem() : "All";
        String stat = (cbStatus != null) ? (String) cbStatus.getSelectedItem() : "All";
        String wing = (cbWing != null) ? (String) cbWing.getSelectedItem() : "All";

        List<Complaint> list = complaintDAO.searchComplaints(kw, cat, stat, wing);
        for (Complaint c : list) {
            String assigned = (c.getAssignedStaffId() != null && !c.getAssignedStaffId().isEmpty())
                    ? c.getAssignedStaffId() : "Unassigned";
            if (currentStaff.getStaffId().equals(c.getAssignedStaffId())) {
                assigned = "Me (" + currentStaff.getStaffId() + ")";
            }
            techModel.addRow(new Object[]{
                    c.getTicketId(),
                    c.getTitle(),
                    c.getCategory(),
                    c.getLocation(),
                    c.getStatus(),
                    c.getVoteCount(),
                    c.isConcerning() ? "CONCERNING" : "Normal",
                    assigned
            });
        }
        if (lblCount != null) {
            lblCount.setText("(" + list.size() + " tickets)");
        }

        // Update summary metrics
        List<Complaint> all = complaintDAO.getAllComplaints();
        int deptCnt = 0, myCnt = 0, concernCnt = 0, resolvedCnt = 0;
        String myCategory = (cbCategory != null) ? (String) cbCategory.getSelectedItem() : "All Categories";

        for (Complaint c : all) {
            if (currentStaff.getStaffId().equals(c.getAssignedStaffId())) myCnt++;
            if (!myCategory.equals("All Categories") && myCategory.equalsIgnoreCase(c.getCategory())) deptCnt++;
            if (c.isConcerning()) concernCnt++;
            if ("Resolved".equalsIgnoreCase(c.getStatus()) || "Closed".equalsIgnoreCase(c.getStatus())) resolvedCnt++;
        }

        if (lblStatDept != null) lblStatDept.setText(String.valueOf(deptCnt > 0 ? deptCnt : all.size()));
        if (lblStatAssigned != null) lblStatAssigned.setText(String.valueOf(myCnt));
        if (lblStatConcerning != null) lblStatConcerning.setText(String.valueOf(concernCnt));
        if (lblStatResolved != null) lblStatResolved.setText(String.valueOf(resolvedCnt));
    }
}
