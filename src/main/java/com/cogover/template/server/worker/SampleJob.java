package com.cogover.template.server.worker;

import com.cogover.threadpool.job.ThreadPoolJob;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author huydn on 2/6/24 11:20
 */
@Log4j2
@Getter
@Setter
public class SampleJob extends ThreadPoolJob implements Runnable {

    private static final AtomicLong JOB_ID_GENERATOR = new AtomicLong(0);

    private final long id;
    private SampleUser user;

    public SampleJob(SampleUser user) {
        super(-1);
        id = JOB_ID_GENERATOR.getAndIncrement();
        this.user = user;

        //chọn Woker để chạy theo từng user để đảm bảo các job đến cùng 1 user sẽ được chạy đúng thứ tự
        //this.workerChooseIndex = Math.abs(user.getUserId().hashCode());
        int hashCode = user.getUserId().hashCode();
        if (hashCode == Integer.MIN_VALUE) {
            this.workerChooseIndex = 0;
        } else {
            //do abs của Integer.MIN_VALUE = Integer.MIN_VALUE
            this.workerChooseIndex = Math.abs(hashCode);
        }
    }

    @Override
    public void run() {
        log.info("Job id={}, from user={} is running...", id, user.getUserId());
    }

    @Override
    public void timeout() {
        log.info("ProcessJob {} timeout", id);
    }
}
