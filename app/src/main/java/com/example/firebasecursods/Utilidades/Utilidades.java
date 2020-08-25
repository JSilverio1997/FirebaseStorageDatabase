package com.example.firebasecursods.Utilidades;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.widget.Toast;

public class Utilidades {

    public static boolean statusInternet(Context context)
    {
        boolean status = false;
        ConnectivityManager conexao = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (conexao != null)
        {

            // Código para novos dispositivos
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {

                NetworkCapabilities recursosRede = conexao.getNetworkCapabilities(conexao.getActiveNetwork());

                if (recursosRede != null)
                {

                    if (recursosRede.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {

                        //Verificando se o Dispositivo possui rede 3G ou 4G
                        return true;

                    }
                    else if (recursosRede.hasTransport(NetworkCapabilities.TRANSPORT_WIFI))
                    {
                        //Verificando se o dispositivo possui Wi fi
                        return true;

                    }

                    //Nao possui conexao valida
                    return false;
                }

            }
            // Código para dispositivos antigos
            else
             {
                 NetworkInfo informacao = conexao.getActiveNetworkInfo();


                if (informacao != null && informacao.isConnected())
                {
                    status = true;
                }
                else
                {
                    status = false;
                }
                return status;

             }
        }

        return false;
    }

    public static boolean verificarCampos(Context context, String texto_1, String texto_2)
    {
        boolean status_rede;
        if(!texto_1.isEmpty() && !texto_2.isEmpty())
        {
            status_rede = statusInternet(context);

            if(status_rede)
            {
                return true;
            }
            else
            {
                Toast.makeText(context, "O Dispositivo não possuí conexão a Internet.",
                        Toast.LENGTH_LONG).show();
                return false;
            }

        }
        else
        {
            Toast.makeText(context, "Preencha os Campos. ", Toast.LENGTH_LONG).show();

            return false;
        }
    }

}
