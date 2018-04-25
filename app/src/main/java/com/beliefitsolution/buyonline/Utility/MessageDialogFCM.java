package com.beliefitsolution.buyonline.Utility;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.beliefitsolution.buyonline.R;
import com.bumptech.glide.Glide;


public class MessageDialogFCM {

    public AlertDialog alert;

    public MessageDialogFCM(Context mContext, String title, String message, String imgurl) {

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View g = inflater.inflate(R.layout.display_message_popup_fcm, null);
        TextView txtview_msg = (TextView) g.findViewById(R.id.txt_msg);
        TextView txtview_title = (TextView) g.findViewById(R.id.txt_title);
        ImageView imageView = g.findViewById(R.id.img_fcm);
        TextView btn_x = (TextView) g.findViewById(R.id.btn_x);

        txtview_msg.setText(message);
        txtview_title.setText(title);
        Glide.with(mContext).load(imgurl).into(imageView);

        builder.setView(g);
        alert = builder.create();
        alert.setCancelable(false);
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        btn_x.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.dismiss();
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
