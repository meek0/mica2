/*
 * Copyright (c) 2014 OBiBa. All rights reserved.
 *
 * This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.obiba.mica.network.search.rest;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Stream;

import javax.inject.Inject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.obiba.mica.network.search.NetworkIndexer;
import org.obiba.mica.search.JoinQueryExecutor;
import org.obiba.mica.search.queries.NetworkQuery;
import org.obiba.mica.search.rest.QueryDtoHelper;
import org.obiba.mica.web.model.MicaSearch;
import org.springframework.context.annotation.Scope;

import com.codahale.metrics.annotation.Timed;
import com.google.common.base.Strings;

import static org.obiba.mica.web.model.MicaSearch.JoinQueryDto;
import static org.obiba.mica.web.model.MicaSearch.JoinQueryResultDto;

@Path("/networks/_search")
@RequiresAuthentication
@Scope("request")
public class PublishedNetworksSearchResource {

  @Inject
  JoinQueryExecutor joinQueryExecutor;

  @GET
  @Timed
  public JoinQueryResultDto query(@QueryParam("from") @DefaultValue("0") int from,
      @QueryParam("limit") @DefaultValue("10") int limit, @QueryParam("sort") String sort,
      @QueryParam("order") String order, @QueryParam("study") String studyId, @QueryParam("query") String query,
      @QueryParam("locale") @DefaultValue("en") String locale)
      throws IOException {

    MicaSearch.QueryDto queryDto = QueryDtoHelper
        .createQueryDto(from, limit, sort, order, query, locale, Stream.of(NetworkIndexer.ANALYZED_FIELDS));
    if (!Strings.isNullOrEmpty(studyId)) {
      queryDto = QueryDtoHelper.addTermFilters(queryDto,
          Arrays.asList(QueryDtoHelper.createTermFilter(NetworkQuery.JOIN_FIELD, Arrays.asList(studyId))),
          QueryDtoHelper.BoolQueryType.SHOULD);
    }

    return joinQueryExecutor.listQuery(JoinQueryExecutor.QueryType.NETWORK, queryDto, locale);
  }

  @POST
  @Timed
  public JoinQueryResultDto list(JoinQueryDto joinQueryDto) throws IOException {
    return joinQueryExecutor.query(JoinQueryExecutor.QueryType.NETWORK, joinQueryDto);
  }
}