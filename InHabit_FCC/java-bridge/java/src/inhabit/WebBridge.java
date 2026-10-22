package inhabit;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.Executors;

/**
 * Minimal HTTP adapter for the FCC Jugaad version.
 *
 * IMPORTANT: the existing Java models, DAOs, JDBC/MySQL schema and escalation
 * thread remain unchanged. This class only exposes a small browser-facing API
 * so the new React UI can call the same Java application logic.
 */
public class WebBridge {
    private static final int PORT = 8090;
    private static final ComplaintDAO complaintDAO = new ComplaintDAO();
    private static final UserDAO userDAO = new UserDAO();

    public static void main(String[] args) throws Exception {
        // Keep the original background escalation thread running.
        EscalationThread escalationThread = new EscalationThread();
        escalationThread.start();

        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/api/health", WebBridge::health);
        server.createContext("/api/login", WebBridge::login);
        server.createContext("/api/complaints", WebBridge::complaints);
        server.createContext("/api/vote", WebBridge::vote);
        server.createContext("/api/status", WebBridge::status);
        server.createContext("/api/assign", WebBridge::assign);
        server.createContext("/api/lostfound", WebBridge::lostFound);
        server.createContext("/api/claim", WebBridge::claim);
        server.setExecutor(Executors.newFixedThreadPool(8));
        System.out.println("[WebBridge] InHabit web adapter running at http://localhost:" + PORT);
        server.start();
    }

    private static void health(HttpExchange ex) throws IOException {
        if (!allow(ex)) return;
        json(ex, 200, "{\"ok\":true,\"service\":\"InHabit Java Bridge\",\"databaseFallback\":" + DBConnection.isFallbackMode() + "}");
    }

    private static void login(HttpExchange ex) throws IOException {
        if (!allow(ex)) return;
        if (!"POST".equalsIgnoreCase(ex.getRequestMethod())) { json(ex, 405, err("POST required")); return; }
        Map<String,String> p = form(ex);
        try {
            User u = userDAO.authenticate(p.get("identity"), p.get("password"));
            String role = u.getRole() == null ? "Student" : u.getRole();
            String id = (u instanceof Staff) ? ((Staff)u).getStaffId() : u.getPrn();
            StringBuilder b = new StringBuilder("{\"success\":true,");
            b.append("\"id\":").append(q(id)).append(",");
            b.append("\"name\":").append(q(u.getFullName())).append(",");
            b.append("\"email\":").append(q(u.getEmail())).append(",");
            b.append("\"role\":").append(q(role)).append(",");
            b.append("\"prn\":").append(q(u.getPrn())).append(",");
            b.append("\"location\":").append(q(u.getLocation())).append("}");
            json(ex, 200, b.toString());
        } catch (Exception e) {
            json(ex, 401, err(e.getMessage() == null ? "Authentication failed" : e.getMessage()));
        }
    }

    private static void complaints(HttpExchange ex) throws IOException {
        if (!allow(ex)) return;
        if (!"GET".equalsIgnoreCase(ex.getRequestMethod())) { json(ex, 405, err("GET required")); return; }
        Map<String,String> q = query(ex.getRequestURI().getRawQuery());
        String scope = q.getOrDefault("scope", "all");
        List<Complaint> list;
        if ("student".equalsIgnoreCase(scope) && q.get("prn") != null) {
            list = complaintDAO.getComplaintsByStudent(q.get("prn"));
        } else if ("staff".equalsIgnoreCase(scope) && q.get("staffId") != null) {
            list = complaintDAO.getComplaintsByAssignedStaff(q.get("staffId"));
        } else if (q.get("keyword") != null || q.get("category") != null || q.get("status") != null || q.get("wing") != null) {
            list = complaintDAO.searchComplaints(q.get("keyword"), q.get("category"), q.get("status"), q.get("wing"));
        } else {
            list = complaintDAO.getAllComplaints();
        }
        StringBuilder out = new StringBuilder("[");
        for (int i=0;i<list.size();i++) { if (i>0) out.append(','); out.append(complaintJson(list.get(i))); }
        out.append(']');
        json(ex, 200, out.toString());
    }

    private static void vote(HttpExchange ex) throws IOException {
        if (!allow(ex)) return;
        if (!"POST".equalsIgnoreCase(ex.getRequestMethod())) { json(ex,405,err("POST required")); return; }
        Map<String,String> p = form(ex);
        try {
            boolean ok = complaintDAO.voteComplaint(Integer.parseInt(p.get("ticketId")), p.get("prn"), p.getOrDefault("voteType", "Genuine"));
            json(ex, ok ? 200 : 400, "{\"success\":" + ok + "}");
        } catch (Exception e) { json(ex, 400, err(e.getMessage())); }
    }

    private static void status(HttpExchange ex) throws IOException {
        if (!allow(ex)) return;
        if (!"POST".equalsIgnoreCase(ex.getRequestMethod())) { json(ex,405,err("POST required")); return; }
        Map<String,String> p = form(ex);
        boolean ok = complaintDAO.updateStatus(Integer.parseInt(p.get("ticketId")), p.get("status"));
        json(ex, ok ? 200 : 400, "{\"success\":" + ok + "}");
    }

    private static void assign(HttpExchange ex) throws IOException {
        if (!allow(ex)) return;
        if (!"POST".equalsIgnoreCase(ex.getRequestMethod())) { json(ex,405,err("POST required")); return; }
        Map<String,String> p = form(ex);
        boolean ok = complaintDAO.assignStaff(Integer.parseInt(p.get("ticketId")), p.get("staffId"));
        json(ex, ok ? 200 : 400, "{\"success\":" + ok + "}");
    }

    private static void lostFound(HttpExchange ex) throws IOException {
        if (!allow(ex)) return;
        if ("GET".equalsIgnoreCase(ex.getRequestMethod())) {
            Map<String,String> q = query(ex.getRequestURI().getRawQuery());
            List<LostAndFoundItem> list = complaintDAO.searchLostAndFound(q.get("keyword"), q.get("itemType"), q.get("status"));
            StringBuilder out = new StringBuilder("[");
            for (int i=0;i<list.size();i++) { if(i>0) out.append(','); out.append(lostJson(list.get(i))); }
            out.append(']'); json(ex,200,out.toString()); return;
        }
        if ("POST".equalsIgnoreCase(ex.getRequestMethod())) {
            Map<String,String> p = form(ex);
            LostAndFoundItem item = new LostAndFoundItem(
                    "WEB-" + System.currentTimeMillis(), p.get("itemName"), p.get("description"),
                    p.get("location"), p.getOrDefault("itemType","Found"), "Open", p.get("prn"), "Just now");
            boolean ok = complaintDAO.reportLostAndFound(item);
            json(ex, ok ? 200 : 400, "{\"success\":" + ok + ",\"itemId\":" + q(item.getItemId()) + "}"); return;
        }
        json(ex,405,err("GET or POST required"));
    }

    private static void claim(HttpExchange ex) throws IOException {
        if (!allow(ex)) return;
        if (!"POST".equalsIgnoreCase(ex.getRequestMethod())) { json(ex,405,err("POST required")); return; }
        Map<String,String> p = form(ex);
        boolean ok = complaintDAO.claimLostAndFound(p.get("itemId"));
        json(ex, ok ? 200 : 400, "{\"success\":" + ok + "}");
    }

    private static String complaintJson(Complaint c) {
        return "{" +
                "\"ticketId\":" + c.getTicketId() + "," +
                "\"title\":" + q(c.getTitle()) + "," +
                "\"description\":" + q(c.getDescription()) + "," +
                "\"category\":" + q(c.getCategory()) + "," +
                "\"location\":" + q(c.getLocation()) + "," +
                "\"status\":" + q(c.getStatus()) + "," +
                "\"concerning\":" + c.isConcerning() + "," +
                "\"createdByPrn\":" + q(c.getCreatedByPrn()) + "," +
                "\"assignedStaffId\":" + q(c.getAssignedStaffId()) + "," +
                "\"createdAt\":" + q(c.getCreatedAt()) + "," +
                "\"voteCount\":" + c.getVoteCount() + "," +
                "\"wardenConfirmed\":" + c.isWardenConfirmed() + "," +
                "\"studentConfirmed\":" + c.isStudentConfirmed() + "}";
    }

    private static String lostJson(LostAndFoundItem i) {
        return "{" +
                "\"itemId\":" + q(i.getItemId()) + "," +
                "\"itemName\":" + q(i.getItemName()) + "," +
                "\"description\":" + q(i.getDescription()) + "," +
                "\"location\":" + q(i.getLocationFound()) + "," +
                "\"itemType\":" + q(i.getItemType()) + "," +
                "\"status\":" + q(i.getStatus()) + "," +
                "\"reportedByPrn\":" + q(i.getReportedByPrn()) + "," +
                "\"reportedAt\":" + q(i.getReportedAt()) + "}";
    }

    private static boolean allow(HttpExchange ex) throws IOException {
        Headers h = ex.getResponseHeaders();
        h.set("Access-Control-Allow-Origin", "*");
        h.set("Access-Control-Allow-Headers", "Content-Type");
        h.set("Access-Control-Allow-Methods", "GET,POST,OPTIONS");
        if ("OPTIONS".equalsIgnoreCase(ex.getRequestMethod())) { ex.sendResponseHeaders(204,-1); return false; }
        return true;
    }

    private static Map<String,String> form(HttpExchange ex) throws IOException { return query(readBody(ex)); }

    private static Map<String,String> query(String raw) {
        Map<String,String> out = new LinkedHashMap<>();
        if (raw == null || raw.isEmpty()) return out;
        for (String part : raw.split("&")) {
            int i = part.indexOf('=');
            String k = i >= 0 ? part.substring(0,i) : part;
            String v = i >= 0 ? part.substring(i+1) : "";
            try { out.put(URLDecoder.decode(k,"UTF-8"), URLDecoder.decode(v,"UTF-8")); }
            catch (Exception ignored) {}
        }
        return out;
    }

    private static String readBody(HttpExchange ex) throws IOException {
        try (InputStream in = ex.getRequestBody(); ByteArrayOutputStream b = new ByteArrayOutputStream()) {
            byte[] buf = new byte[4096]; int n; while((n=in.read(buf))!=-1) b.write(buf,0,n);
            return b.toString(StandardCharsets.UTF_8);
        }
    }

    private static void json(HttpExchange ex, int status, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        ex.sendResponseHeaders(status, bytes.length);
        try (OutputStream out = ex.getResponseBody()) { out.write(bytes); }
    }

    private static String err(String message) { return "{\"success\":false,\"error\":" + q(message) + "}"; }

    private static String q(String s) {
        if (s == null) return "null";
        StringBuilder b = new StringBuilder("\"");
        for (char c : s.toCharArray()) {
            switch(c) {
                case '\\': b.append("\\\\"); break;
                case '"': b.append("\\\""); break;
                case '\n': b.append("\\n"); break;
                case '\r': b.append("\\r"); break;
                case '\t': b.append("\\t"); break;
                default: if(c < 32) b.append(String.format("\\u%04x",(int)c)); else b.append(c);
            }
        }
        return b.append('"').toString();
    }
}
