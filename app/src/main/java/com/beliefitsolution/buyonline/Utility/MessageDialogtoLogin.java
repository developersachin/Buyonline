package com.beliefitsolution.buyonline.Utility;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.beliefitsolution.buyonline.MyApp;
import com.beliefitsolution.buyonline.R;
import com.beliefitsolution.buyonline.login.SignUpActivity;
import com.beliefitsolution.buyonline.login.SigninActivity;


public class MessageDialogtoLogin {

    public AlertDialog alert;

    public MessageDialogtoLogin(Context mContext, String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View g = inflater.inflate(R.layout.display_message_popup_login, null);
        TextView txtview = (TextView) g.findViewById(R.id.txt_msg);
        TextView btn_skip = (TextView) g.findViewById(R.id.btn_skip_signinup);
        TextView btn_signin = g.findViewById(R.id.btn_signin);
        TextView btn_signup = g.findViewById(R.id.btn_signup);

        txtview.setText(message);
        builder.setView(g);
        alert = builder.create();
        alert.setCancelable(false);
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        btn_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.dismiss();
            }
        });


        btn_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.dismiss();
                Intent intent = new Intent(MyApp.getContext(), SigninActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                MyApp.getContext().startActivity(intent);

            }
        });
        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.dismiss();
                Intent intent = new Intent(MyApp.getContext(), SignUpActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                MyApp.getContext().startActivity(intent);

            }
        });


    }

    public void displayMessageShow() {
        if (alert != null)
            alert.show();

    }

    public void dislayMessageHide() {
        if (alert != null)
            alert.dismiss();
    }

}
