package com.nepxion.aquarius.lock.aop;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @email 1394997@qq.com
 * @version 1.0
 */

import java.lang.annotation.Annotation;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.nepxion.aquarius.common.context.AquariusContextAware;
import com.nepxion.aquarius.lock.annotation.Lock;
import com.nepxion.aquarius.lock.annotation.ReadLock;
import com.nepxion.aquarius.lock.annotation.WriteLock;
import com.nepxion.aquarius.lock.constant.LockConstant;
import com.nepxion.aquarius.lock.delegate.LockDelegate;
import com.nepxion.matrix.aop.DefaultAutoScanProxy;
import com.nepxion.matrix.mode.ProxyMode;
import com.nepxion.matrix.mode.ScanMode;

// 通过全局拦截器实现对类头部注解的扫描和代理
@Component("lockAutoScanProxy")
public class LockAutoScanProxy extends DefaultAutoScanProxy {
    private static final long serialVersionUID = -6456216398492047529L;

    private static final String[] SCAN_PACKAGES = { "com.nepxion.aquarius.lock" };

    @SuppressWarnings("rawtypes")
    private Class[] commonInterceptorClasses;

    @SuppressWarnings("rawtypes")
    private Class[] methodAnnotations;

    private LockDelegate lockDelegate;

    @Autowired
    private AquariusContextAware contextAware;

    public LockAutoScanProxy() {
        super(SCAN_PACKAGES, ProxyMode.BY_METHOD_ANNOTATION_ONLY, ScanMode.FOR_METHOD_ANNOTATION_ONLY);
    }

    @PostConstruct
    public void initialize() {
        lockDelegate = contextAware.getBeanByKey(LockConstant.DELEGATE, LockDelegate.class);
        lockDelegate.initialize();
    }

    @PreDestroy
    public void destory() {
        lockDelegate.destroy();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Class<? extends MethodInterceptor>[] getCommonInterceptors() {
        if (commonInterceptorClasses == null) {
            commonInterceptorClasses = new Class[] { LockInterceptor.class };
        }

        return commonInterceptorClasses;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Class<? extends Annotation>[] getMethodAnnotations() {
        if (methodAnnotations == null) {
            methodAnnotations = new Class[] { Lock.class, ReadLock.class, WriteLock.class };
        }
        return methodAnnotations;
    }
}