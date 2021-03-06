/*
 * Copyright 2010 Luke Daley
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *	   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package grails.plugin.aopreloadingfix

import org.springframework.aop.TargetSource
import org.codehaus.groovy.grails.compiler.GrailsClassLoader
import org.springframework.aop.aspectj.annotation.AnnotationAwareAspectJAutoProxyCreator

/**
 * Circumvents GRAILS-6370 by using a new class loader for each proxy.
 */
class ClassLoaderPerProxyGroovyAwareAspectJAwareAdvisorAutoProxyCreator extends AnnotationAwareAspectJAutoProxyCreator {

	private ClassLoader baseLoader = null;
		
	public void setBeanClassLoader(ClassLoader classLoader) {
		baseLoader = classLoader
		super.setBeanClassLoader(classLoader)
	}
	
	protected Object createProxy(Class<?> beanClass, String beanName, Object[] specificInterceptors, TargetSource targetSource) {
		setProxyClassLoader(new GrailsClassLoader(baseLoader, null, null))
		def proxy = super.createProxy(beanClass, beanName, specificInterceptors, targetSource)
		setProxyClassLoader(baseLoader)
		proxy
	}
	
	protected Object getCacheKey(Class<?> beanClass, String beanName) {
		beanClass.hashCode() + "_" + beanName
	}
	
	protected boolean shouldProxyTargetClass(Class<?> beanClass, String beanName) {
		GroovyObject.class.isAssignableFrom(beanClass) || super.shouldProxyTargetClass(beanClass, beanName)
	}

}