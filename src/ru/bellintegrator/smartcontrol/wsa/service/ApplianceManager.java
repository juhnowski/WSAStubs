package ru.bellintegrator.smartcontrol.wsa.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import ru.bellintegrator.smartcontrol.core.entity.ApplianceTypeEnum;
import ru.bellintegrator.smartcontrol.entity.system.Appliance;
import ru.bellintegrator.smartcontrol.entity.system.WsaAppliance;
import ru.bellintegrator.smartcontrol.entity.user.Task;
import ru.bellintegrator.smartcontrol.utils.ApplianceUtils;
import ru.bellintegrator.smartcontrol.utils.TaskUtils;
import ru.bellintegrator.smartcontrol.wsa.utils.Logger;

public class ApplianceManager {
	ArrayList<Item> list = new ArrayList<>();
    private Logger _logger;
    private List<Appliance> appList;    
    private ApplianceUtils applianceUtils = new ApplianceUtils();
    
    public ApplianceManager() {
		Logger.debug("Autostart ApplianceManager");
    		init();
    		Logger.debug("ApplianceManager autostarted");
    }
    
    public void init() {
        appList = applianceUtils.getAppliancesByType(ApplianceTypeEnum.WSA, true);
        appList.forEach(appl -> list.add(new Item((WsaAppliance)appl)));
        Logger.debug("ApplianceManager inited");
    }
    
    public void addTaskById(long taskId ) {
    	_logger.debug("-- ApplianceManager.addTaskById");
    		TaskUtils tu = new TaskUtils();
    		Task task = tu.getById(taskId);
    		
    		Long applianceId = task.getApplianceId();
    		if (applianceId != null) {
    			List<Item> result = list.stream()
    				    .filter(a -> Objects.equals(a.appliance.getId(), applianceId))
    				    .collect(Collectors.toList());
    			Item item = result.get(0);
    			item.service.addTask(task);
    			
    		}
    		
    		
    }
    
    class Item {
        WsaAppliance appliance;
        ApplianceService service;
        public Item(WsaAppliance appliance){
            this.appliance = appliance;
            this.service = new ApplianceService(appliance);
            this.service.init();
            _logger.debug("Appliance.Item created. Appliance:" + appliance);
        }
    }
}
