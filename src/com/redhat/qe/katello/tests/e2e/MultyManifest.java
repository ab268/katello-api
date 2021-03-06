package com.redhat.qe.katello.tests.e2e;

import java.io.File;
import java.util.logging.Logger;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.redhat.qe.Assert;
import com.redhat.qe.katello.base.KatelloCliTestBase;
import com.redhat.qe.katello.base.obj.KatelloEnvironment;
import com.redhat.qe.katello.base.obj.KatelloOrg;
import com.redhat.qe.katello.base.obj.KatelloProvider;
import com.redhat.qe.katello.base.obj.KatelloSystem;
import com.redhat.qe.katello.common.KatelloUtils;

import com.redhat.qe.tools.SSHCommandResult;

@Test(groups={"cfse-e2e"}, singleThreaded = true)
public class MultyManifest extends KatelloCliTestBase{
	protected static Logger log = Logger.getLogger(MultyManifest.class.getName());

	String org;
	private String env_dev;
	private String sys_name;

	public static final String MANIFEST_MULTY = "manifest-multy.zip";

	@BeforeClass(description="Init unique names", alwaysRun=true)
	public void setUp(){
		String uid = KatelloUtils.getUniqueID();
		this.env_dev = "Dev-"+uid;
		this.org = "multy-manifest-"+uid;
		this.sys_name = "localhost-"+uid;
		KatelloOrg org = new KatelloOrg(this.cli_worker, this.org, null);
		org.cli_create();
	}
	
	@Test(description="Import multy manifest", enabled=true)
	public void test_importMultyManifest() {
		
		KatelloUtils.scpOnClient(cli_worker.getClientHostname(), "data/"+MANIFEST_MULTY, "/tmp");
		
		KatelloProvider prov = new KatelloProvider(this.cli_worker, KatelloProvider.PROVIDER_REDHAT, this.org, null, null);
		SSHCommandResult res = prov.import_manifest("/tmp"+File.separator+MANIFEST_MULTY, new Boolean(true));
		Assert.assertTrue(res.getExitCode().intValue()==0, "Check - return code (provider import_manifest)");
		Assert.assertTrue(getOutput(res).contains(KatelloProvider.OUT_MANIFEST_IMPORTED),"Message - (provider import_manifest)");
	}
	
	@Test(description="Add system to dev environment", dependsOnMethods={"test_importMultyManifest"}, enabled=true)
	public void test_addSystemsToEnvs(){
		KatelloEnvironment env = new KatelloEnvironment(this.cli_worker, this.env_dev, null, this.org, KatelloEnvironment.LIBRARY);
		SSHCommandResult res = env.cli_create();
		Assert.assertTrue(res.getExitCode().intValue() == 0, "Check - return code");
		promoteEmptyContentView(org, env_dev);
		
		KatelloSystem sys = new KatelloSystem(this.cli_worker, sys_name, this.org, this.env_dev);
		res = sys.rhsm_registerForce(); 
		Assert.assertTrue(res.getExitCode().intValue() == 0, "Check - return code");
	}
	
	@Test(description="list the subscriptions on environment", dependsOnMethods={"test_addSystemsToEnvs"}, enabled=true)
	public void test_subscriptionList() {
		SSHCommandResult res = sshOnClient("subscription-manager list --available --all | sed  -e 's/^ \\{1,\\}//'");
		Assert.assertTrue(getOutput(res).trim().contains("JBoss Enterprise Application Platform ELS Program, 64 Core Standard"), "Contains all pools from manifest");
		Assert.assertTrue(getOutput(res).trim().contains("Red Hat Enterprise Linux Server for HPC Compute Node, Self-support (8 sockets) (Up to 1 guest)"), "Contains all pools from manifest");
		Assert.assertTrue(getOutput(res).trim().contains("CloudForms Employee Subscription"), "Contains all pools from manifest");
		Assert.assertTrue(getOutput(res).trim().contains("OpenShift Employee Subscription"), "Contains all pools from manifest");
	}
	
	@AfterClass(description="Cleanup the org - allow others to reuse the manifest", alwaysRun=true)
	public void tearDown(){
		KatelloOrg org = new KatelloOrg(this.cli_worker, this.org, null);
		SSHCommandResult res = org.delete();
		Assert.assertTrue(res.getExitCode().intValue()==0, "Check - return code (org delete)");
	}
}
