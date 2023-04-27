package Services;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SynchronizationScheduler implements ServletContextListener {

    private ScheduledExecutorService scheduler;
    static int i=1;
    @Override
    public void contextInitialized(ServletContextEvent event) {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(new MyTask(), 0, 20, TimeUnit.SECONDS);
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        scheduler.shutdownNow();
    }

    private class MyTask implements Runnable {
        @Override
        public void run() {
            try {
                System.out.println("Zoho-Social--->Sync starting...");
                SycronizationService.syncUnreadMessages();
                SycronizationService.syncConnectionDetails();
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }
}
