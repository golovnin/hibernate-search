/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.backend.elasticsearch.index.impl;

import java.util.concurrent.CompletableFuture;

import org.hibernate.search.backend.elasticsearch.multitenancy.impl.MultiTenancyStrategy;
import org.hibernate.search.backend.elasticsearch.orchestration.impl.ElasticsearchWorkOrchestrator;
import org.hibernate.search.backend.elasticsearch.util.spi.URLEncodedString;
import org.hibernate.search.backend.elasticsearch.work.builder.factory.impl.ElasticsearchWorkBuilderFactory;
import org.hibernate.search.engine.backend.index.spi.IndexWorkExecutor;
import org.hibernate.search.util.EventContext;

import com.google.gson.JsonObject;

public class ElasticsearchIndexWorkExecutor implements IndexWorkExecutor {

	private final ElasticsearchWorkBuilderFactory builderFactory;
	private final MultiTenancyStrategy multiTenancyStrategy;
	private final ElasticsearchWorkOrchestrator orchestrator;
	private final URLEncodedString indexName;
	private final EventContext eventContext;

	public ElasticsearchIndexWorkExecutor(ElasticsearchWorkBuilderFactory builderFactory,
			MultiTenancyStrategy multiTenancyStrategy, ElasticsearchWorkOrchestrator orchestrator,
			URLEncodedString indexName, EventContext eventContext) {
		this.builderFactory = builderFactory;
		this.multiTenancyStrategy = multiTenancyStrategy;
		this.orchestrator = orchestrator;
		this.indexName = indexName;
		this.eventContext = eventContext;
	}

	@Override
	public CompletableFuture<?> optimize() {
		return orchestrator.submit( builderFactory.optimize().index( indexName ).build() );
	}

	@Override
	public CompletableFuture<?> purge(String tenantId) {
		multiTenancyStrategy.checkTenantId( tenantId, eventContext );
		JsonObject matchAll = new JsonObject();
		matchAll.add( "match_all", new JsonObject() );
		JsonObject document = new JsonObject();
		document.add( "query", multiTenancyStrategy.decorateJsonQuery( matchAll, tenantId ) );

		return orchestrator.submit( builderFactory.deleteByQuery( indexName, document ).build() );
	}

	@Override
	public CompletableFuture<?> flush() {
		return orchestrator.submit( builderFactory.flush().index( indexName ).build() );
	}
}
