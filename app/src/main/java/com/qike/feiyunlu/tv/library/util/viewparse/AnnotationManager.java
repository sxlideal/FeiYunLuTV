package com.qike.feiyunlu.tv.library.util.viewparse;

import android.util.SparseArray;

public class AnnotationManager {

	public static final int VIEW = -1;
	public static final int BUNDLE = -2;
	public static final int INJECT = -3;
	private static final SparseArray<AnnotationParser> PARSER = new SparseArray<AnnotationParser>();

	static {
		PARSER.put(VIEW, new AnnotationViewParser());
		PARSER.put(INJECT, new AnnotationInjectParser());
	}

	public static void parse(Object obj) {
		try {
			AnnotationParser parser = getParser(INJECT);
			parser.parse(obj);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public static AnnotationParser getParser(int type) {
		return PARSER.get(type);
	}
}
