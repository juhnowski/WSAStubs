package ru.bellintegrator.smartcontrol.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import ru.bellintegrator.smartcontrol.core.entity.ApplianceTypeEnum;
import ru.bellintegrator.smartcontrol.entity.system.Appliance;
import ru.bellintegrator.smartcontrol.entity.system.WsaAppliance;
import ru.bellintegrator.smartcontrol.entity.user.Task;
import ru.bellintegrator.smartcontrol.wsa.utils.Logger;

public class ApplianceUtils {
	private Logger _logger;
	private List<Appliance> list = new ArrayList<>(); 
	
	public List<Appliance> getAppliancesByType(ApplianceTypeEnum type, boolean lock) {
		
		_logger.debug("Start generate stub Appliance");
		for (int i = 1; i<2; i++) {
			WsaAppliance app = new WsaAppliance((long)i, "https://10.201.206.103:8443/ ","smart","Password11!!!");
			list.add(app);
			_logger.debug("Generate stub Appliance: " + app);
		}
		
		return list;
	}
	
	public Appliance getById(Long applianceId) {
		List<Appliance> result = list.stream()
			    .filter(a -> Objects.equals(a.getId(), applianceId))
			    .collect(Collectors.toList());
		return result.get(0);
	}
	
	public Appliance getByURL(String url) {
		List<Appliance> result = list.stream()
			    .filter(a -> Objects.equals(a.getUrl(), url))
			    .collect(Collectors.toList());
		return result.get(0);
	}
}
