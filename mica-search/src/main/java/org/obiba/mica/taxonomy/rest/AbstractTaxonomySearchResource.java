/*
 * Copyright (c) 2015 OBiBa. All rights reserved.
 *
 * This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.obiba.mica.taxonomy.rest;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;

import org.elasticsearch.indices.IndexMissingException;
import org.obiba.mica.micaConfig.service.MicaConfigService;
import org.obiba.mica.micaConfig.service.OpalService;
import org.obiba.mica.taxonomy.EsTaxonomyTermService;
import org.obiba.mica.taxonomy.EsTaxonomyVocabularyService;
import org.obiba.opal.core.domain.taxonomy.Taxonomy;
import org.obiba.opal.web.model.Opal;
import org.obiba.opal.web.taxonomy.Dtos;

import com.google.common.collect.Lists;

import sun.util.locale.LanguageTag;

public class AbstractTaxonomySearchResource {

  private static final String DEFAULT_SORT = "id";

  private static final String[] VOCABULARY_FIELDS = { "title", "description" };

  private static final String[] TERM_FIELDS = { "title", "description", "keywords" };

  @Inject
  private EsTaxonomyTermService esTaxonomyTermService;

  @Inject
  private EsTaxonomyVocabularyService esTaxonomyVocabularyService;

  @Inject
  private MicaConfigService micaConfigService;

  @Inject
  protected OpalService opalService;

  protected void populate(Opal.TaxonomyDto.Builder tBuilder, Taxonomy taxonomy,
    Map<String, Map<String, List<String>>> taxoNamesMap) {
    taxonomy.getVocabularies().stream().filter(v -> taxoNamesMap.get(taxonomy.getName()).containsKey(v.getName()))
      .forEach(voc -> {
        Opal.VocabularyDto.Builder vBuilder = Dtos.asDto(voc, false).toBuilder();
        List<String> termNames = taxoNamesMap.get(taxonomy.getName()).get(voc.getName());
        if(termNames.isEmpty())
          vBuilder.addAllTerms(voc.getTerms().stream().map(Dtos::asDto).collect(Collectors.toList()));
        else voc.getTerms().stream().filter(t -> termNames.contains(t.getName()))
          .forEach(term -> vBuilder.addTerms(Dtos.asDto(term)));
        tBuilder.addVocabularies(vBuilder);
      });
  }

  protected List<String> filterVocabularies(String query) {
    try {
      return esTaxonomyVocabularyService.find(0, Integer.MAX_VALUE, DEFAULT_SORT, "asc", null, query, getFields(VOCABULARY_FIELDS))
        .getList();
    } catch(IndexMissingException e) {
      initTaxonomies();
      // for a 404 response
      throw new NoSuchElementException();
    }
  }

  protected List<String> filterTerms(String query) {
    try {
      return esTaxonomyTermService.find(0, Integer.MAX_VALUE, DEFAULT_SORT, "asc", null, query, getFields(TERM_FIELDS)).getList();
    } catch(IndexMissingException e) {
      initTaxonomies();
      // for a 404 response
      throw new NoSuchElementException();
    }
  }

  private List<String> getFields(String... fieldNames) {
    List<String> fields = Lists.newArrayList("name.analyzed");
    Stream.concat(micaConfigService.getConfig().getLocalesAsString().stream(), Stream.of(LanguageTag.UNDETERMINED))
      .forEach(locale -> {
        Arrays.stream(fieldNames).forEach(f -> {
          fields.add(f + "." + locale + ".analyzed");
        });
      });
    return fields;
  }

  /**
   * Populate taxonomies cache and trigger taxonomies indexing.
   */
  private void initTaxonomies() {
    opalService.getTaxonomies();
  }
}