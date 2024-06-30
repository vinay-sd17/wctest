import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CompletableFuture;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@SpringBootTest
public class ProcessServiceTest {

    @Mock
    private YourService yourService;

    @InjectMocks
    private ProcessService processService;

    public ProcessServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testProcess() {
        User user = new User();
        Article article = new Article();
        History history = new History();

        when(yourService.saveUser(user)).thenReturn(CompletableFuture.completedFuture(null));
        when(yourService.saveArticle(article)).thenReturn(CompletableFuture.completedFuture(null));
        when(yourService.saveHistory(history)).thenReturn(CompletableFuture.completedFuture(null));

        processService.process(user, article, history);

        verify(yourService).saveUser(user);
        verify(yourService).saveArticle(article);
        verify(yourService).saveHistory(history);
    }
}
