package com.inspur.eip.service;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.ConnectionMonitor;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;
import com.alibaba.fastjson.JSONObject;
import com.inspur.eip.entity.fw.Firewall;
import com.inspur.eip.exception.EipInternalServerException;
import com.inspur.eip.util.constant.ErrorStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class FireWallCommondService {


    @Autowired
    private FirewallService firewallService;

    private Connection connection;
    private Session session;
    private BufferedReader stdout;
    private PrintWriter printWriter;
    private BufferedReader stderr;
    private boolean bConnect = false;
    private ExecutorService service = Executors.newFixedThreadPool(3);


    private void initConnection(String hostName, String userName, String passwd) throws Exception {
        connection = new Connection(hostName);
        connection.connect();

        boolean authenticateWithPassword = connection.authenticateWithPassword(userName, passwd);
        if (!authenticateWithPassword) {
            throw new RuntimeException("Authentication failed. Please check hostName, userName and passwd");
        }
        ConnectionMonitor connectionMonitor = new ConnectionMonitor() {
            @Override
            public void connectionLost(Throwable reason) {
                bConnect = false;
                log.info("Connection to fireWall lost, reason:{}", reason.getCause());
            }
        };
        bConnect = true;
        connection.addConnectionMonitor(connectionMonitor);
        initSession();
    }

    private void initSession() throws Exception {

        session = connection.openSession();
        session.requestPTY("vt100", 80, 24, 640, 480, null);
        //session.requestDumbPTY();
        session.startShell();
        //session.getState();
        stdout = new BufferedReader(new InputStreamReader(new StreamGobbler(session.getStdout()), StandardCharsets.UTF_8));
        stderr = new BufferedReader(new InputStreamReader(new StreamGobbler(session.getStderr()), StandardCharsets.UTF_8));
        printWriter = new PrintWriter(session.getStdin());

        TimeUnit.MILLISECONDS.sleep(1000);
    }

    /**
     *
     * @param fireWallId
     * @param cmd
     * @param expectStr  表示期望返回的字符串，如果返回的这行没有Error字段，可以通过该参数控制额外的异常情况
     * @return
     */
    synchronized String execCustomCommand(String fireWallId, String cmd, String expectStr) {

        try {
            if (!bConnect) {
                Firewall firewall = firewallService.getFireWallById(fireWallId);
//                initConnection("10.110.29.206", "test", "test");
                initConnection(firewall.getIp(), firewall.getUser(), firewall.getPasswd());
                log.info("firewall connection reinit.");
            }
            printWriter.write(cmd + "\r\n");
            printWriter.flush();

            String line;
            String retStr = null;
            while (null != (line = stdout.readLine())) {
                log.debug(line);
                if ((null != expectStr && line.contains(expectStr)) || (line.contains("Error"))) {
                    retStr = line;
                    if (line.contains("Error")) {
                        log.info(line);
                    }
                }

                if (line.contains("end")) {
                    if (line.contains("#")) {
                        String endStr = stdout.readLine();
                        if (null == retStr && null != expectStr) {
                            if (null != endStr && endStr.contains(expectStr)) {
                                retStr = endStr;
                            }
                        }
                        log.info("Command return:{}, end string:{}", retStr, endStr);
                        return retStr;
                    } else {
                        log.error("Firewall not connect, end string:{}.");
                        //close();
                        return "ERROR";
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error when init :", e);
        }
        log.error("Commond get no return.");
        return null;
    }

    /**
     * show statistics address entryName
     * @param fireWallId
     * @param cmd
     * @return
     */
    public synchronized JSONObject cmdShowStasiticsAddress(String fireWallId, String cmd) {
        try {
            JSONObject json = new JSONObject();
            if (!bConnect) {
                Firewall firewall = firewallService.getFireWallById(fireWallId);
//                initConnection("10.110.29.206", "test", "test");
                initConnection(firewall.getIp(), firewall.getUser(), firewall.getPasswd());
                log.info("firewall connection reinit.");
            }
            printWriter.write(cmd + "\r\n");
            printWriter.flush();

            String line;
            StringBuffer upLine= new StringBuffer();
            StringBuffer downLine = new StringBuffer();
            while (null != (line = stdout.readLine()) && !line.contains("end")) {
                log.debug(line);
                if (StringUtils.isNotBlank(line)){
                    if (line.startsWith("UP")) {
                        json.put("UP",upLine.append(line));
                    }else if (line.startsWith("DOWN")){
                        json.put("DOWN",downLine.append(line));
                    } else if (line.startsWith(" --More--") &&line.contains("\b") && line.contains("DOWN")){
                        json.put("DOWN",downLine.append(line.substring(line.lastIndexOf("\b")+1)));
                    } else if (line.contains("^-----")){
                        log.error(ErrorStatus.FIREWALL_UNRECOGNIZED_COMMAND+":{}",line);
                        throw new EipInternalServerException(ErrorStatus.FIREWALL_UNRECOGNIZED_COMMAND.getCode(),ErrorStatus.FIREWALL_UNRECOGNIZED_COMMAND.getMessage());
                    }
                }
            }
            if (json.size()<2 ){
                log.error(ErrorStatus.SC_FIREWALL_SERVER_ERROR +"show result:{}",json);
                throw new EipInternalServerException(ErrorStatus.SC_FIREWALL_SERVER_ERROR.getCode(),ErrorStatus.SC_FIREWALL_SERVER_ERROR.getMessage());
            }
            return json;
        } catch (Exception e) {
            log.error("Commond get no return.:{}",e);
        }
        return null;
    }


    private void close() {
        IOUtils.closeQuietly(stdout);
        IOUtils.closeQuietly(stderr);
        IOUtils.closeQuietly(printWriter);
        session.close();
        connection.close();
        bConnect = false;
    }

    public static void main(String[] args) {

        FireWallCommondService sshAgent = new FireWallCommondService();
        long currentTimeMillis = System.currentTimeMillis();
//
//        String ret = sshAgent.execCustomCommand("1",
//                "configure\r"
//                        + "ip vrouter trust-vr\r"
//                        + "dnatrule from ipv6-any to 1111::2222 service any trans-to 22.11.22.11\r"
//                        + "end",
//                "ID=");
        String ret = sshAgent.execCustomCommand("1",
                "configure\r"
                        + "ip vrouter trust-vr\r"
                        + "no dnatrule id 95\r"
                        + "end",
                null);


//        String ret = sshAgent.execCustomCommand("id", "configure\r"
//                +"service my-service1\r"
//                +"tcp dst-port 21 23\r"
//                +"exit\r"
//                +"policy-global\r"
//                +"rule\r"
//                +"src-addr any\r"
//                +"dst-ip 5.6.7.9/32\r"
//                +"service my-service1\r"
//                +"action permit\r"
//                +"end");
        if (null != ret) {
            System.out.print(ret);
        }
        long currentTimeMillis1 = System.currentTimeMillis();
        System.out.println("\r\nganymed-ssh2 time:" + (currentTimeMillis1 - currentTimeMillis));
        //sshAgent.close();
    }
}

