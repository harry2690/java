/*
 * Copyright 2002-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.harrywu.springweb.utils;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.util.StringValueResolver;

/**
 * Utility class for working with Strings that have placeholder values in them. A placeholder takes the form
 * <code>${name}</code>. Using <code>PropertyPlaceholderHelper</code> these placeholders can be substituted for
 * user-supplied values. <p> Values for substitution can be supplied using a {@link Properties} instance or
 * using a {@link PlaceholderResolver}.
 *
 * @author Juergen Hoeller
 * @author Rob Harrop
 * @since 3.0
 */
public class PropertyPlaceholderHelper {

	/** Default placeholder prefix: "${" */
	public static final String DEFAULT_PLACEHOLDER_PREFIX = "${";

	/** Default placeholder suffix: "}" */
	public static final String DEFAULT_PLACEHOLDER_SUFFIX = "}";

	/** Default value separator: ":" */
	public static final String DEFAULT_VALUE_SEPARATOR = ":";

	private static final Log logger = LogFactory.getLog(PropertyPlaceholderHelper.class);

	private final String placeholderPrefix;

	private final String placeholderSuffix;

	private final String valueSeparator;

	private final boolean ignoreUnresolvablePlaceholders;

	private final String nullValue;

	/**
	 * Creates a new <code>PropertyPlaceholderHelper</code> that uses the supplied prefix and suffix.
	 * Unresolvable placeholders are ignored.
	 */
	public PropertyPlaceholderHelper() {
		this(DEFAULT_PLACEHOLDER_PREFIX, DEFAULT_PLACEHOLDER_SUFFIX, DEFAULT_VALUE_SEPARATOR, true);
	}

	/**
	 * Creates a new <code>PropertyPlaceholderHelper</code> that uses the supplied prefix and suffix.
	 * @param ignoreUnresolvablePlaceholders indicates whether unresolvable placeholders should be ignored
	 */
	public PropertyPlaceholderHelper(boolean ignoreUnresolvablePlaceholders) {
		this(DEFAULT_PLACEHOLDER_PREFIX, DEFAULT_PLACEHOLDER_SUFFIX, DEFAULT_VALUE_SEPARATOR, ignoreUnresolvablePlaceholders);
	}

	/**
	 * Creates a new <code>PropertyPlaceholderHelper</code> that uses the supplied prefix and suffix.
	 * Unresolvable placeholders are ignored.
	 * @param placeholderPrefix the prefix that denotes the start of a placeholder.
	 * @param placeholderSuffix the suffix that denotes the end of a placeholder.
	 */
	public PropertyPlaceholderHelper(String placeholderPrefix, String placeholderSuffix) {
		this(placeholderPrefix, placeholderSuffix, DEFAULT_VALUE_SEPARATOR, true);
	}

	/**
	 * Creates a new <code>PropertyPlaceholderHelper</code> that uses the supplied prefix and suffix.
	 * @param placeholderPrefix the prefix that denotes the start of a placeholder.
	 * @param placeholderSuffix the suffix that denotes the end of a placeholder.
	 * @param ignoreUnresolvablePlaceholders indicates whether unresolvable placeholders should be ignored
	 * (<code>true</code>) or cause an exception (<code>false</code>).
	 */
	public PropertyPlaceholderHelper(String placeholderPrefix, String placeholderSuffix, String valueSeparator,
			boolean ignoreUnresolvablePlaceholders) {
		this(placeholderPrefix, placeholderSuffix, valueSeparator, ignoreUnresolvablePlaceholders, null);
	}

	/**
	 * Creates a new <code>PropertyPlaceholderHelper</code> that uses the supplied prefix and suffix.
	 * @param placeholderPrefix the prefix that denotes the start of a placeholder.
	 * @param placeholderSuffix the suffix that denotes the end of a placeholder.
	 * @param ignoreUnresolvablePlaceholders indicates whether unresolvable placeholders should be ignored
	 * (<code>true</code>) or cause an exception (<code>false</code>).
	 * @param nullValue a special string value that signifies the result of a replacement should be null
	 */
	public PropertyPlaceholderHelper(String placeholderPrefix, String placeholderSuffix, String valueSeparator,
			boolean ignoreUnresolvablePlaceholders, String nullValue) {

		Assert.notNull(placeholderPrefix, "placeholderPrefix must not be null");
		Assert.notNull(placeholderSuffix, "placeholderSuffix must not be null");
		this.placeholderPrefix = placeholderPrefix;
		this.placeholderSuffix = placeholderSuffix;
		this.valueSeparator = valueSeparator;
		this.ignoreUnresolvablePlaceholders = ignoreUnresolvablePlaceholders;
		this.nullValue = nullValue;
	}

	/**
	 * Replaces all placeholders of format <code>${name}</code> with the corresponding property
	 * from the supplied {@link Properties}.
	 * @param value the value containing the placeholders to be replaced.
	 * @param properties the <code>Properties</code> to use for replacement.
	 * @return the supplied value with placeholders replaced inline.
	 */
	public String replacePlaceholders(String value, final Properties properties) {
		Assert.notNull(properties, "Argument 'properties' must not be null.");
		return replacePlaceholders(value, new PropertiesPropertyResolver(properties));
	}

	/**
	 * Replaces all placeholders of format <code>${name}</code> with the corresponding property
	 * from the supplied {@link Properties}.
	 * @param value the value containing the placeholders to be replaced.
	 * @param placeholderResolver the <code>StringValueResolver</code> to use for replacement.
	 * @return the supplied value with placeholders replaced inline.
	 */
	public String replacePlaceholders(String value, StringValueResolver placeholderResolver) {
		Assert.notNull(placeholderResolver, "Argument 'placeholderResolver' must not be null.");
		Assert.notNull(value, "Argument 'value' must not be null.");
		String result = parseStringValue(value, placeholderResolver, new HashSet<String>());
		if (nullValue != null && nullValue.equals(result)) {
			return null;
		}
		return result;
	}

	protected String parseStringValue(String strVal, StringValueResolver placeholderResolver, Set<String> visitedPlaceholders) {

		StringBuilder buf = new StringBuilder(strVal);

		int startIndex = strVal.indexOf(this.placeholderPrefix);
		while (startIndex != -1) {
			int endIndex = findPlaceholderEndIndex(buf, startIndex);
			if (endIndex != -1) {
				String placeholder = buf.substring(startIndex + this.placeholderPrefix.length(), endIndex);
				if (!visitedPlaceholders.add(placeholder)) {
					throw new IllegalArgumentException("Circular placeholder reference '" + placeholder
							+ "' in property definitions");
				}
				// Recursive invocation, parsing placeholders contained in the placeholder key.
				placeholder = parseStringValue(placeholder, placeholderResolver, visitedPlaceholders);

				// Now obtain the value for the fully resolved key...
				String propVal = placeholderResolver.resolveStringValue(placeholder);
				if (propVal == null && this.valueSeparator != null) {
					int separatorIndex = placeholder.indexOf(this.valueSeparator);
					if (separatorIndex != -1) {
						String actualPlaceholder = placeholder.substring(0, separatorIndex);
						String defaultValue = placeholder.substring(separatorIndex + this.valueSeparator.length());
						propVal = placeholderResolver.resolveStringValue(actualPlaceholder);
						if (propVal == null) {
							propVal = defaultValue;
						}
					}
				}
				if (propVal != null) {
					// Recursive invocation, parsing placeholders contained in the
					// previously resolved placeholder value.
					propVal = parseStringValue(propVal, placeholderResolver, visitedPlaceholders);
					buf.replace(startIndex, endIndex + this.placeholderSuffix.length(), propVal);

					if (logger.isTraceEnabled()) {
						logger.trace("Resolved placeholder '" + placeholder + "'");
					}

					startIndex = buf.indexOf(this.placeholderPrefix, startIndex + propVal.length());
				} else if (this.ignoreUnresolvablePlaceholders) {
					// Proceed with unprocessed value.
					startIndex = buf.indexOf(this.placeholderPrefix, endIndex + this.placeholderSuffix.length());
				} else {
					throw new IllegalArgumentException("Could not resolve placeholder '" + placeholder + "'");
				}

				visitedPlaceholders.remove(placeholder);
			} else {
				startIndex = -1;
			}
		}

		return buf.toString();
	}

	private int findPlaceholderEndIndex(CharSequence buf, int startIndex) {
		int index = startIndex + this.placeholderPrefix.length();
		int withinNestedPlaceholder = 0;
		while (index < buf.length()) {
			if (StringUtils.substringMatch(buf, index, this.placeholderSuffix)) {
				if (withinNestedPlaceholder > 0) {
					withinNestedPlaceholder--;
					index = index + this.placeholderPrefix.length() - 1;
				} else {
					return index;
				}
			} else if (StringUtils.substringMatch(buf, index, this.placeholderPrefix)) {
				withinNestedPlaceholder++;
				index = index + this.placeholderPrefix.length();
			} else {
				index++;
			}
		}
		return -1;
	}

	private class PropertiesPropertyResolver implements StringValueResolver {

	    private Properties properties;
	    
	    public PropertiesPropertyResolver(Properties properties) {
	        super();
	        this.properties = properties;
	    }
	    
	    public String resolveStringValue(String strVal) {
	        return properties.getProperty(strVal);
	    }
	}
}
