package com.java.datingapp;

import com.java.datingapp.controller.DatingAppController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.annotation.PostConstruct;
import java.nio.charset.Charset;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DatingappApplication.class)
public class DatingAppControllerTest {

    private static final Logger log = LoggerFactory.getLogger(DatingAppControllerTest.class);
    public final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));
    private MockMvc webReqMockMvc;
    @Autowired
    private DatingAppController datingAppController;

    @PostConstruct
    public void setup() {

        MockitoAnnotations.initMocks(this);
        this.webReqMockMvc = MockMvcBuilders.standaloneSetup(datingAppController).build();
    }

    /**
     * Search By User Name - Status should be OK(200) and content to match the
     * mockString
     *
     * @throws Exception
     */
    @Test
    public void searchByUserName() throws Exception {

        String mockMatchingUser = "[\"UserA\",\"UserD\"]";

        String response = webReqMockMvc.perform(get("/datingapp/searchMatch/" + "UserB").contentType(APPLICATION_JSON_UTF8))
                .andExpect(status().isOk()).andExpect(content().json(mockMatchingUser)).andReturn().getResponse().getContentAsString();

        log.info("Response returned for searchByUserName {} ", response);
    }

    /**
     * Search By Invalid User Name - Searched user not found in database, Status
     * Not Found(404)
     *
     * @throws Exception
     */
    @Test
    public void searchByInvalidUser() throws Exception {
        webReqMockMvc.perform(get("/datingapp/searchMatch/" + "TestUser").contentType(APPLICATION_JSON_UTF8))
                .andExpect(status().isNotFound()).andExpect(content().string("Error occurred - Couldn't find the given user in database"));
    }
}
