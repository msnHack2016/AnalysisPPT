import java.util.List;

import com.hankcs.hanlp.HanLP;


public class KeyWord {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String content = "转到文件另存为，然后选择 OneDrive或 SharePoint位置。";
		List<String> keywordList = HanLP.extractKeyword(content, 5);
		System.out.println(keywordList);
	}
}
