<%@ page import="Model.User,java.util.*,Database.DbConnection,Model.FriendRequest,Dao.RequestDao,Services.LocalServices,jakarta.servlet.*,java.io.*"%>
<%@ page import="Dao.UserDao" %>
<head>
	<meta charset="UTF-8">
	<title>home</title>
	<link rel="stylesheet" href="./css/head.css">
	<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.2.1/css/all.min.css" integrity="sha512-MV7K8+y+gLIBoVD59lQIYicR65iaqukzvf/nwasF0nqhPay5w/9lJmVM2hMDcnK1OnMGCdVK+iQrJ7lzPJQd1w==" crossorigin="anonymous" referrerpolicy="no-referrer" />
	<meta name="google-signin-client_id" content="486914574709-8h9efa867i9sa929vlos65s2itlafplt.apps.googleusercontent.com">
	<script src="https://apis.google.com/js/platform.js" async defer></script>
	<script src="https://accounts.google.com/gsi/client" async defer></script>
	<script src="https://code.jquery.com/jquery-3.6.3.min.js" integrity="sha256-pvPw+upLPUjgMXY0G+8O0xUf+/Im1MZjXxxgOcBQBXU=" crossorigin="anonymous"></script>
</head>
<%
	if(session.getAttribute("auth")==null){
		response.sendRedirect("Login.jsp");
	}
	User user=(User)session.getAttribute("auth");
	String search_text=request.getParameter("search");
%>
<%String profile_pic=user.getProfilepic()==null?"https://manager.almadarisp.com/user/img/user.png":user.getProfilepic().contains("google")?user.getProfilepic():"data/user_dp/"+user.getUserId()+".jpg";
	RequestDao reqdao=new RequestDao(DbConnection.getConnection());
	List<FriendRequest> requests=reqdao.getRequests(user.getUserId());
	int friend_requests=requests.size();
	int unread_messages_count=0;
	try{
		unread_messages_count=new UserDao(DbConnection.getConnection()).getunreadMessages(user.getUserId());
	}catch(Exception e){
		out.println(e);
	}
%>
<form action="find-friends.jsp">
	<div class="header">
		<div class="left">
			<img class='logo' src="https://www.zoho.com/branding/images/zoho-logo-512px.png">
			<i class="fa-solid fa-magnifying-glass" id="search-glass"></i>
			<input type="text" class="search-box" id="search_box" name="search" placeholder="Search for Friends" value=<%=search_text==null?"":search_text %>>
			<i class="fa-solid fa-xmark"  id="clear" onclick="clearSearchBox()"></i>
			<button onclick="search()" type="submit" id="search-btn">search</button>
</form>
</div>
	<div class="center">
		<a href="user-home.jsp"><i class="fa-solid fa-house-chimney"></i></a>
		<a href="find-friends.jsp?page=friend-requests"><i class="fa-solid fa-user-group"><span class="count"><%=friend_requests==0?"":friend_requests %></span></i></a>
		<a href="http://localhost:8080/Zoho_Chat/user-home.jsp"><i class="fa-sharp fa-solid fa-message"><span class="count"><%=unread_messages_count==0?"":unread_messages_count%></span></i></a>
		<a href="user-settings.jsp"><i class="fa-solid fa-gear"></i></a>
	</div>
	<div class="right">
		<a href="user-profile.jsp?profileid=<%=user.getUserId()%>&page=post">
			<div class="profile">
				<img src="<%=profile_pic %>" class="small-user-dp">
				<p><%=LocalServices.capitalizeName(user.getUserName()) %></p>
			</div>
		</a>
		<form action="logoutuser" id="logout_form" method="POST" class="logout" >
			<input type="button" onclick="onSignout()" value="logout">
		</form>
	</div>
</div>
<script>
	var mail = null;
	window.signincall = (response) => {
		// decodeJwtResponse() is a custom function defined by you
		// to decode the credential response.
		responsePayload = decodeJwtResponse(response.credential);
		mail = responsePayload.email;
		console.log(mail);
	}
	function onSignout() {
		google.accounts.id.disableAutoSelect();
		console.log("signed out");
		google.accounts.id.revoke(mail , done => {
			console.log('consent revoked');
			document.getElementById('logout_form').submit();
		});
	}
</script>