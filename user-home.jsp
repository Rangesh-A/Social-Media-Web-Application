<%@ page import="Model.User,Services.LocalServices"%>
<%User user=(User)session.getAttribute("auth");%>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>Zoho Social | <%= user.getUserName()%></title>
	<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.2.1/css/all.min.css" integrity="sha512-MV7K8+y+gLIBoVD59lQIYicR65iaqukzvf/nwasF0nqhPay5w/9lJmVM2hMDcnK1OnMGCdVK+iQrJ7lzPJQd1w==" crossorigin="anonymous" referrerpolicy="no-referrer" />
	<link href="https://fonts.googleapis.com/css2?family=Poppins:wght@200&display=swap" rel="stylesheet">
	<link href="https://fonts.googleapis.com/css2?family=Poppins:wght@200;300;400&display=swap" rel="stylesheet">
	<link rel="stylesheet" href="./css/home.css">
</head>
<body>
<jsp:include page="header.jsp" />
<div class="p-container">
	<div class="posts">
		<jsp:include page="posts.jsp" />
	</div>
</div>
</body>
</html>