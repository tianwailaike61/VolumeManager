//package com.jianghongkui.volumemanager.other;
//
//import android.app.Service;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.telephony.TelephonyManager;
//
///**
// * Created by jianghongkui on 2016/10/8.
// */
//
//public class CallReceiver extends BroadcastReceiver {
//    private final static  String TAG="CallReceiver";
//    private boolean mIncomingFlag;
//
//    @Override
//    public void onReceive(Context context, Intent intent) {
//        // 如果是拨打电话
//        if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
//            mIncomingFlag = false;
//
//        } else {
//            TelephonyManager tManager = (TelephonyManager) context
//                    .getSystemService(Service.TELEPHONY_SERVICE);
//            switch (tManager.getCallState()) {
//                case TelephonyManager.CALL_STATE_RINGING:
//
//                    break;
//                case TelephonyManager.CALL_STATE_OFFHOOK:
//                    if (mIncomingFlag) {
//
//                    }
//                    break;
//                case TelephonyManager.CALL_STATE_IDLE:
//                    break;
//            }
//        }
//    }
//}
