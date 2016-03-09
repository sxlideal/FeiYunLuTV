package tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.install.apken;

public class zipheader
{
	  long sigfileoffset;
	  long sigfilesize;

	  zipheader(long o, long s)
	  {
	    this.sigfileoffset = o;
	    this.sigfilesize = s;
	  }
	}
