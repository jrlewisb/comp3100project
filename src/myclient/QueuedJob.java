public class QueuedJob implements Comparable<QueuedJob> {
    Job job;
    int queuePos;

    public QueuedJob(Job job, int queuePos) {
        this.job = job;
        this.queuePos = queuePos;
    }

    @Override
    public int compareTo(QueuedJob other) {
        if (this.job.estRunTime < other.job.estRunTime) {
            return -1;
        } else if (this.job.estRunTime > other.job.estRunTime) {
            return 1;
        } else {
            return 0;
        }
    }
}