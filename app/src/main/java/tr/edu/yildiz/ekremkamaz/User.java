package tr.edu.yildiz.ekremkamaz;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
    private int id;
    private String avatar;
    private String name;
    private String surname;
    private String email;
    private String number;
    private String password;
    private String birthday;

    public User(int id, String avatar, String name, String surname, String email, String number, String password, String birthday) {
        this.id = id;
        this.avatar = avatar;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.number = number;
        this.password = password;
        this.birthday = birthday;
    }

    protected User(Parcel in) {
        id = in.readInt();
        avatar = in.readString();
        name = in.readString();
        surname = in.readString();
        email = in.readString();
        number = in.readString();
        password = in.readString();
        birthday = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public int getId() {
        return id;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getEmail() {
        return email;
    }

    public String getNumber() {
        return number;
    }

    public String getPassword() {
        return password;
    }

    public String getBirthday() {
        return birthday;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(avatar);
        parcel.writeString(name);
        parcel.writeString(surname);
        parcel.writeString(email);
        parcel.writeString(number);
        parcel.writeString(password);
        parcel.writeString(birthday);
    }
}
