package com.rudraksha.rudrakshashakti.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rudraksha.rudrakshashakti.Pojo.UpcommingCalls;
import com.rudraksha.rudrakshashakti.R;
import com.rudraksha.rudrakshashakti.Utilities.Animations;
import com.sinaseyfi.advancedcardview.AdvancedCardView;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class UpcomingCallsAdapter extends RecyclerView.Adapter<UpcomingCallsAdapter.UpcomingCallsViewHolder>{

    Context context;
    ArrayList<UpcommingCalls> upcommingCalls;
    Boolean isOpen = true;

    public UpcomingCallsAdapter(Context context, ArrayList<UpcommingCalls> upcommingCalls){
        this.context = context;
        this.upcommingCalls= upcommingCalls;
    }

    @NotNull
    @Override
    public UpcomingCallsAdapter.UpcomingCallsViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.upcomming_calls_card, parent, false);
        return new UpcomingCallsAdapter.UpcomingCallsViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull @NotNull UpcomingCallsAdapter.UpcomingCallsViewHolder holder, int position) {
        final UpcommingCalls upcommingCallsModel = upcommingCalls.get(position);
        holder.expertName.setText(upcommingCallsModel.getUserName());
        holder.upcomingCallTime.setText(upcommingCallsModel.getCallTime());
        holder.service.setText("Service:  "+ upcommingCallsModel.getService());
        holder.callTime.setText("Time:  "+ upcommingCallsModel.getCallTime());
        holder.callDuration.setText("Duration:  "+ upcommingCallsModel.getCallDuration()+" min");
        holder.callDate.setText("Date:  "+ upcommingCallsModel.getCallDate());
        Picasso.with(context)
                .load(upcommingCallsModel.getUserProfilePic())
                .into(holder.expertProfilePic);


        holder.dropdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDropdown(holder);
            }
        });

        holder.upComingCallsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDropdown(holder);
            }
        });
    }

    /**
     * It will open more details about the expert*/
    private void openDropdown(UpcomingCallsAdapter.UpcomingCallsViewHolder holder) {
        if(isOpen){
            Animations.expand(holder.detailsDropdown);
            holder.dropdown.setBackgroundResource(R.drawable.ic_baseline_expand_less_24);
            isOpen=false;
        }else {
            Animations.collapse(holder.detailsDropdown);
            holder.dropdown.setBackgroundResource(R.drawable.ic_baseline_expand_more_24);
            isOpen=true;
        }
    }

    @Override
    public int getItemCount() {
        return upcommingCalls.size();
    }

    public static class UpcomingCallsViewHolder extends RecyclerView.ViewHolder {
        TextView expertName,callTime,service,callDate,callDuration,upcomingCallTime;
        ImageView expertProfilePic;
        ImageButton dropdown;
        LinearLayout detailsDropdown;
        AdvancedCardView upComingCallsCard;


        public UpcomingCallsViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            expertName = (TextView) itemView.findViewById(R.id.expertName);
            upcomingCallTime = (TextView) itemView.findViewById(R.id.upcomingCallTime);
            expertProfilePic = (ImageView) itemView.findViewById(R.id.expertProfilePic);
            service = (TextView) itemView.findViewById(R.id.service);
            callDate= (TextView) itemView.findViewById(R.id.date);
            callTime = (TextView) itemView.findViewById(R.id.time);
            callDuration = (TextView) itemView.findViewById(R.id.duration);
            dropdown = (ImageButton) itemView.findViewById(R.id.dropdown_menu);
            detailsDropdown = (LinearLayout) itemView.findViewById(R.id.detailsCallDropdown);
            upComingCallsCard = (AdvancedCardView) itemView.findViewById(R.id.upcomingCallsCard);

        }
    }
}
