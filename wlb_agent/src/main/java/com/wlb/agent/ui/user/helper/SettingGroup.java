package com.wlb.agent.ui.user.helper;

import java.util.ArrayList;
import java.util.List;
 /**
  
  @author 张全
 */
public class SettingGroup {
  private List<SettingItem> items=new ArrayList<SettingItem>();
  public int topMargin;
  public void addItem(SettingItem item){
	  items.add(item);
  }
  public List<SettingItem> getItems(){
	  return items;
  }
}
