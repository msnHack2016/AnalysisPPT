import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.omg.CORBA.PRIVATE_MEMBER;

import com.google.gson.Gson;
import com.hankcs.hanlp.HanLP;


public class QuKeyOld {
	
	private ArrayList<Map<String, String>> suMaps = new ArrayList<>();
	//生成string的类型
	private Map<String, String> str = new HashMap<>();
	//生成content那种格式
	private Map<String, HashMap<String, String>> strHashMap= new HashMap<>();
	
	
	//对一张ppt的内容进行分析，仅用了size
	public BasicJson getPptOne(List<BasicJson> basicJsons) {
		if (null == basicJsons) {
			return null;
		}
		Integer maxSizeIndex = 0,maxSize=0;
		List<XiaoqianJson> basicTempJsons = new ArrayList<>();
		//进行for循环得到包含不同size和color的index
		for (int i = 0; i < basicJsons.size(); i++) {
			if (0 == i) {
				ArrayList<Integer> index = new ArrayList<>();
				index.add(i);
				basicTempJsons.add(new XiaoqianJson(basicJsons.get(0).getSize(), basicJsons.get(0).getColor(),index));
				maxSize = basicJsons.get(0).getSize();
			} else {
				boolean existed = false;
				for (int j = 0; j < basicTempJsons.size(); j++) {
					//判断是否想听的条件
					if ((basicJsons.get(i).getSize() == basicTempJsons.get(j).getSize()) &&
							(basicJsons.get(i).getColor() == basicTempJsons.get(j).getColor())) {
						basicTempJsons.get(j).getIndex().add(i);
						existed = true;
						break;
					}
				}
				if (!existed) {
					ArrayList<Integer> index = new ArrayList<>();
					index.add(i);
					basicTempJsons.add(new XiaoqianJson(basicJsons.get(i).getSize(), basicJsons.get(i).getColor(),index));
					//比较得到最大的size和对应的index
					if (maxSize < basicJsons.get(i).getSize()) {
						maxSize = basicJsons.get(i).getSize();
						maxSizeIndex = basicTempJsons.size()-1;
					}
				}
			}
		}
		List<BasicJson> contentBasicJsons = new ArrayList<>();
		BasicJson finalBasicJson = null;

		//得到的index 进行比对包装
		for (int i = 0; i < basicTempJsons.size(); i++) {
			if (i != maxSizeIndex) {
				ArrayList<Integer> jsonindex = new ArrayList<>();
				jsonindex = basicTempJsons.get(i).getIndex();
				for(Integer finalJ:jsonindex) {
					List<String> keywordList = HanLP.extractKeyword(basicJsons.get(finalJ).getTitle(), 3);
					String title = HanLP.extractKeyword(basicJsons.get(finalJ).getTitle(), 1).get(0);
					basicJsons.get(finalJ).setTitle(title);
					basicJsons.get(finalJ).setKeyword(keywordList);
					contentBasicJsons.add(basicJsons.get(finalJ));
				}
			}
		}
		System.out.println(basicJsons.get(maxSizeIndex).getTitle());
		finalBasicJson = new BasicJson(basicJsons.get(maxSizeIndex).getSize(), basicJsons.get(maxSizeIndex).getColor(), 
				basicJsons.get(maxSizeIndex).getTitle(), basicJsons.get(maxSizeIndex).getKeyword(), contentBasicJsons);
//		System.out.print(finalBasicJson.getSize());
//		System.out.print(finalBasicJson.getContent());
		return finalBasicJson;
	}
	

	//这是所有的ppt进行分析
	public BasicJson getSumContents(List<BasicJson> PptOneJson) {
		//有主标题的
		BasicJson themeJson = null;
		System.out.print(PptOneJson.get(1).getTitle());
		List<BasicJson> secordContents = PptOneJson.get(1).getContent(); //第二页ppt的样式，也就是目录的具体小标题
		System.out.println(secordContents.toString());
		for (int i = 2; i < PptOneJson.size(); i++) {
			for (int j = 0; j < secordContents.size(); j++) {
				System.out.print(secordContents.get(i).getTitle());
				if (PptOneJson.get(i).getTitle() == secordContents.get(j).getTitle()) {
					secordContents.get(j).getContent().add(PptOneJson.get(i));
					break;
				}
			}
		}
		themeJson = new BasicJson(PptOneJson.get(0).getSize(), PptOneJson.get(0).getColor(),
				PptOneJson.get(0).getTitle(), PptOneJson.get(0).getKeyword(),secordContents);
		return themeJson;
	}
	
	//小倩改过
	public final List<BasicJson> read(String filePath) throws IOException { 
		List<BasicJson> sumJsons = new ArrayList<>();
		List<BasicJson> basicJsons = new ArrayList<>();
		BufferedReader br = new BufferedReader(new InputStreamReader(
        new FileInputStream(filePath)));
        for (String line = br.readLine(); line != null; line = br.readLine()) {
//        	System.out.println(line);
        	if(line.equals("The page end")){
        		sumJsons.add(getPptOne(basicJsons));
        		br.readLine();
        		basicJsons.clear();
        		continue;
        	}
            String[] onText = line.split("wb!!!");
            Integer size=0,color=0;
            try {
            	if ("".equals(onText[1])) {
            		size= Integer.valueOf(onText[1]);
                    color = Integer.valueOf(onText[2]);
				}
            	
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
            
            List<String> keyword = new ArrayList<>();
				try {
					if ("" != onText[0]) {
						basicJsons.add(new BasicJson(size,color, onText[0], keyword));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
        }
        br.close();
//        System.out.println(basicJsons.toString());
        return sumJsons;
    }
	
	//将baseJson实体类转换成Json
	public String entityToJson(BasicJson themeJson,Gson gson) {
//		Gson gson = new Gson();
		String jsonStr = "["+gson.toJson(themeJson)+"]";
		System.out.print(jsonStr);
		return jsonStr;
	}
	
	 /** 
	   * 传入文件名以及字符串, 将字符串信息保存到文件中 
	   * @param strFilename 
	   * @param strBuffer 
	   */  
	  public void TextToFile(final String strFilename, final String strBuffer)  
	  {  
	    try  
	    {
	      // 创建文件对象  
	      File fileText = new File(strFilename);
	      // 向文件写入对象写入信息 
	      FileWriter fileWriter = new FileWriter(fileText);  
	  
	      // 写文件        
	      fileWriter.write(strBuffer);  
	      // 关闭  
	      fileWriter.close();  
	    }  
	    catch (IOException e)  
	    {  
	      //  
	      e.printStackTrace();  
	    }  
	  }  
	
	//执行C#文件
	public void Cexe(String path) {
		String[] cmd = {"D:\\outputxmind.exe",path};
		try {
			Runtime.getRuntime().exec(cmd);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		QuKeyOld quKeyOld = new QuKeyOld();
		quKeyOld.entry("D:\\wiyuan.txt");
		quKeyOld.Cexe("D:\\wiyuan.txt");
	}
	
	public void entry(String path){
		Gson gson = new Gson();
		List<BasicJson> PptJson = new ArrayList<>();
		int index = path.lastIndexOf('.');
		String newPath = path.substring(0, index)+"2" + path.substring(index,path.length());
		try {
			List<BasicJson> queryJsons = read(path);
			//每页的ppt生成总的
			System.out.print(queryJsons.size());
			BasicJson textJson = getSumContents(queryJsons);
			String jsonStr = entityToJson(textJson,gson);
			TextToFile(newPath, jsonStr);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
