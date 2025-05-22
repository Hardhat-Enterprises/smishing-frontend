package com.example.smishingdetectionapp.notifications.clipboard;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.example.smishingdetectionapp.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class ClipboardService extends Service {
    public static final String CHANNEL_ID = "ServiceChannel";
    private long lastCopiedTime = 0;
    private String lastCopiedText = "";
    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Service Running")
                .setContentText("Monitoring in background")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .build();
        startForeground(1, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Register clipboard listener
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        if (clipboard != null) {
            clipboard.addPrimaryClipChangedListener(() -> {
                ClipData clip = clipboard.getPrimaryClip();
                if (clip != null && clip.getItemCount() > 0) {
                    CharSequence copiedText = clip.getItemAt(0).getText();
                    if (copiedText != null) {
                        long currentTime = System.currentTimeMillis();
                        if (!copiedText.toString().equals(lastCopiedText) || (currentTime - lastCopiedTime > 1000)) {
                            lastCopiedTime = currentTime;
                            lastCopiedText = copiedText.toString();
                            //sendTextToBackend(copiedText.toString());
                            sendTextToBackendBypass(copiedText.toString());
                        }
                    }
                }
            });
        }
        return START_STICKY;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Background Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    private void sendTextToBackendBypass(String text) {
        String[] suspiciousWords = {"password", "bank", "otp", "card", "ssn", "secret"};

        for (String word : suspiciousWords) {
            if (text.toLowerCase().contains(word)) {
                // Simulate backend warning
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(() -> Toast.makeText(getApplicationContext(),
                        "Local Check: Suspicious content detected!",
                        Toast.LENGTH_LONG).show());
                return;
            }
        }
    }

    private void sendTextToBackend(String text) {
        new Thread(() -> {
            try {
                URL url = new URL("http://192.168.0.123:5000/check"); // or /check for Flask
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                // Include both text and pin
                String postData = "text=" + URLEncoder.encode(text, "UTF-8");

                OutputStream os = conn.getOutputStream();
                os.write(postData.getBytes());
                os.flush();
                os.close();

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream is = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    // Parse response
                    JSONObject jsonResponse = new JSONObject(response.toString());
                    String status = jsonResponse.optString("status");

                    if ("bad".equalsIgnoreCase(status)) {
                        // Show toast
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(() -> Toast.makeText(getApplicationContext(),
                                "Backend: Suspicious content detected!",
                                Toast.LENGTH_LONG).show());
                    }
                }

            } catch (Exception e) {
                // Ignore or log the error
                // e.printStackTrace();
            }
        }).start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Stop tasks if needed
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /*
        # testing backend code

        from flask import Flask, request, jsonify
        from pymongo import MongoClient
        from datetime import datetime
        from bson.objectid import ObjectId
        from bson.objectid import ObjectId, InvalidId

        app = Flask(__name__)

        # Connect to MongoDB
        client = MongoClient("mongodb://localhost:27017")
        db = client["user"]
        collection = db["smishing_history"]


        @app.route("/check", methods=["POST"])
        def check_text():
            text = request.form.get("text", "").lower()

            suspicious_keywords = ["password", "bank", "otp", "card", "ssn", "secret"]
            status = "good"

            for keyword in suspicious_keywords:
                if keyword in text:
                    status = "bad"
                    break

            if status == "bad":
                # Save to database
                record = {"text": text, "status": status, "scanned_at": datetime.now()}
                collection.insert_one(record)

            return jsonify({"status": status})


        @app.route("/history")
        def get_paginated_history():
            page = max(int(request.args.get("page", 0)), 0)
            limit = 10
            total_records = collection.count_documents({})
            total_pages = (total_records + limit - 1) // limit
            skip = page * limit

            cursor = collection.find().sort("scanned_at", -1).skip(skip).limit(limit)
            records = []
            for doc in cursor:
                doc["_id"] = str(doc["_id"])
                doc["scanned_at"] = doc["scanned_at"].strftime("%d-%m-%Y")
                records.append(doc)

            return jsonify({"data": records, "current_page": page, "total_pages": total_pages})


        @app.route("/delete", methods=["DELETE"])
        def delete_record():
            record_id = request.args.get("id")
            if not record_id:
                return jsonify({"error": "Missing ID"}), 400

            try:
                obj_id = ObjectId(record_id)
            except InvalidId:
                return jsonify({"error": "Invalid ObjectId"}), 400

            result = collection.delete_one({"_id": obj_id})
            if result.deleted_count == 1:
                return jsonify({"status": "success"}), 200
            else:
                return jsonify({"error": "Record not found"}), 404


        if __name__ == "__main__":
            app.run(host="0.0.0.0", port=5000)

     */
}
