package com.akozadaev.comicbookdownloader;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.akozadaev.comicbookdownloader.api.model.PhotoDTO;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class PhotoListActivity extends AppCompatActivity {

    private static final String EXTRA_DATE = "PhotoListActivity.EXTRA_DATE";

    CompositeDisposable disposable = new CompositeDisposable();

    RecyclerView recyclerView;
    Adapter adapter;

    public static void start(Context caller, String date) {
        Intent intent = new Intent(caller, PhotoListActivity.class);
        intent.putExtra(EXTRA_DATE, date);
        caller.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.list);

        adapter = new Adapter();

        getSupportActionBar().setTitle(getString(R.string.choose_time));

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);

        App app = (App) getApplication();

        disposable.add(app.getService().getApi().getPhotosForDate(getIntent().getStringExtra(EXTRA_DATE))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe((photos, throwable) -> {
                    if (throwable != null) {
                        Toast.makeText(PhotoListActivity.this, "Data loading error", Toast.LENGTH_SHORT).show();
                    } else {
                        adapter.setPhotos(photos);
                    }
                }));
    }

    @Override
    protected void onDestroy() {
        disposable.dispose();
        super.onDestroy();
    }

    private static class Adapter extends RecyclerView.Adapter<PhotoItemViewHolder> {

        private final ArrayList<PhotoDTO> photos = new ArrayList<>();

        @SuppressLint("NotifyDataSetChanged")
        public void setPhotos(List<PhotoDTO> photos) {
            this.photos.clear();
            this.photos.addAll(photos);
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public PhotoItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new PhotoItemViewHolder(LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.item_photo, viewGroup, false));
        }

        @Override
        public void onBindViewHolder(@NonNull PhotoItemViewHolder photoItemViewHolder, int i) {
            photoItemViewHolder.bind(photos.get(i));
        }

        @Override
        public int getItemCount() {
            return photos.size();
        }
    }

    private static class PhotoItemViewHolder extends RecyclerView.ViewHolder {

        TextView text;
        PhotoDTO photo;

        public PhotoItemViewHolder(@NonNull View itemView) {
            super(itemView);
            text = (TextView) itemView.findViewById(R.id.text);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PhotoActivity.start(view.getContext(), photo.getImageUrl());
                }
            });
        }

        public void bind(PhotoDTO photo) {
            text.setText(photo.getDate());
            this.photo = photo;
        }
    }
}
