package com.neoris.tcl.services;

import java.util.List;
import java.util.Map;

import com.neoris.tcl.models.ViewFFSSGrouped;

public interface IViewRollupFFSSGconsService {
	
	
	List<ViewFFSSGrouped> findAll();

	List<Map<String,Object>>  findByCompanyidAndhfmparentAndhfmcode(int companyId, String hfmparent,String hfmcode);
	
	List<ViewFFSSGrouped> findByCompanyidAndHfmcode(int companyid,String hfmcode);
}