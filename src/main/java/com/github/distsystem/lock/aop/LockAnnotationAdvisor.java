
package com.github.distsystem.lock.aop;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.util.StringUtils;

import com.github.distsystem.lock.annotation.DistLock;
import com.github.distsystem.lock.core.DistLockInfo;
import com.github.distsystem.lock.core.LockTemplate;

import lombok.NonNull;

/**
 * 
 * @function 分布式锁注解切面类
 * @date 2019年1月18日 下午2:09:39
 * @author 李桥
 * @version 1.0
 */
public class LockAnnotationAdvisor extends AbstractPointcutAdvisor implements BeanFactoryAware
{

    private static final long serialVersionUID = 1L;

    private Advice advice;

    private Pointcut pointcut;

    public LockAnnotationAdvisor (@NonNull LockMethodInterceptor lockInterceptor)
    {
        this.advice = lockInterceptor;
        this.pointcut = buildPointcut ();
    }

    @Override
    public Pointcut getPointcut ()
    {
        return this.pointcut;
    }

    @Override
    public Advice getAdvice ()
    {
        return this.advice;
    }

    @Override
    public void setBeanFactory (BeanFactory beanFactory) throws BeansException
    {
        if (this.advice instanceof BeanFactoryAware)
        {
            ((BeanFactoryAware) this.advice).setBeanFactory (beanFactory);
        }
    }

    private Pointcut buildPointcut ()
    {
        return AnnotationMatchingPointcut.forMethodAnnotation (DistLock.class);
    }

    public static class LockMethodInterceptor implements MethodInterceptor
    {

        private LockTemplate lockTemplate;

        private LockKeyGenerator lockKeyGenerator = new LockKeyGenerator ();

        public LockMethodInterceptor (LockTemplate lockTemplate)
        {
            this.lockTemplate = lockTemplate;
        }

        @Override
        public Object invoke (MethodInvocation invocation) throws Throwable
        {
            DistLockInfo lockInfo = null;
            try
            {
                DistLock lock4j = invocation.getMethod ().getAnnotation (DistLock.class);
                String lockKey = lockKeyGenerator.getLockKey (invocation, lock4j);
                if (lock4j == null)
                {
                    return invocation.proceed ();
                }

                lockInfo = lockTemplate.lock (DistLockInfo.newDistLockInfo (lockKey, lock4j.expire (), lock4j.timeout ()));
                if (null != lockInfo)
                {
                    return invocation.proceed ();
                }
                return null;
            }
            finally
            {
                if (null != lockInfo)
                {
                    lockTemplate.releaseLock (lockInfo);
                }
            }
        }

    }

    private static class LockKeyGenerator
    {
        private static final ParameterNameDiscoverer NAME_DISCOVERER = new DefaultParameterNameDiscoverer ();
        private static final ExpressionParser PARSER = new SpelExpressionParser ();

        public String getLockKey (MethodInvocation invocation, DistLock lock4j)
        {
            StringBuilder sb = new StringBuilder ();
            Method method = invocation.getMethod ();
            sb.append (method.getDeclaringClass ().getSimpleName ()).append (".").append (method.getName ());
            if (lock4j.keys ().length > 1 || !"".equals (lock4j.keys ()[0]))
            {
                sb.append ("-" + getSpelDefinitionKey (lock4j.keys (), method, invocation.getArguments ()));
            }
            return sb.toString ();
        }

        private String getSpelDefinitionKey (String[] definitionKeys, Method method, Object[] parameterValues)
        {
            EvaluationContext context = new MethodBasedEvaluationContext (null, method, parameterValues,
                                                                          NAME_DISCOVERER);
            List <String> definitionKeyList = new ArrayList<> (definitionKeys.length);
            for (String definitionKey : definitionKeys)
            {
                if (definitionKey != null && !definitionKey.isEmpty ())
                {
                    String key = PARSER.parseExpression (definitionKey).getValue (context).toString ();
                    definitionKeyList.add (key);
                }
            }
            return StringUtils.collectionToDelimitedString (definitionKeyList, "#", "", "");
        }

    }

}