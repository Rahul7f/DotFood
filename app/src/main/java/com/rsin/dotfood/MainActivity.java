package com.rsin.dotfood;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ibm.cloud.appid.android.api.AppID;
import com.ibm.cloud.appid.android.api.AppIDAuthorizationManager;
import com.ibm.cloud.appid.android.api.AuthorizationException;
import com.ibm.cloud.appid.android.api.AuthorizationListener;
import com.ibm.cloud.appid.android.api.LoginWidget;
import com.ibm.cloud.appid.android.api.TokenResponseListener;
import com.ibm.cloud.appid.android.api.tokens.AccessToken;
import com.ibm.cloud.appid.android.api.tokens.IdentityToken;
import com.ibm.cloud.appid.android.api.tokens.RefreshToken;

public class MainActivity extends AppCompatActivity {

    Button login;
    private AppID appId;
    private final static String region = AppID.REGION_UK;
    private final static String authTenantId = "f81892ba-4012-4427-abf0-d976b295208c";
    private AppIDAuthorizationManager appIDAuthorizationManager;
    private TokensPersistenceManager tokensPersistenceManager;
    EditText email_et,password_et;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        login = findViewById(R.id.login_btn);
//        email_et = findViewById(R.id.email_et);
//        password_et = findViewById(R.id.password_et);

        appId = AppID.getInstance();
        appId.initialize(this, authTenantId, region);

        appIDAuthorizationManager = new AppIDAuthorizationManager(this.appId);
        tokensPersistenceManager = new TokensPersistenceManager(this, appIDAuthorizationManager);

//        final String storedRefreshToken = tokensPersistenceManager.getStoredRefreshToken();
        String storedRefreshToken;

        SharedPreferences prefs = getSharedPreferences("mytoken", MODE_PRIVATE);
        storedRefreshToken = prefs.getString("refresh_token", null);

        if (storedRefreshToken != null && !storedRefreshToken.isEmpty()) {
//            refreshTokens(storedRefreshToken);
            Log.d("rsin_token_is", storedRefreshToken);
            Toast.makeText(this, "available", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this, "not av", Toast.LENGTH_SHORT).show();
        }

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                Log.d(logTag("onLoginClick"),"Attempting identified authorization");
                LoginWidget loginWidget = appId.getLoginWidget();
                final String storedAccessToken;
                storedAccessToken = tokensPersistenceManager.getStoredAccessToken();
                AppIdSampleAuthorizationListener appIdSampleAuthorizationListener =
                        new AppIdSampleAuthorizationListener(MainActivity.this, appIDAuthorizationManager, false);
                loginWidget.launch(MainActivity.this, appIdSampleAuthorizationListener, storedAccessToken);

            }
        });
    }
    private void refreshTokens(String refreshToken) {
        Log.d(logTag("refreshTokens"), "Trying to refresh tokens using a refresh token");
        boolean storedTokenAnonymous = tokensPersistenceManager.isStoredTokenAnonymous();
        AppIdSampleAuthorizationListener appIdSampleAuthorizationListener =
                new AppIdSampleAuthorizationListener(this, appIDAuthorizationManager, storedTokenAnonymous);
        appId.signinWithRefreshToken(this, refreshToken, appIdSampleAuthorizationListener);
    }
    private String logTag(String methodName){
        return getClass().getCanonicalName() + "." + methodName;
    }
}