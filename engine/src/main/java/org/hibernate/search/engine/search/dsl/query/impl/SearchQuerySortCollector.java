/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.engine.search.dsl.query.impl;

import java.util.function.Consumer;

import org.hibernate.search.engine.search.SearchSort;
import org.hibernate.search.engine.search.dsl.query.SearchQueryContext;
import org.hibernate.search.engine.search.dsl.sort.SearchSortContainerContext;
import org.hibernate.search.engine.search.dsl.sort.impl.SearchSortDslContextImpl;
import org.hibernate.search.engine.search.dsl.sort.impl.DefaultSearchSortContainerContext;
import org.hibernate.search.engine.search.query.spi.SearchQueryBuilder;
import org.hibernate.search.engine.search.sort.spi.SearchSortBuilderFactory;

/**
 * Collect search sorts to later add them to a search query.
 * <p>
 * This class is essentially a bridge transferring information from {@link SearchQueryContext}
 * to {@link SearchQueryBuilder#getQueryElementCollector()}.
 */
class SearchQuerySortCollector<C, B> {

	private final SearchSortBuilderFactory<C, B> factory;
	private final SearchSortDslContextImpl<B> rootDslContext;

	SearchQuerySortCollector(SearchSortBuilderFactory<C, B> factory) {
		this.factory = factory;
		this.rootDslContext = new SearchSortDslContextImpl<>( factory );
	}

	void contribute(C collector) {
		factory.contribute( collector, rootDslContext.getResultingBuilders() );
	}

	void collect(SearchSort sort) {
		factory.toImplementation( sort, rootDslContext::addChild );
	}

	void collect(Consumer<? super SearchSortContainerContext> dslPredicateContributor) {
		SearchSortContainerContext containerContext =
				new DefaultSearchSortContainerContext<>( factory, rootDslContext );
		dslPredicateContributor.accept( containerContext );
	}
}
