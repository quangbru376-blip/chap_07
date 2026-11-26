package murach.data;

import java.io.*;
import java.util.*;

import murach.business.Product;

public class ProductIO {

    public static Product getProduct(String code, String filepath) {
        if (code == null || filepath == null) {
            return null;
        }
        File file = new File(filepath);
        if (!file.exists()) {
            return null;
        }
        try (BufferedReader in = new BufferedReader(new FileReader(file))) {
            String line = in.readLine();
            while (line != null) {
                StringTokenizer t = new StringTokenizer(line, "|");
                if (t.countTokens() < 3) {
                    line = in.readLine();
                    continue;
                }
                String productCode = t.nextToken();
                if (code.equalsIgnoreCase(productCode)) {
                    String description = t.nextToken();
                    double price = Double.parseDouble(t.nextToken());
                    Product p = new Product();
                    p.setCode(productCode);
                    p.setDescription(description);
                    p.setPrice(price);
                    return p;
                }
                line = in.readLine();
            }
            return null;
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ArrayList<Product> getProducts(String filepath) {
        if (filepath == null) {
            return null;
        }
        File file = new File(filepath);
        if (!file.exists()) {
            return new ArrayList<>();
        }
        ArrayList<Product> products = new ArrayList<>();
        try (BufferedReader in = new BufferedReader(new FileReader(file))) {
            String line = in.readLine();
            while (line != null) {
                StringTokenizer t = new StringTokenizer(line, "|");
                if (t.countTokens() < 3) {
                    line = in.readLine();
                    continue;
                }
                String code = t.nextToken();
                String description = t.nextToken();
                double price = Double.parseDouble(t.nextToken());
                Product p = new Product();
                p.setCode(code);
                p.setDescription(description);
                p.setPrice(price);
                products.add(p);
                line = in.readLine();
            }
            return products;
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
            return null;
        }
    }
}
