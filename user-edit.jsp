<%@ page import="Model.User,Services.LocalServices"%>
<%
if(session.getAttribute("auth")==null){
    response.sendRedirect("Login.jsp");
}
User user=(User)session.getAttribute("auth");
String profile_pic=user.getProfilepic()==null?"https://manager.almadarisp.com/user/img/user.png":user.getProfilepic().contains("google")?user.getProfilepic():"data/user_dp/"+user.getUserId()+".jpg";
%>
<html>
<head>
	<link rel="stylesheet" href="./css/edit-page.css">
</head>
<body>
	<center>
			<form name="change-form" action="update-user-details" method="post" enctype="multipart/form-data">
				<div class="edit-option">
					<h1>Edit Option</h1>
					<div class="sub-option">
						<div class="sub-option-title">
							<h3>Profile Picture</h3>
							<button class="ed" type="button" onclick="uploadPic()">
								 <i class="fa-solid fa-pencil"></i>
								<p>Edit</p>
							</button>
						</div>
						<div class="sub-option-content" id="preview_dp_container">
							<img id="preview_dp" src=<%= profile_pic%> class="edit-user-dp">
							<input type="file" id="dp" name="dp" accept="Image/*" hidden>
						</div>
					</div>
					<div class="sub-option">
						<div class="sub-option-title">
							<h3>Other Details</h3>
						</div>
						<div class="sub-option-content">
							<table>
								<tr>
									<td>Name</td>
									<td><input name="username" class="per-details-input" type="text" value=<%= user.getUserName()%>></td>
								</tr>
								<tr>
									<td>Phone Number</td>
									<td><input class="per-details-input" type="text" value=<%= user.getPhoneNumber()%> readonly></td>
								</tr>
								<tr>
									<td>Email</td>
									<td><input class="per-details-input" type="text" value=<%= user.getEmail()%> readonly></td>
								</tr>
							</table>
						</div>
					</div>
					<div class="sub-option">
						<div class="sub-option-title">
							<h3>Bio</h3>
						</div>
						<div class="sub-option-content-left">
							<textarea name="bio" class="bio-filling-area" rows="3" cols="55" placeholder="Describe who are you ..."><%= user.getBio()==null?"":user.getBio()%></textarea>
						</div>
					</div>
					<div class="sub-option">
						<div class="sub-option-title">
							<h3>Customise your Intro</h3>
						</div>
						<div class="sub-option-content-left">
							<div class="icos">
								<i class="fa-sharp fa-solid fa-house"></i>
								<p>Lives in </p>
								<input name="livesin" type="text" value=<%= user.getLivesIn()==null?"":user.getLivesIn()%>>
							</div>
							<div class="icos">
								<i class="fa-solid fa-clock"></i>
								<p>Joined On <span class="underline"><%= LocalServices.getMonthYear(user.getAccountOpened())%></span> </p>
							</div>
						</div>
					</div>
					<p class="err_msg">${error}</p>
					<p class="succ_msg">${succ}</p>
						<input type="submit" value="Update">
				</div>
					</form>
			</center>
</body>
<script>
	const custom_upload_btn=document.getElementById("dp");
	function uploadPic(){
		custom_upload_btn.click();
	}
</script>
</html>