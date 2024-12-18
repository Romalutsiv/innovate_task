import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.ArrayList;
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

    private List<Document> documentsStorage = new ArrayList<>();

    /**
     * Implementation of this method should upsert the document to your storage
     * And generate unique id if it does not exist, don't change [created] field
     * <h2>
     *  Generate ID if not present. Check creation date.
     * </h2>
     *
     * @param document - document content and author data
     * @return saved document
     */
    public Document save(Document document) {
        generateDocumentId(document);
        if (document.getCreated() == null){
            document.setCreated(Instant.now());
        }
        this.documentsStorage.add(document);
        return document;
    }

    /**
     * Check if ID is present in document.
     * Generate unique ID and set it to document
     *
     * @param document to check and generate id.
     */
    private void generateDocumentId(Document document){
        if (document.getId() == null || document.getId().isEmpty()){
            document.setId(generateId());
            generateDocumentId(document);
        }
        Optional<Document> byId = findById(document.getId());
        if (byId.isPresent()){
            document.setId(generateId());
            generateDocumentId(document);
        }
    }

    /**
     * Generate unique ID.
     *
     * @return random string UUID.
     */
    private String generateId(){
        return UUID.randomUUID().toString();
    }

    /**
     * Implementation this method should find documents which match with request
     * <h2>
     * Filtered storage by present params
     *  </h2>
     * @param request - search request, each field could be null
     * @return list matched documents
     */
    public List<Document> search(SearchRequest request) {

        return this.documentsStorage.stream()
                .filter(document -> request.getTitlePrefixes() == null ||
                        request.getTitlePrefixes().isEmpty() ||
                        request.getTitlePrefixes()
                                .stream().anyMatch(prefix -> document.getTitle().startsWith(prefix)))
                .filter(document -> request.getContainsContents() == null ||
                        request.getContainsContents().isEmpty() ||
                        request.getContainsContents()
                                .stream().anyMatch(content -> document.getContent().contains(content)))
                .filter(document -> request.getAuthorIds() == null ||
                        request.getAuthorIds().isEmpty() ||
                        request.getAuthorIds().contains(document.getAuthor().getId()))
                .filter(document -> request.getCreatedFrom() == null ||
                        !document.getCreated().isBefore(request.getCreatedFrom()))
                .filter(document -> request.getCreatedTo() == null ||
                        !document.getCreated().isAfter(request.getCreatedTo()))
                .toList();
    }


    /**
     * Implementation this method should find document by id
     * <h2>
     *Filtered storage by document ID
     *</h2>
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