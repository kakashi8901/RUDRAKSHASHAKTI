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

import com.rudraksha.rudrakshashakti.Pojo.PreviousCalls;
import com.rudraksha.rudrakshashakti.Pojo.UpcommingCalls;
import com.rudraksha.rudrakshashakti.R;
import com.rudraksha.rudrakshashakti.Utilities.Animations;
import com.sinaseyfi.advancedcardview.AdvancedCardView;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class PreviousCallsAdapter extends RecyclerView.Adapter<PreviousCallsAdapter.PreviousCallsViewHolder>{
    Context context;
    ArrayList<PreviousCalls> previousCalls;
    Boolean isOpen = true;

    public PreviousCallsAdapter(Context context, ArrayList<PreviousCalls> previouscalls){
        this.context = context;
        this.previousCalls= previouscalls;
    }

    @NotNull
    @Override
    public PreviousCallsAdapter.PreviousCallsViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.previous_calls_cards, parent, false);
        return new PreviousCallsAdapter.PreviousCallsViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull @NotNull PreviousCallsAdapter.PreviousCallsViewHolder holder, int position) {
        final PreviousCalls previousCallsModel = previousCalls.get(position);
        holder.userName.setText(previousCallsModel.getUserName());
        holder.upcomingCallTime.setText(previousCallsModel.getCallTime());
        holder.service.setText("Service:  "+ previousCallsModel.getService());
        holder.callTime.setText("Time:  "+ previousCallsModel.getCallTime());
        holder.callDuration.setText("Duration:  "+ previousCallsModel.getCallDuration()+" min");
        holder.callDate.setText("Date:  "+ previousCallsModel.getCallDate());
        Picasso.with(context)
                .load(previousCallsModel.getUserProfilePic())
                .into(holder.userProfilePic);


        holder.dropdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDropdown(holder);
            }
        });

        holder.previousCallsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDropdown(holder);
            }
        });
    }

    /**
     * It will open more details about the user*/
    private void openDropdown(PreviousCallsAdapter.PreviousCallsViewHolder holder) {
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
        return previousCalls.size();
    }

    public static class PreviousCallsViewHolder extends RecyclerView.ViewHolder {
        TextView userName,callTime,service,callDate,callDuration,upcomingCallTime;
        ImageView userProfilePic;
        ImageButton dropdown;
        LinearLayout detailsDropdown;
        AdvancedCardView previousCallsCard;


        public PreviousCallsViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            userName = (TextView) itemView.findViewById(R.id.userName);
            upcomingCallTime = (TextView) itemView.findViewById(R.id.upcomingCallTime);
            userProfilePic = (ImageView) itemView.findViewById(R.id.userProfilePic);
            service = (TextView) itemView.findViewById(R.id.service);
            callDate= (TextView) itemView.findViewById(R.id.date);
            callTime = (TextView) itemView.findViewById(R.id.time);
            callDuration = (TextView) itemView.findViewById(R.id.duration);
            dropdown = (ImageButton) itemView.findViewById(R.id.dropdown_menu);
            detailsDropdown = (LinearLayout) itemView.findViewById(R.id.detailsCallDropdown);
            previousCallsCard = (AdvancedCardView) itemView.findViewById(R.id.previousCallsCard);

        }
    }

}
