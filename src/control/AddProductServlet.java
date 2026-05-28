
package control;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import model.Product;
import model.ProductDAO;

@WebServlet("/AddProductServlet")
@MultipartConfig 
public class AddProductServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    // Costanti che corrispondono ai nomi delle cartelle
    private static final String UPLOAD_DIR_IMAGES = "uploads" + File.separator + "Foto";
    private static final String UPLOAD_DIR_VIDEOS = "uploads" + File.separator + "Video";

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        String applicationPath = request.getServletContext().getRealPath("");
        String uploadFilePathImages = applicationPath + File.separator + UPLOAD_DIR_IMAGES;
        String uploadFilePathVideos = applicationPath + File.separator + UPLOAD_DIR_VIDEOS;
        
        new File(uploadFilePathImages).mkdirs();
        new File(uploadFilePathVideos).mkdirs();

        try {
            // Leggiamo i campi dal form
            String name = request.getParameter("name");
            String description = request.getParameter("description");
            double price = Double.parseDouble(request.getParameter("price"));
            String department = request.getParameter("department");
            
            // Si occupa delle immagini	
            Part fotoPart = request.getPart("foto");
            String fotoFileName = new File(fotoPart.getSubmittedFileName()).getName();
            String fotoDbPath = UPLOAD_DIR_IMAGES.replace(File.separator, "/") + "/" + fotoFileName;
            fotoPart.write(uploadFilePathImages + File.separator + fotoFileName);

            // Si occupa dei video 
            Part videoPart = request.getPart("video");
            String videoFileName = new File(videoPart.getSubmittedFileName()).getName();
            String videoDbPath = UPLOAD_DIR_VIDEOS.replace(File.separator, "/") + "/" + videoFileName;
            videoPart.write(uploadFilePathVideos + File.separator + videoFileName);
            
            // Crea oggetto prodotto
            Product newProduct = new Product();
            newProduct.setName(name);
            newProduct.setDescription(description);
            newProduct.setPrice(price);
            newProduct.setDepartment(department); 
            newProduct.setFotoUrl(fotoDbPath);
            newProduct.setVideoUrl(videoDbPath);
            
            // Salva il prodotto col DAO
            ProductDAO productDAO = new ProductDAO();
            productDAO.addProduct(newProduct);
            
            // Reindirizzamento se ha avuto successo
            response.sendRedirect(request.getContextPath() + "/AdminProductsServlet?status=added");

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("success", false);
            request.setAttribute("errorMessage", "Errore durante il caricamento: " + e.getMessage());
            request.getRequestDispatcher("/admin/upload-product.jsp").forward(request, response);
        }
    }
}