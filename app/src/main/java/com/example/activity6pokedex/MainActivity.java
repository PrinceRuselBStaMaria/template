package com.example.activity6pokedex;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
                Glide.with(MainActivity.this)
                        .load(R.drawable.poke)
                        .into(pokemonImage);
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

                            Glide.with(MainActivity.this)
                                    .load(imageUrl)
                                    .into(pokemonImage);

                            tvID.setText("Pokedex ID: " + id);
                            tvName.setText("Name: " + name);
                            tvType.setText("Type: " + type);
                            tvHP.setText("HP: " + hp);
                            tvAttack.setText("Attack: " + attack);
                            tvDefense.setText("Defense: " + defense);
                            tvSpecialAttack.setText("Special Attack: " + specialAttack);
                            tvSpecialDefense.setText("Special Defense: " + specialDefense);
                            tvSpeed.setText("Speed: " + speed);

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
        tvName.setText("Name: ");
        tvType.setText("Type: ");
        tvHP.setText("HP: ");
        tvAttack.setText("Attack: ");
        tvDefense.setText("Defense: ");
        tvSpecialAttack.setText("Special Attack: ");
        tvSpecialDefense.setText("Special Defense: ");
        tvSpeed.setText("Speed: ");
    }
}

