package com.example.owner.mapDemo;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class BookingHistoryAdapter extends RecyclerView.Adapter<BookingHistoryAdapter.HistoryViewHolder> {

    private Context mCtx;
    private List<BookingHistory> historyList;

    public BookingHistoryAdapter(Context mCtx, List<BookingHistory> historyList) {
        this.mCtx = mCtx;
        this.historyList = historyList;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.list_layout, null);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder historyViewHolder, int i) {
        BookingHistory bookingHistory = historyList.get(i);

        historyViewHolder.fromLocationTV.setText(bookingHistory.getFromLocation());
        historyViewHolder.toLocationTV.setText(bookingHistory.getToLocation());
        historyViewHolder.usersNameTV.setText(bookingHistory.getUsersName());
        historyViewHolder.driversNameTV.setText(bookingHistory.getDriversName());
        historyViewHolder.usersPhoneTV.setText(bookingHistory.getUsersPhone());
        historyViewHolder.priceTV.setText(String.valueOf(bookingHistory.getPrice()));
        historyViewHolder.timestampTV.setText(bookingHistory.getTimestamp());

    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    class HistoryViewHolder extends RecyclerView.ViewHolder {

        TextView fromLocationTV,toLocationTV, usersNameTV, driversNameTV, usersPhoneTV, priceTV, timestampTV;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);

            fromLocationTV = itemView.findViewById(R.id.rec_from_location);
            toLocationTV = itemView.findViewById(R.id.rec_to_location);
            usersNameTV = itemView.findViewById(R.id.rec_user_name);
            driversNameTV = itemView.findViewById(R.id.rec_driver_name);
            usersPhoneTV = itemView.findViewById(R.id.rec_user_phone);
            priceTV = itemView.findViewById(R.id.rec_price);
            timestampTV = itemView.findViewById(R.id.rec_time_stamp);

        }
    }
}
