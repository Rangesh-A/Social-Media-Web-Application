<%@ page language="java" contentType="text/html; charset=UTF-8"
		 pageEncoding="UTF-8"%>

<!doctype html>
<html>
<head>
	<meta charset="utf-8">
	<title>Google Signin</title>
	<meta name="google-signin-client_id" content="486914574709-8h9efa867i9sa929vlos65s2itlafplt.apps.googleusercontent.com">
	<script src="https://apis.google.com/js/platform.js" async defer></script>
	<script src="https://accounts.google.com/gsi/client" async defer></script>
	<script src="https://code.jquery.com/jquery-3.6.3.min.js" integrity="sha256-pvPw+upLPUjgMXY0G+8O0xUf+/Im1MZjXxxgOcBQBXU=" crossorigin="anonymous"></script>

</head>
<body>
<div id="g_id_onload"
	 data-client_id="486914574709-8h9efa867i9sa929vlos65s2itlafplt.apps.googleusercontent.com"
	 data-context="signin"
	 data-ux_mode="popup"
	 data-callback="signincall"
	 data-auto_prompt="true">
</div>

<div class="g_id_signin"
	 data-type="standard"
	 data-shape="rectangular"
	 data-text="signin_with"
	 data-size="large"
	 data-logo_alignment="left"
	 data-width="200px" style="display:block;">
</div>
<script type="text/javascript">
	var mail = null;

	function decodeJwtResponse(data) {
		var tokens = data.split(".");
		return JSON.parse(atob(tokens[1]));
	}

	window.signincall = (response) => {
		// decodeJwtResponse() is a custom function defined by you
		// to decode the credential response.
		responsePayload = decodeJwtResponse(response.credential);
		//console.log(response);
		var name = responsePayload.name;
		console.log("ID: " + responsePayload.sub);
		console.log('Full Name: ' + responsePayload.name);
		console.log('Given Name: ' + responsePayload.given_name);
		console.log('Family Name: ' + responsePayload.family_name);
		console.log("Image URL: " + responsePayload.picture);
		console.log("Email: " + responsePayload.email);
		mail = responsePayload.email;
		$.post(
				"registeruser",
				{name :responsePayload.name,
					profilepic:responsePayload.picture,
					email:responsePayload.email,
					type:"oauth"}, //meaasge you want to send
				function(result) {
					console.log(result);
					//location.href=result;
					location.reload();
				});
		//location.href='http://localhost:8080/Zoho_Social/view.jsp';
	}

	function onSignout() {
		google.accounts.id.disableAutoSelect();
		console.log("signed out");
		google.accounts.id.revoke(mail , done => {
			console.log('consent revoked');
			GoogleAuth.signOut()
			// document.location.href = "https://www.google.com/accounts/Logout?continue=https://appengine.google.com/_ah/logout?continue=http://localhost:8080";
		});
	}
</script>
</script>
</body>
</html>
