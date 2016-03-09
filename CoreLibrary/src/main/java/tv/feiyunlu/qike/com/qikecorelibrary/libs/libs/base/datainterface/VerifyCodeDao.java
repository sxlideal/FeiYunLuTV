package tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.datainterface;

import java.util.Map;

import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.datainterface.impl.BasicGetDao;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.function.configuration.Params;


public class VerifyCodeDao {
	public static final String CONTROLLER_CHECKCEODE = "getcheckcode";
	public static final String UCENTER_URL = Params.BASE_USER_URL;
//	private String controlType;
	private Map<String, Object> paramers;
	private BaseLoadListener listener;

	public VerifyCodeDao(BaseLoadListener listener, Map<String, Object> paramers) {
		super();
		this.listener = listener;
		this.paramers = paramers;
	}

	public void start() {
		final BasicGetDao<CheckCodeBean> dao = new BasicGetDao<CheckCodeBean>(UCENTER_URL, CheckCodeBean.class);
		dao.putAllParams(paramers);
		dao.registerListener(listener);
		dao.asyncDoAPI();

	}

	class CheckCodeBean {
		private int code;
		private String msg;

		public int getCode() {
			return code;
		}

		public void setCode(int code) {
			this.code = code;
		}

		public String getMsg() {
			return msg;
		}

		public void setMsg(String msg) {
			this.msg = msg;
		}
	}
}
