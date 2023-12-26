package com.dr34mt34m.v1ll4g3pl4nn3r.components.RecycleView;

import androidx.appcompat.app.AppCompatActivity;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dr34mt34m.v1ll4g3pl4nn3r.R;

public class ReminderViewHolder extends RecyclerView.ViewHolder{
    TextView StoreView;
    TextView MessageView;
    TextView TimeView;
    Button DeleteButton;
    View view;

    ReminderViewHolder(View itemView)
    {
        super(itemView);
        StoreView
                = (TextView)itemView
                .findViewById(R.id.StoreView);
        MessageView
                = (TextView)itemView
                .findViewById(R.id.MessageView);
        TimeView
                = (TextView)itemView
                .findViewById(R.id.TimeView);
        DeleteButton
                = (Button)itemView
                .findViewById(R.id.DeleteButton);
        view  = itemView;
    }
}
