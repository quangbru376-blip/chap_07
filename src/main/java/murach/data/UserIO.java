package murach.data;

import java.io.*;
import java.util.*;

import murach.business.User;

public class UserIO {

    public static boolean add(User user, String filepath) {
        if (user == null || filepath == null) {
            return false;
        }
        File file = new File(filepath);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
        try (PrintWriter out = new PrintWriter(new FileWriter(file, true))) {
            out.println(user.getEmail() + "|"
                    + user.getFirstName() + "|"
                    + user.getLastName());
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static User getUser(String email, String filepath) {
        if (email == null || filepath == null) {
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
                String token = t.nextToken();
                if (token.equalsIgnoreCase(email)) {
                    String firstName = t.nextToken();
                    String lastName = t.nextToken();
                    return new User(firstName, lastName, email);
                }
                line = in.readLine();
            }
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ArrayList<User> getUsers(String filepath) {
        if (filepath == null) {
            return null;
        }
        File file = new File(filepath);
        if (!file.exists()) {
            return new ArrayList<>();
        }
        ArrayList<User> users = new ArrayList<>();
        try (BufferedReader in = new BufferedReader(new FileReader(file))) {
            String line = in.readLine();
            while (line != null) {
                StringTokenizer t = new StringTokenizer(line, "|");
                if (t.countTokens() < 3) {
                    line = in.readLine();
                    continue;
                }
                String email = t.nextToken();
                String firstName = t.nextToken();
                String lastName = t.nextToken();
                User user = new User(firstName, lastName, email);
                users.add(user);
                line = in.readLine();
            }
            return users;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}