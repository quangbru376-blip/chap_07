package murach.cart;

import java.io.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;

import murach.business.Cart;
import murach.business.LineItem;
import murach.business.Product;
import murach.data.ProductIO;

public class CartServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        String url = "/index.jsp";
        ServletContext sc = getServletContext();

        // get current action
        String action = request.getParameter("action");
        if (action == null) {
            action = "cart";  // default action
        }

        // perform action and set URL to appropriate page
        if ("shop".equals(action)) {
            url = "/index.jsp";
        } else if ("cart".equals(action)) {
            String productCode = request.getParameter("productCode");
            String quantityString = request.getParameter("quantity");

            HttpSession session = request.getSession();
            Cart cart = (Cart) session.getAttribute("cart");
            if (cart == null) {
                cart = new Cart();
            }

            if (productCode != null && !productCode.isEmpty()) {
                // if the user enters a negative or invalid quantity,
                // the quantity is automatically reset to 1.
                int quantity;
                try {
                    quantity = Integer.parseInt(quantityString);
                    if (quantity < 0) {
                        quantity = 1;
                    }
                } catch (NumberFormatException nfe) {
                    quantity = 1;
                }

                String path = sc.getRealPath("/WEB-INF/products.txt");
                Product product = ProductIO.getProduct(productCode, path);

                if (product != null) {
                    LineItem lineItem = new LineItem();
                    lineItem.setProduct(product);
                    lineItem.setQuantity(quantity);
                    if (quantity > 0) {
                        cart.addItem(lineItem);
                    } else {
                        cart.removeItem(lineItem);
                    }
                }
            }

            session.setAttribute("cart", cart);
            url = "/cart.jsp";
        } else if ("checkout".equals(action)) {
            url = "/checkout.jsp";
        }

        sc.getRequestDispatcher(url)
                .forward(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }
}
