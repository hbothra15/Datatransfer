package com.github.hbothra.DataTranfer.dto;

import org.springframework.stereotype.Component;

@Component("project")
public class Project implements DTO {

	private Long id;
	private Long wsid;
	private String proName;
	private String wsName;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getWsid() {
		return wsid;
	}

	public void setWsid(Long wsid) {
		this.wsid = wsid;
	}

	public String getProName() {
		return proName;
	}

	public void setProName(String proName) {
		this.proName = proName;
	}

	public String getWsName() {
		return wsName;
	}

	public void setWsName(String wsName) {
		this.wsName = wsName;
	}
}
