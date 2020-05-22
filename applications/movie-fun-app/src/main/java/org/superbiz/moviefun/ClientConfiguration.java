package org.superbiz.moviefun;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestOperations;
import org.springframework.web.util.UriComponentsBuilder;
import org.superbiz.moviefun.albumsapi.AlbumsClient;
import org.superbiz.moviefun.moviesapi.MoviesClient;

@Configuration
public class ClientConfiguration {

    String albumsUrl = "//album-service/albums";
    String moviesUrl = "//movie-service/movies";

    @Bean
    public AlbumsClient albumsClient(RestOperations restOperations) {
        return new AlbumsClient(UriComponentsBuilder.fromUriString(albumsUrl).build().getPath(), restOperations);
    }

    @Bean
    public MoviesClient moviesClient(RestOperations restOperations) {
        return new MoviesClient(UriComponentsBuilder.fromUriString(moviesUrl).build().getPath(), restOperations);
    }
}
