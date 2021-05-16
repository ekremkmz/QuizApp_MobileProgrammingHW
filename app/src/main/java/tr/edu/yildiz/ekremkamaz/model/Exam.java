package tr.edu.yildiz.ekremkamaz.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class Exam implements Parcelable {
    private String title;
    private int level;
    private int time;
    private int point;
    private ArrayList<String> questions;
    private int id;
    private int user_id;

    public Exam(String title, int level, int time, int point, ArrayList<String> questions, int id, int user_id) {
        this.title = title;
        this.level = level;
        this.time = time;
        this.point = point;
        this.questions = questions;
        this.id = id;
        this.user_id = user_id;
    }

    protected Exam(Parcel in) {
        title = in.readString();
        level = in.readInt();
        time = in.readInt();
        point = in.readInt();
        questions = in.createStringArrayList();
        id = in.readInt();
        user_id = in.readInt();
    }

    public static final Creator<Exam> CREATOR = new Creator<Exam>() {
        @Override
        public Exam createFromParcel(Parcel in) {
            return new Exam(in);
        }

        @Override
        public Exam[] newArray(int size) {
            return new Exam[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public int getLevel() {
        return level;
    }

    public int getTime() {
        return time;
    }

    public int getPoint() {
        return point;
    }

    public ArrayList<String> getQuestions() {
        return questions;
    }

    public int getId() {
        return id;
    }

    public int getUser_id() {
        return user_id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeInt(level);
        parcel.writeInt(time);
        parcel.writeInt(point);
        parcel.writeStringList(questions);
        parcel.writeInt(id);
        parcel.writeInt(user_id);
    }


    public File toFile(Context context) throws IOException {
        File file = File.createTempFile("exam", ".txt", context.getExternalCacheDir());

        String data = new String();

        data = "title=" + title + "\n" +
                "level=" + String.valueOf(level) + "\n" +
                "time=" + String.valueOf(time) + "\n" +
                "point=" + String.valueOf(point) + "\n" +
                "questions=" + String.join(",", questions);


        FileOutputStream stream = new FileOutputStream(file);

        stream.write(data.getBytes());

        stream.close();

        return file;
    }
}
