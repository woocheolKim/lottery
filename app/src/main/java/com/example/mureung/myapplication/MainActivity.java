package com.example.mureung.myapplication;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.os.Handler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {


    Document doc;
    TextView tv_text;
    String text = "";

    int parameter;

    ArrayList<Integer> values;

    static int idx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        idx = 1;

        tv_text = (TextView)findViewById(R.id.tv_text);

        values = new ArrayList<Integer>();

        JsoupTask2 jsoupTask2 = new JsoupTask2();
        jsoupTask2.execute();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                for (int i = 1; i <= parameter; i++){
                    JsoupTask jsoupTask = new JsoupTask();
                    jsoupTask.setParm(i);
                    jsoupTask.execute();
                }
            }
        }, 3000);
    }

    // 조를 뽑아오기위한 통신
    public class JsoupTask extends AsyncTask<Void, Void, Void> {

        private String url = "https://search.naver.com/search.naver?sm=tab_etc&where=nexearch&query=";

        int param;
        Element key1;
        Element key2;

        // 회차 초기화
        public void setParm(int param){
            this.param = param;
        }

        public int calculateAverage(List<Integer> lists){
            Integer sum = 0;

            if (!lists.isEmpty()){
                for (Integer list : lists){
                    sum += list;
                }
                return (sum / lists.size()) / 7;
            }
            return sum;
        }

        protected Void doInBackground(Void... nothing){
            try{
                doc = Jsoup.connect(url + param + "회연금복권").get();

                key1 = doc.select("ul.win_num").select("li").select("span").get(0);
                key2 = doc.select("ul.win_num").select("li").get(1).select("span").get(0);

            }catch (Exception e){
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            if (param != parameter){
                switch (idx){
                    case 1:
                        tv_text.setText("계산중.");
                        idx++;
                        break;
                    case 2:
                        tv_text.setText("계산중..");
                        idx++;
                        break;
                    case 3:
                        tv_text.setText("계산중...");
                        idx = 1;
                        break;
                }

                values.add(Integer.valueOf(key1.text().charAt(0)));
                values.add(Integer.valueOf(key2.text().charAt(0)));
            }else{
                if (param == parameter){
                    switch (idx){
                        case 1:
                            tv_text.setText("계산중.");
                            idx++;
                            break;
                        case 2:
                            tv_text.setText("계산중..");
                            idx++;
                            break;
                        case 3:
                            tv_text.setText("계산중...");
                            idx = 1;
                            break;
                    }

                    values.add(Integer.valueOf(key1.text().charAt(0)));
                    values.add(Integer.valueOf(key2.text().charAt(0)));
                }

                tv_text.setText(calculateAverage(values) + "조");
            }
        }
    }

    // 회차를 뽑아오기위한 통신
    public class JsoupTask2 extends AsyncTask<Void, Void, Void> {

        private String url = "https://search.naver.com/search.naver?sm=tab_etc&where=nexearch&query=연금복권";

        Element key;

        protected Void doInBackground(Void... nothing){

            try{
                doc = Jsoup.connect(url).get();

                key = doc.select("a._lottery-btn-current").select("em").first();

            }catch (Exception e){
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            parameter = Integer.parseInt(key.text().substring(0, 3));
        }
    }
}
