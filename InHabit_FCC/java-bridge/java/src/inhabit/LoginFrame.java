package inhabit;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

// Swing GUI Login Screen
public class LoginFrame extends JFrame {
    JLabel lblTitle, lblSub, l1, l2, lblHint;
    JTextField t1;
    JPasswordField t2;
    JButton bLogin;
    JButton btnFillRaghav, btnFillAniruddha, btnFillAdmin;
    JPanel p1, pCenter, pBottom;

    private UserDAO userDAO;

    public LoginFrame() {
        userDAO = new UserDAO();

        setTitle("InHabit - Hostel Grievance Management System");
        setSize(480, 420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(245, 247, 250));

        // Header Panel
        p1 = new JPanel();
        p1.setLayout(new BoxLayout(p1, BoxLayout.Y_AXIS));
        p1.setBackground(new Color(30, 41, 59));
        p1.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        lblTitle = new JLabel("InHabit Portal");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblSub = new JLabel("Hostel Resident Grievance & Facility Redressal");
        lblSub.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblSub.setForeground(new Color(203, 213, 225));
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);

        p1.add(lblTitle);
        p1.add(Box.createVerticalStrut(5));
        p1.add(lblSub);
        add(p1, BorderLayout.NORTH);

        // Center Form
        pCenter = new JPanel(new GridBagLayout());
        pCenter.setBackground(new Color(245, 247, 250));
        pCenter.setBorder(BorderFactory.createEmptyBorder(15, 30, 10, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 5, 8, 5);

        // Row 0: Email / PRN
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        l1 = new JLabel("Email / PRN:");
        l1.setFont(new Font("SansSerif", Font.BOLD, 13));
        pCenter.add(l1, gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        t1 = new JTextField("raghav.sarda.btech25@college.edu.in", 22);
        t1.setFont(new Font("SansSerif", Font.PLAIN, 13));
        pCenter.add(t1, gbc);

        // Row 1: Password
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.3;
        l2 = new JLabel("Password:");
        l2.setFont(new Font("SansSerif", Font.BOLD, 13));
        pCenter.add(l2, gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        t2 = new JPasswordField("student123", 22);
        t2.setFont(new Font("SansSerif", Font.PLAIN, 13));
        pCenter.add(t2, gbc);

        // Row 2: Login button (Styled with guaranteed visible white text)
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        bLogin = UIUtils.createButton("Login to InHabit", new Color(37, 99, 235));
        bLogin.setPreferredSize(new Dimension(200, 38));
        pCenter.add(bLogin, gbc);

        // Row 3: Quick Demo Fill Buttons
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        JPanel pQuick = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 5));
        pQuick.setOpaque(false);

        btnFillRaghav = UIUtils.createSecondaryButton("Raghav (101)");
        btnFillRaghav.setPreferredSize(new Dimension(110, 28));
        btnFillRaghav.setFont(new Font("SansSerif", Font.PLAIN, 11));

        btnFillAniruddha = UIUtils.createSecondaryButton("Aniruddha (102)");
        btnFillAniruddha.setPreferredSize(new Dimension(120, 28));
        btnFillAniruddha.setFont(new Font("SansSerif", Font.PLAIN, 11));

        btnFillAdmin = UIUtils.createSecondaryButton("Admin Portal");
        btnFillAdmin.setPreferredSize(new Dimension(100, 28));
        btnFillAdmin.setFont(new Font("SansSerif", Font.PLAIN, 11));

        JButton btnFillTech = UIUtils.createSecondaryButton("Tech (Electrician)");
        btnFillTech.setPreferredSize(new Dimension(125, 28));
        btnFillTech.setFont(new Font("SansSerif", Font.PLAIN, 11));

        pQuick.add(btnFillRaghav);
        pQuick.add(btnFillAniruddha);
        pQuick.add(btnFillAdmin);
        pQuick.add(btnFillTech);
        pCenter.add(pQuick, gbc);

        add(pCenter, BorderLayout.CENTER);

        // Bottom panel
        pBottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pBottom.setBackground(new Color(245, 247, 250));
        pBottom.setBorder(BorderFactory.createEmptyBorder(0, 10, 15, 10));

        lblHint = new JLabel("Quick login: Click student/admin/tech buttons above or enter your college credentials.");
        lblHint.setFont(new Font("SansSerif", Font.ITALIC, 11));
        lblHint.setForeground(new Color(100, 116, 139));
        pBottom.add(lblHint);
        add(pBottom, BorderLayout.SOUTH);

        // Action Listeners
        bLogin.addActionListener(e -> performLogin());
        t2.addActionListener(e -> performLogin());

        btnFillRaghav.addActionListener(e -> {
            t1.setText("raghav.sarda.btech25@college.edu.in");
            t2.setText("student123");
        });

        btnFillAniruddha.addActionListener(e -> {
            t1.setText("aniruddha.singh.btech25@college.edu.in");
            t2.setText("student123");
        });

        btnFillAdmin.addActionListener(e -> {
            t1.setText("admin@college.edu.in");
            t2.setText("admin123");
        });

        btnFillTech.addActionListener(e -> {
            t1.setText("electrician1@college.edu.in");
            t2.setText("Staff@106");
        });

        setVisible(true);
    }

    private void performLogin() {
        String emailOrPrn = t1.getText();
        String password = new String(t2.getPassword());

        try {
            User u = userDAO.authenticate(emailOrPrn, password);
            if (u instanceof Student) {
                dispose();
                new StudentDashboard((Student) u);
            } else if (u instanceof Staff) {
                Staff st = (Staff) u;
                dispose();
                if (st.getRole() != null && (st.getRole().contains("Admin") || st.getRole().contains("Chief Warden"))) {
                    new AdminDashboard(st);
                } else {
                    new TechnicianDashboard(st);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Unknown account type.", "Login Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (AuthenticationException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Login Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "An error occurred: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame());
    }
}
