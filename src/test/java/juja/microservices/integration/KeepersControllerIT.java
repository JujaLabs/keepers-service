package juja.microservices.integration;

import com.lordofthejars.nosqlunit.annotation.UsingDataSet;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Dmitriy Lyashenko
 */

@RunWith(SpringRunner.class)
public class KeepersControllerIT extends BaseIntegrationTest{

    private MockMvc mockMvc;

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @UsingDataSet(locations = "/datasets/oneKeeperInDB.json")
    public void addKeeperNotExistUUID() throws Exception {
        //Given
        String json = "{" +
                "  \"from\":\"bill\"," +
                "  \"uuid\":\"max\"," +
                "  \"direction\":\"SomeDirection\"" +
                "}";

        //Then
        mockMvc.perform(post("/keepers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest());
    }

    @Test
    @UsingDataSet(locations = "/datasets/oneKeeperInDB.json")
    public void addKeeperOk() throws Exception {
        //Given
        String json = "{" +
                "  \"from\":\"asdqwe\"," +
                "  \"uuid\":\"max\"," +
                "  \"direction\":\"SomeDirection\"" +
                "}";

        //Then
        mockMvc.perform(post("/keepers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

    }

    @Test
    @UsingDataSet(locations = "/datasets/initEmptyDb.json")
    public void addKeeperInEmptyDb() throws Exception {
        //Given
        String json = "{" +
                "  \"from\":\"bob\"," +
                "  \"uuid\":\"max\"," +
                "  \"direction\":\"SomeDirection\"" +
                "}";

        //Then
        mockMvc.perform(post("/keepers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest());

    }
}