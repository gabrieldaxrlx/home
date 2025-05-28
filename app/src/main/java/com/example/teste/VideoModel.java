package com.example.teste;

import androidx.annotation.NonNull;

public class VideoModel {
    private final String title;
    private final String channel;
    private final String thumbnailUrl;
    private final String videoUrl;
    private String videoId; // Extracted from URL for convenience
    private long duration; // In milliseconds
    private int viewCount;

    public VideoModel(@NonNull String title,
                      @NonNull String channel,
                      @NonNull String thumbnailUrl,
                      @NonNull String videoUrl) {
        this.title = title != null ? title : "";
        this.channel = channel != null ? channel : "";
        this.thumbnailUrl = thumbnailUrl != null ? thumbnailUrl : "";
        this.videoUrl = videoUrl != null ? videoUrl : "";
        this.videoId = extractVideoId(videoUrl);
    }

    // Additional constructor with more metadata
    public VideoModel(@NonNull String title,
                      @NonNull String channel,
                      @NonNull String thumbnailUrl,
                      @NonNull String videoUrl,
                      long duration,
                      int viewCount) {
        this(title, channel, thumbnailUrl, videoUrl);
        this.duration = duration;
        this.viewCount = viewCount;
    }

    private String extractVideoId(String url) {
        if (url == null || url.isEmpty()) return "";

        // Handle different YouTube URL formats
        if (url.contains("v=")) {
            return url.split("v=")[1].split("&")[0];
        } else if (url.contains("youtu.be/")) {
            return url.split("youtu.be/")[1].split("[?]")[0];
        }
        return "";
    }

    // Getters
    @NonNull
    public String getTitle() {
        return title;
    }

    @NonNull
    public String getChannel() {
        return channel;
    }

    @NonNull
    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    @NonNull
    public String getVideoUrl() {
        return videoUrl;
    }

    public String getVideoId() {
        return videoId;
    }

    public long getDuration() {
        return duration;
    }

    public int getViewCount() {
        return viewCount;
    }

    // Formatting methods for UI
    public String getFormattedDuration() {
        long seconds = duration / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;

        seconds %= 60;
        minutes %= 60;

        if (hours > 0) {
            return String.format("%d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%d:%02d", minutes, seconds);
        }
    }

    public String getFormattedViewCount() {
        if (viewCount >= 1000000) {
            return String.format("%.1fM views", viewCount / 1000000.0);
        } else if (viewCount >= 1000) {
            return String.format("%.1fK views", viewCount / 1000.0);
        }
        return viewCount + " views";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VideoModel that = (VideoModel) o;
        return videoUrl.equals(that.videoUrl);
    }

    @Override
    public int hashCode() {
        return videoUrl.hashCode();
    }

    @NonNull
    @Override
    public String toString() {
        return "VideoModel{" +
                "title='" + title + '\'' +
                ", channel='" + channel + '\'' +
                ", videoId='" + videoId + '\'' +
                '}';
    }
}