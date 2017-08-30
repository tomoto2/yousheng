package com.joe.frame.web.boot;

import java.lang.reflect.Method;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.CallbackFilter;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import net.sf.cglib.proxy.NoOp;
/**
 * 动态代理测试
 * @author Administrator
 *
 */
public class Test implements MethodInterceptor{
	public static void main(String[] arg){
		Enhancer en = new Enhancer();
		en.setSuperclass(A.class);
//		en.setCallback(new Test());
		en.setCallbacks(new Callback[]{new Test() , NoOp.INSTANCE});
		en.setCallbackFilter(new B());;
		A a = (A)en.create();
		a.say();
		a.eat();
	}

	public Object intercept(Object arg0, Method arg1, Object[] arg2, MethodProxy arg3) throws Throwable {
		System.out.println("代理生效了");
		return arg3.invokeSuper(arg0, arg2);
	}
}
class A{
	public void say(){
		System.out.println("hello");
	}
	public void eat(){
		System.out.println("eat");
	}
}
class B implements CallbackFilter{

	public int accept(Method method) {
		if(method.getName().equals("say")){
			return 0;
		}else{
			return 1;
		}
	}
	
}
