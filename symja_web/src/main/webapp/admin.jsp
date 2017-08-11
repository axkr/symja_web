<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ page import="com.google.appengine.api.users.User"%>
<%@ page import="com.google.appengine.api.users.UserService"%>
<%@ page import="com.google.appengine.api.users.UserServiceFactory"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<title>PDA, iPhone, mobile phone computer algebra form</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta name="DESCRIPTION"
	content="MathEclipse computer algebra tool for PDA, iPhone, mobile phone">
<meta name="KEYWORDS"
	content="Mathematics, pda, iPhone, mobile phone, ajax, front end, java, computer algebra, symbolic math">
<%
	String userAgent = request.getHeader("User-Agent");
	if (userAgent.contains("iPhone")) {
		out
				.println("<meta name = \"viewport\" content = \"width = 320; initial-scale=1.0; maximum-scale=1.0; user-scalable=0;\">");
		out
				.println("<link rel=\"stylesheet\" type=\"text/css\"  href=\"iphone.css\" >");
	} else if (userAgent.contains("Safari")) {
		out
				.println("<meta name = \"viewport\" content = \"width = 320; initial-scale=1.0; maximum-scale=1.0; user-scalable=0;\">");
		out
				.println("<link rel=\"stylesheet\" type=\"text/css\"  href=\"iphone.css\" >");
	} else {
		out
				.println("<meta name = \"viewport\" content = \"width = 640; initial-scale=1.0; maximum-scale=1.0; user-scalable=0;\">");
		out
				.println("<link rel=\"stylesheet\" type=\"text/css\"  href=\"screen.css\" >");
	}
%> <%
 	if (!userAgent.contains("BlackBerry")) {
 %> <script type="text/javascript"
	src="http://ajax.googleapis.com/ajax/libs/jquery/1.4.2/jquery.min.js"></script>
<%
	}
%> <script type="text/javascript" language="javascript">
var handlerFunc = function(resultstr) { 
  var index = resultstr.indexOf(';');
  if (index>=0) { 
    index = resultstr.indexOf(';',index+1);
	if (index>=0) {
	  resultstr = resultstr.substring(index+1);
	  document.getElementById('result').innerHTML = resultstr.replace(/&/g,"&amp;").replace(/</g,"&lt;").replace(/>/g,"&gt;");;  
	  return;
	}
  }
  document.getElementById('result').innerHTML = 'Got response in wrong format.';        
}
var errFunc = function(t) {
  document.getElementById('result').innerHTML = 'Error ' + t.status + ' -- ' + t.statusText;   
}
function stateChanged() {
  if (request.readyState==4) {
    handlerFunc(request.responseText);
  }
}
function getResult(nameArray, btnStr) {
  var temp;
  for (var i = 0; i < nameArray.length; ++i) {
	temp = document.getElementById(nameArray[i]).value;
	btnStr = btnStr.replace(new RegExp(nameArray[i],"g"), temp);
  } 
  document.getElementById('result').innerHTML = 'Loading...';
  var poststr = "evaluate=" + encodeURIComponent( btnStr );	  
<%if (userAgent.contains("BlackBerry")) {%>
    poststr = "/admin?" + poststr;
    request = new XMLHttpRequest();
    request.onreadystatechange = stateChanged;
    request.open("POST", poststr, true);
    request.send(null);
<%} else {%>
  $.ajax({ type: "POST", url: 'admin', data: poststr, 
    success: function(responseText){handlerFunc(responseText) },
    error: function(responseText){errFunc(responseText) }
  }); 
<%}%>
}

function clearta(nameArray) {
  for (var i = 0; i < nameArray.length; ++i) {
    document.getElementById(nameArray[i]).value = '';
  } 
  if (nameArray.length>0) {
	document.getElementById(nameArray[0]).focus();
  }
}
</script>
</head>
<body>

<%
	UserService userService = UserServiceFactory.getUserService();
	User user = userService.getCurrentUser();
	if (user == null || !userService.isUserAdmin()) {
%>
<a href="<%=userService.createLoginURL(request.getRequestURI())%>">Admin Login</a>
<%
	} else {
%>
<a href="<%=userService.createLogoutURL(request.getRequestURI())%>">Sign Out</a>
<form action="">
<%
	// input field 
		String ci = request.getParameter("ci");
		if (ci == null) {
			ci = "ta:Input package source code:t";
		}
		// action buttons 
		String ca = request.getParameter("ca");
		if (ca == null) {
			ca = "Save Package:ta";
		}
		StringBuilder buf = new StringBuilder(1024);

		String[] inps = ci.split("\\|");
		String[] btns = ca.split("\\|");
		String[] comps;
		StringBuilder arrayString = new StringBuilder(128);
		//buf.append("\n<table border=\"1\">");
		buf.append("\n<table>");
		for (int i = 0; i < inps.length; i++) {

			comps = inps[i].split("\\:");
			if (comps.length >= 2) {
				arrayString.append("'");
				arrayString.append(comps[0]);
				if (i < inps.length - 1) {
					arrayString.append("',");
				} else {
					arrayString.append("'");
				}
				if (comps.length == 2 || comps[2].equalsIgnoreCase("i")) {
					buf.append("\n<tr><td width=\"25%\"><label for=\"");
					buf.append(comps[0]);
					buf.append("\">");
					buf.append(comps[1]);
					buf.append(":</label></td><td width=\"75%\">");
					buf.append("<input id=\"");
					buf.append(comps[0]);
					buf.append("\" size=\"25\" ");
					if (comps.length > 3) {
						buf.append("value=\"");
						buf.append(comps[3]);
						buf.append("\"");
					}
					buf.append(" />");
					buf.append("</td></tr>");
					// buf.append("");
					continue;
				}
				if (comps.length > 2) {
					if (comps[2].equalsIgnoreCase("t")) {
						// <textarea id="xxx" rows="3" cols="40" >value</textarea>
						buf.append("\n<tr><td colspan=\"2\">");
						if (comps[1].length() > 0) {
							buf.append("<label for=\"");
							buf.append(comps[0]);
							buf.append("\">");
							buf.append(comps[1]);
							buf.append(":</label><br />");
						}

						buf.append("<textarea id=\"");
						buf.append(comps[0]);
						buf.append("\" rows=\"3\" cols=\"40\" >");
						if (comps.length > 3) {
							buf.append(comps[3]);
						}
						buf.append("</textarea>");
						buf.append("</td></tr>");
					}
				}
			}
		}
		buf.append("\n<tr><td colspan=\"2\">");
		for (int i = 0; i < btns.length; i++) {
			comps = btns[i].split("\\:");
			if (comps.length >= 2) {
				buf
						.append("\n<button type=\"submit\" onclick=\"getResult(new Array(");
				buf.append(arrayString);
				buf.append("),\'");
				buf.append(comps[1]);
				buf.append("\'); return false;\" >");
				buf.append(comps[0]);
				buf.append("</button>");
			}
		}
		buf
				.append("\n<button type=\"submit\" onclick=\"clearta(new Array(");
		buf.append(arrayString);
		buf.append(")); return false;\" >Clear</button> ");

		buf
				.append("<input type=\"hidden\" id=\"token\" value=\"47110815\">");
		//buf.append("\n</td></tr>\n</table>"); 

		out.println(buf.toString());
%>
</td>
</tr>
<tr>
	<td colspan="2"><pre id="result">Result output area:</pre></td>
</tr>

</table>
</form>
<%
	}
%>
</body>
</html>