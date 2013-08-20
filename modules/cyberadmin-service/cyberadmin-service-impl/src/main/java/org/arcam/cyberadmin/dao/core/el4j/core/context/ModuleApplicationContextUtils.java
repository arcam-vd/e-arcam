/*
 * ModuleApplicationContextUtils 
 * Project: Cyber Admin
 * 
  * 
 * Copyright (c) 2013, ARCAM - Association de la Region Cossonay-Aubonne-Morges
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without 
 * modification,are permitted provided that the following conditions are met:
 * 
 * - Redistributions of source code must retain the above copyright notice, 
 *   this list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright notice, 
 *   this list of conditions and the following disclaimer in the documentation 
 *   and/or other materials provided with the distribution.
 * 
 * Neither the name of the ARCAM - Association de la Region 
 * Cossonay-Aubonne-Morges nor the names of its contributors may be used to 
 * endorse or promote products derived from this software without specific 
 * prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE REGENTS AND CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 */
package org.arcam.cyberadmin.dao.core.el4j.core.context;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.core.OrderComparator;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;
import org.arcam.cyberadmin.dao.core.el4j.services.monitoring.notification.CoreNotificationHelper;


/**
 * This class allows excluding some items out of a file list.
 *
 * @author Andreas Bur (ABU)
 */
public class ModuleApplicationContextUtils {

	/**
	 * String to find all spring configuration files in folder
	 * <code>mandatory</code>.
	 */
	private static final String MANDATORY = "mandatory/*.xml";
	
	/** The static logger. */
	private static Logger s_logger = LoggerFactory.getLogger(
			ModuleApplicationContextUtils.class);
	
	/** The application context that uses this instance. */
	private ApplicationContext m_appContext;
	
	/**
	 * @see #setReverseConfigLocationResourceArray(boolean)
	 */
	private boolean m_reverseConfigLocationResourceArray = false;
	
	/**
	 * Creates a new instance that is connected to the given application
	 * contet.
	 *
	 * @param context
	 *          The application context to connect to.
	 */
	public ModuleApplicationContextUtils(ApplicationContext context) {
		m_appContext = context;
	}
	
	/**
	 * Calculate the array of xml configuration files which are loaded into the
	 * ApplicationContext, i.e. exclude the xml files in inclusiveFileNames
	 * which are in exclusiveFileNames.
	 *
	 * @param inclusiveConfigLocations
	 *            array of file paths
	 * @param exclusiveConfigLocations
	 *            array of file paths which are excluded
	 * @param allowBeanDefinitionOverriding
	 *            a boolean which defines if overriding of bean definitions is
	 *            allowed
	 * @return Returns the adapted list of configuration locations.
	 */
	public String[] calculateInputFiles(String[] inclusiveConfigLocations,
			String[] exclusiveConfigLocations,
			boolean allowBeanDefinitionOverriding) {
		
		if (ArrayUtils.isEmpty(inclusiveConfigLocations)) {
			s_logger.warn("No inclusive configuration locations given!");
			return null;
		}

		checkConfigLocations(inclusiveConfigLocations[0]);
		
		List<String> inclusiveFileNames
			= getResolvedFileNames(inclusiveConfigLocations);

		List<String> exclusiveFileNames
			= getResolvedFileNames(exclusiveConfigLocations);

		//remove the xml files in inclusiveFileNames which are in
		// exclusiveFileNames
		for (int i = 0; i < inclusiveFileNames.size(); i++) {
			Object obj = inclusiveFileNames.get(i);
			if (exclusiveFileNames.contains(obj)) {
				inclusiveFileNames.remove(i);
				i--;
			}
		}

		String[] conLoc = new String[inclusiveFileNames.size()];

		for (int i = 0; i < inclusiveFileNames.size(); i++) {
			conLoc[i] = (String) inclusiveFileNames.get(i);
		}

		return conLoc;
	}
	
	/**
	 * Check whether the 'classpath*:mandatory/*.xml' config location is loaded.
	 *
	 * @param configLocation
	 *            The config location
	 */
	protected void checkConfigLocations(String configLocation) {
		if (!(configLocation.equals("classpath*:" + MANDATORY)
				|| (configLocation.equals("classpath*:/" + MANDATORY)))) {

			s_logger.warn("The config location 'classpath*:" + MANDATORY
					+ "' is not loaded or is not the first config location"
					+ " which is loaded.");
		}
	}

	/**
	 * Changes the syntax of the pathnames, i.e. filepaths beginning with
	 * "file:$Drive" and not with "file:/$Drive" are changed and "\" characters
	 * are changed to "/". This is necessary for the
	 * PathMatchingResourcePatternResolver to resolve ant-style filepaths.
	 *
	 * @param unresolvedFileNames Are the names of unresolved file names.
	 * @return Returns a list of resolved file names.
	 */
	protected List<String> getResolvedFileNames(String[] unresolvedFileNames) {

		List<String> result = new ArrayList<String>();

		if (unresolvedFileNames == null) {
			return result;
		}

		for (int i = 0; i < unresolvedFileNames.length; i++) {
			String[] resolvedFileNames = resolveAttribute(unresolvedFileNames[i]
					.replace('\\', '/'));
			for (int j = 0; j < resolvedFileNames.length; j++) {
				if ((resolvedFileNames[j].startsWith("file:"))
						&& (!resolvedFileNames[j].startsWith("file:/"))) {
					resolvedFileNames[j] = resolvedFileNames[j].replaceFirst(
							"file:", "file:/");
				}
				result.add(resolvedFileNames[j]);
			}
		}
		return result;
	}

	/**
	 * Resolves a path (i.e. file- or classpath) by applying Ant-style path
	 * matching. Returns all resolved xml files. A warning will be displayed if
	 * a resource does not exist.
	 *
	 * @param path
	 *            a path of an xml file, either absolute, relative or Ant-style
	 * @return all resolved xml files
	 */
	protected String[] resolveAttribute(String path) {
		List<String> resolvedAttributes = new ArrayList<String>();

		try {
			Resource[] resLocal = m_appContext.getResources(path);
			if (isReverseConfigLocationResourceArray()) {
				ArrayUtils.reverse(resLocal);
			}

			for (int i = 0; i < resLocal.length; i++) {
				if (resLocal[i].exists()) {
					resolvedAttributes.add(resLocal[i].getURL().toString());
				} else {
					s_logger.warn("The file '" + resLocal[i].toString()
							+ "' does not exist.");
				}
			}
		} catch (IOException e) {
			String message = "An IOException has occurred.";
			CoreNotificationHelper.notifyMisconfiguration(message, e);
		}

		String[] result = new String[resolvedAttributes.size()];

		for (int i = 0; i < result.length; i++) {
			result[i] = (String) resolvedAttributes.get(i);
		}
		return result;
	}

	/**
	 * @return Returns the reverseConfigLocationResourceArray.
	 */
	public boolean isReverseConfigLocationResourceArray() {
		return m_reverseConfigLocationResourceArray;
	}

	/**
	 * Flag to indicate if the resource array of a config location should be
	 * reversed. The default is set to <code>false</code>.
	 *
	 * @param reverseConfigLocationResourceArray
	 *            Is the reverseConfigLocationResourceArray to set.
	 */
	public void setReverseConfigLocationResourceArray(
		boolean reverseConfigLocationResourceArray) {
		m_reverseConfigLocationResourceArray
			= reverseConfigLocationResourceArray;
	}
	
	/**
	 * All bean factory post processors of the given bean factory will be
	 * created and invoked in strict order. First the {@link PriorityOrdered},
	 * then the {@link Ordered} and as last the unordered bean factory post
	 * processors.
	 *
	 * @param beanFactory
	 *            Is the factory to create the bean factory post processors.
	 */
	@SuppressWarnings("unchecked")
	public void invokeBeanFactoryPostProcessorsStrictlyOrdered(
		ConfigurableListableBeanFactory beanFactory) {
		
		// The given application context must be an AbstractApplicationContext
		Assert.isInstanceOf(AbstractApplicationContext.class, m_appContext);
		AbstractApplicationContext ctx
			= (AbstractApplicationContext) m_appContext;
		
		// Invoke factory processors registered with the context instance.
		for (Iterator it = ctx.getBeanFactoryPostProcessors().iterator();
			it.hasNext();) {
			
			BeanFactoryPostProcessor factoryProcessor
				= (BeanFactoryPostProcessor) it.next();
			factoryProcessor.postProcessBeanFactory(beanFactory);
		}

		// Do not initialize FactoryBeans here: We need to leave all regular
		// beans uninitialized to let the bean factory post-processors apply to
		// them!
		String[] postProcessorNames
			= beanFactory.getBeanNamesForType(BeanFactoryPostProcessor.class,
				true, false);

		// Separate between BeanFactoryPostProcessors that implement
		// PriorityOrdered, Ordered, and the rest.
		List<OrderedBeanNameHolder> priorityOrderedPostProcessorHolders
			= new ArrayList<OrderedBeanNameHolder>();
		List<OrderedBeanNameHolder> orderedPostProcessorHolders
			= new ArrayList<OrderedBeanNameHolder>();
		List<String> nonOrderedPostProcessorNames = new ArrayList<String>();
		for (int i = 0; i < postProcessorNames.length; i++) {
			String postProcessorName = postProcessorNames[i];
			if (ctx.isTypeMatch(postProcessorName, PriorityOrdered.class)) {
				priorityOrderedPostProcessorHolders.add(
					getOrderedBeanNameHolder(beanFactory, postProcessorName));
			} else if (ctx.isTypeMatch(postProcessorName, Ordered.class)) {
				orderedPostProcessorHolders.add(
					getOrderedBeanNameHolder(beanFactory, postProcessorName));
			} else {
				nonOrderedPostProcessorNames.add(postProcessorName);
			}
		}

		// First, invoke the BeanFactoryPostProcessors that implement
		// PriorityOrdered.
		Collections.sort(priorityOrderedPostProcessorHolders,
			new OrderComparator());
		invokeBeanFactoryPostProcessors(beanFactory,
			priorityOrderedPostProcessorHolders);

		// Second, invoke the BeanFactoryPostProcessors that implement Ordered.
		Collections.sort(orderedPostProcessorHolders,
			new OrderComparator());
		invokeBeanFactoryPostProcessors(beanFactory,
			orderedPostProcessorHolders);

		// Finally, invoke all other BeanFactoryPostProcessors.
		for (String nonOrderedPostProcessorName
			: nonOrderedPostProcessorNames) {
			Object bean = ctx.getBean(nonOrderedPostProcessorName);
			BeanFactoryPostProcessor postProcessor
				= (BeanFactoryPostProcessor) bean;
			postProcessor.postProcessBeanFactory(beanFactory);
		}
	}

	/**
	 * Invoke the given BeanFactoryPostProcessor beans.
	 *
	 * @param beanFactory
	 *            Is the factory where to create the
	 *            <code>BeanFactoryPostProcessor</code>s
	 * @param postProcessorHolders
	 *            Are the holders of the factory post processor bean names.
	 */
	protected void invokeBeanFactoryPostProcessors(
		ConfigurableListableBeanFactory beanFactory,
		List<OrderedBeanNameHolder> postProcessorHolders) {
		for (OrderedBeanNameHolder orderedBeanNameHolder
			: postProcessorHolders) {
			Object bean = m_appContext.getBean(
				orderedBeanNameHolder.getBeanName());
			BeanFactoryPostProcessor postProcessor
				= (BeanFactoryPostProcessor) bean;
			postProcessor.postProcessBeanFactory(beanFactory);
		}
	}
	
	/**
	 * Returns a ordered bean name holder for the given bean.
	 *
	 * @param beanFactory
	 *            Is the factory where the bean is configured.
	 * @param orderedBeanName
	 *            Is the name of the ordered bean.
	 * @return Returns a ordered bean name holder for the given bean.
	 * @throws NoSuchBeanDefinitionException
	 *             If the given bean name does not exist.
	 */
	protected OrderedBeanNameHolder getOrderedBeanNameHolder(
		ConfigurableListableBeanFactory beanFactory, String orderedBeanName)
		throws NoSuchBeanDefinitionException {
		
		BeanDefinition beanDefinition
			= beanFactory.getBeanDefinition(orderedBeanName);
		PropertyValues processorDefinitionProps
			= beanDefinition.getPropertyValues();
		PropertyValue order
			= processorDefinitionProps.getPropertyValue("order");
		int orderAsInt = 0;
		if (order != null) {
			try {
				Object orderValue = order.getValue();
				String orderAsString;
				if (orderValue instanceof TypedStringValue) {
					TypedStringValue orderValueString
						= (TypedStringValue) order.getValue();
					orderAsString = orderValueString.getValue();
				} else {
					orderAsString = orderValue.toString();
				}
				orderAsInt = Integer.parseInt(orderAsString);
			} catch (NumberFormatException e) {
				orderAsInt = 0;
			}
		}
		return new OrderedBeanNameHolder(orderAsInt, orderedBeanName);
	}
}
