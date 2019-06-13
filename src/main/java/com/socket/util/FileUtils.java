package com.socket.util;

import net.sf.json.JSONArray;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.*;

@Component
public class FileUtils {
	@Value("${filePath}")
	private String filePath;

	/**
	 * 图片上传(服务器)
	 */
	public static String uploadFileToService(MultipartFile file, HttpServletRequest request, int type) {
		String saveName= DateUtil.now() + DataUtil.createNums(7);
		String name = file.getOriginalFilename();
		saveName = saveName+name.substring(name.lastIndexOf("."));
		String filePath=null;
		if(type==1){
			filePath=filePath+"//img";
		}else{
			filePath=filePath+"//file";
		}
		File dirPath = new File(filePath);
		if (!dirPath.exists()) {
			dirPath.mkdirs();
		}
		// 创建文件
		File uploadFile = new File(filePath+"/"+saveName);
		OutputStream outputStream = null;
		InputStream inputStream=null;
		try {
			outputStream = new FileOutputStream(uploadFile);
			inputStream = file.getInputStream();
			FileCopyUtils.copy(inputStream, outputStream);
			return filePath + saveName;
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException e) {
				}
			}
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
				}
			}
		}
		return null;
	}
	/**
	 * 生成.json格式文件
	 */
	public static boolean createJsonFile(JSONArray jsonString, String filePath, String fileName) {
		// 标记文件生成是否成功
		boolean flag = true;
		// 拼接文件完整路径
		String fullPath = filePath + File.separator + fileName + ".json";
		// 生成json格式文件
		try {
			// 保证创建一个新文件
			File file = new File(fullPath);
			// 如果父目录不存在，创建父目录
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			// 如果已存在,删除旧文件
			if (file.exists()) {
				file.delete();
			}
			file.createNewFile();
			// 将格式化后的字符串写入文件
			Writer write = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
			write.write(jsonString.toString());
			write.flush();
			write.close();
		} catch (Exception e) {
			flag = false;
			e.printStackTrace();
		}
		// 返回是否成功的标记
		return flag;
	}
}
