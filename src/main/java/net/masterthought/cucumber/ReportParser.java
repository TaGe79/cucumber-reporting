package net.masterthought.cucumber;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import net.masterthought.cucumber.json.Feature;
import org.apache.commons.io.Charsets;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReportParser {

  private final Configuration configuration;

  public ReportParser(Configuration configuration) {
    this.configuration = configuration;
  }

  public List<Feature> parseJsonResults(List<String> jsonReportFiles) {
    return parseJsonResults(jsonReportFiles, false);
  }

  public List<Feature> parseJsonResults(List<String> jsonReportFiles, boolean rerun) {
    List<Feature> featureResults = new ArrayList<>();
    Gson gson = new Gson();

    for (int i = 0; i < jsonReportFiles.size(); i++) {
      String jsonFile = jsonReportFiles.get(i);
      try (Reader reader = new InputStreamReader(new FileInputStream(jsonFile), Charsets.UTF_8)) {
        Feature[] features = gson.fromJson(reader, Feature[].class);
        if (features == null) {
          throw new IllegalArgumentException(String.format("File '%s' does not contain features!", jsonFile));
        }
        setMetadata(features, jsonFile, i, rerun);

        featureResults.addAll(Arrays.asList(features));
      } catch (IOException | JsonSyntaxException e) {
        throw new ValidationException(e);
      }
    }

    return featureResults;
  }

  /**
   * Sets additional information and calculates values which should be calculated during object creation.
   */
  private void setMetadata(Feature[] features, String jsonFile, int jsonFileNo, boolean rerun) {
    for (Feature feature : features) {
      feature.setMetaData(jsonFile, jsonFileNo, rerun, configuration);
    }
  }
}
