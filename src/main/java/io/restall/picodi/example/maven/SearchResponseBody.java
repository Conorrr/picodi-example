package io.restall.picodi.example.maven;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class SearchResponseBody {

    private final Response response;

    public SearchResponseBody(@JsonProperty("response") Response response) {
        this.response = response;
    }

    public Response getResponse() {
        return response;
    }

    public static class Response {
        private final int numFound;

        private final List<Doc> docs;

        public Response(@JsonProperty("numFound") int numFound, @JsonProperty("docs") List<Doc> docs) {
            this.numFound = numFound;
            this.docs = docs;
        }

        public int getNumFound() {
            return numFound;
        }

        public List<Doc> getDocs() {
            return docs;
        }
    }

    public static class Doc {
        private final String id;

        private final String group;

        private final String artifact;

        private final String latestVersion;

        public Doc(@JsonProperty("id") String id, @JsonProperty("g") String group, @JsonProperty("a") String artifact,
                   @JsonProperty("latestVersion") String latestVersion) {
            this.id = id;
            this.group = group;
            this.artifact = artifact;
            this.latestVersion = latestVersion;
        }

        public String getId() {
            return id;
        }

        public String getGroup() {
            return group;
        }

        public String getArtifact() {
            return artifact;
        }

        public String getLatestVersion() {
            return latestVersion;
        }
    }
}
