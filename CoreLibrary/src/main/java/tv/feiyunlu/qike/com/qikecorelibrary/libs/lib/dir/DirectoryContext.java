package tv.feiyunlu.qike.com.qikecorelibrary.libs.lib.dir;

import java.util.ArrayList;
import java.util.List;

public abstract class DirectoryContext
{

    protected Directory mBaseDirectory;

    public Directory getBaseDirectory()
    {
        return mBaseDirectory;
    }

    public DirectoryContext(String basePath)
    {
        Directory dir = new Directory ();
        dir.setName (basePath);
        dir.setType (DirType.APP_BASE);
        this.mBaseDirectory = dir;
        List<Directory> children = initDirectories ();
        if (children != null && children.size () > 0) dir.addChildren (children);
    }

    protected abstract List<Directory> initDirectories();

    protected Directory AddChild(ArrayList<Directory> children,DirType type,String name)
    {
        Directory child = new Directory ();
        child.setType (type);
        child.setName (name);
        children.add (child);
        return child;

    }
}
