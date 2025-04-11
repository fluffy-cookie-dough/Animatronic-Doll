package com.example.dollcontroller;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import java.util.List;

public class ActionListItem extends ArrayAdapter<String> {
    private final Context context;
    private final List<String> values;

    public ActionListItem(Context context, List<String> values) {
        super(context, R.layout.list_item, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int index, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row_view = inflater.inflate(R.layout.list_item, parent, false);

        EditText edit_text = row_view.findViewById(R.id.item_edit_text);

        edit_text.setText(values.get(index));
        edit_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { values.set(index, s.toString()); }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void afterTextChanged(Editable s) { }
        });
        edit_text.setHint("Enter [" + EmotionDefinition.Emotions[index] + "] Value");

        return row_view;
    }
}
