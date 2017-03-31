package servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/*
 *      ��������Ϣʵ���е�ÿһ����Ŀ��װ�ɵ�����DiskFileItem (FileItem�ӿڵ�ʵ��) ���������
 *�� org.apache.commons.fileupload.FileItemFactory �ӿڵ�Ĭ��ʵ�� 
 *org.apache.commons.fileupload.disk.DiskFileItemFactory ����ɡ�
 *���ϴ����ļ���Ŀ�Ƚ�Сʱ��ֱ�ӱ������ڴ��У��ٶȱȽϿ죩���Ƚϴ�ʱ������ʱ�ļ�����ʽ�������ڴ�����ʱ�ļ��У���Ȼ�ٶ���Щ�������ڴ���Դ�����޵ģ���
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
		System.out.println("-------------------------��ʼ-----------------------------");
		DiskFileItemFactory factory = new DiskFileItemFactory();
		// �õ������ļ���·��������("D://tomcat6//webapps//test//upload")
		String path = req.getSession().getServletContext().getRealPath("/upload");
		System.out.println("����·��" + path);
		// ��ʱ�ļ���·��
		String repositoryPath = req.getSession().getServletContext().getRealPath("/upload/temp");
		System.out.println("���·��" + repositoryPath);
		// �趨��ʱ�ļ���ΪrepositoryPath
		factory.setRepository(new File(repositoryPath));
		// �趨�ϴ��ļ���ֵ������ϴ��ļ�����1M���Ϳ�����repository
		// ��������ļ����в�����ʱ�ļ�������ֱ�����ڴ��н��д���
		factory.setSizeThreshold(1024 * 1024);

		// ����һ��ServletFileUpload����
		ServletFileUpload uploader = new ServletFileUpload(factory);
		try {

			/**
			 * ����uploader�е�parseRequest���������Ի�������е�������ݣ� ��һ��FileItem���͵�ArrayList��
			 * FileItem��ָorg.apache.commons.fileupload�ж���ģ������Դ���һ���ļ���
			 * Ҳ���Դ���һ����ͨ��formfield
			 */
			ArrayList<FileItem> list = (ArrayList<FileItem>) uploader.parseRequest(req);
			System.out.println(list.size());
			for (FileItem fileItem : list) {
				if (fileItem.isFormField()) {// �������ͨ��formfield
					String name = fileItem.getFieldName();
					String value = fileItem.getString();
					System.out.println(name + ":" + value);
				} else {// ������ļ�
					String value = fileItem.getName();
					// value.lastIndexOf("\\")���ء�\\�������ֵ�λ���±�
					int start = value.lastIndexOf("\\");
					// substring ��ȡ�ַ�����ʽ֮һ
					String fileName = value.substring(start + 1);
					// �����а���������д��path()�£���uploadĿ¼�£���ΪfileName���ļ���
					fileItem.write(new File(path, fileName));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// ��ͻ��˷��ؽ��
		PrintWriter out = resp.getWriter();
		out.println("ok");
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
