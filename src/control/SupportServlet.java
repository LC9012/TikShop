package control;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.*;
import java.sql.*;
import java.util.*;
import org.json.JSONArray;
import org.json.JSONObject;

@WebServlet("/SupportServlet")

public class SupportServlet extends HttpServlet {
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        Integer userId = (session != null) ? (Integer)session.getAttribute("userId") : null;
        if (userId == null) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        String action = req.getParameter("action");
        resp.setContentType("application/json");
        JSONObject response = new JSONObject();

        try (Connection conn = util.DBUtil.getConnection()) {
            if ("listChats".equals(action)) {
                // Restituisce lista chat dell’utente
                PreparedStatement ps = conn.prepareStatement("SELECT * FROM support_chats WHERE user_id=? ORDER BY created_at DESC");
                ps.setInt(1, userId);
                ResultSet rs = ps.executeQuery();
                JSONArray chats = new JSONArray();
                while (rs.next()) {
                    JSONObject chat = new JSONObject();
                    chat.put("id", rs.getInt("id"));
                    chat.put("created_at", rs.getTimestamp("created_at").toString());
                    chats.put(chat);
                }
                response.put("chats", chats);
            } else if ("getMessages".equals(action)) {
                int chatId = Integer.parseInt(req.getParameter("chatId"));
                PreparedStatement ps = conn.prepareStatement("SELECT * FROM support_messages WHERE chat_id=? ORDER BY sent_at ASC");
                ps.setInt(1, chatId);
                ResultSet rs = ps.executeQuery();
                JSONArray messages = new JSONArray();
                while (rs.next()) {
                    JSONObject msg = new JSONObject();
                    msg.put("sender", rs.getString("sender"));
                    msg.put("message", rs.getString("message"));
                    msg.put("sent_at", rs.getTimestamp("sent_at").toString());
                    messages.put(msg);
                }
                response.put("messages", messages);
            }
        } catch(Exception e) { e.printStackTrace(); }
        resp.getWriter().write(response.toString());
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        Integer userId = (session != null) ? (Integer)session.getAttribute("userId") : null;
        if (userId == null) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        String action = req.getParameter("action");
        resp.setContentType("application/json");
        JSONObject response = new JSONObject();

        try (Connection conn = util.DBUtil.getConnection()) {
            if ("newChat".equals(action)) {
                // Crea una nuova chat e aggiunge il primo messaggio
                PreparedStatement chatPs = conn.prepareStatement("INSERT INTO support_chats (user_id) VALUES (?)", Statement.RETURN_GENERATED_KEYS);
                chatPs.setInt(1, userId);
                chatPs.executeUpdate();
                ResultSet keys = chatPs.getGeneratedKeys();
                keys.next();
                int chatId = keys.getInt(1);

                // Messaggio di sistema di base
                PreparedStatement msgPs = conn.prepareStatement("INSERT INTO support_messages (chat_id, sender, message) VALUES (?, ?, ?)");
                msgPs.setInt(1, chatId);
                msgPs.setString(2, "support");
                msgPs.setString(3, "Come posso aiutarti?");
                msgPs.executeUpdate();

                response.put("chatId", chatId);
            } else if ("sendMessage".equals(action)) {
                int chatId = Integer.parseInt(req.getParameter("chatId"));
                String msg = req.getParameter("message");
                // Salva messaggio utente
                PreparedStatement msgPs = conn.prepareStatement("INSERT INTO support_messages (chat_id, sender, message) VALUES (?, ?, ?)");
                msgPs.setInt(1, chatId);
                msgPs.setString(2, "user");
                msgPs.setString(3, msg);
                msgPs.executeUpdate();

                // Risposta automatica dopo qualche secondo
                response.put("success", true);
            } else if ("replyAuto".equals(action)) {
                int chatId = Integer.parseInt(req.getParameter("chatId"));
                PreparedStatement msgPs = conn.prepareStatement("INSERT INTO support_messages (chat_id, sender, message) VALUES (?, ?, ?)");
                msgPs.setInt(1, chatId);
                msgPs.setString(2, "support");
                msgPs.setString(3, "Segnalazione ricevuta, ti risponderemo al più presto");
                msgPs.executeUpdate();
                response.put("success", true);
            }
        } catch(Exception e) { e.printStackTrace(); }
        resp.getWriter().write(response.toString());
    }
}