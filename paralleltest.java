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









import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import static org.mockito.Mockito.verify;

@SpringBootTest
public class YourServiceTest {

    @Mock
    private UserRepository userRepo;

    @Mock
    private ArticleRepository articleRepo;

    @Mock
    private HistoryRepository historyRepo;

    @InjectMocks
    private YourService yourService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(10);
        taskExecutor.setMaxPoolSize(50);
        taskExecutor.setQueueCapacity(100);
        taskExecutor.setThreadNamePrefix("Async-");
        taskExecutor.initialize();
        yourService = new YourService();
    }

    @Test
    public void testSaveUser() {
        User user = new User();
        yourService.saveUser(user);
        verify(userRepo).save(user);
    }

    @Test
    public void testSaveArticle() {
        Article article = new Article();
        yourService.saveArticle(article);
        verify(articleRepo).save(article);
    }

    @Test
    public void testSaveHistory() {
        History history = new History();
        yourService.saveHistory(history);
        verify(historyRepo).save(history);
    }
}

