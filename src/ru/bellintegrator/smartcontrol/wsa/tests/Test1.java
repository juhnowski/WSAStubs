package ru.bellintegrator.smartcontrol.wsa.tests;

import ru.bellintegrator.smartcontrol.core.api.dictionary.ActionEnum;
import ru.bellintegrator.smartcontrol.core.api.dictionary.ObjectTypeEnum;
import ru.bellintegrator.smartcontrol.entity.system.WsaAppliance;
import ru.bellintegrator.smartcontrol.entity.user.Task;
import ru.bellintegrator.smartcontrol.utils.TaskUtils;
import ru.bellintegrator.smartcontrol.wsa.service.ApplianceManager;
import ru.bellintegrator.smartcontrol.wsa.utils.Logger;

public class Test1 {

	public static void main(String[] args) {
		Logger.debug("Start proto"); 
		
		ApplianceManager am = new ApplianceManager();
		am.init();
		TaskUtils tu = new TaskUtils();
		Task task = tu.persistNewTask(1, 1, ObjectTypeEnum.CUSTOM_CATEGORY, ActionEnum.CREATE, "{some content}");
		am.addTaskById(task.getId());
		
		Logger.debug("===============================  TEST DONE ===================================");
	}
	
	

}
