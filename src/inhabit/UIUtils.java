package inhabit;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

// UI utilities and cross-platform styled components
public class UIUtils {

    // Primary solid colored button with guaranteed visible high-contrast text across all OS
    public static JButton createButton(String text, Color bgColor) {
        JButton b = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                if (getModel().isArmed()) {
                    g2.setColor(bgColor.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(bgColor.brighter());
                } else {
                    g2.setColor(bgColor);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);

                // Draw centered white text
                g2.setColor(Color.WHITE);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        b.setFont(new Font("SansSerif", Font.BOLD, 13));
        b.setContentAreaFilled(false);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(130, 34));
        return b;
    }

    // Secondary subtle outline button
    public static JButton createSecondaryButton(String text) {
        JButton b = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                if (getModel().isRollover()) {
                    g2.setColor(new Color(241, 245, 249));
                } else {
                    g2.setColor(Color.WHITE);
                }
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);

                g2.setColor(new Color(203, 213, 225));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);

                g2.setColor(new Color(30, 41, 59));
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        b.setFont(new Font("SansSerif", Font.PLAIN, 13));
        b.setContentAreaFilled(false);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(130, 34));
        return b;
    }

    // Show full details dialog for a complaint
    public static void showComplaintDetailsDialog(Component parent, Complaint c, User currentUser,
                                                  ComplaintDAO complaintDAO, Runnable onRefresh) {
        if (c == null) {
            JOptionPane.showMessageDialog(parent, "Please select a complaint to view details.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Window owner = (parent instanceof Window) ? (Window) parent : SwingUtilities.getWindowAncestor(parent);
        JDialog dialog;
        if (owner instanceof Frame) {
            dialog = new JDialog((Frame) owner, "Grievance Ticket #" + c.getTicketId(), true);
        } else if (owner instanceof Dialog) {
            dialog = new JDialog((Dialog) owner, "Grievance Ticket #" + c.getTicketId(), true);
        } else {
            dialog = new JDialog((Frame) null, "Grievance Ticket #" + c.getTicketId(), true);
        }
        dialog.setLayout(new BorderLayout(15, 15));
        dialog.setSize(580, 540);
        dialog.setLocationRelativeTo(owner != null ? owner : parent);

        // Header
        JPanel pHead = new JPanel(new BorderLayout());
        pHead.setBackground(new Color(30, 41, 59));
        pHead.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel lblTitle = new JLabel("Ticket #" + c.getTicketId() + " - " + c.getTitle());
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblTitle.setForeground(Color.WHITE);

        String priority = c.isConcerning() ? " [CONCERNING]" : "";
        JLabel lblStatus = new JLabel("Status: " + c.getStatus() + priority);
        lblStatus.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblStatus.setForeground(c.isConcerning() ? new Color(248, 113, 113) : new Color(52, 211, 153));

        pHead.add(lblTitle, BorderLayout.NORTH);
        pHead.add(lblStatus, BorderLayout.SOUTH);
        dialog.add(pHead, BorderLayout.NORTH);

        // Center Details
        JPanel pCenter = new JPanel(new BorderLayout(10, 10));
        pCenter.setBorder(BorderFactory.createEmptyBorder(15, 20, 10, 20));

        JPanel pGrid = new JPanel(new GridLayout(0, 2, 10, 8));
        pGrid.add(new JLabel("Category:"));
        JLabel lCat = new JLabel(c.getCategory());
        lCat.setFont(new Font("SansSerif", Font.BOLD, 13));
        pGrid.add(lCat);

        pGrid.add(new JLabel("Location:"));
        JLabel lLoc = new JLabel(c.getLocation());
        lLoc.setFont(new Font("SansSerif", Font.BOLD, 13));
        pGrid.add(lLoc);

        pGrid.add(new JLabel("Reported by PRN:"));
        JLabel lPrn = new JLabel(c.getCreatedByPrn());
        lPrn.setFont(new Font("SansSerif", Font.PLAIN, 13));
        pGrid.add(lPrn);

        pGrid.add(new JLabel("Date Filed:"));
        JLabel lDate = new JLabel(c.getCreatedAt() != null ? c.getCreatedAt() : "Recently");
        lDate.setFont(new Font("SansSerif", Font.PLAIN, 13));
        pGrid.add(lDate);

        pGrid.add(new JLabel("Assigned Technician:"));
        JLabel lStaff = new JLabel(c.getAssignedStaffId() != null ? c.getAssignedStaffId() : "Unassigned");
        lStaff.setFont(new Font("SansSerif", Font.PLAIN, 13));
        pGrid.add(lStaff);

        pGrid.add(new JLabel("Community Genuine Votes:"));
        JLabel lVotes = new JLabel(String.valueOf(c.getVoteCount()));
        lVotes.setFont(new Font("SansSerif", Font.BOLD, 14));
        lVotes.setForeground(new Color(37, 99, 235));
        pGrid.add(lVotes);

        pCenter.add(pGrid, BorderLayout.NORTH);

        // Description area
        JPanel pDesc = new JPanel(new BorderLayout(5, 5));
        JLabel lDescTitle = new JLabel("Full Grievance Description:");
        lDescTitle.setFont(new Font("SansSerif", Font.BOLD, 13));
        pDesc.add(lDescTitle, BorderLayout.NORTH);

        JTextArea taDesc = new JTextArea(c.getDescription(), 6, 30);
        taDesc.setLineWrap(true);
        taDesc.setWrapStyleWord(true);
        taDesc.setEditable(false);
        taDesc.setFont(new Font("SansSerif", Font.PLAIN, 13));
        taDesc.setBackground(new Color(248, 250, 252));
        taDesc.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JScrollPane sp = new JScrollPane(taDesc);
        pDesc.add(sp, BorderLayout.CENTER);
        pCenter.add(pDesc, BorderLayout.CENTER);

        dialog.add(pCenter, BorderLayout.CENTER);

        // Bottom Actions
        JPanel pBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        pBottom.setBorder(BorderFactory.createEmptyBorder(5, 15, 10, 15));

        // If admin/staff: can update status
        if (currentUser instanceof Staff) {
            JLabel lblChange = new JLabel("Change Status:");
            String[] statuses = {"Open", "Under Review", "In Progress", "Resolved", "Closed"};
            JComboBox<String> cbStatus = new JComboBox<>(statuses);
            cbStatus.setSelectedItem(c.getStatus());

            JButton btnSaveStatus = createButton("Update", new Color(37, 99, 235));
            btnSaveStatus.setPreferredSize(new Dimension(85, 30));
            btnSaveStatus.addActionListener(e -> {
                String newSt = (String) cbStatus.getSelectedItem();
                complaintDAO.updateStatus(c.getTicketId(), newSt);
                c.setStatus(newSt);
                lblStatus.setText("Status: " + newSt + priority);
                JOptionPane.showMessageDialog(dialog, "Ticket #" + c.getTicketId() + " status updated to " + newSt + "!", "Updated", JOptionPane.INFORMATION_MESSAGE);
                if (onRefresh != null) onRefresh.run();
            });

            pBottom.add(lblChange);
            pBottom.add(cbStatus);
            pBottom.add(btnSaveStatus);
        } else if (currentUser instanceof Student && !currentUser.getPrn().equals(c.getCreatedByPrn())) {
            // Student viewing peer ticket: allow upvoting
            JButton btnUpvote = createButton("Upvote Genuine", new Color(16, 185, 129));
            btnUpvote.setPreferredSize(new Dimension(140, 32));
            btnUpvote.addActionListener(e -> {
                try {
                    complaintDAO.voteComplaint(c.getTicketId(), currentUser.getPrn(), "Genuine");
                    c.setVoteCount(c.getVoteCount() + 1);
                    lVotes.setText(String.valueOf(c.getVoteCount()));
                    JOptionPane.showMessageDialog(dialog, "Your Genuine vote has been recorded!", "Vote Recorded", JOptionPane.INFORMATION_MESSAGE);
                    if (onRefresh != null) onRefresh.run();
                } catch (InvalidVoteException ex) {
                    JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Vote Notice", JOptionPane.WARNING_MESSAGE);
                }
            });
            pBottom.add(btnUpvote);
        }

        JButton btnClose = createSecondaryButton("Close");
        btnClose.setPreferredSize(new Dimension(90, 32));
        btnClose.addActionListener(e -> dialog.dispose());
        pBottom.add(btnClose);

        dialog.add(pBottom, BorderLayout.SOUTH);
        dialog.toFront();
        dialog.requestFocus();
        dialog.setVisible(true);
    }

    // Show full details dialog for a Lost and Found Notice
    public static void showLostAndFoundDetailsDialog(Component parent, LostAndFoundItem item,
                                                     ComplaintDAO complaintDAO, Runnable onRefresh) {
        if (item == null) {
            JOptionPane.showMessageDialog(parent, "Please select an item notice to view details.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Window owner = (parent instanceof Window) ? (Window) parent : SwingUtilities.getWindowAncestor(parent);
        JDialog dialog;
        if (owner instanceof Frame) {
            dialog = new JDialog((Frame) owner, "Notice #" + item.getItemId() + " - " + item.getItemName(), true);
        } else if (owner instanceof Dialog) {
            dialog = new JDialog((Dialog) owner, "Notice #" + item.getItemId() + " - " + item.getItemName(), true);
        } else {
            dialog = new JDialog((Frame) null, "Notice #" + item.getItemId() + " - " + item.getItemName(), true);
        }

        dialog.setLayout(new BorderLayout(15, 15));
        dialog.setSize(560, 480);
        dialog.setLocationRelativeTo(owner != null ? owner : parent);

        JPanel pHead = new JPanel(new BorderLayout());
        pHead.setBackground(new Color(30, 41, 59));
        pHead.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel lblTitle = new JLabel("Notice #" + item.getItemId() + " - " + item.getItemName());
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblTitle.setForeground(Color.WHITE);

        boolean isLost = "Lost".equalsIgnoreCase(item.getItemType());
        JLabel lblType = new JLabel("Notice Type: " + item.getItemType() + " | Status: " + item.getStatus());
        lblType.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblType.setForeground(isLost ? new Color(248, 113, 113) : new Color(52, 211, 153));

        pHead.add(lblTitle, BorderLayout.NORTH);
        pHead.add(lblType, BorderLayout.SOUTH);
        dialog.add(pHead, BorderLayout.NORTH);

        JPanel pCenter = new JPanel(new BorderLayout(10, 10));
        pCenter.setBorder(BorderFactory.createEmptyBorder(15, 20, 10, 20));

        JPanel pGrid = new JPanel(new GridLayout(0, 2, 10, 8));
        pGrid.add(new JLabel("Item Name:"));
        JLabel lName = new JLabel(item.getItemName());
        lName.setFont(new Font("SansSerif", Font.BOLD, 13));
        pGrid.add(lName);

        pGrid.add(new JLabel("Campus Location:"));
        pGrid.add(new JLabel(item.getLocationFound()));

        pGrid.add(new JLabel("Notice Type:"));
        JLabel lT = new JLabel(item.getItemType());
        lT.setFont(new Font("SansSerif", Font.BOLD, 13));
        lT.setForeground(isLost ? new Color(225, 29, 72) : new Color(13, 148, 136));
        pGrid.add(lT);

        pGrid.add(new JLabel("Notice Status:"));
        JLabel lS = new JLabel(item.getStatus());
        lS.setFont(new Font("SansSerif", Font.BOLD, 13));
        lS.setForeground("Claimed".equalsIgnoreCase(item.getStatus()) ? new Color(16, 185, 129) : new Color(245, 158, 11));
        pGrid.add(lS);

        pGrid.add(new JLabel("Reported by PRN:"));
        pGrid.add(new JLabel(item.getReportedByPrn()));

        pGrid.add(new JLabel("Date & Time Logged:"));
        pGrid.add(new JLabel(item.getReportedAt() != null ? item.getReportedAt() : "Recently"));

        pCenter.add(pGrid, BorderLayout.NORTH);

        JPanel pDesc = new JPanel(new BorderLayout(5, 5));
        JLabel lDescTitle = new JLabel("Full Notice Description:");
        lDescTitle.setFont(new Font("SansSerif", Font.BOLD, 13));
        pDesc.add(lDescTitle, BorderLayout.NORTH);

        JTextArea ta = new JTextArea(item.getDescription(), 5, 30);
        ta.setLineWrap(true);
        ta.setWrapStyleWord(true);
        ta.setEditable(false);
        ta.setFont(new Font("SansSerif", Font.PLAIN, 13));
        ta.setBackground(new Color(248, 250, 252));
        ta.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        pDesc.add(new JScrollPane(ta), BorderLayout.CENTER);
        pCenter.add(pDesc, BorderLayout.CENTER);

        dialog.add(pCenter, BorderLayout.CENTER);

        JPanel pBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        pBottom.setBorder(BorderFactory.createEmptyBorder(5, 15, 10, 15));

        if (!"Claimed".equalsIgnoreCase(item.getStatus()) && complaintDAO != null) {
            JButton btnClaim = createButton("Claim Notice", new Color(16, 185, 129));
            btnClaim.setPreferredSize(new Dimension(130, 32));
            btnClaim.addActionListener(e -> {
                int confirm = JOptionPane.showConfirmDialog(dialog, "Mark this item as Claimed by its rightful owner?", "Confirm Claim", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    complaintDAO.claimLostAndFound(item.getItemId());
                    item.setStatus("Claimed");
                    lS.setText("Claimed");
                    lS.setForeground(new Color(16, 185, 129));
                    lblType.setText("Notice Type: " + item.getItemType() + " | Status: Claimed");
                    JOptionPane.showMessageDialog(dialog, "Item " + item.getItemId() + " marked as Claimed!", "Claim Registered", JOptionPane.INFORMATION_MESSAGE);
                    if (onRefresh != null) onRefresh.run();
                }
            });
            pBottom.add(btnClaim);
        }

        JButton btnClose = createSecondaryButton("Close");
        btnClose.setPreferredSize(new Dimension(90, 32));
        btnClose.addActionListener(e -> dialog.dispose());
        pBottom.add(btnClose);

        dialog.add(pBottom, BorderLayout.SOUTH);
        dialog.toFront();
        dialog.requestFocus();
        dialog.setVisible(true);
    }
}
