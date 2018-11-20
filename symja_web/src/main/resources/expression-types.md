[<< Back](javascript:loadDoc('/index'))

## Expression types

<table>
<thead>
<tr>
<th align="center">Type</th>
<th align="center">Description</th>
<th align="center">Input Example</th>
</tr>
</thead>
<tbody>
<tr>
<td align="center">Integer</td>
<td align="center">integer numbers</td>
<td align="center"><code>42</code></td>
</tr>
<tr>
<td align="center">Rational</td>
<td align="center">rational numbers</td>
<td align="center"><code>13/17</code></td>
</tr>
<tr>
<td align="center">Complex</td>
<td align="center">complex numbers</td>
<td align="center"><code>2+I*1/3</code></td>
</tr>
<tr>
<td align="center">Real</td>
<td align="center">double values</td>
<td align="center"><code>0.5</code></td>
</tr>
<tr>
<td align="center">Complex Real</td>
<td align="center">complex double values</td>
<td align="center"><code>0.5-I*0.25</code></td>
</tr>
<tr>
<td align="center">Evaluation Precedence</td>
<td align="center">control precedence with <code>(...)</code></td>
<td align="center"><code>(a+b)*c</code></td>
</tr>
<tr>
<td align="center">Lists</td>
<td align="center">comma separated list of elements which are surrounded by <code>{ ... }</code></td>
<td align="center"><code>{a, b, c, d}</code></td>
</tr>
<tr>
<td align="center">Vectors</td>
<td align="center">vectors are like list, but cannot contain sub-lists <code>{ ... }</code></td>
<td align="center"><code>{1, 2, 3, 4}</code></td>
</tr>
<tr>
<td align="center">Matrices</td>
<td align="center">a matrix contains the rows as sub-lists</td>
<td align="center"><code>{{1, 2}, {3, 4}}</code></td>
</tr>
<tr>
<td align="center">Predefined Functions</td>
<td align="center">predefined function names start with an upper case character and the arguments are enclosed by <code>( ... )</code></td>
<td align="center"><code>Sin(0), PrimeQ(13)</code></td>
</tr>
<tr>
<td align="center">Predefined Constants</td>
<td align="center">predefined constant names start with an upper case character</td>
<td align="center"><code>Degree, E, Pi, False, True, ...</code></td>
</tr>
<tr>
<td align="center">User-defined variables</td>
<td align="center">in the Symja web app user-defined variables can be defined without a preceding <code>$</code> character</td>
<td align="center"><code>a=42</code></td>
</tr>
<tr>
<td align="center">User-defined rules</td>
<td align="center">in the Symja web app user-defined rules can be defined without a preceding <code>$</code> character</td>
<td align="center"><code>f(x_,y_):={x,y}</code></td>
</tr>
<tr>
<td align="center">Pattern Symbols</td>
<td align="center">patterns end with an appended <code>_</code> character and could have a constraint</td>
<td align="center"><code>f(x_Integer):={x}</code></td>
</tr>
<tr>
<td align="center">Strings</td>
<td align="center">character strings are enclosed by double quote characters</td>
<td align="center"><code>"Hello World"</code></td>
</tr>
<tr>
<td align="center">Slots</td>
<td align="center">a <code>#</code> character followed by an optional integer number</td>
<td align="center"><code>#</code> or <code>#2</code></td>
</tr>
<tr>
<td align="center">Pure Functions</td>
<td align="center">pure functions can be expressed with the <code>&amp;</code> operator</td>
<td align="center"><code>(#^3)&amp;[x]</code>  gives <code>x^3</code></td>
</tr>
<tr>
<td align="center">Parts of an expression</td>
<td align="center"><code>expr[[index]]</code></td>
<td align="center"><code>{a, b, c, d}[[2]]</code>  gives <code>b</code></td>
</tr></tbody></table>

[<< Back](javascript:loadDoc('/index'))