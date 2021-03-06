package org.evilsoft.pathfinder.reference;

import org.evilsoft.pathfinder.reference.db.psrd.ClassAdapter;
import org.evilsoft.pathfinder.reference.db.psrd.FeatAdapter;
import org.evilsoft.pathfinder.reference.db.psrd.MonsterAdapter;
import org.evilsoft.pathfinder.reference.db.psrd.PsrdDbAdapter;
import org.evilsoft.pathfinder.reference.db.psrd.RaceAdapter;
import org.evilsoft.pathfinder.reference.db.psrd.RuleAdapter;
import org.evilsoft.pathfinder.reference.db.psrd.SkillAdapter;
import org.evilsoft.pathfinder.reference.db.psrd.SpellAdapter;
import org.evilsoft.pathfinder.reference.list.ClassListAdapter;
import org.evilsoft.pathfinder.reference.list.FeatListAdapter;
import org.evilsoft.pathfinder.reference.list.MonsterListAdapter;
import org.evilsoft.pathfinder.reference.list.RaceListAdapter;
import org.evilsoft.pathfinder.reference.list.RuleListAdapter;
import org.evilsoft.pathfinder.reference.list.SearchListAdapter;
import org.evilsoft.pathfinder.reference.list.SkillListAdapter;
import org.evilsoft.pathfinder.reference.list.SpellListAdapter;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;

public class DetailsListFragment extends ListFragment implements OnItemClickListener {
	private static final String TAG = "DetailsListFragment";
	private PsrdDbAdapter dbAdapter;
	private String currentUrl;
	private BaseAdapter currentListAdapter;

	public void updateUrl(String newUrl) {
		this.getListView().setOnItemClickListener(this);
		this.getListView().setCacheColorHint(Color.WHITE);
		if (currentUrl == newUrl) {
			return;
		}
		currentUrl = newUrl;
		String[] parts = newUrl.split("\\/");
		if (parts[2].equals("Classes")) {
			ClassAdapter ca = new ClassAdapter(dbAdapter);
			String id = parts[parts.length - 1];
			Cursor curs = ca.fetchClassList(id);
			currentListAdapter = new ClassListAdapter(getActivity().getApplicationContext(), curs);
		} else if (parts[2].equals("Feats")) {
			if (parts.length > 4) {
				FeatAdapter fa = new FeatAdapter(dbAdapter);
				String featType = parts[parts.length - 1];
				Cursor curs;
				if (featType.equals("All Feats")) {
					curs = fa.fetchFeatList();
				} else {
					curs = fa.fetchFeatList(featType);
				}
				currentListAdapter = new FeatListAdapter(getActivity().getApplicationContext(), curs, false);
			}
		} else if (parts[2].equals("Races")) {
			RaceAdapter ra = new RaceAdapter(dbAdapter);
			Cursor curs = ra.fetchRaceList();
			currentListAdapter = new RaceListAdapter(getActivity().getApplicationContext(), curs);
		} else if (parts[2].startsWith("Rules")) {
			RuleAdapter ra = new RuleAdapter(dbAdapter);
			String ruleId = parts[parts.length - 1];
			Cursor curs = ra.fetchRuleList(ruleId);
			currentListAdapter = new RuleListAdapter(getActivity().getApplicationContext(), curs);
		} else if (parts[2].equals("Monsters")) {
			MonsterAdapter ma = new MonsterAdapter(dbAdapter);
			String monsterId = parts[parts.length - 1];
			Cursor curs;
			if (monsterId.equals("All Monsters")) {
				monsterId = parts[parts.length - 2];
				curs = ma.fetchMonsterList(monsterId);
			} else {
				curs = ma.fetchMonstersByType(monsterId);
			}
			currentListAdapter = new MonsterListAdapter(getActivity().getApplicationContext(), curs, false);
		} else if (parts[2].equals("Skills")) {
			SkillAdapter sa = new SkillAdapter(dbAdapter);
			Cursor curs = sa.fetchSkillList();
			currentListAdapter = new SkillListAdapter(getActivity().getApplicationContext(), curs, false);
		} else if (parts[2].equals("Spells")) {
			SpellAdapter sa = new SpellAdapter(dbAdapter);
			String spellClass = parts[parts.length - 1];
			Cursor curs;
			if (spellClass.equals("All")) {
				curs = sa.fetchSpellList();
			} else {
				curs = sa.fetchSpellList(spellClass);
			}
			currentListAdapter = new SpellListAdapter(getActivity().getApplicationContext(), curs, false);
		} else if (parts[2].equals("Search")) {
			Cursor curs = dbAdapter.search(parts[parts.length - 1]);
			currentListAdapter = new SearchListAdapter(getActivity().getApplicationContext(), curs);
		}
		setListAdapter(currentListAdapter);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setListAdapter(ArrayAdapter.createFromResource(getActivity().getApplicationContext(), R.array.top_titles,
				R.layout.list_item));
		dbAdapter = new PsrdDbAdapter(getActivity().getApplicationContext());
		dbAdapter.open();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (dbAdapter != null) {
			dbAdapter.close();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		String uri = currentUrl + "/" + currentListAdapter.getItemId(position);
		Log.e(TAG, uri);
		DetailsViewFragment viewer = (DetailsViewFragment) this.getActivity().getSupportFragmentManager()
				.findFragmentById(R.id.details_view_fragment);
		viewer.updateUrl(uri);
	}
}
