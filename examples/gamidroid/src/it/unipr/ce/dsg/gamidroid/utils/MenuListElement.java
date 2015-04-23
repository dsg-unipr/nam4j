package it.unipr.ce.dsg.gamidroid.utils;

/**
 * Titles and Subtitles of Setting menu list
 *
 */
public class MenuListElement {
	private String title;
	private String subTitle;
	
	public MenuListElement(String t, String s) {
		this.title = t;
		this.subTitle = s;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSubTitle() {
		return subTitle;
	}

	public void setSubTitle(String subTitle) {
		this.subTitle = subTitle;
	}
}
