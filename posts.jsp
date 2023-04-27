<%@ page import="java.util.*,Model.User,Dao.UserDao,Model.Post,Services.LocalServices,java.util.*,Dao.PostDao,Database.DbConnection"%>
<%@ page import="Dao.ConnectionDao" %>
<%
	if(session.getAttribute("auth")==null){
		response.sendRedirect("Login.jsp");
	}
	User user=(User)session.getAttribute("auth");
%>
<%String path="data/user_dp/";%>
<%String profile_pic=user.getProfilepic()==null?"https://manager.almadarisp.com/user/img/user.png":user.getProfilepic().contains("google")?user.getProfilepic():"data/user_dp/"+user.getUserId()+".jpg";
	String myprofile_pic=user.getProfilepic()==null?"https://manager.almadarisp.com/user/img/user.png":user.getProfilepic().contains("google")?user.getProfilepic():"data/user_dp/"+user.getUserId()+".jpg";
	UserDao userdao=new UserDao(DbConnection.getConnection());
	ConnectionDao connectionDao=new ConnectionDao(DbConnection.getConnection());
	int	profile_id=user.getUserId();
	String cur_page=request.getParameter("profileid")==null?"home":"profile";
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
	String hashtag=request.getParameter("hashtag");
	PostDao postdao=new PostDao(DbConnection.getConnection());
%>
<html>
<head>
	<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.2.1/css/all.min.css" integrity="sha512-MV7K8+y+gLIBoVD59lQIYicR65iaqukzvf/nwasF0nqhPay5w/9lJmVM2hMDcnK1OnMGCdVK+iQrJ7lzPJQd1w==" crossorigin="anonymous" referrerpolicy="no-referrer" />
	<link rel="stylesheet" href="./css/post.css">
	<script src="https://code.jquery.com/jquery-3.6.3.min.js" integrity="sha256-pvPw+upLPUjgMXY0G+8O0xUf+/Im1MZjXxxgOcBQBXU=" crossorigin="anonymous"></script>
	<link href="https://unpkg.com/aos@2.3.1/dist/aos.css" rel="stylesheet">
	<script src="https://unpkg.com/aos@2.3.1/dist/aos.js"></script>

</head>
<body>

<div class="dialogue-box-share" id="dialog-share" >
	<div class="dialogue-box-top">
		<h2>Share to Friends</h2>
		<h1 id="close" onclick="closeShareDialog()">Done</h1>
	</div>
	<hr>
	<div class="dialogue-box-middle" >
		<div class="viewers">
			<%List<User> friends=connectionDao.getAllFriends(user.getUserId());
				for(User friend:friends){
					profile_pic=friend.getProfilepic()==null?"https://manager.almadarisp.com/user/img/user.png":friend.getProfilepic().contains("google")?friend.getProfilepic():"data/user_dp/"+friend.getUserId()+".jpg";%>
			<div class="people">
				<div class="peoples-left">
					<div class="peoples-left-dp">
						<a href="user-profile.jsp?profileid=<%=friend.getUserId()%>">
							<img class="friend-user-dp" src=<%=profile_pic%>>
						</a>
					</div>
					<div class="peoples-left-details">
						<a href="user-profile.jsp?profileid=<%=friend.getUserId()%>">
							<h3><%=friend.getUserName() %></h3>
						</a>
						<%if(friend.getLivesIn()!=null&&!friend.getLivesIn().isEmpty()){ %>
						<p>From <%=friend.getLivesIn() %></p>
						<%} %>
					</div>
					<div>
						<button type="button"  class="post-btn" onclick="sharepost(this)" value="<%= friend.getUserId()%>">Send</button>
					</div>
				</div>
			</div>
			<%}%>
		</div>
	</div>
</div>
<div id="er"></div>
<center>
	<%if(isCurrentUserProfile){ %>
	<div class="my-post" data-aos="fade-down">
		<div class="my-post-top">
			<img src=<%=myprofile_pic %> class="small-user-dp">
			<form action="post" method="post" class="post-form" enctype="multipart/form-data" id="postform">
				<input type="text" class="post-input" id="posttext" name="post_text" placeholder="What's on your mind, <%=user.getUserName()+"?"%>" required>
				<input type="hidden" id="format" name="format">
				<input type="file" id="postmedia" name="post-media" accept="image/*,video/*" hidden>
				<%-- <input type="file" id="postmedia" name="post-media" accept="video/*" hidden> --%>
				<input type="button" class="post-btn" value="Post" onclick="validateAndSend()">
			</form>
		</div>
		<hr>
				
		<div class="my-post-middle">
			<div class="post-container">
				<img class="media" id="preview-post">
				<video id="preview-video" controls autoplay></video>
			</div>
		</div>
		<div class="my-post-bottom" onclick="uploadPost()">
			<img src="https://cdn-icons-png.flaticon.com/512/1230/1230138.png?w=826&t=st=1673100547~exp=1673101147~hmac=bd5089a2dc31c307a64ee7776ef27100ab0a7d40f1527e51ebda64a8e10faee8">
			<p>Photo/Video</p>
		</div>
	</div>
		<%} %>
	<div class="Titles">
		<%if(hashtag!=null){%>
		<br>
		<h1 class="tags">Posts with <span id="hashtag"><%="#"+hashtag %></span> HashTag</h1>
		<button class="clear-btn" onclick="location.href='user-home.jsp'">Clear</button>
		<%}else{%>
		<h1 class="title" onclick="location.href='user-home.jsp'" data-aos="fade-down">ALL Posts</h1>
		<%}%>
		<div class="Trendings" onclick="location.href='user-home.jsp?page=trendings'">
			<h1 data-aos="fade-down">Trendings</h1>
		</div>
	</div>
	<div class="posts">
			<%List<Post> posts=null;
			if(cur_page.equals("home")){
				if(request.getParameter("hashtag")!=null){
					posts=postdao.getAllUserPosts();
				}else{
					posts= postdao.getAllPosts(user.getUserId());
				}
			}else{
				posts= postdao.getPostsById(profile_id);
			}
			if(request.getParameter("page")!=null&&request.getParameter("page").equals("trendings")){%>	<div>
		<div class="trend-container">
				<%-- <div id="fire">FIRE</div> --%>
			<%
				int trending=1;
				for(Map.Entry<String,Integer> hta:postdao.getTrendingHashTags().entrySet()){
					String ht=hta.getKey();
					int post_count=(int)hta.getValue();%>
			<h1 id="fire" class="ht-trnds" onclick="location.href='user-home.jsp?hashtag=<%=ht.substring(1)%>'">#<%=trending+" "+ht.substring(1)%> <span class="posts-count"><%="("+post_count+")"%></span></h1>
			<%trending++;}%>
		</div>
		<%}else{
			for(Post post:posts){%>
		<%if(hashtag!=null&&!Arrays.asList(postdao.getHashTags(post.getPostId())).contains("#"+hashtag)){
			continue;
		}%>
		<%
			User post_owner=userdao.getUserById(post.getUserId());
			profile_pic=post_owner.getProfilepic()==null?"https://manager.almadarisp.com/user/img/user.png":post_owner.getProfilepic().contains("google")?post_owner.getProfilepic():"data/user_dp/"+post_owner.getUserId()+".jpg";
		%>
		<div class="individual-post" id="postview" data-aos="fade-up" data-aos-anchor-placement="center-bottom"  data-aos-offset="0">
			<div class="my-post-top">
				<div class="my-post-top-left">
					<a href="user-profile.jsp?profileid=<%=post.getUserId()%>">
						<img src=<%=profile_pic%> class="small-user-dp">
					</a>
					<div class="my-post-top-left-near">
						<a href="user-profile.jsp?profileid=<%=post.getUserId()%>">
							<h3><%= post_owner.getUserName() %></h3>
						</a>
						<p><%=LocalServices.getTime(post.getTime())+", "+LocalServices.getMonthYear(post.getTime())%></p>
					</div>
				</div>
				<form action="delete-post" method="post">
					<input type="hidden" name="postid" value=<%= post.getPostId()%>>
					<%if(post.getUserId()==user.getUserId()){%>
					<button class="delete-btn" title="remove post"><i class="fa-sharp fa-solid fa-trash"></i></button>
					<%} %>
				</form>
			</div>
			<div class="my-post-middle" id="postview">
				<div class="post_view_id"><%=post.getPostId()%></div>
				<div class="post-container">
					<%
						String post_media_path=post.getPath();
						if(post.getMedia()!=null&&post.getMedia().equals("image")){
							%>
							<img class="media" src="<%=post_media_path%>" alt="Media no more exists">
					<%}else if(post.getMedia()!=null&&post.getMedia().equals("video")){
						boolean duplicate=post.getPath().substring(post.getPath().length()-4).equals("null");
							%>
					<video class="media" id="video" controls autoplay>
						<source src="<%=post_media_path%>" >
					</video>
					<%}
						Integer []viewers=postdao.getViewers(post.getPostId());
					%>
				</div>
				<div class="cnt" >
					<%
						for(String word:post.getContent().split("\\s")){
							out.print("<p style=\"display:inline;\">");
							if(LocalServices.getHashTags(post.getContent()).contains(word)){
								out.print("<a href=\"user-home.jsp?hashtag="+word.substring(1)+"\"><span style=\"color:#0080ff;\">"+word+" "+"</span></a>");
							}else{
								out.print(word+" ");
							}
							out.print("</p>");
						}
					%>
				</div>
			</div><hr>
			<div class="my-post-bottom">
				<div onclick="this.children[2].style='display:block !important'" class="view_btn" id="view_btn">
					<i class="fa-solid fa-eye" ></i><%=" "+(viewers.length-1)%><p class="views"> view<%=viewers.length-1>1?"s":""%></p>
					<div class="dialogue-box" id="dialog">
						<div class="dialogue-box-top">
							<h2>Viewers</h2>
							<h1 id="close" onclick="closeDialog()">X</h1>
						</div>
						<hr>
						<div class="dialogue-box-middle" >
							<div class="viewers">
								<%for(int i=0;i<viewers.length-1;i++){
									User search_user=userdao.getUserById((int)viewers[i]);
									profile_pic=search_user.getProfilepic()==null?"https://manager.almadarisp.com/user/img/user.png":"data/user_dp/"+search_user.getUserId()+".jpg";%>
								<div class="people">
									<div class="peoples-left">
										<div class="peoples-left-dp">
											<a href="user-profile.jsp?profileid=<%=search_user.getUserId()%>">
												<img class="friend-user-dp" src=<%=profile_pic%>>
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
								</div>
								<%}%>
							</div>
						</div>
					</div>
				</div>
				<%
					String btn_sty="like";
					String btn_text="Like";
					String type="like";
					if(postdao.isLikedByUser(user.getUserId(),post.getPostId())){
						type="unlike";
						btn_text="Unlike";
						btn_sty="unlike";
					}
				%>
				<form action="like-post" method="post">
					<input type="hidden" name="post_id" value=<%= post.getPostId()%>>
					<input type="hidden" name="type" value=<%= type%>>
					<button class="like-btn <%=btn_sty%>" onclick="like(this)" value=<%=post.getPostId()+","+type%> ><%= post.getLikes()%> <i class="fa-solid fa-thumbs-up <%=btn_sty%>"></i><%=btn_text %></button>
					
					<%if(post.getMedia()!=null){%>
					<button class="like-btn" type="button" id="share_btn" onclick="share(this)" value="<%= post.getPostId()%>"><i class="fa-solid fa-share"></i>Share</button>
					<%}%>
				</form>
			</div>
		</div>
		<%}}%>
	</div>
</center>
</body>
<script>
	AOS.init();
	$.get(
		"getpostdetails",
		{userid :<%=user.getUserId()%>,
		},
				function(result) {
					console.log(result);
				});
	// fetchText().then((data) => {
	// 	console.log(data);
    //     // var s="<label for='dept'>Choose a Subject</label><select name='sub_id'>"
    //     // for (let x=0;x<data.length;x++) {
    //     //     s+="<option value='"+data[x][0]+"'>"+data[x][0]+"-"+data[x][1]+"</option>";
    // }

	// async function fetchText() {
    //     let response = await fetch('http://localhost:8080/Zoho_Social/getpostdetails?userid=1');
    //     let data = await response.json();
	// 	return data;
	// }

	const custom_upload_btn=document.getElementById("postmedia");
	function uploadPost(){
		custom_upload_btn.click();
	}
	var val=false;
	const post=document.getElementById("postmedia");
	const preview_container=document.getElementById("preview-post");
	const video_preview_container=document.getElementById("preview-video");
	const format_input=document.getElementById('format');
	
	var image_uploaded=false;
	try{
		post.addEventListener("change",function(){
			const format_input=document.getElementById('format');
			image_uploaded=true;
			const file=this.files[0];
			const fileSize = file.size;
			const sizeInMB = (fileSize / 1024 / 1024).toFixed(2);
			console.log("File size: "+sizeInMB+" MB");
			if(sizeInMB>50){
				alert("Media size should not exceed 50 MB");
				return;
			}
			
			if(file){
				const reader=new FileReader();
				reader.addEventListener("load",function(){
					console.log(format_input.value);
						preview_container.style.display="block";
						preview_container.setAttribute("src",this.result);		
				});
				const format=document.getElementById('postmedia');
				const format_input=document.getElementById('format');
				format_input.value=format.value.substring(format.value.length-3);
				reader.readAsDataURL(file);
			}
		});
	}catch(err){
		val=true;
	}
		post.addEventListener('change', function() {
		if(format_input.value=='mp4'){
			preview_container.style.display="none";
			const file = this.files[0];
			const url = URL.createObjectURL(file);
			video_preview_container.style.display="block";
			video_preview_container.src = url;
			console.log(url);
		}else{
			video_preview_container.style.display="none";
		}
	});
	function validateAndSend() {
		//if (document.getElementById("posttext").value==''||!image_uploaded) {

		//}else{
		setTimeout(function () {
			document.getElementById("postform").submit();
		}, 1000)
		//}
	}


	var video= document.getElementById("video");
	var ind_post=document.getElementById('postview');
	function checkScroll() {
		var fraction = 0.8;

		var x = video.offsetLeft, y = video.offsetTop, w = video.offsetWidth, h = video.offsetHeight, r = x + w, //right
				b = y + h,
				visibleX, visibleY, visible;

		visibleX = Math.max(0, Math.min(w, window.pageXOffset + window.innerWidth - x, r - window.pageXOffset));
		visibleY = Math.max(0, Math.min(h, window.pageYOffset + window.innerHeight - y, b - window.pageYOffset));

		visible = visibleX * visibleY / (w * h);

		if (visible > fraction) {
			video.play();
		} else {
			video.pause();
		}
	}
	window.addEventListener('scroll', checkScroll, false);
	window.addEventListener('resize', checkScroll, false);
	var posts=document.getElementsByClassName('my-post-middle');
	for(i=0;i<posts.length;i++){
		//console.log(posts[i]);
	}
	window.addEventListener('scroll', function() {
		var i=val?0:1;
		for(i;i<posts.length;i++){
			//console.log(posts[i]);
			var position = posts[i].getBoundingClientRect();
			// checking whether fully visible

			if(position.top >= 0 && position.bottom <= window.innerHeight) {
				//console.log('Element'+i+' is fully visible in screen');
				var post_id=posts[i].children[0];
				//console.log(post_id.innerHTML);
				let posid=post_id.innerHTML;
				$.post(
						"view-post",
						{postid :posid}, //meaasge you want to send
						function(result) {
							console.log(result);
						});

			}

		}
	});

	var container=document.getElementsByClassName('my-post-bottom');

	function closeDialog(){
		setTimeout(function () {
			const collection = document.getElementsByClassName("dialogue-box");
			for(i=0;i<collection.length;i++){
				collection[i].style="display:none !important";
			}
		}, 50)

	}
	function closeShareDialog(){
		setTimeout(function () {
			const collection = document.getElementsByClassName("dialogue-box-share");
			for(i=0;i<collection.length;i++){
				collection[i].style="display:none !important";
			}
			closeDialog();
		}, 50)

	}
	var share_post_id=0;
	function share(e){
		document.getElementById('dialog-share').style="display:block !important";
		share_post_id=e.value;
		console.log(e.value);

	}
	function sharepost(e){
		var share_mate=e.value
		console.log("share id="+share_post_id);
		console.log("share mate="+share_mate);
		$.post(
				"sync-to-chat",
				{postid :share_post_id,
					type:"share-post",
					receiverid:share_mate
				}, //meaasge you want to send
				function(result) {
					console.log(result);
				});
				alert('Post shared Successfully');
	}
	function like(e){
		// console.log(e.value);
		// const arr=e.value.split(",");
		// const post_id=arr[0];
		// const type=arr[1];
		// console.log(post_id+"-"+type);
		// $.post(
		// 		"like-post",
		// 		{postid :share_post_id,
		// 			type:type,
		// 			post_id:post_id,
		// 		},
		// 		function(result) {
		// 			console.log(result);
		// 		});
	}
</script>
</html>