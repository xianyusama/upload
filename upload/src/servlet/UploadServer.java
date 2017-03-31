package servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.alibaba.fastjson.JSON;

import model.FileData;
import model.ResponseData;

/*
 *      将请求消息实体中的每一个项目封装成单独的DiskFileItem (FileItem接口的实现) 对象的任务
 *由 org.apache.commons.fileupload.FileItemFactory 接口的默认实现 
 *org.apache.commons.fileupload.disk.DiskFileItemFactory 来完成。
 *当上传的文件项目比较小时，直接保存在内存中（速度比较快），比较大时，以临时文件的形式，保存在磁盘临时文件夹（虽然速度慢些，但是内存资源是有限的）。
 */
@WebServlet("/UploadServer")
public class UploadServer extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public UploadServer() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		System.out.println("-------------------------开始-----------------------------");
		DiskFileItemFactory factory = new DiskFileItemFactory();
		// 得到绝对文件夹路径，比如("D://tomcat6//webapps//test//upload")
		String path = req.getSession().getServletContext().getRealPath("/upload");
		System.out.println("绝对路径" + path);
		// 临时文件夹路径
		String repositoryPath = req.getSession().getServletContext().getRealPath("/upload/temp");
		System.out.println("相对路径" + repositoryPath);
		// 设定临时文件夹为repositoryPath
		factory.setRepository(new File(repositoryPath));
		// 设定上传文件的值，如果上传文件大于1M，就可能在repository
		// 所代表的文件夹中产生临时文件，否则直接在内存中进行处理
		factory.setSizeThreshold(1024 * 1024);

		// 创建一个ServletFileUpload对象
		ServletFileUpload uploader = new ServletFileUpload(factory);
		List<FileData> fileDatas = new ArrayList<FileData> ();
		ResponseData responseData= new ResponseData();
		try {

			/**
			 * 调用uploader中的parseRequest方法，可以获得请求中的相关内容， 即一个FileItem类型的ArrayList。
			 * FileItem是指org.apache.commons.fileupload中定义的，他可以代表一个文件，
			 * 也可以代表一个普通的formfield
			 */

			ArrayList<FileItem> list = (ArrayList<FileItem>) uploader.parseRequest(req);
			System.out.println(list.size());
			for (FileItem fileItem : list) {
				if (fileItem.isFormField()) {// 如果是普通的formfield
					String name = fileItem.getFieldName();
					String value = fileItem.getString();
					System.out.println(name + ":" + value);
				} else {// 如果是文件
					String value = fileItem.getName();
					// value.lastIndexOf("\\")返回“\\”最后出现的位置下标
					int start = value.lastIndexOf("\\");
					// substring 截取字符串方式之一
					String fileName = value.substring(start + 1);
					// 将其中包含的内容写到path()下，即upload目录下，名为fileName的文件中
					fileItem.write(new File(path, fileName));
					FileData fileData= new FileData();
					fileData.setCreateTime(String.valueOf(System.currentTimeMillis()));
					fileData.setName(fileName);
					fileData.setServerPath("http://192.168.1.146:8080/upload/upload/"+fileName);
					fileDatas.add(fileData);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 向客户端返回结果
		PrintWriter out = resp.getWriter();
		responseData.setFileDatas(fileDatas);
		String response=JSON.toJSONString(responseData);
		out.println(response);
		out.flush();
		out.close();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
