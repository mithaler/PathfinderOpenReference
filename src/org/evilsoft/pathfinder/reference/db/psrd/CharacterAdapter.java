package org.evilsoft.pathfinder.reference.db.psrd;

import java.util.ArrayList;
import java.util.HashMap;
import android.database.Cursor;
import org.evilsoft.pathfinder.reference.db.user.PsrdUserDbAdapter;

public class CharacterAdapter {
    private PsrdUserDbAdapter userDbAdapter;

    public CharacterAdapter(PsrdUserDbAdapter userDbAdapter) {
        this.userDbAdapter = userDbAdapter;
    }

    private Cursor fetchCharacterList() {
        String sql = "SELECT * FROM collections";
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
            charList.add(child);
            hasNext = curs.moveToNext();
        }

        userDbAdapter.close();
        return charList;
    }

    public Cursor fetchCharacterList(String string) {
        // TODO
        return null;
    }

}
