package pingis.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.context.WebApplicationContext;
import pingis.entities.tmc.TmcSubmission;
import pingis.entities.tmc.TmcSubmissionStatus;
import pingis.repositories.TmcSubmissionRepository;
import pingis.Application;
import pingis.config.SecurityDevConfig;
import pingis.config.WebSocketSecurityConfig;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import static org.mockito.BDDMockito.*;
import org.springframework.test.annotation.DirtiesContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import pingis.entities.Task;
import pingis.entities.TaskInstance;
import pingis.entities.tmc.Logs;
import pingis.entities.tmc.ResultStatus;
import pingis.entities.tmc.TestOutput;
/**
 * Created by dwarfcrank on 7/28/17.
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = 
        {Application.class, TmcSubmissionController.class, SecurityDevConfig.class, WebSocketSecurityConfig.class})
@DirtiesContext
@WebAppConfiguration
@WebMvcTest(TmcSubmissionController.class)
public class TmcSubmissionControllerTest {
    @Autowired
    private WebApplicationContext context;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private TmcSubmissionRepository submissionRepository;
    
    private String testOutput;

    // TODO: Make this generate random data. This is a separate method just to please checkstyle.
    private ResultActions performMockRequest(UUID submissionId) throws Exception {
        return mvc.perform(
                post("/submission-result")
                        .param("test_output", testOutput)
                        .param("stdout", "test_stdout")
                        .param("stderr", "test_stderr")
                        .param("validations", "test_validations")
                        .param("vm_log", "test_vm_log")
                        .param("token", submissionId.toString())
                        .param("status", "finished")
                        .param("exit_code", "0"));
    }
    
    @Before
    public void initializeTestOutput() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        
        TestOutput top = new TestOutput();
        top.setStatus(ResultStatus.PASSED);
        top.setTestResults(new ArrayList<>());
        
        Logs logs = new Logs();
        logs.setStderr("stderr".getBytes());
        logs.setStdout("stdout".getBytes());
        top.setLogs(logs);
        
        this.testOutput = mapper.writeValueAsString(top);
    }

    @Test
    public void doubleSubmitReturnsBadRequest() throws Exception {
        UUID submissionId = UUID.randomUUID();
        TmcSubmission submission = createSubmission();
        submission.setId(submissionId);

        given(submissionRepository.findOne(submissionId))
                .willReturn(submission);

        performMockRequest(submissionId)
                .andExpect(status().isOk());

        performMockRequest(submissionId)
                .andExpect(status().isBadRequest());

        ArgumentCaptor<TmcSubmission> submissionCaptor = ArgumentCaptor.forClass(TmcSubmission.class);
        verify(submissionRepository, times(2)).findOne(submissionId);
        verify(submissionRepository, times(1)).save(submissionCaptor.capture());
        verifyNoMoreInteractions(submissionRepository);
    }

    @Test
    public void returnsNotFoundWithInvalidToken() throws Exception {
        UUID submissionId = UUID.randomUUID();
        TmcSubmission submission = createSubmission();
        submission.setId(submissionId);

        given(submissionRepository.findOne(submissionId))
                .willReturn(null);

        performMockRequest(submissionId)
                .andExpect(status().isNotFound());

        verify(submissionRepository, times(1)).findOne(submissionId);
        verifyNoMoreInteractions(submissionRepository);
    }

    @Test
    public void testWithValidToken() throws Exception {
        UUID submissionId = UUID.randomUUID();
        TmcSubmission submission = createSubmission();
        submission.setId(submissionId);

        given(submissionRepository.findOne(submissionId))
                .willReturn(submission);

        performMockRequest(submissionId)
                .andExpect(status().isOk());

        ArgumentCaptor<TmcSubmission> submissionCaptor = ArgumentCaptor.forClass(TmcSubmission.class);
        verify(submissionRepository, times(1)).findOne(submissionId);
        verify(submissionRepository, times(1)).save(submissionCaptor.capture());
        verifyNoMoreInteractions(submissionRepository);

        TmcSubmission captured = submissionCaptor.getValue();
        
        assertSubmission(captured, submissionId);
    }
    
    private TmcSubmission createSubmission() {
        TmcSubmission submission = new TmcSubmission();

        submission.setStatus(TmcSubmissionStatus.PENDING);

        Task task = new Task(0,null,null,null,null,null,0,0);
        TaskInstance ti = new TaskInstance(null,null,task);
        submission.setTaskInstance(ti);
        return submission;
    }
    
    private void assertSubmission(TmcSubmission captured, UUID submissionId) {
        assertEquals((int)captured.getExitCode(), 0);
        assertEquals(captured.getStdout(), "test_stdout");
        assertEquals(captured.getStderr(), "test_stderr");
        assertEquals(captured.getValidations(), "test_validations");
        assertEquals(captured.getVmLog(), "test_vm_log");
        assertEquals(captured.getStatus(), TmcSubmissionStatus.FINISHED);
        assertEquals(captured.getId(), submissionId);
        
        TestOutput top = captured.getTestOutput();
        assertEquals(top.getStatus(), ResultStatus.PASSED);
        assertNotNull(top.getTestResults());
        assertEquals("stderr", new String(top.getLogs().getStderr()));
        assertEquals("stdout", new String(top.getLogs().getStdout()));
    }
}
