<%@ page import="java.util.*,Model.User,Model.Post,Database.DbConnection,Dao.ConnectionDao,Dao.UserDao,Dao.RequestDao,Dao.PostDao,Services.LocalServices"%>
<%
	if(session.getAttribute("auth")==null){
		response.sendRedirect("Login.jsp");
	}
%>
<%

	User user=(User)session.getAttribute("auth");
	int	profile_id=Integer.parseInt(request.getParameter("profileid"));

	UserDao userdao=new UserDao(DbConnection.getConnection());
	ConnectionDao connectiondao=new ConnectionDao(DbConnection.getConnection());
	RequestDao requestdao=new RequestDao(DbConnection.getConnection());
	User profile_user=null;
	boolean isCurrentUserProfile=profile_id==user.getUserId();
	if(isCurrentUserProfile){
		profile_user=user;
	}else{
		profile_user=userdao.getUserById(profile_id);
	}
	if(profile_user==null){
		response.sendRedirect("user-home.jsp");
		return;
	}
	String profile_pic=profile_user.getProfilepic()==null?"https://manager.almadarisp.com/user/img/user.png":profile_user.getProfilepic().contains("google")?profile_user.getProfilepic():"data/user_dp/"+profile_user.getUserId()+".jpg";
	List<User> friends=connectiondao.getAllFriends(profile_id);
%>
<html>
<head>
	<title>Zoho</title>
	<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.2.1/css/all.min.css" integrity="sha512-MV7K8+y+gLIBoVD59lQIYicR65iaqukzvf/nwasF0nqhPay5w/9lJmVM2hMDcnK1OnMGCdVK+iQrJ7lzPJQd1w==" crossorigin="anonymous" referrerpolicy="no-referrer" />
	<link href="https://fonts.googleapis.com/css2?family=Poppins:wght@200&display=swap" rel="stylesheet">
	<link href="https://fonts.googleapis.com/css2?family=Poppins:wght@200;300;400&display=swap" rel="stylesheet">
	<link rel="stylesheet" href="./css/find-friends.css">
	<link rel="stylesheet" href="./css/profile.css">
	<link href="https://unpkg.com/aos@2.3.1/dist/aos.css" rel="stylesheet">
	<script src="https://unpkg.com/aos@2.3.1/dist/aos.js"></script>
</head>
<body>
<div class="container">
	<jsp:include page="header.jsp" />
	<div class="body">
		<div class="profile-section">
			<div class="profile-section-left" data-aos="fade-right">
				<img src=<%=profile_pic %> class="user-dp">
				<div class="name-friends">
					<p><%=LocalServices.capitalizeName(profile_user.getUserName()) %></p>
					<p class="friends-count"><%=friends.size() %> friend<%=friends.size()>1||friends.size()==0?"s":"" %></p>
				</div>
			</div>
			<div class="profile-section-right" data-aos="fade-left">
				<%
					String btn_text="";
					String btn_style="normal";
					String dis="";
					if(requestdao.isAlreadyRequestSent(user.getUserId(),profile_user.getUserId() )){
						btn_text="Request sent";
						btn_style="sent";
						dis="disabled";
					}else if(connectiondao.isFriends(user.getUserId(),profile_user.getUserId())){
						btn_text="Friends";
						btn_style="friends";
						dis="disabled";
					}
					else{
						btn_text="Add Friend";
						btn_style="normal";
					}
					if(profile_user.getUserId()==user.getUserId()){%>
				<button onclick="window.location='user-profile.jsp?profileid=<%=profile_id%>&page=edit'" class="edit-profile-button">Edit Profile</button>
				<%}else{%>
				<div class="peoples-right">
					<%if(requestdao.isAlreadyRequestSent(user.getUserId(),profile_user.getUserId())){ %>
					<form action="cancel-friend-request" id="cancel_request_form_<%=profile_user.getUserId() %>" method="post">
						<input type="hidden" name="receiverid" value=<%=profile_user.getUserId() %>>
						<input type="submit" name="cancel_btn" class="add-btn" id="cancel-btn" value="Cancel">

					</form>
					<%}else if(connectiondao.isFriends(user.getUserId(),profile_user.getUserId())){%>
					<form action="unfriend" method="post">
						<input type="hidden" name="friend_id" value=<%=profile_user.getUserId() %>>
						<input type="submit" name="cancel_btn" class="add-btn" id="cancel-btn" value="Unfriend">

					</form>
					<%} %>
					<%if(requestdao.isAlreadyRequestCame(user.getUserId(),profile_user.getUserId())){ %>
					<form action="react-friend-request" method="post">
						<input type="hidden" name="type" value="reject">
						<input type="hidden" name="friend_id" value=<%=profile_user.getUserId() %>>
						<input type="hidden" name="request_id" value=<%= requestdao.getRequestId(profile_user.getUserId(), user.getUserId())%>>
						<input type="submit" name="cancel_btn" class="add-btn" id="cancel-btn" value="Decline" >

					</form>
					<form action="react-friend-request" id="accept_form_<%=profile_user.getUserId() %>" method="post">
						<input type="hidden" name="type" value="accept">
						<input type="hidden" name="friend_id" value=<%=profile_user.getUserId() %>>
						<input type="hidden" name="request_id" value=<%= requestdao.getRequestId(profile_user.getUserId(), user.getUserId())%>>
						<input type="submit" name="add_btn" class="add-btn clr" id=<%= btn_style%> value="Accept" >

					</form>
					<%}else{ %>
					<form action="send-friend-request" id="send_request_form_<%=profile_user.getUserId() %>" method="post">
						<input type="hidden" name="receiverid" value=<%=profile_user.getUserId() %>>
						<input type="submit" name="add_btn" class="add-btn" id=<%= btn_style%> value="<%=btn_text%>" <%=dis %>>

					</form>
					<%} %>
				</div>
				<%} %>
			</div>
		</div>
		<hr>
		<%String current_page=request.getParameter("page");%>
		<div class="tab-options" data-aos="fade-down">
			<button onclick="window.location='user-profile.jsp?profileid=<%=profile_id%>&page=post'" class=<%=current_page!=null&&current_page.equals("post")?"selected-tab":""%>>Posts</button>
			<button onclick="window.location='user-profile.jsp?profileid=<%=profile_id%>&page=about'" class=<%=current_page!=null&&current_page.equals("about")?"selected-tab":""%>>About</button>
			<button onclick="window.location='user-profile.jsp?profileid=<%=profile_id%>&page=friends'" class=<%=current_page!=null&&current_page.equals("friends")?"selected-tab":""%>>Friends</button>
			<button onclick="window.location='user-profile.jsp?profileid=<%=profile_id%>&page=photos'"class=<%=current_page!=null&&current_page.equals("photos")?"selected-tab":""%>>Photos</button>
		</div>
		<br>
		<%if(request.getParameter("page")==null||request.getParameter("page").equals("post")){%>
		<div class="center-align">
			<jsp:include page="posts.jsp" />
		</div>
		<%}else if(request.getParameter("page")!=null&&request.getParameter("page").equals("edit")){%>
		<jsp:include page="user-edit.jsp" />
		<%}else if(request.getParameter("page")!=null&&request.getParameter("page").equals("about")){%>
		<jsp:include page="user-about.jsp" />
		<%}else if(request.getParameter("page")!=null&&request.getParameter("page").equals("friends")){%>
		<jsp:include page="friends-list.jsp" />
		<%}%>

	<%
	List<Post> posts=null;
	PostDao postdao=new PostDao(DbConnection.getConnection());
	posts= postdao.getPostsById(profile_id);
	%>
	<%if(request.getParameter("page")==null||request.getParameter("page").equals("photos")){%>
		<div class="photo-gallery">
			<%-- <img src="https://images.pexels.com/photos/36487/above-adventure-aerial-air.jpg"> --%>
			<%for(Post post:posts){%>
				<%String post_media_path=post.getPath();
				if(post.getMedia()!=null&&post.getMedia().equals("image")){%>
					<img class="media" src="<%=post_media_path%>" alt="Media no more exists" data-aos="zoom-in">
				<%}else if(post.getMedia()!=null&&post.getMedia().equals("video")){
					boolean duplicate=post.getPath().substring(post.getPath().length()-4).equals("null");%>
					<video class="media" id="video" controls autoplay data-aos="zoom-in">
						<source src="<%=post_media_path%>">
					</video>
				<%}
			}%>
		</div>
	</div>
	<%}%>
</div>
</body>
<script type="text/javascript">
	AOS.init();
	const file1=document.getElementById("dp");
	const previewcontainer=document.getElementById("preview_dp");
	file1.addEventListener("change",function(){
		const file=this.files[0];
		if(file){
			const reader=new FileReader();
			reader.addEventListener("load",function(){
				previewcontainer.setAttribute("src",this.result);
			});
			reader.readAsDataURL(file);
		}
	});
</script>
</html>