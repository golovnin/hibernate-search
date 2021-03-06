/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.engine.environment.bean;

import org.hibernate.search.util.impl.common.Contracts;

final class TypeAndNameBeanReference<T> extends TypeBeanReference<T> {

	private final String name;

	TypeAndNameBeanReference(Class<T> type, String name) {
		super( type );
		Contracts.assertNotNullNorEmpty( name, "name" );
		this.name = name;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "[type=" + type + ", name=" + name + "]";
	}

	@Override
	public BeanHolder<T> getBean(BeanProvider beanProvider) {
		return beanProvider.getBean( type, name );
	}

}
