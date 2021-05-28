package com.neoris.tcl.controller;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.event.ActionEvent;

import org.primefaces.PrimeFaces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import com.neoris.tcl.models.HfmOracleAcc;
import com.neoris.tcl.models.HfmRollupEntries;
import com.neoris.tcl.models.SetDefinedAccounts;
import com.neoris.tcl.models.SetHfmCodes;
import com.neoris.tcl.models.SetIcpcodes;
import com.neoris.tcl.models.ViewCostCenter;
import com.neoris.tcl.models.ViewPartnersICP;
import com.neoris.tcl.security.models.User;
import com.neoris.tcl.services.IHfmAccEntriesDetService;
import com.neoris.tcl.services.IHfmOracleAccService;
import com.neoris.tcl.services.IHfmRollupEntriesService;
import com.neoris.tcl.services.ISetDefinedAccountsService;
import com.neoris.tcl.services.ISetHfmCodesService;
import com.neoris.tcl.services.ISetIcpcodesService;
import com.neoris.tcl.services.IViewCostCenterService;
import com.neoris.tcl.utils.Functions;
import com.neoris.tcl.utils.ViewScope;

@Controller(value = "setdefaccountsControllerBean")
@Scope(ViewScope.VIEW)
public class SetDefinedAccountsController {

	private final static Logger LOG = LoggerFactory.getLogger(SetDefinedAccountsController.class);

	@Autowired
	private ISetDefinedAccountsService service;
	@Autowired
	private IHfmOracleAccService serviceOAS;
	@Autowired
	private IViewCostCenterService servcc;
	@Autowired
	private IHfmRollupEntriesService servcomp;
	@Autowired 
	ISetHfmCodesService servhfm;
	@Autowired
	ISetIcpcodesService sericp;
	
	
	private List<SetDefinedAccounts> lsttpAccs;
	private List<SetDefinedAccounts> lstSelectdAccs;
	private SetDefinedAccounts curtpAccs; // actual iterator
	
	//Company
  	private List<HfmRollupEntries> lstcompany;

  	private List<HfmOracleAcc> lstOrcl;
	private List<ViewCostCenter> lstCC;
	private Authentication authentication;
	

	
	private int lcompanyid; 
	
	
	private User user;

	@PostConstruct
	public void init() {
		
		this.curtpAccs = new SetDefinedAccounts();
		
		LOG.info("Initializing lstcompany ...");
		
		this.lstcompany = servcomp.findAll();
		
		
		this.lsttpAccs = service.findByIdCompanyid(this.curtpAccs.getId().getCompanyid());
		
		try{
			LOG.info("Initializing Cost Centers...");
		
		  this.lstCC = servcc.findAll();

			LOG.info(" lstCC "+this.lstCC.size());
		}catch (Exception e) {
			LOG.error("init lstCC ERRor -> {}", e.getMessage(),e);
		}
		
		this.authentication = SecurityContextHolder.getContext().getAuthentication();
		if (this.authentication.getPrincipal() instanceof User) {
			this.user = (User) this.authentication.getPrincipal();
		}
		
		
	}

	public void openNew() {
		//this.curtpAccs = new SetDefinedAccounts();
		this.lcompanyid = this.curtpAccs.getId().getCompanyid();
		LOG.info(" open new "+lcompanyid);
	}

	public void save() {
		LOG.info("Entering to save Accounting Accounts => {}", this.curtpAccs);
		this.curtpAccs.setUserid(user.getUsername());
		this.curtpAccs = service.save(curtpAccs);
		
		this.lsttpAccs = service.findByIdCompanyid(this.curtpAccs.getId().getCompanyid());
		
		Functions.addInfoMessage("Succes", "Accounting Accounts saved");
		PrimeFaces.current().executeScript("PF('" + getDialogName() + "').hide()");
		PrimeFaces.current().ajax().update("form:messages", "form:" + getDataTableName());
		PrimeFaces.current().executeScript("PF('dtCodes').clearFilters()");
	}
	public void delete() {
		LOG.info("Entering to delete Accounting Accounts => {}", this.curtpAccs);
		service.delete(this.curtpAccs);
		this.curtpAccs = null;
		this.lsttpAccs = service.findByIdCompanyid(this.curtpAccs.getId().getCompanyid());
		Functions.addInfoMessage("Succes", "Code Removed");
		PrimeFaces.current().ajax().update("form:messages", "form:" + getDataTableName());
		PrimeFaces.current().executeScript("PF('dtCodes').clearFilters()");
	}

	public void deleteSelected(ActionEvent event) {
		LOG.info("[deleteSelected] = > Entering to delete Accounting Account: {}", this.lstSelectdAccs);
		service.deleteAll(this.lstSelectdAccs);
		this.lstSelectdAccs = null;
		this.lsttpAccs = service.findByIdCompanyid(this.curtpAccs.getId().getCompanyid());
		Functions.addInfoMessage("Succes", "Accounting Account Removed");
		PrimeFaces.current().ajax().update("form:messages", "form:" + getDataTableName());
		PrimeFaces.current().executeScript("PF('dtCodes').clearFilters()");
	}

	public void update() {
		LOG.info("Entering to update Accounting Account => {}", curtpAccs);
		save();
	}

	public boolean hasSelectedCodes() {
		return this.lstSelectdAccs != null && !this.lstSelectdAccs.isEmpty();
	}

	public String getDeleteButtonMessage() {
		String message = "Delete %s code%s selected";
		String retval = "Delete";
		if (hasSelectedCodes()) {
			int size = this.lstSelectdAccs.size();
			if (size > 1) {
				retval = String.format(message, size, "s");
			} else {
				retval = String.format(message, size, "");
			}
		}
		return retval;
	}

	public String getTitle() {
		return "Source Accounts Setting";
	}

	public String getDialogName() {
		return "manageCodeDialog";
	}

	public String getDataTableName() {
		return "dt-codes";
	}

	public String getDeleteCodesButton() {
		return "delete-codes-button-id";
	}

	public List<SetDefinedAccounts> getLsttpAccs() {
		return lsttpAccs;
	}

	public void setLsttpAccs(List<SetDefinedAccounts> lsttpAccs) {
		this.lsttpAccs = lsttpAccs;
	}

	public List<SetDefinedAccounts> getLstSelectdAccs() {
		return lstSelectdAccs;
	}

	public void setLstSelectdAccs(List<SetDefinedAccounts> lstSelectdAccs) {
		this.lstSelectdAccs = lstSelectdAccs;
	}

	public SetDefinedAccounts getCurtpAccs() {
		return curtpAccs;
	}

	public void setCurtpAccs(SetDefinedAccounts curtpAccs) {
		this.curtpAccs = curtpAccs;
		
	 try {	
		this.lstCC = servcc.findAll();
		
		LOG.info("**setCurtpAccs company  => {},costcenter  => {}, source {}", this.curtpAccs.getId().getCompanyid(),this.curtpAccs.getId().getCostcenter(),this.curtpAccs.getId().getSource());
		
		
		//LOG.info("current lstCC "+this.lstCC.size());
		
		lstOrcl = serviceOAS.findByOrgidAndCostcenter(this.curtpAccs.getId().getCompanyid(),this.curtpAccs.getId().getCostcenter());
		LOG.info("setCurtpAccs return lstOrcl con items => {}", lstOrcl != null ? lstOrcl.size() : "is null");
		
	   } catch (Exception e) {
			LOG.error("setCurtpAccs ERRor -> {}", e.getMessage());
		}
		
	}

	
	public List<HfmOracleAcc> getLstOrcl() {
		return lstOrcl;
	}

	public void setLstOrcl(List<HfmOracleAcc> lstOrcl) {
		this.lstOrcl = lstOrcl;
	}

	public List<HfmRollupEntries> getLstcompany() {
		return lstcompany;
	}

	public void setLstcompany(List<HfmRollupEntries> lstcompany) {
		this.lstcompany = lstcompany;
	}
	
	

	public List<ViewCostCenter> getLstCC() {
		return lstCC;
	}

	public void setLstCC(List<ViewCostCenter> lstCC) {
		this.lstCC = lstCC;
	}

	public void companyidChange() {
		this.lcompanyid = this.curtpAccs.getId().getCompanyid();
				
		try {
			LOG.info("companyidChange company  => {},costcenter  => {}", this.lcompanyid,this.curtpAccs.getId().getCostcenter());
			//this.lstCC = servcc.findAll();
			//LOG.info("change lstCC "+this.lstCC.size());			
		} catch (Exception e) {
			LOG.error("companyidChange ERRor -> {}", e.getMessage());
		}
	}
	
	public void companyidChangeorcl() {
		this.lcompanyid = this.curtpAccs.getId().getCompanyid();
				
		try {
			LOG.info("companyidChangeorcl company  => {},costcenter  => {}", this.lcompanyid);
			this.lsttpAccs = service.findByIdCompanyid(this.lcompanyid);
			LOG.info("companyidChangeorcl lsttpAccs "+this.lsttpAccs.size());			
		} catch (Exception e) {
			LOG.error("companyidChangeorcl ERRor -> {}", e.getMessage());
		}
		PrimeFaces.current().ajax().update("form:dt-codes");

	}
	
	public void costcenterChange() {
		try {
			LOG.info("costcenterChange companyid  => {},costcenter  => {}", this.lcompanyid ,this.curtpAccs.getId().getCostcenter());
			lstOrcl = serviceOAS.findByOrgidAndCostcenter(this.lcompanyid ,this.curtpAccs.getId().getCostcenter());
			
			
			LOG.info("costcenterChange return lstOrcl con items => {}", lstOrcl != null ? lstOrcl.size() : "is null");
		} catch (Exception e) {
			LOG.error("costcenterChange ERRor -> {}", e.getMessage());
		}
	}
	
}
