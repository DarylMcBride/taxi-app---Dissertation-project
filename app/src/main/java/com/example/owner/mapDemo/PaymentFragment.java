package com.example.owner.mapDemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

public class PaymentFragment extends Fragment {

    ImageButton backButton;
    Button paypal, creditCard;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_payment, container, false);

        backButton = view.findViewById(R.id.backToMapImgButton3);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(getActivity(), MapsActivity.class);
                startActivity(in);
            }
        });

        paypal = view.findViewById(R.id.payPalButton);
        paypal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "PayPal selected", Toast.LENGTH_LONG).show();
            }
        });

        creditCard = view.findViewById(R.id.creditCardButton);
        creditCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Credit Card selected", Toast.LENGTH_LONG).show();
            }
        });

        return view;
    }
}
