package com.rsin.dotfood;

import android.content.Context;
import android.content.SharedPreferences;

import com.ibm.cloud.appid.android.api.AppIDAuthorizationManager;
import com.ibm.cloud.appid.android.api.tokens.AccessToken;
import com.ibm.cloud.appid.android.api.tokens.IdentityToken;
import com.ibm.cloud.appid.android.api.tokens.RefreshToken;

/**
 * Handles the process of storing the Access token on the Android device and retrieving it to be used
 * in App ID SDK methods.
 * This class present the advised way of storing tokens at the device.
 * Note: The token stored the shared preferences at "private" mode, but it is not protected from being read on a rooted device.
 * For this reason, we store only the Access token which has short expiry time, and rather not store the identity token which
 * might have private information.
 */
public class TokensPersistenceManager {
    enum StoredTokenState{
        empty, anonymous, identified
    }

    private static final String APPID_ACCESS_TOKEN = "appid_access_token";
    private static final String APPID_REFRESH_TOKEN = "appid_refresh_token";
    private static final String APPID_TOKENS_PREF = "appid_tokens";
    private static final String APPID_USER_NAME = "appid_user_name";
    private static final String APPID_USER_ID = "appid_user_id";
    private static final String APPID_IS_ANONYMOUS = "appid_is_anonymous";

    private static String ANONYMOUS_USER_NAME = "Guest";
    Context ctx;
    AppIDAuthorizationManager appIDAuthorizationManager;

    public TokensPersistenceManager(Context ctx, AppIDAuthorizationManager appIDAuthorizationManager){
        this.appIDAuthorizationManager = appIDAuthorizationManager;
        this.ctx = ctx;
    }

    public void persistTokensOnDevice(){
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(APPID_TOKENS_PREF, ctx.MODE_PRIVATE);
        AccessToken accessToken = appIDAuthorizationManager.getAccessToken();
        IdentityToken identityToken = appIDAuthorizationManager.getIdentityToken();

        String refreshTokenString = null;
        RefreshToken refreshToken = appIDAuthorizationManager.getRefreshToken();
        if (refreshToken != null) {
            refreshTokenString = refreshToken.getRaw();
        }

        String storedAccessToken = accessToken == null ? null : accessToken.getRaw();

        sharedPreferences.edit().
                putString(APPID_ACCESS_TOKEN, storedAccessToken).
                putString(APPID_USER_NAME, identityToken.getName()).
                putString(APPID_USER_ID, identityToken.getSubject()).
                putString(APPID_REFRESH_TOKEN, refreshTokenString).
                putBoolean(APPID_IS_ANONYMOUS, identityToken.isAnonymous()).
                apply();
    }

    public String getStoredAccessToken(){
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(APPID_TOKENS_PREF, ctx.MODE_PRIVATE);
        return sharedPreferences.getString(APPID_ACCESS_TOKEN, null);
    }

    public String getStoredAnonymousAccessToken(){
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(APPID_TOKENS_PREF, ctx.MODE_PRIVATE);
        if(!sharedPreferences.getBoolean(APPID_IS_ANONYMOUS, false)){
            return null;
        }
        return sharedPreferences.getString(APPID_ACCESS_TOKEN, null);
    }

    public String getStoredRefreshToken(){
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(APPID_TOKENS_PREF, ctx.MODE_PRIVATE);
        return sharedPreferences.getString(APPID_REFRESH_TOKEN, null);
    }

    public String getStoredLoginAccessToken(){
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(APPID_TOKENS_PREF, ctx.MODE_PRIVATE);
        if(sharedPreferences.getBoolean(APPID_IS_ANONYMOUS, true)){
            return null;
        }
        return sharedPreferences.getString(APPID_ACCESS_TOKEN, null);
    }

    public boolean isStoredTokenAnonymous(){
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(APPID_TOKENS_PREF, ctx.MODE_PRIVATE);
        return sharedPreferences.getBoolean(APPID_IS_ANONYMOUS, false);
    }

    public boolean isStoredTokenExists(){
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(APPID_TOKENS_PREF, ctx.MODE_PRIVATE);
        return !sharedPreferences.getAll().isEmpty();
    }

    public boolean clearStoredTokens(){
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(APPID_TOKENS_PREF, ctx.MODE_PRIVATE);
        return !sharedPreferences.edit().clear().commit();
    }

    public String getStoredUserName(){
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(APPID_TOKENS_PREF, ctx.MODE_PRIVATE);
        if(sharedPreferences.getBoolean(APPID_IS_ANONYMOUS, true)){
            return ANONYMOUS_USER_NAME;
        }
        return sharedPreferences.getString(APPID_USER_NAME, null);
    }

    public String getStoredUserID(){
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(APPID_TOKENS_PREF, ctx.MODE_PRIVATE);
        return sharedPreferences.getString(APPID_USER_ID, null);
    }

    public StoredTokenState getStoreTokenState(){
        if(!isStoredTokenExists()){
            return StoredTokenState.empty;
        }
        if (isStoredTokenAnonymous()){
            return StoredTokenState.anonymous;
        }else{
            return StoredTokenState.identified;
        }
    }

}
