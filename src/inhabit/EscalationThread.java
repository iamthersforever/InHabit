package inhabit;

// Multithreading background daemon thread for auto-escalating high-priority complaints
public class EscalationThread extends Thread {
    private ComplaintDAO complaintDAO;
    private volatile boolean running = true;

    public EscalationThread() {
        this.complaintDAO = new ComplaintDAO();
        setDaemon(true); // Allows JVM to exit cleanly
        setName("Escalation-Daemon-Thread");
    }

    @Override
    public void run() {
        System.out.println("[EscalationThread] Background daemon thread started.");
        while (running) {
            try {
                // Step 1: Sleep for 15 seconds between periodic checks
                Thread.sleep(15000);

                // Step 2: Check for grievances exceeding 30 genuine votes
                int escalated = complaintDAO.checkAndEscalateConcerning();
                if (escalated > 0) {
                    System.out.println("[EscalationThread] Auto-escalated " + escalated + " grievance(s) to CONCERNING status.");
                }
            } catch (InterruptedException e) {
                System.out.println("[EscalationThread] Daemon thread interrupted.");
                break;
            } catch (Exception e) {
                System.out.println("[EscalationThread] Error in periodic check: " + e.getMessage());
            }
        }
    }

    public void stopThread() {
        running = false;
        interrupt();
    }
}
