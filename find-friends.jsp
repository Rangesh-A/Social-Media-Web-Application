<%@ page import="Model.User,Model.FriendRequest,Services.LocalServices,Dao.UserDao,Dao.RequestDao,Dao.ConnectionDao,java.util.*,Database.DbConnection"%>
<%
	if(session.getAttribute("auth")==null){
		response.sendRedirect("Login.jsp");
	}
	User user=(User)session.getAttribute("auth");
	String profile_pic=user.getProfilepic()==null?"https://manager.almadarisp.com/user/img/user.png":user.getProfilepic().contains("google")?user.getProfilepic():"data/user_dp/"+user.getUserId()+".jpg";
%>
<html>
<head>
	<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.2.1/css/all.min.css" integrity="sha512-MV7K8+y+gLIBoVD59lQIYicR65iaqukzvf/nwasF0nqhPay5w/9lJmVM2hMDcnK1OnMGCdVK+iQrJ7lzPJQd1w==" crossorigin="anonymous" referrerpolicy="no-referrer" />
	<link href="https://fonts.googleapis.com/css2?family=Poppins:wght@200&display=swap" rel="stylesheet">
	<link href="https://fonts.googleapis.com/css2?family=Poppins:wght@200;300;400&display=swap" rel="stylesheet">
	<link rel="stylesheet" href="./css/find-friends.css">
	<link href="https://unpkg.com/aos@2.3.1/dist/aos.css" rel="stylesheet">
	<script src="https://unpkg.com/aos@2.3.1/dist/aos.js"></script>
</head>
<body>
<%
	UserDao userdao=new UserDao(DbConnection.getConnection());
	String search_text=request.getParameter("search");
	List<User> users=new ArrayList<>();
	if(search_text!=null){
		users=userdao.getUsers(user.getUserId(),search_text.toLowerCase());
	}
	String current_page=request.getParameter("page");
%>
<jsp:include page="header.jsp" />
<center>
	<div class="tab-options">
		<button onclick="window.location='find-friends.jsp?page=friend-requests'" class=<%=current_page!=null&&current_page.equals("friend-requests")?"selected-tab":""%>>Friend Requests</button>
		<button onclick="window.location='find-friends.jsp?page=suggestions'" class=<%=current_page!=null&&current_page.equals("suggestions")?"selected-tab":""%>>Suggestions</button>
		<button onclick="window.location='find-friends.jsp?page=find-friends'" class=<%=current_page!=null&&current_page.equals("find-friends")?"selected-tab":""%>>Find Friends</button>
		<button onclick="window.location='find-friends.jsp?page=all-friends'" class=<%=current_page!=null&&current_page.equals("all-friends")?"selected-tab":""%>>All Friends</button>
	</div>
	<div class="find-container">
		<%if((current_page!=null&&current_page.equals("find-friends"))||search_text!=null){ %>
		<div class="peoples">
			<h1>People</h1>
			<%if(search_text==null){
				users=userdao.getAllUsers(user.getUserId());
			}
				for(User search_user:users){
					profile_pic=search_user.getProfilepic()==null?"https://manager.almadarisp.com/user/img/user.png":search_user.getProfilepic().contains("google")?search_user.getProfilepic():"data/user_dp/"+search_user.getUserId()+".jpg";
			%>
			<div class="people">
				<div class="peoples-left">
					<div class="peoples-left-dp">
						<a href="user-profile.jsp?profileid=<%=search_user.getUserId()%>">
							<img class="friend-user-dp" src="<%=profile_pic%>">
						</a>
					</div>
					<div class="peoples-left-details">
						<a href="user-profile.jsp?profileid=<%=search_user.getUserId()%>">
							<h3><%=search_user.getUserName() %></h3>
						</a>
						<%if(search_user.getLivesIn()!=null&&!search_user.getLivesIn().isEmpty()){ %>
						<p>From <%=search_user.getLivesIn() %></p>
						<%} %>
					</div>
				</div>
				<%
					RequestDao requestdao=new RequestDao(DbConnection.getConnection());
					ConnectionDao connectiondao=new ConnectionDao(DbConnection.getConnection());
					String btn_text="";
					String btn_ico="";
					String btn_style="normal";
					String dis="";
					if(requestdao.isAlreadyRequestSent(user.getUserId(),search_user.getUserId() )){
						btn_text="Request sent";
						btn_ico="fa-check";
						btn_style="sent";
						dis="disabled";
					}else if(connectiondao.isFriends(user.getUserId(),search_user.getUserId())){
						btn_text="Friends";
						btn_ico="fa-handshake";
						btn_style="friends";
						dis="disabled";
					}
					else{
						btn_text="Add Friend";
						btn_ico="fa-user-plus";
						btn_style="normal";
					}
				%>

				<div class="peoples-right">
					<%if(requestdao.isAlreadyRequestSent(user.getUserId(),search_user.getUserId())){ %>
					<form action="cancel-friend-request" id="cancel_request_form_<%=search_user.getUserId() %>" method="post">
						<input type="hidden" name="receiverid" value=<%=search_user.getUserId() %>>
						<input type="submit" name="cancel_btn" class="add-btn" id="cancel-btn" value="Cancel">
						<i id="cancel-icon" class="fa-solid fa-ban"></i>
					</form>
					<%}else if(connectiondao.isFriends(user.getUserId(),search_user.getUserId())){%>
					<form action="unfriend" id="unfriend_form_<%=search_user.getUserId() %>" method="post">
						<input type="hidden" name="friend_id" value=<%=search_user.getUserId() %>>
						<input type="submit" name="cancel_btn" class="add-btn" id="cancel-btn" value="Unfriend">
						<i id="cancel-icon"  class="fa-solid fa-handshake-simple-slash"></i>
					</form>
					<%} %>
					<%if(requestdao.isAlreadyRequestCame(user.getUserId(),search_user.getUserId())){ %>
					<form action="react-friend-request" id="decline_form_<%=search_user.getUserId() %>" method="post">
						<input type="hidden" name="type" value="reject">
						<input type="hidden" name="friend_id" value=<%=search_user.getUserId() %>>
						<input type="hidden" name="request_id" value=<%= requestdao.getRequestId(search_user.getUserId(), user.getUserId())%>>
						<input type="submit" name="cancel_btn" class="add-btn" id="cancel-btn" value="Decline" >
						<i id="cancel-icon" class="fa-solid fa-handshake-simple-slash"></i>
					</form>
					<form action="react-friend-request" id="accept_form_<%=search_user.getUserId() %>" method="post">
						<input type="hidden" name="type" value="accept">
						<input type="hidden" name="friend_id" value=<%=search_user.getUserId() %>>
						<input type="hidden" name="request_id" value=<%= requestdao.getRequestId(search_user.getUserId(), user.getUserId())%>>
						<input type="submit" name="add_btn" class="add-btn clr" id=<%= btn_style%> value="Accept" >
						<i id="add-icon" class="fa-solid <%=btn_ico%>"></i>
					</form>
					<%}else{ %>
					<form action="send-friend-request" id="send_request_form_<%=search_user.getUserId() %>" method="post">
						<input type="hidden" name="receiverid" value=<%=search_user.getUserId() %>>
						<input type="submit" name="add_btn" class="add-btn" id=<%= btn_style%> value="<%=btn_text%>" <%=dis %>>
						<i id="add-icon" class="fa-solid <%=btn_ico%>"></i>
					</form>
					<%} %>
				</div>
			</div>
			<%}
				if(search_text!=null&&users.size()==0){%>
			<div class="no-users">
				<h1>No People found with given name</h1>
				<img src="https://img.freepik.com/free-vector/happy-people-air-balloon-flat-vector-illustration-man-woman-employees-looking-through-binoculars-searching-creative-vacancies-finding-work-job-hunting-research-concept_74855-24259.jpg?w=1060&t=st=1671937166~exp=1671937766~hmac=1985cc94d86770556dcce4e83d5f567b182e3938718462ff26ddde2a7bc40d14">
			</div>
			<%}%>
		</div>
		<%}else if(current_page!=null&&current_page.equals("friend-requests")){
			RequestDao reqdao=new RequestDao(DbConnection.getConnection());
			List<FriendRequest> requests=reqdao.getRequests(user.getUserId());
		%>
		<div class="friend-requests">
			<%
				if(requests.size()>0){
					for(FriendRequest frnd_request:requests){
						User requested_friend=new UserDao(DbConnection.getConnection()).getUserById(frnd_request.getSenderId());
						profile_pic=requested_friend.getProfilepic()==null?"https://manager.almadarisp.com/user/img/user.png":requested_friend.getProfilepic().contains("google")?requested_friend.getProfilepic():"data/user_dp/"+requested_friend.getUserId()+".jpg";
			%>
			<a href="user-profile.jsp?profileid=<%=requested_friend.getUserId()%>">
				<div class="friend-request">
					<div class="friend-request-top">
						<img src=<%=profile_pic%>>
					</div>
					<div class="friend-request-bottom">
						<h4><%=requested_friend.getUserName() %></h4>
						<form action="react-friend-request" method="post">
							<input type="hidden" value=<%=requested_friend.getUserId()%> name="friend_id">
							<input type="hidden" value="accept" name="type">
							<input type="hidden" value=<%=frnd_request.getRequestId() %> name="request_id">
							<input type="submit" class="acct-btn confrm-btn" value="Confirm">
						</form>
						<form action="react-friend-request" method="post">
							<input type="hidden" value=<%=requested_friend.getUserId()%> name="friend_id">
							<input type="hidden" value="reject" name="type">
							<input type="hidden" value=<%=frnd_request.getRequestId() %> name="request_id">
							<input type="submit" class="acct-btn" value="Delete">
						</form>
					</div>
				</div>
			</a>
			<%}
			}else{
			%>
			<div class="no-friend-requests">
				<h1>You got no Friend requests</h1>
				<img src="https://img.freepik.com/free-vector/business-team-looking-new-people-allegory-searching-ideas-staff-woman-with-magnifier-man-with-spyglass-flat-illustration_74855-18236.jpg?w=1060&t=st=1671859066~exp=1671859666~hmac=6327a78c35c2febe74c532a620260792f589d68ca82a90d605cbded1ab8e05d5">
			</div>
			<%} %>
		</div>
		<%}else if(current_page!=null&&current_page.equals("all-friends")){%>
		<jsp:include page="friends-list.jsp" />
		<%}else if(current_page!=null&&current_page.equals("suggestions")){%>
		<div class="no-friend-suggestion">
			<h1>Currently no suggestions</h1>
			<img src="https://img.freepik.com/free-vector/people-search-concept-illustration_114360-1044.jpg?w=826&t=st=1671992476~exp=1671993076~hmac=b6762d55405a2a2abc45457c7484b7a4c42883732901af3486be91548d271568">
		</div>
		<%} %>
	</div>
</center>
</body>
<Script>
AOS.init();
	const search_box=document.getElementById("search_box");
	const finder=document.getElementById("search_div");
	function getText(){
		console.log(search_box.value);
		if(search_box.value!=""){
			var old_search='<%=search_text%>';
			var loc=window.location.href.toString();
			if(window.location.href.substring(window.location.href.length - 3)=="jsp"){
				loc+="?search="+search_box.value;
			}else{
				loc.replace("search="+old_search,"search="+search_box.value);
			}
			console.log(loc);
		}
	}
	function clearSearchBox(){
		document.getElementById('search_box').value = '';
		history.back();
	}
</Script>
</html>