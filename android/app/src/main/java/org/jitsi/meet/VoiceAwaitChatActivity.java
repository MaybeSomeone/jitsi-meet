package org.jitsi.meet;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.react.modules.core.PermissionListener;

import org.jitsi.meet.sdk.JitsiMeet;
import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetActivityDelegate;
import org.jitsi.meet.sdk.JitsiMeetActivityInterface;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.jitsi.meet.sdk.JitsiMeetFragment;
import org.jitsi.meet.sdk.JitsiMeetView;
import org.jitsi.meet.sdk.JitsiMeetViewListener;
import org.jitsi.meet.NotificationModule;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 单聊
 * 语音聊天等待页面
 * liglig  2019/5/20
 */
public class VoiceAwaitChatActivity  extends FragmentActivity  implements View.OnClickListener, JitsiMeetActivityInterface ,JitsiMeetViewListener {

    private ImageView iv_chat_avatar;
    private TextView tv_name,tv_text1,tv_text2;
    private LinearLayout ll_inShow,ll_toShow,ll_accept;
    private  TextView tv_jj,tv_jt,tv_qx,tv_acceptJY,tv_acceptGD,tv_acceptMT,tv_acceptTime;
    private  FrameLayout fragment;
    private  JitsiMeetView jitsiMeetView;
    private  String jid;
    private NotificationModule notificationModule;
    private AudioManager audioManager ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_await);
        audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
//        notificationModule = new NotificationModule();
        init();

        initJitsi(false);
    }


    private void init() {
        tv_name =findViewById(R.id.tv_name);
        tv_text1 =findViewById(R.id.tv_text1);
        tv_text2 =findViewById(R.id.tv_text2);
        ll_inShow =findViewById(R.id.ll_inShow);
        ll_toShow =findViewById(R.id.ll_toShow);
        ll_accept=findViewById(R.id.ll_accept);
        tv_jj =findViewById(R.id.tv_jj);
        tv_jt =findViewById(R.id.tv_jt);
        tv_qx =findViewById(R.id.tv_qx);
        tv_jj.setOnClickListener(this);
        tv_jt.setOnClickListener(this);
        tv_qx.setOnClickListener(this);

        tv_acceptJY =findViewById(R.id.tv_acceptJY);
        tv_acceptJY.setOnClickListener(this);
        tv_acceptGD =findViewById(R.id.tv_acceptGD);
        tv_acceptGD.setOnClickListener(this);
        tv_acceptMT =findViewById(R.id.tv_acceptMT);
        tv_acceptMT.setOnClickListener(this);

        fragment=findViewById(R.id.jitsiFragment);
        tv_acceptTime=findViewById(R.id.tv_acceptTime);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_jj: {//拒绝
//                ChatBean.ChatMsgBean videochat = getChatBean(1,ChatBean.MSG_SP, 1, "reject",chatMsgBean.getSetting().getRoom(),jid);
//                String str1=  gson.toJson(videochat);
//                LogeUtil.e("拒绝语音聊天："+str1);

                finish();
            }
            break;
            case R.id.tv_jt: {//接听
//                ChatBean.ChatMsgBean videochat = getChatBean(1,ChatBean.MSG_SP, 1, "accept",chatMsgBean.getSetting().getRoom(),jid);
//                String str1=  gson.toJson(videochat);
//                LogeUtil.e("接听语音聊天："+str1);
                ll_inShow.setVisibility(View.VISIBLE);
                ll_toShow.setVisibility(View.GONE);
            }
            break;
            case R.id.tv_qx: {//取消
//                ChatBean.ChatMsgBean videochat = getChatBean(1,ChatBean.MSG_SP, 1, "close",chatMsgBean.getSetting().getRoom(),jid);
//                String str1=  gson.toJson(videochat);
//                LogeUtil.e("取消语音聊天："+str1);

            }
            break;
            case R.id.tv_acceptJY:{//静音
//                stopPlaying();
//                if (tv_acceptJY.isSelected()){
//                    tv_acceptJY.setSelected(false);
//                }else {
//                    tv_acceptJY.setSelected(true);
//                }
//                jitsiMeetView.leave();
//                initJitsi(tv_acceptJY.isSelected());
            }
            break;
            case R.id.tv_acceptGD:{//挂断
//                ChatBean.ChatMsgBean videochat = getChatBean(1,ChatBean.MSG_SP, 1, "reject",chatMsgBean.getSetting().getRoom(),jid);
//                String str1=  gson.toJson(videochat);
//                LogeUtil.e("挂断语音聊天："+str1);
finish();
            }
            break;
            case R.id.tv_acceptMT:{//免提
                if (tv_acceptMT.isSelected()){
                    tv_acceptMT.setSelected(false);
                }else {
                    tv_acceptMT.setSelected(true);
                }
                setSpeakerphoneOn(tv_acceptMT.isSelected());
            }
            break;
        }
    }



    /**
     * 是否静音
     * @param audioMuted
     */
    private void initJitsi(boolean audioMuted) {
        try{
            jitsiMeetView = new JitsiMeetView(VoiceAwaitChatActivity.this);
            jitsiMeetView.setListener(this);
            JitsiMeetConferenceOptions options = new JitsiMeetConferenceOptions.Builder()
                    .setServerURL(new URL("https://cod.xinhoo.com:7443/codmeet/"))
                    .setRoom("0001")
//                    .setRoom(XmppConfig.CODMEET+chatMsgBean.getSetting().getRoom())
                    .setAudioMuted(audioMuted)
                    .setVideoMuted(false)
                    .setAudioOnly(true)
                    .setWelcomePageEnabled(false)
                    .build();
            jitsiMeetView.join(options);
            fragment.addView(jitsiMeetView);

        }catch (Exception e){
            e.printStackTrace();
        }

    }


    /**
     * 扬声器与听筒切换
     * @param isSpeakerphoneOn
     */
    public void setSpeakerphoneOn(boolean isSpeakerphoneOn){
        audioManager.setSpeakerphoneOn(isSpeakerphoneOn);
        if(!isSpeakerphoneOn){
            audioManager.setMode(AudioManager.MODE_NORMAL);
        }
    }


    private Handler handler=new Handler(){
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
//                    startActivity(new Intent(VoiceAwaitChatActivity.this,VoiceChatActivity.class)
//                            .putExtra("room",chatMsgBean.getSetting().getRoom()));
//                    finish();
                    initJitsi(false);
                    break;
                case 1:
                    ll_accept.setVisibility(View.VISIBLE);
                    ll_inShow.setVisibility(View.GONE);
                    ll_toShow.setVisibility(View.GONE);
                    tv_text1.setVisibility(View.GONE);
                    tv_text2.setVisibility(View.GONE);

                    time=0;
                    if (mRunnable==null){
                        mRunnable = new MyRunnable();
                        mHandler.postDelayed(mRunnable, 0);
                    }
                    break;
                case 2:
                    VoiceAwaitChatActivity.this.finish();
                    break;



                case 100:
                    VoiceAwaitChatActivity.this.finish();
                    break;
                default:
                    break;
            }
        }
    };

    MyRunnable mRunnable;
    Handler mHandler=new Handler();
    int time=0;

    @Override
    public void onConferenceJoined(Map<String, Object> map) {//在会议加入时调用。
        for (String key : map.keySet()) {
            Log.e("log","Key = " + key+"    values = "+map.get(key));
        }
        android.os.Message msg=new android.os.Message();
        msg.what=1;
        handler.sendMessage(msg);
    }

    @Override
    public void onConferenceTerminated(Map<String, Object> map) {//当用户选择或由于失败而终止会议时调用。
        for (String key : map.keySet()) {
            Log.e("log","Key = " + key+"    values = "+map.get(key));
        }
        //失败
        android.os.Message msg=new android.os.Message();
        msg.what=2;
        handler.sendMessage(msg);
    }

    @Override
    public void onConferenceWillJoin(Map<String, Object> map) {//在会议加入之前召集。
        for (String key : map.keySet()) {
            Log.e("log","Key = " + key+"    values = "+map.get(key));
        }
    }

    private class MyRunnable implements Runnable {
        @Override
        public void run() {
            time++;
            String  timeStr;
            if(time<60){
                timeStr=String.format("00:%02d",time%60);
            }else if(time<3600){
                timeStr=String.format("%02d:%02d",time/60,time%60);
            }else{
                timeStr=String.format("%02d:%02d:%02d",time/3600,time%3600/60,time%60);
            }
            tv_acceptTime.setText(timeStr);
            mHandler.postDelayed(this, 1000);
        }
    }


    public void finish() {
        if (jitsiMeetView!=null){
            jitsiMeetView.leave();
        }
        super.finish();
    }





    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (jitsiMeetView!=null){
            jitsiMeetView.dispose();
            jitsiMeetView = null;
        }
        if (mHandler!=null){
            mHandler.removeCallbacks(mRunnable);
            mRunnable = null;
        }
        JitsiMeetActivityDelegate.onHostDestroy(VoiceAwaitChatActivity.this);
    }

    @Override
    public void requestPermissions(String[] strings, int i, PermissionListener permissionListener) {
    }
    @Override
    public void onNewIntent(Intent intent) {
        JitsiMeetActivityDelegate.onNewIntent(intent);
    }

    @Override
    public void onRequestPermissionsResult(
            final int requestCode,
            final String[] permissions,
            final int[] grantResults) {
        JitsiMeetActivityDelegate.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onResume() {
        super.onResume();
        JitsiMeetActivityDelegate.onHostResume(VoiceAwaitChatActivity.this);
    }

    @Override
    protected void onStop() {
        super.onStop();
//        try{
//        }catch (Exception e){
        JitsiMeetActivityDelegate.onHostPause(VoiceAwaitChatActivity.this);
//            e.printStackTrace();
//        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        JitsiMeetActivityDelegate.onActivityResult(this, requestCode, resultCode, data);
    }
    @Override
    public void onBackPressed() {
        JitsiMeetActivityDelegate.onBackPressed();
    }
}
