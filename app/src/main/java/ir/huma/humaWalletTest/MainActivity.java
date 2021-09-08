package ir.huma.humaWalletTest;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;

import ir.huma.humawallet.lib.HumaWallet;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**
         * get temeporaryToken from your server
         * your server must call Huma Wallet Service to get temeporaryToken
         */
        new HumaWallet(this).setPaymentToken("your new temeporaryToken").setOnPayListener(new HumaWallet.OnPayListener() {
            @Override
            public void onPayComplete(String code) {
                /**
                 * here you must check your server is Pay successful
                 */
            }

            @Override
            public void onPayFail(String message) {
                /**
                 * pay cancel or stop with error
                 */
            }
        }).send();

    }
}