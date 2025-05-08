package com.example.activity6pokedex;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {

    private EditText searchEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        searchEditText = findViewById(R.id.editTextText);
        Button searchBtn = findViewById(R.id.search);

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchQuery = searchEditText.getText().toString().trim();
                
                if (searchQuery.isEmpty() || searchQuery.equalsIgnoreCase("Name")) {
                    Toast.makeText(WelcomeActivity.this, "Please enter a Pokemon name or ID", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                intent.putExtra("SEARCH_QUERY", searchQuery);
                startActivity(intent);
                finish();
            }
        });
    }
}