package com.qike.feiyunlu.tv.library.util.viewparse;

import android.content.Context;
import android.view.View;

public interface ViewInjectable {
	public View findView(int id);
	public Context getContext();
}
