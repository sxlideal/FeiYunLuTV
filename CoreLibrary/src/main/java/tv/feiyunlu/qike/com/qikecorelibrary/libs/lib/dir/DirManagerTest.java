package tv.feiyunlu.qike.com.qikecorelibrary.libs.lib.dir;

public class DirManagerTest {

	public static void main(String[] args) {
		DirectoryManager.init(new MzwDirectoryContext("c://aaaa"));
		
		
		
//		System.out.println(directoryManager.getDirPath(DirType.LOCAL));
		System.out.println(DirectoryManager.getInstance().getDirPath(DirType.IMAGE));
		
	}

}
