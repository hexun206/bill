package com.huatu.teacheronline.bean;

import java.io.Serializable;

/***************************************
 * 
 * @author ljyu
 * @time 2017-6-21 11:57:41
 * 类说明:
 * 
 **************************************/
public class TabItemBean implements Serializable{
	
	/**
	 * @param ic_res
	 * @param clzz
	 * @param name
	 */
	public TabItemBean(int ic_res, Class clzz, String name) {
		super();
		this.ic_res = ic_res;
		this.clzz = clzz;
		this.name = name;
	}
	private int ic_res;
	private Class clzz;
	private String name;
	public int getIc_res() {
		return ic_res;
	}
	public void setIc_res(int ic_res) {
		this.ic_res = ic_res;
	}
	public Class getClzz() {
		return clzz;
	}
	public void setClzz(Class clzz) {
		this.clzz = clzz;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

}
