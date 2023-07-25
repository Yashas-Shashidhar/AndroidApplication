package com.example.tipsplit;
import android.text.TextUtils;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    EditText TotalBillInput,PeopleCount;
    TextView TipAmount, TotalAmount,Totalperperson;
    RadioButton percentage_12, percentage_15, percentage_18, percentage_20;
    Button calc,clear;
    RadioGroup mradiogroup;
    float final_amt=0.0f;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TotalBillInput = (EditText) findViewById(R.id.TotalBillInput);
        PeopleCount= (EditText) findViewById(R.id.PeopleCount);
        TipAmount = (TextView) findViewById(R.id.TipAmount);
        TotalAmount=(TextView) findViewById(R.id.Totalwithtip);
        percentage_12 = (RadioButton) findViewById(R.id.rdbpercentage_12);
        percentage_15 = (RadioButton) findViewById(R.id.rdbpercentage_15);
        percentage_18 = (RadioButton) findViewById(R.id.rdbpercentage_18);
        percentage_20 = (RadioButton) findViewById(R.id.rdbpercentage_20);
        calc=(Button) findViewById(R.id.calculate);
        clear=(Button) findViewById(R.id.clear);
        Totalperperson=(TextView) findViewById(R.id.Totalperperson);
        mradiogroup=(RadioGroup) findViewById(R.id.radiogroup);
        calc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                int peoplecount=Integer.parseInt(PeopleCount.getText().toString());
//                int peoplecount=4;
                float perpersonbill=final_amt/peoplecount;
                String final_display=String.format("%.2f", perpersonbill);
                Totalperperson.setText("$"+final_display);
            }

        });
        clear.setOnClickListener(new Button.OnClickListener()

        {
            public void onClick
                    (View v) {
                TipAmount.setText("");
                TotalAmount.setText("");
                Totalperperson.setText("");
                TotalBillInput.setText("");
                PeopleCount.setText("");
                mradiogroup.clearCheck();
            }
        });

        if (savedInstanceState!=null){
            String savedtipamount=savedInstanceState.getString("KEY_tipamount");
            TipAmount.setText(savedtipamount);

            String savedtotalamount=savedInstanceState.getString("KEY_totalamount");;
            TotalAmount.setText(savedtotalamount);
            String savedtotalperson=savedInstanceState.getString("KEY_totalperson");;
            Totalperperson.setText(savedtotalperson);


        }

    }
    public void onRadioButtonClicked (View view) {

        String TotalBillIn1=TotalBillInput.getText().toString();
        if(TextUtils.isEmpty(TotalBillInput.getText().toString()))
        {
            percentage_12.setChecked(false);
            percentage_15.setChecked(false);
            percentage_18.setChecked(false);
            percentage_20.setChecked(false);
            return;

        }
        if (TotalBillIn1.contains("$"))
        {
            TotalBillIn1=TotalBillIn1.substring(1);
        }

        float TotalBillIn= Float.parseFloat(TotalBillIn1);
        float tip_amt=0.0f;
        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.rdbpercentage_12:
                tip_amt=TotalBillIn*0.12f;
                break;
            case R.id.rdbpercentage_15:
                tip_amt=TotalBillIn*0.15f;
                break;
            case R.id. rdbpercentage_18:
                tip_amt=TotalBillIn*0.18f;
                break;
            case R.id.rdbpercentage_20:
                tip_amt=TotalBillIn*0.2f;
                break;

        }
        String val_display1=String.format("%.2f", tip_amt);
        TipAmount.setText("$"+val_display1);
        final_amt=tip_amt+TotalBillIn;
        String val_display2=String.format("%.2f", final_amt);
        TotalAmount.setText("$"+val_display2);
        String val_display3=String.valueOf(TotalBillIn);
        TotalBillInput.setText("$"+val_display3);
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString("KEY_tipamount", TipAmount.getText().toString());
        savedInstanceState.putString("KEY_totalamount", TotalAmount.getText().toString());
        savedInstanceState.putString("KEY_totalperson",Totalperperson.getText().toString());

        super.onSaveInstanceState(savedInstanceState);
    }



    protected void saveView (View view) {
        TipAmount.setText(TipAmount.getText().toString().trim());
        TotalAmount.setText(TotalAmount.getText().toString());
        Totalperperson.setText(Totalperperson.getText().toString());

    }


}