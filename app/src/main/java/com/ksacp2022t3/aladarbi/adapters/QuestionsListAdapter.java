package com.ksacp2022t3.aladarbi.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ksacp2022t3.aladarbi.R;
import com.ksacp2022t3.aladarbi.models.Question;

import java.util.List;

public class QuestionsListAdapter extends RecyclerView.Adapter<QuestionItem> {
    List<Question> questions;

    public QuestionsListAdapter(List<Question> questions) {
        this.questions = questions;
    }

    @NonNull
    @Override
    public QuestionItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_question,parent,false);
        return new QuestionItem(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionItem holder, int position) {
        Question question=questions.get(position);
            holder.txt_question.setText(question.getText());
        holder.txt_answer.setText(question.getAnswer());
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }
}

class QuestionItem extends RecyclerView.ViewHolder {
        TextView txt_question,txt_answer;

    public QuestionItem(@NonNull View itemView) {
        super(itemView);
        txt_question=itemView.findViewById(R.id.txt_question);
        txt_answer=itemView.findViewById(R.id.txt_answer);
    }
}
