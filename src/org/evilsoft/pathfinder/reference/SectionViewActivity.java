package org.evilsoft.pathfinder.reference;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class SectionViewActivity extends FragmentActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.section_view);

		Intent launchingIntent = getIntent();
		String content = launchingIntent.getData().toString();

		SectionViewFragment viewer = (SectionViewFragment) getSupportFragmentManager().findFragmentById(
				R.id.section_view_fragment);
		viewer.updateUrl(content);
	}
}
