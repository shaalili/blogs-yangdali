package cn.yangdali.common.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Calendar;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import cn.yangdali.dto.ResultVO;
import cn.yangdali.dto.UploadFileVO;

@Controller
public class UploadFileController {

	/**
	 * 上传文件
	 *
	 * @param file
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
	@ResponseBody
	public ResultVO<UploadFileVO> uploadFile(MultipartFile file) {

		// 本地使用,上传位置(80端口服务器)
		String rootPath_80 = "/tomcat/80/apache-tomcat-8.5.4/webapps/ROOT/uploads/";

		// 文件的完整名称,如spring.jpeg
		String filename = file.getOriginalFilename();
		// 文件名,如spring
		String name = filename.substring(0, filename.indexOf("."));
		// 文件后缀,如.jpeg
		String suffix = filename.substring(filename.lastIndexOf("."));

		// 创建年月文件夹
		Calendar date = Calendar.getInstance();
		File dateDirs = new File(date.get(Calendar.YEAR) + File.separator + (date.get(Calendar.MONTH) + 1));

		// 目标文件
		File descFile_80 = new File(rootPath_80 + File.separator + dateDirs + File.separator + filename);
		int i = 1;
		// 若文件存在重命名
		String newFilename = filename;
		while (descFile_80.exists()) {
			newFilename = name + "(" + i + ")" + suffix;
			String parentPath_80 = descFile_80.getParent();
			descFile_80 = new File(parentPath_80 + File.separator + newFilename);
			i++;
		}
		// 判断目标文件所在的目录是否存在
		if (!descFile_80.getParentFile().exists()) {
			// 如果目标文件所在的目录不存在，则创建父目录
			descFile_80.getParentFile().mkdirs();
		}

		// 将内存中的数据写入磁盘
		try {
			file.transferTo(descFile_80);
			Files.copy(descFile_80.toPath(), descFile_80.toPath());
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 完整的url
		String fileUrl = "/uploads/" + dateDirs + "/" + newFilename;

		ResultVO<UploadFileVO> resultVO = new ResultVO<>();
		resultVO.setCode(0);
		resultVO.setMsg("成功");

		UploadFileVO uploadFileVO = new UploadFileVO();
		uploadFileVO.setTitle(filename);
		uploadFileVO.setSrc(fileUrl);
		resultVO.setData(uploadFileVO);
		return resultVO;
	}
}
