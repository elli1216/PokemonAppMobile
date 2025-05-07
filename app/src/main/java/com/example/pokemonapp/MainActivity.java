package com.example.pokemonapp;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.graphics.Bitmap;
import android.os.Bundle;
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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    Button searchButton, clearButton;
    EditText searchEditText;
    TextView PokemonIdTextView, PokemonNameTextView, PokemonTypeTextView, PokemonHPTextView, PokemonDefenseTextView,
            PokemonAttackTextView, PokemonSpecialAttackTextView, PokemonSpecialDefenseTextView, PokemonSpeedTextView;
    ImageView pokemonImageView;
    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            init();
            return insets;
        });
    }

    private void init() {
        requestQueue = Volley.newRequestQueue(this);

        searchButton = findViewById(R.id.buttonSearch);
        clearButton = findViewById(R.id.buttonClear);
        searchEditText = findViewById(R.id.editTextPokemon);
        PokemonIdTextView = findViewById(R.id.textViewPID);
        PokemonNameTextView = findViewById(R.id.textViewName);
        PokemonTypeTextView = findViewById(R.id.textViewType);
        PokemonHPTextView = findViewById(R.id.textViewHP);
        PokemonDefenseTextView = findViewById(R.id.textViewDefense);
        PokemonAttackTextView = findViewById(R.id.textViewAttack);
        PokemonSpecialAttackTextView = findViewById(R.id.textViewSpec);
        PokemonSpecialDefenseTextView = findViewById(R.id.textViewSpecD);
        PokemonSpeedTextView = findViewById(R.id.textViewSpeed);
        pokemonImageView = findViewById(R.id.imageViewPic);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pokemonName = searchEditText.getText().toString().toLowerCase().trim();
                if (!pokemonName.isEmpty()) {
                    fetchPokemonData(pokemonName);
                } else {
                    Toast.makeText(MainActivity.this, "Please enter a Pokemon name", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void fetchPokemonData(String pokemonName) {
        String url = "https://pokeapi.co/api/v2/pokemon/" + pokemonName;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Parse Pokemon ID
                            int id = response.getInt("id");
                            PokemonIdTextView.setText("PokeDex ID: " + id);

                            // Parse Pokemon name
                            String name = response.getString("name");
                            PokemonNameTextView.setText("Name: " + name.substring(0, 1).toUpperCase() + name.substring(1));

                            // Parse Pokemon types
                            JSONArray types = response.getJSONArray("types");
                            StringBuilder typeBuilder = new StringBuilder("Type: ");
                            for (int i = 0; i < types.length(); i++) {
                                JSONObject typeObj = types.getJSONObject(i);
                                String typeName = typeObj.getJSONObject("type").getString("name");
                                typeBuilder.append(typeName);
                                if (i < types.length() - 1) {
                                    typeBuilder.append(", ");
                                }
                            }
                            PokemonTypeTextView.setText(typeBuilder.toString());

                            // Parse Pokemon stats
                            JSONArray stats = response.getJSONArray("stats");
                            for (int i = 0; i < stats.length(); i++) {
                                JSONObject statObj = stats.getJSONObject(i);
                                String statName = statObj.getJSONObject("stat").getString("name");
                                int statValue = statObj.getInt("base_stat");

                                switch (statName) {
                                    case "hp":
                                        PokemonHPTextView.setText("HP: " + statValue);
                                        break;
                                    case "attack":
                                        PokemonAttackTextView.setText("Attack: " + statValue);
                                        break;
                                    case "defense":
                                        PokemonDefenseTextView.setText("Defense: " + statValue);
                                        break;
                                    case "special-attack":
                                        PokemonSpecialAttackTextView.setText("Special Attack: " + statValue);
                                        break;
                                    case "special-defense":
                                        PokemonSpecialDefenseTextView.setText("Special Defense: " + statValue);
                                        break;
                                    case "speed":
                                        PokemonSpeedTextView.setText("Speed: " + statValue);
                                        break;
                                }
                            }

                            String imageUrl = response.getJSONObject("sprites").getString("front_default");
                            loadPokemonImage(imageUrl);

                        } catch (JSONException e) {
                            Toast.makeText(MainActivity.this, "Error parsing data", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "Pokemon not found", Toast.LENGTH_SHORT).show();
                    }
                });

        // Add the request to the RequestQueue
        requestQueue.add(jsonObjectRequest);
    }

    private void loadPokemonImage(String imageUrl) {
        ImageRequest imageRequest = new ImageRequest(imageUrl,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        pokemonImageView.setImageBitmap(response);
                    }
                }, 0, 0, ImageView.ScaleType.FIT_CENTER, null,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "Error loading image", Toast.LENGTH_SHORT).show();
                    }
                });

        requestQueue.add(imageRequest);
    }

    private void clearData() {
        searchEditText.setText("");
        PokemonIdTextView.setText("PokeDex ID");
        PokemonNameTextView.setText("Name");
        PokemonTypeTextView.setText("Type");
        PokemonHPTextView.setText("HP");
        PokemonAttackTextView.setText("Attack");
        PokemonDefenseTextView.setText("Defense");
        PokemonSpecialAttackTextView.setText("Special Attack");
        PokemonSpecialDefenseTextView.setText("Special Defense");
        PokemonSpeedTextView.setText("Speed");
        pokemonImageView.setImageResource(android.R.drawable.ic_menu_gallery);
    }
}