package io.geekya215.lava.tokenizer;

public sealed interface Annotations permits Annotations.REST, Annotations.WHOLE  {
    record REST() implements Annotations {
        @Override
        public String toString() {
            return "REST";
        }
    }

    record WHOLE() implements Annotations {
        @Override
        public String toString() {
            return "WHOLE";
        }
    }
}
