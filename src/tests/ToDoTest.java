import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Created by WesleyLewis on 9/28/16.
 */
public class ToDoTest {
    public Connection startConnection() throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:h2:mem:test");
        ToDo.createTables(conn);
        return conn;
    }
    @Test
    public void testUser() throws SQLException {
        Connection conn = startConnection();
        ToDo.insertUser(conn, "Test");
        User user = ToDo.selectUser(conn, "Test");
        conn.close();
        assertTrue(user != null);
    }
    @Test
    public void testInsert() throws SQLException{
        Connection conn = startConnection();
        ToDo.insertUser(conn, "Test");
        User user = ToDo.selectUser(conn, "Test");
        ToDo.insertToDo(conn, user.id, "Hello, World!");
        ToDo.insertToDo(conn, user.id, "This is another item!");
        ToDoItem t = ToDo.selectTodo(conn, user.id);
        ArrayList<ToDoItem> items = ToDo.selectToDos(conn, t.id);
        conn.close();
        assertTrue(t.text.contains("Hello"));
        assertTrue(items.size() == 2);
    }
    @Test
    public void testToggle() throws SQLException {
        Connection conn = startConnection();
        ToDo.insertUser(conn, "Test");
        User user = ToDo.selectUser(conn, "Test");
        ToDo.insertToDo(conn, user.id, "Test");
        ToDoItem t = ToDo.selectTodo(conn, user.id);
        ToDo.toggleToDo(conn, t.id);
        assertFalse(t.isDone);
    }
    @Test
    public void testDeleteItem() throws SQLException{
        Connection conn = startConnection();
        ToDo.insertUser(conn, "Test");
        User user = ToDo.selectUser(conn, "Test");
        ToDo.insertToDo(conn, user.id, "Test");
        ToDoItem t = ToDo.selectTodo(conn, user.id);
        ToDo.deleteItem(conn, t.id);
        assertTrue(t.text.contains(""));
    }
}