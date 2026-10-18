package inhabit;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.*;

// Administrator Dashboard for Hostel Authorities & Wardens with Full Search, Separator Controls & Analytics/Insights
public class AdminDashboard extends JFrame {
    private Staff currentAdmin;
    private ComplaintDAO complaintDAO;
    private UserDAO userDAO;

    private JTabbedPane tabPane;
    private JTable complaintsTable, studentsTable, staffTable;
    private DefaultTableModel complaintsModel, studentsModel, staffModel;

    // Search and Separator Controls for Complaints Tab
    private JTextField tfComplaintSearch;
    private JComboBox<String> cbComplaintCategory;
    private JComboBox<String> cbComplaintStatus;
    private JComboBox<String> cbComplaintWing;
    private JLabel lblComplaintCount;

    // Live Details Inspector for Complaints
    private JLabel lblAdminInspTitle;
    private JLabel lblAdminInspMeta;
    private JTextArea taAdminInspDesc;
    private JButton btnAdminInspModal;
    private JButton btnAdminInspStatus;

    // Search Controls for Students and Staff
    private JTextField tfStudentSearch;
    private JComboBox<String> cbStudentBlock;
    private JLabel lblStudentCount;
    private JLabel lblStudentDetail;

    private JTextField tfStaffSearch;
    private JComboBox<String> cbStaffDept;
    private JLabel lblStaffCount;

    public AdminDashboard(Staff admin) {
        this.currentAdmin = admin;
        this.complaintDAO = new ComplaintDAO();
        this.userDAO = new UserDAO();

        setTitle("InHabit - Administration Portal");
        setSize(1100, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Header Panel
        JPanel pTop = new JPanel(new BorderLayout(15, 10));
        pTop.setBackground(new Color(15, 23, 42)); // Deep slate navy
        pTop.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JPanel pBrand = new JPanel(new GridLayout(2, 1, 0, 2));
        pBrand.setOpaque(false);
        JLabel lblBrand = new JLabel("InHabit | Administration Portal");
        lblBrand.setFont(new Font("SansSerif", Font.BOLD, 19));
        lblBrand.setForeground(Color.WHITE);

        JLabel lblSub = new JLabel("Hostel Facility Management, Student Directory & Operational Analytics");
        lblSub.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblSub.setForeground(new Color(148, 163, 184));
        pBrand.add(lblBrand);
        pBrand.add(lblSub);

        JPanel pUserMeta = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        pUserMeta.setOpaque(false);

        JLabel lblAdminInfo = new JLabel("Admin: " + admin.getFullName() + " (" + admin.getRole() + ")");
        lblAdminInfo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblAdminInfo.setForeground(new Color(226, 232, 240));

        JButton btnLogout = UIUtils.createButton("Logout", new Color(220, 38, 38));
        btnLogout.setPreferredSize(new Dimension(85, 30));
        btnLogout.addActionListener(e -> {
            dispose();
            new LoginFrame();
        });

        pUserMeta.add(lblAdminInfo);
        pUserMeta.add(btnLogout);

        pTop.add(pBrand, BorderLayout.WEST);
        pTop.add(pUserMeta, BorderLayout.EAST);
        add(pTop, BorderLayout.NORTH);

        // Tabbed Pane (4 Core Tabs with Analytics & Insights replacing Overview & Stats)
        tabPane = new JTabbedPane();
        tabPane.setFont(new Font("SansSerif", Font.BOLD, 13));

        tabPane.addTab("All Grievances", createComplaintsTab());
        tabPane.addTab("All Students", createStudentsTab());
        tabPane.addTab("Staff & Technicians", createStaffTab());
        tabPane.addTab("Analytics & Insights", createAnalyticsTab());

        add(tabPane, BorderLayout.CENTER);
        setVisible(true);
    }

    private JComponent createVerticalSeparator() {
        JSeparator s = new JSeparator(SwingConstants.VERTICAL);
        s.setPreferredSize(new Dimension(2, 18));
        s.setForeground(new Color(203, 213, 225));
        return s;
    }

    // Tab 1: All Grievances with Search, Separator Toolbar & Live Inspector
    private JPanel createComplaintsTab() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBackground(new Color(248, 250, 252));
        p.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JPanel topContainer = new JPanel();
        topContainer.setLayout(new BoxLayout(topContainer, BoxLayout.Y_AXIS));
        topContainer.setOpaque(false);

        // Row 1: Action Toolbar
        JPanel tb = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        tb.setOpaque(false);

        JButton btnDetails = UIUtils.createButton("View Details", new Color(37, 99, 235));
        JButton btnStatus = UIUtils.createButton("Update Status", new Color(16, 185, 129));
        JButton btnRefresh = UIUtils.createSecondaryButton("Refresh");

        tb.add(btnDetails);
        tb.add(btnStatus);
        tb.add(btnRefresh);

        JLabel lblHint = new JLabel(" (Click any grievance row to preview details inline, or double-click for modal dialog)");
        lblHint.setFont(new Font("SansSerif", Font.ITALIC, 12));
        lblHint.setForeground(new Color(100, 116, 139));
        tb.add(lblHint);

        topContainer.add(tb);
        topContainer.add(Box.createVerticalStrut(10));

        JSeparator sep = new JSeparator(SwingConstants.HORIZONTAL);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
        topContainer.add(sep);
        topContainer.add(Box.createVerticalStrut(8));

        // Row 2: Search & Separator Filter Controls
        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        searchBar.setOpaque(false);

        JLabel lblSearch = new JLabel("🔍 Search:");
        lblSearch.setFont(new Font("SansSerif", Font.BOLD, 12));
        searchBar.add(lblSearch);

        tfComplaintSearch = new JTextField(15);
        tfComplaintSearch.setFont(new Font("SansSerif", Font.PLAIN, 12));
        tfComplaintSearch.setToolTipText("Search keyword, ticket ID, title, description, or student PRN...");
        searchBar.add(tfComplaintSearch);

        searchBar.add(createVerticalSeparator());

        JLabel lblCat = new JLabel("Category:");
        lblCat.setFont(new Font("SansSerif", Font.PLAIN, 12));
        searchBar.add(lblCat);

        String[] categories = {"All Categories", "Internet", "Plumbing", "Mess", "Cleanliness", "Electrical", "Security"};
        cbComplaintCategory = new JComboBox<>(categories);
        cbComplaintCategory.setFont(new Font("SansSerif", Font.PLAIN, 12));
        searchBar.add(cbComplaintCategory);

        searchBar.add(createVerticalSeparator());

        JLabel lblStat = new JLabel("Status:");
        lblStat.setFont(new Font("SansSerif", Font.PLAIN, 12));
        searchBar.add(lblStat);

        String[] statuses = {"All Statuses", "Open", "Under Review", "In Progress", "Resolved", "Concerning Only"};
        cbComplaintStatus = new JComboBox<>(statuses);
        cbComplaintStatus.setFont(new Font("SansSerif", Font.PLAIN, 12));
        searchBar.add(cbComplaintStatus);

        searchBar.add(createVerticalSeparator());

        JLabel lblWing = new JLabel("Wing:");
        lblWing.setFont(new Font("SansSerif", Font.PLAIN, 12));
        searchBar.add(lblWing);

        String[] wings = {"All Wings", "Wing Alpha", "Wing Beta", "Wing Charlie", "Wing Delta"};
        cbComplaintWing = new JComboBox<>(wings);
        cbComplaintWing.setFont(new Font("SansSerif", Font.PLAIN, 12));
        searchBar.add(cbComplaintWing);

        JButton btnClear = UIUtils.createSecondaryButton("✕ Clear");
        btnClear.setPreferredSize(new Dimension(75, 26));
        btnClear.setFont(new Font("SansSerif", Font.PLAIN, 11));
        searchBar.add(btnClear);

        lblComplaintCount = new JLabel("...");
        lblComplaintCount.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblComplaintCount.setForeground(new Color(71, 85, 105));
        searchBar.add(lblComplaintCount);

        topContainer.add(searchBar);
        p.add(topContainer, BorderLayout.NORTH);

        String[] cols = {"Ticket ID", "Title", "Category", "Location", "Filed By PRN", "Status", "Votes", "Priority"};
        complaintsModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        complaintsTable = new JTable(complaintsModel);
        complaintsTable.setRowHeight(28);
        complaintsTable.setFont(new Font("SansSerif", Font.PLAIN, 13));
        complaintsTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        complaintsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        complaintsTable.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
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

        loadComplaints();
        p.add(new JScrollPane(complaintsTable), BorderLayout.CENTER);

        // Live Details Inspector Panel for Admin Grievances
        lblAdminInspTitle = new JLabel("💡 Select any grievance above to preview full details.");
        lblAdminInspMeta = new JLabel("Live inspector displays full description, votes, location, and assigned staff.");
        taAdminInspDesc = new JTextArea();
        btnAdminInspModal = UIUtils.createButton("View Full Modal", new Color(37, 99, 235));
        btnAdminInspModal.setPreferredSize(new Dimension(135, 28));
        btnAdminInspModal.setEnabled(false);
        btnAdminInspModal.addActionListener(e -> openSelectedComplaintDetails());

        btnAdminInspStatus = UIUtils.createButton("Update Status", new Color(16, 185, 129));
        btnAdminInspStatus.setPreferredSize(new Dimension(120, 28));
        btnAdminInspStatus.setEnabled(false);
        btnAdminInspStatus.addActionListener(e -> handleUpdateStatusAction());

        JPanel pInsp = createAdminPreviewCard(lblAdminInspTitle, lblAdminInspMeta, taAdminInspDesc, btnAdminInspModal, btnAdminInspStatus);
        p.add(pInsp, BorderLayout.SOUTH);

        // Selection listeners: single click, keyboard navigation, and list selection
        complaintsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateAdminInspector();
            }
        });

        complaintsTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                updateAdminInspector();
                if (e.getClickCount() == 2) {
                    openSelectedComplaintDetails();
                }
            }
        });

        // Listeners for live search & separator filters
        tfComplaintSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { loadComplaints(); }
            public void removeUpdate(DocumentEvent e) { loadComplaints(); }
            public void changedUpdate(DocumentEvent e) { loadComplaints(); }
        });

        cbComplaintCategory.addActionListener(e -> loadComplaints());
        cbComplaintStatus.addActionListener(e -> loadComplaints());
        cbComplaintWing.addActionListener(e -> loadComplaints());

        btnClear.addActionListener(e -> {
            tfComplaintSearch.setText("");
            cbComplaintCategory.setSelectedIndex(0);
            cbComplaintStatus.setSelectedIndex(0);
            cbComplaintWing.setSelectedIndex(0);
            loadComplaints();
        });

        // Action: View Details
        btnDetails.addActionListener(e -> openSelectedComplaintDetails());

        // Action: Quick Status Update
        btnStatus.addActionListener(e -> handleUpdateStatusAction());

        btnRefresh.addActionListener(e -> loadComplaints());

        return p;
    }

    private void handleUpdateStatusAction() {
        int selectedRow = complaintsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a ticket to update status.", "Notice", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelRow = complaintsTable.convertRowIndexToModel(selectedRow);
        int ticketId = Integer.parseInt(complaintsModel.getValueAt(modelRow, 0).toString());
        String currentStatus = complaintsModel.getValueAt(modelRow, 5).toString();

        String[] options = {"Open", "Under Review", "In Progress", "Resolved", "Closed"};
        String newStatus = (String) JOptionPane.showInputDialog(this,
                "Select new status for Ticket #" + ticketId + ":",
                "Update Status", JOptionPane.QUESTION_MESSAGE, null, options, currentStatus);

        if (newStatus != null && !newStatus.equals(currentStatus)) {
            complaintDAO.updateStatus(ticketId, newStatus);
            loadComplaints();
            updateAdminInspector();
            JOptionPane.showMessageDialog(this, "Ticket #" + ticketId + " updated to " + newStatus + "!", "Updated", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void updateAdminInspector() {
        int row = complaintsTable.getSelectedRow();
        if (row == -1) {
            lblAdminInspTitle.setText("💡 Select any grievance above to preview full details.");
            lblAdminInspMeta.setText("Live inspector displays full description, votes, location, and assigned staff.");
            taAdminInspDesc.setText("");
            btnAdminInspModal.setEnabled(false);
            btnAdminInspStatus.setEnabled(false);
            return;
        }
        int modelRow = complaintsTable.convertRowIndexToModel(row);
        int ticketId = Integer.parseInt(complaintsModel.getValueAt(modelRow, 0).toString());
        Complaint c = complaintDAO.getComplaintById(ticketId);
        if (c != null) {
            lblAdminInspTitle.setText("Ticket #" + c.getTicketId() + ": " + c.getTitle());
            lblAdminInspMeta.setText("Category: " + c.getCategory() + "  |  Location: " + c.getLocation() + "  |  Status: " + c.getStatus() + (c.isConcerning() ? " [CONCERNING]" : "") + "  |  Votes: " + c.getVoteCount() + "  |  Filed By: " + c.getCreatedByPrn() + "  |  Assigned: " + (c.getAssignedStaffId() != null ? c.getAssignedStaffId() : "Unassigned"));
            taAdminInspDesc.setText(c.getDescription());
            btnAdminInspModal.setEnabled(true);
            btnAdminInspStatus.setEnabled(true);
        }
    }

    private void openSelectedComplaintDetails() {
        int row = complaintsTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a complaint row.", "Select Row", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelRow = complaintsTable.convertRowIndexToModel(row);
        int ticketId = Integer.parseInt(complaintsModel.getValueAt(modelRow, 0).toString());
        Complaint c = complaintDAO.getComplaintById(ticketId);
        UIUtils.showComplaintDetailsDialog(this, c, currentAdmin, complaintDAO, this::loadComplaints);
    }

    private void loadComplaints() {
        complaintsModel.setRowCount(0);
        String kw = (tfComplaintSearch != null) ? tfComplaintSearch.getText() : "";
        String cat = (cbComplaintCategory != null) ? (String) cbComplaintCategory.getSelectedItem() : "All";
        String stat = (cbComplaintStatus != null) ? (String) cbComplaintStatus.getSelectedItem() : "All";
        String wing = (cbComplaintWing != null) ? (String) cbComplaintWing.getSelectedItem() : "All";

        List<Complaint> list = complaintDAO.searchComplaints(kw, cat, stat, wing);
        for (Complaint c : list) {
            complaintsModel.addRow(new Object[]{
                    c.getTicketId(),
                    c.getTitle(),
                    c.getCategory(),
                    c.getLocation(),
                    c.getCreatedByPrn(),
                    c.getStatus(),
                    c.getVoteCount(),
                    c.isConcerning() ? "CONCERNING" : "Normal"
            });
        }
        if (lblComplaintCount != null) {
            lblComplaintCount.setText("(" + list.size() + " matches)");
        }
    }

    // Tab 2: All Students with Live Search & Block Filter (Displays full 500-student directory)
    private JPanel createStudentsTab() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBackground(new Color(248, 250, 252));
        p.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JPanel tb = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        tb.setOpaque(false);
        JLabel lblSearch = new JLabel("🔍 Search Student:");
        lblSearch.setFont(new Font("SansSerif", Font.BOLD, 13));
        tfStudentSearch = new JTextField(16);
        tfStudentSearch.setFont(new Font("SansSerif", Font.PLAIN, 12));
        tfStudentSearch.setToolTipText("Filter by name, PRN, room, or branch...");

        tb.add(lblSearch);
        tb.add(tfStudentSearch);
        tb.add(createVerticalSeparator());

        JLabel lblBlock = new JLabel("Block / Wing:");
        lblBlock.setFont(new Font("SansSerif", Font.PLAIN, 12));
        tb.add(lblBlock);

        String[] blocks = {"All Blocks", "Alpha (Boys)", "Beta (Girls)", "Charlie", "Delta"};
        cbStudentBlock = new JComboBox<>(blocks);
        cbStudentBlock.setFont(new Font("SansSerif", Font.PLAIN, 12));
        tb.add(cbStudentBlock);

        JButton btnReset = UIUtils.createSecondaryButton("✕ Clear");
        btnReset.setPreferredSize(new Dimension(75, 28));
        tb.add(btnReset);

        lblStudentCount = new JLabel("...");
        lblStudentCount.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblStudentCount.setForeground(new Color(71, 85, 105));
        tb.add(lblStudentCount);

        p.add(tb, BorderLayout.NORTH);

        String[] cols = {"PRN", "Full Name", "Hostel Room", "Branch & Division", "Gender", "Email", "Phone"};
        studentsModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        studentsTable = new JTable(studentsModel);
        studentsTable.setRowHeight(26);
        studentsTable.setFont(new Font("SansSerif", Font.PLAIN, 13));
        studentsTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        studentsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        loadStudents();
        p.add(new JScrollPane(studentsTable), BorderLayout.CENTER);

        // Footer Card showing selected student details
        lblStudentDetail = new JLabel("💡 Select any student from the 500-student directory above to view full resident record.");
        lblStudentDetail.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblStudentDetail.setForeground(new Color(71, 85, 105));
        JPanel pFoot = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        pFoot.setBackground(Color.WHITE);
        pFoot.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(226, 232, 240)));
        pFoot.add(lblStudentDetail);
        p.add(pFoot, BorderLayout.SOUTH);

        studentsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = studentsTable.getSelectedRow();
                if (row != -1) {
                    int modelRow = studentsTable.convertRowIndexToModel(row);
                    String prn = studentsModel.getValueAt(modelRow, 0).toString();
                    String name = studentsModel.getValueAt(modelRow, 1).toString();
                    String room = studentsModel.getValueAt(modelRow, 2).toString();
                    String branch = studentsModel.getValueAt(modelRow, 3).toString();
                    String email = studentsModel.getValueAt(modelRow, 5).toString();
                    String phone = studentsModel.getValueAt(modelRow, 6).toString();
                    lblStudentDetail.setText("👤 Selected: " + name + " (PRN: " + prn + ")  |  Room: " + room + "  |  Branch: " + branch + "  |  Email: " + email + "  |  Phone: " + phone);
                }
            }
        });

        tfStudentSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { loadStudents(); }
            public void removeUpdate(DocumentEvent e) { loadStudents(); }
            public void changedUpdate(DocumentEvent e) { loadStudents(); }
        });

        cbStudentBlock.addActionListener(e -> loadStudents());

        btnReset.addActionListener(e -> {
            tfStudentSearch.setText("");
            cbStudentBlock.setSelectedIndex(0);
            loadStudents();
        });

        return p;
    }

    private void loadStudents() {
        studentsModel.setRowCount(0);
        String filter = (tfStudentSearch != null) ? tfStudentSearch.getText() : "";
        String blk = (cbStudentBlock != null) ? (String) cbStudentBlock.getSelectedItem() : "All Blocks";

        List<Student> list = userDAO.searchStudents(filter);
        int matched = 0;
        for (Student s : list) {
            if (!blk.equals("All Blocks")) {
                String cleanBlk = blk.replace(" (Boys)", "").replace(" (Girls)", "");
                if (s.getLocation() == null || !s.getLocation().startsWith(cleanBlk)) {
                    continue;
                }
            }
            matched++;
            studentsModel.addRow(new Object[]{
                    s.getPrn(),
                    s.getFullName(),
                    s.getLocation(),
                    s.getBranchDivision(),
                    s.getGender(),
                    s.getEmail(),
                    s.getPhone()
            });
        }
        if (lblStudentCount != null) {
            lblStudentCount.setText("(" + matched + " students)");
        }
    }

    // Tab 3: Staff & Technicians with Search & Department Separator
    private JPanel createStaffTab() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBackground(new Color(248, 250, 252));
        p.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JPanel tb = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        tb.setOpaque(false);

        JLabel lblSearch = new JLabel("🔍 Search Staff:");
        lblSearch.setFont(new Font("SansSerif", Font.BOLD, 13));
        tfStaffSearch = new JTextField(16);
        tfStaffSearch.setFont(new Font("SansSerif", Font.PLAIN, 12));

        tb.add(lblSearch);
        tb.add(tfStaffSearch);
        tb.add(createVerticalSeparator());

        JLabel lblDept = new JLabel("Department:");
        lblDept.setFont(new Font("SansSerif", Font.PLAIN, 12));
        tb.add(lblDept);

        String[] depts = {"All Departments", "Hostel Administration", "Electrical", "Plumbing", "Internet & IT", "Mess & Food", "Security"};
        cbStaffDept = new JComboBox<>(depts);
        cbStaffDept.setFont(new Font("SansSerif", Font.PLAIN, 12));
        tb.add(cbStaffDept);

        JButton btnReset = UIUtils.createSecondaryButton("✕ Clear");
        btnReset.setPreferredSize(new Dimension(75, 28));
        tb.add(btnReset);

        lblStaffCount = new JLabel("...");
        lblStaffCount.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblStaffCount.setForeground(new Color(71, 85, 105));
        tb.add(lblStaffCount);

        p.add(tb, BorderLayout.NORTH);

        String[] cols = {"Staff ID", "Full Name", "Role", "Department", "Email", "Phone"};
        staffModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        staffTable = new JTable(staffModel);
        staffTable.setRowHeight(26);
        staffTable.setFont(new Font("SansSerif", Font.PLAIN, 13));
        staffTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));

        loadStaff();
        p.add(new JScrollPane(staffTable), BorderLayout.CENTER);

        tfStaffSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { loadStaff(); }
            public void removeUpdate(DocumentEvent e) { loadStaff(); }
            public void changedUpdate(DocumentEvent e) { loadStaff(); }
        });

        cbStaffDept.addActionListener(e -> loadStaff());

        btnReset.addActionListener(e -> {
            tfStaffSearch.setText("");
            cbStaffDept.setSelectedIndex(0);
            loadStaff();
        });

        return p;
    }

    private void loadStaff() {
        staffModel.setRowCount(0);
        String filter = (tfStaffSearch != null) ? tfStaffSearch.getText() : "";
        String dept = (cbStaffDept != null) ? (String) cbStaffDept.getSelectedItem() : "All Departments";

        List<Staff> list = userDAO.searchStaff(filter);
        int matched = 0;
        for (Staff st : list) {
            if (!dept.equals("All Departments") && !dept.equalsIgnoreCase(st.getDepartment())) {
                continue;
            }
            matched++;
            staffModel.addRow(new Object[]{
                    st.getStaffId(),
                    st.getFullName(),
                    st.getRole(),
                    st.getDepartment(),
                    st.getEmail(),
                    st.getPhone()
            });
        }
        if (lblStaffCount != null) {
            lblStaffCount.setText("(" + matched + " staff members)");
        }
    }

    // Tab 4: Analytics & Insights Panel (Replaces Overview & Stats with deep integrated intelligence)
    private JPanel createAnalyticsTab() {
        JPanel container = new JPanel(new BorderLayout(15, 15));
        container.setBackground(new Color(248, 250, 252));
        container.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        // Top Toolbar
        JPanel pTop = new JPanel(new BorderLayout());
        pTop.setOpaque(false);

        JPanel pTitles = new JPanel(new GridLayout(2, 1, 0, 2));
        pTitles.setOpaque(false);
        JLabel lblHeading = new JLabel("Hostel Operational Analytics & Grievance Intelligence");
        lblHeading.setFont(new Font("SansSerif", Font.BOLD, 17));
        lblHeading.setForeground(new Color(15, 23, 42));

        JLabel lblSub = new JLabel("Integrated cross-facility metrics, category distributions, SLA lifecycle, and wing demographics");
        lblSub.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblSub.setForeground(new Color(100, 116, 139));
        pTitles.add(lblHeading);
        pTitles.add(lblSub);

        JButton btnRefreshAnalytics = UIUtils.createButton("🔄 Refresh Live Analytics", new Color(37, 99, 235));
        btnRefreshAnalytics.setPreferredSize(new Dimension(195, 32));
        btnRefreshAnalytics.addActionListener(e -> {
            tabPane.setComponentAt(3, createAnalyticsTab());
        });

        pTop.add(pTitles, BorderLayout.WEST);
        pTop.add(btnRefreshAnalytics, BorderLayout.EAST);
        container.add(pTop, BorderLayout.NORTH);

        // Fetch metrics
        List<Complaint> complaints = complaintDAO.getAllComplaints();
        List<Student> students = userDAO.getAllStudents();
        List<Staff> staff = userDAO.getAllStaff();

        int totalStudents = students.size();
        int totalStaff = staff.size();
        int totalComplaints = complaints.size();
        int openCnt = 0;
        int inProgressCnt = 0;
        int underReviewCnt = 0;
        int resolvedCnt = 0;
        int closedCnt = 0;
        int concerningCnt = 0;

        Map<String, Integer> catCounts = new LinkedHashMap<>();
        String[] standardCats = {"Internet", "Plumbing", "Electrical", "Cleanliness", "Mess", "Security"};
        for (String c : standardCats) catCounts.put(c, 0);

        Map<String, Integer> blockComplaints = new LinkedHashMap<>();
        blockComplaints.put("Alpha", 0);
        blockComplaints.put("Beta", 0);
        blockComplaints.put("Charlie", 0);
        blockComplaints.put("Delta", 0);

        List<Complaint> criticalList = new ArrayList<>();

        for (Complaint c : complaints) {
            String st = c.getStatus() != null ? c.getStatus() : "Open";
            if ("Open".equalsIgnoreCase(st)) openCnt++;
            else if ("Under Review".equalsIgnoreCase(st)) underReviewCnt++;
            else if ("In Progress".equalsIgnoreCase(st)) inProgressCnt++;
            else if ("Resolved".equalsIgnoreCase(st)) resolvedCnt++;
            else if ("Closed".equalsIgnoreCase(st)) closedCnt++;

            if (c.isConcerning()) {
                concerningCnt++;
                criticalList.add(c);
            }

            String cat = c.getCategory() != null ? c.getCategory() : "Other";
            boolean matchedCat = false;
            for (String sc : standardCats) {
                if (sc.equalsIgnoreCase(cat)) {
                    catCounts.put(sc, catCounts.get(sc) + 1);
                    matchedCat = true;
                    break;
                }
            }
            if (!matchedCat) {
                catCounts.put(cat, catCounts.getOrDefault(cat, 0) + 1);
            }

            String loc = c.getLocation() != null ? c.getLocation() : "";
            for (String blk : blockComplaints.keySet()) {
                if (loc.toLowerCase().contains(blk.toLowerCase())) {
                    blockComplaints.put(blk, blockComplaints.get(blk) + 1);
                }
            }
        }

        int activeUnresolved = openCnt + underReviewCnt + inProgressCnt;
        double resRate = totalComplaints > 0 ? ((double)(resolvedCnt + closedCnt) / totalComplaints) * 100.0 : 0.0;

        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setOpaque(false);

        // Row 1: Executive KPI Cards (Incorporates all stats from former Overview & Stats)
        JPanel pKpi = new JPanel(new GridLayout(2, 3, 12, 12));
        pKpi.setOpaque(false);
        pKpi.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));

        pKpi.add(createKpiCard("👥 Total Enrolled Students", String.valueOf(totalStudents), "500 in InHabitDB (100% Occupancy)", new Color(37, 99, 235)));
        pKpi.add(createKpiCard("🛠️ Maintenance & Ops Staff", String.valueOf(totalStaff), "20 Active (6 Technical Trades)", new Color(16, 185, 129)));
        pKpi.add(createKpiCard("📋 Total Grievances Logged", String.valueOf(totalComplaints), activeUnresolved + " Active / " + (resolvedCnt + closedCnt) + " Closed", new Color(139, 92, 246)));
        pKpi.add(createKpiCard("⚡ Active Pending Issues", String.valueOf(activeUnresolved), openCnt + " Open, " + inProgressCnt + " In Progress", new Color(245, 158, 11)));
        pKpi.add(createKpiCard("🚨 Critical Escalated SLA", String.valueOf(concerningCnt), "Tickets with >= 30 Community Votes", new Color(239, 68, 68)));
        pKpi.add(createKpiCard("📈 SLA Resolution Rate", String.format("%.1f%%", resRate), "Target SLA: > 80.0% (Avg Turnaround 2.3d)", new Color(13, 148, 136)));

        mainContent.add(Box.createVerticalStrut(8));
        mainContent.add(pKpi);
        mainContent.add(Box.createVerticalStrut(15));

        // Row 2: Deep Analytics & Visual Pipelines
        JPanel pGrid2 = new JPanel(new GridLayout(1, 2, 15, 15));
        pGrid2.setOpaque(false);

        // Left Column: Category Breakdown & Status Pipeline
        JPanel pLeft = new JPanel();
        pLeft.setLayout(new BoxLayout(pLeft, BoxLayout.Y_AXIS));
        pLeft.setOpaque(false);

        pLeft.add(createCategoryAnalyticsCard(catCounts, totalComplaints));
        pLeft.add(Box.createVerticalStrut(12));
        pLeft.add(createPipelineCard(openCnt, underReviewCnt, inProgressCnt, resolvedCnt, closedCnt, totalComplaints));

        // Right Column: Block Demographics & Concerning Alert Queue
        JPanel pRight = new JPanel();
        pRight.setLayout(new BoxLayout(pRight, BoxLayout.Y_AXIS));
        pRight.setOpaque(false);

        pRight.add(createBlockDemographicsCard(blockComplaints, totalStudents));
        pRight.add(Box.createVerticalStrut(12));
        pRight.add(createCriticalAlertsQueueCard(criticalList));

        pGrid2.add(pLeft);
        pGrid2.add(pRight);
        mainContent.add(pGrid2);

        JScrollPane scroll = new JScrollPane(mainContent);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        container.add(scroll, BorderLayout.CENTER);

        return container;
    }

    private JPanel createKpiCard(String title, String val, String sub, Color accent) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, accent),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));

        JLabel lTitle = new JLabel(title);
        lTitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lTitle.setForeground(new Color(100, 116, 139));

        JLabel lVal = new JLabel(val);
        lVal.setFont(new Font("SansSerif", Font.BOLD, 22));
        lVal.setForeground(new Color(15, 23, 42));

        JLabel lSub = new JLabel(sub);
        lSub.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lSub.setForeground(new Color(148, 163, 184));

        card.add(lTitle, BorderLayout.NORTH);
        card.add(lVal, BorderLayout.CENTER);
        card.add(lSub, BorderLayout.SOUTH);
        return card;
    }

    private JPanel createCategoryAnalyticsCard(Map<String, Integer> catCounts, int total) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)
        ));

        JLabel lblTitle = new JLabel("📊 Grievance Category Breakdown");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblTitle.setForeground(new Color(15, 23, 42));
        card.add(lblTitle, BorderLayout.NORTH);

        JPanel pRows = new JPanel(new GridLayout(0, 1, 6, 8));
        pRows.setOpaque(false);

        Color[] colors = {
                new Color(37, 99, 235),  // Internet - Blue
                new Color(13, 148, 136), // Plumbing - Teal
                new Color(245, 158, 11), // Electrical - Amber
                new Color(16, 185, 129), // Cleanliness - Emerald
                new Color(249, 115, 22), // Mess - Orange
                new Color(239, 68, 68)   // Security - Red
        };

        int idx = 0;
        for (Map.Entry<String, Integer> entry : catCounts.entrySet()) {
            String cat = entry.getKey();
            int count = entry.getValue();
            double pct = total > 0 ? ((double) count / total) * 100.0 : 0.0;
            Color barColor = colors[idx % colors.length];
            idx++;

            JPanel row = new JPanel(new BorderLayout(8, 0));
            row.setOpaque(false);

            JLabel lName = new JLabel(cat);
            lName.setFont(new Font("SansSerif", Font.BOLD, 12));
            lName.setPreferredSize(new Dimension(85, 20));

            JPanel pBarWrap = new JPanel(new BorderLayout());
            pBarWrap.setOpaque(false);
            pBarWrap.setPreferredSize(new Dimension(140, 16));

            final double frac = total > 0 ? (double) count / total : 0.0;
            JPanel bar = new JPanel() {
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(241, 245, 249));
                    g2.fillRoundRect(0, 2, getWidth(), getHeight() - 4, 6, 6);
                    g2.setColor(barColor);
                    int fillW = (int) (getWidth() * frac);
                    if (fillW > 0) {
                        g2.fillRoundRect(0, 2, fillW, getHeight() - 4, 6, 6);
                    }
                    g2.dispose();
                }
            };
            bar.setOpaque(false);
            pBarWrap.add(bar, BorderLayout.CENTER);

            JLabel lCount = new JLabel(count + " (" + String.format("%.0f%%", pct) + ")");
            lCount.setFont(new Font("SansSerif", Font.PLAIN, 12));
            lCount.setForeground(new Color(71, 85, 105));
            lCount.setPreferredSize(new Dimension(65, 20));
            lCount.setHorizontalAlignment(SwingConstants.RIGHT);

            row.add(lName, BorderLayout.WEST);
            row.add(pBarWrap, BorderLayout.CENTER);
            row.add(lCount, BorderLayout.EAST);
            pRows.add(row);
        }

        card.add(pRows, BorderLayout.CENTER);
        return card;
    }

    private JPanel createPipelineCard(int open, int underReview, int inProgress, int resolved, int closed, int total) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)
        ));

        JLabel lblTitle = new JLabel("🔄 Grievance Resolution Lifecycle Pipeline");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblTitle.setForeground(new Color(15, 23, 42));
        card.add(lblTitle, BorderLayout.NORTH);

        JPanel pStages = new JPanel(new GridLayout(1, 5, 8, 0));
        pStages.setOpaque(false);

        pStages.add(createStagePill("Open", open, new Color(245, 158, 11)));
        pStages.add(createStagePill("Under Review", underReview, new Color(59, 130, 246)));
        pStages.add(createStagePill("In Progress", inProgress, new Color(139, 92, 246)));
        pStages.add(createStagePill("Resolved", resolved, new Color(16, 185, 129)));
        pStages.add(createStagePill("Closed", closed, new Color(100, 116, 139)));

        card.add(pStages, BorderLayout.CENTER);
        return card;
    }

    private JPanel createStagePill(String stage, int count, Color col) {
        JPanel pill = new JPanel(new GridLayout(2, 1, 0, 2));
        pill.setBackground(new Color(col.getRed(), col.getGreen(), col.getBlue(), 20));
        pill.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(2, 0, 0, 0, col),
                BorderFactory.createEmptyBorder(8, 6, 8, 6)
        ));

        JLabel lVal = new JLabel(String.valueOf(count), SwingConstants.CENTER);
        lVal.setFont(new Font("SansSerif", Font.BOLD, 17));
        lVal.setForeground(col);

        JLabel lName = new JLabel(stage, SwingConstants.CENTER);
        lName.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lName.setForeground(new Color(71, 85, 105));

        pill.add(lVal);
        pill.add(lName);
        return pill;
    }

    private JPanel createBlockDemographicsCard(Map<String, Integer> blockComplaints, int totalStudents) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)
        ));

        JLabel lblTitle = new JLabel("🏢 Hostel Wing & Block Demographics");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblTitle.setForeground(new Color(15, 23, 42));
        card.add(lblTitle, BorderLayout.NORTH);

        JPanel pBlocks = new JPanel(new GridLayout(2, 2, 10, 10));
        pBlocks.setOpaque(false);

        int perBlock = totalStudents / 4;
        pBlocks.add(createBlockItem("Block Alpha (Boys)", perBlock + " Residents", blockComplaints.getOrDefault("Alpha", 0) + " Issues", new Color(37, 99, 235)));
        pBlocks.add(createBlockItem("Block Beta (Girls)", perBlock + " Residents", blockComplaints.getOrDefault("Beta", 0) + " Issues", new Color(219, 39, 119)));
        pBlocks.add(createBlockItem("Block Charlie (Engg)", perBlock + " Residents", blockComplaints.getOrDefault("Charlie", 0) + " Issues", new Color(13, 148, 136)));
        pBlocks.add(createBlockItem("Block Delta (B.Tech)", perBlock + " Residents", blockComplaints.getOrDefault("Delta", 0) + " Issues", new Color(139, 92, 246)));

        card.add(pBlocks, BorderLayout.CENTER);
        return card;
    }

    private JPanel createBlockItem(String title, String residents, String issues, Color accent) {
        JPanel item = new JPanel(new GridLayout(3, 1, 0, 2));
        item.setBackground(new Color(248, 250, 252));
        item.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 3, 0, 0, accent),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));

        JLabel lTitle = new JLabel(title);
        lTitle.setFont(new Font("SansSerif", Font.BOLD, 12));
        lTitle.setForeground(new Color(15, 23, 42));

        JLabel lRes = new JLabel(residents);
        lRes.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lRes.setForeground(new Color(71, 85, 105));

        JLabel lIss = new JLabel(issues);
        lIss.setFont(new Font("SansSerif", Font.BOLD, 11));
        lIss.setForeground(accent);

        item.add(lTitle);
        item.add(lRes);
        item.add(lIss);
        return item;
    }

    private JPanel createCriticalAlertsQueueCard(List<Complaint> criticalList) {
        JPanel card = new JPanel(new BorderLayout(8, 8));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)
        ));

        JPanel pHead = new JPanel(new BorderLayout());
        pHead.setOpaque(false);
        JLabel lblTitle = new JLabel("🚨 High Priority Escalated SLA Queue (>= 30 Votes)");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblTitle.setForeground(new Color(220, 38, 38));

        JLabel lblCount = new JLabel(criticalList.size() + " Critical");
        lblCount.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblCount.setForeground(new Color(220, 38, 38));

        pHead.add(lblTitle, BorderLayout.WEST);
        pHead.add(lblCount, BorderLayout.EAST);
        card.add(pHead, BorderLayout.NORTH);

        String[] cols = {"Ticket ID", "Title", "Category", "Location", "Votes", "Status"};
        DefaultTableModel critModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        for (Complaint c : criticalList) {
            critModel.addRow(new Object[]{
                    c.getTicketId(), c.getTitle(), c.getCategory(), c.getLocation(), c.getVoteCount(), c.getStatus()
            });
        }

        JTable critTable = new JTable(critModel);
        critTable.setRowHeight(24);
        critTable.setFont(new Font("SansSerif", Font.PLAIN, 12));
        critTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));

        JScrollPane sp = new JScrollPane(critTable);
        sp.setPreferredSize(new Dimension(Integer.MAX_VALUE, 90));
        card.add(sp, BorderLayout.CENTER);

        JPanel pActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        pActions.setOpaque(false);
        JButton btnInspectCrit = UIUtils.createButton("Inspect Selected Ticket", new Color(220, 38, 38));
        btnInspectCrit.setPreferredSize(new Dimension(180, 28));
        btnInspectCrit.addActionListener(e -> {
            int row = critTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Select a critical ticket row to view details.", "Notice", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int ticketId = Integer.parseInt(critModel.getValueAt(row, 0).toString());
            Complaint c = complaintDAO.getComplaintById(ticketId);
            UIUtils.showComplaintDetailsDialog(this, c, currentAdmin, complaintDAO, () -> {
                loadComplaints();
                tabPane.setComponentAt(3, createAnalyticsTab());
            });
        });
        pActions.add(btnInspectCrit);
        card.add(pActions, BorderLayout.SOUTH);

        return card;
    }

    // Helper: Shared Preview Card Component for Admin
    private JPanel createAdminPreviewCard(JLabel lblTitle, JLabel lblMeta, JTextArea taDesc, JButton btnModal, JButton btnSec) {
        JPanel card = new JPanel(new BorderLayout(8, 6));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        card.setPreferredSize(new Dimension(Integer.MAX_VALUE, 125));

        JPanel pHeader = new JPanel(new BorderLayout(5, 2));
        pHeader.setOpaque(false);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblTitle.setForeground(new Color(15, 23, 42));
        lblMeta.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblMeta.setForeground(new Color(100, 116, 139));
        pHeader.add(lblTitle, BorderLayout.NORTH);
        pHeader.add(lblMeta, BorderLayout.SOUTH);

        taDesc.setLineWrap(true);
        taDesc.setWrapStyleWord(true);
        taDesc.setEditable(false);
        taDesc.setFont(new Font("SansSerif", Font.PLAIN, 12));
        taDesc.setBackground(new Color(248, 250, 252));
        taDesc.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        JScrollPane sp = new JScrollPane(taDesc);
        sp.setPreferredSize(new Dimension(Integer.MAX_VALUE, 45));

        JPanel pActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        pActions.setOpaque(false);
        if (btnSec != null) pActions.add(btnSec);
        if (btnModal != null) pActions.add(btnModal);

        card.add(pHeader, BorderLayout.NORTH);
        card.add(sp, BorderLayout.CENTER);
        card.add(pActions, BorderLayout.SOUTH);
        return card;
    }
}
