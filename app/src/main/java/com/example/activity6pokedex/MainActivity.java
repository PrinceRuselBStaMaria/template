package com.example.activity6pokedex;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Color;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    EditText et1;
    Button searchBTN;
    Button clearBTN;
    ImageView pokemonImage;
    TextView tvID;
    TextView tvName;
    TextView tvType;
    TextView tvHP;
    TextView tvAttack;
    TextView tvDefense;
    TextView tvSpecialAttack;
    TextView tvSpecialDefense;
    TextView tvSpeed;

    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        initialize();
        queue = Volley.newRequestQueue(this);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Glide.with(this)
                .load(R.drawable.poke)
                .into(pokemonImage);

        searchBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pokemonNameOrId = et1.getText().toString().trim();
                if (pokemonNameOrId.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter a Pokemon name or ID", Toast.LENGTH_SHORT).show();
                    return;
                }
                fetchPokemonData(pokemonNameOrId);
            }
        });

        clearBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearPokemonData();
            }
        });
    }

    private void initialize() {
        et1 = findViewById(R.id.et1);
        searchBTN = findViewById(R.id.searchBTN);
        clearBTN = findViewById(R.id.clearBTN);
        pokemonImage = findViewById(R.id.pokemonImage);
        tvID = findViewById(R.id.tvID);
        tvName = findViewById(R.id.tvName);
        tvType = findViewById(R.id.tvType);
        tvHP = findViewById(R.id.tvHP);
        tvAttack = findViewById(R.id.tvAttack);
        tvDefense = findViewById(R.id.tvDefense);
        tvSpecialAttack = findViewById(R.id.tvSpecialAttack);
        tvSpecialDefense = findViewById(R.id.tvSpecialDefense);
        tvSpeed = findViewById(R.id.tvSpeed);
    }

    private void fetchPokemonData(String pokemonNameOrId) {
        String url = "https://pokeapi.co/api/v2/pokemon/" + pokemonNameOrId;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int id = response.getInt("id");
                            String name = response.getString("name");
                            String type = response.getJSONArray("types").getJSONObject(0).getJSONObject("type").getString("name");
                            String imageUrl = response.getJSONObject("sprites").getJSONObject("other").getJSONObject("official-artwork").getString("front_default");
                            int hp = response.getJSONArray("stats").getJSONObject(0).getInt("base_stat");
                            int attack = response.getJSONArray("stats").getJSONObject(1).getInt("base_stat");
                            int defense = response.getJSONArray("stats").getJSONObject(2).getInt("base_stat");
                            int specialAttack = response.getJSONArray("stats").getJSONObject(3).getInt("base_stat");
                            int specialDefense = response.getJSONArray("stats").getJSONObject(4).getInt("base_stat");
                            int speed = response.getJSONArray("stats").getJSONObject(5).getInt("base_stat");

                            // Format the name to title case
                            String formattedName = name.substring(0, 1).toUpperCase() + name.substring(1);

                            Glide.with(MainActivity.this)
                                    .load(imageUrl)
                                    .into(pokemonImage);

                            // Set the background color for the type badge
                            setTypeBackground(tvType, type);

                            tvID.setText("Pokedex ID: " + id);
                            tvName.setText(formattedName);
                            tvType.setText(type.toUpperCase());
                            tvHP.setText("HP: " + hp);
                            tvAttack.setText("Attack: " + attack);
                            tvDefense.setText("Defense: " + defense);
                            tvSpecialAttack.setText("Special Attack: " + specialAttack);
                            tvSpecialDefense.setText("Special Defense: " + specialDefense);
                            tvSpeed.setText("Speed: " + speed);

                            // Update progress bars
                            ProgressBar progressHP = findViewById(R.id.progressHP);
                            ProgressBar progressAttack = findViewById(R.id.progressAttack);
                            ProgressBar progressDefense = findViewById(R.id.progressDefense);
                            ProgressBar progressSpecialAttack = findViewById(R.id.progressSpecialAttack);
                            ProgressBar progressSpecialDefense = findViewById(R.id.progressSpecialDefense);
                            ProgressBar progressSpeed = findViewById(R.id.progressSpeed);

                            progressHP.setProgress(hp);
                            progressAttack.setProgress(attack);
                            progressDefense.setProgress(defense);
                            progressSpecialAttack.setProgress(specialAttack);
                            progressSpecialDefense.setProgress(specialDefense);
                            progressSpeed.setProgress(speed);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("JSON Error", "Error parsing JSON: " + e.getMessage());
                            Toast.makeText(MainActivity.this, "Error parsing Pokemon data.", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley Error", "Error fetching data: " + error.getMessage());
                        if (error.networkResponse != null && error.networkResponse.statusCode == 404) {
                            Toast.makeText(MainActivity.this, "Pokemon not found.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "Failed to fetch Pokemon data.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        queue.add(request);
    }

    private void clearPokemonData() {
        et1.setText("");
        tvID.setText("Pokedex ID: ");
        tvName.setText("Name");
        tvType.setText("Type");
        tvHP.setText("HP: ");
        tvAttack.setText("Attack: ");
        tvDefense.setText("Defense: ");
        tvSpecialAttack.setText("Special Attack: ");
        tvSpecialDefense.setText("Special Defense: ");
        tvSpeed.setText("Speed: ");
        
        // Reset progress bars
        ProgressBar progressHP = findViewById(R.id.progressHP);
        ProgressBar progressAttack = findViewById(R.id.progressAttack);
        ProgressBar progressDefense = findViewById(R.id.progressDefense);
        ProgressBar progressSpecialAttack = findViewById(R.id.progressSpecialAttack);
        ProgressBar progressSpecialDefense = findViewById(R.id.progressSpecialDefense);
        ProgressBar progressSpeed = findViewById(R.id.progressSpeed);
        
        progressHP.setProgress(0);
        progressAttack.setProgress(0);
        progressDefense.setProgress(0);
        progressSpecialAttack.setProgress(0);
        progressSpecialDefense.setProgress(0);
        progressSpeed.setProgress(0);
        
        // Reset type background
        tvType.setBackgroundColor(Color.GRAY);
        
        // Load default image
        Glide.with(MainActivity.this)
                .load(R.drawable.poke)
                .into(pokemonImage);
    }

    // Helper method to set type background color
    private void setTypeBackground(TextView typeTextView, String type) {
        int color;
        switch (type.toLowerCase()) {
            case "fire": color = getResources().getColor(android.R.color.holo_red_light); break;
            case "water": color = getResources().getColor(android.R.color.holo_blue_light); break;
            case "grass": color = getResources().getColor(android.R.color.holo_green_light); break;
            case "electric": color = Color.parseColor("#FFD700"); break;
            case "psychic": color = Color.parseColor("#FF69B4"); break;
            case "ice": color = Color.parseColor("#ADD8E6"); break;
            case "dragon": color = Color.parseColor("#6F35FC"); break;
            case "dark": color = Color.parseColor("#705746"); break;
            case "fairy": color = Color.parseColor("#EE99AC"); break;
            case "normal": color = Color.parseColor("#A8A77A"); break;
            case "fighting": color = Color.parseColor("#C22E28"); break;
            case "flying": color = Color.parseColor("#A98FF3"); break;
            case "poison": color = Color.parseColor("#A33EA1"); break;
            case "ground": color = Color.parseColor("#E2BF65"); break;
            case "rock": color = Color.parseColor("#B6A136"); break;
            case "bug": color = Color.parseColor("#A6B91A"); break;
            case "ghost": color = Color.parseColor("#735797"); break;
            case "steel": color = Color.parseColor("#B7B7CE"); break;
            default: color = Color.GRAY;
        }
        typeTextView.setBackgroundColor(color);
    }
}

