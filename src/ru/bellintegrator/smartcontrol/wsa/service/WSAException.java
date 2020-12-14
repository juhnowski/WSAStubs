package ru.bellintegrator.smartcontrol.wsa.service;

public class WSAException extends Exception{
	public static final String WSA_SERVICE_INTERNAL_ERROR_APPLIANCE_URL_IS_NULL = "WSA Service Internal Error: appliance url is null";
	public static final String WSA_SERVICE_INTERNAL_ERROR_APPLIANCE_URL_IS_EMPTY = "WSA Service Internal Error: appliance url is empty";
	public static final String WSA_SERVICE_INTERNAL_ERROR_APPLIANCE_URL_MALFORMED_URL = "WSA Service Internal Error: appliance url malformed url : ";
	
	public static final String WSA_SERVICE_GET_SSH_KEY_FROM_APPLIANCE_ERROR = "WSA Service get ssh_key from appliance error";
	public static final String WSA_SERVICE_GET_CONFIG_FROM_APPLIANCE_ERROR = "WSA Service get config from appliance error";
	public static final String WSA_SERVICE_SAVE_SSH_KEY_FROM_APPLIANCE_ERROR = "WSA Service save ssh_key from appliance error";
	public static final String WSA_SERVICE_CREATE_SSH_KEY_FROM_APPLIANCE_ERROR = "WSA Service create ssh_key from appliance error";
	public static final String WSA_SERVICE_CREATE_SSH_KEY_DIR_ERROR = "WSA Service create ssh_key dir error";
	public static final String WSA_SERVICE_CREATE_CONFIG_DIR_ERROR = "WSA Service create config dir error";
	public static final String WSA_SERVICE_GENERATE_CONFIG_ERROR = "WSA Service generate config error";
	
	public WSAException() { super(); }
	public WSAException(String message) { super(message); }
	public WSAException(String message, Throwable cause) { super(message, cause); }
	public WSAException(Throwable cause) { super(cause); }
}
