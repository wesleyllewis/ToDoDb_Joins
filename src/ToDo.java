import org.h2.tools.Server;

import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

public class ToDo {

    public static void main(String[] args) throws SQLException {
        Server.createWebServer().start();
        Connection conn = DriverManager.getConnection("jdbc:h2:./main");
        createTables(conn);
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please Enter Your Name");
        String name = scanner.nextLine();
        if (name == null){
            System.out.println("Name Was Left Blank. Please Enter A User Name");
        }
        insertUser(conn, name);
        System.out.println("Hello!" + name + "!");
        while (true) {
            System.out.println("1. Create to-do item");
            System.out.println("2. Toggle to-do item");
            System.out.println("3. List to-do items");
            System.out.println("4. Delete to-do item");

            String option = scanner.nextLine();

            switch (option) {
                case "1": { //Create New Item
                    System.out.println("Enter your to-do item:");
                    String text = scanner.nextLine();
                    User user = selectUser(conn, name);
                    insertToDo(conn, user.id, text);
                    break;
                }
                case "2": { //Toggle Item
                    System.out.println("Enter the number of the item you want to toggle:");
                    int itemNum = Integer.valueOf(scanner.nextLine());
                    toggleToDo(conn, itemNum);
                    break;
                }
                case "3": //Display List
                    User user = selectUser(conn, name);
                    ArrayList<ToDoItem> items = selectToDos(conn, user.id);
                    for (ToDoItem item : items) {
                        String checkbox = "[ ] ";
                        if (item.isDone) {
                            checkbox = "[x] ";
                        }
                        String line = String.format("%d. %s %s", item.id, checkbox, item.text );
                        System.out.println(line);
                    }
                    break;
                case "4": //Delete item
                    System.out.println("Enter the number of the item you would like to delete:");
                    int itemNum = Integer.valueOf(scanner.nextLine());
                    deleteItem(conn, itemNum);
                    break;
                default:
                    System.out.println("Invalid option");
                    break;
            }
        }
    }
    public static void createTables(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.execute("CREATE TABLE IF NOT EXISTS users (id IDENTITY, name VARCHAR)");
        stmt.execute("CREATE TABLE IF NOT EXISTS todos (id IDENTITY, user_id INT, text VARCHAR, is_done BOOLEAN )");
    }
    public static void insertToDo(Connection conn, int userId, String text) throws SQLException{
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO todos VALUES (NULL, ?, ?, FALSE )");
        stmt.setInt(1, userId);
        stmt.setString(2, text);
        stmt.execute();
    }
    public static ToDoItem selectTodo(Connection conn, int id) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM todos INNER JOIN users ON todos.user_id = users.id WHERE todos.id=?");
        stmt.setInt(1, id);
        ResultSet results = stmt.executeQuery();
        if (results.next()){
            int userId = results.getInt("users.id");
            String text = results.getString("todos.text");
            Boolean isDone = results.getBoolean("todos.is_done");
            return new ToDoItem(id, userId, text, isDone);
        }
        return null;
    }
    public static ArrayList<ToDoItem> selectToDos(Connection conn, int userId) throws SQLException{
        ArrayList<ToDoItem> items = new ArrayList<>();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM todos INNER JOIN users ON todos.user_id = users.id WHERE todos.user_id=?");
        stmt.setInt(1, userId);
        ResultSet results = stmt.executeQuery();
        while (results.next()){
            int id = results.getInt("todos.id");
            String text = results.getString("todos.text");
            Boolean isDone = results.getBoolean("todos.is_done");
            items.add(new ToDoItem(id, userId, text, isDone));
        }
        return items;
    }
    public static void toggleToDo(Connection conn, int id) throws SQLException{
        PreparedStatement stmt = conn.prepareStatement("UPDATE todos SET is_done = NOT is_done where id=?");
        stmt.setInt(1, id);
        stmt.execute();
    }
    public static void insertUser(Connection conn, String name) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO users VALUES (NULL, ?)");
        stmt.setString(1, name);
        stmt.execute();
    }
    public static User selectUser(Connection conn, String name) throws SQLException{
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE name=?");
        stmt.setString(1, name);
        ResultSet results = stmt.executeQuery();
        if (results.next()){
            int id = results.getInt("id");
            return new User(name, id);
        }
        return null;
    }
    public static void deleteItem(Connection conn, int id) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM todos WHERE id=?");
        stmt.setInt(1, id);
        stmt.execute();
    }

}
