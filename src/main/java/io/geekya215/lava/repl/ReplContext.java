package io.geekya215.lava.repl;

import io.geekya215.lava.Constants;

import java.nio.file.Path;

public final class ReplContext {
    private Path fileLoadPath;
    private String prompt;
    private String indicator;

    public ReplContext(Path fileLoadPath, String prompt, String indicator) {
        this.fileLoadPath = fileLoadPath;
        this.prompt = prompt;
        this.indicator = indicator;
    }


    public ReplContext(Path fileLoadPath) {
        this(fileLoadPath, Constants.DEFAULT_PROMPT, Constants.DEFAULT_INDICATOR);
    }

    public Path getFileLoadPath() {
        return fileLoadPath;
    }

    public void setFileLoadPath(Path fileLoadPath) {
        this.fileLoadPath = fileLoadPath;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public String getIndicator() {
        return indicator;
    }

    public void setIndicator(String indicator) {
        this.indicator = indicator;
    }
}
