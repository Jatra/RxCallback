package uk.co.jatra.rxcallback;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

public class MainActivity extends AppCompatActivity {

    private static final boolean USE_RX = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText questionView = (EditText) findViewById(R.id.question);
        Button ask = (Button) findViewById(R.id.ask);
        final TextView answerView = (TextView)findViewById(R.id.answer);



        if (!USE_RX) {

            ask.setOnClickListener(v -> {
                answerView.setText("...thinking...");

                new Api().ask(questionView.getText().toString(), new Api.Callback() {
                    @Override
                    public void onSuccess(String answer) {
                        runOnUiThread(() -> answerView.setText(answer));
                    }

                    @Override
                    public void onFailure(int status) {
                        runOnUiThread(() -> answerView.setText("ERRROOOORRR " + status));
                    }
                });
            });
        }
        else {

            ask.setOnClickListener(v -> {
                answerView.setText("...thinking...");

                rxAsk(questionView.getText().toString())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                answer -> answerView.setText(answer),
                                error -> answerView.setText(error.getMessage()));
            });


        }

    }

    Observable<String> rxAsk(String question) {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                new Api().ask(question, new Api.Callback() {
                    @Override
                    public void onSuccess(String answer) {
                        subscriber.onNext(answer);
                    }

                    @Override
                    public void onFailure(int status) {
                        subscriber.onError(new AskError(status));
                    }
                });
            }
        });
    }

    class AskError extends RuntimeException {
        private int status;

        public AskError(int status) {
            this.status = status;
        }

        public int getStatus() {
            return status;
        }

        @Override
        public String getMessage() {
            return "ERRROOOORRR " + status;
        }
    }
}
