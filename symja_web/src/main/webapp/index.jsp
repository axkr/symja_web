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

<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1"> 
<!-- make page fit nicer in iOS -->

<link rel="search" href="http://matheclipse.org/opensearch.xml" type="application/opensearchdescription+xml" title="Symja" />

<link rel="stylesheet" type="text/css" href="/media/css/styles.css" />
<link rel="stylesheet" type="text/css" href="/media/css/documentation.css" />
<link rel="stylesheet" type="text/css" href="/media/css/message.css" />
<link rel="stylesheet" type="text/css" media="print" href="/media/css/styles_print.css" />

<link rel="stylesheet" type="text/css" href="/media/css/font-awesome.min.css" />

<!-- Favicons 
<link rel="apple-touch-icon" sizes="180x180" href="/media/img/favicons/apple-touch-icon.png" />
<link rel="icon" type="image/png" href="/media/img/favicons/favicon-32x32.png" sizes="32x32" />
<link rel="icon" type="image/png" href="/media/img/favicons/favicon-16x16.png" sizes="16x16" />
<link rel="manifest" href="/media/img/favicons/manifest.json" />
<link rel="mask-icon" href="/media/img/favicons/safari-pinned-tab.svg" color="#333333" />
<link rel="shortcut icon" href="/media/img/favicons/favicon.ico" />
<meta name="msapplication-config" content="/media/img/favicons/browserconfig.xml" />
-->
<link rel="shortcut icon" href="/favicon.ico" /> 
<meta name="theme-color" content="#ffffff" />

<!--[if lte IE 9]>
<link rel="stylesheet" type="text/css" href="/media/css/styles_ie.css" />
<![endif]-->

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
 
<header>
<div id="headerleft"> 
 <img id="logo" class="load" src="/media/img/logo.png" height="32" alt="Logo" />
  <!-- 
  <img id="logotext" class="load" src="/media/img/logo-text.svg" height="26" alt="Symja" />
  <div class="menu">
    <a href="javascript:showOpen()" title="Open"><i class="fa fa-file-text"></i></a>
    <a href="javascript:showSave()" title="Save"><i class="fa fa-download"></i></a>
    <a href="javascript:createLink()" title="Generate hash"><i class="fa fa-share-alt"></i></a>
   </div>
    -->
</div>

<div id="headerright">
  <input id="search" type="text" /><a id="doclink" href="javascript:toggleDoc()" title="Docs"><i class="fa fa-lg fa-question-circle-o"></i></a>

 <!-- 
  <div id="notAuthenticated" class="login">
    <a href="javascript:showLogin()" class="fa fa-lg fa-sign-in" title="Sign in"></a>
  </div>
  
  <div id="authenticated" class="login" style="display: none">
    <span id="username"></span><a id="logout" href="javascript:logout()" class="fa fa-lg fa-sign-out" title="Sign out"></a>
  </div>
  -->
   
<%
	UserService userService = UserServiceFactory.getUserService();
	if (userService.getCurrentUser() != null) {
		User user = userService.getCurrentUser();
		if (user != null) {
%>
		  <div id="authenticated" class="logout" >
			<span id="username"><%=request.getUserPrincipal().getName()%></span><br />
			<a href="<%=userService.createLogoutURL(request.getRequestURI())%>">Logout</a><br />
		  </div>
<%
        }
	} else {
%>
		  <div id="notAuthenticated" class="login" >
			<a href="<%=userService.createLoginURL(request.getRequestURI())%>" title="Login to persist your session in the datastore">Google-Login</a><br />
		  </div>
<%
	}
%> 
	
</div>
</header> 

<!-- Login -->
<script type="text/javascript">
  var REQUIRE_LOGIN = true;
</script>


<div id="document">

<!-- Insert ul into this div, as empty ul is not allowed! -->
<div id="queriesContainer"></div>

<div id="welcomeContainer">  
<div id="welcome">
<h1>Welcome to Symja computer algebra system!</h1>
<p>Enter queries and evaluate them by pressing <code>Shift</code> + <code>Return</code>.</p>
<p>See the <a href="javascript:showGallery()">gallery</a> for some examples or the <a href="javascript:showDoc()">documentation</a> for a full list of supported functions.</p>
<p>Symja uses <a href="http://www.mathjax.org/" target="_blank">MathJax</a> to display beautiful math.
Please contact us on <a href="https://gitter.im/symja_android_library/Lobby">Gitter Chat</a>.</p>
<div style="position:absolute;right:0;bottom:-2.8em;">
  <p style="text-align:right;color:#3f3f3f;"><label><input type="checkbox" id="hideStartupMsg" />Do not show on startup.</label></p>
</div>
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
	<tr><th><label for="id_email">Email:</label></th><td><input id="id_email" maxlength="80" name="email" type="email" /></td></tr>
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
	<tr><th><label for="id_name">Name:</label></th><td><input id="id_name" maxlength="30" name="name" type="text" /><br /><span class="helptext"><p class="helptext">Worksheet names are not case-sensitive.</p></span></td></tr>
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
 
<div style="white-space: nowrap;" id="calc_all">
<span class="calc_container" style="font-size: 0.90em; font-family: default;"></span><span class="calc_next"> </span><br />
<span class="calc_below"> </span>
</div>

<%
	Object value = request.getAttribute("input");
	if (value!= null && (value instanceof String)) {
	  String str=((String)value).trim();
	  if (str.length()>0){
%>	
<script type="text/javascript">
window.onload = function iload() { setQueries(['<%=str%>']); }
</script> 
<%    
	  }
    }
%>

</body>
</html>
