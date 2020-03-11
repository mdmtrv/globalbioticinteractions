package org.globalbioticinteractions.content;

import org.eol.globi.util.InputStreamFactory;
import org.globalbioticinteractions.cache.ContentProvenance;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * This pin only resolves to local content related to requested URIs.
 */


public class ContentPinStatic implements ContentPin {

    private final ContentResolver resolver;
    private final ContentStore store;
    private final InputStreamFactory inputStreamFactory;

    ContentPinStatic(ContentResolver resolver,
                     ContentStore store,
                     InputStreamFactory factory) {
        this.resolver = resolver;
        this.store = store;
        this.inputStreamFactory = factory;
    }

    ContentStore getStore() {
        return store;
    }

    @Override
    public URI pin(URI knownContentIdentifier) throws IOException {
        ContentProvenance prov = findFirstContentProvenanceFor(knownContentIdentifier);
        ContentSource retrieve = getStore().retrieve(prov.getContentHash());
        Optional<URI> uri = retrieve
                .getContent()
                .flatMap(x -> {
            try (InputStream is = x) {
                return Optional.ofNullable(prov.getLocalURI());
            } catch (IOException ex) {
                return Optional.empty();
            }
        });

        return uri
                .orElseThrow(getIoExceptionSupplier(knownContentIdentifier, "failed to locate last known content uri [" + prov.getContentHash() + "]"));

    }

    private ContentProvenance findFirstContentProvenanceFor(URI knownContentIdentifier) throws IOException {
        Stream<ContentProvenance> contentProvenanceStream = doQuery(knownContentIdentifier);
        return contentProvenanceStream
                .findFirst()
                .orElseThrow(getIoExceptionSupplier(knownContentIdentifier));
    }

    private Supplier<IOException> getIoExceptionSupplier(URI knownContentIdentifier) {
        return () -> new IOException("failed to pin [" +  knownContentIdentifier + "]");
    }

    private Supplier<IOException> getIoExceptionSupplier(URI knownContentIdentifier, String reason) {
        return () -> new IOException("failed to pin [" +  knownContentIdentifier + "]: " + reason);
    }

    protected Stream<ContentProvenance> doQuery(URI knownContentIdentifier) throws IOException {
        return resolver
                    .query(knownContentIdentifier);
    }

    public InputStreamFactory getInputStreamFactory() {
        return inputStreamFactory;
    }
}