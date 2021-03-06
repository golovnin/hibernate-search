/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.backend.lucene.analysis.model.dsl.impl;

import org.hibernate.search.backend.lucene.analysis.model.dsl.LuceneAnalysisDefinitionContainerContext;
import org.hibernate.search.backend.lucene.analysis.model.dsl.LuceneAnalyzerDefinitionContext;
import org.hibernate.search.backend.lucene.analysis.model.dsl.LuceneNormalizerDefinitionContext;


class DelegatingAnalysisDefinitionContainerContext implements LuceneAnalysisDefinitionContainerContext {

	private final LuceneAnalysisDefinitionContainerContext parentContext;

	DelegatingAnalysisDefinitionContainerContext(LuceneAnalysisDefinitionContainerContext parentContext) {
		this.parentContext = parentContext;
	}

	@Override
	public LuceneAnalyzerDefinitionContext analyzer(String name) {
		return parentContext.analyzer( name );
	}

	@Override
	public LuceneNormalizerDefinitionContext normalizer(String name) {
		return parentContext.normalizer( name );
	}

}
