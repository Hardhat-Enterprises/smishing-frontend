package com.example.smishingdetectionapp.notifications.clipboard;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.smishingdetectionapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ClipboardHistory extends AppCompatActivity {

    private LinearLayout itemContainer;
    private LinearLayout paginationContainer;
    private Button btnNext, btnPrev;
    private int page = 0;
    private int totalPages = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clipboard_history);

        itemContainer = findViewById(R.id.itemContainer);
        paginationContainer = findViewById(R.id.paginationContainer);
        btnNext = findViewById(R.id.btnNext);
        btnPrev = findViewById(R.id.btnPrev);

        btnNext.setOnClickListener(v -> {
            if (page < totalPages - 1) {
                page++;
                //fetchData(page);
                fetchDataBypass(page);
            }
        });

        btnPrev.setOnClickListener(v -> {
            if (page > 0) {
                page--;
                //fetchData(page);
                fetchDataBypass(page);
            }
        });

        //fetchData(page);
        fetchDataBypass(page);
    }

    private void fetchDataBypass(int pageNumber) {
        new Handler(Looper.getMainLooper()).post(() -> {
            itemContainer.removeAllViews();
            paginationContainer.removeAllViews();

            // === Fake pagination data ===
            totalPages = 5; // Simulate 5 pages
            page = pageNumber;

            // Simulate 10 items per page
            for (int i = 0; i < 10; i++) {
                final String text = "Demo text item " + (i + 1 + (page * 10));
                final String scannedAt = "2025-05-14 12:00:0" + i;
                final String itemId = "demo_" + (i + 1 + (page * 10));

                LinearLayout row = new LinearLayout(this);
                row.setOrientation(LinearLayout.HORIZONTAL);
                row.setPadding(10, 10, 10, 10);
                row.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                ));

                TextView indexView = new TextView(this);
                indexView.setText((i + 1 + (page * 10)) + ".");
                indexView.setPadding(10, 0, 20, 0);

                TextView textView = new TextView(this);
                textView.setText(text);
                textView.setMaxLines(2);
                textView.setEllipsize(TextUtils.TruncateAt.END);
                textView.setTextColor(Color.BLACK);
                textView.setTextSize(16);

                TextView dateView = new TextView(this);
                dateView.setText("Detected On: " + scannedAt);
                dateView.setTextColor(Color.DKGRAY);
                dateView.setTextSize(14);

                LinearLayout content = new LinearLayout(this);
                content.setOrientation(LinearLayout.VERTICAL);
                content.setLayoutParams(new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1
                ));
                content.addView(textView);
                content.addView(dateView);

                Button deleteBtn = new Button(this);
                deleteBtn.setText("Delete");
                deleteBtn.setOnClickListener(v -> {
                    Toast.makeText(this, "Delete: " + itemId, Toast.LENGTH_SHORT).show();
                });

                row.addView(indexView);
                row.addView(content);
                row.addView(deleteBtn);
                itemContainer.addView(row);

                row.setOnClickListener(v -> {
                    Intent intent = new Intent(ClipboardHistory.this, ClipboardDetail.class);
                    intent.putExtra("fullText", text);
                    intent.putExtra("scannedAt", scannedAt);
                    startActivity(intent);
                });
            }

            // === Pagination buttons ===

            if (page > 0) {
                paginationContainer.addView(createPageButton("<<", 0));
            }

            btnPrev.setEnabled(page > 0);
            btnNext.setEnabled(page < totalPages - 1);

            int start = Math.max(0, page - 2);
            int end = Math.min(totalPages, page + 3);

            if (start > 0) {
                paginationContainer.addView(createDisabledButton("..."));
            }

            for (int i = start; i < end; i++) {
                Button pageBtn = new Button(this);
                pageBtn.setText(String.valueOf(i + 1));

                ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(5, 0, 5, 0);
                pageBtn.setLayoutParams(params);

                if (i == page) {
                    pageBtn.setEnabled(false);
                    pageBtn.setBackgroundColor(Color.parseColor("#FF6200EE"));
                    pageBtn.setTextColor(Color.WHITE);
                } else {
                    int pageIndex = i;
                    pageBtn.setOnClickListener(v -> fetchDataBypass(pageIndex));
                }

                paginationContainer.addView(pageBtn);
            }

            if (end < totalPages) {
                paginationContainer.addView(createDisabledButton("..."));
            }

            if (page < totalPages - 1) {
                paginationContainer.addView(createPageButton(">>", totalPages - 1));
            }
        });
    }


    private void fetchData(int pageNumber) {
        new Thread(() -> {
            try {
                URL url = new URL("http://192.168.0.123:5000/history?page=" + pageNumber);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder builder = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        builder.append(line);
                    }
                    reader.close();

                    JSONObject response = new JSONObject(builder.toString());
                    JSONArray jsonArray = response.getJSONArray("data");
                    totalPages = response.getInt("total_pages");
                    page = response.getInt("current_page");

                    new Handler(Looper.getMainLooper()).post(() -> {
                        itemContainer.removeAllViews();
                        paginationContainer.removeAllViews();

                        // Items list
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject item = null;
                            try {
                                item = jsonArray.getJSONObject(i);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                // throw new RuntimeException(e);
                            }
                            String text = null;
                            try {
                                text = item.getString("text");
                            } catch (JSONException e) {
                                e.printStackTrace();
                                // throw new RuntimeException(e);
                            }
                            String scannedAt = null;
                            try {
                                scannedAt = item.getString("scanned_at");
                            } catch (JSONException e) {
                                e.printStackTrace();
                                // throw new RuntimeException(e);
                            }
                            String itemIdTemp = null;
                            try {
                                itemIdTemp = item.getString("_id");
                            } catch (JSONException e) {
                                e.printStackTrace();
                                // throw new RuntimeException(e);
                            }

                            final String itemId = itemIdTemp;

                            LinearLayout row = new LinearLayout(this);
                            row.setOrientation(LinearLayout.HORIZONTAL);
                            row.setPadding(10, 10, 10, 10);
                            row.setLayoutParams(new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                            ));

                            TextView indexView = new TextView(this);
                            indexView.setText((i + 1 + (page * 10)) + ".");
                            indexView.setPadding(10, 0, 20, 0);

                            TextView textView = new TextView(this);
                            textView.setText(text);
                            textView.setMaxLines(2);
                            textView.setEllipsize(TextUtils.TruncateAt.END);
                            //textView.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
                            textView.setTextColor(Color.BLACK);
                            textView.setTextSize(16);

                            TextView dateView = new TextView(this);
                            dateView.setText("Detected On: " + scannedAt);
                            dateView.setTextColor(Color.DKGRAY);
                            dateView.setTextSize(14);

                            LinearLayout content = new LinearLayout(this);
                            content.setOrientation(LinearLayout.VERTICAL);
                            content.setLayoutParams(new LinearLayout.LayoutParams(
                                    0,
                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                    1  // Give weight to content, not textView itself
                            ));
                            content.addView(textView);
                            content.addView(dateView);

                            Button deleteBtn = new Button(this);
                            deleteBtn.setText("Delete");

                            deleteBtn.setOnClickListener(v->deleteItem(itemId));

                            row.addView(indexView);
                            row.addView(content);
                            row.addView(deleteBtn);
                            itemContainer.addView(row);

                            String finalText = text;
                            String finalScannedAt = scannedAt;
                            row.setOnClickListener(v -> {
                                Intent intent = new Intent(ClipboardHistory.this, ClipboardDetail.class);
                                intent.putExtra("fullText", finalText);
                                intent.putExtra("scannedAt", finalScannedAt);
                                startActivity(intent);
                            });
                        }

                        // === Pagination ===

                        // First page <<
                        if (page > 0) {
                            paginationContainer.addView(createPageButton("<<", 0));
                        }

                        // Prev button
                        btnPrev.setEnabled(page > 0);
                        btnNext.setEnabled(page < totalPages - 1);

                        int start = Math.max(0, page - 2);
                        int end = Math.min(totalPages, page + 3);

                        // Ellipsis at start
                        if (start > 0) {
                            paginationContainer.addView(createDisabledButton("..."));
                        }

                        for (int i = start; i < end; i++) {
                            Button pageBtn = new Button(this);
                            pageBtn.setText(String.valueOf(i + 1));

                            ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(
                                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            params.setMargins(5, 0, 5, 0);
                            pageBtn.setLayoutParams(params);

                            if (i == page) {
                                pageBtn.setEnabled(false);
                                pageBtn.setBackgroundColor(Color.parseColor("#FF6200EE"));
                                pageBtn.setTextColor(Color.WHITE);
                            } else {
                                int pageIndex = i;
                                pageBtn.setOnClickListener(v -> fetchData(pageIndex));
                            }

                            paginationContainer.addView(pageBtn);
                        }

                        // Ellipsis at end
                        if (end < totalPages) {
                            paginationContainer.addView(createDisabledButton("..."));
                        }

                        // Last page >>
                        if (page < totalPages - 1) {
                            paginationContainer.addView(createPageButton(">>", totalPages - 1));
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                new Handler(Looper.getMainLooper()).post(() ->
                        Toast.makeText(this, "Failed to load data", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private Button createPageButton(String label, int targetPage) {
        Button button = new Button(this);
        button.setText(label);
        ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(5, 0, 5, 0);
        button.setLayoutParams(params);
        button.setOnClickListener(v -> fetchData(targetPage));
        return button;
    }

    private Button createDisabledButton(String label) {
        Button button = new Button(this);
        button.setText(label);
        button.setEnabled(false);
        ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(5, 0, 5, 0);
        button.setLayoutParams(params);
        return button;
    }

    private void deleteItem(String id) {
        new Thread(() -> {
            try {
                URL url = new URL("http://192.168.0.123:5000/delete?id=" + id);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("DELETE");

                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        Toast.makeText(this, "Deleted!", Toast.LENGTH_SHORT).show();
                        fetchData(page); // Refresh page
                    });
                } else {
                    new Handler(Looper.getMainLooper()).post(() ->
                            Toast.makeText(this, "Delete failed", Toast.LENGTH_SHORT).show());
                }
            } catch (Exception e) {
                e.printStackTrace();
                new Handler(Looper.getMainLooper()).post(() ->
                        Toast.makeText(this, "Error occurred", Toast.LENGTH_SHORT).show());
            }
        }).start();
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