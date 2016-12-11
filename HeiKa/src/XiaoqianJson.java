import java.util.ArrayList;


public class XiaoqianJson implements Comparable<XiaoqianJson>{
	
	private Integer size;
	private Integer color;
	private ArrayList<Integer> index;
	
	public XiaoqianJson(Integer size, Integer color, ArrayList<Integer> index) {
		this.size = size;
		this.color = color;
		this.index = index;
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
	public ArrayList<Integer> getIndex() {
		return index;
	}
	public void setIndex(ArrayList<Integer> index) {
		this.index = index;
	}
	
	@Override  
    public int compareTo(XiaoqianJson o) {  
        int i = this.getSize() - o.getSize();//先按照年龄排序  
//        if(i == 0){  
//            return this.score - o.getScore();//如果年龄相等了再用分数进行排序  
//        }  
        return i;  
    }  
}
