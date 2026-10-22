package inhabit;

import javax.swing.*;

// Main entry point for InHabit application
public class Main {
    public static void main(String[] args) {
        // Step 1: Set modern system Look & Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Keep default Swing look and feel
        }

        // Step 2: Start background multithreading escalation monitor daemon
        EscalationThread escalationThread = new EscalationThread();
        escalationThread.start();

        // Step 3: Launch Swing Login GUI
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new LoginFrame();
            }
        });
    }
}
