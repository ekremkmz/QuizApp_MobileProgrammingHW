package tr.edu.yildiz.ekremkamaz.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Question implements Parcelable {
    private String title;
    private int level;
    private ArrayList<String> choices;
    private int answer;
    private String content_type;
    private String content_path;
    private boolean expanded;
    private int id;
    private int user_id;

    public Question(String title, int level, ArrayList<String> choices, int answer, String content_type, String content_path, int id, int user_id) {
        this.title = title;
        this.level = level;
        this.choices = choices;
        this.answer = answer;
        this.content_type = content_type;
        this.content_path = content_path;
        this.id = id;
        this.user_id = user_id;
        expanded = false;
    }

    protected Question(Parcel in) {
        title = in.readString();
        level = in.readInt();
        choices = in.createStringArrayList();
        answer = in.readInt();
        content_type = in.readString();
        content_path = in.readString();
        expanded = in.readByte() != 0;
        id = in.readInt();
        user_id = in.readInt();
    }

    public static final Creator<Question> CREATOR = new Creator<Question>() {
        @Override
        public Question createFromParcel(Parcel in) {
            return new Question(in);
        }

        @Override
        public Question[] newArray(int size) {
            return new Question[size];
        }
    };

    public int getUser_Id() {
        return user_id;
    }

    public int getId() {
        return id;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeInt(level);
        parcel.writeStringList(choices);
        parcel.writeInt(answer);
        parcel.writeString(content_type);
        parcel.writeString(content_path);
        parcel.writeByte((byte) (expanded ? 1 : 0));
        parcel.writeInt(id);
        parcel.writeInt(user_id);
    }
}
