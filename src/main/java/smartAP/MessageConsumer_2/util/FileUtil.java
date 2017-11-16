
package smartAP.MessageConsumer_2.util;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * 
 * 
 *         文件操作的相关工具方法
 */
public class FileUtil {

	/**
	 * 获取后辍名
	 * 
	 * @param FileName
	 * @return
	 */
	public static String getExtension(String FileName) {
		return FileName.substring(FileName.lastIndexOf("."));
	}

	/**
	 * 获取后辍名
	 * 
	 * @param FileName
	 * @return
	 */
	public static String getFileName(String Path_FileName) {
		// return Path_FileName.substring(Path_FileName.lastIndexOf("\\"));
		String fileName = Path_FileName.substring(Path_FileName
				.lastIndexOf("/") + 1);
		return fileName.substring(0, fileName.indexOf("."));
	}

	/**
	 * 将InputSteam转换成Byte[]
	 * 
	 * @param is
	 * @return
	 * @throws IOException
	 */
	public static byte[] changeInputStreamToByte(InputStream is)
			throws IOException {
		ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
		int ch;
		while ((ch = is.read()) != -1) {
			bytestream.write(ch);
		}
		byte imgdata[] = bytestream.toByteArray();
		bytestream.close();
		return imgdata;
	}

	/**
	 * 将File转换成Byte[]
	 * 
	 * @param file
	 *            new File("C:\\aaa.txt")
	 * @return
	 * @throws Exception
	 */
	public static byte[] changeFileToBype(File file) throws Exception {
		InputStream is = new FileInputStream(file);
		return changeInputStreamToByte(is);
	}

	/**
	 * 写文件到本地
	 * 
	 * @param in
	 * @param fileName
	 * @param savePath
	 * @return
	 */
	public static boolean copyFile(InputStream in, String fileName,
			String savePath) {
		boolean result = false;
		FileOutputStream fs = null;
		try {
			fs = new FileOutputStream(savePath + fileName);
			byte[] buffer = new byte[1024 * 1024];
			int byteread = 0;
			while ((byteread = in.read(buffer)) != -1) {
				fs.write(buffer, 0, byteread);
				fs.flush();
			}
			fs.close();
			in.close();
			result = true;
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception ex) {

			}
			try {
				if (fs != null) {
					fs.close();
				}
			} catch (Exception ex) {

			}
		}
		return result;
	}

	/**
	 * 从文件获得参数
	 * 
	 * @param fileName
	 *            文件名
	 */
	public static String loadFromFile(String fileName) {
		StringBuilder fileString = new StringBuilder();
		try {
			String path = FileUtil.class.getResource("/").getPath();
			BufferedReader bf = new BufferedReader(new FileReader(new File(path
					+ "/" + fileName)));
			String msg = bf.readLine();
			while (msg != null) {
				fileString.append(msg);
				msg = bf.readLine();
			}
			bf.close();
		} catch (IOException e1) {
			e1.printStackTrace();
			return fileString.toString();
		}
		return fileString.toString();
	}

	/**
	 * 另存为
	 * 
	 * @param bfile
	 * @param filePath
	 * @param fileName
	 */
	public static void saveFile(byte[] bfile, String filePath, String fileName) {
		BufferedOutputStream bos = null;
		FileOutputStream fos = null;
		File file = null;
		try {
			File dir = new File(filePath);
			if (!dir.exists() && dir.isDirectory()) {// 判断文件目录是否存在
				dir.mkdirs();
			}
			file = new File(filePath + "/" + fileName);
			fos = new FileOutputStream(file);
			bos = new BufferedOutputStream(fos);
			bos.write(bfile);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (bos != null) {
				try {
					bos.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	/**
	 * 将数据保存成文件，并指定文件名
	 * 
	 * @Title: creayeFileAndWriteData
	 * @Description: 将数据保存成文件，并指定文件名
	 * @author pengxy
	 * @date 2015年5月15日 下午3:46:03
	 *
	 * @param filename
	 * @param content
	 * @throws IOException
	 */
	public static void createFileAndWriteData(String filename, String content)
			throws IOException {
		File docFile = new File(filename + ".bcp.bak");
		docFile.createNewFile();
		FileOutputStream txtfile = new FileOutputStream(docFile);
		PrintStream p = new PrintStream(txtfile);
		p.println(content);

		txtfile.close();
		docFile.renameTo(new File(filename + ".bcp"));
		p.close();
	}

	/**
	 * 将列表数据保存成文件，并指定文件名
	 * 
	 * @Title: creayeFileAndWriteData
	 * @Description: 将数据保存成文件，并指定文件名
	 * @author pengxy
	 * @date 2015年5月15日 下午3:46:03
	 *
	 * @param filename
	 * @param content
	 * @throws IOException
	 */
	public static void createFileAndWriteData(String filename,
			List<String> content) throws IOException {
		String br = System.getProperty("line.separator");
		StringBuilder sb = new StringBuilder();
		content.forEach(item -> sb.append(item + br));
		createFileAndWriteData(filename, sb.toString());
	}

	/**
	 * 
	 * @Title: createExternalDatFile
	 * @Description: create external table datas ,if the file exists append the
	 *               new datas
	 * @author zhengcx
	 * @date 2017年2月24日 上午11:18:17
	 *
	 * @param fileName
	 * @param content
	 * @throws IOException
	 */
	public static void createExternalDatFile(String fileName,
			List<String> content) throws IOException {
		OutputStream out = null;
		try {
			out = new FileOutputStream(fileName, true);
			File file = new File(fileName);
			if (file.length() > 0)
				out.write("\n".getBytes());
			for (int i = 0; i < content.size(); i++) {
				out.write(content.get(i).getBytes());
				if (i != content.size() - 1
						&& !StringUtil.isNullOrEmpty(content.get(i)))
					out.write("\n".getBytes());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				out.close();
				// in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 依据年月日+随机5位数
	 * 
	 * @return
	 */
	public static String getRandomFileName() {
		SimpleDateFormat simpleDateFormat;
		simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
		Date date = new Date();
		String str = simpleDateFormat.format(date);
		Random random = new Random();
		int rannum = (int) (random.nextDouble() * (99999 - 10000 + 1)) + 10000;// 获取5位随机数
		return str + rannum;// 当前时间
	}

	/**
	 * 
	 * @Title: checkAndCreateFolder
	 * @Description: 检查并创建指定的文件夹
	 * @author pengxy
	 * @date 2015年12月21日 下午3:37:07
	 *
	 * @param folderName
	 * @return
	 */
	public static boolean checkAndCreateFolder(String folderName) {
		File file = new File(folderName);
		// 如果文件夹不存在则创建
		if (!file.exists() && !file.isDirectory()) {
			return file.mkdirs();
		}
		return true;
	}

	/**
	 * 
	 * @Title: moveAndRename
	 * @Description: 同时移动到指定目录
	 * @author pengxy
	 * @date 2017年1月14日 下午4:06:29
	 *
	 * @param from
	 * @param toFolderPath
	 * @return
	 * @throws IOException
	 */
	public static boolean moveAndRename(File from, String toFolderPath) {
		boolean isSuccess = false;
		try {
			String fileName = from.getName();
			File afile = from;
			if (afile.renameTo(new File(toFolderPath + fileName + ".tmp"))) {
				File toFile = new File(toFolderPath + fileName + ".tmp");
				if (toFile.renameTo(new File(toFolderPath + fileName + ".bcp"))) {
					isSuccess = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return isSuccess;
	}
}
