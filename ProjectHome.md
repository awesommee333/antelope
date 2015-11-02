**Antelope** (formerly "`OPIA`") aims to provide the fundamental aspects of polymorphism in an easy-to-use format suitable for a limited [z80](http://en.wikipedia.org/wiki/Z80) environment. Rather than packaging objects and methods into a [single package](http://en.wikipedia.org/wiki/Class_(computer_programming)), structures and methods are provided as separate entities free of any built-in framework. However, these can be combined with easy to use [function pointers](http://en.wikipedia.org/wiki/Function_pointer) and [interfaces](http://en.wikipedia.org/wiki/Interface_(object-oriented_programming)), resulting in statically typed code with [dynamic behavior](http://en.wikipedia.org/wiki/Duck_typing) which is more flexible than would be possible with a rigid [class hierarchy](http://en.wikipedia.org/wiki/Inheritance_(computer_science)). Antelope also provides [function objects](http://en.wikipedia.org/wiki/Function_object) ("cofunctions") as an explicit and unobfuscated model for closures, coroutines, and iterators/generators.

Additionally, the Antelope compiler will use data-flow analysis to "pre-interpret" as much of the program as possible (predict values, remove conditional-branches, etc.). Various elements of code (functions, variables, expressions, etc.) may be marked with `$` to explicitly require interpreting to be done. For example, a function can serve as a "smart macro", or an array can be pre-populated algorithmically.

The compiler is to be coded in Java, and will operate both as a command-line interface and a modularized Java API to expose every component of the compiler/compilation process to other external tools (editors, other-than-text editors, partial compilation, etc.). Antelope code is geared to run efficiently in a bare z80 environment (embedded) with minimal overhead, though it can also be interfaced directly with system values and routines by associating variables and functions with explicit assembly addresses.

Antelope spawned as the aftermath to [a previous project](http://tinyurl.com/adsmblg) which has been left as an early learning experience. Since then, various languages, books, and other resources have been analyzed, and various language features and paradigms have been studied, decomposed, reconstructed, and even formulated from scratch to design the right set of features for Antelope. Here are some of those sources:

  * Languages: [Go](http://http://golang.org/), [C#](http://msdn.microsoft.com/en-us/vstudio/hh341490), [Kotlin](http://kotlin.jetbrains.org/), [Scala](http://www.scala-lang.org/), [Java](http://www.oracle.com/technetwork/java/index.html), [C/C++](http://msdn.microsoft.com/en-us/vstudio/hh386302), [Lua](http://www.lua.org/), [Proton](http://oisyn.nl/proton/proton.html), and many others
  * [Modern Compiler Implementation in Java](http://tinyurl.com/mciij)
  * [The Dragon Book](http://tinyurl.com/dragbook)
  * [Jack Crenshaw's Tutorials](http://tinyurl.com/jclbac)
  * [Revisiting Coroutines](http://tinyurl.com/revcor)
  * [Traits: Composable Units of Behaviour](http://tinyurl.com/ooptraits)

Further Documentation and Discussion:
  * [Language Overview](http://code.google.com/p/antelope/wiki/Overview)
  * [Language Grammar](http://code.google.com/p/antelope/wiki/Grammar).
  * [Discussion on Cemetech](http://www.cemetech.net/forum/viewtopic.php?t=4675&postdays=0&postorder=asc&start=300)
  * [Discussion on Omnimaga](http://www.omnimaga.org/index.php?topic=9498.0)
  * [Contact me directly](mailto:shkaboinka@gmail.com)