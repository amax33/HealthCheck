package com.example.healthcheck;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.ramijemli.percentagechartview.PercentageChartView;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import pl.droidsonroids.gif.GifImageView;

public class Show extends AppCompatActivity {
    Uri file = MainActivity.path;//information file
    TextView text1, text2, text3, text4;
    PercentageChartView chart;
    GifImageView gifHot, gifCold;
    String information = null;
    Button sendinfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show);
        String result = readFromFile(file);
        String[] info = result.split("\\n");
        //setting up part of layout
        text1 = this.findViewById(R.id.text1);
        text2 = this.findViewById(R.id.text2);
        text3 = this.findViewById(R.id.text3);
        text4 = this.findViewById(R.id.text4);
        text1.setText("\n"+"ضربان قلب:" + "\n" +info[0]);
        text2.setText("\n"+"میزان اکسیژن:" + "\n" + info[1] + "%");
        text3.setText("\n"+"دمای بدن:" + "\n" + info[2] + "°");
        chart = this.findViewById(R.id.percent);
        sendinfo = this.findViewById(R.id.sendsmsbtn);
        //setting oxygen diagram:
        //blue if it's good, yellow if it's not normal, red if it's dangerous.
        if(Integer.parseInt(info[1])>90) {
            chart.textColor(Color.WHITE)
                    .textSize(60)
                    .textShadow(Color.BLACK, 2f, 2f, 2f)
                    .progressColor(Color.BLUE)
                    .backgroundColor(Color.BLACK)
                    .startAngle(270)
                    .apply();
        } else if(Integer.parseInt(info[1])>=80){
            chart.textColor(Color.WHITE)
                    .textSize(60)
                    .textShadow(Color.BLACK, 2f, 2f, 2f)
                    .progressColor(Color.YELLOW)
                    .backgroundColor(Color.BLACK)
                    .startAngle(270)
                    .apply();
        } else if(Integer.parseInt(info[1])<80){
            chart.textColor(Color.WHITE)
                    .textSize(60)
                    .textShadow(Color.BLACK, 2f, 2f, 2f)
                    .progressColor(Color.RED)
                    .backgroundColor(Color.BLACK)
                    .startAngle(270)
                    .apply();
        }
        chart.setProgress(Float.parseFloat(info[1]),false);
        //setting temperature gif, red and blue.
        gifHot = this.findViewById(R.id.gif_tempHot);
        gifCold = this.findViewById(R.id.gif_tempCold);
        if(Float.parseFloat(info[2])<=37.5){
            gifHot.setVisibility(View.GONE);
            gifCold.setVisibility(View.VISIBLE);
        }else{
            gifCold.setVisibility(View.GONE);
            gifHot.setVisibility(View.VISIBLE);
        }
        //sending the total overview of the patient:
        if(Float.parseFloat(info[2])<=37.5 && Integer.parseInt(info[1])>90 && Integer.parseInt(info[0])>=80
                && Integer.parseInt(info[0])<=120) {
            text4.setText("\n" + "بیمار در وضعیت مناسبی است.");
            information = String.valueOf(text4.getText());
        } else{
            //if patient is not good:
            sendinfo.setTextColor(getResources().getColor(R.color.red));
            StringBuilder situation = new StringBuilder();
            information = "بیمار در وضعیت مناسبی نیست.";
            if(Integer.parseInt(info[0])<80){
                situation.append( "ضربان قلب بیمار پایین است."+"\n");
            }
            if(Integer.parseInt(info[0])>120){
                situation.append( "ضربان قلب بیمار بالا است."+"\n");
            }
            if(Integer.parseInt(info[1])<90){
                situation.append( "میزان اکسیژن خون بیمار کم است."+"\n");
            }
            if(Float.parseFloat(info[2])>37.5){
                situation.append( "دمای بدن بیمار بالا است."+"\n");
            }
            text4.setTextSize(10);
            text4.setText(situation.toString());

        }

        //going to contact to send information
        sendinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), contact.class)
                        .putExtra("information", information));
            }
        });
    }
    //reading information from the chosen file
    public String readFromFile(Uri file) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            InputStream fs = getContentResolver().openInputStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(fs));
            for(int i = 1; i <= 3; i++) {//there is only 3 line of information
                stringBuilder.append(br.readLine()).append("\n");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }
}