package com.example.firebasecursods;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.firebasecursods.Database.DatabaseGravarAlterarRemoverActivity;
import com.example.firebasecursods.Database.DatabaseLerDadosActivity;
import com.example.firebasecursods.Storage.StorageDownloadActivity;
import com.example.firebasecursods.Storage.StorageUploadActivity;
import com.example.firebasecursods.Utilidades.Permissao;
import android.Manifest;

import Database_Lista_Empresa.DatabaseListaEmpresaActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private CardView cardView_StorageDownload;
    private CardView cardView_StorageUpload;
    private CardView cardView_Database_LerDados;
    private CardView cardView_Database_GravarAlterar_Excluir;
    private CardView cardView_Empresas;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cardView_StorageDownload = (CardView)findViewById(R.id.cardView_StorageDownload);
        cardView_StorageUpload = (CardView)findViewById(R.id.cardView_StorageUpload);
        cardView_Database_LerDados = (CardView)findViewById(R.id.cardView_Database_LerDados);
        cardView_Database_GravarAlterar_Excluir = (CardView)findViewById(R.id.cardView_Database_GravarAlterarExcluir);
        cardView_Empresas = (CardView)findViewById(R.id.cardView_Empresas);

        cardView_StorageDownload.setOnClickListener(this);
        cardView_StorageUpload.setOnClickListener(this);
        cardView_Database_LerDados.setOnClickListener(this);
        cardView_Database_GravarAlterar_Excluir.setOnClickListener(this);
        cardView_Empresas.setOnClickListener(this);

        permitir();


    }

    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.cardView_StorageDownload:

                Intent intent = new Intent(getBaseContext(), StorageDownloadActivity.class);
                startActivity(intent);

               break;

            case R.id.cardView_StorageUpload:

                startActivity(new Intent(getBaseContext(), StorageUploadActivity.class));
                break;

            case R.id.cardView_Database_LerDados:

                startActivity( new Intent(getBaseContext(), DatabaseLerDadosActivity.class));
                break;

            case R.id.cardView_Database_GravarAlterarExcluir:

                startActivity(new Intent(getBaseContext(), DatabaseGravarAlterarRemoverActivity.class));
                break;

            case R.id.cardView_Empresas:

                startActivity(new Intent(getBaseContext(), DatabaseListaEmpresaActivity.class));
                break;
        }
    }

    // Permissoes
    private void permitir()
    {
        String permissoes [] =  new String[]
                {
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                };

        Permissao.permissao(this, 0, permissoes);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for(int result : grantResults)
        {
            if(result == PackageManager.PERMISSION_DENIED)
            {
                Toast.makeText(this, "Aceite as permissões para o aplicativo funcionar corretamente",
                        Toast.LENGTH_LONG).show();
                finish();

                break;
            }
        }
    }
}
