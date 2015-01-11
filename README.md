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
