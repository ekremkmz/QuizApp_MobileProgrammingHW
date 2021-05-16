package tr.edu.yildiz.ekremkamaz;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.net.URLConnection;
import java.util.ArrayList;

import tr.edu.yildiz.ekremkamaz.model.Exam;

public class ExamActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int ADD_EXAM = 1;
    RecyclerView examRecyclerView;
    FloatingActionButton add;
    ArrayList<Exam> examArrayList;
    DatabaseHelper DBHelper;
    int user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam);
        defineVariables();
        defineListeners();
    }


    private void defineVariables() {
        examRecyclerView = findViewById(R.id.examRecyclerView);
        add = findViewById(R.id.addExam);
        DBHelper = DatabaseHelper.getInstance(ExamActivity.this);
        user_id = getIntent().getIntExtra("user_id", 1);
        examArrayList = DBHelper.getExams(user_id);
        examRecyclerView.setAdapter(new CustomAdapter(examArrayList));
        examRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void defineListeners() {
        add.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.addExam: {
                Intent _intent = new Intent(ExamActivity.this, AddExamActivity.class);
                startActivityForResult(_intent, ADD_EXAM);
            }
            break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case ADD_EXAM: {
                    Exam exam = data.getParcelableExtra("exam");
                    ((CustomAdapter) examRecyclerView.getAdapter()).addExam(exam);
                }
                break;
                default:
                    break;
            }
        }
    }

    private class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {
        ArrayList<Exam> examArrayList;

        public CustomAdapter(ArrayList<Exam> examArrayList) {
            this.examArrayList = examArrayList;
        }

        @NonNull
        @NotNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);

            View exam = inflater.inflate(R.layout.item_list_exam, parent, false);

            ViewHolder viewHolder = new ViewHolder(exam);

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
            Exam exam = examArrayList.get(position);
            holder.listExamItemTitle.setText(exam.getTitle());
        }

        @Override
        public int getItemCount() {
            return examArrayList.size();
        }

        private class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView listExamItemTitle;
            MaterialButton listExamItemRemoveButton;
            MaterialButton listExamItemShareButton;

            public ViewHolder(@NonNull @NotNull View itemView) {
                super(itemView);
                listExamItemTitle = itemView.findViewById(R.id.listExamItemTitle);
                listExamItemRemoveButton = itemView.findViewById(R.id.listExamItemRemoveButton);
                listExamItemShareButton = itemView.findViewById(R.id.listExamItemShareButton);
                listExamItemRemoveButton.setOnClickListener(this);
                listExamItemShareButton.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                int position = getAdapterPosition();
                switch (view.getId()) {
                    case R.id.listExamItemRemoveButton: {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ExamActivity.this);
                        builder.setTitle("Uyarı");
                        builder.setMessage("Sınav silinecek. Emin misiniz?");
                        builder.setNegativeButton("Hayır", null);
                        builder.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                deleteExam(position);
                            }
                        });
                        builder.show();
                    }
                    break;
                    case R.id.listExamItemShareButton: {
                        try {
                            File file = examArrayList.get(position).toFile(getApplicationContext());
                            Intent _intent = new Intent();
                            _intent.setAction(Intent.ACTION_SEND);
                            _intent.setType("text/plain");
                            _intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));

                            startActivity(Intent.createChooser(_intent, "Sınavı paylaş"));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                    default:
                        break;
                }
            }
        }

        private void deleteExam(int position) {
            Exam exam = examArrayList.get(position);
            DBHelper.deleteExam(exam);
            examArrayList.remove(exam);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, examArrayList.size());
        }

        public void addExam(Exam exam) {
            examArrayList.add(exam);
            notifyItemInserted(examArrayList.size() - 1);
        }
    }
}