package com.cardlan.out.log;

/**
 * 日志容器
 * @author zhoushenghua
 */
public abstract class BaseLogContainer {

    protected BaseLogClass mBaseLogClass = null;

    /**
     * 构造一个日志类
     */
    public BaseLogContainer() {
        this.mBaseLogClass = new BaseLogClass();
        mBaseLogClass.init(this.getClass());
    }


}
