package murach.download;

import java.io.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;

import murach.business.Product;
import murach.business.User;
import murach.data.ProductIO;
import murach.data.UserIO;
import murach.util.CookieUtil;

public class DownloadServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws IOException, ServletException {

        // get current action
        String action = request.getParameter("action");
        if (action == null) {
            action = "viewAlbums";  // default action
        }

        // perform action and set URL to appropriate page
        String url = "/index.jsp";
        if (action.equals("viewAlbums")) {
            url = "/index.jsp";
        } else if (action.equals("checkUser")) {
            url = checkUser(request, response);
        } else if (action.equals("viewCookies")) {
            url = "/view_cookies.jsp";
        } else if (action.equals("deleteCookies")) {
            url = deleteCookies(request, response);
        }

        // forward to the view
        getServletContext()
                .getRequestDispatcher(url)
                .forward(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws IOException, ServletException {

        String action = request.getParameter("action");

        // perform action and set URL to appropriate page
        String url = "/index.jsp";
        if ("registerUser".equals(action)) {
            url = registerUser(request, response);
        }

        // forward to the view
        getServletContext()
                .getRequestDispatcher(url)
                .forward(request, response);
    }

    private String checkUser(HttpServletRequest request,
            HttpServletResponse response) {

        String productCode = request.getParameter("productCode");
        if (productCode == null || productCode.isEmpty()) {
            return "/index.jsp";
        }

        HttpSession session = request.getSession();

        // get Product object and set it as session attribute instead of productCode (ch07_ex2)
        ServletContext sc = getServletContext();
        String productPath = sc.getRealPath("/WEB-INF/products.txt");
        Product product = ProductIO.getProduct(productCode, productPath);
        if (product == null) {
            return "/index.jsp";
        }
        session.setAttribute("product", product);

        // get the User object
        User user = (User) session.getAttribute("user");

        String url;
        // if User object doesn't exist, check email cookie
        if (user == null) {
            Cookie[] cookies = request.getCookies();
            String emailAddress =
                CookieUtil.getCookieValue(cookies, "userEmail");

            // if cookie doesn't exist, go to Registration page
            if (emailAddress == null || emailAddress.isEmpty()) {
                url = "/register.jsp";
            }
            // if cookie exists, create User object and go to Downloads page
            else {
                String path = sc.getRealPath("/WEB-INF/EmailList.txt");
                user = UserIO.getUser(emailAddress, path);
                if (user == null) {
                    url = "/register.jsp";
                } else {
                    session.setAttribute("user", user);
                    url = "/" + productCode + "_download.jsp";
                }
            }
        }
        // if User object exists, go to Downloads page
        else {
            url = "/" + productCode + "_download.jsp";
        }
        return url;
    }

    private String registerUser(HttpServletRequest request,
            HttpServletResponse response) {

        // get the user data
        String email = request.getParameter("email");
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");

        // store the data in a User object
        User user = new User();
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);

        // write the User object to a file
        ServletContext sc = getServletContext();
        String path = sc.getRealPath("/WEB-INF/EmailList.txt");
        UserIO.add(user, path);

        // store the User object as a session attribute
        HttpSession session = request.getSession();
        session.setAttribute("user", user);

        // add a cookie that stores the user's email to browser
        Cookie c = new Cookie("userEmail", email);
        c.setMaxAge(60 * 60 * 24 * 365 * 3); // set age to 3 years
        c.setPath("/");                      // allow entire app to access it
        response.addCookie(c);

        // create and return a URL for the appropriate Download page
        Product product = (Product) session.getAttribute("product");
        if (product == null || product.getCode().isEmpty()) {
            return "/index.jsp";
        }
        return "/" + product.getCode() + "_download.jsp";
    }

    private String deleteCookies(HttpServletRequest request,
            HttpServletResponse response) {

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                cookie.setMaxAge(0); // delete the cookie
                cookie.setPath("/"); // allow the download application to access it
                response.addCookie(cookie);
            }
        }
        return "/delete_cookies.jsp";
    }
}