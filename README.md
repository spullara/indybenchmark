
Build the `indybench` executable using maven:

```
mvn clean install
```

This benchmark calls methods using a variety of mechanisms:

- Mustache.java Reflection Object Handler
- Mustache.java Codegenerated guards + Reflection Object Handler
- Invokedynamic
- Mustache.java Invokedynamic Object Handler with No Guards
- Mustache.java Invokedynamic Object Handler
- Lookup + Reflection
- Cached Lookup + Reflection
- Lookup + Unflection -> Method Handle
- Cached Lookup + Unflection -> Method Handle
- Lookup + Method Handle
- Cached Lookup + Method Handle
- Direct call

This benchmark reproduces various types of deoptimization behavior when you access the same method
using different mechanisms. For example, the Indy OH is almost as fast as a direct call when you benchmark
them against each other. When benchmarked with other mechanisms it can go as slow as 20x worse.

Running a couple of benchmarks:

```
$ ./target/appassembler/bin/indybench -indy -direct -n 5
indy wrapper: 47
direct: 16
-----------------
indy wrapper: 25
direct: 20
-----------------
indy wrapper: 3
direct: 5
-----------------
indy wrapper: 5
direct: 8
-----------------
indy wrapper: 4
direct: 7
-----------------
```

Running all benchmarks:

```
$ ./target/appassembler/bin/indybench -all -n 3
reflection OH: 1255
codegen reflection OH: 1276
indy wrapper: 48
indy wrapper no guard: 29
indy OH: 1127
reflection: 27120
reflection cached: 1409
unreflection: 620000
unreflection cached: 1056
methodhandle: 480000
methodhandle cached: 956
direct: 19
-----------------
reflection OH: 1556
codegen reflection OH: 1559
indy wrapper: 1103
indy wrapper no guard: 22
indy OH: 1076
reflection: 25220
reflection cached: 1524
unreflection: 200000
unreflection cached: 1041
methodhandle: 260000
methodhandle cached: 1035
direct: 16
-----------------
reflection OH: 1155
codegen reflection OH: 1369
indy wrapper: 1025
indy wrapper no guard: 76
indy OH: 1147
reflection: 24150
reflection cached: 1184
unreflection: 130000
unreflection cached: 900
methodhandle: 170000
methodhandle cached: 882
direct: 6
-----------------
```
