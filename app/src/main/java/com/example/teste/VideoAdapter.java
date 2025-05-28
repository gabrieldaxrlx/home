package com.example.teste;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {
    private final List<VideoModel> videoList;
    private final Context context;
    private final ExecutorService executorService; // For background tasks
    private final Handler mainHandler; // For UI updates

    public VideoAdapter(List<VideoModel> videoList, Context context) {
        this.videoList = videoList;
        this.context = context;
        this.executorService = Executors.newFixedThreadPool(4); // Limited thread pool
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        VideoModel video = videoList.get(position);
        holder.bind(video);
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    public void updateData(List<VideoModel> newVideos) {
        videoList.clear();
        videoList.addAll(newVideos);
        notifyDataSetChanged();
    }

    @Override
    public void onViewRecycled(@NonNull VideoViewHolder holder) {
        super.onViewRecycled(holder);
        // Cancel any pending image loading for this holder
        holder.cancelImageLoading();
    }

    public class VideoViewHolder extends RecyclerView.ViewHolder {
        private final TextView txtTitle;
        private final TextView txtChannel;
        private final ImageView imgThumbnail;
        private volatile boolean imageLoadCancelled = false;

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtChannel = itemView.findViewById(R.id.txtChannel);
            imgThumbnail = itemView.findViewById(R.id.imgThumbnail);
        }

        public void bind(VideoModel video) {
            // Reset cancellation flag
            imageLoadCancelled = false;

            // Set text content
            txtTitle.setText(video.getTitle());
            txtChannel.setText(video.getChannel());

            // Set placeholder image
            imgThumbnail.setImageResource(R.drawable.pdiddy);

            // Load image in background
            executorService.execute(() -> loadImage(video.getThumbnailUrl()));

            // Set click listener
            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(video.getVideoUrl()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            });
        }

        private void loadImage(String imageUrl) {
            if (imageLoadCancelled) return;

            try {
                URL url = new URL(imageUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();

                // Check if loading was cancelled during connection
                if (imageLoadCancelled) {
                    connection.disconnect();
                    return;
                }

                InputStream input = connection.getInputStream();
                final Bitmap bitmap = BitmapFactory.decodeStream(input);

                if (!imageLoadCancelled && bitmap != null) {
                    mainHandler.post(() -> {
                        if (!imageLoadCancelled) {
                            imgThumbnail.setImageBitmap(bitmap);
                        }
                    });
                }
                input.close();
                connection.disconnect();
            } catch (Exception e) {
                if (!imageLoadCancelled) {
                    mainHandler.post(() ->
                            imgThumbnail.setImageResource(R.drawable.pdiddy));
                }
            }
        }

        public void cancelImageLoading() {
            imageLoadCancelled = true;
        }
    }

    public void shutdown() {
        executorService.shutdown();
    }
}