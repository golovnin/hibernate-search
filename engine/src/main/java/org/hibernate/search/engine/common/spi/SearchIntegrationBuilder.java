/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.engine.common.spi;

import org.hibernate.search.engine.environment.bean.spi.BeanResolver;
import org.hibernate.search.engine.environment.classpath.spi.ClassResolver;
import org.hibernate.search.engine.environment.classpath.spi.ResourceResolver;
import org.hibernate.search.engine.mapper.mapping.building.spi.MappingInitiator;
import org.hibernate.search.engine.mapper.mapping.spi.MappingKey;

/**
 * @author Yoann Rodiere
 */
public interface SearchIntegrationBuilder {

	SearchIntegrationBuilder setClassResolver(ClassResolver classResolver);

	SearchIntegrationBuilder setResourceResolver(ResourceResolver resourceResolver);

	SearchIntegrationBuilder setBeanResolver(BeanResolver beanResolver);

	SearchIntegrationBuilder setProperty(String name, Object value);

	<M> SearchIntegrationBuilder addMappingInitiator(MappingKey<M> mappingKey, MappingInitiator<?, M> initiator);

	SearchIntegration build();

}
