/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.mapper.pojo.mapping.building.impl;

import org.hibernate.search.engine.environment.bean.BeanHolder;
import org.hibernate.search.mapper.pojo.bridge.PropertyBridge;
import org.hibernate.search.mapper.pojo.model.impl.PojoModelPropertyRootElement;

public final class BoundPropertyBridge<P> {
	private final BeanHolder<? extends PropertyBridge> bridgeHolder;
	private final PojoModelPropertyRootElement<P> pojoModelRootElement;

	BoundPropertyBridge(BeanHolder<? extends PropertyBridge> bridgeHolder, PojoModelPropertyRootElement<P> pojoModelRootElement) {
		this.bridgeHolder = bridgeHolder;
		this.pojoModelRootElement = pojoModelRootElement;
	}

	public BeanHolder<? extends PropertyBridge> getBridgeHolder() {
		return bridgeHolder;
	}

	public PojoModelPropertyRootElement<P> getPojoModelRootElement() {
		return pojoModelRootElement;
	}
}
