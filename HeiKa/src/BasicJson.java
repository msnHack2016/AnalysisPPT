import java.util.List;


public class BasicJson {

	private Integer size;
	private Integer color;
	private String title;
	private List<String> keyword;
	private List<BasicJson> content;
	
	public BasicJson(Integer size, Integer color, String title, List<String> keyword) {
		this.size = size;
		this.color = color;
		this.title = title;
		this.keyword = keyword;
	}
	
	public BasicJson(Integer size, Integer color, String title, List<String> keyword, List<BasicJson> content) {
		this.size = size;
		this.color = color;
		this.title = title;
		this.keyword = keyword;
		this.content = content;
	}
	
	public Integer getSize() {
		return size;
	}
	public void setSize(Integer size) {
		this.size = size;
	}
	public Integer getColor() {
		return color;
	}
	public void setColor(Integer color) {
		this.color = color;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}

	public List<String> getKeyword() {
		return keyword;
	}

	public void setKeyword(List<String> keyword) {
		this.keyword = keyword;
	}

	public List<BasicJson> getContent() {
		return content;
	}

	public void setContent(List<BasicJson> content) {
		this.content = content;
	}
}
