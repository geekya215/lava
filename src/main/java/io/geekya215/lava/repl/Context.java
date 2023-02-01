package io.geekya215.lava.repl;

public class Context {
    private String prompt;
    private String indicator;

    public Context(String prompt, String indicator) {
        this.prompt = prompt;
        this.indicator = indicator;
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
