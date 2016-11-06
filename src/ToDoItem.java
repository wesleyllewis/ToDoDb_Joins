public class ToDoItem {
    int id;
    int userId;
    public String text;
    public boolean isDone;

    public ToDoItem(int id, int userId, String text, boolean isDone){
        this.id = id;
        this.userId = userId;
        this.text = text;
        this.isDone = isDone;

    }
}
