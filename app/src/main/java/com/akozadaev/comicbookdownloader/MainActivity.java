package com.akozadaev.comicbookdownloader;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.akozadaev.comicbookdownloader.api.model.ComicsDTO;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.BiConsumer;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    CompositeDisposable disposable = new CompositeDisposable();

    RecyclerView recyclerView;
    Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.list);

        adapter = new Adapter();

        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        getSupportActionBar().setTitle(getString(R.string.choose_day));

        App app = (App) getApplication();

        disposable.add(app.getService().getApi().getCharacters()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BiConsumer<List<ComicsDTO>, Throwable>() {
                    @Override
                    public void accept(List<ComicsDTO> dates, Throwable throwable) throws Exception {
                        if (throwable != null) {
                            Toast.makeText(MainActivity.this, "Data loading error", Toast.LENGTH_SHORT).show();
                        } else {
                            adapter.setComicses(dates);
                        }
                    }
                }));
    }

    @Override
    protected void onDestroy() {
        disposable.dispose();
        super.onDestroy();
    }

    private class Adapter extends RecyclerView.Adapter<ViewHolder> {

        ArrayList<ComicsDTO> comicsDTOS = new ArrayList<>();

        public void setComicses(List<ComicsDTO> comicses) {
            this.comicsDTOS.clear();
            this.comicsDTOS.addAll(comicses);
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new ViewHolder(LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.item_date, viewGroup, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
            viewHolder.bind(comicsDTOS.get(i));
        }

        @Override
        public int getItemCount() {
            return comicsDTOS.size();
        }
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {

        ComicsDTO comicsDTO;

        TextView text;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.text);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CharactersListActivity.start(view.getContext(), comicsDTO.getName());
                }
            });
        }

        public void bind(ComicsDTO comics) {
            comicsDTO = comics;
            text.setText(comics.getName());
        }
    }
}
