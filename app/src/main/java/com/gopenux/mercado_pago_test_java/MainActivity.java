package com.gopenux.mercado_pago_test_java;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mercadopago.*;

import com.mercadopago.android.px.core.MercadoPagoCheckout;
import com.mercadopago.android.px.model.Item;
import com.mercadopago.android.px.model.Payer;
import com.mercadopago.android.px.model.Payment;
import com.mercadopago.android.px.model.exceptions.MercadoPagoError;


public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 1;
    private Button button;

    String public_key = "TEST-91063014-a801-4062-b4b5-0caf4445dcb3";
    String access_token = "TEST-3872868554572949-033017-5c0786854f318d02a9b0f95b8fdfec0d-736253559";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = findViewById(R.id.button_mercado_pago);

        MercadoPago.SDK.setAccessToken(access_token);

        button.setOnClickListener(v -> {

            Preference preference = new Preference();

            Item item = new Item();
            item.setId("1234");
            item.setTitle("Cucus");
            item.setQuantity(1);
            item.setDescription("Lindos cucus");
            item.setCategoryId("COP");
            item.setUnitPrice((float)2000);

            Payer payer = new Payer();
            payer.setEmail("test_user_51283107@testuser.com");

            try {
                preference.setPayer(payer);
                preference.appendItem(item);
                preference.save();
            } catch (MPException e) {
                e.printStackTrace();
            }

            //startMercadoPagoCheckout(preference.getId());
        });
    }

    private void startMercadoPagoCheckout(final String checkoutPreferenceId) {
        new MercadoPagoCheckout.Builder(public_key, checkoutPreferenceId).build()
                .startPayment(MainActivity.this, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == MercadoPagoCheckout.PAYMENT_RESULT_CODE) {
                final Payment payment = (Payment) data.getSerializableExtra(MercadoPagoCheckout.EXTRA_PAYMENT_RESULT);
                ((TextView) findViewById(R.id.mp_results)).setText("Resultado del pago: " + payment.getStatus());
                //Done!
            } else if (resultCode == RESULT_CANCELED) {
                if (data != null && data.getExtras() != null
                        && data.getExtras().containsKey(MercadoPagoCheckout.EXTRA_ERROR)) {
                    final MercadoPagoError mercadoPagoError =
                            (MercadoPagoError) data.getSerializableExtra(MercadoPagoCheckout.EXTRA_ERROR);
                    ((TextView) findViewById(R.id.mp_results)).setText("Error: " + mercadoPagoError.getMessage());
                    //Resolve error in checkout
                } else {
                    //Resolve canceled checkout
                }
            }
        }
    }
}