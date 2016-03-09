package tv.feiyunlu.qike.com.qikecorelibrary.libs.lib.dir;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MzwDirectoryContext extends DirectoryContext
{

    public MzwDirectoryContext(String dir)
    {
        super (dir);
    }

    @Override
    protected List<Directory> initDirectories()
    {
        ArrayList<Directory> children = new ArrayList<Directory> ();

        Directory cache = AddChild (children, DirType.CACHE, "android" + File.separator + "data" + File.separator + "com.muzhiwan.lib" + File.separator + "cache");
        cache.addChild (DirType.DATA, "xml");
        cache.addChild (DirType.IMAGE, "image");
        cache.addChild (DirType.LOG, "log");
        cache.addChild(DirType.STORE,"store");
        Directory resource = AddChild (children, DirType.RESURCE, "MzwBackUp");

        resource.addChild (DirType.LOCAL, "Local");
        resource.addChild (DirType.DOWNLOAD, "DOWNLOAD");
        resource.addChild (DirType.USER, "USER");
        return children;
    }
}
