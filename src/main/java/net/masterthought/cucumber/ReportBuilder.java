package net.masterthought.cucumber;

import net.masterthought.cucumber.generators.ErrorPage;
import net.masterthought.cucumber.generators.FeatureOverviewPage;
import net.masterthought.cucumber.generators.FeatureReportPage;
import net.masterthought.cucumber.generators.StepOverviewPage;
import net.masterthought.cucumber.generators.TagOverviewPage;
import net.masterthought.cucumber.generators.TagReportPage;
import net.masterthought.cucumber.json.Feature;
import net.masterthought.cucumber.json.support.TagObject;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ReportBuilder {

  private static final Logger LOG = LogManager.getLogger(ReportBuilder.class);

  private ReportResult reportResult;
  private final ReportParser reportParser;

  private Configuration configuration;
  private List<String> jsonFiles;
  private List<String> rerunJsonFiles;

  public ReportBuilder(List<String> jsonFiles, Configuration configuration) {
    this(configuration, jsonFiles, null);
  }

  public ReportBuilder(Configuration configuration, List<String> jsonFiles, List<String> jsonRerunFiles) {
    this.jsonFiles = jsonFiles;
    this.configuration = configuration;
    if (jsonRerunFiles != null) {
      this.rerunJsonFiles = jsonRerunFiles;
    }
    reportParser = new ReportParser(configuration);
  }

  /**
   * Checks if all features pass.
   *
   * @return true if all feature pass otherwise false
   */
  public boolean hasBuildPassed() {
    return reportResult != null && reportResult.getAllFailedFeatures() == 0;
  }

  public void generateReports() {
    try {
      List<Feature> features = reportParser.parseJsonResults(jsonFiles, false);
      if (rerunJsonFiles != null) {
        final List<Feature> rerunFeatures = reportParser.parseJsonResults(this.rerunJsonFiles, true);
        features.addAll(rerunFeatures);
      }

      reportResult = new ReportResult(features);

      copyStaticResources();
      generateAllPages();

      // whatever happens we want to provide at least error page instead of empty report
    } catch (Exception e) {
      generateErrorPage(e);
    }
  }

  private void copyStaticResources() {
    copyResources("css", "reporting.css", "bootstrap.min.css");
    copyResources("js", "jquery.min.js", "bootstrap.min.js", "jquery.tablesorter.min.js", "highcharts.js",
      "highcharts-3d.js");
  }

  private void generateAllPages() {
    new FeatureOverviewPage(reportResult, configuration).generatePage();
    for (Feature feature : reportResult.getAllFeatures()) {
      new FeatureReportPage(reportResult, configuration, feature).generatePage();
    }

    new TagOverviewPage(reportResult, configuration).generatePage();
    for (TagObject tagObject : reportResult.getAllTags()) {
      new TagReportPage(reportResult, configuration, tagObject).generatePage();
    }

    new StepOverviewPage(reportResult, configuration).generatePage();
  }

  private void copyResources(String resourceLocation, String... resources) {
    for (String resource : resources) {
      File tempFile = new File(configuration.getReportDirectory().getAbsoluteFile(), resource);
      // don't change this implementation unless you verified it works on Jenkins
      try {
        FileUtils.copyInputStreamToFile(
          this.getClass().getResourceAsStream("/" + resourceLocation + "/" + resource), tempFile);
      } catch (IOException e) {
        throw new ValidationException(e);
      }
    }
  }

  private void generateErrorPage(Exception exception) {
    LOG.info(exception);
    ErrorPage errorPage = new ErrorPage(reportResult, configuration, exception, jsonFiles);
    errorPage.generatePage();
  }
}
