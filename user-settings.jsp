<%@ page import="Model.User,Services.LocalServices,Dao.UserDao,Database.DbConnection"%>
	<%
	if(session.getAttribute("auth")==null){
	    response.sendRedirect("Login.jsp");
	}
	User user=(User)session.getAttribute("auth");
	%>
<html>
<head>
	<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.2.1/css/all.min.css" integrity="sha512-MV7K8+y+gLIBoVD59lQIYicR65iaqukzvf/nwasF0nqhPay5w/9lJmVM2hMDcnK1OnMGCdVK+iQrJ7lzPJQd1w==" crossorigin="anonymous" referrerpolicy="no-referrer" />
	<link href="https://fonts.googleapis.com/css2?family=Poppins:wght@200&display=swap" rel="stylesheet">
	<link href="https://fonts.googleapis.com/css2?family=Poppins:wght@200;300;400&display=swap" rel="stylesheet">
	<link rel="stylesheet" href="./css/user-settings.css">
</head>
<body>
		<jsp:include page="header.jsp" />  
		<div class="settings-container">
			<div class="table-container">
					<h2>General Account Settings</h2>
					<hr>
					<table>
						<tr>
							<td class="left-column"><h3>Name</h3></td>
							<td class="center-column"><p><%=user.getUserName() %></p></td>
							<td class="right-column"><a href="user-profile.jsp?profileid=<%=user.getUserId()%>&page=edit">Edit</a></td>
						</tr>
						<tr>
							<td class="left-column" ><h3>Email</h3></td>
							<td class="center-column"><p><%=user.getEmail() %></p></td>
							<td class="right-column"><a>Edit</a></td>
						</tr>
						<tr>
							<td class="left-column"><h3>Mobile</h3></td>
							<td class="center-column"><p><%=user.getPhoneNumber() %></p></td>
							<td class="right-column"><a >Edit</a></td>
						</tr>
						<tr>
							<td class="left-column"><h3>Profile Settings</h3></td>
							<td class="center-column"><p>Profile Picture,Bio etc...</p></td>
							<td class="right-column"><a href="user-profile.jsp?profileid=<%=user.getUserId()%>&page=edit">Edit</a></td>
						</tr>
						<tr>
							<td class="left-column"><h3>Password</h3></td>
							<td class="center-column"><p>
								<form action="change-password" method="post" id="password-change-form">
									<input type="password" placeholder="Old Password" name="old_password" id="old_password">
									<input type="password" placeholder="New Password" name="new_password" id="new_password" required>
									<input type="password" placeholder="Confirm Password" name="confirm_password" id="confirm_password" required>
									<input type="submit" id="sub_btn">
								</form>
								<p id="err_msg">${error}</p>
								<p id="succ_msg">${success}</p>
							</p></td>
							<td class="right-column">
								<button  id="cng-btn" onclick="validate()">Change</button>
							</td>
						</tr>
						<%if(false){ %>
						<tr>
							<td class="left-column"><h3>Two step Auth</h3></td>
							<td class="center-column">
								<div class="toggle" onclick="toggleAuth()" id="toggle">
									<div class="toggler"  id="toggler">						
									</div>
								</div>
							</td>
						</tr>
						<%} %>
					</table>
						<center>
							<button id="deact-acnt-btn" onclick="openDialog()">Deactivate Account</button>
						</center>
					<hr>
			</div>
		</div>		
		<div class="dialogue-box" id="dialog">
			<div class="dialogue-box-middle">
				<h1>Confirm Deactivate your Account</h1>
				<p><b>Note:</b> Your account will be no longer accessible.</p>
			</div>
			<div class="dialogue-box-bottom">
				<form action="deactivate-account" method="post">
					<input class="dia-btn" id="con-btn" type="submit" value="Deactivate">
					<button class="dia-btn" type="button" id="can-btn" onclick="closeDialog()">Cancel</button>
				</form>
			</div>
		</div>
</body>
<script type="text/javascript">
	function validate(){
		var err1="Confirm password does'nt match!!!";
		var err2="Old password cannot be new password!!!";
		var old_password=document.getElementById('old_password');
		var new_password=document.getElementById('new_password');
		var con_password=document.getElementById('confirm_password');
		var err=document.getElementById('err_msg');
		if(new_password.value!==con_password.value){
			new_password.value="";
			con_password.value="";
			err.innerHTML=err1;
		}else if(old_password.value==new_password.value){
			new_password.value="";
			con_password.value="";
			old_password.value="";
			err.innerHTML=err2;
		}else{
			document.getElementById('sub_btn').click();
		}
	}
	
	var dialog=document.getElementById('dialog');
	function openDialog(){
		dialog.style="display:block !important";
	}
	function closeDialog(){
		dialog.style="display:none !important";
	}
	var back=document.getElementById("toggle");
	var front=document.getElementById("toggler");
	var i=1;
	if(i==0){
		back.style.justifyContent="flex-end";
		back.style.backgroundColor="#94cfa0";
	}else{
		back.style.justifyContent="flex-start";
		back.style.backgroundColor="#ff8080";
	}
	function toggleAuth(){

		if(i==0){
			back.style.justifyContent="flex-end";
			back.style.backgroundColor="#94cfa0";
			i=1;
		}else{
			back.style.justifyContent="flex-start";
			back.style.backgroundColor="#ff8080";
			i=0;
		}
	}
</script>
</html>