package tr.edu.yildiz.ekremkamaz;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nambimobile.widgets.efab.FabOption;

import java.io.IOException;
import java.util.ArrayList;

public class AddQuestionActivity extends AppCompatActivity implements View.OnClickListener {
    LinearLayout choices;
    LinearLayout content;
    FloatingActionButton addItem;
    FloatingActionButton save;
    FloatingActionButton removeContent;
    EditText editTextTitle;
    DatabaseHelper DBHelper;
    Question question;
    FabOption addImage;
    FabOption addVoice;
    FabOption addVideo;
    TextView contentType;
    TextView contentPath;
    int userId = 1;
    boolean edit = false;
    static final int IMAGE_REQUEST = 1;
    static final int AUDIO_REQUEST = 2;
    static final int VIDEO_REQUEST = 3;
    static final int STORAGE_REQUEST = 4;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_question);
        defineVariables();
        defineListeners();
        edit = getIntent().hasExtra("question");
        if (edit) {
            question = getIntent().getParcelableExtra("question");
            question.getChoices().forEach((item) -> {
                addNewItem(item);
            });
            userId = question.getUser_Id();
            editTextTitle.setText(question.getTitle());
            ((RadioButton) choices.getChildAt(question.getAnswer()).findViewById(R.id.radio)).setChecked(true);
            //TODO implement content
        } else {
            //userId = getIntent().getExtras().getInt("user_id");
            addNewItem("");
            addNewItem("");
            content.setVisibility(View.GONE);
        }
    }

    private void defineVariables() {
        choices = (LinearLayout) findViewById(R.id.choices);
        content = (LinearLayout) findViewById(R.id.content);
        addItem = (FloatingActionButton) findViewById(R.id.addItem);
        save = (FloatingActionButton) findViewById(R.id.save);
        editTextTitle = findViewById(R.id.editTextTitle);
        addImage = findViewById(R.id.addImage);
        addVideo = findViewById(R.id.addVideo);
        addVoice = findViewById(R.id.addAudio);
        removeContent = findViewById(R.id.removeContent);
        DBHelper = DatabaseHelper.getInstance(getApplicationContext());
        contentPath = findViewById(R.id.contentPath);
        contentType = findViewById(R.id.contentType);

    }

    private void defineListeners() {
        addItem.setOnClickListener(this);
        save.setOnClickListener(this);
        addImage.setOnClickListener(this);
        addVideo.setOnClickListener(this);
        addVoice.setOnClickListener(this);
        removeContent.setOnClickListener(this);
    }

    void addNewItem(String choice) {
        View view = getLayoutInflater().inflate(R.layout.add_question_item, choices, false);
        view.findViewById(R.id.removeItem).setOnClickListener(this);
        view.findViewById(R.id.radio).setOnClickListener(this);
        ((EditText) view.findViewById(R.id.choice)).setText(choice);
        choices.addView(view);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.save:
                try {
                    saveQuestion();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.addItem:
                addNewItem("");
                if (choices.getChildCount() >= 5) {
                    addItem.setEnabled(false);
                }
                break;
            case R.id.removeItem:
                choices.removeView((View) view.getParent());
                if (choices.getChildCount() < 5) {
                    addItem.setEnabled(true);
                }
                break;
            case R.id.radio:
                for (int i = 0; i < choices.getChildCount(); i++) {
                    RadioButton rd = (RadioButton) choices.getChildAt(i).findViewById(R.id.radio);
                    if (rd != view) {
                        rd.setChecked(false);
                    }
                }
                break;
            case R.id.removeContent: {
                content.removeViewAt(0);
                content.setVisibility(View.GONE);
            }
            break;
            case R.id.addImage: {
                if (isStoragePermissionGranted()) {
                    Intent _intent = new Intent(Intent.ACTION_PICK);
                    _intent.setType("image/*");
                    startActivityForResult(_intent, IMAGE_REQUEST);
                } else {
                    requestStoragePermission();
                }
            }
            break;
            case R.id.addAudio: {

                if (isStoragePermissionGranted()) {
                    Intent _intent = new Intent(Intent.ACTION_PICK);
                    _intent.setType("audio/*");
                    startActivityForResult(_intent, AUDIO_REQUEST);
                } else {
                    requestStoragePermission();
                }
            }
            break;
            case R.id.addVideo: {
                if (isStoragePermissionGranted()) {
                    Intent _intent = new Intent(Intent.ACTION_PICK);
                    _intent.setType("video/*");
                    startActivityForResult(_intent, VIDEO_REQUEST);
                } else {
                    requestStoragePermission();
                }
            }
            break;
            default:
                break;
        }
    }

    public boolean isStoragePermissionGranted() {
        return (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
    }

    public void requestStoragePermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (!contentType.getText().equals("")) {
                content.removeViewAt(0);
                contentType.setText("");
                contentPath.setText("");
            }
            Uri uri = data.getData();
            switch (requestCode) {
                case IMAGE_REQUEST: {
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                        ImageView imageView = new ImageView(this);
                        imageView.setImageBitmap(bitmap);
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(300, 300);
                        layoutParams.setMargins(10, 10, 10, 10);
                        imageView.setLayoutParams(layoutParams);
                        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                        imageView.setImageBitmap(bitmap);
                        content.addView(imageView, 0);
                        content.setVisibility(View.VISIBLE);
                        contentType.setText("Image");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
                case VIDEO_REQUEST: {

                    FrameLayout frameLayout = new FrameLayout(AddQuestionActivity.this);

                    VideoView videoView = new VideoView(this);
                    FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.MATCH_PARENT);

                    layoutParams.setMargins(10, 10, 10, 10);
                    videoView.setLayoutParams(layoutParams);

                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
                    lp.setMargins(50, 50, 50, 0);
                    frameLayout.setLayoutParams(lp);

                    MediaController mediaController = new MediaController(videoView.getContext());
                    mediaController.setAnchorView(videoView);
                    mediaController.setMediaPlayer(videoView);

                    videoView.setVideoURI(uri);
                    videoView.setMediaController(mediaController);

                    frameLayout.addView(videoView);

                    content.addView(frameLayout, 0);
                    contentType.setText("Video");

                    content.setVisibility(View.VISIBLE);
                }
                break;
            }
            String[] proj = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(uri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(column_index);
            contentPath.setText(path);
            cursor.close();
        }

    }

    private void saveQuestion() throws IOException {
        String title = editTextTitle.getText().toString();
        ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < choices.getChildCount(); i++) {
            String s = ((EditText) choices.getChildAt(i).findViewById(R.id.choice)).getText().toString();
            list.add(s);
        }
        int answer = -1;
        for (int i = 0; i < choices.getChildCount(); i++) {
            if (((RadioButton) choices.getChildAt(i).findViewById(R.id.radio)).isChecked()) {
                answer = i;
            }
        }
        if (answer == -1) {
            Toast.makeText(this, "Doğru şıkkı işaretleyiniz", Toast.LENGTH_LONG).show();
            return;
        }
        String content_type = contentType.getText().toString();
        String content_path = contentPath.getText().toString();
        boolean result;
        if (edit) {
            Question editedQ = new Question(title, list.size(), list, answer, content_type, content_path, question.getId(), userId);
            result = DBHelper.updateQuestion(editedQ);
            Intent _intent = new Intent();
            _intent.putExtra("question", editedQ);
            _intent.putExtra("position", getIntent().getExtras().getInt("position"));
            setResult(Activity.RESULT_OK, _intent);
        } else {
            result = DBHelper.addQuestion(new Question(title, list.size(), list, answer, content_type, content_path, 0, userId), this);
        }

        if (result) {
            Toast.makeText(this, edit ? "Soru güncellendi" : "Soru kaydedildi", Toast.LENGTH_LONG).show();
            finish();
        } else {
            Toast.makeText(this, edit ? "Soru güncellenemedi!!!" : "Soru kaydedilemedi!!!", Toast.LENGTH_LONG).show();
        }
    }

}