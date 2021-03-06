package org.evilsoft.pathfinder.reference;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.evilsoft.pathfinder.reference.db.psrd.PsrdDbAdapter;
import org.evilsoft.pathfinder.reference.render.AbilityRenderer;
import org.evilsoft.pathfinder.reference.render.AfflictionRenderer;
import org.evilsoft.pathfinder.reference.render.AnimalCompanionRenderer;
import org.evilsoft.pathfinder.reference.render.FeatRenderer;
import org.evilsoft.pathfinder.reference.render.ItemRenderer;
import org.evilsoft.pathfinder.reference.render.MonsterRenderer;
import org.evilsoft.pathfinder.reference.render.RaceRenderer;
import org.evilsoft.pathfinder.reference.render.Renderer;
import org.evilsoft.pathfinder.reference.render.SectionRenderer;
import org.evilsoft.pathfinder.reference.render.SettlementRenderer;
import org.evilsoft.pathfinder.reference.render.SkillRenderer;
import org.evilsoft.pathfinder.reference.render.SpellRenderer;
import org.evilsoft.pathfinder.reference.render.TableRenderer;
import org.evilsoft.pathfinder.reference.render.TrapRenderer;
import org.evilsoft.pathfinder.reference.render.VehicleRenderer;

import android.content.res.AssetManager;
import android.database.Cursor;
import android.util.Log;
import android.widget.TextView;

public class RenderFarm {
	private static final String TAG = "SectionRenderer";
	private PsrdDbAdapter dbAdapter;
	private AssetManager assets;
	private static String css;
	private TextView title;
	private List<Renderer> renderPath;

	public RenderFarm(PsrdDbAdapter dbAdapter, AssetManager assets, TextView title) {
		this.dbAdapter = dbAdapter;
		this.assets = assets;
		this.title = title;
	}

	public Renderer getRenderer(String type) {
		if (type.equals("ability")) {
			return new AbilityRenderer(this.dbAdapter);
		} else if (type.equals("affliction")) {
			return new AfflictionRenderer(this.dbAdapter);
		} else if (type.equals("animal_companion")) {
			return new AnimalCompanionRenderer(this.dbAdapter);
		} else if (type.equals("creature")) {
			return new MonsterRenderer(this.dbAdapter);
		} else if (type.equals("feat")) {
			return new FeatRenderer(this.dbAdapter);
		} else if (type.equals("item")) {
			return new ItemRenderer(this.dbAdapter);
		} else if (type.equals("race")) {
			return new RaceRenderer(this.dbAdapter);
		} else if (type.equals("settlement")) {
			return new SettlementRenderer(this.dbAdapter);
		} else if (type.equals("skill")) {
			return new SkillRenderer(this.dbAdapter);
		} else if (type.equals("spell")) {
			return new SpellRenderer(this.dbAdapter);
		} else if (type.equals("table")) {
			return new TableRenderer(this.dbAdapter);
		} else if (type.equals("trap")) {
			return new TrapRenderer(this.dbAdapter);
		} else if (type.equals("vehicle")) {
			return new VehicleRenderer(this.dbAdapter);
		} else {
			return new SectionRenderer(this.dbAdapter);
		}
	}

	public static String swapUrl(String uri, String title, String id) {
		String[] parts = uri.split("\\/");
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < parts.length - 2; i++) {
			String part = parts[i];
			if (i > 0) {
				part = part.replace(':', '_');
			}
			sb.append(part);
			sb.append('/');
		}
		sb.append(title);
		sb.append("/");
		sb.append(id);
		return sb.toString();
	}

	public String render(String sectionId, String uri) {
		Cursor curs = this.dbAdapter.fetchFullSection(sectionId);
		renderPath = new ArrayList<Renderer>();
		return renderSection(curs, uri);
	}

	public String renderSection(Cursor curs, String uri) {
		HashMap<Integer, Integer> depthMap = new HashMap<Integer, Integer>();
		HashMap<Integer, String> titleMap = new HashMap<Integer, String>();
		int depth = 0;
		StringBuffer sb = new StringBuffer();
		boolean has_next = curs.moveToFirst();
		boolean top = true;
		String topTitle = curs.getString(6);
		// 0:section_id, 1:lft, 2:rgt, 3:parent_id, 4:type, 5:subtype, 6:name,
		// 7:abbrev,
		// 8:source, 9:description, 10:body
		sb.append(renderCss());
		this.title.setText(topTitle);
		while (has_next) {
			int sectionId = curs.getInt(0);
			int parentId = curs.getInt(3);
			String name = curs.getString(6);
			depth = getDepth(depthMap, sectionId, parentId, depth);
			titleMap.put(sectionId, name);
			String title = name;
			if (titleMap.containsKey(parentId)) {
				title = titleMap.get(parentId);
			}
			sb.append(renderSectionText(curs, title, depth, uri, top));
			has_next = curs.moveToNext();
			top = false;
		}
		return sb.toString();
	}

	public int getDepth(HashMap<Integer, Integer> depthMap, int section_id, int parent_id, int depth) {
		if (depthMap.containsKey(parent_id)) {
			depth = depthMap.get(parent_id) + 1;
			depthMap.put(section_id, depth);
		} else {
			depthMap.put(section_id, depth);
		}
		return depth;
	}

	public String renderSectionText(Cursor curs, String title, int depth, String uri, boolean top) {
		String id = curs.getString(0);
		String type = curs.getString(4);
		String newUri = swapUrl(uri, title, id);
		Renderer renderer = getRenderer(type);
		String text = renderer.render(curs, newUri, depth, top, suppressTitle());
		renderPath.add(renderer);
		return text;
	}

	public boolean suppressTitle() {
		if (renderPath.size() == 0) {
			return true;
		}
		Renderer prev = renderPath.get(renderPath.size() - 1);
		return prev.suppressNextTitle == true;
	}

	public String renderCss() {
		StringBuffer sb = new StringBuffer();
		sb.append("<head><style type='text/css'>");
		if (css == null) {
			try {
				InputStream in = assets.open("display.css");
				css = readFile(in);
			} catch (IOException e) {
				Log.e(TAG, "Failed to loaded display.css");
			}
		}
		sb.append("\n");
		sb.append(css);
		sb.append("</style></head>");
		sb.append("\n");
		return sb.toString();
	}

	public String readFile(InputStream is) {
		ByteArrayOutputStream bo = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		try {
			for (int readnum; (readnum = is.read(buffer)) != -1;) {
				// is.read(buffer);
				bo.write(buffer, 0, readnum);
			}
			bo.close();
			is.close();
		} catch (IOException e) {

		}
		return bo.toString();
	}
}
