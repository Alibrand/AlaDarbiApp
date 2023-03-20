package com.ksacp2022t3.aladarbi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ksacp2022t3.aladarbi.adapters.FilteredQuestionsListAdapter;
import com.ksacp2022t3.aladarbi.adapters.QuestionsListAdapter;
import com.ksacp2022t3.aladarbi.models.Question;

import java.util.ArrayList;
import java.util.List;

public class ChatBotActivity extends AppCompatActivity {
    EditText txt_your_question;

    RecyclerView recycler_questions_list, recycler_filtered;

    List<Question> questionList=new ArrayList<>();
    List<Question> chat_list=new ArrayList<>();
    List<Question> filteredList;
    ImageView btn_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_bot);
        txt_your_question = findViewById(R.id.txt_your_question);
        recycler_questions_list = findViewById(R.id.recycler_questions_list);
        recycler_filtered = findViewById(R.id.recycler_filtered);
        btn_back = findViewById(R.id.btn_back);


        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
         
        

        init_question_list();

        txt_your_question.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String query= txt_your_question.getText().toString();
                if(query.isEmpty())
                {
                    recycler_filtered.setVisibility(View.GONE);
                    return;
                }
                filteredList =new ArrayList<>();
                for (Question quest:questionList
                ) {
                    if(quest.getText().contains(query)
                            || quest.getAnswer().contains(query))
                        filteredList.add(quest);

                }
                if(filteredList.size()>0)
                {
                    recycler_filtered.setVisibility(View.VISIBLE);
                    FilteredQuestionsListAdapter adapter=new FilteredQuestionsListAdapter(filteredList,ChatBotActivity.this);
                    recycler_filtered.setAdapter(adapter);
                }
                else{
                    recycler_filtered.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    private void init_question_list() {
        questionList.add(new Question("كيف يتم احتساب كلفة الرحلة؟","يتم احتساب 3 ريال سعودي لكل 1كم من الرحلة"));
        questionList.add(new Question("هل يمكن إضافة أكثر من سيارة للسائق؟","من خلال واجهة رحلاتي يمكنك إضافة رحلاتك اليومية أو الرحلات المفردة ويمكنك تعيين سيارة مختلفة لكل رحلة"));
        questionList.add(new Question("هل يتم تقسيم التكلفة على عدد الأشخاص؟","نعم يقوم التطبيق تلقائيا بتقسيم كلفة الرحلة على عدد المقاعد المحجوزة "));
        questionList.add(new Question("ماهي آلية الدفع؟","يمكنك الدفع عند نهاية الرحلة"));
        questionList.add(new Question("هل يمكن إلغاء الحجز في يوم الرحلة؟","نعم يمكنك إلغاء الحجز في اي وقت طالما ان الرحلة لم تنتهي"));
        questionList.add(new Question("هل يمكن الاتصال بالسائق قبل الحجز؟","لا يتم عرض بيانات الاتصال والاسماء الا بعد الحجز تبعا للخصوصية"));

    }

    public void add_question(Question question) {

        chat_list.add(question);
        QuestionsListAdapter adapter=new QuestionsListAdapter(chat_list);
        recycler_questions_list.setAdapter(adapter);
        recycler_filtered.setVisibility(View.GONE);
        txt_your_question.setText("");
        recycler_questions_list.scrollToPosition(chat_list.size()-1);
    }

}