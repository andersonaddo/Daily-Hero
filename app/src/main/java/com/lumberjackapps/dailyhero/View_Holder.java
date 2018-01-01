package com.lumberjackapps.dailyhero;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by addoa on 03-Jul-16.
 */
public class View_Holder extends RecyclerView.ViewHolder {

    CardView cv;
    TextView name;
    TextView number;
    ImageView imageView;
    Button fullbutton;
    Button deletebutton;
    Button sharebutton;

    View_Holder(View itemView) {
        super(itemView);
        cv = (CardView) itemView.findViewById(R.id.cardView);
        name = (TextView) itemView.findViewById(R.id.card_title);
        number = (TextView) itemView.findViewById(R.id.card_number);
        imageView = (ImageView) itemView.findViewById(R.id.card_image);
        fullbutton = (Button)itemView.findViewById(R.id.card_full);
        deletebutton = (Button)itemView.findViewById(R.id.card_delete);
        sharebutton = (Button)itemView.findViewById(R.id.card_share);
    }
}