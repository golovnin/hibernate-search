/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.engine.common.impl;

import java.util.List;
import java.util.function.Function;

import org.hibernate.search.engine.mapper.mapping.spi.MappedIndexSearchTarget;
import org.hibernate.search.engine.mapper.session.context.spi.SessionContextImplementor;
import org.hibernate.search.engine.search.DocumentReference;
import org.hibernate.search.engine.search.SearchProjection;
import org.hibernate.search.engine.search.SearchQuery;
import org.hibernate.search.engine.search.dsl.query.SearchQueryResultContext;
import org.hibernate.search.engine.search.dsl.query.impl.SearchQueryResultContextImpl;
import org.hibernate.search.engine.search.loading.spi.ObjectLoader;
import org.hibernate.search.engine.search.dsl.predicate.SearchPredicateFactoryContext;
import org.hibernate.search.engine.search.dsl.predicate.impl.SearchPredicateFactoryContextImpl;
import org.hibernate.search.engine.search.dsl.projection.SearchProjectionFactoryContext;
import org.hibernate.search.engine.search.dsl.projection.impl.SearchProjectionFactoryContextImpl;
import org.hibernate.search.engine.search.dsl.sort.SearchSortContainerContext;
import org.hibernate.search.engine.search.dsl.sort.impl.SearchTargetSortRootContext;
import org.hibernate.search.engine.search.dsl.spi.SearchTargetContext;
import org.hibernate.search.engine.search.query.impl.ObjectHitAggregator;
import org.hibernate.search.engine.search.query.impl.ProjectionHitAggregator;
import org.hibernate.search.engine.search.query.impl.ReferenceHitAggregator;
import org.hibernate.search.engine.search.query.spi.HitAggregator;
import org.hibernate.search.engine.search.query.spi.LoadingHitCollector;
import org.hibernate.search.engine.search.query.spi.ProjectionHitCollector;
import org.hibernate.search.engine.search.query.spi.ReferenceHitCollector;
import org.hibernate.search.engine.search.query.spi.SearchQueryBuilder;

class MappedIndexSearchTargetImpl<C> implements MappedIndexSearchTarget {

	private final SearchTargetContext<C> searchTargetContext;

	MappedIndexSearchTargetImpl(SearchTargetContext<C> searchTargetContext) {
		this.searchTargetContext = searchTargetContext;
	}

	@Override
	public String toString() {
		return new StringBuilder( getClass().getSimpleName() )
				.append( "[" )
				.append( "context=" ).append( searchTargetContext )
				.append( "]" )
				.toString();
	}

	@Override
	public <R, O, Q> SearchQueryResultContext<Q> queryAsLoadedObjects(SessionContextImplementor sessionContext,
			Function<DocumentReference, R> documentReferenceTransformer,
			ObjectLoader<R, O> objectLoader,
			Function<SearchQuery<O>, Q> searchQueryWrapperFactory) {
		HitAggregator<LoadingHitCollector, List<O>> hitAggregator =
				new ObjectHitAggregator<>( documentReferenceTransformer, objectLoader );

		SearchQueryBuilder<O, C> builder = searchTargetContext.getSearchQueryBuilderFactory()
				.asObjects( sessionContext, hitAggregator );

		return new SearchQueryResultContextImpl<>(
				searchTargetContext, builder, searchQueryWrapperFactory
		);
	}

	@Override
	public <R, T, Q> SearchQueryResultContext<Q> queryAsReferences(SessionContextImplementor sessionContext,
			Function<DocumentReference, R> documentReferenceTransformer,
			Function<R, T> hitTransformer,
			Function<SearchQuery<T>, Q> searchQueryWrapperFactory) {
		HitAggregator<ReferenceHitCollector, List<T>> hitAggregator =
				new ReferenceHitAggregator<>( hitTransformer.compose( documentReferenceTransformer ) );

		SearchQueryBuilder<T, C> builder = searchTargetContext.getSearchQueryBuilderFactory()
				.asReferences( sessionContext, hitAggregator );

		return new SearchQueryResultContextImpl<>(
				searchTargetContext, builder, searchQueryWrapperFactory
		);
	}

	@Override
	public <R, O, T, Q> SearchQueryResultContext<Q> queryAsProjections(
			SessionContextImplementor sessionContext,
			Function<DocumentReference, R> documentReferenceTransformer,
			ObjectLoader<R, O> objectLoader,
			Function<List<?>, T> hitTransformer,
			Function<SearchQuery<T>, Q> searchQueryWrapperFactory,
			SearchProjection<?>... projections) {
		HitAggregator<ProjectionHitCollector, List<T>> hitAggregator =
				new ProjectionHitAggregator<>( documentReferenceTransformer, objectLoader, hitTransformer,
						projections.length );

		SearchQueryBuilder<T, C> builder = searchTargetContext.getSearchQueryBuilderFactory()
				.asProjections( sessionContext, hitAggregator, projections );

		return new SearchQueryResultContextImpl<>(
				searchTargetContext, builder, searchQueryWrapperFactory
		);
	}

	@Override
	public SearchPredicateFactoryContext predicate() {
		return new SearchPredicateFactoryContextImpl<>( searchTargetContext.getSearchPredicateBuilderFactory() );
	}

	@Override
	public SearchSortContainerContext sort() {
		return new SearchTargetSortRootContext<>( searchTargetContext.getSearchSortBuilderFactory() );
	}

	@Override
	public SearchProjectionFactoryContext projection() {
		return new SearchProjectionFactoryContextImpl( searchTargetContext.getSearchProjectionFactory() );
	}
}