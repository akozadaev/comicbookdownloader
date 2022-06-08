package com.akozadaev.comicbookdownloader;

import com.akozadaev.comicbookdownloader.api.model.ComicsDTO;

import java.util.List;
import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ComicsApi {
    @GET("v1/public/characters")
    Single<List<ComicsDTO>> getCharacters();
    @GET("v1/public/characters/{characterId}")
    Single<List<ComicsDTO>> getCharacter(@Path("characterId") String date);
}
