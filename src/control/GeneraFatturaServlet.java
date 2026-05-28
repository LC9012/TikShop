package control;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import model.Order;
import model.OrderDAO;
import model.OrderItem;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

@WebServlet("/GeneraFatturaServlet")
public class GeneraFatturaServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int orderId = Integer.parseInt(req.getParameter("orderId"));
        HttpSession session = req.getSession(false);
        Integer userId = (session != null) ? (Integer) session.getAttribute("userId") : null;

        if (userId == null || !isOrderOwnedBy(orderId, userId)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        try {
            Order order = OrderDAO.getOrdersByUser(userId)
                                  .stream()
                                  .filter(o -> o.getId() == orderId)
                                  .findFirst()
                                  .orElse(null);

            if (order == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            resp.setContentType("application/pdf");
            resp.setHeader("Content-Disposition", "attachment; filename=fattura_" + orderId + ".pdf");

            try (OutputStream os = resp.getOutputStream()) {
                Document document = new Document();
                PdfWriter.getInstance(document, os);
                document.open();

                document.add(new Paragraph("Fattura Ordine #" + order.getId()));
                document.add(new Paragraph("Data: " + order.getOrderDate()));
                document.add(new Paragraph("Cliente: " + order.getCardHolderName()));
                document.add(new Paragraph("Indirizzo: " + order.getShippingAddress()));
                document.add(new Paragraph("Totale: €" + order.getTotal()));
                document.add(new Paragraph(" "));

                PdfPTable table = new PdfPTable(3);
                table.addCell("Prodotto");
                table.addCell("Quantità");
                table.addCell("Prezzo");

                for (OrderItem item : order.getItems()) {
                    table.addCell(item.getProductName());
                    table.addCell(String.valueOf(item.getQuantity()));
                    table.addCell("\u20ac" + item.getPrice());
                }

                document.add(table);
                document.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Errore durante la generazione della fattura");
        }
    }

    private boolean isOrderOwnedBy(int orderId, int userId) {
        try {
            return OrderDAO.isOrderOwnedBy(orderId, userId);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
