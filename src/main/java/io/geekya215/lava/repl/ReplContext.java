package io.geekya215.lava.repl;

import java.nio.file.Path;

public final class ReplContext {
    private Path fileLoadPath;

    public ReplContext(Path fileLoadPath) {
        this.fileLoadPath = fileLoadPath;
    }

    public Path getFileLoadPath() {
        return fileLoadPath;
    }

    public void setFileLoadPath(Path fileLoadPath) {
        this.fileLoadPath = fileLoadPath;
    }
}
