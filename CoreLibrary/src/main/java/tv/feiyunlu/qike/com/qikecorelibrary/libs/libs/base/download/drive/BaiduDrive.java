package tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.download.drive;

import android.text.TextUtils;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URLEncoder;

public class BaiduDrive implements Drive{

	private String fs_id;
	private long uk = -1;
	private long shareid = -1;
	private String vcode;
	private String time;
	private String sign;
	private boolean showVcode;

	private HttpClient client;

	public BaiduDrive(HttpClient client) {
		this.client = client;
	}
	@Override
	public String getDownloadUrl(String url) {
		BufferedReader reader = null;
		HttpGet req = null;
		String result = null;

		try {
			req = new HttpGet(url);
			req.addHeader(
					"User-Agent",
					"Mozilla/5.0 (Linux; U; Android 4.2.2; zh-cn; Galaxy Nexus Build/JDQ39) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30");
			HttpResponse resp = client.execute(req);
			HttpEntity entity = resp.getEntity();
			reader = new BufferedReader(new InputStreamReader(
					entity.getContent(), "UTF-8"));
			String line = null;
			String firstStart = "mpan.viewsingle_param.list=JSON.parse(\"";
			String secondStart = "mpan.viewsingle_param.queryStr=JSON.parse(\"";
			while ((line = reader.readLine()) != null) {
				if (!TextUtils.isEmpty(line.trim())) {
					if (line.contains(firstStart)) {
						int index = line.indexOf(firstStart);
						line = line.substring(index);
						line = line.replace(firstStart, "");
						String source = line;
						time=line.substring( line.indexOf("FileUtils.timestamp="));
						time=time.substring(time.indexOf("FileUtils.timestamp="),time.indexOf(";"));
						String[] times=time.split("=");
						time=times[1].replaceAll("\"", "");
						sign=line.substring( line.indexOf("FileUtils.downloadsign="));
						sign=sign.substring(sign.indexOf("FileUtils.downloadsign="),sign.indexOf(";"));
						String[] signs=sign.split("=");
						sign=signs[1].replaceAll("\"", "");
						line = line.substring(0, line.indexOf("\")"));
						String jsonToken = "\\\"";
						line = line.replace(jsonToken, "\"");
						Log.i("mzw_baidu", "firstLine:" + line);
						JSONArray array = new JSONArray(line);

						fs_id = array.getJSONObject(0).getString("fs_id");
						Log.i("mzw_baidu", "fs_id:" + fs_id);
						
						
						index = source.indexOf("FileUtils.shareid=\"") + "FileUtils.shareid=\"".length();
						
						line = source.substring(index);
						
						index = line.indexOf("\"");
						
						
						line = line.substring(0,index);
						
						shareid = Long.valueOf(line);
						
						
						index = source.indexOf("FileUtils.uk=\"") + "FileUtils.uk=\"".length();
						
						line = source.substring(index);
						
						index = line.indexOf("\"");
						
						
						line = line.substring(0,index);
						
						uk = Long.valueOf(line);
						

//						line = line.substring(0, line.indexOf("\")"));
//						Log.i("mzw_baidu", "secondLine:" + line);
//
//						line = line.replace(jsonToken, "\"");
//						JSONObject jsonObj = new JSONObject(line);

//						uk = jsonObj.getLong("uk");
						
//						shareid = jsonObj.getLong("shareid");

						Log.i("mzw_baidu", "uk:" + uk);
						Log.i("mzw_baidu", "shareid:" + shareid);

					}
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
			if (req != null) {
				try {
					req.abort();
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		}

		if (!TextUtils.isEmpty(fs_id) && shareid != -1 && uk != -1) {
			HttpGet req2 = null;
			try {
				req2 = new HttpGet("http://pan.baidu.com/share/download?uk="+uk+"&shareid="+shareid+"&fid_list="+URLEncoder.encode("[","UTF-8")+fs_id+URLEncoder.encode("]","UTF-8")+"&sign="+sign+"&timestamp="+time+"&r=0.9656054731458426");
				req2.addHeader(
						"User-Agent",
						"Mozilla/5.0 (Linux; U; Android 4.2.2; zh-cn; Galaxy Nexus Build/JDQ39) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30");
				HttpResponse resp = client.execute(req2);
				HttpEntity entity = resp.getEntity();
				reader = new BufferedReader(new InputStreamReader(
						entity.getContent(), "UTF-8"));
				String line = null;
				while ((line = reader.readLine()) != null) {
					if (!TextUtils.isEmpty(line.trim())) {
						Log.i("mzw_baidu", "share line:" + line);
						JSONObject json = new JSONObject(line);
						int errno = json.getInt("errno");
						if (errno != 0) {
							String vcodePath = json.getString("img");
							vcode = json.getString("vcode");
							result = vcodePath;
							showVcode = true;
						} else {
							result = getRealUrl(line);
						}
					}

				}

			} catch (Throwable e) {
				e.printStackTrace();
			} finally {
				if (reader != null) {
					try {
						reader.close();
					} catch (Throwable e) {
						e.printStackTrace();
					}
				}
				if (req2 != null) {
					try {
						req2.abort();
					} catch (Throwable e) {
						e.printStackTrace();
					}
				}
			}
		}

		return result;
	}

	private String getRealUrl(String line) {
		try {
			JSONObject json = new JSONObject(line);
			int errno = json.getInt("errno");
			if (errno == 0) {
				String path = json.getString("dlink");
				return path;
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}
	@Override
	public String verifyVcode(String code) {
		if (!TextUtils.isEmpty(code.trim())) {
			HttpGet req = null;
			BufferedReader reader = null;
			try {
				req = new HttpGet("http://pan.baidu.com/share/download?uk="+uk+"&shareid="+shareid+"&fid_list="+URLEncoder.encode("[","UTF-8")+fs_id+URLEncoder.encode("]","UTF-8")+"&sign="+sign+"&timestamp="+time+"&r=0.9656054731458426&input="+code+"&vcode="+vcode);
				req.addHeader(
						"User-Agent",
						"Mozilla/5.0 (Linux; U; Android 4.2.2; zh-cn; Galaxy Nexus Build/JDQ39) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30");
				HttpResponse resp = client.execute(req);
				HttpEntity entity = resp.getEntity();
				reader = new BufferedReader(new InputStreamReader(
						entity.getContent(), "UTF-8"));
				String line = null;
				while ((line = reader.readLine()) != null) {
					if (!TextUtils.isEmpty(line.trim())) {
						return getRealUrl(line);
					}
				}
			} catch (Throwable e) {
				e.printStackTrace();
			} finally {
				if (reader != null) {
					try {
						reader.close();
					} catch (Throwable e) {
						e.printStackTrace();
					}
				}
				if (req != null) {
					try {
						req.abort();
					} catch (Throwable e) {
						e.printStackTrace();
					}
				}
			}
		}
		return null;
	}

	public boolean isShowVcode() {
		return showVcode;
	}

	public void setShowVcode(boolean showVcode) {
		this.showVcode = showVcode;
	}

}
