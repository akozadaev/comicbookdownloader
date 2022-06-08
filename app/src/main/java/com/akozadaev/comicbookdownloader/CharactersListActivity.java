package com.akozadaev.comicbookdownloader;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.akozadaev.comicbookdownloader.api.model.ComicsDTO;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.BiConsumer;
import io.reactivex.schedulers.Schedulers;

public class CharactersListActivity extends AppCompatActivity {

    private static final String EXTRA_DATE = "CharactersListActivity.EXTRA_DATE";

    CompositeDisposable disposable = new CompositeDisposable();

    RecyclerView recyclerView;
    Adapter adapter;

    public static void start(Context caller, String date) {
        Intent intent = new Intent(caller, CharactersListActivity.class);
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

        disposable.add(app.getService().getApi().getCharacter(getIntent().getStringExtra(EXTRA_DATE))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new BiConsumer<List<ComicsDTO>, Throwable>() {
                    @Override
                    public void accept(List<ComicsDTO> photos, Throwable throwable) throws Exception {
                        if (throwable != null) {
                            Toast.makeText(CharactersListActivity.this, "Data loading error", Toast.LENGTH_SHORT).show();
                        } else {
                            adapter.setPhotos(photos);
                        }
                    }
                }));
    }

    @Override
    protected void onDestroy() {
        disposable.dispose();
        super.onDestroy();
    }

    private static class Adapter extends RecyclerView.Adapter<PhotoItemViewHolder> {

        private ArrayList<ComicsDTO> photos = new ArrayList<>();

        public void setPhotos(List<ComicsDTO> photos) {
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
        ComicsDTO photo;

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

        public void bind(ComicsDTO photo) {
            text.setText(photo.getDate());
            this.photo = photo;
        }
    }
}
