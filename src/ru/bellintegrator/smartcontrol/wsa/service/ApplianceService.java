package ru.bellintegrator.smartcontrol.wsa.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import ru.bellintegrator.smartcontrol.core.api.dictionary.StatusEnum;
import ru.bellintegrator.smartcontrol.entity.system.WsaAppliance;
import ru.bellintegrator.smartcontrol.entity.user.Task;
import ru.bellintegrator.smartcontrol.utils.TaskQueue;
import ru.bellintegrator.smartcontrol.utils.TaskUtils;
import ru.bellintegrator.smartcontrol.wsa.utils.JNDIStub;
import ru.bellintegrator.smartcontrol.wsa.utils.Logger;
import static java.util.concurrent.TimeUnit.SECONDS;

public class ApplianceService {
	public WsaAppliance appliance;
	private Logger _logger;
	private State state;
    private ConnectionState connectionState;
    private Connect conn;
    private InetAddress host;
    private Path pathSSH;
    private Path pathConfig;
    private TaskQueue queue;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    
    TaskUtils tu = new TaskUtils(); 
    
	public ApplianceService(WsaAppliance appliance){
        this.appliance = appliance;
        _logger.debug("ApplianceService for Appliance.id="+appliance.getId() + " has been created");        
    }
	
    public void init() {
    		_logger.debug("ApplianceService for Appliance.id="+appliance.getId() + " start init");  
    		queue = new TaskQueue();
    		changeState(State.INIT);
    		changeConnectionState(ConnectionState.DISCONNECTED);
        conn = new Connect(this);
        conn.pingForAMinute();
        setPathSSH();
        setPathConfig();
        _logger.debug("ApplianceService for Appliance.id="+appliance.getId() + " inited");  
        getTask();
        
    }
    
    public State getState() {
        return state;
    }

    public ConnectionState getConnectionState() {
        return connectionState;
    }

    protected void changeState(State state) {
    		_logger.debug("ApplianceService for Appliance.id=" + appliance.getId() + " change state: "+ this.state + " -> " + state);  
        this.state = state;

    }

    public void changeConnectionState(ConnectionState connectionState) {
    		_logger.debug("ApplianceService for Appliance.id=" + appliance.getId() + " change connection state: "+ this.connectionState + " -> " + connectionState);
        this.connectionState = connectionState;
        
        if ((getState() == State.INIT) || (getState() == State.ERROR)) {
        		if (getConnectionState() == ConnectionState.CONNECTED) {
        			try {
        				downloadSSHKey();
        			} catch (WSAException e) {
        				e.printStackTrace(System.out);
        			}
        		}
        }
    }
    
    public void downloadSSHKey() throws WSAException{
    	String STUB_SSH_KEY;
    	
    	try {
    		_logger.debug("Start download ssh key");
            
    		STUB_SSH_KEY = "-----BEGIN RSA PRIVATE KEY-----\n"
    	            + "MIIEowIBAAKCAQEArGW47rM0TlGgmCYvrdtTLVUU+1S8IzFCRx0XGtic51LpOGg5\n"
    	            + "Xp1sb4fIYxHPyQfQTng0ssRTFI8ykxt0oezlT13OcVhEUplR5iw+faPAbuAZXQFk\n"
    	            + "dA3dFuaiU/pkwuVCYtOe1rBp46SbTuSGdDghMfFumqqZgLGyRS/ilysTZSxhzKXb\n"
    	            + "7XxaEA0ZqxOq4bvSfJXOSfqHCb1sG22AFo3L5LdFX3VkWjtXfNFTh0dx6MNkvVCP\n"
    	            + "FOjpAmHtDlpN7LOv4CixfVlI6LabG63A6nXX2mUOIcm5NrDodoJ1I8aW9x6lDsr+\n"
    	            + "lblKgodqWIftl6eZ7suKCvnawJ94j6nGIT2ZdQIDAQABAoIBADgPL+626VhaZM11\n"
    	            + "BWYUftyr4RHFQRR0jSfQxtWdAJCcPmhwQ8+Hg15VGg1VGms1lkAeCIzlGFsEIZ8C\n"
    	            + "e4+4F+0SBPKIkFcHskEVvBBg8k9bogPDUyYF6BHtV3XVY3SY1liKlgkb/qLewP4L\n"
    	            + "g8lBA1JZPu0ecqINpnmnE5iykjEAj+1ahdm036bCOB0j09EMpTI3K5B1Hgtom6l1\n"
    	            + "tMoPUiZ7bY6SI/FmPd/0mYuQj1hkt47uTbjqTROvVW/jVBobQeStJTplwPu8XTKr\n"
    	            + "oYCtL7c7BfbfIjkM6BP9L94k5j135T5veW7Zh3KOuEFNbvH2iVkGE/zCQ8Vn5kDj\n"
    	            + "fOaB6PUCgYEA2RPw1jgW+Yqp77WBpciJ/tcDz8jXiSu6vm2jeKUKOgfm6wOHx6sC\n"
    	            + "/wC4ru3Miimeq1jENwyQl7UcsbE2yjKOxOC2hgGclDWe0HPCx15Od4w4eU1nEaxv\n"
    	            + "xhCvjuyfH2m4X2zR3xAQLstHy5TmhrG1epGdgzYy3gzPhGAyAQZDjJMCgYEAy07o\n"
    	            + "LnDCbBHNzRLeqSGxqWBNJxlyrVQfTbWnKmQL6PTCj+Mb/Ia8Sdnfa8pDDEDg5tpC\n"
    	            + "19TuToPc11Ua6eafWaJrjVTN+bGlsm0YJupP5uOmVqdfgYrE+IkBZtSCe07P9kw0\n"
    	            + "7F5hQeouCTgn/OT9sutznYr7I6zWQ7klp43zjtcCgYA+ze8i+01iW0T5Tmk5HsEW\n"
    	            + "Ev1OU4xQam94At6F3DIOQhUEL5xMH2UZhArqyXAhFROL/X2fc6otyj+IBX6Vl3UI\n"
    	            + "7v6/3J2Bn9XX93YGGnnXSqFYLxW/3kTkaBVy43DSNNMcAK7ju5EDCNs3wlfD5oxb\n"
    	            + "7t9EhBzJc6liYjZ1Z2uJswKBgCIogMQEt/W2TBCPgg+ClLnzbyMJmIXco2HICm0J\n"
    	            + "4CJktSe9rZ8Q+COkUnBmzDsLz32Z4C8pHFzetMUNDHeOAunpaw6U2AKCwWcoAy3O\n"
    	            + "olwVlYXrmxVFvnBPfIfNXprfE/u6tA2ekdKkSNXNtJI92w4rlwI6LpXA3UMFoXj0\n"
    	            + "VZ6DAoGBAKPrX7OeC18FDSAYbdiRrgZ7F+ufy8CnjDnhLE0E6uZVXRsQVlqsSAJH\n"
    	            + "1G5Pj+/sVbxueHAc1tHGJyNYNJuM0MDE7WkrGJ1rwa49zPhveR1TKVnR913aZCit\n"
    	            + "Kaag2vMA4HwTfWpfqC6lj5XiwPlF7BC/6ARl93vuud/Uov5lakSK\n"
    	            + "-----END RSA PRIVATE KEY-----";
    	} catch(Exception e) {
    		changeState(State.ERROR);
    		throw new WSAException(WSAException.WSA_SERVICE_GET_SSH_KEY_FROM_APPLIANCE_ERROR);
    	}
    		saveSSHKey(STUB_SSH_KEY);
    		downloadConfig();
    }
    
    public void saveSSHKey(String ssh) throws WSAException{
    		_logger.debug("Start save received ssh key");
    		File f_dir;
    		File f = null;
    		try {
    			f_dir = new File(JNDIStub.SSH_PATH);
    			f_dir.mkdirs() ;
    		} catch(Exception e) {
    			e.printStackTrace();
    			throw new WSAException(WSAException.WSA_SERVICE_CREATE_SSH_KEY_DIR_ERROR+"("+e.getMessage()+")");
    		}
    		

		try {
			_logger.debug("Start create ssh file");
			f = pathSSH.toFile();
			
			if (f != null) {
				f.createNewFile();
				_logger.debug("Appliance Service new ssh_key file created ");
				Charset charset = Charset.forName("US-ASCII");
				BufferedWriter writer = Files.newBufferedWriter(pathSSH, charset);
				writer.write(ssh, 0, ssh.length());
				writer.flush();
				writer.close();
			} else {
				_logger.debug("Create ssh file error file is null");
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
			throw new WSAException(WSAException.WSA_SERVICE_CREATE_SSH_KEY_FROM_APPLIANCE_ERROR+"("+ioe.getMessage()+")");
		}

        _logger.debug("Ssh key has been saved");
    }
    
    
    public void downloadConfig()  throws WSAException{
    	File f_dir = null;
    	File f;
    	BufferedReader br = null;
	FileReader fr = null;
	BufferedWriter bw = null;
	
	String FILENAME = JNDIStub.CONFIG_STUB_PATH;	
	
	try {
			f_dir = new File(JNDIStub.CONFIG_PATH);
			f_dir.mkdirs() ;
		} catch(Exception e) {
			e.printStackTrace();
			throw new WSAException(WSAException.WSA_SERVICE_CREATE_CONFIG_DIR_ERROR+"("+e.getMessage()+")");
		}
    	
    	try {

    		f = pathConfig.toFile();
    		f.createNewFile();
    		Charset charset = Charset.forName("US-ASCII");
    		bw = Files.newBufferedWriter(pathConfig, charset);
    		
			fr = new FileReader(FILENAME);
			br = new BufferedReader(fr);

			String sCurrentLine;

			while ((sCurrentLine = br.readLine()) != null) {
				bw.write(sCurrentLine, 0, sCurrentLine.length());
				bw.flush();
			}

		} catch (IOException e) {

			e.printStackTrace();

		} finally {

			try {

				if (br != null)
					br.close();

				if (fr != null)
					fr.close();
				
				if (bw != null)
					bw.close();
				
			} catch (IOException ex) {

				ex.printStackTrace();

			}

		}
    	
    	_logger.debug("Finish download config");
    changeState(State.READY);
    }
    
    public void setPathSSH() {
        pathSSH = FileSystems.getDefault().getPath(JNDIStub.SSH_PATH, ""+appliance.getId());
        _logger.debug("ApplianceService pathSSH for Appliance.id=" + appliance.getId() + " has been set to " + pathSSH.toString());
    }
    
    public void setPathConfig() {
    		pathConfig = FileSystems.getDefault().getPath(JNDIStub.CONFIG_PATH, ""+appliance.getId() + ".xml");
    		_logger.debug("ApplianceService pathConfig for Appliance.id=" + appliance.getId() + " has been set to " + pathConfig.toString());
    }
    
    protected String getPathSSH() {
    		_logger.debug("getPathSSH() return: " + pathSSH.toString());
    		return pathSSH.toString();
    }

    protected String getPathConfig() {
    	_logger.debug("getPathConfig() return: " + pathConfig.toString());
        return pathConfig.toString();
    }
    
    //URL может содержать порт, а ping с портом не работает
    //Если url нулевой или пустой, то это ошибка
    protected InetAddress getHost() throws WSAException {
    		if (host == null) {
    			String url = appliance.getUrl();
    			if (url == null) {
    				changeState(State.ERROR);
    				throw new WSAException(WSAException.WSA_SERVICE_INTERNAL_ERROR_APPLIANCE_URL_IS_NULL);
    				
    			}
    			if (url.length() == 0) {
    				changeState(State.ERROR);
    				throw new WSAException(WSAException.WSA_SERVICE_INTERNAL_ERROR_APPLIANCE_URL_IS_EMPTY);
    			}
    			
    			URL u;
    			try {
    				u = new URL(url);
    				u.getHost();
    			} catch (MalformedURLException mue) {
    				changeState(State.ERROR);
    				throw new WSAException(WSAException.WSA_SERVICE_INTERNAL_ERROR_APPLIANCE_URL_MALFORMED_URL + "(" + mue.getMessage()+")");
    			}
    			
    			try {
    	            host = (Inet4Address) Inet4Address.getByName(u.toString());
    	        } catch (UnknownHostException uhe) {
    	        		changeState(State.ERROR);
    	            throw new WSAException(WSAException.WSA_SERVICE_INTERNAL_ERROR_APPLIANCE_URL_MALFORMED_URL + "(" + uhe.getMessage()+")");
    	        }
    		}
        return host;
    }
    
    public Task addTask(Task task) {
    		_logger.debug("addTask " + task);
    		queue.add(task);
    		task.setStatus(StatusEnum.QUEUE);
    		tu.updateTask(task);
    		return task;
    }
    
    public void getTask() {

        final Runnable get = new Runnable() {
            @Override
            public void run() {
            	_logger.debug("____________getTask.run()____________ State: " + state);
                if (state == State.INIT) {
                    try {
                        conn.receiveConfig();
                    } catch (WSAException wsae) {
                        changeState(State.ERROR);
                    }
                    return;
                }

                if (state == State.READY) {
                	_logger.debug("____________getTask.run()____________ try poll task");
                    Task task = queue.poll();
                    while (task != null) {
                    	_logger.debug("____________getTask.run()____________ Task: " + task);
                    		if (!generateConfig(task.getObjectId())) {
                    			task = queue.poll();
                    			break;
                    		}
                    
                    		changeState(State.PROCESS);
                    		tu.updateTask(task);
                    		try {
                        		conn.sendConfig();
                        		task.setStatus(StatusEnum.DONE);
                        		tu.updateTask(task);
                        		changeState(State.READY);
                    		} catch (WSAException wsae) {
                        		task.setStatus(StatusEnum.ERROR);
                        		tu.updateTask(task);
                    		}
                    		
                    		task = queue.poll();
                    }
                }
            }

            //TODO: analyze command output
            private boolean parseResult(String cmdResult) {
                return true;
            }
        };

        final ScheduledFuture<?> getHandle = scheduler.scheduleAtFixedRate(get, 0, 60, SECONDS);

        scheduler.schedule(new Runnable() {
            @Override
            public void run() {
                getHandle.cancel(true);
            }
        }, Long.MAX_VALUE, SECONDS);
    }
    
    private boolean generateConfig(Long taskId) {
        // 1. В поле content лежат данные в формате json, парсим и на выходе получаем POJO объект
    		Task task = tu.getById(taskId);
    		task.setStatus(StatusEnum.APPLYING);
    		tu.updateTask(task);
    		String strJson =  task.getContent();
    		Object delta = parseContentToObject(strJson);
    		Object latest = parseLatestToObject();
    		if (merge(latest, delta)) {
    			//мерж прошел успешно, можно отправлять
    			if (parsePojoToXML()) {
    				return true;
    			}
    		}
    		
    		//в мерже ошибки, переводим Task в статус ERROR
    		task.setStatus(StatusEnum.ERROR);
    		tu.updateTask(task);
    		
    		return false;
    }
    
    
    private Object parseContentToObject(String json) {
    		// Вызвать реальный парсер, пока заглушка  
    		return new Object();
    }
    
    /**
     * Метод вычитывает последний config.xml и преобразует его в объект
     * @return
     */
    private Object parseLatestToObject() {
    	
    		String FILENAME = JNDIStub.CONFIG_STUB_PATH;	
		try (BufferedReader br = new BufferedReader(new FileReader(pathConfig.toFile()))) {

			String sCurrentLine;

			while ((sCurrentLine = br.readLine()) != null) {
				System.out.println(sCurrentLine);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		// Вызвать реальный парсер, пока заглушка    	
		return new Object();
    }
    
    // Мержим новые изменения в последний конфиг
    private boolean merge(Object latest, Object delta) {
    		return true;
    }
    
    private boolean parsePojoToXML(){
    		return true;
    }
    public String toString() {
    		return "{\"appliance\":"+appliance+",\"state\":\""+state+"\",\"connectionState\":"+connectionState+"}";
    }
}
