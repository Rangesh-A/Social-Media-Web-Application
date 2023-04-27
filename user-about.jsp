	<%@ page import="Model.User,Services.LocalServices,Dao.UserDao,Database.DbConnection"%>
	<%
	if(session.getAttribute("auth")==null){
	    response.sendRedirect("Login.jsp");
	}
	User user=(User)session.getAttribute("auth");
	
	int profile_id=Integer.parseInt(request.getParameter("profileid"));
	UserDao userdao=new UserDao(DbConnection.getConnection());
	User profile_user=null;
	boolean isCurrentUserProfile=profile_id==user.getUserId();
	if(isCurrentUserProfile){
		profile_user=user;
	}else{
		profile_user=userdao.getUserById(profile_id);
	}
	%>
	<html>
	<head>
		<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.2.1/css/all.min.css" integrity="sha512-MV7K8+y+gLIBoVD59lQIYicR65iaqukzvf/nwasF0nqhPay5w/9lJmVM2hMDcnK1OnMGCdVK+iQrJ7lzPJQd1w==" crossorigin="anonymous" referrerpolicy="no-referrer" />
		<link rel="stylesheet" href="./css/about-page.css">
	</head>
	<body>
		<center>
			<div class="about">
				<div class=about-details>
					<div class="left">
						<h2>About</h2>
						<div class="bio">
							<h3>Bio</h3>
							<p><%=profile_user.getBio()==null?"Empty":profile_user.getBio()%></p>
						</div>
					</div>
					<div class="right">
						<div class="detail">
							<div class="small-left">
								<i class="fa-solid fa-suitcase"></i>
								<p>Works at <%=profile_user.getLivesIn()==null||profile_user.getLivesIn().isEmpty()?"Not filled":profile_user.getLivesIn() %></p>
							</div>
							<%if(isCurrentUserProfile){ %>
							<a href="user-profile.jsp?profileid=<%=user.getUserId()%>&page=edit">
								<div class="small-right">
									<i class="fa-solid fa-pencil"></i>
			  						<p>Edit</p>
								</div>
							</a>
							<%} %>
						</div>
						<div class="detail">
							<div class="small-left">
								<i class="fa-solid fa-house"></i>
								<p>Lives in <%=profile_user.getLivesIn()==null||profile_user.getLivesIn().isEmpty()?"Not filled":profile_user.getLivesIn() %></p>
							</div>
							<%if(isCurrentUserProfile){ %>
							<a href="user-profile.jsp?profileid=<%=user.getUserId()%>&page=edit">
								<div class="small-right">
									<i class="fa-solid fa-pencil"></i>
									<p>Edit</p>
								</div>
							</a>
							<%} %>
						</div>
						<div class="detail">
							<div class="small-left">
								<i class="fa-solid fa-location-dot"></i>
								<p>From <%=profile_user.getLivesIn()==null||profile_user.getLivesIn().isEmpty()?"Not filled":profile_user.getLivesIn() %></p>
							</div>
							<%if(isCurrentUserProfile){ %>
							<a href="user-profile.jsp?profileid=<%=user.getUserId()%>&page=edit">
								<div class="small-right">
									<i class="fa-solid fa-pencil"></i>
									<p>Edit</p>
								</div>
							</a>
							<%} %>
						</div>
						<div class="detail">
							<div class="small-left">
								<i class="fa-solid fa-phone"></i>
								<p>Mobile <%=profile_user.getPhoneNumber() %></p>
							</div>
							<%if(isCurrentUserProfile){ %>
								<div class="small-right">
									<i class="fa-solid fa-pencil"></i>
									<p>Edit</p>
								</div>	
								<%} %>			
						</div>
					</div>
				</div>
			</div>
		</center>
	</body>
	</html>