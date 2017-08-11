<%@ page contentType="text/html;charset=UTF-8" isELIgnored="true"
	language="java"%>
<%@ page import="com.google.appengine.api.users.User"%>
<%@ page import="com.google.appengine.api.users.UserService"%>
<%@ page import="com.google.appengine.api.users.UserServiceFactory"%>
<?xml version="1.0" encoding="UTF-8"?>

<!DOCTYPE html PUBLIC
  "-//W3C//DTD XHTML 1.1 plus MathML 2.0 plus SVG 1.1//EN"
  "http://www.w3.org/2002/04/xhtml-math-svg/xhtml-math-svg.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">

<head>

<meta http-equiv="content-type" content="text/html; charset=UTF-8" />
<title>Symja</title>

<meta name="viewport" content="width=480" />
<!-- make page fit nicer in iOS -->

<link rel="stylesheet" type="text/css" href="/media/css/styles.css" />
<link rel="stylesheet" type="text/css" href="/media/css/documentation.css" />
<link rel="stylesheet" type="text/css" href="/media/css/message.css" />
<link rel="stylesheet" type="text/css" media="print" href="/media/css/styles_print.css" />
<!--[if lte IE 9]>
<link rel="stylesheet" type="text/css" href="/media/css/styles_ie.css" />
<![endif]-->

<!--<link rel="shortcut icon" href="/favicon.ico" />-->

<script type="text/javascript" src="/media/js/prototype/prototype.js"></script>
<script type="text/javascript" src="/media/js/three/Three.js"></script>
<script type="text/javascript" src="/media/js/three/Detector.js"></script>

<script type="text/javascript" src="/media/js/mathjax/MathJax.js?config=MML_HTMLorMML&amp;delayStartupUntil=configured"></script>

<script type="text/javascript" src="/media/js/message.js"></script>
<script type="text/javascript" src="/media/js/authentication.js"></script>
<script type="text/javascript" src="/media/js/inout.js"></script>
<script type="text/javascript" src="/media/js/utils.js"></script>
<script type="text/javascript" src="/media/js/symja.js"></script>
<script type="text/javascript" src="/media/js/graphics3d.js"></script>
<script type="text/javascript" src="/media/js/doc.js"></script>

 
<!-- including scriptaculous main does not work in Safari --> 
<script type="text/javascript" src="/media/js/scriptaculous/builder.js"></script>
<script type="text/javascript" src="/media/js/scriptaculous/effects.js"></script>
<script type="text/javascript" src="/media/js/scriptaculous/dragdrop.js"></script>
<script type="text/javascript" src="/media/js/scriptaculous/controls.js"></script>
<script type="text/javascript" src="/media/js/scriptaculous/slider.js"></script>
<script type="text/javascript" src="/media/js/scriptaculous/sound.js"></script>
<!--<script type="text/javascript" src="/media/js/scriptaculous/scriptaculous.js"></script>-->


</head>

<body>
  


<div id="head">
	<div class="headright">
		<a href="javascript:createLink()">#</a>
	</div>
	<div class="headright">
<%
	UserService userService = UserServiceFactory.getUserService();
	if (userService.getCurrentUser() != null) {
		User user = userService.getCurrentUser();
		if (user != null) {
%>
		  <div id="authenticated">
			<span id="username"><%=request.getUserPrincipal().getName()%></span><br />
			<a href="<%=userService.createLogoutURL(request.getRequestURI())%>">Logout</a><br />
		  </div>
<%
        }
	} else {
%>
		  <div id="notAuthenticated">
			<a href="<%=userService.createLoginURL(request.getRequestURI())%>" title="Login to persist your '$'-user variables in the datastore">Login</a><br />
		  </div>
<%
	}
%>
	</div>
	
	<span id="logo"><a href="http://symjaweb.appspot.com" target="_blank" style="font-family:'Times New Roman',Times,serif; font-size:150%">Symja</a></span>
</div>

<div id="menu">
<div id="menuleft">
    <span><a href="javascript:showSave()">Save</a></span><span><a href="javascript:showOpen()">Load</a></span>
    <span style="color:red">This is a prototype interface NOT all menus are working</span>
</div>
<div id="menuright">
	<a id="doclink" href="javascript:toggleDoc()">Documentation</a>
	<input id="search" type="text" />
</div>
</div>

<script type="text/javascript">
    var REQUIRE_LOGIN = true;
</script>





<div id="document">

<!-- Insert ul into this div, as empty ul is not allowed! -->
<div id="queriesContainer"></div>

<div id="welcomeContainer">
<div id="welcome">
<p><strong>Welcome to Symja!</strong></p>
<p>Symja is a general-purpose computer algebra system.</p>
<p>Enter queries and submit them by pressing <code>Shift</code> + <code>Return</code>.
See the <a href="javascript:showDoc()">documentation</a> for a full list of supported functions.
<!--<p>Currently, only <a href="http://www.getfirefox.com" target="_blank">Firefox</a> is supported. Install the <a href="http://www.mozilla.org/projects/mathml/fonts/" target="_blank">STIX fonts</a> for optimal display of mathematical content.</p>-->
<!--<p><small>This box will disappear as soon as you submit your first query.</small></p>-->
Symja uses <a href="http://www.mathjax.org/" target="_blank">MathJax</a> to display beautiful math.</p>
<p>Visit <a href="http://bitbucket.org/axelclk/symja_android_library/wiki/Home" target="_blank">Symja project page</a> for further information about the project.</p>
</div>

<div id="welcomeBrowser" style="display: none">
<p>It seems that you are using an unsupported Web browser. Please consider running Symja with <a href="http://www.getfirefox.com" target="_blank">Firefox</a>, <a href="http://www.google.com/chrome" target="_blank">Chrome</a>, or <a href="http://www.apple.com/safari" target="_blank">Safari</a>.</p>
</div>
</div>

</div>

<div id="code" style="display: none">
<textarea id="codetext" rows="20" cols="80"></textarea>
</div>

<div id="doc" style="display: none">

</div>



<div id="login" class="dialog" style="display: none">
	<h1>Login</h1>
	
	<p id="loginReason" style="display: none">
	</p>

	<p id="passwordSent" class="formsuccess">
		A new password has been sent to your e-mail address <code id="passwordEmail">&nbsp;</code>.
	</p>

	<form id="loginForm" action="javascript:;">
	<table>
	<tr><th><label for="id_email">Email:</label></th><td><input id="id_email" maxlength="80" name="email" type="text" /></td></tr>
<tr><th><label for="id_password">Password:</label></th><td><input id="id_password" maxlength="40" name="password" type="password" /><br /><span class="helptext"><p class="helptext">Leave this field empty if you don't have an account yet,
or if you have forgotten your pass&shy;word.
A new password will be sent to your e-mail address.</p></span></td></tr>
	<tr>
		<td class="submit" colspan="2">
			<input type="button" class="submit" onclick="login()" value="Login" />
			<input type="button" class="cancel" onclick="cancelLogin()" value="Cancel" />
		</td>
	</tr>
	</table>
	</form>
</div>

<div id="save" class="dialog" style="display: none">
	<h1>Save worksheet</h1>
	
	<form id="saveForm" action="javascript:;">
	<table>
	<tr><th><label for="id_name">Name:</label></th><td><input id="id_name" maxlength="30" name="name" type="text" /><br /><span class="helptext"><p class="helptext">Worksheet names are notcase-sensitive.</p></span></td></tr>
	<tr>
		<td class="submit" colspan="2">
			<input type="button" class="submit" onclick="save()" value="Save" />
			<input type="button" class="cancel" onclick="cancelSave()" value="Cancel" />
		</td>
	</tr>
	</table>
	</form>
</div>

<div id="open" class="dialog" style="display: none">
	<h1>Open worksheet</h1>
	
	<div class="filelist">
	<table>
	<thead>
		<tr>
			<th>Name</th>
		</tr>
	</thead>
	<tbody id="openFilelist">
		<tr><td></td></tr> <!-- must not be empty -->
	</tbody>
	</table>
	</div>
	
	<input type="button" class="cancel" onclick="cancelOpen()" value="Cancel" />
</div>


<div id="dialog" class="dialog" style="display: none">
	<h1></h1>
	<p></p>
	<div class="buttons">
		<input type="button" class="submit" onclick="dialogYes()" />
		<input type="button" class="cancel" onclick="dialogNo()" />
	</div>
</div>



<!--
<div style="display: none">
<math id="prototype_math" xmlns="http://www.w3.org/1998/Math/MathML">
<mstyle id="prototype_mstyle"></mstyle>
<mrow id="prototype_mrow"></mrow>
<msup id="prototype_msup"><mi>x</mi><mi>y</mi></msup>
<msub id="prototype_msub"><mi>x</mi><mi>y</mi></msub>
<msubsup id="prototype_msubsup"><mi>x</mi><mi>y</mi><mi>z</mi></msubsup>
<mfrac id="prototype_mfrac"><mi>x</mi><mi>y</mi></mfrac>
<msqrt id="prototype_msqrt"><mi>y</mi></msqrt>
<mo id="prototype_mo">x</mo>
<mn id="prototype_mn">1</mn>
<ms id="prototype_ms">x</ms>
<mi id="prototype_mi">x</mi>
<mtext id="prototype_mtext">t</mtext>
<mtable id="prototype_mtable">
<mtr id="prototype_mtr">
<mtd id="prototype_mtd"></mtd>
</mtr>
</mtable>
</math>

<svg id="prototype_svg" xmlns:svg="http://www.w3.org/2000/svg" xmlns="http://www.w3.org/2000/svg">
<g id="prototype_g" />
<rect id="prototype_rect" />
<circle id="prototype_circle" />
<polyline id="prototype_polyline" />
<polygon id="prototype_polygon" />
<path id="prototype_path" />
<ellipse id="prototype_ellipse" />
<foreignObject id="prototype_foreignObject" />
</svg>

<div id="prototype_div_html" xmlns="http://www.w3.org/1999/xhtml"></div>
<canvas id="prototype_canvas" width="200" height="200" style="border: 1px solid lightgray"></canvas>
</div>
-->

<!--<div style="white-space: nowrap;" id="calc_all">
<span class="calc_container" style="font-size: 0.90em; font-family: default;" id="calc_container"></span><span class="calc_next" id="calc_next"> </span><br />
<span class="calc_below" id="calc_below"> </span>
</div>-->
<div style="white-space: nowrap;" id="calc_all">
<span class="calc_container" style="font-size: 0.90em; font-family: default;"></span><span class="calc_next"> </span><br />
<span class="calc_below"> </span>
</div>

</body>
</html>
