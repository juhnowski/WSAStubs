package ru.bellintegrator.smartcontrol.entity.system;

import ru.bellintegrator.smartcontrol.wsa.utils.Logger;

public abstract class Appliance {
	private Logger _logger;
	/**
	 * Идентификатор будет использоваться как суфикс для файлов config_<id>.xml и ssh_key_<id>
	 * Файлы будут располагаться в папке указанном в JNDI ресурсе
	 * 
	 */
	private long _id;
	private String _url;
    private String _adminUser;
    private String _adminPassword;
    private String _sshKey;
    
    /**
     * В конструкторе мы не знаем sshKey - его мы получим по запросу к Appliance
     * @param id - автогенерируемое поле Entity
     * @param url
     * @param adminUser
     * @param adminPassword
     */
    public Appliance(long id, String url, String adminUser, String adminPassword) {
    	_logger.debug("Appliance constructor: id="+id+", url=" +url+", adminUser="+adminUser+", adminPassword="+adminPassword);
    		_id = id;
    		_url = url;
    		_adminUser = adminUser;
    		_adminPassword = adminPassword;
    }
    
    public String getUrl() {
    	_logger.debug("getAdminUser() return: " + _url);
        return _url;
    }

    public void setUrl(String url) {
        this._url = url;
    }

	public long getId() {
		return _id;
	}

	public void setId(long _id) {
		this._id = _id;
	}

	public String getAdminUser() {
		_logger.debug("getAdminUser() return: " + _adminUser);
		return _adminUser;
	}

	public void setAdminUser(String _adminUser) {
		this._adminUser = _adminUser;
	}
	
	public String toString() {
		return "{id:"+_id+",url:\""+_url+"\",\"adminUser\":\""+_adminUser+"\",\"adminPassword\""+_adminPassword+"\"}";
				
	}
}
