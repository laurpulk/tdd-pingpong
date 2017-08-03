package pingis.controllers;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import pingis.Application;
import pingis.config.SecurityDevConfig;

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {Application.class, IndexController.class, SecurityDevConfig.class})
@WebAppConfiguration
@WebMvcTest(IndexController.class)
public class IndexControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    public void simpleIndexTest() throws Exception {
        performSimpleGetReguest("/", "<h1><strong>TDD-pingpong</strong> frontpage.</h1>");
    }

    private void performSimpleGetReguest(String uri, String content) throws Exception {
        mvc.perform(get(uri))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(content)));
    }
}
