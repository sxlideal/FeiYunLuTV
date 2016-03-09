package tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.http;

import org.apache.http.entity.mime.content.FileBody;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class FileBodyKownSizeBody extends FileBody {
	private File file;
	private int sentBytes = 0;
	private final float PERCENT = 0.02f;
	private Request.UploadFileListener mListener;
	
	public void setUploadFileListener(Request.UploadFileListener listener){
		mListener = listener;
	}

	public FileBodyKownSizeBody(File file, String filename, String mimeType,
			String charset) {
		super(file, filename, mimeType, charset);
		this.file = file;
	}

	public FileBodyKownSizeBody(final File file, final String mimeType,
			final String fileName) {
		super(file, fileName, mimeType, null);
		this.file = file;
	}

	public FileBodyKownSizeBody(final File file, final String mimeType) {
		super(file, mimeType, null);
		this.file = file;
	}

	public FileBodyKownSizeBody(final File file) {
		super(file, "application/octet-stream");
		this.file = file;
	}

	@Override
	public void writeTo(OutputStream out) throws IOException {
		if (out == null) {
			throw new IllegalArgumentException("Output stream may not be null");
		}
		InputStream in = new FileInputStream(file);
		try {
			byte[] tmp = new byte[4096];
			int l;
			float progress = 0;
			float pprogress = 0;
			while ((l = in.read(tmp)) != -1) {
				out.write(tmp, 0, l);
				sentBytes += l;
				sendMessageByPercent(progress, pprogress);
				// sendMessageByTime();
			}
			out.flush();
		} finally {
			in.close();
		}
	}

	private void sendMessageByPercent(float progress, float pprogress) {
		progress = sentBytes / (float) getContentLength();

		if (progress - pprogress >= PERCENT) {
			pprogress = progress;
		}
		
		mListener.onProgress((long)progress, getContentLength());
	}
}
