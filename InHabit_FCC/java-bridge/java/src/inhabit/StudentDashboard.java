package inhabit;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.*;

// Student Portal GUI Dashboard with Real-Time Search, Separator Filters & Lost-Found UI
public class StudentDashboard extends JFrame {
    private Student currentStudent;
    private ComplaintDAO complaintDAO;
    private UserDAO userDAO;

    // Components
    JTabbedPane tabPane;
    JTable feedTable, myTable, lfTable;
    DefaultTableModel feedModel, myModel, lfModel;

    // Feed Search & Separator Filter Controls
    private JTextField tfFeedSearch;
    private JComboBox<String> cbFeedCategory;
    private JComboBox<String> cbFeedStatus;
    private JComboBox<String> cbFeedWing;
    private JLabel lblFeedCount;

    // My Grievances Search & Filter Controls
    private JTextField tfMySearch;
    private JComboBox<String> cbMyCategory;
    private JComboBox<String> cbMyStatus;
    private JLabel lblMyCount;

    // Lost & Found Search & Filter Controls
    private JTextField tfLfSearch;
    private JComboBox<String> cbLfType;
    private JComboBox<String> cbLfStatus;
    private JLabel lblLfCount;

    // Live Details Inspectors (Guarantee immediate visibility upon single-click or selection)
    private JLabel lblFeedInspTitle;
    private JLabel lblFeedInspMeta;
    private JTextArea taFeedInspDesc;
    private JButton btnFeedInspModal;
    private JButton btnFeedInspVote;

    private JLabel lblMyInspTitle;
    private JLabel lblMyInspMeta;
    private JTextArea taMyInspDesc;
    private JButton btnMyInspModal;

    private JLabel lblLfInspTitle;
    private JLabel lblLfInspMeta;
    private JTextArea taLfInspDesc;
    private JButton btnLfInspModal;
    private JButton btnLfInspClaim;

    public StudentDashboard(Student student) {
        this.currentStudent = student;
        this.complaintDAO = new ComplaintDAO();
        this.userDAO = new UserDAO();

        // Step 1: Configure Frame
        setTitle("InHabit - Student Grievance Portal");
        setSize(1020, 720);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Step 2: Top Header Bar
        JPanel pTop = new JPanel(new BorderLayout(15, 10));
        pTop.setBackground(new Color(30, 41, 59));
        pTop.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel lblBrand = new JLabel("InHabit | Campus Grievances");
        lblBrand.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblBrand.setForeground(Color.WHITE);

        JPanel pUserMeta = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        pUserMeta.setOpaque(false);

        JLabel lblUserInfo = new JLabel(student.getFullName() + " (PRN: " + student.getPrn() + " | Room: " + student.getLocation() + ")");
        lblUserInfo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblUserInfo.setForeground(new Color(226, 232, 240));

        // Guaranteed visible Logout button
        JButton btnLogout = UIUtils.createButton("Logout", new Color(220, 38, 38));
        btnLogout.setPreferredSize(new Dimension(85, 30));
        btnLogout.addActionListener(e -> {
            dispose();
            new LoginFrame();
        });

        pUserMeta.add(lblUserInfo);
        pUserMeta.add(btnLogout);

        pTop.add(lblBrand, BorderLayout.WEST);
        pTop.add(pUserMeta, BorderLayout.EAST);
        add(pTop, BorderLayout.NORTH);

        // Step 3: Main Tabbed Pane (5 Tabs)
        tabPane = new JTabbedPane();
        tabPane.setFont(new Font("SansSerif", Font.BOLD, 13));

        // Tab 1: My Profile
        tabPane.addTab("My Profile", createProfilePanel());

        // Tab 2: Campus Feed (with Search & Separator filter)
        tabPane.addTab("Campus Feed", createFeedPanel());

        // Tab 3: File Grievance
        tabPane.addTab("File Grievance", createFilePanel());

        // Tab 4: My Grievances (with Search & Separator filter)
        tabPane.addTab("My Grievances", createMyGrievancesPanel());

        // Tab 5: Campus Notices & Lost-Found (Full Interactive UI)
        tabPane.addTab("Campus Notices & Lost-Found", createLostAndFoundPanel());

        add(tabPane, BorderLayout.CENTER);

        setVisible(true);
    }

    // Tab 1: Profile Panel
    private JPanel createProfilePanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(new Color(248, 250, 252));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        // Main card
        JPanel card = new JPanel(new GridLayout(0, 2, 20, 15));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));

        card.add(createLabel("Full Name:", true));
        card.add(createLabel(currentStudent.getFullName(), false));

        card.add(createLabel("Permanent Reg. Number (PRN):", true));
        card.add(createLabel(currentStudent.getPrn(), false));

        card.add(createLabel("Hostel Room / Location:", true));
        card.add(createLabel(currentStudent.getLocation(), false));

        card.add(createLabel("Branch & Division:", true));
        card.add(createLabel(currentStudent.getBranchDivision(), false));

        card.add(createLabel("Institutional Email:", true));
        card.add(createLabel(currentStudent.getEmail(), false));

        card.add(createLabel("Contact Phone:", true));
        card.add(createLabel(currentStudent.getPhone(), false));

        card.add(createLabel("Gender:", true));
        card.add(createLabel(currentStudent.getGender(), false));

        // Roommates section dynamically queried
        List<Student> roommates = userDAO.getRoommates(currentStudent.getLocation(), currentStudent.getPrn());
        StringBuilder rmNames = new StringBuilder();
        if (roommates.isEmpty()) {
            rmNames.append("None recorded");
        } else {
            for (int i = 0; i < roommates.size(); i++) {
                if (i > 0) rmNames.append(", ");
                rmNames.append(roommates.get(i).getFullName());
            }
        }
        card.add(createLabel("Roommates (" + currentStudent.getLocation() + "):", true));
        card.add(createLabel(rmNames.toString(), false));

        panel.add(card, BorderLayout.NORTH);
        return panel;
    }

    private JLabel createLabel(String text, boolean bold) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("SansSerif", bold ? Font.BOLD : Font.PLAIN, 14));
        l.setForeground(bold ? new Color(15, 23, 42) : new Color(51, 65, 85));
        return l;
    }

    // Tab 2: Campus Feed Panel with Search & Separator Toolbar
    private JPanel createFeedPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(248, 250, 252));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        // Top Container: Action Buttons + Search & Separator Bar
        JPanel topContainer = new JPanel();
        topContainer.setLayout(new BoxLayout(topContainer, BoxLayout.Y_AXIS));
        topContainer.setOpaque(false);

        // Row 1: Actions
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        toolbar.setOpaque(false);

        JButton btnViewDetails = UIUtils.createButton("View Details", new Color(37, 99, 235));
        JButton btnVote = UIUtils.createButton("Upvote Genuine", new Color(16, 185, 129));
        JButton btnRefresh = UIUtils.createSecondaryButton("Refresh Feed");

        toolbar.add(btnViewDetails);
        toolbar.add(btnVote);
        toolbar.add(btnRefresh);

        JLabel lblHint = new JLabel(" (Double-click any complaint row to view full details)");
        lblHint.setFont(new Font("SansSerif", Font.ITALIC, 12));
        lblHint.setForeground(new Color(100, 116, 139));
        toolbar.add(lblHint);

        topContainer.add(toolbar);
        topContainer.add(Box.createVerticalStrut(10));

        // Horizontal divider line
        JSeparator sep = new JSeparator(SwingConstants.HORIZONTAL);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
        topContainer.add(sep);
        topContainer.add(Box.createVerticalStrut(8));

        // Row 2: Search Bar & Separator Filter Controls
        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        searchBar.setOpaque(false);

        JLabel lblSearch = new JLabel("🔍 Search:");
        lblSearch.setFont(new Font("SansSerif", Font.BOLD, 12));
        searchBar.add(lblSearch);

        tfFeedSearch = new JTextField(14);
        tfFeedSearch.setFont(new Font("SansSerif", Font.PLAIN, 12));
        tfFeedSearch.setToolTipText("Search keyword, ticket ID, title, description, or room...");
        searchBar.add(tfFeedSearch);

        // Separator between search input and category filter
        searchBar.add(createVerticalSeparator());

        JLabel lblCat = new JLabel("Category:");
        lblCat.setFont(new Font("SansSerif", Font.PLAIN, 12));
        searchBar.add(lblCat);

        String[] categories = {"All Categories", "Internet", "Plumbing", "Mess", "Cleanliness", "Electrical", "Security"};
        cbFeedCategory = new JComboBox<>(categories);
        cbFeedCategory.setFont(new Font("SansSerif", Font.PLAIN, 12));
        searchBar.add(cbFeedCategory);

        // Separator between category and status filter
        searchBar.add(createVerticalSeparator());

        JLabel lblStat = new JLabel("Status:");
        lblStat.setFont(new Font("SansSerif", Font.PLAIN, 12));
        searchBar.add(lblStat);

        String[] statuses = {"All Statuses", "Open", "Under Review", "In Progress", "Resolved", "Concerning Only"};
        cbFeedStatus = new JComboBox<>(statuses);
        cbFeedStatus.setFont(new Font("SansSerif", Font.PLAIN, 12));
        searchBar.add(cbFeedStatus);

        // Separator between status and wing filter
        searchBar.add(createVerticalSeparator());

        JLabel lblWing = new JLabel("Wing:");
        lblWing.setFont(new Font("SansSerif", Font.PLAIN, 12));
        searchBar.add(lblWing);

        String[] wings = {"All Wings", "Wing Alpha", "Wing Beta", "Wing Charlie", "Wing Delta"};
        cbFeedWing = new JComboBox<>(wings);
        cbFeedWing.setFont(new Font("SansSerif", Font.PLAIN, 12));
        searchBar.add(cbFeedWing);

        JButton btnClear = UIUtils.createSecondaryButton("✕ Clear");
        btnClear.setPreferredSize(new Dimension(75, 26));
        btnClear.setFont(new Font("SansSerif", Font.PLAIN, 11));
        searchBar.add(btnClear);

        lblFeedCount = new JLabel("...");
        lblFeedCount.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblFeedCount.setForeground(new Color(71, 85, 105));
        searchBar.add(lblFeedCount);

        topContainer.add(searchBar);
        panel.add(topContainer, BorderLayout.NORTH);

        // Table
        String[] cols = {"Ticket ID", "Title", "Category", "Location", "Status", "Genuine Votes", "Priority"};
        feedModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };

        feedTable = new JTable(feedModel);
        feedTable.setRowHeight(28);
        feedTable.setFont(new Font("SansSerif", Font.PLAIN, 13));
        feedTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        feedTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Custom renderer for priority badge
        feedTable.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable tbl, Object val, boolean isSel, boolean hasFoc, int r, int c) {
                Component comp = super.getTableCellRendererComponent(tbl, val, isSel, hasFoc, r, c);
                if ("CONCERNING".equals(val)) {
                    comp.setForeground(new Color(220, 38, 38));
                    setFont(getFont().deriveFont(Font.BOLD));
                } else {
                    comp.setForeground(new Color(100, 116, 139));
                }
                return comp;
            }
        });

        loadFeedData();

        JScrollPane scroll = new JScrollPane(feedTable);
        panel.add(scroll, BorderLayout.CENTER);

        // Listeners for Real-Time Search & Separator Filters
        DocumentListener searchListener = new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { loadFeedData(); }
            public void removeUpdate(DocumentEvent e) { loadFeedData(); }
            public void changedUpdate(DocumentEvent e) { loadFeedData(); }
        };
        tfFeedSearch.getDocument().addDocumentListener(searchListener);

        cbFeedCategory.addActionListener(e -> loadFeedData());
        cbFeedStatus.addActionListener(e -> loadFeedData());
        cbFeedWing.addActionListener(e -> loadFeedData());

        btnClear.addActionListener(e -> {
            tfFeedSearch.setText("");
            cbFeedCategory.setSelectedIndex(0);
            cbFeedStatus.setSelectedIndex(0);
            cbFeedWing.setSelectedIndex(0);
            loadFeedData();
        });

        // Live Details Inspector Panel for Feed (Instant inline details upon selection)
        lblFeedInspTitle = new JLabel("💡 Select any grievance above to preview full details.");
        lblFeedInspMeta = new JLabel("Live inspector displays full description, votes, location, and actions.");
        taFeedInspDesc = new JTextArea();
        btnFeedInspModal = UIUtils.createButton("View Full Modal", new Color(37, 99, 235));
        btnFeedInspModal.setPreferredSize(new Dimension(135, 28));
        btnFeedInspModal.setEnabled(false);
        btnFeedInspModal.addActionListener(e -> openSelectedFeedDetails());

        btnFeedInspVote = UIUtils.createButton("▲ Upvote", new Color(16, 185, 129));
        btnFeedInspVote.setPreferredSize(new Dimension(95, 28));
        btnFeedInspVote.setEnabled(false);
        btnFeedInspVote.addActionListener(e -> handleVoteFeedAction());

        JPanel pInsp = createPreviewCard(lblFeedInspTitle, lblFeedInspMeta, taFeedInspDesc, btnFeedInspModal, btnFeedInspVote);
        panel.add(pInsp, BorderLayout.SOUTH);

        // Selection listeners: single click, arrow keys, and list selection
        feedTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateFeedInspector();
            }
        });

        feedTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                updateFeedInspector();
                if (e.getClickCount() == 2) {
                    openSelectedFeedDetails();
                }
            }
        });

        // Action: View Details button
        btnViewDetails.addActionListener(e -> openSelectedFeedDetails());

        // Action: Vote button
        btnVote.addActionListener(e -> handleVoteFeedAction());

        // Action: Refresh
        btnRefresh.addActionListener(e -> loadFeedData());

        return panel;
    }

    private void handleVoteFeedAction() {
        int selectedRow = feedTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a complaint to upvote.", "Select Ticket", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelRow = feedTable.convertRowIndexToModel(selectedRow);
        int ticketId = Integer.parseInt(feedTable.getModel().getValueAt(modelRow, 0).toString());
        try {
            boolean success = complaintDAO.voteComplaint(ticketId, currentStudent.getPrn(), "Genuine");
            if (success) {
                JOptionPane.showMessageDialog(this, "Your Genuine vote has been recorded for Ticket #" + ticketId + "!", "Vote Registered", JOptionPane.INFORMATION_MESSAGE);
                loadFeedData();
                loadMyData();
            }
        } catch (InvalidVoteException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Vote Notice", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to vote: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateFeedInspector() {
        int row = feedTable.getSelectedRow();
        if (row == -1) {
            lblFeedInspTitle.setText("💡 Select any grievance above to preview full details.");
            lblFeedInspMeta.setText("Live inspector displays full description, votes, location, and actions.");
            taFeedInspDesc.setText("");
            btnFeedInspModal.setEnabled(false);
            btnFeedInspVote.setEnabled(false);
            return;
        }
        int modelRow = feedTable.convertRowIndexToModel(row);
        int ticketId = Integer.parseInt(feedTable.getModel().getValueAt(modelRow, 0).toString());
        Complaint c = complaintDAO.getComplaintById(ticketId);
        if (c != null) {
            lblFeedInspTitle.setText("Ticket #" + c.getTicketId() + ": " + c.getTitle());
            lblFeedInspMeta.setText("Category: " + c.getCategory() + "  |  Location: " + c.getLocation() + "  |  Status: " + c.getStatus() + (c.isConcerning() ? " [CONCERNING]" : "") + "  |  Votes: " + c.getVoteCount() + "  |  Filed By: " + c.getCreatedByPrn());
            taFeedInspDesc.setText(c.getDescription());
            btnFeedInspModal.setEnabled(true);
            btnFeedInspVote.setEnabled(!currentStudent.getPrn().equals(c.getCreatedByPrn()));
        }
    }

    private JComponent createVerticalSeparator() {
        JSeparator s = new JSeparator(SwingConstants.VERTICAL);
        s.setPreferredSize(new Dimension(2, 18));
        s.setForeground(new Color(203, 213, 225));
        return s;
    }

    private void openSelectedFeedDetails() {
        int selectedRow = feedTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a complaint row to view details.", "Select Complaint", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelRow = feedTable.convertRowIndexToModel(selectedRow);
        int ticketId = Integer.parseInt(feedTable.getModel().getValueAt(modelRow, 0).toString());
        Complaint c = complaintDAO.getComplaintById(ticketId);
        UIUtils.showComplaintDetailsDialog(this, c, currentStudent, complaintDAO, () -> {
            loadFeedData();
            loadMyData();
        });
    }

    private void loadFeedData() {
        feedModel.setRowCount(0);
        String kw = (tfFeedSearch != null) ? tfFeedSearch.getText() : "";
        String cat = (cbFeedCategory != null) ? (String) cbFeedCategory.getSelectedItem() : "All";
        String stat = (cbFeedStatus != null) ? (String) cbFeedStatus.getSelectedItem() : "All";
        String wing = (cbFeedWing != null) ? (String) cbFeedWing.getSelectedItem() : "All";

        List<Complaint> list = complaintDAO.searchComplaints(kw, cat, stat, wing);
        for (Complaint c : list) {
            String priority = c.isConcerning() ? "CONCERNING" : "Normal";
            feedModel.addRow(new Object[]{
                    c.getTicketId(),
                    c.getTitle(),
                    c.getCategory(),
                    c.getLocation(),
                    c.getStatus(),
                    c.getVoteCount(),
                    priority
            });
        }
        if (lblFeedCount != null) {
            lblFeedCount.setText("(" + list.size() + " matches)");
        }
    }

    // Tab 3: File Grievance Panel
    private JPanel createFilePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(248, 250, 252));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JPanel formCard = new JPanel(new GridBagLayout());
        formCard.setBackground(Color.WHITE);
        formCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                BorderFactory.createEmptyBorder(20, 25, 20, 25)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 5, 8, 5);

        // Title
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.2;
        formCard.add(createLabel("Grievance Title:", true), gbc);
        gbc.gridx = 1; gbc.weightx = 0.8;
        JTextField tfTitle = new JTextField(30);
        tfTitle.setFont(new Font("SansSerif", Font.PLAIN, 13));
        formCard.add(tfTitle, gbc);

        // Category dropdown
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.2;
        formCard.add(createLabel("Category:", true), gbc);
        gbc.gridx = 1; gbc.weightx = 0.8;
        String[] categories = {"Internet", "Plumbing", "Mess", "Cleanliness", "Electrical", "Security"};
        JComboBox<String> cbCategory = new JComboBox<>(categories);
        cbCategory.setFont(new Font("SansSerif", Font.PLAIN, 13));
        formCard.add(cbCategory, gbc);

        // Location
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.2;
        formCard.add(createLabel("Hostel Location:", true), gbc);
        gbc.gridx = 1; gbc.weightx = 0.8;
        JTextField tfLocation = new JTextField(currentStudent.getLocation(), 20);
        tfLocation.setFont(new Font("SansSerif", Font.PLAIN, 13));
        formCard.add(tfLocation, gbc);

        // Description
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.2;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        formCard.add(createLabel("Detailed Description:", true), gbc);
        gbc.gridx = 1; gbc.weightx = 0.8;
        JTextArea taDesc = new JTextArea(5, 30);
        taDesc.setFont(new Font("SansSerif", Font.PLAIN, 13));
        taDesc.setLineWrap(true);
        taDesc.setWrapStyleWord(true);
        JScrollPane spDesc = new JScrollPane(taDesc);
        formCard.add(spDesc, gbc);

        // Submit Button (Visible styled button)
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton btnSubmit = UIUtils.createButton("Submit Grievance Ticket", new Color(37, 99, 235));
        btnSubmit.setPreferredSize(new Dimension(220, 36));
        formCard.add(btnSubmit, gbc);

        panel.add(formCard, BorderLayout.NORTH);

        // Action: Submit
        btnSubmit.addActionListener(e -> {
            String title = tfTitle.getText().trim();
            String cat = (String) cbCategory.getSelectedItem();
            String loc = tfLocation.getText().trim();
            String desc = taDesc.getText().trim();

            Complaint c = new Complaint();
            c.setTitle(title);
            c.setCategory(cat);
            c.setLocation(loc);
            c.setDescription(desc);
            c.setCreatedByPrn(currentStudent.getPrn());

            try {
                boolean success = complaintDAO.fileComplaint(c);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Your grievance ticket #" + c.getTicketId() + " has been lodged successfully!", "Ticket Lodged", JOptionPane.INFORMATION_MESSAGE);
                    tfTitle.setText("");
                    taDesc.setText("");
                    loadFeedData();
                    loadMyData();
                    tabPane.setSelectedIndex(3); // Switch to My Grievances tab
                }
            } catch (InvalidComplaintException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation Error", JOptionPane.WARNING_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Failed to submit: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        return panel;
    }

    // Tab 4: My Grievances Panel with Search & Filter Separator Toolbar
    private JPanel createMyGrievancesPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(248, 250, 252));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JPanel topContainer = new JPanel();
        topContainer.setLayout(new BoxLayout(topContainer, BoxLayout.Y_AXIS));
        topContainer.setOpaque(false);

        // Row 1: Actions
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        toolbar.setOpaque(false);

        JButton btnViewDetails = UIUtils.createButton("View Details", new Color(37, 99, 235));
        JButton btnRefresh = UIUtils.createSecondaryButton("Refresh My Grievances");

        toolbar.add(btnViewDetails);
        toolbar.add(btnRefresh);

        JLabel lblHint = new JLabel(" (Double-click any complaint row to view full details)");
        lblHint.setFont(new Font("SansSerif", Font.ITALIC, 12));
        lblHint.setForeground(new Color(100, 116, 139));
        toolbar.add(lblHint);

        topContainer.add(toolbar);
        topContainer.add(Box.createVerticalStrut(10));

        JSeparator sep = new JSeparator(SwingConstants.HORIZONTAL);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
        topContainer.add(sep);
        topContainer.add(Box.createVerticalStrut(8));

        // Row 2: Search & Filter Separator Controls
        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        searchBar.setOpaque(false);

        JLabel lblSearch = new JLabel("🔍 Search My Tickets:");
        lblSearch.setFont(new Font("SansSerif", Font.BOLD, 12));
        searchBar.add(lblSearch);

        tfMySearch = new JTextField(15);
        tfMySearch.setFont(new Font("SansSerif", Font.PLAIN, 12));
        searchBar.add(tfMySearch);

        searchBar.add(createVerticalSeparator());

        JLabel lblCat = new JLabel("Category:");
        lblCat.setFont(new Font("SansSerif", Font.PLAIN, 12));
        searchBar.add(lblCat);

        String[] categories = {"All Categories", "Internet", "Plumbing", "Mess", "Cleanliness", "Electrical", "Security"};
        cbMyCategory = new JComboBox<>(categories);
        cbMyCategory.setFont(new Font("SansSerif", Font.PLAIN, 12));
        searchBar.add(cbMyCategory);

        searchBar.add(createVerticalSeparator());

        JLabel lblStat = new JLabel("Status:");
        lblStat.setFont(new Font("SansSerif", Font.PLAIN, 12));
        searchBar.add(lblStat);

        String[] statuses = {"All Statuses", "Open", "Under Review", "In Progress", "Resolved", "Closed"};
        cbMyStatus = new JComboBox<>(statuses);
        cbMyStatus.setFont(new Font("SansSerif", Font.PLAIN, 12));
        searchBar.add(cbMyStatus);

        JButton btnClear = UIUtils.createSecondaryButton("✕ Clear");
        btnClear.setPreferredSize(new Dimension(75, 26));
        btnClear.setFont(new Font("SansSerif", Font.PLAIN, 11));
        searchBar.add(btnClear);

        lblMyCount = new JLabel("...");
        lblMyCount.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblMyCount.setForeground(new Color(71, 85, 105));
        searchBar.add(lblMyCount);

        topContainer.add(searchBar);
        panel.add(topContainer, BorderLayout.NORTH);

        String[] cols = {"Ticket ID", "Title", "Category", "Location", "Status", "Genuine Votes", "Filed At"};
        myModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        myTable = new JTable(myModel);
        myTable.setRowHeight(28);
        myTable.setFont(new Font("SansSerif", Font.PLAIN, 13));
        myTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        myTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        loadMyData();

        JScrollPane scroll = new JScrollPane(myTable);
        panel.add(scroll, BorderLayout.CENTER);

        // Listeners
        tfMySearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { loadMyData(); }
            public void removeUpdate(DocumentEvent e) { loadMyData(); }
            public void changedUpdate(DocumentEvent e) { loadMyData(); }
        });

        cbMyCategory.addActionListener(e -> loadMyData());
        cbMyStatus.addActionListener(e -> loadMyData());

        btnClear.addActionListener(e -> {
            tfMySearch.setText("");
            cbMyCategory.setSelectedIndex(0);
            cbMyStatus.setSelectedIndex(0);
            loadMyData();
        });

        // Live Details Inspector Panel for My Grievances
        lblMyInspTitle = new JLabel("💡 Select any of your filed grievances above to preview details.");
        lblMyInspMeta = new JLabel("Track maintenance resolution progress and comments.");
        taMyInspDesc = new JTextArea();
        btnMyInspModal = UIUtils.createButton("View Full Modal", new Color(37, 99, 235));
        btnMyInspModal.setPreferredSize(new Dimension(135, 28));
        btnMyInspModal.setEnabled(false);
        btnMyInspModal.addActionListener(e -> openSelectedMyDetails());

        JPanel pInsp = createPreviewCard(lblMyInspTitle, lblMyInspMeta, taMyInspDesc, btnMyInspModal, null);
        panel.add(pInsp, BorderLayout.SOUTH);

        // Selection listeners: single click, arrow keys, and list selection
        myTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateMyInspector();
            }
        });

        // Action: View Details button
        btnViewDetails.addActionListener(e -> openSelectedMyDetails());

        // Action: Click updates preview, double-click row opens modal
        myTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                updateMyInspector();
                if (e.getClickCount() == 2) {
                    openSelectedMyDetails();
                }
            }
        });

        btnRefresh.addActionListener(e -> loadMyData());

        return panel;
    }

    private void updateMyInspector() {
        int row = myTable.getSelectedRow();
        if (row == -1) {
            lblMyInspTitle.setText("💡 Select any of your filed grievances above to preview details.");
            lblMyInspMeta.setText("Track maintenance resolution progress and comments.");
            taMyInspDesc.setText("");
            btnMyInspModal.setEnabled(false);
            return;
        }
        int modelRow = myTable.convertRowIndexToModel(row);
        int ticketId = Integer.parseInt(myTable.getModel().getValueAt(modelRow, 0).toString());
        Complaint c = complaintDAO.getComplaintById(ticketId);
        if (c != null) {
            lblMyInspTitle.setText("Ticket #" + c.getTicketId() + ": " + c.getTitle());
            lblMyInspMeta.setText("Category: " + c.getCategory() + "  |  Location: " + c.getLocation() + "  |  Status: " + c.getStatus() + "  |  Votes: " + c.getVoteCount() + "  |  Filed: " + (c.getCreatedAt() != null ? c.getCreatedAt() : "Recently"));
            taMyInspDesc.setText(c.getDescription());
            btnMyInspModal.setEnabled(true);
        }
    }

    private void openSelectedMyDetails() {
        int selectedRow = myTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select one of your complaints to view details.", "Select Complaint", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelRow = myTable.convertRowIndexToModel(selectedRow);
        int ticketId = Integer.parseInt(myTable.getModel().getValueAt(modelRow, 0).toString());
        Complaint c = complaintDAO.getComplaintById(ticketId);
        UIUtils.showComplaintDetailsDialog(this, c, currentStudent, complaintDAO, this::loadMyData);
    }

    private void loadMyData() {
        myModel.setRowCount(0);
        String kw = (tfMySearch != null) ? tfMySearch.getText() : "";
        String cat = (cbMyCategory != null) ? (String) cbMyCategory.getSelectedItem() : "All";
        String stat = (cbMyStatus != null) ? (String) cbMyStatus.getSelectedItem() : "All";

        List<Complaint> list = complaintDAO.searchStudentComplaints(currentStudent.getPrn(), kw, cat, stat);
        for (Complaint c : list) {
            myModel.addRow(new Object[]{
                    c.getTicketId(),
                    c.getTitle(),
                    c.getCategory(),
                    c.getLocation(),
                    c.getStatus(),
                    c.getVoteCount(),
                    c.getCreatedAt() != null ? c.getCreatedAt() : "Recently"
            });
        }
        if (lblMyCount != null) {
            lblMyCount.setText("(" + list.size() + " matches)");
        }
    }

    // Tab 5: Campus Notices & Lost-and-Found Panel (New Interactive UI)
    private JPanel createLostAndFoundPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(248, 250, 252));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JPanel topContainer = new JPanel();
        topContainer.setLayout(new BoxLayout(topContainer, BoxLayout.Y_AXIS));
        topContainer.setOpaque(false);

        // Row 1: Action buttons
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        toolbar.setOpaque(false);

        JButton btnReport = UIUtils.createButton("Report Item", new Color(37, 99, 235));
        JButton btnClaim = UIUtils.createButton("Claim Item", new Color(16, 185, 129));
        JButton btnDetails = UIUtils.createSecondaryButton("View Item Details");
        JButton btnRefresh = UIUtils.createSecondaryButton("Refresh Notices");

        toolbar.add(btnReport);
        toolbar.add(btnClaim);
        toolbar.add(btnDetails);
        toolbar.add(btnRefresh);

        JLabel lblNoticeHint = new JLabel(" (Report lost belongings or found items across campus)");
        lblNoticeHint.setFont(new Font("SansSerif", Font.ITALIC, 12));
        lblNoticeHint.setForeground(new Color(100, 116, 139));
        toolbar.add(lblNoticeHint);

        topContainer.add(toolbar);
        topContainer.add(Box.createVerticalStrut(10));

        JSeparator sep = new JSeparator(SwingConstants.HORIZONTAL);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
        topContainer.add(sep);
        topContainer.add(Box.createVerticalStrut(8));

        // Row 2: Search & Separator Toolbar
        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        searchBar.setOpaque(false);

        JLabel lblSearch = new JLabel("🔍 Search Notices:");
        lblSearch.setFont(new Font("SansSerif", Font.BOLD, 12));
        searchBar.add(lblSearch);

        tfLfSearch = new JTextField(15);
        tfLfSearch.setFont(new Font("SansSerif", Font.PLAIN, 12));
        tfLfSearch.setToolTipText("Search item name, location, keyword...");
        searchBar.add(tfLfSearch);

        searchBar.add(createVerticalSeparator());

        JLabel lblType = new JLabel("Type:");
        lblType.setFont(new Font("SansSerif", Font.PLAIN, 12));
        searchBar.add(lblType);

        String[] types = {"All Types", "Lost", "Found"};
        cbLfType = new JComboBox<>(types);
        cbLfType.setFont(new Font("SansSerif", Font.PLAIN, 12));
        searchBar.add(cbLfType);

        searchBar.add(createVerticalSeparator());

        JLabel lblStatus = new JLabel("Status:");
        lblStatus.setFont(new Font("SansSerif", Font.PLAIN, 12));
        searchBar.add(lblStatus);

        String[] statuses = {"All Statuses", "Open", "Claimed"};
        cbLfStatus = new JComboBox<>(statuses);
        cbLfStatus.setFont(new Font("SansSerif", Font.PLAIN, 12));
        searchBar.add(cbLfStatus);

        JButton btnClear = UIUtils.createSecondaryButton("✕ Clear");
        btnClear.setPreferredSize(new Dimension(75, 26));
        btnClear.setFont(new Font("SansSerif", Font.PLAIN, 11));
        searchBar.add(btnClear);

        lblLfCount = new JLabel("...");
        lblLfCount.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblLfCount.setForeground(new Color(71, 85, 105));
        searchBar.add(lblLfCount);

        topContainer.add(searchBar);
        panel.add(topContainer, BorderLayout.NORTH);

        // Table
        String[] cols = {"Item ID", "Item Name", "Type", "Location Found / Lost", "Status", "Reported By PRN", "Date"};
        lfModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        lfTable = new JTable(lfModel);
        lfTable.setRowHeight(28);
        lfTable.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lfTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        lfTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Improved cell renderer with selection contrast preservation
        lfTable.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable tbl, Object val, boolean isSel, boolean hasFoc, int r, int c) {
                Component comp = super.getTableCellRendererComponent(tbl, val, isSel, hasFoc, r, c);
                if (isSel) {
                    comp.setForeground(tbl.getSelectionForeground());
                    comp.setBackground(tbl.getSelectionBackground());
                } else {
                    comp.setBackground(r % 2 == 0 ? Color.WHITE : new Color(248, 250, 252));
                    if ("Lost".equals(val)) {
                        comp.setForeground(new Color(225, 29, 72)); // Rose
                        setFont(getFont().deriveFont(Font.BOLD));
                    } else if ("Found".equals(val)) {
                        comp.setForeground(new Color(13, 148, 136)); // Teal
                        setFont(getFont().deriveFont(Font.BOLD));
                    } else {
                        comp.setForeground(new Color(30, 41, 59));
                        setFont(getFont().deriveFont(Font.PLAIN));
                    }
                }
                return comp;
            }
        });

        loadLostAndFoundData();

        JScrollPane scroll = new JScrollPane(lfTable);
        panel.add(scroll, BorderLayout.CENTER);

        // Live Notice Details Inspector
        lblLfInspTitle = new JLabel("💡 Select any campus notice above to preview item description.");
        lblLfInspMeta = new JLabel("View found locations, contact details, and claim status.");
        taLfInspDesc = new JTextArea();
        btnLfInspModal = UIUtils.createButton("View Notice Modal", new Color(37, 99, 235));
        btnLfInspModal.setPreferredSize(new Dimension(145, 28));
        btnLfInspModal.setEnabled(false);
        btnLfInspModal.addActionListener(e -> openSelectedNoticeDetails());

        btnLfInspClaim = UIUtils.createButton("Claim Notice", new Color(16, 185, 129));
        btnLfInspClaim.setPreferredSize(new Dimension(110, 28));
        btnLfInspClaim.setEnabled(false);
        btnLfInspClaim.addActionListener(e -> handleClaimAction());

        JPanel pInsp = createPreviewCard(lblLfInspTitle, lblLfInspMeta, taLfInspDesc, btnLfInspModal, btnLfInspClaim);
        panel.add(pInsp, BorderLayout.SOUTH);

        // Selection listeners: single click, arrow keys, and list selection
        lfTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateLfInspector();
            }
        });

        lfTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                updateLfInspector();
                if (e.getClickCount() == 2) {
                    openSelectedNoticeDetails();
                }
            }
        });

        // Real-time search listeners
        tfLfSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { loadLostAndFoundData(); }
            public void removeUpdate(DocumentEvent e) { loadLostAndFoundData(); }
            public void changedUpdate(DocumentEvent e) { loadLostAndFoundData(); }
        });

        cbLfType.addActionListener(e -> loadLostAndFoundData());
        cbLfStatus.addActionListener(e -> loadLostAndFoundData());

        btnClear.addActionListener(e -> {
            tfLfSearch.setText("");
            cbLfType.setSelectedIndex(0);
            cbLfStatus.setSelectedIndex(0);
            loadLostAndFoundData();
        });

        // Report item action
        btnReport.addActionListener(e -> showReportItemDialog());

        // Claim item action
        btnClaim.addActionListener(e -> handleClaimAction());

        // View details action
        btnDetails.addActionListener(e -> openSelectedNoticeDetails());

        btnRefresh.addActionListener(e -> loadLostAndFoundData());

        return panel;
    }

    private void handleClaimAction() {
        int row = lfTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select an item notice to claim.", "Select Item", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelRow = lfTable.convertRowIndexToModel(row);
        String itemId = lfTable.getModel().getValueAt(modelRow, 0).toString().trim();
        String currentSt = lfTable.getModel().getValueAt(modelRow, 4).toString().trim();
        if ("Claimed".equalsIgnoreCase(currentSt)) {
            JOptionPane.showMessageDialog(this, "Item " + itemId + " is already marked as Claimed.", "Notice", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to mark Item " + itemId + " as Claimed by its rightful owner?", "Confirm Claim", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            boolean done = complaintDAO.claimLostAndFound(itemId);
            if (done) {
                JOptionPane.showMessageDialog(this, "Item " + itemId + " has been successfully marked as Claimed!", "Claim Registered", JOptionPane.INFORMATION_MESSAGE);
                loadLostAndFoundData();
                updateLfInspector();
            }
        }
    }

    private void updateLfInspector() {
        int row = lfTable.getSelectedRow();
        if (row == -1) {
            lblLfInspTitle.setText("💡 Select any campus notice above to preview item description.");
            lblLfInspMeta.setText("View found locations, contact details, and claim status.");
            taLfInspDesc.setText("");
            btnLfInspModal.setEnabled(false);
            btnLfInspClaim.setEnabled(false);
            return;
        }
        int modelRow = lfTable.convertRowIndexToModel(row);
        String itemId = lfTable.getModel().getValueAt(modelRow, 0).toString().trim();
        List<LostAndFoundItem> all = complaintDAO.getAllLostAndFound();
        LostAndFoundItem target = null;
        for (LostAndFoundItem it : all) {
            if (itemId.equalsIgnoreCase(it.getItemId())) {
                target = it;
                break;
            }
        }
        if (target != null) {
            lblLfInspTitle.setText("Notice #" + target.getItemId() + ": " + target.getItemName() + " (" + target.getItemType() + ")");
            lblLfInspMeta.setText("Location: " + target.getLocationFound() + "  |  Status: " + target.getStatus() + "  |  Reported By: " + target.getReportedByPrn() + "  |  Date: " + target.getReportedAt());
            taLfInspDesc.setText(target.getDescription());
            btnLfInspModal.setEnabled(true);
            btnLfInspClaim.setEnabled(!"Claimed".equalsIgnoreCase(target.getStatus()));
        }
    }

    private void openSelectedNoticeDetails() {
        int row = lfTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select an item row to inspect details.", "Select Notice", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelRow = lfTable.convertRowIndexToModel(row);
        String itemId = lfTable.getModel().getValueAt(modelRow, 0).toString().trim();
        List<LostAndFoundItem> all = complaintDAO.getAllLostAndFound();
        LostAndFoundItem target = null;
        for (LostAndFoundItem item : all) {
            if (itemId.equalsIgnoreCase(item.getItemId())) {
                target = item;
                break;
            }
        }
        if (target != null) {
            UIUtils.showLostAndFoundDetailsDialog(this, target, complaintDAO, this::loadLostAndFoundData);
        } else {
            JOptionPane.showMessageDialog(this, "Notice details for #" + itemId + " could not be found.", "Notice", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // Helper: Shared Preview Card Component
    private JPanel createPreviewCard(JLabel lblTitle, JLabel lblMeta, JTextArea taDesc, JButton btnModal, JButton btnSec) {
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

    private void showReportItemDialog() {
        JDialog dlg = new JDialog(this, "Report Lost or Found Item Notice", true);
        dlg.setSize(480, 420);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout(15, 15));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(6, 4, 6, 4);

        g.gridx = 0; g.gridy = 0; g.weightx = 0.3;
        form.add(new JLabel("Item Name:"), g);
        g.gridx = 1; g.weightx = 0.7;
        JTextField tfName = new JTextField(20);
        form.add(tfName, g);

        g.gridx = 0; g.gridy = 1;
        form.add(new JLabel("Notice Type:"), g);
        g.gridx = 1;
        JComboBox<String> cbType = new JComboBox<>(new String[]{"Lost", "Found"});
        form.add(cbType, g);

        g.gridx = 0; g.gridy = 2;
        form.add(new JLabel("Location:"), g);
        g.gridx = 1;
        JTextField tfLoc = new JTextField(20);
        form.add(tfLoc, g);

        g.gridx = 0; g.gridy = 3;
        g.anchor = GridBagConstraints.NORTHWEST;
        form.add(new JLabel("Description:"), g);
        g.gridx = 1;
        JTextArea taDesc = new JTextArea(4, 20);
        taDesc.setLineWrap(true);
        taDesc.setWrapStyleWord(true);
        form.add(new JScrollPane(taDesc), g);

        dlg.add(form, BorderLayout.CENTER);

        JPanel pBot = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton btnSubmit = UIUtils.createButton("Post Notice", new Color(37, 99, 235));
        JButton btnCancel = UIUtils.createSecondaryButton("Cancel");

        btnSubmit.addActionListener(e -> {
            String name = tfName.getText().trim();
            String typ = (String) cbType.getSelectedItem();
            String loc = tfLoc.getText().trim();
            String desc = taDesc.getText().trim();

            if (name.isEmpty() || loc.isEmpty()) {
                JOptionPane.showMessageDialog(dlg, "Please provide an item name and location.", "Input Required", JOptionPane.WARNING_MESSAGE);
                return;
            }

            LostAndFoundItem item = new LostAndFoundItem();
            item.setItemName(name);
            item.setItemType(typ);
            item.setLocationFound(loc);
            item.setDescription(desc);
            item.setReportedByPrn(currentStudent.getPrn());

            boolean ok = complaintDAO.reportLostAndFound(item);
            if (ok) {
                JOptionPane.showMessageDialog(dlg, "Notice posted successfully as #" + item.getItemId() + "!", "Posted", JOptionPane.INFORMATION_MESSAGE);
                dlg.dispose();
                loadLostAndFoundData();
            } else {
                JOptionPane.showMessageDialog(dlg, "Failed to post notice.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnCancel.addActionListener(e -> dlg.dispose());

        pBot.add(btnSubmit);
        pBot.add(btnCancel);
        dlg.add(pBot, BorderLayout.SOUTH);

        dlg.setVisible(true);
    }

    private void loadLostAndFoundData() {
        if (lfModel == null) return;
        lfModel.setRowCount(0);
        String kw = (tfLfSearch != null) ? tfLfSearch.getText() : "";
        String typ = (cbLfType != null) ? (String) cbLfType.getSelectedItem() : "All";
        String stat = (cbLfStatus != null) ? (String) cbLfStatus.getSelectedItem() : "All";

        List<LostAndFoundItem> list = complaintDAO.searchLostAndFound(kw, typ, stat);
        for (LostAndFoundItem item : list) {
            lfModel.addRow(new Object[]{
                    item.getItemId(),
                    item.getItemName(),
                    item.getItemType(),
                    item.getLocationFound(),
                    item.getStatus(),
                    item.getReportedByPrn(),
                    item.getReportedAt() != null ? item.getReportedAt() : "Recently"
            });
        }
        if (lblLfCount != null) {
            lblLfCount.setText("(" + list.size() + " matches)");
        }
    }
}
