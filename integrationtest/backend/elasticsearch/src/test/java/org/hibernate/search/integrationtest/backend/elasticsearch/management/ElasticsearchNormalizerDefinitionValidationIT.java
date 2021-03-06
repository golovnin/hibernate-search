/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.integrationtest.backend.elasticsearch.management;

import org.hibernate.search.backend.elasticsearch.cfg.ElasticsearchBackendSettings;
import org.hibernate.search.backend.elasticsearch.cfg.ElasticsearchIndexLifecycleStrategyName;
import org.hibernate.search.backend.elasticsearch.cfg.ElasticsearchIndexSettings;
import org.hibernate.search.integrationtest.backend.elasticsearch.testsupport.configuration.ElasticsearchNormalizerManagementITAnalysisConfigurer;
import org.hibernate.search.integrationtest.backend.elasticsearch.testsupport.util.TestElasticsearchClient;
import org.hibernate.search.integrationtest.backend.tck.testsupport.util.rule.SearchSetupHelper;
import org.hibernate.search.util.SearchException;
import org.hibernate.search.util.impl.integrationtest.common.FailureReportUtils;
import org.hibernate.search.util.impl.test.SubTest;
import org.hibernate.search.util.impl.test.annotation.PortedFromSearch5;

import org.junit.Rule;
import org.junit.Test;

/**
 * Tests for the normalizer validation feature when using automatic index management.
 */
@PortedFromSearch5(original = "org.hibernate.search.elasticsearch.test.ElasticsearchNormalizerDefinitionValidationIT")
public class ElasticsearchNormalizerDefinitionValidationIT {

	private static final String SCHEMA_VALIDATION_CONTEXT = "schema validation";

	private static final String BACKEND_NAME = "myElasticsearchBackend";
	private static final String INDEX_NAME = "IndexName";

	@Rule
	public SearchSetupHelper setupHelper = new SearchSetupHelper();

	@Rule
	public TestElasticsearchClient elasticSearchClient = new TestElasticsearchClient();

	@Test
	public void success_simple() throws Exception {
		elasticSearchClient.index( INDEX_NAME ).deleteAndCreate(
				"index.analysis",
				"{"
					+ "'normalizer': {"
							+ "'custom-normalizer': {"
									+ "'char_filter': ['custom-char-mapping'],"
									+ "'filter': ['custom-elision']"
							+ "}"
					+ "},"
					+ "'char_filter': {"
							+ "'custom-char-mapping': {"
									+ "'type': 'mapping',"
									+ "'mappings': ['foo => bar']"
							+ "}"
					+ "},"
					+ "'filter': {"
							+ "'custom-elision': {"
									+ "'type': 'elision',"
									+ "'articles': ['l', 'd']"
							+ "}"
					+ "}"
				+ "}"
				);

		putMapping();

		setup();

		// If we get here, it means validation passed (no exception was thrown)
	}

	@Test
	public void normalizer_missing() throws Exception {
		elasticSearchClient.index( INDEX_NAME ).deleteAndCreate(
				"index.analysis",
				"{"
					+ "'char_filter': {"
							+ "'custom-char-mapping': {"
									+ "'type': 'mapping',"
									+ "'mappings': ['foo => bar']"
							+ "}"
					+ "},"
					+ "'filter': {"
							+ "'custom-elision': {"
									+ "'type': 'elision',"
									+ "'articles': ['l', 'd']"
							+ "}"
					+ "}"
				+ "}"
				);

		putMapping();

		setupExpectingFailure(
				FailureReportUtils.buildFailureReportPattern()
						.indexContext( INDEX_NAME )
						.contextLiteral( SCHEMA_VALIDATION_CONTEXT )
						.normalizerContext( "custom-normalizer" )
						.failure( "Missing normalizer" )
						.build()
		);
	}

	private void setupExpectingFailure(String failureReportPattern) {
		SubTest.expectException( this::setup )
				.assertThrown()
				.isInstanceOf( SearchException.class )
				.hasMessageMatching( failureReportPattern );
	}

	private void setup() {
		withManagementStrategyConfiguration()
				.withIndex(
						"MappedType", INDEX_NAME,
						ctx -> { },
						indexMapping -> { }
				)
				.setup();
	}

	private SearchSetupHelper.SetupContext withManagementStrategyConfiguration() {
		return setupHelper.withDefaultConfiguration( BACKEND_NAME )
				.withIndexDefaultsProperty(
						BACKEND_NAME,
						ElasticsearchIndexSettings.LIFECYCLE_STRATEGY,
						ElasticsearchIndexLifecycleStrategyName.VALIDATE.getExternalName()
				)
				.withBackendProperty(
						BACKEND_NAME,
						ElasticsearchBackendSettings.ANALYSIS_CONFIGURER,
						new ElasticsearchNormalizerManagementITAnalysisConfigurer()
				);
	}

	protected void putMapping() {
		elasticSearchClient.index( INDEX_NAME ).type().putMapping(
				"{"
						+ "'dynamic': 'strict',"
						+ "'properties': {"
						+ "}"
						+ "}"
		);
	}

}
