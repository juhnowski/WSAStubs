package ru.bellintegrator.smartcontrol.wsa.tests;

import ru.bellintegrator.smartcontrol.entity.system.WsaAppliance;
import ru.bellintegrator.smartcontrol.wsa.service.ApplianceService;
import ru.bellintegrator.smartcontrol.wsa.service.Connect;
import ru.bellintegrator.smartcontrol.wsa.service.WSAException;

public class Test2 {

	public static void main(String[] args) {
		WsaAppliance appliance = new WsaAppliance(1L, "https://10.201.206.103:8443/ ","smart","Password11!!!");
		ApplianceService aps = new ApplianceService(appliance);
		Connect conn = new Connect(aps);
		try {
			aps.setPathSSH();
			aps.setPathConfig();
			conn.sendConfig();
		} catch (WSAException e) {
			e.printStackTrace();
		}
	}

}
