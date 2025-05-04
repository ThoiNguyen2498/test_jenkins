package com.cogover.template.server.worker;

import com.cogover.threadpool.ThreadPoolWorkerManager;
import com.cogover.threadpool.worker.ThreadPoolWorker;
import lombok.extern.log4j.Log4j2;

/**
 * @author huydn on 2/6/24 11:18
 */
@Log4j2
public class SampleWorker extends ThreadPoolWorker {

    public SampleWorker(String name) {
        super(name, 20000);
    }

    public static void execute(SampleJob job) {
        ThreadPoolWorkerManager.getInstance().pubJob(job, SampleWorker.class);
    }

}
