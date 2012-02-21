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
        String sql = "SELECT * FROM collection_entries WHERE collection_id = ?";
        // assume that DB is open because it's coming from SectionViewFragment
        return userDbAdapter.database.rawQuery(sql, new String[] {character_id});
    }
    
    public static boolean entryIsStarred(Context context, long character_id, String path) {
        PsrdUserDbAdapter userDbAdapter = new PsrdUserDbAdapter(context);
        boolean result;

        try {
            userDbAdapter.open();

            // TODO: figure out what the query would look like
            StringBuffer sql = new StringBuffer();
            sql.append("SELECT * FROM collection_entries");
            sql.append(" WHERE collection_id = ?");
            sql.append("   AND path = ?");

            Cursor curs = userDbAdapter.database.rawQuery(sql.toString(), new String[] {String.valueOf(character_id), path});
            result = curs.moveToFirst();
        } finally {
            userDbAdapter.close();
        }

        // return result;
        return character_id == 1 ? true : false;
    }

}
