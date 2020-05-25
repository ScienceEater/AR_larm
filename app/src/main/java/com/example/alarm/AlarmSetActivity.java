package com.example.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AlarmSetActivity extends AppCompatActivity {

    private View view;
    AlarmDB pdb;
    Context mContext;

    private Calendar calendar;
    static int cnt = 3;
    private TimePicker timePicker;
    //public static TabFragment_Alarm

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_set);

        this.calendar = Calendar.getInstance();
        // 현재 날짜 표시
        displayDate();

        this.timePicker = findViewById(R.id.timePicker);
        //Calender, 알람 버튼에 리스너 연결
        findViewById(R.id.btnCalendar).setOnClickListener(mClickListener);
        findViewById(R.id.btnAlarm).setOnClickListener(mClickListener);


    }

    /* 날짜 표시 */
    private void displayDate() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        //((TextView) findViewById(R.id.txtDate)).setText(format.format(this.calendar.getTime()));
    }

    /* 알람 등록 */
    private void setAlarm() {
        //DB실험
        mContext = getApplicationContext();
        pdb = new AlarmDB(mContext); // SizerDB 연동 클래스 인스턴스


        // 알람 시간 설정
        this.calendar.set(Calendar.HOUR_OF_DAY, this.timePicker.getHour());
        this.calendar.set(Calendar.MINUTE, this.timePicker.getMinute());
        this.calendar.set(Calendar.SECOND, 0);

        // 현재일보다 이전이면 등록 실패
        if (this.calendar.before(Calendar.getInstance())) {
            Toast.makeText(getApplicationContext(), "알람시간이 현재시간보다 이전일 수 없습니다.", Toast.LENGTH_LONG).show();
            return;
        }

        /**
         요일 추가하는거(레이아웃 완성되면 추가 할 부분), 비트 연산으로 해당 요일값 추가
         int week;
         week+=1<<view.findViewId(R.id.btnWeek1) //일요일
         week+=1<<view.findViewId(R.id.btnWeek2) //월요일
         week+=1<<view.findViewId(R.id.btnWeek3)
         week+=1<<view.findViewId(R.id.btnWeek4)
         week+=1<<view.findViewId(R.id.btnWeek5)
         week+=1<<view.findViewId(R.id.btnWeek6)
         week+=1<<view.findViewId(R.id.btnWeek7) //토요일

         */

        /**
         Receiver 설정
         Receiver에 알람이 울릴 요일을 전달. 1이 일요일, 7이 토요일 의미!
         Receiver에 PID 전달

         **/

        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);

        intent.putExtra("week",calendar.get(Calendar.DAY_OF_WEEK));
        intent.putExtra("pid",pdb.SelectPid()+1);
        //Fragment fragment =new TabFragment_Info();
        Bundle bundle =new Bundle();
        bundle.putInt("cnt",cnt);
        //fragment.setArguments(bundle);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), cnt++, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        //알람 DB에 저장하기
        pdb.insert(pdb.SelectPid()+1,calendar.get(Calendar.DAY_OF_WEEK),1,1,1,"첫 생성입니다",1,this.timePicker.getHour(),this.timePicker.getMinute());
        int[][] t=pdb.SelectTime(pdb.SelectPid());
        Log.d("AlarmDB",Integer.toString(pdb.SelectPid())+Integer.toString(t[0][0])+"요일"+Integer.toString(t[0][1])+"시"+Integer.toString(t[0][2])+"분");
        // 알은 24시간만다 반복되도록 설정
        AlarmManager alarmManager = (AlarmManager) getSystemService(getApplicationContext().ALARM_SERVICE);
        long oneday = 24 * 60 * 60 * 1000;// 24시간 마다 반복
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, this.calendar.getTimeInMillis(),oneday, pendingIntent);

        // Toast 보여주기 (알람 시간 표시)
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Toast.makeText(getApplicationContext(), "Alarm : "+Integer.toString(cnt) + format.format(calendar.getTime()), Toast.LENGTH_LONG).show();
    }

    View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnAlarm:
                    // 알람 등록
                    setAlarm();

                    break;
            }
        }
    };

}
