 
- [Basic calculations](#basic-calculations) 
- [Tutorial](#tutorial) 

## Basic calculations

Symja can be used to calculate basic stuff:

```
1 + 2
```

To submit a command to Symja, press Shift+Return in the Web interface or Return in the console interface. The result will be printed in a new line below your query.

Symja understands all basic arithmetic operators and applies the usual operator precedence. Use parentheses when needed:
```
1 - 2 * (3 + 5) / 4
```

The multiplication can be omitted:
```
1 - 2 (3 + 5) / 4
```

But function `f(x)` notation isn't interpreted as `f*(x)`
```
f(x)
```

Powers can be entered using ^:
```
3 ^ 4
```

Integer divisions yield rational numbers:
```
6 / 4
```

To convert the result to a floating point number, apply the function `N`:
```
N(6 / 4)
```

As you can see, functions are applied using the identifier `N` and parentheses `(` and `)`. 
In general an identifier is a user-defined or built-in name for a variable, function or constant. 
Only the identifiers which consists of only one character are case sensitive. 
For all other identifiers the input parser doesn't distinguish between lower and upper case characters.

For example: the upper-case identifiers [D](functions/D.md), [E](functions/E.md), [I](functions/I.md), [N](functions/N.md), 
are different from the identifiers `d, e, i, n`, whereas the 
functions like [Factorial](functions/Factorial.md), [Integrate](functions/Integrate.md) can be entered as 
`factorial(100)` or `integrate(sin(x),x)`. If you type `SIN(x)` or `sin(x)`, 
Symja assumes you always mean the same built-in [Sin](functions/Sin.md) function.  

Symja provides many common mathematical functions and constants, e.g.:
```
Log(E)

Sin(Pi)

Cos(0.5)
```
When entering floating point numbers in your query, Symja will perform a numerical evaluation and present a numerical result, pretty much like if you had applied N.

Of course, Symja has complex numbers:
```
Sqrt(-4)

I ^ 2

(3 + 2*I) ^ 4

(3 + 2*I) ^ (2.5 - I)

Tan(I + 0.5)
```

Abs calculates absolute values:
```
Abs(-3)

Abs(3 + 4*I)
```

Symja can operate with pretty huge numbers:
```
100!
```

(! denotes the factorial function.) The precision of numerical evaluation can be set:

```
N(Pi, 100)
```

Division by zero is forbidden:
```
1 / 0
```

Other expressions involving Infinity are evaluated:
```
Infinity + 2 Infinity
```

In contrast to combinatorial belief, 0^0 is undefined:
```
0 ^ 0
```

The result of the previous query to Symja can be accessed by %:
```
3 + 4

% ^ 2
```

In the console available functions can be determined with the `?` operator

```
?ArcC*
ArcCos, ArcCosh, ArcCot, ArcCoth, ArcCsc, ArcCsch
```

Documentation can be displayed by asking for information for the function name.

```
?Integrate
```

## Tutorial

The following sections are introductions to the basic principles of the language of Symja. 
A few examples and functions are presented. Only their most common usages are listed; 
for a full description of their possible arguments, options, etc., see their entry in the "function reference" of built-in symbols.

* [Symbols and assignments](javascript:loadDoc('symbols-and-assignments'))
* [Comparisons and Boolean logic](javascript:loadDoc('comparisons-and-boolean-logic'))
* [Strings](javascript:loadDoc('strings'))
* [Lists](javascript:loadDoc('lists'))
* [The structure of things](javascript:loadDoc('the-structure-of-things'))
* [Functions and patterns](javascript:loadDoc('functions-and-patterns'))
* [Control statements](javascript:loadDoc('control-statements'))
* [Scoping](javascript:loadDoc('scoping'))
* [Expression types](javascript:loadDoc('expression-types'))
