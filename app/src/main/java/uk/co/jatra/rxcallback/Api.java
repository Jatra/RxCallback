package uk.co.jatra.rxcallback;

public class Api {

    public static final int INTERRUPTED = 78;

    interface Callback {
        void onSuccess(String answer);
        void onFailure(int status);
    }

    public int ask(String question, final Callback callback) {
            new Thread() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(2000);
                        if ("what's the meaning of life, the universe and everything" .equals(question)) {
                            callback.onSuccess("Dont know");
                        } else {
                            callback.onSuccess("Dont know");
                        }
                    } catch (InterruptedException e) {
                        callback.onFailure(INTERRUPTED);
                    }
                }

            }.start();
        return 0;
    }
}
