package com.redhat.qe.katello.base.obj;

import java.util.logging.Logger;

import javax.management.Attribute;

import com.redhat.qe.katello.base.threading.KatelloCliWorker;
import com.redhat.qe.tools.SSHCommandResult;

public class HammerEnvironment extends _HammerObject {
	protected static Logger log = Logger.getLogger(HammerEnvironment.class.getName());
	
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

	public SSHCommandResult cli_create(){		
		args.clear();
		args.add(new Attribute("name", this.name));
		return run(CMD_CREATE);
	}
	
	public SSHCommandResult cli_info(){
		args.clear();
		args.add(new Attribute("name", this.name));
		return run(CMD_INFO);
	}
	
	public SSHCommandResult cli_list(){
		args.clear();
		return run(CMD_LIST);
	}

	public SSHCommandResult cli_search(String search){
		args.clear();
		args.add(new Attribute("search", search));
		return run(CMD_LIST);
	}
	
	public SSHCommandResult cli_list(String order, Integer page, Integer per_page){
		args.clear();
		args.add(new Attribute("order", order));
		args.add(new Attribute("page", page));
		args.add(new Attribute("per-page", per_page));
		return run(CMD_LIST);
	}
	
	public SSHCommandResult cli_list(String searchStr, String order, String page, Integer per_page) {
		args.clear();
		args.add(new Attribute("search", searchStr));
		args.add(new Attribute("order", order));
		args.add(new Attribute("page", page));
		args.add(new Attribute("per-page", per_page));
		return run(CMD_LIST);
	}
	
	public SSHCommandResult update(String new_name){
		args.clear();
		args.add(new Attribute("name", this.name));
		args.add(new Attribute("new-name", new_name));
		return run(CMD_UPDATE);
	}

	public SSHCommandResult delete() {
		args.clear();
		if (this.id != null) {
			args.add(new Attribute("id", this.id));	
		} else {
			args.add(new Attribute("name", this.name));
		}
		return run(CMD_DELETE);
	}

	// ** ** ** ** ** ** **
	// ASSERTS
	// ** ** ** ** ** ** **
}
