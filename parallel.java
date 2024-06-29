Step 1: Enable Asynchronous Processing
In your main application class or a configuration class, enable asynchronous processing:

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class YourApplication {
    public static void main(String[] args) {
        SpringApplication.run(YourApplication.class, args);
    }
}


Step 2: Define Asynchronous Methods
In your service class, define asynchronous methods for saving entities. You can mark these methods with @Async.

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;

@Service
public class YourService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private ArticleRepository articleRepo;

    @Autowired
    private HistoryRepository historyRepo;

    @Async
    @Transactional
    public CompletableFuture<Void> saveUser(User user) {
        userRepo.save(user);
        return CompletableFuture.completedFuture(null);
    }

    @Async
    @Transactional
    public CompletableFuture<Void> saveArticle(Article article) {
        articleRepo.save(article);
        return CompletableFuture.completedFuture(null);
    }

    @Async
    @Transactional
    public CompletableFuture<Void> saveHistory(History history) {
        historyRepo.save(history);
        return CompletableFuture.completedFuture(null);
    }
}


Step 3: Use CompletableFuture to Run Methods in Parallel
In your controller or another service method, use CompletableFuture to call these methods in parallel.

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.concurrent.CompletableFuture;

@Controller
public class YourController {

    @Autowired
    private YourService yourService;

    public void saveAll(User user, Article article, History history) {
        CompletableFuture<Void> userFuture = yourService.saveUser(user);
        CompletableFuture<Void> articleFuture = yourService.saveArticle(article);
        CompletableFuture<Void> historyFuture = yourService.saveHistory(history);

        // Wait until all futures are done
        CompletableFuture.allOf(userFuture, articleFuture, historyFuture).join();
    }
}
