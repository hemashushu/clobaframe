/**
 * For compatible with Spring Framework.
 * 
 * There are several interface were share directly from Spring Framework, includes:
 * {@link Autowired} and {@link Value} for inject.
 * {@link MessageSource} for i18n message source resolve.
 * {@link Ordered}.
 * {@link Resource} and {@link ResourceLoader}.
 * {@link Assert}.
 * 
 */
package org.archboy.clobaframe.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.core.Ordered;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.Assert;
