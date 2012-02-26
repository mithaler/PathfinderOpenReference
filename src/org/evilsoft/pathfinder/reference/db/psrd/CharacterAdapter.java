package org.evilsoft.pathfinder.reference.db.psrd;

import java.util.ArrayList;
import java.util.HashMap;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import org.evilsoft.pathfinder.reference.db.user.PsrdUserDbAdapter;

public class CharacterAdapter {
    private PsrdUserDbAdapter userDbAdapter;

    public CharacterAdapter(PsrdUserDbAdapter userDbAdapter) {
        this.userDbAdapter = userDbAdapter;
    }
    
    public void closeDb() {
        if (userDbAdapter != null) {
            userDbAdapter.close();
        }
    }

    public Cursor fetchCharacterList() {
        String sql = "SELECT collection_id AS _id, name FROM collections";
        userDbAdapter.open();
        return userDbAdapter.database.rawQuery(sql, new String[]{});
    }

    public ArrayList<HashMap<String, Object>> createCharacterList() {
        ArrayList<HashMap<String, Object>> charList = new ArrayList<HashMap<String, Object>>();
        Cursor curs = fetchCharacterList();
        HashMap<String, Object> child;

        boolean hasNext = curs.moveToFirst();
        while (hasNext) {
            child = new HashMap<String, Object>();
            child.put("id", curs.getString(0));
            child.put("specificName", curs.getString(1));
            Log.e(child.get("id").toString(), child.get("specificName").toString());
            charList.add(child);
            hasNext = curs.moveToNext();
        }

        userDbAdapter.close();
        return charList;
    }

    public Cursor fetchCharacterEntries(String character_id) {
        String sql = "SELECT collection_entries.* FROM collection_entries INNER JOIN collections ON collections.collection_id = collection_entries.collection_id WHERE collections.name = ?";
        // assume that DB is open because it's coming from SectionViewFragment
        return userDbAdapter.database.rawQuery(sql, new String[] {character_id});
    }

    public static void toggleEntryStar(Context context, long characterId, ArrayList<HashMap<String, String>> path, String url) {
        if (CharacterAdapter.entryIsStarred(context, characterId, path)) {
            CharacterAdapter.unstar(context, characterId, path, url);
        } else {
            CharacterAdapter.star(context, characterId, path, url);
        }
    }

    public static boolean entryIsStarred(Context context, long characterId, ArrayList<HashMap<String, String>> path) {
        PsrdUserDbAdapter userDbAdapter = new PsrdUserDbAdapter(context);

        try {
            userDbAdapter.open();

            StringBuffer sql = new StringBuffer();
            sql.append("SELECT 1 FROM collection_entries");
            sql.append(" WHERE collection_id = ?");
            sql.append("   AND section_id = ?");
            sql.append("   AND name = ?");

            Cursor curs = userDbAdapter.database.rawQuery(sql.toString(),
                new String[] {Long.toString(characterId), path.get(0).get("id"), path.get(1).get("name")});

            return curs.moveToFirst();
        } finally {
            userDbAdapter.close();
        }
    }

    private static void star(Context context, long characterId, ArrayList<HashMap<String, String>> path, String url) {
        PsrdUserDbAdapter userDbAdapter = new PsrdUserDbAdapter(context);

        try {
            userDbAdapter.open();
            userDbAdapter.star(characterId, path.get(0).get("id"), path.get(1).get("name"), url);
        } finally {
            userDbAdapter.close();
        }
    }

    private static void unstar(Context context, long characterId, ArrayList<HashMap<String, String>> path, String url) {
        PsrdUserDbAdapter userDbAdapter = new PsrdUserDbAdapter(context);

        try {
            userDbAdapter.open();
            userDbAdapter.unstar(characterId, path.get(0).get("id"), path.get(1).get("name"), url);
        } finally {
            userDbAdapter.close();
        }
    }

}
