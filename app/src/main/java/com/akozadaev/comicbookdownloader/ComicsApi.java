package com.akozadaev.comicbookdownloader;

import com.akozadaev.comicbookdownloader.api.model.DateDTO;
import com.akozadaev.comicbookdownloader.api.model.PhotoDTO;

import java.util.List;
import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ComicsApi {
    @GET("natural/all")
    Single<List<DateDTO>> getDatesWithPhoto();

    @GET("natural/date/{date}")
    Single<List<PhotoDTO>> getPhotosForDate(@Path("date") String date);
}
