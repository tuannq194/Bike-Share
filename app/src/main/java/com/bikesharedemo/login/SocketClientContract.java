package com.bikesharedemo.login;

import android.util.Log;

import io.reactivex.CompletableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;
public class SocketClientContract {
    private CompositeDisposable compositeDisposable;
    private StompClient mStompClient;
    private static final String TAG = "SocketClientContract";


    public SocketClientContract(){
        this.mStompClient = Stomp.over(Stomp.ConnectionProvider.JWS, "ws://" + Common.DOMAIN
                + ":" + Common.SERVER_PORT + "/example-endpoint/websocket");
        mStompClient.connect();
    }

    private void resetSubscriptions() {
        if (compositeDisposable != null) {
            compositeDisposable.dispose();
        }
        compositeDisposable = new CompositeDisposable();
    }

    public String subscriberStomp(String bikeId) {
        resetSubscriptions();
        Disposable disposable = mStompClient.topic("/topic/greetings/" + bikeId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(topicMessage -> {
                    checkSocketClose.checksocket = topicMessage.getPayload();
                    System.out.println("aaaa " + topicMessage.getPayload());
                }, throwable -> {
                    Log.e(TAG, "Error on subscribe topic", throwable);
                });
        compositeDisposable.add(disposable);
        return checkSocketClose.checksocket;
    }

    public void sendEchoViaStomp(String message) {
//        if (!mStompClient.isConnected()) return;
        compositeDisposable.add(mStompClient.send("/topic/hello-msg-mapping", message)
                .compose(applySchedulers())
                .subscribe(() -> {
                    Log.d(TAG, "STOMP echo send successfully");
                }, throwable -> {
                    Log.e(TAG, "Error send STOMP echo", throwable);
                }));

    }

    protected CompletableTransformer applySchedulers() {
        return upstream -> upstream
                .unsubscribeOn(Schedulers.newThread())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
    public String unSubscribe(){
        if(mStompClient.isConnected()){
            mStompClient.disconnect();
            checkSocketClose.checksocket = "op";
        }
        return checkSocketClose.checksocket;
    }
}
