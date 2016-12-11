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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.omg.CORBA.PRIVATE_MEMBER;

import com.google.gson.Gson;
import com.hankcs.hanlp.HanLP;


public class QuKey {
	
	private ArrayList<Map<String, String>> suMaps = new ArrayList<>();
	//生成string的类型
	private Map<String, String> str = new HashMap<>();
	//生成content那种格式
	private Map<String, HashMap<String, String>> strHashMap= new HashMap<>();
	
	
	//对一张ppt的内容进行分析，仅用了size和color
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

		//size排序算法,从大到小排序
		Collections.sort(basicTempJsons);
		Gson gson = new Gson();
		System.out.println(gson.toJson(basicTempJsons));
		
		//得到的index 进行比对包装
		if (basicTempJsons.size() ==1 ) {
			finalBasicJson = basicJsons.get(0);
			return finalBasicJson;
		}
		if (basicTempJsons.size() == 2) {
			ArrayList<Integer> jsonindex = new ArrayList<>();
			jsonindex = basicTempJsons.get(1).getIndex();
			for(Integer finalJ:jsonindex) {
				System.out.println(finalJ);
				System.out.println(basicJsons.get(finalJ).getSize());
				contentBasicJsons.add(basicJsons.get(finalJ));
			}
		} else {
			for (int i = basicTempJsons.get(1).getIndex().size()-1,length = basicJsons.size(); i > 0; i--) {
				List<BasicJson> tempBasicIndex = new ArrayList<>();
				for (int j = basicTempJsons.get(1).getIndex().get(i)+1; j < length; j++) {
					List<String> keywordList = HanLP.extractKeyword(basicJsons.get(j).getTitle(), 3);
					String title = HanLP.extractKeyword(basicJsons.get(j).getTitle(), 1).get(0);
					tempBasicIndex.add(new BasicJson(basicJsons.get(j).getSize(), basicJsons.get(j).getColor(), title, keywordList));
				}
				length = basicTempJsons.get(1).getIndex().get(i);
				List<String> keyword = new ArrayList<>();
				contentBasicJsons.add(new BasicJson(basicJsons.get(length).getSize(), basicJsons.get(length).getColor(), 
						basicJsons.get(length).getTitle(), keyword,tempBasicIndex));
			}
		}
		finalBasicJson = new BasicJson(basicJsons.get(maxSizeIndex).getSize(), basicJsons.get(maxSizeIndex).getColor(), 
				basicJsons.get(maxSizeIndex).getTitle(), basicJsons.get(maxSizeIndex).getKeyword(), contentBasicJsons);
		return finalBasicJson;
	}
	
	//这是所有的ppt进行分析
	public BasicJson getSumContents(List<BasicJson> PptOneJson) {
		//有主标题的
		BasicJson themeJson = null;
		List<BasicJson> secordContents = PptOneJson.get(1).getContent(); //第二页ppt的样式，也就是目录的具体小标题
		for (int i = 2; i < PptOneJson.size(); i++) {
			for (int j = 0; j < secordContents.size(); j++) {
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
        	System.out.println(line);
        	if(line.equals("The page end")){
        		sumJsons.add(getPptOne(basicJsons));
        		br.readLine();
        		basicJsons.clear();
        		continue;
        	}
            String[] onText = line.split("wb!!!");
            Integer size = Integer.valueOf(onText[1]);
            Integer color = Integer.valueOf(onText[2]);
            List<String> keyword = new ArrayList<>();
				try {
					basicJsons.add(new BasicJson(size,color, onText[0], keyword));
				} catch (Exception e) {
					e.printStackTrace();
				}
        }
        br.close();
        System.out.println(basicJsons.toString());
        return sumJsons;
    }
	
	//将baseJson实体类转换成Json
	public String entityToJson(BasicJson themeJson) {
		Gson gson = new Gson();
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
	public void Cexe() {
		String[] cmd = {"D:\\outputxmind.exe","D:\\zr2.txt"};
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
		// TODO Auto-generated method stub
		QuKey quKey = new QuKey();
		String path = "D:\\zr.txt";
		List<BasicJson> PptJson = new ArrayList<>();
		try {
			List<BasicJson> queryJsons = quKey.read(path);
			//每页的ppt生成总的
			BasicJson textJson = quKey.getSumContents(queryJsons);
			String jsonStr = quKey.entityToJson(textJson);
			quKey.TextToFile("D:\\zr2.txt", jsonStr);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		quKey.Cexe();
	}

}
