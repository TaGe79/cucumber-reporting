package net.masterthought.cucumber;

import static net.masterthought.cucumber.FileReaderUtil.getAbsolutePathFromResource;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import net.masterthought.cucumber.json.Element;
import net.masterthought.cucumber.json.Feature;
import net.masterthought.cucumber.json.support.TagObject;

public class TagsTest {

    private final Configuration configuration = new Configuration(new File(""), "testProject");

    private ReportResult reportResult;

    @Before
    public void setUpJsonReports() throws IOException {
        List<String> jsonReports = new ArrayList<String>();
        jsonReports.add(getAbsolutePathFromResource("net/masterthought/cucumber/tags.json"));
        List<Feature> features = new ReportParser(configuration).parseJsonResults(jsonReports);
        reportResult = new ReportResult(features);
    }

    @Test
    public void shouldGetTotalTagSteps() {
        assertThat(reportResult.getAllTagSteps(), is(4));
    }

    @Test
    public void shouldGetTotalTagPasses() {
        assertThat(reportResult.getAllPassesTags(), is(2));
    }

    @Test
    public void shouldGetTotalTagFails() {
        assertThat(reportResult.getAllFailsTags(), is(2));
    }

    @Test
    public void shouldGetTotalTagSkipped() {
        assertThat(reportResult.getAllSkippedTags(), is(0));
    }

    @Test
    public void shouldGetTotalTagPending() {
        assertThat(reportResult.getAllPendingTags(), is(0));
    }

    @Test
    public void shouldGetTotalTagScenarios() {
        assertThat(reportResult.getAllTagScenarios(), is(4));
    }

    @Test
    public void shouldgetTotalTagScenariosPassed() {
        assertThat(reportResult.getAllPassedTagScenarios(), is(2));
    }

    @Test
    public void shouldgetTotalTagScenariosFailed() {
        assertThat(reportResult.getAllFailedTagScenarios(), is(2));
    }

    @Test
    public void shouldGetTagInfoForTag1() {
        TagObject tagObject = reportResult.getAllTags().get(0);
        assertThat(tagObject.getTagName(), is("@tag1"));
    }

    @Test
    public void shouldGetTagInfoForTag2() {
        TagObject tagObject = reportResult.getAllTags().get(1);
        assertThat(tagObject.getTagName(), is("@tag2"));
    }

    @Test
    public void shouldGetTagScenariosForTag1() {
        List<Element> elements = reportResult.getAllTags().get(0).getElements();
        assertThat(elements.size(), is(2));
        Element firstElement = elements.get(0);
        Element secondElement = elements.get(1);
        assertThat(firstElement.getRawName(), is("scenario1 for tag1"));
        assertThat(secondElement.getRawName(), is("scenario2 for tag1"));
    }

    @Test
    public void shouldGetTagScenariosForTag2() {
        List<Element> elements = reportResult.getAllTags().get(1).getElements();
        assertThat(elements.size(), is(2));
        Element firstElement = elements.get(0);
        Element secondElement = elements.get(1);
        assertThat(firstElement.getRawName(), is("scenario1 for tag2"));
        assertThat(secondElement.getRawName(), is("scenario2 for tag2"));
    }
}
