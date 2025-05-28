package com.example.teste;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private VideoAdapter adapter;
    private List<VideoModel> videoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        setupRecyclerView();
        loadVideoData();
    }

    private void initializeViews() {
        recyclerView = findViewById(R.id.recyclerVideos);
    }

    private void setupRecyclerView() {
        // Use this context instead of getApplicationContext() for consistency with theme
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Improve performance if all items are of the same size
        recyclerView.setHasFixedSize(true);

        // Initialize the adapter with empty list
        videoList = new ArrayList<>();
        adapter = new VideoAdapter(videoList, this);
        recyclerView.setAdapter(adapter);
    }

    private void loadVideoData() {
        // In a real app, this would come from a database or API
        videoList.add(new VideoModel(
                "NOVOS RELATOS PESADOS - O JULGAMENTO DIA 10",
                "Planeta Novo",
                "https://i.ytimg.com/vi/YX04StuAPhY/maxresdefault.jpg",
                "https://youtu.be/YX04StuAPhY?feature=shared"
        ));

        // Notify adapter of data changes
        adapter.notifyDataSetChanged();
    }
}