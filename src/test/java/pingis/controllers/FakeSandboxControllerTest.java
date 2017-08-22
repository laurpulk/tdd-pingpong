package pingis.controllers;

import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Matchers.eq;


import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;

import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.Mockito.verify;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import pingis.config.OAuthProperties;
import pingis.config.SecurityConfig;
import pingis.services.sandbox.FakeSandboxRestService;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 *
 * @author juicyp
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {
  FakeSandboxController.class, SecurityConfig.class, OAuthProperties.class})
@WebAppConfiguration
@WebMvcTest(FakeSandboxController.class)
public class FakeSandboxControllerTest {
  
  @Autowired
  private MockMvc mvc;
  
  @MockBean
  private FakeSandboxRestService fakeSandboxMock;
  
  @Test
  public void resultStatusFailedWhenTestTaskInSubmission() throws Exception {
    UUID uuid = UUID.randomUUID();
    String url = "localhost";
    
    mvc.perform(post("/tasks.json")
        .param("token", uuid.toString())
        .param("notify", url))
        .andExpect(status().isOk());
    
    verify(fakeSandboxMock).postSubmissionResults(eq(uuid.toString()), eq(url));
  }
}