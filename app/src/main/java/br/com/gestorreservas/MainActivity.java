package br.com.gestorreservas;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import br.com.gestorreservas.Shared.Shared;

public class MainActivity extends AppCompatActivity {

    private static final List<Integer> reservarIds = Arrays.asList(R.id.btn_reservar_1, R.id.btn_reservar_2, R.id.btn_reservar_3, R.id.btn_reservar_4, R.id.btn_reservar_5, R.id.btn_reservar_6, R.id.btn_reservar_7, R.id.btn_reservar_8, R.id.btn_reservar_9);
    private static final List<Integer> mesaIds = Arrays.asList(R.id.linear_mesa_1, R.id.linear_mesa_2, R.id.linear_mesa_3, R.id.linear_mesa_4, R.id.linear_mesa_5, R.id.linear_mesa_6, R.id.linear_mesa_7, R.id.linear_mesa_8, R.id.linear_mesa_9);
    private List<LinearLayout> mesas = new ArrayList<>();
    private List<Button> botoesReservar = new ArrayList<>();

    private EditText editNumeroMesa;
    private Button btnLiberar;
    private Button btnSalavarOperacao;
    private Button btnReservarTodasMesas;

    private Set<Integer> cacheMesasUtilizadas = new HashSet<>();

    private static final int AZUL = Color.rgb(63, 81, 181);
    private static final int VERMELHO = Color.rgb(244, 67, 54);
    private static final String SHARED_KEY = "mesasOcupadas";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mesaIds.forEach( m -> {
            mesas.add(findViewById(m));
        });

        int i = 0;
        for(Integer reservarId : reservarIds){
            Button btn = findViewById(reservarId);

            final int j = i;
            btn.setOnClickListener( o -> {
                btn.setEnabled(false);
                mesas.get(j).setBackgroundColor(VERMELHO);
                cacheMesasUtilizadas.add(j);
            });

            botoesReservar.add(btn);
            i++;
        }

        editNumeroMesa = findViewById(R.id.edit_numero_mesa);

        btnLiberar = findViewById(R.id.btn_liberarMesa);
        btnLiberar.setOnClickListener(o -> {
            try{
                String numeroMesaStr = editNumeroMesa.getText().toString();
                if(numeroMesaStr.length() < 1){
                    throw new Exception("Informe o número para liberar a mesa!");
                }

                int mesa = Integer.valueOf(numeroMesaStr);
                if(mesa < 0 || mesa > 9){
                    throw new Exception("Foi informada uma mesa inexistente!");
                }

                int mesaReal = mesa - 1;

                boolean isMesaUtilizada = cacheMesasUtilizadas.contains(mesaReal);
                if(!isMesaUtilizada){
                    throw new Exception("Mesa não reservada. A mesa " + mesa + " encontra-se habilitada para reserva");
                }

                mesas.get(mesaReal).setBackgroundColor(AZUL);
                botoesReservar.get(mesaReal).setEnabled(true);
                cacheMesasUtilizadas.remove(mesaReal);

                editNumeroMesa.setText("");

            }catch (Exception e){
                exibirAlertDialog(e.getMessage());
            }

        });

        btnSalavarOperacao = findViewById(R.id.btn_salvar_operacao);
        btnSalavarOperacao.setOnClickListener( o -> {
            Shared.putIntegerSet(this, SHARED_KEY, cacheMesasUtilizadas);
            exibirAlertDialog("Salvo com sucesso!");
        });

        btnReservarTodasMesas = findViewById(R.id.btn_reservar_todas_mesas);
        btnReservarTodasMesas.setOnClickListener( o -> {
            try {
                if(cacheMesasUtilizadas.size() == 9){
                    throw new Exception("Operação inválida! Todas as mesas já possuem reserva.");
                }

                for(int k=0; k<9; k++){
                    reservarMesa(k);
                }
            }catch (Exception e){
                exibirAlertDialog(e.getMessage());
            }
        });

        //Carrega as mesas ocupadas
        Shared.getIntegerSet(this, SHARED_KEY).forEach(this::reservarMesa);
    }

    private void reservarMesa(int mesa){
        botoesReservar.get(mesa).setEnabled(false);
        mesas.get(mesa).setBackgroundColor(VERMELHO);
        cacheMesasUtilizadas.add(mesa);
    }

    private void exibirAlertDialog(String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Aviso:");
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Não faz nada
            }
        });
        builder.create().show();
    }
}
