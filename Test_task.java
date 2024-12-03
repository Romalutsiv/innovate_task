import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * For implement this task focus on clear code, and make this solution as simple readable as possible
 * Don't worry about performance, concurrency, etc
 * You can use in Memory collection for sore data
 * <p>
 * Please, don't change class name, and signature for methods save, search, findById
 * Implementations should be in a single class
 * This class could be auto tested
 */
public class DocumentManager {

    private List<Document> documentsStorage;

    /**
     * Implementation of this method should upsert the document to your storage
     * And generate unique id if it does not exist, don't change [created] field
     *
     * @param document - document content and author data
     * @return saved document
     */
    public Document save(Document document) {
        if (document.getId() == null || document.getId().isEmpty()){
            document.setId(generateId());
        }
        Instant created = Instant.now();
        document.setCreated(created);
        this.documentsStorage.add(document);
        return document;
    }
    private String generateId(){
        return UUID.randomUUID().toString();
    }

    /**
     * Implementation this method should find documents which match with request
     *
     * @param request - search request, each field could be null
     * @return list matched documents
     */
    public List<Document> search(SearchRequest request) {

        return this.documentsStorage.stream()
                .filter(document -> request.getTitlePrefixes() == null ||
                        request.getTitlePrefixes()
                                .stream().anyMatch(prefix -> document.getTitle().startsWith(prefix)))
                .filter(document -> request.getContainsContents() == null ||
                        request.getContainsContents()
                                .stream().anyMatch(content -> document.getContent().contains(content)))
                .filter(document -> request.getAuthorIds() == null ||
                        request.getAuthorIds().contains(document.getAuthor().getId()))
                .filter(document -> request.getCreatedFrom() == null ||
                        !document.getCreated().isBefore(request.getCreatedFrom()))
                .filter(document -> request.getCreatedTo() == null ||
                        !document.getCreated().isAfter(request.getCreatedTo()))
                .toList();
    }


    /**
     * Implementation this method should find document by id
     *
     * @param id - document id
     * @return optional document
     */
    public Optional<Document> findById(String id) {

        return this.documentsStorage.stream()
                .filter(document -> document.id.equals(id))
                .findFirst();
    }

    @Data
    @Builder
    public static class SearchRequest {
        private List<String> titlePrefixes;
        private List<String> containsContents;
        private List<String> authorIds;
        private Instant createdFrom;
        private Instant createdTo;
    }

    @Data
    @Builder
    public static class Document {
        private String id;
        private String title;
        private String content;
        private Author author;
        private Instant created;
    }

    @Data
    @Builder
    public static class Author {
        private String id;
        private String name;
    }
}