# Table of Contents #
  * ## [General Features](Overview#General_Features.md) ##
  * ## [Functions & Methods](Overview#Functions_&_Methods.md) ##
  * ## [Structs, Cofuncs, Interfaces](Overview#Structs,_Cofuncs,_Interfaces.md) ##
  * ## [The Antelope Environment](Overview#The_Antelope_Environment.md) ##
  * ## [Final Modifications](Overview#Final_Modifications.md) ##

<br>
<hr />
<h1>General Features</h1>
<hr />
<br>

<h2>Variable Declarations</h2>

<table><thead><th> <b>Primitive Types</b> </th><th> <i>One Byte</i> </th><th> <i>Two Bytes</i> </th><th> </th><th> <b>Modifiers</b> </th><th> <i>Explanation</i> </th></thead><tbody>
<tr><td> <i>Unsigned Types:</i> </td><td> byte, char, bool </td><td>uint              </td><td> </td><td> <i>volatile</i>  </td><td> Always fetch from memory (never assume value). </td></tr>
<tr><td> <i>Signed Types:</i>   </td><td> sbyte           </td><td> int              </td><td> </td><td> <i>static</i>    </td><td> Share across all contexts (one-time initializations). </td></tr>
<tr><td>                        </td><td>                 </td><td>                  </td><td> </td><td> <i>const</i>     </td><td> Value cannot be modified once set. </td></tr></tbody></table>

<pre><code>   *int pi;    // pi is a pointer-to-int variable<br>
   int x,y,z;   // Declare int variables x, y, and z<br>
   int a=1, b=2; // Declare and initialize a and b to 1 and 2<br>
   const HALF = 1/2; // Constant expressions are declared without a type<br>
   const byte W = 2; // Constant variables are declared with the type<br>
   volatile int v;   // v will always be fetched from memory<br>
<br>
   i := 5;         // Numeric literals are ints by default (i is an int),<br>
   b := (5-&gt;byte); // but they can be cast to other types (b is a byte).<br>
<br>
   h1 := 0x01;     // Hexadecimal and binary literals default to the<br>
   h2 := 0x0001;   // smallest (unsigned) type for the number of digits<br>
   b1 := 0b1;      // (h1 and b1 are bytes, but h2 and b2 are uints).<br>
   b2 := 0b0000000001;<br>
<br>
   p := &amp;x;        // Declare p with an interred type of *int<br>
   q := {'H','I'}; // Declare q with inferred type of [2]char<br>
</code></pre>

Variables are statically allocated (<i>within</i> instructions when possible, which saves space and uses faster instructions); otherwise within "safe-RAM" space (specified with <code>#allocate</code>); otherwise directly into the program. Variables can be associated with an explicit assembly-coded address (e.g. <code>int x @ "asmAddress"</code> aliases x with an int value at "asmAddress").<br>
<br>
<h2>Arrays</h2>

Array types are declared with square brackets <code>[]</code> preceding the base-type. If a size is provided, then the array is statically allocated; otherwise it is stored as a pointer to an array. "Jagged" arrays are constructed as arrays of array-pointers (with the "inner" dimensions omitted). "Rectangular" arrays are stored as a single static allocation, and indicated by listing multiple dimensions between the brackets (e.g. <code>[x,y,z]</code>). Array literals are declared with the dimensions omitted, but result in static allocations:<br>
<pre><code>   // Array types:<br>
      [5]int // (static) array of 5 ints<br>
       []int // pointer to array of ints<br>
      *[]int // pointer to pointer to array of ints<br>
     *[5]int // ILLEGAL! (use []int instead)<br>
<br>
    [5,5]int // (static) array of 5x5 (25) ints<br>
     [,5]int // pointer to array of (?x5) ints<br>
      [,]int // ILLEGAL! (provide an inner dimension, or use []int)<br>
    [5][]int // (static) array of 5 []int values<br>
   [5][5]int // ILLEGAL! (use [5,5]int or [5][]int instead)<br>
<br>
     arr1 := []int{1,2,3,4,5,6,7,8,9};  // arr1 is this [9]int<br>
     arr2 := [,]int{{1,2},{3,4},{5,6}}; // arr2 is this [3,2]int<br>
<br>
     arr3 := &amp;[]int{1,2,3}; // arr3 is given the ADDRESS ("&amp;") of<br>
                            // this [3]int, and is therefor an []int<br>
<br>
     arr4 := new [5]int; // arr4 is given the address of a dynamically<br>
                         // allocated [5]int, and is therefor an []int<br>
<br>
     arr5 := new []int{1,2,3}; // same deal as with arr4, but with the<br>
                               // array initialized with the given values<br>
<br>
   // Rectangular arrays can be represented as linear arrays:<br>
   [X,Y,Z]int arr; // An XxYxZ array of ints<br>
    [,Y,Z]int p3 = arr; // Pointer to an (?)xYxZ array (3D)<br>
      [,Z]int p2 = arr; // Pointer to an (?)xZ array (2D)<br>
        []int p1 = arr; // Pointer to an (?) array (1D)<br>
   p3[a,b,c] == p2[a*Y+b, c] == p1[(a*Y+b)*Z+c] == arr[a,b,c]<br>
   // NOTE ALSO: arr[i] == p1[i]; arr[i,j] == p2[i,j]; etc.<br>
<br>
   // Strings are represented as character arrays:<br>
   str1 := "Null";  // null terminated: []char{'N','u','l','l',0};<br>
   str2 := b"Byte"; // byte prefixed: []char{4,'B','y','t','e'};<br>
   str3 := i"Int";  // int prefixed: []char{3,0,'I','n','t'}; ("3,0" because z80 is little-endian)<br>
   str4 := r"RAW!"; // raw string: []char{'R','A','W','!'};<br>
</code></pre>

<h2>Tuples</h2>

Antelope only provides "tuples" in the loose sense that parenthesis can be used to group values together <i>before</i> they are operated on (i.e. you can operate on a "tuple of values", but a tuple is not a datatype in itself). Thus ((A,B),C) is the same as (A,B,C) in the same manner that ((A+B)+C) is the same as (A+B+C):<br>
<pre><code>   byte a, b = c, d;     // byte a, (b = c), d;<br>
   byte (a, b) = (c, d); // byte a = c, b = d;<br>
   byte a = (((b)));     // byte a = b; (as expected)<br>
  (a,b,c) = (x,y,z);     // a=x; b=y; c=z;<br>
  (a,b,c) = ((x,y),z);   // (same as above)<br>
  (x,y,z) = arr[5..8];   // (x,y,z) = (arr[5],arr[6],arr[7]);<br>
</code></pre>

<h2>Enumerations</h2>

Enums provide generic label-values, and are designed to be very efficient for use with switches:<br>
<br>
<ul><li>An enum may not contain more than 256 values (which are bytes)<br>
</li><li>Enum values can be compared and assigned, but the numeric representation is hidden (and predetermined for use with look-up tables)<br>
</li><li>A look-up-table is used for fast jumping when an enum is used in a switch:</li></ul>

<pre><code>   enum Letter {A, B, C, D, E}<br>
   Letter l1 = Letter.A;<br>
   Letter l2 = l1;<br>
<br>
   if(l1 &gt; l2) { ... }<br>
<br>
   switch(l1) {<br>
      case Letter.A: ...<br>
      case Letter.B: ...<br>
      case Letter.C: ...<br>
   }<br>
<br>
   /* --The Assembly--<br>
    * ld hl,cases   ;3<br>
    * ld bc,(value) ;4<br>
    * add hl,bc     ;1<br>
    * jp (hl)       ;1<br>
    *cases: <br>
    * .dw case_A <br>
    * .dw case_B <br>
    * .dw case_C<br>
    * .dw END_OF_THE_SWITCH ;D<br>
    * .dw END_OF_THE_SWITCH ;E<br>
    */<br>
</code></pre>

<h2>Switch Variables</h2>

Switch-variables are used to directly modify the selected value within a switch. This is much more efficient than using an enum value, because each value of a switch variable corresponds directly with the address of a "case" within its corresponding switch (and therefor can be referred to directly by name without any ambiguity). In other words, a switch variables is not <i>used in a switch</i>, but it actually <i>is part of a specific switch</i>:<br>
<br>
<pre><code>   switch{X,Y,Z} g1 = g1.X; // Values are referred to via switch-name<br>
   switch{X,Y,Z} g2; ... g2 = g2.X; // g1 and g2 are different types!<br>
<br>
   g1 copy1 = g1.Y; // The switch variable name is also a datatype!<br>
   g1 = copy1;<br>
<br>
   switch(g1) { <br>
      case X: ... // X refers to THIS case-address when used with g1<br>
      case Y: ... <br>
      case Z: ... <br>
      // NO default code, because all cases are explicit<br>
   }<br>
<br>
   /* --The Assembly--<br>
    * jp something ;modifying g1 modifies "something" directly<br>
    */<br>
</code></pre>

<h2>Type-Casts</h2>

A Type-cast tells the compiler to convert something to different datatype. Whenever possible, and actual conversion will be avoided (e.g. the compiler will have an operation performed in such a way that the <i>result</i> is of the correct type; or it may try to "pick out" the relevant portion or "mask out" the irrelevant information). Type-casts are written by placing an arrow and the target type after an expression:<br>
<pre><code>   int a, b;<br>
   (a + b) -&gt; byte; // add a + b such that the result is a byte<br>
   (a -&gt; byte) + b; // treat a like a byte while adding<br>
   a + (b -&gt; byte); // treat b like a byte while adding<br>
</code></pre>

<h2>Flow-Control Constructs</h2>

Switches and "else-if" chains are modeled the same way: If there is a clear pattern of values, then one or more look-up-tables are used (with value bounds checking as needed). Otherwise all conditional checks are placed together before all the code-blocks (saves one jump). "While" and "do-while" loops are stored with the conditional checks AFTER the loop body (while loops initially jump to the check after the loop) (saves one jump per iteration):<br>
<pre><code>   if(X) { A }      // If X, then do A;<br>
   else if(Y) { B } // Otherwise if Y, do B;<br>
   ...              // (as many "else-ifs" as you like);<br>
   else { C }       // Otherwise, do C (all "elses" optional).<br>
<br>
   switch(X) {<br>
      case A:   ... break; // Do this code if X==A.<br>
      case B,C: ... break; // Do this code if X==B or X==C.<br>
      case D:   ...  // Fall through to next case (no "break").<br>
      default:  ...  // Default case (optional).<br>
   }<br>
<br>
   while(X) { A }     // While X, do A; repeat.<br>
   until(X) { A }     // Until X, do A; repeat.<br>
   do { A };          // Do A; repeat (forever).<br>
   do { A } while(X); // Do A; repeat while X.<br>
   do { A } until(X); // Do A; repeat until X.<br>
<br>
   do {          // Equivalent to:<br>
       A         // ::        goto start<br>
    =&gt; until(X); // :: top:   B<br>
       B         // :: start: A<br>
   }             // ::        if(!X) goto top<br>
<br>
   for(X; Y; Z) { ... } // X; while(Y) { ... Z }<br>
   for(V,A,B,I) { ... } // for(V=A; V&lt;B; V+=I) { ... }<br>
   for(V: arr)  { ... } // for(t,0,length(arr),1) { V = arr[t]; ... }<br>
   for(V: someYieldyCofunc) { ... } // (similar to above)<br>
<br>
   outer:<br>
   while(X) {<br>
      while(Y) {<br>
         ...<br>
         break;       // Exit from inner-most loop<br>
         break outer; // Exit from "outer" (the "while(X)")<br>
         continue;       // Continue inner-most loop<br>
         continue outer; // Continue "outer" loop<br>
      }<br>
   }<br>
</code></pre>

<h2>Goto Command</h2>

The "goto" command causes program execution to jump to the correspondingly labeled part of the program. Labels associate identifiers with locations in the program (indicated by an identifier followed by a colon). The goto command can only be used to jump <i>forward</i> within the same function, but never into a nested context (out of is ok):<br>
<pre><code>   func main() {<br>
      start:<br>
      if(...) {<br>
         goto start;  // ILLEGAL (backwards)<br>
         goto end;    // Ok (out of "if")<br>
         goto inner;  // ILLEGAL (into "else")<br>
         goto extern; // ILLEGAL (into another function)<br>
      }<br>
      else {<br>
         inner:<br>
      }<br>
      end:<br>
   }<br>
<br>
   func another() { extern: }<br>
</code></pre>

<h2>Static Initializations</h2>

Non-precomputable static initializations are computed at runtime "before" main(). Other "pre-main" code can be specified within a <code>static { ... }</code> block. All such code is compiled into a self-falsifying conditional block so that program write-back works as intended. Static class-instances are initialized using the initial-values of each class member (these are also copied into constructors as needed).<br>
<br>
<h2>Dynamic Memory Allocation</h2>

The <code>new</code> and <code>delete</code> keywords dynamically allocate & deallocate memory, but must be defined via specific function declarations (where "size" is how many bytes to allocate, and "addr" is the address of the (previous) allocation):<br>
<pre><code>    func new&lt;T&gt;(*T item, byte size): *T { ... } // allocates memory and returns the address.<br>
    func delete&lt;T&gt;(*T item, byte size) { ... } // frees previously allocated memory.<br>
    // NOTE: 'delete' may be omitted for garbage-collecting environments.<br>
</code></pre>
Pointer-values can then be dynamically allocated with initial values in braces, or left unitialized:<br>
<pre><code>   *byte x = new byte{5}; // initialized to 5<br>
   *byte y = new byte;    // uninitialized<br>
</code></pre>

<h2>Interpreted Aspects</h2>

Antelope uses data-flow and liveness analysis to predict which values and computations can be precomputed, simplified, or circumvented altogether. This includes storing <code>final</code> references as static allocations, deciding when to inline function-code, and removing variables and constructs which can be entirely circumvented.<br>
<br>
The $ operator designates entities to be "interpreted" by performing the aforementioned optimizations with a higher priority, and giving a compilation error if such an entity cannot be evaluated at compile-time. The $ operator designates:<br>
<ul><li>Variables for which the value must be known whenever it is used.<br>
</li><li>Functions and function-calls which must be inlined and have no runtime computations.<br>
</li><li>Flow-control constructs which must be interpreted (i.e. conditions and jumps removed).</li></ul>

<h2>Inline Assembly</h2>

The assembly command allows for the inlining of assembly code (<code>asm "assembly code";</code>). The addresses of variables and other entities can be inserted into the assembly code by separating each item with commas (e.g. <code>asm " LD A," someVarNamedThis;</code>). The <code>#assembly</code> directive behaves the same way, except that the associated code comes before anything else in the program (see "File-Level Declarations").<br>
<br>
<br>
<hr />
<h1>Functions & Methods</h1>
<hr />
<br>

<b>Functions</b> contain a block of code that can be called from anywhere else in the program. Local function values are "saved" in the system stack when necessary to avoid collisions with recursive calls. Values are passed to and returned from functions via registers as determined by the compiler. Functions cannot be declared inside of other functions (<i>but see "Cofuncs" for an example of <b>closures</b></i>). Functions are declared with the "func" keyword, followed by the name of the function, followed by its arguments listed in parenthesis. Return values (if any) are indicated with a colon followed by the return type(s):<br>
<pre><code>   func plain() { ............. } // 0 args, 0 returns: plain();<br>
   func both(int x, y) { ...... } // 2 args, 0 returns: both(5,6);<br>
   func give(int i): byte { ... } // 1 arg,  1 return:  byte b = give(300);<br>
   func two():(char,char) { ... } // 0 args, 2 returns: char (x,y) = two();<br>
</code></pre>

<b>Default arguments</b> are special in Antelope, because they are embedded directly within the function as an assignment (rather than being "automatically" inserted into each call). When a default value that is <i>not</i> used, the function is called just <i>after</i> the corresponding default assignment. In addition to saving program space, this allows each "version" of a function to exist in its reduced form:<br>
<pre><code>   // Defualt values must come "last":<br>
   func x(int a, b=5, c=6) { ... }<br>
<br>
   // Internally, x is stored as follows:<br>
   // (This kind of explicit nesting is NOT ACTUALLY LEGAL)<br>
      func x(int a) {<br>
         int b = 5;<br>
         // Execution continues into the next function:<br>
         func x(int a, b) {<br>
            int c = 6;<br>
            // Execution continues into the next function:<br>
            func x(int a, b, c) {<br>
               ...<br>
            }<br>
         }<br>
      }<br>
<br>
   x(1,2,3); // Calls the "innermost function" (the "full version")<br>
   x(1,2); // Like x(1,2,6), but actually calls the "middle function"<br>
   x(1); // Like x(1,5,6), but actually calls the "outermost function"<br>
<br>
   // Each "version" of the function is even a valid<br>
   // target for function pointers of the same type:<br>
<br>
   func(int,int,int) f3 = x; // f3 points to the inner function<br>
   func(int,int) f2 = x;    // f2 points to the middle function<br>
   func(int) f3 = x;       // f3 points to the inner function<br>
</code></pre>
<b>Function pointers</b> are declared like nameless functions, followed by a variable name. They can point to named functions or anonymous functions which are declared on the spot:<br>
<pre><code>   func(int,char):bool w; // w points to a func(int,char):bool<br>
   func():(int,int) x;    // x points to a func():(int,int)<br>
   func(int) y;           // y points to a func(int)<br>
   func() z;              // z points to a func()<br>
</code></pre>

<b>Lambda Expressions</b> are used to declare anonymous functions that function-pointers can point to. Argument types, return types, and even the arguments themselves can be omitted when the context makes them clear. When the function consists of a single return statement, the body can be replaced with "=>" followed by the return-value:<br>
<pre><code>   (int a,b):int { return a+b; } // Full form<br>
   (a,b) =&gt; a+b; // Types removed, body replaced with return value<br>
   func(int a,b):int f = { return a+b; } // All except body removed<br>
   x =&gt; 5*x; // Parentheses optional for single argument form<br>
</code></pre>

<b>Methods</b> are functions declared with a receiving-type (struct, cofunc, or interface) "dotted" to the function name. Methods are stored as functions with an invisible first argument called "this", which is a pointer to the calling entity. The members of the calling entity can be accessed directly (i.e. "this." is implicit):<br>
<pre><code>   struct Foo { int x,y; } // see "structs"<br>
<br>
   func Foo.divide():int { // Foo.divide is a func(*Foo):int<br>
      return x/y; // short for this.x/this.y<br>
   }<br>
<br>
   Foo f(10,5); // see "structs"<br>
   func(*Foo):int fp = Foo.divide;<br>
<br>
   f.divide(); // short for Foo.divide(f)<br>
   fp(f); // this also calls Foo.divide(f)<br>
</code></pre>

<b>Virtual Methods</b> are declared as functions within a struct, which are stored in the struct as function-pointers, but act as methods for that struct. Virtual methods may be declared with or without a default function body:<br>
<pre><code>   struct Foo { // (see "structs")<br>
      int x, y;<br>
      func(*Foo) fp; // plain func-pointer (not a method)<br>
      func vm() { ..code.. }; // same as as fp, but acts as a method<br>
   }<br>
<br>
   Foo foo(1, 2, f =&gt; f.x + f.y);<br>
<br>
   foo.fp(foo); // foo has to pass itself into fp<br>
   foo.mp();    // foo passed implicitly as "this"<br>
</code></pre>

<br>
<hr />
<h1>Structs, Cofuncs, Interfaces</h1>
<hr />
<br>

<h2>Structs</h2>

Structs are aggregate types which may only contain (non-static) data-members:<br>
<pre><code>   struct Foo { byte b; int i; } // "Foo" is a {byte, int} allocation<br>
   Foo f(10, 260); // Declare f as a Foo of {10, 260}<br>
   f.b = 7;        // Fields can be accessed/modified individually<br>
</code></pre>

<b>Default values</b> may be provided to struct members, as long as all members with default values are at the "end" of the struct. These members may be left out of the initialization list, in which case the default values are used:<br>
<pre><code>   struct Foo {<br>
      int a, b;<br>
      int sum = a+b+c+d; // default can be an expression<br>
      int c, d = 40, 50;<br>
   }<br>
<br>
   Foo f1(1,2,3,4,5); // as stated<br>
   Foo f2(1,2,3,4);   // Foo(1,2,3,4,50)<br>
   Foo f3(1,2,3);     // Foo(1,2,3,40,50)<br>
   Foo f4(1,2);       // Foo(1,2,93,40,50) (1+2+40+50=93)<br>
</code></pre>

Structs "inherit" the members their inner members, but only where such transparent does not create ambiguity. To illustrate this intention explicitly, it is legal to use datatype names as variable names:<br>
<pre><code>   struct Foo { int a,b; }<br>
   struct Bar { Foo Foo; int c; }<br>
<br>
   func Bar.sum():int {<br>
      return a+b+c; // short for this.Foo.a + this.Foo.b + this.c<br>
          // (since Bar does not have an a or b, but Bar.Foo does) <br>
   }<br>
<br>
   Bar b(Foo(1,2),3);<br>
   Foo f = b.Foo;    // Access the "Foo" in b like anything else.<br>
</code></pre>

When multiple members contain members of the same name, or when the containing struct already has a member of that name, then neither can be accessed transparently:<br>
<pre><code>   struct A { int x; }<br>
   struct B { int x, y; }<br>
   struct C { A A; B B; int y; }<br>
<br>
   C c(A(1),B(2,3),4);<br>
<br>
   c.x;   // INVALID (is it c.A.x or c.B.x?)<br>
   c.B.x; // 2<br>
   c.A.x; // 1<br>
   c.y;   // 4 (hides c.B.y)<br>
   c.B.y; // 3<br>
</code></pre>

("Bridge" methods will be created automatically so that structs can use methods of their anonymous members as their own, but only when it is <i>absolutely necessary</i> for dynamic behavior.)<br>
<br>
<h2>Cofuncs</h2>

Cofunctions are function-objects which can be called like functions, but store data between calls (like structs). The <code>yield</code> command is used to return values, but continue from the same spot on the next call:<br>
<pre><code>   // Using yield to simulate a generator<br>
   cofunc RotatingSeq(int add): int {<br>
      do { // "do forever" loop<br>
         int last = add; // last will be stored internally<br>
         yield add;<br>
         yield add+last;<br>
         yield add-last; // next call starts back at top<br>
      };<br>
   }<br>
<br>
   RotatingSeq r();<br>
   r(1); // yields 1<br>
   r(2); // yields (2+1) = 3<br>
   r(3); // yields (3-1) = 2<br>
   r(4); // (starts over) = 4<br>
</code></pre>

Cofuncs can also contain data like a struct:<br>
<pre><code>   // Simulating a closure on x<br>
   cofunc Counter { int x; }():int {<br>
      x = x+1;<br>
      return x;<br>
   }<br>
<br>
   Counter c(3);<br>
   c(); c(); c(); // 4,5,6<br>
   c.x = 10;<br>
   c(); // 11<br>
</code></pre>

The compiler accomplishes all of this by making the following modifications:<br>
<ol><li>The underlying function is a method (takes an implicit "this" pointer to a cofunc-instance).<br>
</li><li>Each cofunc-instance is stored as a tail-call (a "goto") to the underlying function (thus allowing the cofunc to be called <i>as if it was the function itself</i>), followed by any information needing to be stored between calls.<br>
</li><li>When the <code>yield</code> command is used, the underlying function directly modifies the tail-call in the cofunc to jump to where the function left off (rather than to the start of the function).</li></ol>

<h2>Interfaces</h2>

Interfaces define datatypes representing any entity which has a certain set of methods and data members. For example, a "Printer" could be defined as anything with a "print" method, and a "Communicator" as anything with a "read" and a "print" method (these can be method-pointers or normal methods):<br>
<pre><code>   struct X { byte b; }<br>
   struct Y { char c; }<br>
<br>
   func X.read(): []char { ... }<br>
   func X.print() { ...code to print b... }<br>
   func Y.print() { ...code to print "Hello!"... }<br>
<br>
   iface Communicator {<br>
      func read(): []char;<br>
      func print();<br>
   }<br>
<br>
   iface Printer { func print(); }<br>
<br>
   func usePrinter(Printer p) {<br>
      p.print();<br>
   }<br>
<br>
   func useCom(Communicator c): []char {<br>
      c.print();<br>
      return c.read();<br>
   }<br>
<br>
   X x(5);<br>
   T y('Q');<br>
   usePrinter(x); // prints 5<br>
   usePrinter(y); // prints "Hello!"<br>
   msg := useCom(x); // prints 5, returns x.read()<br>
<br>
   // Interface instances can even be created directly:<br>
<br>
   Printer p = x;<br>
   p.print();<br>
   p = y;<br>
   p.print();<br>
</code></pre>

Interfaces variables contain a pointer to the struct representing the interface (the "this"), and a pointer to an array of method-pointers and data members (or just a pointer to a method, if the interface only use one method). For added flexibility, Antelope allows interfaces to be overloaded with alternate values and methods:<br>
<pre><code>   struct Imitator { int x, y; }<br>
<br>
   func Imitator.blah() { ... }<br>
   func Imitator.flah(): []char { ... }<br>
<br>
   Imitator i(1,2);<br>
   Communicator c(i, i.flah, i.blah);<br>
   c.read();  // calls i.flah<br>
   c.print(); // calls i.blah<br>
</code></pre>

<br>
<hr />
<h1>The Antelope Environment</h1>
<hr />
<br>

<h2>Namespaces</h2>

Namespaces are used to "package" source code under different names, both to prevent "name collisions" and to encapsulate (hide) code that should not be accessed externally. Each source program may have one namespace declared at the top of the file, and multiple files may use the same namespace. Anything from another namespace is accessed with the namespace name "dotted" to the front (unless the file is "using" that namespace, which will then do this automatically). Only things which start with a Capital Letter can be accessed from other namespaces. Namespaces may be "nested":<br>
<pre><code>   // This file is part of the "Animal" namespace:<br>
   namespace Animal;<br>
<br>
   int Dinosaur;<br>
   int monkey; // monkey is not accessible outside of<br>
               // the "Animal" namespace (lowercase).<br>
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~<br>
   // This file is part of the "Bird" namespace, which<br>
   // is nested within "Animal" (hence "Animal.Bird"):<br>
   namespace Animal.Bird;<br>
<br>
   int Cardinal;<br>
   int dodo = monkey; // Everything in "Bird" is also<br>
                      // "Animal", so this is okay.<br>
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~<br>
   // This file is in the "Fruit" namespace:<br>
   namespace Fruit;<br>
<br>
   int Apple;<br>
   int banana;<br>
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~<br>
   // This file is not part of ANY namespace.<br>
   // However, it "uses" the "Fruit" namespace directly:<br>
   using Fruit;<br>
<br>
   void main() {<br>
      int x = Animal.Dinosaur; // Dinosaur is in Animal.<br>
      x = Animal.Bird.Cardinal; // etc.<br>
      x = Animal.monkey; // ILLEGAL: monkey is "hidden".<br>
      x = Apple; // The "using Fruit" at the top makes<br>
                 // this the same as "Fruit.Apple".<br>
      x = banana; // ILLEGAL: banana is nevertheless<br>
                  // "hidden" within its namespace.<br>
   }<br>
</code></pre>

<h2>Preprocessor Directives</h2>

Preprocessor Directives are "prerequisite" instructions to the compiler, such as including code from other files or conditionally ignoring a section of code. They all begin with <code>#</code> to distinguish them from other code. All arguments to a directive must be on the same line, but are otherwise separated by whitespace:<br>
<br>
<pre><code>   #include "filename.ant"  // Include code from another file.<br>
<br>
   #define TI_83_PLUS // Define some identifier (as a flag only).<br>
<br>
   #if FOO // The following code is only to be included if "FOO"<br>
           // was defined with #define (probably in another file).<br>
      ...<br>
   #endif  // Marks the end of the code for the #if directive.<br>
<br>
   #assembly "code" // Insert "code" as assembly code.<br>
<br>
   #allocate "place1" 99 // Allocate space at "place1" (up to 99 bytes)<br>
<br>
   #allocate "place2" -99 // Allocate at "place2" (grow "down" to 99 bytes)<br>
<br>
   #error "Some Message" // Signal an error with the given message.<br>
</code></pre>
The <code>#allocate</code> directive specifies an assembly-coded address <i>outside</i> of the program which the compiler <i>may</i> use to store large uninitialized values (such as arrays). Allocation will start at the given address, and grow "upward" to the given byte-limit (or "downward" if the limit is negative).<br>
<br>
The <code>#assembly</code> directive specifies assembly code which is to be inserted at the "very top" of the program (before the "main" function and before any static initializations). Any number of arguments can be given (string literals are translated directly into assembly code; anything else must correspond to a variable, which is then translated into the assembly address of that variable). Example: <code>#assembly " LD HL,(" someVariable ")"</code>

Multiple arguments can be provided to the <code>#if</code> using the "and" (<code>&amp;</code>), "or" (<code>|</code>), "xor" (<code>^</code>), and "not" (<code>!</code>) operators, which resolve in the same order as they do in other code. These conditionals may also be "nested":<br>
<pre><code>   #if !FOO // If "FOO" was NOT defined...<br>
      #include "file" // (Only include "file" if "FOO" was not defined)<br>
      #if (A &amp; B) ^ (C | D) // If Either (A and B) or (C or D), but not both...<br>
         ...<br>
      #endif // end of the "inner" #if<br>
   #endif // end of the "outer" #if<br>
</code></pre>

<h2>Compiler Interface</h2>

Every major aspect of the compiler (tokenization, preprocessing, parse-trees, etc.) will be modularized, serializable, and invokable as an API packaged into a JAR file. The Jar file will be executable: As a program, the compiler will take command-line arguments specifying files to compile, directories to use, namespace and environment related options, and possibly requests to only run certain portions of compilation. This design will allow the compiler to function as a standalone compiler, or to be interfaced with a higher-level editing environment; or to be used as a Java API to design a more complex system that has direct control over the compilation process (for example, what BlueJ does with Java).<br>
<br>
<br>
<hr />
<h1>Final Modifications</h1>
<h3>(Changes which still need to be made to the above material)</h3>
<hr />
<br>

Need to add examples of templated ("generic") functions and types.<br>
<br>
Anonymous functions which refer to (non- static/const) external local variables will have restricted use so as not to "escape" the context of its parent function.<br>
<br>
All code is precomputed as much as possible (without unrolling loops or recursive calls). The $ operator Requires something to be interpreted, including loops and recursive calls.<br>
<br>
Values will be passed/returned in registers such that any two functions with the same pattern of arguments will use the same registers for them. Function pointers may point to functions with "extra" return values (e.g. func(byte) pointing to func(byte):byte).