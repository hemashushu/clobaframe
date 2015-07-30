package org.archboy.clobaframe.setting.application.config;

import java.util.Properties;
import org.archboy.clobaframe.setting.application.ApplicationSetting;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionVisitor;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.util.PropertyPlaceholderHelper;
import org.springframework.util.StringValueResolver;

/**
 * Modified of {@link org.springframework.beans.factory.config.PropertyPlaceholderConfigurer}.
 * 
 * @author yang
 */
public class PropertyPlaceholderConfigurer implements BeanFactoryPostProcessor, PriorityOrdered, 
		BeanNameAware, BeanFactoryAware {

	public static final String DEFAULT_PLACEHOLDER_PREFIX = "${";
	public static final String DEFAULT_PLACEHOLDER_SUFFIX = "}";
	public static final String DEFAULT_VALUE_SEPARATOR = ":";

	protected String placeholderPrefix = DEFAULT_PLACEHOLDER_PREFIX;
	protected String placeholderSuffix = DEFAULT_PLACEHOLDER_SUFFIX;
	protected String valueSeparator = DEFAULT_VALUE_SEPARATOR;

	protected boolean ignoreUnresolvablePlaceholders = false;
	protected String nullValue;

	private ApplicationSetting applicationSetting;
	private BeanFactory beanFactory;
	private String beanName;

	private int order = Ordered.LOWEST_PRECEDENCE;  // default: same as non-Ordered

	public void setApplicationSetting(ApplicationSetting applicationSetting) {
		this.applicationSetting = applicationSetting;
	}
	
	public void setOrder(int order) {
		this.order = order;
	}

	@Override
	public int getOrder() {
		return this.order;
	}
	
	public void setPlaceholderPrefix(String placeholderPrefix) {
		this.placeholderPrefix = placeholderPrefix;
	}

	public void setPlaceholderSuffix(String placeholderSuffix) {
		this.placeholderSuffix = placeholderSuffix;
	}

	/**
	 * Specify the separating character between the placeholder variable
	 * and the associated default value, or {@code null} if no such
	 * special character should be processed as a value separator.
	 * The default is {@value #DEFAULT_VALUE_SEPARATOR}.
	 * @param valueSeparator
	 */
	public void setValueSeparator(String valueSeparator) {
		this.valueSeparator = valueSeparator;
	}

	/**
	 * Set a value that should be treated as {@code null} when
	 * resolved as a placeholder value: e.g. "" (empty String) or "null".
	 * <p>Note that this will only apply to full property values,
	 * not to parts of concatenated values.
	 * <p>By default, no such null value is defined. This means that
	 * there is no way to express {@code null} as a property
	 * value unless you explicitly map a corresponding value here.
	 * @param nullValue
	 */
	public void setNullValue(String nullValue) {
		this.nullValue = nullValue;
	}

	/**
	 * Set whether to ignore unresolvable placeholders.
	 * <p>Default is "false": An exception will be thrown if a placeholder fails
	 * to resolve. Switch this flag to "true" in order to preserve the placeholder
	 * String as-is in such a case, leaving it up to other placeholder configurers
	 * to resolve it.
	 * @param ignoreUnresolvablePlaceholders
	 */
	public void setIgnoreUnresolvablePlaceholders(boolean ignoreUnresolvablePlaceholders) {
		this.ignoreUnresolvablePlaceholders = ignoreUnresolvablePlaceholders;
	}

	@Override
	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	protected void doProcessProperties(ConfigurableListableBeanFactory beanFactoryToProcess,
			StringValueResolver valueResolver) {

		BeanDefinitionVisitor visitor = new BeanDefinitionVisitor(valueResolver);

		String[] beanNames = beanFactoryToProcess.getBeanDefinitionNames();
		for (String curName : beanNames) {
			// Check that we're not parsing our own bean definition,
			// to avoid failing on unresolvable placeholders in properties file locations.
			if (!(curName.equals(this.beanName) && beanFactoryToProcess.equals(this.beanFactory))) {
				BeanDefinition bd = beanFactoryToProcess.getBeanDefinition(curName);
				try {
					visitor.visitBeanDefinition(bd);
				}
				catch (Exception ex) {
					throw new BeanDefinitionStoreException(bd.getResourceDescription(), curName, ex.getMessage(), ex);
				}
			}
		}

		// New in Spring 2.5: resolve placeholders in alias target names and aliases as well.
		beanFactoryToProcess.resolveAliases(valueResolver);

		// New in Spring 3.0: resolve placeholders in embedded values such as annotation attributes.
		beanFactoryToProcess.addEmbeddedValueResolver(valueResolver);
	}
	

	/**
	 * Core
	 * 
	 * @param beanFactory
	 * @throws BeansException 
	 */
	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		processProperties(beanFactory, null);
	}
	
	protected void processProperties(ConfigurableListableBeanFactory beanFactoryToProcess, Properties props)
			throws BeansException {
		StringValueResolver valueResolver = new PlaceholderResolvingStringValueResolver();
		this.doProcessProperties(beanFactoryToProcess, valueResolver);
	}
	
	private class PlaceholderResolvingStringValueResolver implements StringValueResolver {

		private final PropertyPlaceholderHelper helper;
		private final PropertyPlaceholderHelper.PlaceholderResolver resolver;

		public PlaceholderResolvingStringValueResolver() {
			this.helper = new PropertyPlaceholderHelper(
					placeholderPrefix, placeholderSuffix, 
					valueSeparator, ignoreUnresolvablePlaceholders);
			this.resolver = new PropertyPlaceholderConfigurerResolver();
		}

		@Override
		public String resolveStringValue(String strVal) throws BeansException {
			String value = this.helper.replacePlaceholders(strVal, this.resolver);
			return (value.equals(nullValue) ? null : value);
		}
	}

	private class PropertyPlaceholderConfigurerResolver implements PropertyPlaceholderHelper.PlaceholderResolver {
		@Override
		public String resolvePlaceholder(String placeholderName) {
			return (String)applicationSetting.getValue(placeholderName);
		}
	}

}
