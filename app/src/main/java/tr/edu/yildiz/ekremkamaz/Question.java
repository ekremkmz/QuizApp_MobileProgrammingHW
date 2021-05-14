package tr.edu.yildiz.ekremkamaz;

import java.util.ArrayList;

public class Question {
    String title;
    int level;
    ArrayList<String> choices;
    int answer;
    String content_type;
    String content_path;
    boolean expanded = false;

    public Question(String title, int level, ArrayList<String> choices, int answer, String content_type, String content_path) {
        this.title = title;
        this.level = level;
        this.choices = choices;
        this.answer = answer;
        this.content_type = content_type;
        this.content_path = content_path;
    }

    public boolean getExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public String getTitle() {
        return title;
    }

    public int getLevel() {
        return level;
    }

    public ArrayList<String> getChoices() {
        return choices;
    }

    public int getAnswer() {
        return answer;
    }

    public String getContent_type() {
        return content_type;
    }

    public String getContent_path() {
        return content_path;
    }
}
