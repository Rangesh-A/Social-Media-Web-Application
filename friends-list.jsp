<%@ page import="Model.User,Model.FriendRequest,Services.LocalServices,Dao.UserDao,Dao.RequestDao,Dao.ConnectionDao,java.util.*,Database.DbConnection"%>
<html>
<head>
	<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.2.1/css/all.min.css" integrity="sha512-MV7K8+y+gLIBoVD59lQIYicR65iaqukzvf/nwasF0nqhPay5w/9lJmVM2hMDcnK1OnMGCdVK+iQrJ7lzPJQd1w==" crossorigin="anonymous" referrerpolicy="no-referrer" />
	<link href="https://fonts.googleapis.com/css2?family=Poppins:wght@200&display=swap" rel="stylesheet">
	<link href="https://fonts.googleapis.com/css2?family=Poppins:wght@200;300;400&display=swap" rel="stylesheet">
	<link rel="stylesheet" href="./css/friends_list.css">
</head>
<body>
<div class="list">
	<%
		if(session.getAttribute("auth")==null){
			response.sendRedirect("Login.jsp");
		}
		User user=(User)session.getAttribute("auth");
	%>
	<%String profile_pic=user.getProfilepic()==null?"https://manager.almadarisp.com/user/img/user.png":user.getProfilepic().contains("google")?user.getProfilepic():"data/user_dp/"+user.getUserId()+".jpg";
		UserDao userdao=new UserDao(DbConnection.getConnection());
		ConnectionDao connectiondao=new ConnectionDao(DbConnection.getConnection());
		RequestDao requestdao=new RequestDao(DbConnection.getConnection());
		int	profile_id=user.getUserId();
		if(request.getParameter("profileid")!=null){
			profile_id=Integer.parseInt(request.getParameter("profileid"));
		}
		User profile_user=null;
		boolean isCurrentUserProfile=profile_id==user.getUserId();
		if(isCurrentUserProfile){
			profile_user=user;
		}else{
			profile_user=userdao.getUserById(profile_id);
		}
	%>
	<%
		List<User> friends=connectiondao.getAllFriends(profile_id);
		if(friends.size()>0){
	%>

	<div class="friend-requests" data-aos="zoom-in">

		<%for(User friend:friends){
			String time=connectiondao.getConnectionTime(profile_id,friend.getUserId());
			time=LocalServices.getByFormat("dd")+", "+LocalServices.getMonthYear(time);

			String btn_text="";
			String btn_style="normal";
			String dis="";
			if(requestdao.isAlreadyRequestSent(user.getUserId(),friend.getUserId() )){
				btn_text="Friend request sent";
				btn_style="sent";
				dis="disabled";
			}else if(connectiondao.isFriends(user.getUserId(),friend.getUserId())){
				btn_text="Friends";
				btn_style="friends";
				dis="disabled";
			}
			else{
				btn_text="Add Friend";
				btn_style="normal";
			}
			profile_pic=friend.getProfilepic()==null?"https://manager.almadarisp.com/user/img/user.png":friend.getProfilepic().contains("google")?friend.getProfilepic():"data/user_dp/"+friend.getUserId()+".jpg";
		%>
		<div class="friend-request">
			<a href="user-profile.jsp?profileid=<%=friend.getUserId()%>">
				<div class="friend-request-top">
					<img src="<%=profile_pic%>">
				</div>
				<div class="friend-request-bottom">
					<h4><%=friend.getUserName() %></h4>
					<p class="timee">Friends From <%=time %></p>
			</a>
			<%if(user.getUserId()==friend.getUserId()){ %>
			<input type="submit" id="unf-btn" class="acc-btn confrm-btn" value="You" disabled>
			<%}else if(connectiondao.isFriends(user.getUserId(),friend.getUserId())){%>
			<form action="unfriend" method="post" id="unfriend-<%=friend.getUserId()%>">
				<input type="hidden" name="friend_id" value=<%=friend.getUserId() %>>
				<input onclick="document.getElementById('unfriend-<%=friend.getUserId()%>').submit()" id="unf-btn" class="acct-btn confrm-btn" value="Unfriend">
			</form>
			<%}else if(!requestdao.isAlreadyRequestSent(user.getUserId(),friend.getUserId())){ %>
			<form action="send-friend-request" id="send_request_form_<%=friend.getUserId() %>" method="post">
				<input type="hidden" name="receiverid" value=<%=friend.getUserId() %>>
				<input type="submit" id="unf-btn" class="acct-btn confrm-btn" value="<%=btn_text %>" <%=dis%>>
			</form>
			<%}else{%>
			<input type="submit" id="unf-btn" class="acc-btn confrm-btn" value="<%=btn_text %>" <%=dis%>>
			<%} %>
		</div>
	</div>
	<%}
	}else{
	%>
	<div class="no-friend-requests">
		<h1>Has no friends</h1>
		<img src="https://img.freepik.com/free-vector/alone-concept-illustration_114360-2393.jpg?w=1060&t=st=1671937044~exp=1671937644~hmac=767bce8365c52bde988442440c08470c12e5a8cc5450756925b8e687e6548c2f">
	</div>
	<%} %>
</div>
</div>
</body>
</html>