package tr.edu.yildiz.ekremkamaz;

import android.app.DatePickerDialog;
import android.os.Bundle;


import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView _imageViewIcon;
    private ImageView _imageView;
    private EditText _editTextName;
    private EditText _editTextSurname;
    private EditText _editTextEmail;
    private EditText _editTextPhone;
    private EditText _editTextPassword;
    private EditText _editTextPassword2;
    private TextView _textViewDate;
    private DatePickerDialog.OnDateSetListener _mDateSetListener;
    private Button _buttonCancel;
    private Button _buttonSignUp;
    private String avatarName;
    private DatabaseHelper DBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        _defineVariables();
        _defineListeners();
    }

    private void _defineListeners() {
        _editTextPhone.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        _mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                String date = day + "/" + (month + 1) + "/" + year;
                _textViewDate.setText(date);
            }
        };
        _imageViewIcon.setOnClickListener(this);
        _buttonCancel.setOnClickListener(this);
        _buttonSignUp.setOnClickListener(this);
        _textViewDate.setOnClickListener(this);
    }

    private void _defineVariables() {
        avatarName = "avatar_143";
        _imageViewIcon = findViewById(R.id.imageViewIcon);
        _imageView = findViewById(R.id.imageView);
        _editTextName = findViewById(R.id.editTextName);
        _editTextSurname = findViewById(R.id.editTextSurname);
        _editTextPhone = findViewById(R.id.editTextPhone);
        _editTextEmail = findViewById(R.id.editTextEmailAddress);
        _editTextPassword = findViewById(R.id.editTextPassword);
        _editTextPassword2 = findViewById(R.id.editTextPassword2);
        _textViewDate = findViewById(R.id.textViewDate);
        _buttonCancel = findViewById(R.id.buttonCancel);
        _buttonSignUp = findViewById(R.id.buttonSignUp);
        DBHelper = DatabaseHelper.getInstance(getApplicationContext());
    }

    private boolean _validateForms() {
        boolean formValidation = _validateName() && _validateSurname() && _validateEmail() && _validatePassword();
        if (formValidation) {
            if (DBHelper.checkMail(_editTextEmail.getText().toString())) {
                _editTextEmail.setError("This mail address already signed up");
                return false;
            }
        }
        return formValidation;
    }

    private boolean _validateName() {
        String name = _editTextName.getText().toString();
        if (name.isEmpty()) {
            _editTextName.setError("Field cannot be empty");
            return false;
        } else if (name.length() < 2) {
            _editTextName.setError("Name can not be shorter than 2 letters");
            return true;
        }
        return true;
    }

    private boolean _validateSurname() {
        String surname = _editTextSurname.getText().toString();
        if (surname.isEmpty()) {
            _editTextSurname.setError("Field cannot be empty");
            return false;
        } else if (surname.length() < 2) {
            _editTextSurname.setError("Surname can not be shorter than 2 letters");
            return true;
        }
        return true;
    }

    private boolean _validateEmail() {
        String email = _editTextEmail.getText().toString();
        Pattern pattern = Pattern.compile("^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$");
        if (!pattern.matcher(email).matches()) {
            _editTextEmail.setError("Please type a valid mail");
            return false;
        }
        return true;
    }

    private boolean _validatePassword() {
        String password = _editTextPassword.getText().toString();
        String password2 = _editTextPassword2.getText().toString();
        if (password.equals(password2) == false) {
            _editTextPassword2.setError("Password doesn't match !");
            return false;
        }
        //At least one upper case English letter, (?=.*?[A-Z])
        //At least one lower case English letter, (?=.*?[a-z])
        //At least one digit, (?=.*?[0-9])
        //At least one special character, (?=.*?[#?!@$%^&*-])
        //Minimum eight in length

        Pattern pattern = Pattern.compile("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$");

        if (!pattern.matcher(password).matches()) {
            _editTextPassword.setError("At least one upper case English letter, [A-Z]\n" +
                    "At least one lower case English letter, [a-z]\n" +
                    "At least one digit, [0-9]\n" +
                    "At least one special character, [#?!@$%^&*-]\n" +
                    "Minimum eight in length");
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.imageViewIcon:
                DisplayMetrics metrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(metrics);
                AvatarChooserDialog acg = new AvatarChooserDialog(SignUpActivity.this, metrics);
                acg.show();
                break;
            case R.id.buttonCancel:
                finish();
                break;
            case R.id.buttonSignUp:
                if (_validateForms()) {
                    boolean result = false;
                    try {
                        result = DBHelper.addUser(avatarName,
                                _editTextName.getText().toString(),
                                _editTextSurname.getText().toString(),
                                _editTextEmail.getText().toString().trim(),
                                _editTextPhone.getText().toString(),
                                toHexString(getSHA(_editTextPassword.getText().toString())),
                                _textViewDate.getText().toString()
                        );
                    } catch (NoSuchAlgorithmException e) {
                        Toast.makeText(SignUpActivity.this, "SHA fonksiyon hatası",
                                Toast.LENGTH_LONG).show();
                    }
                    if (result == true) {
                        Toast.makeText(SignUpActivity.this, "User created !!",
                                Toast.LENGTH_LONG).show();
                        finish();
                    }

                }
                break;
            case R.id.textViewDate:
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int mounth = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog(SignUpActivity.this, _mDateSetListener, year, mounth, day);
                dialog.show();
                break;
            default:
                break;
        }
    }


    public static byte[] getSHA(String input) throws NoSuchAlgorithmException {
        // Static getInstance method is called with hashing SHA
        MessageDigest md = MessageDigest.getInstance("SHA-256");

        // digest() method called
        // to calculate message digest of an input
        // and return array of byte
        return md.digest(input.getBytes(StandardCharsets.UTF_8));
    }

    public static String toHexString(byte[] hash) {
        // Convert byte array into signum representation
        BigInteger number = new BigInteger(1, hash);

        // Convert message digest into hex value
        StringBuilder hexString = new StringBuilder(number.toString(16));

        // Pad with leading zeros
        while (hexString.length() < 32) {
            hexString.insert(0, '0');
        }

        return hexString.toString();
    }

    public void setImage(String i) {
        _imageView.setImageResource(getResources().getIdentifier(i, "drawable", getPackageName()));
        avatarName = i;
    }
}

