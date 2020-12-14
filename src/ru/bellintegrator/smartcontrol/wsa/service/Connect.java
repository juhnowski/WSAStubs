package ru.bellintegrator.smartcontrol.wsa.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import static java.util.concurrent.TimeUnit.SECONDS;
import ru.bellintegrator.smartcontrol.wsa.utils.Logger;

public class Connect {
	private Logger _logger;
    private ApplianceService appSrv;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    
    public Connect(ApplianceService appSrv) {
    	_logger.debug("Create Connect for ApplianceService="+appSrv.toString());
        this.appSrv = appSrv;
    }
    
    public void pingForAMinute() {
        final Runnable ping = new Runnable() {
            @Override
            public void run() {
                boolean isRepeat = false;
                String EXEC_STRING = getPingCommandString();
                while (isRepeat) {
                    StringBuilder result = new StringBuilder();
                    try {
                        Runtime r = Runtime.getRuntime();
                        Process p = r.exec(EXEC_STRING);
                        BufferedReader in = new BufferedReader(
                                new InputStreamReader(p.getInputStream()));
                        String line = null;
                        while ((line = in.readLine()) != null) {
                            result.append(line);
                        }
                        isRepeat = parseResult(result.toString());
                    } catch (IOException e) {
                        isRepeat = true;
                    }

                    if ((isRepeat) && (appSrv.getConnectionState() == ConnectionState.CONNECTED)) {
                        appSrv.changeConnectionState(ConnectionState.DISCONNECTED);
                    }
                }

                appSrv.changeConnectionState(ConnectionState.CONNECTED);
            }

            /**
             * Вариант дисконнекта $ ping -c 2 10.201.206.103 PING
             * 10.201.206.103 (10.201.206.103): 56 data bytes Request timeout
             * for icmp_seq 0
             *
             * --- 10.201.206.103 ping statistics --- 2 packets transmitted, 0
             * packets received, 100.0% packet loss
             *
             * Вариант соединения $ ping -c 2 10.201.206.103 PING 10.201.206.103
             * (10.201.206.103): 56 data bytes 64 bytes from 10.201.206.103:
             * icmp_seq=0 ttl=62 time=41.729 ms 64 bytes from 10.201.206.103:
             * icmp_seq=1 ttl=62 time=38.033 ms
             *
             * --- 10.201.206.103 ping statistics --- 2 packets transmitted, 2
             * packets received, 0.0% packet loss round-trip min/avg/max/stddev
             * = 38.033/39.881/41.729/1.848 ms
             *
             * @param cmdResult
             * @return
             */
            private boolean parseResult(String cmdResult) {
                String[] tmp = cmdResult.split(" packets received,");
                String[] tmp1 = tmp[0].split(" ");
                Integer packetsReceived = Integer.parseInt(tmp1[tmp1.length - 1]);
                return packetsReceived > 0;
            }
        };

        final ScheduledFuture<?> pingHandle = scheduler.scheduleAtFixedRate(ping, 0, 60, SECONDS);

        scheduler.schedule(new Runnable() {
            @Override
            public void run() {
                pingHandle.cancel(true);
            }
        }, Long.MAX_VALUE, SECONDS);
    }

    public void receiveConfig() throws WSAException {

        final Runnable receive = new Runnable() {
            @Override
            public void run() {

                String EXEC_STRING = getReceiveConfigCommandString();

                    StringBuilder result = new StringBuilder();
                    try {
                        Runtime r = Runtime.getRuntime();
                        Process p = r.exec(EXEC_STRING);
                        BufferedReader in = new BufferedReader(
                                new InputStreamReader(p.getInputStream()));
                        String line = null;
                        while ((line = in.readLine()) != null) {
                            result.append(line);
                        }
                    } catch (IOException e) {
                        appSrv.changeConnectionState(ConnectionState.DISCONNECTED);
                    }
                    if (parseResult(result.toString())) {
                        if(((appSrv.getState() == State.INIT) || ((appSrv.getState() == State.ERROR)))&&(appSrv.getConnectionState() == ConnectionState.CONNECTED)) {
                            appSrv.changeState(State.READY);
                        }
                    } else {
                        appSrv.changeState(State.ERROR);
                    }
            }

            //TODO: нужно вызвать парсер Валеры для нового конфига
            private boolean parseResult(String cmdResult) {
                return true;
            }
        };

        final ScheduledFuture<?> pingHandle = scheduler.scheduleAtFixedRate(receive, 0, 60, SECONDS);

        scheduler.schedule(new Runnable() {
            @Override
            public void run() {
                pingHandle.cancel(true);
            }
        }, Long.MAX_VALUE, SECONDS);
    }

    public void sendConfig() throws WSAException {

        final Runnable send = new Runnable() {
            @Override
            public void run() {
            	_logger.debug("send.run()");
                //TODO: сгенерировать конфиг и сохранить его как файл на диск для отправки

                String EXEC_STRING = getSendConfigCommandString();
                _logger.debug("EXEC_STRING=" + EXEC_STRING);
                
                StringBuilder result = new StringBuilder();
                _logger.debug("_________ command send start ______________");
                
                try {
                    Runtime r = Runtime.getRuntime();
                    Process p = r.exec(EXEC_STRING); 
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(p.getInputStream()));
                    String line = null;
                    while ((line = in.readLine()) != null) {
                        result.append(line);
                    }
                    
                		_logger.debug("_________ command send done ______________");
                		
                } catch (IOException e) {
                		e.printStackTrace();
                    appSrv.changeConnectionState(ConnectionState.DISCONNECTED);
                }
                
                if (parseResult(result.toString())) {
                    if((appSrv.getState() == State.INIT)&&(appSrv.getConnectionState() == ConnectionState.CONNECTED)) {
                        appSrv.changeState(State.READY);
                    }
                } else {
                    appSrv.changeState(State.ERROR);
                }
            }

            //TODO: analyze command output
            private boolean parseResult(String cmdResult) {
                return true;
            }
        };

        final ScheduledFuture<?> pingHandle = scheduler.scheduleAtFixedRate(send, 0, 60, SECONDS);

        scheduler.schedule(new Runnable() {
            @Override
            public void run() {
                pingHandle.cancel(true);
            }
        }, Long.MAX_VALUE, SECONDS);
    }

    /**
     * Генерация строки команды пинга
     *
     * @return команда
     */
    private String getPingCommandString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ping ");
        sb.append(appSrv.appliance.getUrl());
        return sb.toString();
    }

    /**
     * Генерация строки запроса конфига с Appliance scp -i /path/to/ssh_key
     * Config_file.xml smart@10.201.206.103:configuration/
     *
     * @return команда
     */
    private String getReceiveConfigCommandString() {
        StringBuilder sb = new StringBuilder();
        sb.append("scp -i ");
        sb.append(appSrv.getPathSSH());
        sb.append(" ");
        sb.append(appSrv.getPathConfig());
        sb.append(" ");
        sb.append(appSrv.appliance.getAdminUser());
        sb.append("@");
        sb.append(appSrv.appliance.getUrl());
        sb.append(":configuration/");
        return sb.toString();
    }

    /**
     * Генерация строки команды для применения конфигурационного файла ssh -i
     * /path/to/ssh_key smart@10.201.206.103 'loadconfig Config_file.xml; commit
     * Yes'
     *
     * @return команда
     */
    private String getSendConfigCommandString() {
    	_logger.debug("getSendConfigCommandString()");
        StringBuilder sb = new StringBuilder();
        sb.append("ssh -i ");
        sb.append(appSrv.getPathSSH());
    	_logger.debug("1");
        sb.append(" ");
        sb.append(appSrv.appliance.getAdminUser());
    	_logger.debug("2");
        sb.append("@");
        sb.append(appSrv.appliance.getUrl());
    	_logger.debug("3");
        sb.append(" 'loadconfig ");
        sb.append(" ");
        sb.append(appSrv.getPathConfig());
        sb.append("; commit Yes'");
        _logger.debug("getSendConfigCommandString() Command: " + sb.toString());
        return sb.toString();
    }

}
