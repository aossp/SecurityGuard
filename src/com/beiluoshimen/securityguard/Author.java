package com.beiluoshimen.securityguard;

import android.os.Bundle;

import com.beiluoshimen.securityguard.slideingmenu.BaseActivity;

/**
 * Info about author will display in this activity 
 * 
 * @author Hsieh Yu-Hua
 * @date Jan 3, 20158:26:59 PM
 */
public class Author extends BaseActivity{

	public Author(){
		super(R.string.title_author);
		
	}
	public Author(int titleRes) {
		super(R.string.title_author);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.aty_author);
	}

}
