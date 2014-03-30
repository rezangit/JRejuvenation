package jrejuvenation.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;

/**
 *
 * @author Reza Azizi (azizi.ra@gmail.com)
 * @version 1.0.1
 */

public class BaseNet extends ReceiverAdapter {

    private JChannel channel;
    //ReplicatedHashMap<String, String> map = null;
    private String user_name = System.getProperty("user.name", "n/a");
    private String path = System.getProperty("user.dir", "n/a");
    private MasterQueryThread querymasterThread;
    private TaskWorker task;
    private int masterID = 0;
    private Random randomIdGenerator;
    private DynaClassLoader DCLoader;
    private HashMap<Address, Command> responseList = new HashMap<Address, Command>();
    private HashMap data = new HashMap();
    private boolean halt;
    private boolean eventLoop = true;
    BufferedReader in;

    public void start() throws RuntimeException, Exception {


        System.out.println("Network Setting(tcp.xml) base DIR=" + path);

        randomIdGenerator = new Random();
        DCLoader = new DynaClassLoader();
        DCLoader.loader();


        channel = new JChannel(path + "/tcp.xml"); // use the default config, udp.xml


        channel.setReceiver(this);
        channel.connect("RejuvenationCluster");


        halt = false;

        eventLoop();

        channel.close();

    }

    private void eventLoop() {
        in = new BufferedReader(new InputStreamReader(System.in));
        while (eventLoop) {
            try {
                System.out.println("Enter quit to exit! ");
                System.out.println("Enter run to run the deployed project! ");
                System.out.println("Enter halt to make jvm stack overflow! ");
                System.out.println("> ");
                System.out.flush();
                String line = in.readLine();

                if (line.startsWith("quit") || line.startsWith("exit")) {
                    System.exit(0);
                }
                if (line.startsWith("run")) {

                    querymasterThread = new MasterQueryThread();
                    querymasterThread.start();
                    System.out.println("Master VM Messaging started...");
                    continue;

                }
                if (line.startsWith("halt")) {
                    halt = true;
                    continue;
                }

                if (channel.isOpen()) {
                    System.out.println("Sending test message to all VM");
                    line = "[" + user_name + "] " + line;
                    channel.send(null, new Command(Command.NETWORK_TEST_MSG, line));
                }


            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }
    }

    public void makeBufferOverFlow() {

        String str = "this function is going to make stack memory overflow";
        makeBufferOverFlow();

    }

    @Override
    public void viewAccepted(View new_view) {

        System.out.println("cluster node changed: " + new_view);

        if (task == null) {
            task = new TaskWorker();
        }

        if (querymasterThread == null) {
            querymasterThread = new MasterQueryThread();
        }

        if (!task.isAlive()) {
            task.start();
            System.out.println("Task Thread Started");

        }

        if (masterID != 100) {

            if (!querymasterThread.isAlive()) {

                try {
                    querymasterThread.start();
                } catch (IllegalThreadStateException ex) {
                    querymasterThread = new MasterQueryThread();
                    querymasterThread.start();
                }

                System.out.println("Master VM Messaging started...");
            }

        }

    }

    @Override
    public void receive(Message msg) {

        Command cmd = null;

        cmd = (Command) msg.getObject();

        if (cmd.getCommand() == Command.QUERY_MASTER) {

            // for first time we choose a random masterID 
            // form 0  to 9, evry masterID > 10 is active
            // masterID and other are suspended IDs
            if (masterID == 0) {
                masterID = randomIdGenerator.nextInt(90);
                masterID++;
            }

            try {

                System.out.println("Master Id generated and send to all : " + masterID);
                Command scmd = new Command(Command.QUERY_MASTER_RESPOSE, masterID);
                channel.send(null, scmd);

            } catch (Exception ex) {
            }

            return;
        }


        /////////////////////////////////////////////////////////////////////// 


        if (cmd.getCommand() == Command.QUERY_MASTER_RESPOSE) {

            Address src = msg.getSrc();

            responseList.put(src, cmd);

            return;

        }

        /////////////////////////////////////////////////////////////////////////////


        if (cmd.getCommand() == Command.SYNC_DATA_MSG) {

            
            if (cmd.getData() == 100) {
                data = cmd.getMap();
            }

            return;

        }

        System.out.println("Receive test message from" + msg.getSrc() + ": " + cmd.getStr());


    }

    public class MasterQueryThread extends Thread {

        @Override
        public void run() {


            try {
                responseList.clear();

                Command scmd = new Command(Command.QUERY_MASTER, 0);
                channel.send(null, scmd);

            } catch (Exception ex) {
                Logger.getLogger(BaseNet.class.getName()).log(Level.SEVERE, null, ex);
            }

            while (channel.isConnected()) {

                View v = channel.getView();
                List<Address> list = v.getMembers();

                //System.out.println("thread-> list size= " + list.size() + "response size=" + responseList.size());
                if (list.size() == responseList.size()) {
                    int max = 0;
                    for (Address ad : list) {
                        if (responseList.get(ad).getData() > max) {
                            max = responseList.get(ad).getData();
                            if (masterID >= max) {
                                masterID = 100;
                                System.out.println("MasterQuery selected, thread is finished");
                                return;
                            }

                        }
                    }
                    System.out.println("MasterQuery not selected, thread is finished");
                    return;
                }

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(BaseNet.class.getName()).log(Level.SEVERE, null, ex);
                }

            }

        }
    }

    public void sendSyncData(HashMap map) {
        try {

            Command scmd = new Command(Command.SYNC_DATA_MSG, masterID,map);
            channel.send(null, scmd);


        } catch (Exception ex) {
            Logger.getLogger(BaseNet.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public class TaskWorker extends Thread {

        @Override
        public void run() {

            try {
                while (true) {

                    // this line is just for testing buffer over flow
                    if (halt == true) {
                        halt = false;
                        makeBufferOverFlow();
                    }

                    if (masterID == 100) {


                        DCLoader.OnStart(data);
                        DCLoader.RunStep(data);
                        DCLoader.OnStop(data);

                        sendSyncData(data);


                    } else {
                        try {
                            Thread.sleep(10000);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(BaseNet.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            } catch (Throwable ex) {
                System.out.println("error in task management service...");
                masterID = 0;
                eventLoop = false;
                channel.clearChannelListeners();
                channel.disconnect();
                channel.close();
                System.out.println("Press a key to Finishing all and starting a new service");                

            }
        }
    }
}
