package tr.edu.yildiz.ekremkamaz;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import java.security.NoSuchAlgorithmException;

import tr.edu.yildiz.ekremkamaz.model.User;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText _emailEditText;
    private EditText _passwordEditText;
    private Button _buttonSignIn;
    private Button _buttonSignUp;
    private DatabaseHelper DBHelper;
    int attemp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getDelegate().setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_main);
        _defineVariables();
        _defineListeners();
    }

    private void _defineListeners() {
        _buttonSignIn.setOnClickListener(this);
        _buttonSignUp.setOnClickListener(this);
    }

    private void _defineVariables() {
        attemp = 0;
        _emailEditText = findViewById(R.id.editTextEmailAddress);
        _passwordEditText = findViewById(R.id.editTextPassword);
        _buttonSignIn = findViewById(R.id.buttonSignIn);
        _buttonSignUp = findViewById(R.id.buttonSignUp);
        DBHelper = DatabaseHelper.getInstance(getApplicationContext());
    }

    private User _validateForms() throws NullPointerException {
        String email = _emailEditText.getText().toString();
        String password = _passwordEditText.getText().toString();
        if (!DBHelper.checkMail(email)) {
            _emailEditText.setError("Account is not signed up");
            return null;
        }
        User user = null;
        try {
            user = DBHelper.checkPassword(email, SignUpActivity.toHexString(SignUpActivity.getSHA(password)));

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return user;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonSignIn:
                User _user = _validateForms();
                try {
                    _user.getAvatar();
                } catch (NullPointerException e) {
                    attemp++;
                    if (attemp == 3) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("Uyar??");
                        builder.setMessage("??ifrenizi 3 kez hatal?? girdiniz. ??ye olma sayfas??na gitmek ister misiniz?");
                        builder.setNegativeButton("Hay??r", null);
                        builder.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent _intent = new Intent(MainActivity.this, SignUpActivity.class);
                                startActivity(_intent);
                            }
                        });
                        builder.show();
                        attemp = 0;
                    }
                    _passwordEditText.setError("Wrong password!!!");
                    return;
                }
            {
                Intent _intent = new Intent(MainActivity.this, MenuActivity.class);
                _intent.putExtra("user", _user);
                startActivity(_intent);
                finish();
            }
            break;
            case R.id.buttonSignUp: {
                Intent _intent = new Intent(MainActivity.this, SignUpActivity.class);
                startActivity(_intent);
            }
            break;
            default:
                break;
        }
    }
}