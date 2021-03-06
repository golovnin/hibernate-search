/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.engine.backend.document.model.dsl.impl;

import org.hibernate.search.engine.backend.document.IndexObjectFieldAccessor;
import org.hibernate.search.engine.backend.document.model.dsl.IndexSchemaObjectField;
import org.hibernate.search.engine.backend.document.model.dsl.spi.IndexSchemaObjectFieldNodeBuilder;
import org.hibernate.search.engine.backend.types.dsl.IndexFieldTypeFactoryContext;

class IndexSchemaObjectFieldImpl extends IndexSchemaElementImpl<IndexSchemaObjectFieldNodeBuilder>
		implements IndexSchemaObjectField {

	IndexSchemaObjectFieldImpl(IndexFieldTypeFactoryContext typeFactoryContext,
			IndexSchemaObjectFieldNodeBuilder objectFieldBuilder,
			IndexSchemaNestingContext nestingContext) {
		super( typeFactoryContext, objectFieldBuilder, nestingContext );
	}

	@Override
	public IndexObjectFieldAccessor createAccessor() {
		return objectNodeBuilder.createAccessor();
	}

}
