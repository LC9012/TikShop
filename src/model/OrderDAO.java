
package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import util.DBUtil;

public class OrderDAO {

    public static List<Order> getAllOrders() throws SQLException {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT o.id, o.user_id, o.order_date, o.total, o.status, o.shipping_address, o.card_holder_name, u.name as customer_name " +
                     "FROM orders o JOIN users u ON o.user_id = u.id " +
                     "ORDER BY o.order_date DESC";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Order order = new Order();
                order.setId(rs.getInt("id"));
                order.setUserId(rs.getInt("user_id"));
                Timestamp ts = rs.getTimestamp("order_date");
                order.setOrderDate(ts != null ? new java.util.Date(ts.getTime()) : null);
                order.setTotal(rs.getDouble("total"));
                order.setStatus(rs.getString("status"));
                order.setShippingAddress(rs.getString("shipping_address"));
                order.setCustomerName(rs.getString("customer_name"));
                order.setCardHolderName(rs.getString("card_holder_name"));
                order.setItems(getOrderItems(order.getId()));
                orders.add(order);
            }
        }
        return orders;
    }

    public static int createOrder(Order order) throws SQLException {
        String insertOrder = "INSERT INTO orders (user_id, total, status, shipping_address, " +
                             "card_holder_name, encrypted_card_number, card_last_four_digits, card_expiry_date) " +
                             "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        String insertItem = "INSERT INTO order_items (order_id, product_id, quantity, price) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false); 

            try (PreparedStatement psOrder = conn.prepareStatement(insertOrder, Statement.RETURN_GENERATED_KEYS)) {
                psOrder.setInt(1, order.getUserId());
                psOrder.setDouble(2, order.getTotal());
                psOrder.setString(3, order.getStatus());
                psOrder.setString(4, order.getShippingAddress());
                psOrder.setString(5, order.getCardHolderName());
                psOrder.setString(6, order.getEncryptedCardNumber());
                psOrder.setString(7, order.getCardLastFourDigits());
                psOrder.setString(8, order.getCardExpiryDate());
                psOrder.executeUpdate();

                try (ResultSet rs = psOrder.getGeneratedKeys()) {
                    if (rs.next()) {
                        int orderId = rs.getInt(1);
                        order.setId(orderId);

                        try (PreparedStatement psItem = conn.prepareStatement(insertItem)) {
                            for (OrderItem item : order.getItems()) {
                                psItem.setInt(1, orderId);
                                psItem.setInt(2, item.getProductId());
                                psItem.setInt(3, item.getQuantity());
                                psItem.setDouble(4, item.getPrice());
                                psItem.addBatch();
                            }
                            psItem.executeBatch();
                        }
                        
                        conn.commit(); 
                        return orderId;
                    } else {
                        conn.rollback();
                        throw new SQLException("Creazione ordine fallita, nessun ID ottenuto.");
                    }
                }
            } catch (SQLException e) {
                conn.rollback(); 
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    public static List<Order> getOrdersByUser(int userId) throws SQLException {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE user_id = ? ORDER BY order_date DESC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Order o = new Order();
                    o.setId(rs.getInt("id"));
                    o.setUserId(rs.getInt("user_id"));
                    Timestamp ts = rs.getTimestamp("order_date");
                    o.setOrderDate(ts != null ? new java.util.Date(ts.getTime()) : null);
                    o.setTotal(rs.getDouble("total"));
                    o.setStatus(rs.getString("status"));
                    o.setShippingAddress(rs.getString("shipping_address"));
                    o.setCardHolderName(rs.getString("card_holder_name"));
                    o.setItems(getOrderItems(o.getId()));
                    orders.add(o);
                }
            }
        }
        return orders;
    }

    public static List<OrderItem> getOrderItems(int orderId) throws SQLException {
        List<OrderItem> items = new ArrayList<>();
        String sql = "SELECT oi.*, p.name AS productName, p.foto AS productImg FROM order_items oi JOIN products p ON oi.product_id = p.id WHERE oi.order_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OrderItem item = new OrderItem();
                    item.setId(rs.getInt("id"));
                    item.setOrderId(rs.getInt("order_id"));
                    item.setProductId(rs.getInt("product_id"));
                    item.setQuantity(rs.getInt("quantity"));
                    item.setPrice(rs.getDouble("price"));
                    item.setProductName(rs.getString("productName"));
                    item.setProductImg(rs.getString("productImg"));
                    items.add(item);
                }
            }
        }
        return items;
    }
    
    //Aggiorna lo stato di un ordine specifico
    public static void updateOrderStatus(int orderId, String newStatus) throws SQLException {
        String sql = "UPDATE orders SET status = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setInt(2, orderId);
            ps.executeUpdate();
        }
    }

    //Verifica se un ordine appartiene a un determinato utente
    //per evitare che gli utenti modifichino ordini altrui

    public static boolean isOrderOwnedBy(int orderId, int userId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM orders WHERE id = ? AND user_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ps.setInt(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
    
    //Elimina un ordine e tutti i suoi articoli associati.

    public static void deleteOrder(int orderId) throws SQLException {
        String deleteItemsSql = "DELETE FROM order_items WHERE order_id = ?";
        String deleteOrderSql = "DELETE FROM orders WHERE id = ?";
        
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);

            //Elimina prima gli articoli dell'ordine
            try (PreparedStatement psItems = conn.prepareStatement(deleteItemsSql)) {
                psItems.setInt(1, orderId);
                psItems.executeUpdate();
            }

            //Elimina l'ordine stesso
            try (PreparedStatement psOrder = conn.prepareStatement(deleteOrderSql)) {
                psOrder.setInt(1, orderId);
                psOrder.executeUpdate();
            }
         // Finalizza la transazione se tutto è andato a buon fine
            conn.commit(); 

        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback(); // Annulla la transazione in caso di errore
            }
            throw e; // Rilancia l'eccezione
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true); 
                conn.close();
            }
        }
    }
    
    public static List<Order> getAllOrders(String sortBy, String sortOrder, String statusFilter) throws SQLException {
        List<Order> orders = new ArrayList<>();
        
        // Costruzione dinamica della query SQL
        StringBuilder sql = new StringBuilder(
            "SELECT o.id, o.user_id, o.order_date, o.total, o.status, o.shipping_address, o.card_holder_name, u.name as customer_name " +
            "FROM orders o JOIN users u ON o.user_id = u.id "
        );

        // Applica il filtro per stato, se presente
        if (statusFilter != null && !statusFilter.isEmpty() && !statusFilter.equals("all")) {
            sql.append("WHERE o.status = ? ");
        }

        // Applica l'ordinamento
        sql.append("ORDER BY ");
        switch (sortBy) {
            case "name":
                sql.append("u.name ");
                break;
            case "status":
                sql.append("o.status ");
                break;
            default: 
                sql.append("o.order_date ");
                break;
        }
        sql.append(sortOrder.equalsIgnoreCase("asc") ? "ASC" : "DESC"); 

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            
            // Imposta il parametro per il filtro di stato, se necessario
            if (statusFilter != null && !statusFilter.isEmpty() && !statusFilter.equals("all")) {
                stmt.setString(1, statusFilter);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Order order = new Order();
                    order.setId(rs.getInt("id"));
                    order.setUserId(rs.getInt("user_id"));
                    java.sql.Timestamp ts = rs.getTimestamp("order_date");
                    order.setOrderDate(ts != null ? new java.util.Date(ts.getTime()) : null);
                    order.setTotal(rs.getDouble("total"));
                    order.setStatus(rs.getString("status"));
                    order.setShippingAddress(rs.getString("shipping_address"));
                    order.setCustomerName(rs.getString("customer_name"));
                    order.setCardHolderName(rs.getString("card_holder_name"));
                    order.setItems(getOrderItems(order.getId()));
                    orders.add(order);
                }
            }
        }
        return orders;
    }
}