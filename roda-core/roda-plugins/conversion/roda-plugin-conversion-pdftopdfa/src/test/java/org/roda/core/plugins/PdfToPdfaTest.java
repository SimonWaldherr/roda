/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE file at the root of the source
 * tree and available online at
 *
 * https://github.com/keeps/roda
 */
package org.roda.core.plugins;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.solr.client.solrj.SolrServerException;
import org.junit.Assert;
import org.junit.Test;
import org.roda.core.RodaCoreFactory;
import org.roda.core.common.iterables.CloseableIterable;
import org.roda.core.data.common.RodaConstants;
import org.roda.core.data.exceptions.IsStillUpdatingException;
import org.roda.core.data.exceptions.RODAException;
import org.roda.core.data.v2.common.OptionalWithCause;
import org.roda.core.data.v2.ip.AIP;
import org.roda.core.data.v2.ip.File;
import org.roda.core.data.v2.ip.Representation;
import org.roda.core.plugins.plugins.conversion.PdfToPdfaPlugin;

import com.google.common.collect.Iterables;

import jersey.repackaged.com.google.common.collect.Lists;

public class PdfToPdfaTest extends AbstractConvertTest {

  @Test
  public void testPdfToPdfaPlugin() throws RODAException, FileAlreadyExistsException, InterruptedException, IOException,
    SolrServerException, IsStillUpdatingException {
    AIP aip = ingestCorpora(0);

    CloseableIterable<OptionalWithCause<File>> allFiles = getModel().listFilesUnder(aip.getId(),
      aip.getRepresentations().get(0).getId(), true);
    List<File> reusableAllFiles = new ArrayList<>();
    Iterables.addAll(reusableAllFiles,
      Lists.newArrayList(allFiles).stream().filter(f -> f.isPresent()).map(f -> f.get()).collect(Collectors.toList()));

    Plugin<Representation> plugin = new PdfToPdfaPlugin<Representation>();
    Map<String, String> parameters = new HashMap<>();
    parameters.put(RodaConstants.PLUGIN_PARAMS_JOB_ID, getFakeJobId());
    parameters.put("outputFormat", "pdf");
    plugin.setParameterValues(parameters);

    // FIXME 20160623 hsilva: passing by null just to make code compiling
    RodaCoreFactory.getPluginOrchestrator().runPluginOnAllRepresentations(null, plugin);
    aip = getModel().retrieveAIP(aip.getId());
    Assert.assertEquals(2, aip.getRepresentations().size());

    CloseableIterable<OptionalWithCause<File>> newAllFiles = getModel().listFilesUnder(aip.getId(),
      aip.getRepresentations().get(1).getId(), true);
    List<File> newReusableAllFiles = new ArrayList<>();
    Iterables.addAll(newReusableAllFiles, Lists.newArrayList(newAllFiles).stream().filter(f -> f.isPresent())
      .map(f -> f.get()).collect(Collectors.toList()));
    Assert.assertEquals(getNumberOfConvertableFiles(), newReusableAllFiles.size());

    for (File f : reusableAllFiles) {
      if (f.getId().matches(".*[.](pdf)$")) {
        String filename = f.getId().substring(0, f.getId().lastIndexOf('.'));
        Assert.assertEquals(1, newReusableAllFiles.stream().filter(o -> o.getId().equals(filename + ".pdf")).count());
      }
    }
  }

}
