package tr.edu.yildiz.ekremkamaz;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;

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
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS users");
        onCreate(db);
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
        File destFile = new File(activity.getFilesDir().getPath() +"/"+ question.getUser_Id() + "_" + question.getId());
        cv.put("content_type", question.getContent_type());
        cv.put("content_path", destFile.getPath());
        copyFile(new File(question.getContent_path()), destFile);

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
