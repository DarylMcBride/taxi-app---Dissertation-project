package com.example.owner.mapDemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class CompanyBookingHistory extends AppCompatActivity {

    RecyclerView recyclerView;
    BookingHistoryAdapter adapter;

    List<BookingHistory> bookingHistoryList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        bookingHistoryList = new ArrayList<>();

        recyclerView = findViewById(R.id.company_recycler_view);
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));



        adapter = new BookingHistoryAdapter(this, bookingHistoryList);
        recyclerView.setAdapter(adapter);
    }
}
