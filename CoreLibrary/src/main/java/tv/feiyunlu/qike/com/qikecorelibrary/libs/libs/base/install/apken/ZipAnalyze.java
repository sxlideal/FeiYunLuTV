package tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.install.apken;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Vector;

public class ZipAnalyze
{
  File zipfile;
  Vector<zipheader> filelist = new Vector();
  int filecount;

  ZipAnalyze(File zipfile)
  {
    this.zipfile = zipfile;
  }

  void Analyze() {
    try {
      RandomAccessFile rw = new RandomAccessFile(this.zipfile, "r");
      long zipfilesize = rw.length();

      byte[] temp1 = new byte[4];
      rw.seek(zipfilesize - 6L);
      rw.read(temp1);
      long headeroffset = FormatConversion.bytes2intD(temp1);

      rw.seek(zipfilesize - 12L);
      byte[] temp2 = new byte[2];
      rw.read(temp2);
      this.filecount = FormatConversion.Tbytes2intD(temp2);
      rw.seek(headeroffset);

      byte[] mark = { 80, 75, 1, 2 };

      for (int i = 0; i < this.filecount; i++) {
        byte[] tempb = new byte[1];
        int markid = 0;

        while (rw.read(tempb) > 0) {
          if (tempb[0] == mark[markid]) {
            markid++;

            if (markid == 4)
              break;
          }
          else {
            markid = 0;
          }
        }

        long currect = rw.getFilePointer();
        rw.seek(currect + 16L);
        byte[] tempfilesizeb = new byte[4];
        rw.read(tempfilesizeb);
        long tempfilesize = FormatConversion.bytes2intD(tempfilesizeb);
        this.filelist.add(new zipheader(currect - 4L, tempfilesize));
      }

      rw.close();
    }
    catch (FileNotFoundException e)
    {
      e.printStackTrace();
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }

  void alterzip(byte[] m1) {
    try {
      RandomAccessFile rw = new RandomAccessFile(this.zipfile, "rw");
      byte[] mark = { 80, 75, 3, 4 };

      for (int i = 0; i < this.filecount; i++) {
        byte[] tempb = new byte[1];
        int markid = 0;

        while (rw.read(tempb) > 0) {
          if (tempb[0] == mark[markid]) {
            markid++;

            if (markid == 4)
              break;
          }
          else {
            markid = 0;
          }
        }

        long currect = rw.getFilePointer();
        rw.seek(currect + 2L);
        rw.write(m1);
        rw.seek(((zipheader)this.filelist.get(i)).sigfileoffset + 8L);
        rw.write(m1);
        rw.seek(currect + 4L + ((zipheader)this.filelist.get(i)).sigfilesize);
      }

      rw.close();
    }
    catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }

  void printfilelist()
  {
    for (int i = 0; i < this.filelist.size(); i++)
      System.out.println("文件头偏移地址：\t" + ((zipheader)this.filelist.get(i)).sigfileoffset + "\t\t文件大小：\t" + ((zipheader)this.filelist.get(i)).sigfilesize);
  }
}