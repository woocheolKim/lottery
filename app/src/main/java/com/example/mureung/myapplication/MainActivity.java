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

    static int idx = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_text = (TextView)findViewById(R.id.tv_text);

        values = new ArrayList<Integer>();

        // 회차 뽑아오는 통신
        JsoupTask2 jsoupTask2 = new JsoupTask2();
        jsoupTask2.execute();

        // 회차 뽑아오는 통신이 끝나고 실행되어야되서 3초 후 실행
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // 1회부터 마지막회까지 통신 => 초기에만 다 불러오고
                // DB에 저장후에 그 다음부터는 최근 회만 통신 하도록
                // 변경 해야됨
                // TODO
                for (int i = 1; i <= parameter; i++){
                    // 데이터 뽑아와서 계산 하고 텍스트뷰에 보여줌
                    JsoupTask jsoupTask = new JsoupTask();
                    jsoupTask.setParm(i);
                    jsoupTask.execute();
                }
            }
        }, 3000);
    }

    // 조를 뽑아오기위한 통신
    // TODO 초기에만 데이터를 처음부터 다 받아오고 DB에 저장
    // 그 후부터는 DB가 있으면 가장 최근회만 불러옴
    // DB에 각 횟차 정보와 조 데이터를 가지고있어서
    // 디비에있는 최근 횟차와 통신해야될 횟차가 같으면 통신안하는 구조로
    // 해야됨.
    public class JsoupTask extends AsyncTask<Void, Void, Void> {

        private String url = "https://search.naver.com/search.naver?sm=tab_etc&where=nexearch&query=";

        int param;
        Element key1;
        Element key2;

        // 회차 초기화
        public void setParm(int param){
            this.param = param;
        }

        // 계산 알고리즘 => 평균? or 가장 많이나온 수치
        public int calculate(List<Integer> lists){
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

            if (param <= parameter){
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
                values.add(Integer.valueOf(key1.text().charAt(0)));
                values.add(Integer.valueOf(key2.text().charAt(0)));
            }

                tv_text.setText(calculate(values) + "조");
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
