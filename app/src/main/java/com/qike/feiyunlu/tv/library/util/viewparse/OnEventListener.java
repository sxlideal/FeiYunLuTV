package com.qike.feiyunlu.tv.library.util.viewparse;

import java.lang.reflect.Method;

import android.view.View;
import android.widget.AdapterView;

public class OnEventListener implements View.OnClickListener,
		AdapterView.OnItemClickListener {
	private Object obj;
	private Method clickMethod;
	private Method onItemClickMethod;

	public OnEventListener(Object obj) {
		this.obj = obj;
	}

	@Override
	public void onClick(View v) {
		try {
			clickMethod.invoke(obj, v);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void click(String methodName) {
		try {
			clickMethod = obj.getClass().getDeclaredMethod(methodName, View.class);
			clickMethod.setAccessible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> view, View v, int pos, long value) {
		try {
			onItemClickMethod.invoke(obj, view, v, pos, value);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void onItemClick(String methodName) {
		try {
			onItemClickMethod = obj.getClass().getDeclaredMethod(methodName,
					AdapterView.class, View.class, int.class, long.class);
			onItemClickMethod.setAccessible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
