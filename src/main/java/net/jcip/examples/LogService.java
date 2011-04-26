package net.jcip.examples;

import net.jcip.annotations.GuardedBy;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * LogService
 * <p/>
 * Adding reliable cancellation to LogWriter
 *
 * @author Brian Goetz and Tim Peierls
 * Minor changes for implementation example by Todd Cook
 *
 */
public class LogService {
    private final BlockingQueue<String> queue;
    private final LoggerThread loggerThread;
    private final PrintWriter writer;
    @GuardedBy("this")
    private boolean isShutdown;
    @GuardedBy("this")
    private int reservations;

    public LogService (Writer writer) {
        this.queue = new LinkedBlockingQueue<String> ();
        this.loggerThread = new LoggerThread ();
        this.writer = new PrintWriter (writer);
    }

    /**
     * Starts the LogService, logging thread and adds a shutdown hook.
     * NOTE: Because shutdown hooks run asynchronously, there is no way to
     * guarantee that shutdown data will be captured in the log, if this
     * shutdown hook is used, hence we have moved it to the Configurator class.
     * @see com.cookconsulting.util.configuration.Configurator
     */
    public void start () {
        /*
        // see method documentation comment above
        Runtime.getRuntime ().addShutdownHook (new Thread() {
            public void run () {
                try {
                    LogService.this.write("Shutting down");
                    LogService.this.stop ();
                } catch (Exception e) {
                    e.printStackTrace ();
                }
            }
        });
        */
        loggerThread.start ();
    }

    public void stop () {
        synchronized (this) {
            isShutdown = true;
            // let the queue drain
            try {
                Thread.currentThread ().sleep (1000);
            } catch (InterruptedException e) {
                e.printStackTrace ();
            }
            writer.close ();
        }
        loggerThread.interrupt ();
    }

    public void log (String msg) throws InterruptedException {
        synchronized (this) {
            if (isShutdown) {
                throw new IllegalStateException (/*...*/);
            }
            ++reservations;
        }
        queue.put (msg);
    }

    private class LoggerThread extends Thread {
        public void run () {
            try {
                while (true) {
                    try {
                        synchronized (LogService.this) {
                            if (isShutdown && reservations == 0)
                                break;
                        }
                        String msg = queue.take ();
                        synchronized (LogService.this) {
                            --reservations;
                        }
                        writer.println (msg);
                    } catch (InterruptedException e) { /* retry */
                    }
                }
            } finally {
                writer.close ();
            }
        }
    }
}

