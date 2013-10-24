package com.redhat.qe.katello.base.obj;

import java.util.logging.Logger;

import javax.management.Attribute;

import com.redhat.qe.katello.base.threading.KatelloCliWorker;
import com.redhat.qe.tools.SSHCommandResult;

public class HammerEnvironment extends _HammerObject {
	protected static Logger log = Logger.getLogger(HammerArchitecture.class.getName());
	
	// ** ** ** ** ** ** ** Public constants
	public static final String CMD_LIST = "environment list";
	public static final String CMD_UPDATE = "environment update";
	public static final String CMD_DELETE = "environment delete";
	public static final String CMD_CREATE = "environment create";
	public static final String CMD_INFO = "environment info";
	
	public static final String OUT_CREATE = "Environment created";
	public static final String OUT_UPDATE = "Environment updated";
	public static final String OUT_DELETE = "Environment deleted";
	
	public static final String ERR_DUPLICATE_NAME = "Could not create the environment:" + "\n" + "  Name has already been taken";
	public static final String ERR_INVALID_NAME = "Could not create the environment:" + "\n" + "  Name is alphanumeric and cannot contain spaces";
	public static final String ERR_404 = "404 Resource Not Found";
	
	public static final String REG_ENVIRONMENT_INFO = ".*Name\\s*:\\s+%s.*";
	
	// ** ** ** ** ** ** ** Class members
	public String name;
	public String id;
	
	public HammerEnvironment(){super();}
	
	public HammerEnvironment(KatelloCliWorker kcr, String pName)
	{
		this.kcr = kcr;
		this.name = pName;
	}
	
	public String getName() {
	    return name;
	}

	public void setName(String name) {
	    this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public SSHCommandResult cli_create() {
		args.clear();
		args.add(new Attribute("name", this.name));
		return run(CMD_CREATE);
	}
	
	public SSHCommandResult cli_info(String id) {
		args.clear();
		args.add(new Attribute("id", id));
		args.add(new Attribute("name", this.name));
		return run(CMD_INFO);
	}
	
	public SSHCommandResult update(String newName) {
		args.clear();
		args.add(new Attribute("name", this.name));
		args.add(new Attribute("new-name", newName));
		return run(CMD_UPDATE);
	}
	
	public SSHCommandResult cli_list(String searchStr, String order, String page) {
		args.clear();
		args.add(new Attribute("search", searchStr));
		args.add(new Attribute("order", order));
		args.add(new Attribute("page", page));
		return run(CMD_LIST);
	}
	
	public SSHCommandResult delete(String id) {
		args.clear();
		if(id != null)
		{
			args.add(new Attribute("id", id));
		}
		else
		{
			args.add(new Attribute("name", this.name));
		}
		return run(CMD_DELETE);
	}
	
	
}
