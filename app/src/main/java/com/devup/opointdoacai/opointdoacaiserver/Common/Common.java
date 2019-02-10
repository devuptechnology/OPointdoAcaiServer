package com.devup.opointdoacai.opointdoacaiserver.Common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.devup.opointdoacai.opointdoacaiserver.Model.Request;
import com.devup.opointdoacai.opointdoacaiserver.Remote.APIService;
import com.devup.opointdoacai.opointdoacaiserver.Remote.FCMRetrofitClient;
import com.devup.opointdoacai.opointdoacaiserver.Remote.RetrofitClient;
import com.firebase.ui.auth.data.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Common {

    public static Request currentRequest;

    public static User currentUser;

    private static final String FCM_URL = "https://fcm.googleapis.com/";

    public static APIService getFCMClient(){

        return FCMRetrofitClient.getClient(FCM_URL).create(APIService.class);

    }

    public static String convertCodeToStatus(String status) {

        if (status.equals("0")){
            return "Pedido";
        }else if (status.equals("1")){
            return "Aguardando";
        }else if (status.equals("2")){
            return "Saiu para Entrega";
        }else{
            return "Entregue";
        }
    }

    public static final String UPDATE = "Atualizar";

    public static final String DELETE = "Excluir";

    public static boolean isConnectedToInternet(Context context){

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null){

            NetworkInfo[] info = connectivityManager.getAllNetworkInfo();

            if (info != null){

                for (int i = 0; i < info.length; i++){

                    if (info[i].getState() == NetworkInfo.State.CONNECTED){

                        return true;

                    }

                }

            }

        }
        return false;

    }
}
