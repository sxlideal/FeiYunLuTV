package tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.install;

import java.util.ArrayList;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;



@DatabaseTable
public class Datapackage {
	
	
	@DatabaseField(id=true)
	private String packageName;
	@DatabaseField(dataType=DataType.SERIALIZABLE)
	private ArrayList<String> paths = new ArrayList<String>();
	@DatabaseField
	private long size;
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	public ArrayList<String> getPaths() {
		return paths;
	}
	public void setPaths(ArrayList<String> paths) {
		this.paths = paths;
	}
	public long getSize() {
		return size;
	}
	public void setSize(long size) {
		this.size = size;
	}
	
	
	
}
