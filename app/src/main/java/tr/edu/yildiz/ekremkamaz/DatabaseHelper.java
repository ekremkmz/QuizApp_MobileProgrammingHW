package tr.edu.yildiz.ekremkamaz;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;

import tr.edu.yildiz.ekremkamaz.model.Exam;
import tr.edu.yildiz.ekremkamaz.model.Question;
import tr.edu.yildiz.ekremkamaz.model.User;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static DatabaseHelper DBHelper = null;
    public static SQLiteDatabase DB = null;

    private DatabaseHelper(Context context) {
        super(context, "database", null, 1);
        openDatabase();
    }

    public static DatabaseHelper getInstance(Context context) {
        if (DBHelper == null) {
            DBHelper = new DatabaseHelper(context);
        }
        return DBHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE users (\n" +
                "    id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "    avatar varchar(10),\n" +
                "    name varchar(20),\n" +
                "    surname varchar(20),\n" +
                "    email varchar(50),\n" +
                "    number varchar(50),\n" +
                "    password varchar(64),\n" +
                "    birthday varchar(10)\n" +
                ");");
        db.execSQL("CREATE TABLE questions (\n" +
                "    id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "    user_id INTEGER,\n" +
                "    title text,\n" +
                "    level INTEGER,\n" +
                "    choices text,\n" +
                "    answer INTEGER,\n" +
                "    content_type text,\n" +
                "    content_path text\n" +
                ");");
        db.execSQL("CREATE TABLE exams (\n" +
                "    id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "    user_id INTEGER,\n" +
                "    title text,\n" +
                "    questions text,\n" +
                "    level INTEGER,\n" +
                "    time INTEGER,\n" +
                "    point INTEGER\n" +
                ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS users");
        onCreate(db);
    }


    public boolean deleteExam(Exam exam) {
        openDatabase();
        int result = DB.delete("exams", "id=?", new String[]{String.valueOf(exam.getId())});
        if (result > -1)
            return true;
        else
            return false;
    }

    public boolean addExam(Exam exam) {
        openDatabase();
        ContentValues cv = new ContentValues();
        cv.put("user_id", exam.getUser_id());
        cv.put("title", exam.getTitle());
        cv.put("questions", String.join(",", exam.getQuestions()));
        cv.put("level", exam.getLevel());
        cv.put("time", exam.getTime());
        cv.put("point", exam.getPoint());
        long result = DB.insert("exams", null, cv);

        if (result > -1)
            return true;
        else
            return false;
    }

    public ArrayList<Exam> getExams(int user_id) {
        openDatabase();
        String[] columns = {"title", "level", "questions", "time", "point", "id"};

        String[] args = {String.valueOf(user_id)};
        Cursor c = DB.query("exams", columns, "user_id=?", args, null, null, null);
        ArrayList<Exam> examArrayList = new ArrayList<>();
        while (c.moveToNext()) {
            ArrayList<String> questions = new ArrayList<>(Arrays.asList(c.getString(2).split(",")));
            examArrayList.add(new Exam(c.getString(0), c.getInt(1), c.getInt(3), c.getInt(4), questions, c.getInt(5), user_id));
        }
        return examArrayList;
    }

    public boolean addUser(String avatar, String name, String surname, String email, String number, String password, String birthday) {
        openDatabase();
        ContentValues cv = new ContentValues();
        cv.put("avatar", avatar);
        cv.put("name", name);
        cv.put("surname", surname);
        cv.put("email", email);
        cv.put("number", number);
        cv.put("password", password);
        cv.put("birthday", birthday);
        long result = DB.insert("users", null, cv);

        if (result > -1)
            return true;
        else
            return false;
    }

    public boolean addQuestion(Question question, Activity activity) throws IOException {
        openDatabase();
        ContentValues cv = new ContentValues();
        cv.put("user_id", question.getUser_Id());
        cv.put("title", question.getTitle());
        cv.put("level", question.getLevel());
        cv.put("choices", String.join("$$$", question.getChoices()));
        cv.put("answer", question.getAnswer());
        cv.put("content_type", question.getContent_type());

        if (question.getContent_type().equals("")) {
            cv.put("content_path", question.getContent_type());
        } else {
            File destFile = new File(activity.getFilesDir().getPath() + "/" + question.getUser_Id() + "_" + question.getId());
            cv.put("content_path", destFile.getPath());
            copyFile(new File(question.getContent_path()), destFile);
        }

        long result = DB.insert("questions", null, cv);

        if (result > -1)
            return true;
        else
            return false;
    }

    public void copyFile(File src, File dst) throws IOException {
        FileInputStream inStream = new FileInputStream(src);
        FileOutputStream outStream = new FileOutputStream(dst);
        FileChannel inChannel = inStream.getChannel();
        FileChannel outChannel = outStream.getChannel();
        inChannel.transferTo(0, inChannel.size(), outChannel);
        inStream.close();
        outStream.close();
    }

    public ArrayList<Question> getQuestions(int user_id) {
        openDatabase();
        String[] columns = {"title", "level", "choices", "answer", "content_type", "content_path", "id", "user_id"};

        String[] args = {String.valueOf(user_id)};
        Cursor c = DB.query("questions", columns, "user_id=?", args, null, null, null);
        ArrayList<Question> questionArrayList = new ArrayList<>();
        while (c.moveToNext()) {
            ArrayList<String> choices = new ArrayList<>(Arrays.asList(c.getString(2).split("\\Q$$$\\E")));
            questionArrayList.add(new Question(c.getString(0), c.getInt(1), choices, c.getInt(3), c.getString(4), c.getString(5), c.getInt(6), c.getInt(7)));
        }
        return questionArrayList;
    }

    public Question getQuestion(int id) {
        openDatabase();
        String[] columns = {"title", "level", "choices", "answer", "content_type", "content_path", "id", "user_id"};

        String[] args = {String.valueOf(id)};
        Cursor c = DB.query("questions", columns, "id=?", args, null, null, null);
        c.moveToNext();
        ArrayList<String> choices = new ArrayList<>(Arrays.asList(c.getString(2).split("\\Q$$$\\E")));

        return new Question(c.getString(0), c.getInt(1), choices, c.getInt(3), c.getString(4), c.getString(5), c.getInt(6), c.getInt(7));
    }

    public boolean deleteQuestion(Question question) {
        openDatabase();
        int result = DB.delete("questions", "id=?", new String[]{String.valueOf(question.getId())});
        if (result > -1)
            return true;
        else
            return false;
    }

    public boolean updateQuestion(Question question) {
        openDatabase();
        Question oldQuestion = getQuestion(question.getId());
        if (!oldQuestion.getContent_path().equals(question.getContent_path())) {
            //Deleting old files
            File file = new File(oldQuestion.getContent_path());
            file.delete();
        }
        ContentValues cv = new ContentValues();
        cv.put("user_id", question.getUser_Id());
        cv.put("title", question.getTitle());
        cv.put("level", question.getLevel());
        cv.put("choices", String.join("$$$", question.getChoices()));
        cv.put("answer", question.getAnswer());
        cv.put("content_type", question.getContent_type());
        cv.put("content_path", question.getContent_path());
        long result = DB.update("questions", cv, "id=?", new String[]{String.valueOf(question.getId())});
        if (result > -1)
            return true;
        else
            return false;
    }

    public boolean checkMail(String mail) {
        openDatabase();
        String[] columns = {"id"};
        String[] args = {mail};
        Cursor c = DB.query("users", columns, "email=?", args, null, null, null);

        String s = new String();
        while (c.moveToNext()) {
            s = c.getString(0);
        }

        if (s.equals("")) {
            return false;
        }

        return true;

    }

    public User checkPassword(String mail, String password) {
        openDatabase();
        String[] columns = {"id", "avatar", "name", "surname", "email", "number", "password", "birthday"};

        String[] args = {mail, password};
        Cursor c = DB.query("users", columns, "email=? AND password=?", args, null, null, null);


        User user = null;
        while (c.moveToNext()) {
            user = new User(c.getInt(0), c.getString(1), c.getString(2), c.getString(3), c.getString(4), c.getString(5), c.getString(6), c.getString(7));
        }

        return user;
    }

    private void openDatabase() {
        if (DB == null) {
            DB = getWritableDatabase();
        }
    }
}
