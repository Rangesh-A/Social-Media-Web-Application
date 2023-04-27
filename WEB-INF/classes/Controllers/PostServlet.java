package Controllers;

import java.io.IOException;
import java.io.PrintWriter;

import Dao.PostDao;
import Database.DbConnection;
import Model.Post;
import Model.User;
import java.io.File;
import Services.LocalServices;
import jakarta.servlet.*;
import jakarta.servlet.annotation.*;
import jakarta.servlet.http.*;

@MultipartConfig
@WebServlet("/post")
public class PostServlet extends HttpServlet {
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		PrintWriter out = response.getWriter();
		response.setContentType("text/html");
		Post post = new Post();
		Part post_media = request.getPart("post-media");
		post.setContent(request.getParameter("post_text"));
		post.setTime(LocalServices.getCurrentDateTime());
		post.setLikes(0);
		post.setUserId(((User) request.getSession().getAttribute("auth")).getUserId());

		String hashtags[] = new String[LocalServices.getHashTags(post.getContent()).size()];
		int i = 0;
		for (String hashtag : LocalServices.getHashTags(post.getContent())) {
			hashtags[i] = hashtag;
			i++;
		}
		try {
			PostDao postDao = new PostDao(DbConnection.getConnection());
			if (post_media.getSize() > 0) {
				File file = new File(
						"C:\\Program Files\\Apache Software Foundation\\Tomcat 10.0\\webapps\\Zoho_Social\\data\\user_posts");
				if (!file.exists()) {
					file.mkdirs();
				}
				String format = request.getParameter("format");
				if (format.equals("mp4")) {
					post.setMedia("video");
				} else {
					post.setMedia("image");
				}
				String hashvalue=LocalServices.getHashValueofMedia(post_media);
				int resource_id=postDao.getResourceIdByHash(hashvalue);
				int post_id=0;
				String path="";
				if(resource_id==0){
					resource_id=postDao.insertResource(hashvalue);
					path="C:\\Program Files\\Apache Software Foundation\\Tomcat 10.0\\webapps\\Zoho_Social\\data\\user_posts\\"
								+ resource_id + "." + format;
					postDao.updatePath(resource_id,path);
					post_media.write(path);
				}
				post_id = postDao.insertPost(post, hashtags,resource_id);
				if (post.getContent().contains("#")) {
					postDao.insertHashtags(post_id, LocalServices.getHashTags(post.getContent()));
				}	
			} else {
				int post_id = postDao.insertPost(post, hashtags,0);
				if (post.getContent().contains("#")) {
					postDao.insertHashtags(post_id, LocalServices.getHashTags(post.getContent()));
				}
			}
			out.print("<script>window.location = document.referrer;</script>");
		} catch (Exception e) {
			out.print(e);
			e.printStackTrace();
		}
	}
}
