package com.rsin.dotfood.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;


import com.ibm.cloud.appid.android.api.AppID;
import com.ibm.cloud.appid.android.api.AuthorizationException;
import com.ibm.cloud.appid.android.api.AuthorizationListener;
import com.ibm.cloud.appid.android.api.tokens.AccessToken;
import com.ibm.cloud.appid.android.api.tokens.IdentityToken;
import com.ibm.cloud.appid.android.api.tokens.RefreshToken;
import com.ibm.cloud.appid.android.api.userprofile.UserProfileException;
import com.ibm.cloud.appid.android.api.userprofile.UserProfileResponseListener;
import com.rsin.dotfood.Detail_Form;
import com.rsin.dotfood.R;

import org.json.JSONException;
import org.json.JSONObject;


public class AccountFragment extends Fragment {
    ImageView userImage;
    TextView username;
    CardView giver,taker;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.account, container, false);
        giver = root.findViewById(R.id.giver_card);
        taker = root.findViewById(R.id.taker_card);
        giver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), Detail_Form.class);
                intent.putExtra("tag","giver");
                startActivity(intent);
            }
        });

        taker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), Detail_Form.class);
                intent.putExtra("tag","taker");
                startActivity(intent);
            }
        });
        username = root.findViewById(R.id.username);
        AppID.getInstance().getUserProfileManager().getUserInfo(new UserProfileResponseListener() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    username.setText(response.getString("name"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(UserProfileException e) {

            }
        });
       AppID.getInstance().signinWithRefreshToken(getContext(), "", new AuthorizationListener() {
           @Override
           public void onAuthorizationCanceled() {

           }

           @Override
           public void onAuthorizationFailure(AuthorizationException exception) {

           }

           @Override
           public void onAuthorizationSuccess(AccessToken accessToken, IdentityToken identityToken, RefreshToken refreshToken) {

           }
       });

//        code here

        return root;
    }
}