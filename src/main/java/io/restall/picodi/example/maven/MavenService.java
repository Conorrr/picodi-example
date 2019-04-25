package io.restall.picodi.example.maven;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;

/**
 * Very simple service that searches for basic information in Maven
 */
public class MavenService {

    private final HttpClient httpClient;

    private final ObjectMapper objectMapper;

    public MavenService(HttpClient httpClient, ObjectMapper objectMapper) {
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
    }

    public SearchResponseBody search(String query) throws URISyntaxException, IOException, InterruptedException {
        String encodedQuery = URLEncoder.encode(query, Charset.defaultCharset());
        URI uri = new URI(String.format("https://search.maven.org/solrsearch/select?q=%s&wt=json", encodedQuery));

        HttpRequest request = HttpRequest.newBuilder(uri)
                .header("Accept", "application/json")
                .build();

        try (InputStream rawResponseBody = httpClient
                .send(request, HttpResponse.BodyHandlers.ofInputStream())
                .body()) {
            return objectMapper.readValue(rawResponseBody, SearchResponseBody.class);
        }
    }

    public static void main(String... args) throws InterruptedException, IOException, URISyntaxException {
        ObjectMapper om = new ObjectMapper();


        SearchResponseBody response = new MavenService(HttpClient.newHttpClient(), om).search("picodi");

        System.out.println(response.getResponse().getNumFound());
        response.getResponse().getDocs().forEach(it -> System.out.println(it.getId()));
    }

}
