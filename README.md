# lava
lava is an interpreter implemented in Java. It supports parsing a subset of the Lisp language.
The internal implementation of lava is very concise, and it has some basic operators built in for the user to implement various abstractions.

### Builtin values and operators
- **constants**: `#t` `#f`

- **arithmetic**: `+` `-` `*` `/`

- **comparator**: `<` `<=` `>` `>=` `=`

- **list**: `cons` `car` `cdr` `list`

- **procedure**: `begin` `lambda`

- **macro**: `defmacro`

For lava all values of non-empty lists are treated as `#t`, and arithmetic operators only support integer.

### Usage
The minimum running version of lava is Java 17. Because of the preview feature used in the source code, you need to run lava in the following way

```bash
java --enable-preview -jar lava.jar
```

### Examples
```lisp
lava> (+ 1 2)
=> 3

lava> (> 1 2)
=> #t

lava> (car '(a b c))
=> a

lava> (cdr '(a b c))
=> (b c)

lava> (cons 'a '(b c))
=> (a b c)

lava> (list 'a 1 'b 2)
=> (a 1 b 2)

lava> (begin (define fib (lambda (n) (if (<= n 2) 1 (+ (fib (- n 1)) (fib (- n 2)))))) (fib 10))
=> 55

lava> (((lambda (b) ((lambda (f) (b (lambda (x) ((f f) x)))) (lambda (f) (b (lambda (x) ((f f) x))))))
                  (lambda (fact) (lambda (n) (if (= 0 n) 1 (* n (fact (- n 1)))))))
                  5)
=> 120
```

### Acknowledgments
- [lisp.re](https://github.com/jsdf/lisp.re)
- [(How to Write a (Lisp) Interpreter (in Python))](http://norvig.com/lispy.html)
