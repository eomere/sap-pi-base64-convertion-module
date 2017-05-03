package com.sap.mdp;

import java.io.InputStream;
import java.util.Base64;

import javax.ejb.Local;
import javax.ejb.LocalHome;
import javax.ejb.Remote;
import javax.ejb.RemoteHome;
import javax.ejb.Stateless;

import com.sap.aii.af.lib.mp.module.Module;
import com.sap.aii.af.lib.mp.module.ModuleContext;
import com.sap.aii.af.lib.mp.module.ModuleData;
import com.sap.aii.af.lib.mp.module.ModuleException;
import com.sap.aii.af.lib.mp.module.ModuleHome;
import com.sap.aii.af.lib.mp.module.ModuleLocal;
import com.sap.aii.af.lib.mp.module.ModuleLocalHome;
import com.sap.aii.af.lib.mp.module.ModuleRemote;
import com.sap.engine.interfaces.messaging.api.Message;
import com.sap.engine.interfaces.messaging.api.MessageKey;
import com.sap.engine.interfaces.messaging.api.PublicAPIAccessFactory;
import com.sap.engine.interfaces.messaging.api.XMLPayload;
import com.sap.engine.interfaces.messaging.api.auditlog.AuditAccess;
import com.sap.engine.interfaces.messaging.api.auditlog.AuditLogStatus;

/**
 * Session Bean implementation class Converter
 */
@Stateless(name="Base64ConverterBean")
@Local(value=(ModuleLocal.class))
@Remote(value=(ModuleRemote.class))
@LocalHome(value=(ModuleLocalHome.class))
@RemoteHome(value=(ModuleHome.class))
public class Converter implements Module{
	
    /**
     * Default constructor. 
     */
    public Converter() {
        // TODO Auto-generated constructor stub
    }

	@Override
	public ModuleData process(ModuleContext moduleContext, ModuleData inputModuleData) throws ModuleException {
			
		byte[] encodedMessage = null;
		AuditAccess audit = null;
		boolean isSucceeded = false;
		
		Object obj = inputModuleData.getPrincipalData();
		Message message = (Message)obj;
		MessageKey key = message.getMessageKey();
		
		XMLPayload attachment = message.getDocument();
		String strMessage = message.getDocument().getText();
		
		//MessageKey auditMsgKey = new MessageKey(message.getMessageId(), message.getMessageDirection());

		try{
			
			audit = PublicAPIAccessFactory.getPublicAPIAccess().getAuditAccess();
			audit.addAuditLogEntry(key, AuditLogStatus.SUCCESS, "Base64Converter : module called");

			encodedMessage = Base64.getEncoder().encode(strMessage.getBytes());
			attachment.setContent(encodedMessage);
			inputModuleData.setPrincipalData(message);
			
			isSucceeded = true;
			
		}catch(Exception e){
			
			isSucceeded = false;
			audit.addAuditLogEntry(key, AuditLogStatus.ERROR, e.toString());
			
		}finally {
			
			if(isSucceeded)
				audit.addAuditLogEntry(key, AuditLogStatus.SUCCESS, "Convertion Succeeded!");
		}
		return inputModuleData;
	}

}
