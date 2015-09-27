package com.findhotel.model;

import java.io.Serializable;
public class HotelInfo implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	private int rank;
	private String address;
	private String description;
	private String descriptiondetail;
	private String contactInfo;
	private double lat;
	private double lng;
	private String avataLink;
	private float bankinh;
	
	public HotelInfo(String name,int rank,String address,String description,String descriptiondetail,String tel,Double lat,Double lng, String link,Float bk) {
		super();
		this.name = name;
		this.rank = rank;
		this.address = address;
		this.description = description;
		this.descriptiondetail = descriptiondetail;
		this.contactInfo = tel;
		this.lat = lat;
		this.lng = lng;
		this.avataLink = link;
		this.bankinh = bk;
	}
	public HotelInfo() {
	}
	// get Name
	public String getName() {
		return name;
	}
	// set Name
	public void setName(String name) {
		this.name = name;
	}
	// get Rank
	public int getRank() {
		return this.rank;
	}
	// set Rank
	public void setRank(int rank) {
		this.rank =rank;
	}
	
	 // get address
	public String getAddress() {
		return address;
	}
	// set address
	public void setAddress(String address) {
		this.address = address;
	}
	// get description
	public String getDescription(){
		return description;
	}
	// set description
	public void setDescription(String description){
		this.description=description;
	}
	// get descriptiondetail
	public String getDescriptiondetail(){
		return descriptiondetail;
	}
	// set descriptiondetail
	public void getDescriptiondetail(String descriptiondetail){
		this.descriptiondetail = descriptiondetail;
	}
	// get contactinfo
	public String getContactInfo(){
			return contactInfo;
		}
		// set contactinfo
	public void setContactInfo(String tel){
			this.contactInfo=tel;
		}
		// get lat
	public double getLat(){
			return lat;
		}
		// set lat
	public void setLat(Long lat){
			this.lat = lat;
		}
		// get lng
	public double getLng(){
			return lng;
		}
		// set lng
	public void setLng(Long lng){
			this.lng =lng;
		}
	// get Link
	public String getAvataLink() {
		return avataLink;
	    }
	// set Link
	public void setAvataLink(String link) {
		this.avataLink =link;
	    }
	// get ban kinh
	public float getBankinh(){
		return bankinh;
	}
	// set ban kinh
	public void setBankinh(Float bk){
		this.bankinh = bk;
	}
}
