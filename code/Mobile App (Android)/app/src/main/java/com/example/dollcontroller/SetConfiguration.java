package com.example.dollcontroller;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class SetConfiguration extends AppCompatActivity {

    private ListView configuration_list;
    private ArrayList<String> emotion_items;

    private Button back_button;

    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.setup_configuration);

        configuration_list = findViewById(R.id.configuration_list);
        back_button = findViewById(R.id.setup_button);

        Setup_Configuration_list();
        Initialize_Set_and_Back_button();
    }

    private void Setup_Configuration_list() {
        emotion_items = new ArrayList<>();
        for (String emotion : EmotionDefinition.Emotions)
            emotion_items.add(EmotionDefinition.Get_Action_Command(emotion));

        ActionListItem adapter = new ActionListItem(this, emotion_items);
        configuration_list.setAdapter(adapter);
    }

    private void Initialize_Set_and_Back_button() {
        back_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if (Set_Emotion_Action_Configuration())
                    finish();
            }
        });
    }

    private boolean Set_Emotion_Action_Configuration() {
        try
        {
            for (int index = 0; index < EmotionDefinition.Emotions.length; index++)
                EmotionDefinition.Set_Action_Angle(index, emotion_items.get(index));
        }
        catch (Exception e)
        {
            Show_Alert_Dialog(e);
            return false;
        }

        return true;
    }

    private void Show_Alert_Dialog(Exception e) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Alert");
        builder.setMessage("There is inappropriate command set\n" + e.getMessage());

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
