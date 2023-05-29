import java.io.*;
import java.net.*;
import java.lang.Math.*;
import java.util.Vector;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

public class Session {
    private boolean debug = true; // set to true to see debug output
    BufferedReader in;
    DataOutputStream out;
    Socket sock;
    SchedulingManager schedulingManager;

    public class LRRManager extends SchedulingManager {
        // Stateful member variables that we require for our algorithm - these should
        // only be accessed from within the algorithm and we provide a generic interface
        // to the session
        int robinNumber = 0;
        String largestType;

        Server getNextServer(Job job) {
            Vector<Server> serversOfLargestType = new Vector<Server>();

            for (Server s : this.servers) {
                if (s.type.equals(largestType)) {
                    serversOfLargestType.add(s);
                }
            }

            this.robinNumber %= serversOfLargestType.size();
            return serversOfLargestType.get(this.robinNumber++);
        }

        void schedule(Job job) throws Exception {
            writeln("GETS Capable " + job.requirements());
            String[] serversRaw = handleData(in.readLine());
            if (responseEqual(".")) {
                if (this.servers == null) {
                    this.servers = sortServers(serversRaw); // sort the servers
                    this.largestType = this.servers[0].type; // assign the largest type
                }
                sendSCHD(job, getNextServer(job));
            }
        }

    }

    public class FFManager extends SchedulingManager {
        Server getNextServer(Job job) throws Exception {
            for (int i = 0; i < this.servers.length; i++) {
                Server s = this.servers[i];
                if (!s.state.strip().equalsIgnoreCase("inactive")) {
                    if (s.waitingJobs > 0) {
                        Vector<Job> jobsOnServer = LSTJ(s);
                    }

                    debugln("sumcores: " + job.coresReq + " and server has: " + s.cores + " available!");
                    debugln("sumMemory: " + job.memoryReq + " and server has: " + s.memory + " available!");
                    debugln("sumDisk: " + job.diskReq + " and server has: " + s.disk + " available!");
                    if ((s.cores - job.coresReq >= 0) && (s.memory - job.memoryReq >= 0)
                            && (s.disk - job.diskReq >= 0)) {
                        debugln("I CAN SCHEDULE IT HERE");
                        return s;
                    }

                } else {
                    return s;
                }
            }
            return this.servers[0];

        }

        void schedule(Job job) throws Exception {
            writeln("GETS Capable " + job.requirements());
            String[] serversRaw = handleData(in.readLine());
            this.servers = makeServerArray(serversRaw);
            sendSCHD(job, getNextServer(job));

        }

    }

    public class BFManager extends SchedulingManager {

        Server getNextServer(Job job) throws Exception {
            Server bestServer = null;
            for (int i = 0; i < this.servers.length; i++) {
                Server s = this.servers[i];
                if (!s.state.strip().equalsIgnoreCase("inactive")) {
                    if (s.waitingJobs > 0) {
                        Vector<Job> jobsOnServer = LSTJ(s);
                    }
                }
                if (s.cores < job.coresReq ||
                        (s.cores == job.coresReq && s.memory < job.memoryReq) ||
                        (s.cores == job.coresReq && s.memory == job.memoryReq && s.disk < job.diskReq)) {
                    continue;
                }
                if (bestServer == null) {
                    bestServer = s;
                }
                if (s.cores < bestServer.cores ||
                        (s.cores == bestServer.cores && s.memory < bestServer.memory) ||
                        (s.cores == bestServer.cores && s.memory == bestServer.memory && s.disk < bestServer.disk)) {
                    bestServer = s;
                }
            }
            return bestServer;

        }

        void schedule(Job job) throws Exception {
            writeln("GETS Capable " + job.requirements());
            String[] serversRaw = handleData(in.readLine());
            this.servers = makeServerArray(serversRaw);
            sendSCHD(job, getNextServer(job));

        }

    }

    public class FFQManager extends SchedulingManager {
        Server getNextServer(Job job) throws Exception {
            for (Server s : this.servers) {
                if (!s.state.strip().equalsIgnoreCase("inactive")) {
                    if ((s.cores - job.coresReq >= 0) && (s.memory - job.memoryReq >= 0)
                            && (s.disk - job.diskReq >= 0)) {
                        debugln("sumcores: " + job.coresReq + " and server has: " + s.cores + " available!");
                        debugln("sumMemory: " + job.memoryReq + " and server has: " + s.memory + " available!");
                        debugln("sumDisk: " + job.diskReq + " and server has: " + s.disk + " available!");
                        debugln("I CAN SCHEDULE IT HERE");
                        if (s.waitingJobs > 0) {
                            Vector<Job> jobsOnServer = LSTJ(s);
                        }
                        return s;
                    } else if (s.waitingJobs > 0) {
                        Vector<Job> jobsOnServer = LSTJ(s);
                    }
                } else {
                    return s;
                }
            }
            return null;
        }

        void schedule(Job job) throws Exception {
            writeln("GETS Capable " + job.requirements());
            String[] serversRaw = handleData(in.readLine());
            this.servers = makeServerArray(serversRaw);
            Server nextServer = getNextServer(job);
            if (nextServer != null) {
                sendSCHD(job, nextServer);
            } else {
                ENQJ();
            }

        }

    }

    public class BFQManager extends SchedulingManager {
        Server getNextServer(Job job) throws Exception {
            Server bestServer = null;
            for (int i = 0; i < this.servers.length; i++) {
                Server s = this.servers[i];
                if (!s.state.strip().equalsIgnoreCase("inactive")) {
                    if (s.waitingJobs > 0) {
                        Vector<Job> jobsOnServer = LSTJ(s);
                    }
                }
                if (s.cores < job.coresReq ||
                        (s.cores == job.coresReq && s.memory < job.memoryReq) ||
                        (s.cores == job.coresReq && s.memory == job.memoryReq && s.disk < job.diskReq)) {
                    continue;
                }
                if (bestServer == null) {
                    bestServer = s;
                }
                if (s.cores < bestServer.cores ||
                        (s.cores == bestServer.cores && s.memory < bestServer.memory) ||
                        (s.cores == bestServer.cores && s.memory == bestServer.memory && s.disk < bestServer.disk)) {
                    bestServer = s;
                }
            }
            return bestServer;
        }

        void schedule(Job job) throws Exception {
            writeln("GETS Capable " + job.requirements());
            String[] serversRaw = handleData(in.readLine());
            this.servers = makeServerArray(serversRaw);
            Server nextServer = getNextServer(job);
            if (nextServer != null) {
                sendSCHD(job, nextServer);
            } else {
                ENQJ();
            }
        }

    }

    public class WFQManager extends SchedulingManager {

        Server getNextServer(Job job) throws Exception {
            Server worstServer = null;
            for (int i = 0; i < this.servers.length; i++) {
                Server s = this.servers[i];
                if (!s.state.strip().equalsIgnoreCase("inactive")) {
                    if (s.waitingJobs > 0) {
                        Vector<Job> jobsOnServer = LSTJ(s);
                    }
                }

                if (job.coresReq > s.cores) {
                    continue;
                } else if (job.coresReq == s.cores && job.memoryReq > s.memory) {
                    continue;
                }

                if (worstServer == null) {
                    worstServer = s;
                }
                if (s.cores > worstServer.cores) {
                    if (s.cores == worstServer.cores) {
                        continue;
                    }
                    worstServer = s;
                }
            }
            return worstServer;
        }

        void schedule(Job job) throws Exception {
            writeln("GETS Capable " + job.requirements());
            String[] serversRaw = handleData(in.readLine());
            this.servers = makeServerArray(serversRaw);
            Server nextServer = getNextServer(job);
            if (nextServer != null) {
                sendSCHD(job, nextServer);
            } else {
                ENQJ();
                return;
            }
        }

    }

    public class BFLWTManager extends SchedulingManager {
        // Best Fit x Lowest Wait Time Manager
        // Best fit, once there is no available space for execution we queue on the
        // lowest wait time

        Server getNextServer(Job job) throws Exception {
            // IF available server (ie; a server can fit the job),
            // use best fit,
            // IF no available server,
            // use lowest wait time
            writeln("GETS Capable " + job.requirements());
            String[] serversRaw = handleData(in.readLine());
            this.servers = makeServerArray(serversRaw);
            Server bestServer = null;
            Server serverWithLowestWaitTime = null;
            int lowestWaitTime = Integer.MAX_VALUE;
            for (int i = 0; i < this.servers.length; i++) {
                Server s = this.servers[i];

                if (job.coresReq > s.cores || (job.coresReq == s.cores && job.memoryReq > s.memory)) {
                    writeln("EJWT " + s.type + " " + s.id);
                    int waitTime = Integer.valueOf(in.readLine());
                    if (waitTime < lowestWaitTime) {
                        serverWithLowestWaitTime = s;
                        lowestWaitTime = waitTime;
                    }
                    continue;
                }

                // Check if bestServer is null or current server has fewer remaining cores after
                // job allocation
                if (bestServer == null || (s.cores - job.coresReq) < (bestServer.cores - job.coresReq)) {
                    bestServer = s;
                }
            }

            if (bestServer == null) {
                return serverWithLowestWaitTime;
            } else {
                return bestServer;
            }
        }

        void schedule(Job job) throws Exception {
            Server nextServer = getNextServer(job);
            sendSCHD(job, nextServer);
        }

    }

    public Session(Socket sock, String algorithm) {
        try {
            this.sock = sock;
            in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            out = new DataOutputStream(sock.getOutputStream());

        } catch (Exception e) {
            System.out.println(e);
        }

        // if our session was successfully started, lets instanciate our
        // schedulingManager
        switch (algorithm) {
            case "lrr":
                this.schedulingManager = new LRRManager();
                break;
            case "ff":
                this.schedulingManager = new FFManager();
                break;
            case "bf":
                this.schedulingManager = new BFManager();
                break;
            case "ffq":
                this.schedulingManager = new FFQManager();
                break;
            case "bfq":
                this.schedulingManager = new BFQManager();
                break;
            case "wfq":
                this.schedulingManager = new WFQManager();
                break;
            case "ass":
                this.schedulingManager = new BFLWTManager();
                break;
        }

    }

    public void print(String s) {
        // System.out.println(s);
    }

    public void debugln(String s) {
        if (this.debug) {
            // System.out.println(s);
        }
    }

    public void debug(String s) {
        if (this.debug) {
            // System.out.print(s);
        }
    }

    public void writeln(String s) throws Exception {
        debugln("CLIENT SEND: " + s);
        out.write(s.concat("\n").getBytes());
        out.flush();
    }

    boolean responseEqual(String s) throws Exception {
        return s.equals(in.readLine());
    }

    String compareAndReturn(String s) throws Exception {
        String res = in.readLine();
        if (s.equals(res)) {
            return res;
        } else {
            throw new Exception();
        }
    }

    public boolean makeConnection() {
        try {
            writeln("HELO");
            if (responseEqual("OK")) {
                writeln("AUTH " + System.getProperty("user.name"));
                if (responseEqual("OK")) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            print(e.toString());
            return false;
        }
    }

    public void sendSCHD(Job job, Server server) throws Exception {
        String infoString = "SCHD " + job.id + " " + server.type + " " + server.id;
        debugln(infoString);
        writeln(infoString);
    }

    public String[] handleData(String header) throws Exception {
        debugln("HEADER RCVD: " + header);
        String[] headerData = header.split(" ");
        if (!headerData[0].equals("DATA")) {
            throw new Exception();
        }
        int amtLines = Integer.valueOf(headerData[1]);
        int amtBytes = Integer.valueOf(headerData[2]);
        // prepare to read
        writeln("OK");
        String[] lines = new String[amtLines];
        for (int i = 0; i < amtLines; i++) {
            lines[i] = in.readLine();
        }
        // after all data recieved
        writeln("OK");
        for (String l : lines) {
            debugln(l);
        }

        if (responseEqual(".")) {
            return lines;
        } else {
            throw new Exception();
        }

    }

    public void handleJOBN(EventData e) throws Exception {
        Job job = new Job(e.tokens);
        debugln(job.type);
        for (String s : e.tokens) {
            debug(s + ", ");
        } // debug
        debugln("\n");
        schedulingManager.schedule(job);
        if (!responseEqual("OK")) {
            throw new Exception();
        }

    }

    Server[] makeServerArray(String[] rawServers) {
        Server[] servers = new Server[rawServers.length];

        for (int i = 0; i < rawServers.length; i++) {
            servers[i] = new Server(rawServers[i]);
        }

        return servers;
        // end sort servers
    }

    Server[] sortServers(String[] rawServers) {
        Server[] serverArray = makeServerArray(rawServers);
        Arrays.sort(serverArray);
        return serverArray;
    }

    void handleJCPL(EventData e) throws Exception {
        // job completion
        // should just continue?
        //
        return;
    }

    void handleCHKQ(EventData e) throws Exception {

        writeln("DEQJ GQ 0");
        if (!responseEqual("OK")) {
            throw new Exception();
        }
        return;
    }

    void handleJOBP(EventData e) throws Exception {
        // just adding here in case we want to do some more advanced de-queuing but for
        // now lets just send it to JOBN
        handleJOBN(e);
    }

    Vector<Job> LSTJ(Server server) throws Exception {
        writeln("LSTJ " + server.type + " " + server.id);
        String[] jobStrings = handleData(in.readLine());
        Vector<Job> jobsVector = new Vector<Job>();
        for (String str : jobStrings) {
            debugln(str);
            String[] tokens = str.split(" ");
            String jobId = tokens[0];
            String jobState = tokens[1];
            String submitTime = tokens[2];
            String startTime = tokens[3];
            String estRunTime = tokens[4];
            String coresReq = tokens[5];
            String memoryReq = tokens[6];
            String diskReq = tokens[7];
            debugln("id: " + jobId + ", " + "State: " + jobState + ", " + "submitTime: " + submitTime + ", "
                    + "estRunTime: " + estRunTime + ", " + "coresReq: " + coresReq + ", " + "memoryReq: " + memoryReq
                    + ", " + "diskReq: " + diskReq);
            jobsVector.add(new Job(jobId, jobState, submitTime, estRunTime, coresReq, memoryReq, diskReq));
        }
        return jobsVector;
    }

    void ENQJ() throws Exception {
        writeln("ENQJ GQ");
    }

    public void handleEvent(String s) throws Exception {
        EventData e = new EventData(s);
        debugln(e.type);
        switch (e.type) {
            case "JOBN":
                handleJOBN(e);
                break;
            case "JOBP":
                handleJOBP(e); // handleJOBP(e); break;
            case "JCPL":
                handleJCPL(e);
                break;
            case "RESF": // handleRESF(e); break;
            case "RESR": // handleRESR(e); break;
            case "CHKQ":
                handleCHKQ(e);
                break;
            case "NONE": // not needed
        }
        writeln("REDY");
    }

    public void start() throws Exception {
        if (makeConnection() == true) // if successful connection
        {
            writeln("REDY"); // let the server know we are ready to begin the event loop
            String event;
            while (!(event = in.readLine()).equals("NONE")) {
                debugln(event);
                handleEvent(event);
            }
            writeln("QUIT");
            // out.close();
            if (responseEqual("QUIT")) {
                in.close();
            }
            return;
        }
    }
}
