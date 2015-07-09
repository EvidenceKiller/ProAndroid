package com.zxn.contentprovider;

import android.content.Context;

/**
 * Created by Administrator on 2015/7/9.
 */
public class BaseTester {

    protected IReportBack mReportTo;
    protected Context mContext;
    public BaseTester(Context ctx, IReportBack target) {
        mReportTo = target;
        mContext = ctx;
    }

}
