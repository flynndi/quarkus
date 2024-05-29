package io.quarkus.bootstrap.app;

import java.nio.file.Path;

public final class JarResult {

    private final Path path;
    private final Path originalArtifact;
    private final Path libraryDir;
    private final boolean mutable;
    private final String classifier;

    public JarResult(Path path, Path originalArtifact, Path libraryDir, boolean mutable, String classifier) {
        this.path = path;
        this.originalArtifact = originalArtifact;
        this.libraryDir = libraryDir;
        this.mutable = mutable;
        this.classifier = classifier;
    }

    public boolean isUberJar() {
        return libraryDir == null;
    }

    public Path getPath() {
        return path;
    }

    public Path getLibraryDir() {
        return libraryDir;
    }

    public Path getOriginalArtifact() {
        return originalArtifact;
    }

    public boolean mutable() {
        return mutable;
    }

    public String getClassifier() {
        return classifier;
    }
}
