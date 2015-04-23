package it.unipr.ce.dsg.gamidroid.utils;

import java.util.ArrayList;
import java.util.List;

public class ListGroup {

	public String string;
	public final List<String> children = new ArrayList<String>();

	public ListGroup(String string) {
		this.string = string;
	}

}
